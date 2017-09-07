package com.finflux.fileprocess.service;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;

import org.apache.commons.io.FileUtils;
import org.apache.fineract.infrastructure.core.serialization.FromJsonHelper;
import org.apache.fineract.infrastructure.core.service.DateUtils;
import org.apache.fineract.infrastructure.documentmanagement.command.DocumentCommand;
import org.apache.fineract.infrastructure.security.service.PlatformSecurityContext;
import org.apache.fineract.useradministration.domain.AppUser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.finflux.common.util.FinfluxDocumentConverterUtils;
import com.finflux.common.util.FinfluxFileUtils;
import com.finflux.fileprocess.data.FileProcessType;
import com.finflux.fileprocess.domain.FileProcess;
import com.finflux.fileprocess.domain.FileProcessRepositoryWrapper;
import com.finflux.fileprocess.domain.FileRecords;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

@Service
public class FileProcessServiceImpl implements FileProcessService {

    private final static Logger logger = LoggerFactory.getLogger(FileProcessServiceImpl.class);

    private final PlatformSecurityContext context;
    private final FromJsonHelper fromApiJsonHelper;
    private final FileProcessRepositoryWrapper repository;

    private DateFormat dateFormat;
    private DateFormat fileNameGeneratedateFormat;
    private final String DIRECTORY = System.getProperty("user.home") + File.separator + "BC";

    @Autowired
    public FileProcessServiceImpl(final PlatformSecurityContext context, final FromJsonHelper fromApiJsonHelper,
            final FileProcessRepositoryWrapper repository) {
        this.context = context;
        this.fromApiJsonHelper = fromApiJsonHelper;
        this.repository = repository;
    }

    @PostConstruct
    private void init() {
        this.dateFormat = new SimpleDateFormat("dd-MM-yyyy");
        this.fileNameGeneratedateFormat = new SimpleDateFormat("HHmmss");
        final File file = FinfluxFileUtils.createDirectories(false, DIRECTORY);
        if (!file.exists()) {
            file.mkdirs();
        }
    }

    @Override
    @Transactional
    public Long fileUploadProcess(final String fileProcessType, final DocumentCommand documentCommand, final InputStream inputStream) {
        try {
            final AppUser appUser = this.context.authenticatedUser();
            final String fileName = documentCommand.getFileName();
            final Date createdDate = DateUtils.getLocalDateTimeOfTenant().toDate();
            final FileProcessType fileProcessTypeEnum = FileProcessType.fromString(fileProcessType);
            final String DIRECTORY = this.DIRECTORY + File.separator + fileProcessTypeEnum.getSystemName().toUpperCase();
            final String date = this.dateFormat.format(DateUtils.getLocalDateOfTenant().toDate());
            final String time = this.fileNameGeneratedateFormat.format(createdDate);
            File file = FinfluxFileUtils.createDirectories(true, DIRECTORY, date, time);
            final String filePath = file.getPath() + File.separator + fileName;
            file = new File(filePath);
            file.createNewFile();
            FileUtils.copyInputStreamToFile(inputStream, file);
            final String fileType = getExtensionOfFile(file);
            FileProcess fileProcess = null;
            if (fileType != null && (fileType.equalsIgnoreCase("xlsx") || fileType.equalsIgnoreCase("xlx"))) {
                fileProcess = excelFileProcess(fileProcessTypeEnum, fileName, fileType, createdDate, file, fileProcess, appUser);
            }
            if (fileProcess != null) {
                this.repository.save(fileProcess);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private FileProcess excelFileProcess(final FileProcessType fileProcessTypeEnum, final String fileName, final String fileType,
            final Date createdDate, final File file, FileProcess fileProcess, final AppUser appUser) {
        final String filePath = file.getPath();
        final String fileContentAsJsonStr = FinfluxDocumentConverterUtils.ExcelToJsonConverter(filePath);
        final JsonElement element = this.fromApiJsonHelper.parse(fileContentAsJsonStr);
        final JsonObject object = element.getAsJsonObject();
        if (this.fromApiJsonHelper.parameterExists("sheets", element)) {
            final JsonArray array = object.get("sheets").getAsJsonArray();
            if (array != null && array.size() > 0) {
                Integer totalRecords = 0;
                final Integer totalPendingRecords = 0;
                final Integer totalSuccessRecords = 0;
                final Integer totalFailureRecords = 0;
                final Long createdbyId = appUser.getId();
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
                                final FileRecords fileRecord = FileRecords.create(fileProcess, content, 1, createdDate);
                                fileRecords.add(fileRecord);
                            }
                        }
                    }
                }
                if (totalRecords > 0) {
                    fileProcess = FileProcess.create(fileName, fileType, filePath, fileProcessTypeEnum.getValue(), totalRecords,
                            totalPendingRecords, totalSuccessRecords, totalFailureRecords, 1, createdDate, createdbyId, createdDate);
                    for (final FileRecords fileRecord : fileRecords) {
                        fileRecord.setFileProcess(fileProcess);
                    }
                    fileProcess.setFileRecords(fileRecords);
                }
            }
        }
        return fileProcess;
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
