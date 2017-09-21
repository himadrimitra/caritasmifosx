package com.finflux.fileprocess.exception;

import org.apache.fineract.infrastructure.core.exception.AbstractPlatformResourceNotFoundException;

public class FileProcessNotFoundException extends AbstractPlatformResourceNotFoundException {

    public FileProcessNotFoundException(final Long id) {
        super("error.msg.file.process.id.invalid", "File process with identifier " + id + " does not exist", id);
    }

}