package com.finflux.fileprocess.helper;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

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
import org.springframework.util.CollectionUtils;

import com.finflux.fileprocess.data.FileStatus;
import com.finflux.fileprocess.domain.FileProcess;
import com.finflux.fileprocess.domain.FileProcessRepositoryWrapper;
import com.finflux.fileprocess.service.ExtractFileRecordsService;
import com.finflux.fileprocess.service.FileRecordsProcessService;

@Component
public class FileAsyncProcessingHelper implements ApplicationListener<ContextRefreshedEvent> {

    private static boolean isInitialisationEvent = true;
    private final AppUserRepositoryWrapper userRepository;
    private final GrantedAuthoritiesMapper authoritiesMapper = new NullAuthoritiesMapper();
    private final TenantDetailsService tenantDetailsService;
    private final ExtractFileRecordsService extractFileRecordsService;
    private final FileProcessRepositoryWrapper repository;
    private final FileRecordsProcessService fileRecordsProcessService;

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        if (isInitialisationEvent) {
            isInitialisationEvent = false;
            ExecutorService executorService = Executors.newFixedThreadPool(5);
            final List<FineractPlatformTenant> tenants = this.tenantDetailsService.findAllTenants();
            final List<Integer> pendingStatuses = new ArrayList<>();
            pendingStatuses.add(FileStatus.UPLOADED.getValue());
            pendingStatuses.add(FileStatus.IN_PROGRESS.getValue());
            for (final FineractPlatformTenant tenant : tenants) {
                ThreadLocalContextUtil.setTenant(tenant);
                AppUser user = this.userRepository.fetchSystemUser();
                UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(user, user.getPassword(),
                        this.authoritiesMapper.mapAuthorities(user.getAuthorities()));
                SecurityContextHolder.getContext().setAuthentication(auth);
                final List<FileProcess> fileProcesses = this.repository.findByStatusIn(pendingStatuses);
                if (!CollectionUtils.isEmpty(fileProcesses)) {
                    for (final FileProcess fileProcess : fileProcesses) {
                        this.fileRecordsProcessService.fileRecordsProcess(fileProcess);
                    }
                }
            }
            ThreadLocalContextUtil.clearTenant();
            executorService.shutdown();
        }
    }

    @Autowired
    public FileAsyncProcessingHelper(final AppUserRepositoryWrapper userRepository, final TenantDetailsService tenantDetailsService,
            final ExtractFileRecordsService extractFileRecordsService, final FileProcessRepositoryWrapper repository,
            final FileRecordsProcessService fileRecordsProcessService) {
        this.userRepository = userRepository;
        this.tenantDetailsService = tenantDetailsService;
        this.extractFileRecordsService = extractFileRecordsService;
        this.repository = repository;
        this.fileRecordsProcessService = fileRecordsProcessService;
    }

    public void fileProcess(final Long fileProcessId) {
        ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();
        executorService.schedule(new FileProcessInvoker(ThreadLocalContextUtil.getTenant(), fileProcessId), 5, TimeUnit.SECONDS);
        executorService.shutdown();
    }

    private class FileProcessInvoker implements Runnable {

        private final FineractPlatformTenant tenant;
        private final Long fileProcessId;

        FileProcessInvoker(final FineractPlatformTenant tenant, final Long fileProcessId) {
            this.tenant = tenant;
            this.fileProcessId = fileProcessId;
        }

        @Override
        public void run() {
            ThreadLocalContextUtil.setTenant(this.tenant);
            final AppUser user = userRepository.fetchSystemUser();
            UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(user, user.getPassword(),
                    authoritiesMapper.mapAuthorities(user.getAuthorities()));
            SecurityContextHolder.getContext().setAuthentication(auth);
            final FileProcess fileProcess = repository.findOneWithNotFoundDetection(this.fileProcessId);
            final String fileType = fileProcess.getFileType();
            switch (fileType) {
                case "xls":
                case "xlsx":
                    extractFileRecordsService.excelFileProcess(fileProcess);
                break;

                default:
                break;
            }
        }
    }

}
