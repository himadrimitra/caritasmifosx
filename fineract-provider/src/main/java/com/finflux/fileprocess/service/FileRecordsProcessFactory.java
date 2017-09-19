package com.finflux.fileprocess.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.finflux.fileprocess.data.FileProcessType;

@Component
public class FileRecordsProcessFactory {

    private final SanctionButNotDisbursedService sanctionButNotDisbursedService;

    @Autowired
    public FileRecordsProcessFactory(final SanctionButNotDisbursedService sanctionButNotDisbursedService) {
        this.sanctionButNotDisbursedService = sanctionButNotDisbursedService;
    }

    public FileRecordsProcessService create(final FileProcessType fileProcessType) {
        FileRecordsProcessService fileRecordsProcessService = null;
        switch (fileProcessType.getSystemName()) {
            case "sanctionedButNotDisbursed":
                fileRecordsProcessService = this.sanctionButNotDisbursedService;
            break;

            default:
            break;
        }
        return fileRecordsProcessService;
    }
}
