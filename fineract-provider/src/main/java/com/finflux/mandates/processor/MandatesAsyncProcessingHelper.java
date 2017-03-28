package com.finflux.mandates.processor;

import com.finflux.mandates.data.MandatesProcessData;
import com.finflux.mandates.domain.MandateProcessStatusEnum;
import com.finflux.mandates.domain.MandateProcessTypeEnum;
import com.finflux.mandates.service.MandatesProcessingReadPlatformService;
import org.apache.fineract.infrastructure.core.domain.FineractPlatformTenant;
import org.apache.fineract.infrastructure.core.service.ThreadLocalContextUtil;
import org.apache.fineract.infrastructure.security.service.TenantDetailsService;
import org.apache.fineract.useradministration.domain.AppUser;
import org.apache.fineract.useradministration.domain.AppUserRepositoryWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.mapping.GrantedAuthoritiesMapper;
import org.springframework.security.core.authority.mapping.NullAuthoritiesMapper;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Component
public class MandatesAsyncProcessingHelper implements ApplicationListener<ContextRefreshedEvent> {

        private static boolean isInitialisationEvent = true;
        private final MandateDownloadProcessor mandateDownloadProcessor;
        private final MandateUploadProcessor mandateUploadProcessor;
        private final TransactionsDownloadProcessor transactionsDownloadProcessor;
        private final TransactionsUploadProcessor transactionsUploadProcessor;
        private final MandatesProcessingReadPlatformService mandatesProcessingReadPlatformService;
        private final AppUserRepositoryWrapper userRepository ;
        private final GrantedAuthoritiesMapper authoritiesMapper = new NullAuthoritiesMapper();
        private final TenantDetailsService tenantDetailsService;

        @Autowired
        public MandatesAsyncProcessingHelper(final MandateDownloadProcessor mandateDownloadProcessor,
                final MandateUploadProcessor mandateUploadProcessor,
                final TransactionsDownloadProcessor transactionsDownloadProcessor,
                final TransactionsUploadProcessor transactionsUploadProcessor,
                final MandatesProcessingReadPlatformService mandatesProcessingReadPlatformService,
                final AppUserRepositoryWrapper userRepository,
                final TenantDetailsService tenantDetailsService){

                this.mandateDownloadProcessor = mandateDownloadProcessor;
                this.mandateUploadProcessor = mandateUploadProcessor;
                this.transactionsDownloadProcessor = transactionsDownloadProcessor;
                this.transactionsUploadProcessor = transactionsUploadProcessor;
                this.mandatesProcessingReadPlatformService = mandatesProcessingReadPlatformService;
                this.userRepository = userRepository;
                this.tenantDetailsService = tenantDetailsService;
        }

        public void processMandateDownload(final Long requestId) {
                ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();
                executorService.schedule(new MandateProcessInvoker(ThreadLocalContextUtil.getTenant(),
                        requestId, MandateProcessTypeEnum.MANDATES_DOWNLOAD), 5, TimeUnit.SECONDS);
                executorService.shutdown();
        }

        public void processMandateUpload(final Long requestId) {
                ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();
                executorService.schedule(new MandateProcessInvoker(ThreadLocalContextUtil.getTenant(),
                        requestId, MandateProcessTypeEnum.MANDATES_UPLOAD), 5, TimeUnit.SECONDS);
                executorService.shutdown();
        }

        public void processTransactionsDownload(final Long requestId) {
                ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();
                executorService.schedule(new MandateProcessInvoker(ThreadLocalContextUtil.getTenant(),
                        requestId, MandateProcessTypeEnum.TRANSACTIONS_DOWNLOAD), 5, TimeUnit.SECONDS);
                executorService.shutdown();
        }

        public void processTransactionsUpload(final Long requestId) {
                ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();
                executorService.schedule(new MandateProcessInvoker(ThreadLocalContextUtil.getTenant(),
                        requestId, MandateProcessTypeEnum.TRANSACTIONS_UPLOAD), 5, TimeUnit.SECONDS);
                executorService.shutdown();
        }

        @Override
        public void onApplicationEvent(final ContextRefreshedEvent event) {
                if(isInitialisationEvent){
                        isInitialisationEvent = false;
                        ExecutorService executorService = Executors.newFixedThreadPool(5);
                        final List<FineractPlatformTenant> tenants = this.tenantDetailsService.findAllTenants();
                        final Integer[] pendingStatuses = new Integer[]{ MandateProcessStatusEnum.REQUESTED.getValue(), MandateProcessStatusEnum.INPROCESS.getValue()};
                        for (final FineractPlatformTenant tenant : tenants) {
                                ThreadLocalContextUtil.setTenant(tenant);
                                AppUser user = this.userRepository.fetchSystemUser();
                                UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(user, user.getPassword(),
                                        this.authoritiesMapper.mapAuthorities(user.getAuthorities()));
                                SecurityContextHolder.getContext().setAuthentication(auth);

                                Collection<MandatesProcessData> pendingMandateProcesses = this.mandatesProcessingReadPlatformService
                                        .retrieveMandatesWithStatus(pendingStatuses);
                                if(null != pendingMandateProcesses && pendingMandateProcesses.size() > 0){
                                        for (MandatesProcessData data:pendingMandateProcesses) {
                                                executorService.submit(new MandateProcessInvoker(tenant, data.getId(),
                                                        MandateProcessTypeEnum.from(data.getMandateProcessType())));
                                        }
                                }

                        }
                        executorService.shutdown();
                }
        }

        private class MandateProcessInvoker implements Runnable{

                private final FineractPlatformTenant tenant;
                private final Long requestId;
                private final MandateProcessTypeEnum type;

                MandateProcessInvoker(final FineractPlatformTenant tenant, final Long requestId, final MandateProcessTypeEnum type){
                        this.tenant = tenant;
                        this.requestId = requestId;
                        this.type = type;
                }

                @Override
                public void run() {
                        ThreadLocalContextUtil.setTenant(this.tenant);
                        AppUser user = userRepository.fetchSystemUser();
                        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(user, user.getPassword(),
                                authoritiesMapper.mapAuthorities(user.getAuthorities()));
                        SecurityContextHolder.getContext().setAuthentication(auth);

                        if(type.hasStateOf(MandateProcessTypeEnum.MANDATES_DOWNLOAD)){
                                mandateDownloadProcessor.processMandateDownload(requestId);
                        }else if(type.hasStateOf(MandateProcessTypeEnum.MANDATES_UPLOAD)){
                                mandateUploadProcessor.processMandateUpload(requestId);
                        }else if(type.hasStateOf(MandateProcessTypeEnum.TRANSACTIONS_DOWNLOAD)){
                                transactionsDownloadProcessor.processTransactionDownload(requestId);
                        }else if(type.hasStateOf(MandateProcessTypeEnum.TRANSACTIONS_UPLOAD)){
                                transactionsUploadProcessor.processTransactionUpload(requestId);
                        }
                }
        }
}
