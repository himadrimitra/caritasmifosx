package com.finflux.fileprocess.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.apache.fineract.infrastructure.core.domain.FineractPlatformTenant;
import org.apache.fineract.infrastructure.core.serialization.FromJsonHelper;
import org.apache.fineract.infrastructure.core.service.DateUtils;
import org.apache.fineract.infrastructure.core.service.ThreadLocalContextUtil;
import org.apache.fineract.useradministration.domain.AppUser;
import org.apache.fineract.useradministration.domain.AppUserRepositoryWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.mapping.GrantedAuthoritiesMapper;
import org.springframework.security.core.authority.mapping.NullAuthoritiesMapper;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import com.finflux.common.util.FinfluxDocumentConverterUtils;
import com.finflux.fileprocess.data.FileProcessType;
import com.finflux.fileprocess.data.FileRecordsStatus;
import com.finflux.fileprocess.data.FileStatus;
import com.finflux.fileprocess.domain.FileProcess;
import com.finflux.fileprocess.domain.FileProcessRepositoryWrapper;
import com.finflux.fileprocess.domain.FileRecords;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

@Component
public class ExtractFileRecordsService {

    private final FromJsonHelper fromApiJsonHelper;
    private final FileProcessRepositoryWrapper repository;
    private final AppUserRepositoryWrapper userRepository;
    private final GrantedAuthoritiesMapper authoritiesMapper = new NullAuthoritiesMapper();
    private final FileRecordsProcessFactory factory;

    @Autowired
    public ExtractFileRecordsService(final FromJsonHelper fromApiJsonHelper, final FileProcessRepositoryWrapper repository,
            final AppUserRepositoryWrapper userRepository, final FileRecordsProcessFactory factory) {
        this.fromApiJsonHelper = fromApiJsonHelper;
        this.repository = repository;
        this.userRepository = userRepository;
        this.factory = factory;
    }

    public void excelFileProcess(final FileProcess fileProcess) {
        final Date createdDate = DateUtils.getLocalDateTimeOfTenant().toDate();
        final String filePath = fileProcess.getFilePath();
        final String fileContentAsJsonStr = FinfluxDocumentConverterUtils.ExcelToJsonConverter(filePath);
        final JsonElement element = this.fromApiJsonHelper.parse(fileContentAsJsonStr);
        final JsonObject object = element.getAsJsonObject();
        if (this.fromApiJsonHelper.parameterExists("sheets", element)) {
            final JsonArray array = object.get("sheets").getAsJsonArray();
            if (array != null && array.size() > 0) {
                Integer totalRecords = 0;
                final List<FileRecords> fileRecords = new ArrayList<>();
                for (int i = 0; i < array.size(); i++) {
                    final JsonObject sheetObj = array.get(i).getAsJsonObject();
                    if (sheetObj.get("data") != null) {
                        final JsonArray dataArray = sheetObj.get("data").getAsJsonArray();
                        if (dataArray != null && dataArray.size() > 0) {
                            totalRecords += dataArray.size();
                            for (int j = 0; j < dataArray.size(); j++) {
                                final JsonObject dataObj = dataArray.get(j).getAsJsonObject();
                                final String content = dataObj.toString();
                                final FileRecords fileRecord = FileRecords.create(fileProcess, content,
                                        FileRecordsStatus.PENDING.getValue(), createdDate);
                                fileRecords.add(fileRecord);
                            }
                        }
                    }
                }
                if (totalRecords > 0) {
                    fileProcess.setTotalRecords(totalRecords);
                    fileProcess.setFileRecords(fileRecords);
                    this.repository.save(fileProcess);
                    fileRecordsProcess(fileProcess);
                }
            }
        }
    }

    private void fileRecordsProcess(final FileProcess fileProcess) {
        fileProcess.setStatus(FileStatus.IN_PROGRESS.getValue());
        final ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();
        executorService.schedule(new FileRecordsProcessInvoker(ThreadLocalContextUtil.getTenant(), fileProcess), 5, TimeUnit.SECONDS);
        executorService.shutdown();
    }

    private class FileRecordsProcessInvoker implements Runnable {

        private final FineractPlatformTenant tenant;
        private final FileProcess fileProcess;
        private final FileProcessType fileProcessType;
        private FileRecordsProcessService fileRecordsProcessService;

        FileRecordsProcessInvoker(final FineractPlatformTenant tenant, final FileProcess fileProcess) {
            this.tenant = tenant;
            this.fileProcess = fileProcess;
            this.fileProcessType = FileProcessType.fromInt(fileProcess.getFileProcessType());
        }

        @Override
        public void run() {
            ThreadLocalContextUtil.setTenant(this.tenant);
            final AppUser user = userRepository.fetchSystemUser();
            UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(user, user.getPassword(),
                    authoritiesMapper.mapAuthorities(user.getAuthorities()));
            SecurityContextHolder.getContext().setAuthentication(auth);
            this.fileRecordsProcessService = factory.create(this.fileProcessType);
            if (null != this.fileRecordsProcessService) {
                this.fileRecordsProcessService.fileRecordsProcess(this.fileProcess);
            }
        }
    }
}
