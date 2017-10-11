package com.finflux.fileprocess.service;

import java.io.File;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.annotation.PostConstruct;

import org.apache.fineract.infrastructure.core.service.DateUtils;
import org.apache.fineract.infrastructure.documentmanagement.command.DocumentCommand;
import org.apache.fineract.infrastructure.documentmanagement.contentrepository.ContentRepository;
import org.apache.fineract.infrastructure.documentmanagement.contentrepository.ContentRepositoryFactory;
import org.apache.fineract.infrastructure.security.service.PlatformSecurityContext;
import org.apache.fineract.useradministration.domain.AppUser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.finflux.fileprocess.data.FileProcessType;
import com.finflux.fileprocess.data.FileStatus;
import com.finflux.fileprocess.domain.FileProcess;
import com.finflux.fileprocess.domain.FileProcessRepositoryWrapper;
import com.finflux.fileprocess.helper.FileAsyncProcessingHelper;

@Service
public class FileProcessServiceImpl implements FileProcessService {

    private final static Logger logger = LoggerFactory.getLogger(FileProcessServiceImpl.class);

    private final PlatformSecurityContext context;
    private final FileProcessRepositoryWrapper repository;
    private final ContentRepositoryFactory contentRepositoryFactory;
    private final FileAsyncProcessingHelper helper;

    private DateFormat dateFormat;
    private DateFormat fileNameGeneratedateFormat;
    private final String BUSINESS_CORRESPONDENT_DIRECTORY = "businesscorrespondent";

    @Autowired
    public FileProcessServiceImpl(final PlatformSecurityContext context, final FileProcessRepositoryWrapper repository,
            final ContentRepositoryFactory contentRepositoryFactory, final FileAsyncProcessingHelper helper) {
        this.context = context;
        this.repository = repository;
        this.contentRepositoryFactory = contentRepositoryFactory;
        this.helper = helper;
    }

    @PostConstruct
    private void init() {
        this.dateFormat = new SimpleDateFormat("dd-MM-yyyy");
        this.fileNameGeneratedateFormat = new SimpleDateFormat("HHmmss");
    }

    @Override
    @Transactional
    public Long fileUploadProcess(final String fileProcessType, final DocumentCommand documentCommand, final InputStream inputStream) {
        try {
            final FileProcessType fileProcessTypeEnum = FileProcessType.fromString(fileProcessType);
            final ContentRepository contentRepository = this.contentRepositoryFactory.getRepository();
            final Date createdDate = DateUtils.getLocalDateTimeOfTenant().toDate();
            final String date = this.dateFormat.format(DateUtils.getLocalDateOfTenant().toDate());
            final String time = this.fileNameGeneratedateFormat.format(createdDate);
            final String filePath = contentRepository.saveFile(inputStream, documentCommand, this.BUSINESS_CORRESPONDENT_DIRECTORY,
                    fileProcessTypeEnum.name(), date, time);
            final AppUser appUser = this.context.authenticatedUser();
            final String fileName = documentCommand.getFileName();
            final File file = new File(filePath);
            final String fileType = getExtensionOfFile(file);
            final Integer totalRecords = 0;
            final Integer totalPendingRecords = 0;
            final Integer totalSuccessRecords = 0;
            final Integer totalFailureRecords = 0;
            final FileProcess fileProcess = FileProcess.create(fileName, fileType, documentCommand.getType(), filePath,
                    fileProcessTypeEnum.getValue(), totalRecords, totalPendingRecords, totalSuccessRecords, totalFailureRecords,
                    FileStatus.UPLOADED.getValue(), createdDate, appUser.getId(), createdDate);
            this.repository.save(fileProcess);

            this.helper.fileProcess(fileProcess.getId());

            return fileProcess.getId();

        } catch (final Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String getExtensionOfFile(final File file) {
        String fileExtension = "";
        // Get file Name first
        final String fileName = file.getName();

        // If fileName do not contain "." or starts with "." then it is not a
        // valid file
        if (fileName.contains(".") && fileName.lastIndexOf(".") != 0) {
            fileExtension = fileName.substring(fileName.lastIndexOf(".") + 1);
        }

        return fileExtension;
    }
}
