package com.finflux.fileprocess.service;

import java.io.InputStream;

import org.apache.fineract.infrastructure.documentmanagement.command.DocumentCommand;

public interface FileProcessService {

    Long fileUploadProcess(final String fileProcessType, final DocumentCommand documentCommand, final InputStream inputStream);

}
