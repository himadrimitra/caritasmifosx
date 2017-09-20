package com.finflux.fileprocess.service;

import java.io.File;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.finflux.common.util.FinfluxDocumentConverterUtils;
import com.finflux.fileprocess.data.FileStatus;
import com.finflux.fileprocess.domain.FileProcess;
import com.finflux.fileprocess.domain.FileProcessRepositoryWrapper;
import com.finflux.fileprocess.domain.FileRecords;

@Component
public class SanctionedButNotDisbursedService implements FileRecordsProcessService {

    private final SanctionedButNotDisbursedServiceProcess process;
    private final FileProcessRepositoryWrapper fileProcessRepository;

    @Autowired
    public SanctionedButNotDisbursedService(final SanctionedButNotDisbursedServiceProcess process,
            final FileProcessRepositoryWrapper fileProcessRepository) {
        this.process = process;
        this.fileProcessRepository = fileProcessRepository;
    }

    @Override
    public void fileRecordsProcess(final FileProcess fileProcess) {
        try {
            if (!fileProcess.getFileRecords().isEmpty()) {
                final String filePath = fileProcess.getFilePath();
                final File file = new File(filePath);
                FinfluxDocumentConverterUtils.addColumnsToExcelWorksheet(file, "Error Message");
                for (final FileRecords fileRecords : fileProcess.getFileRecords()) {
                    this.process.fileRecordsProcess(fileProcess, fileRecords);
                }
            }
            fileProcess.setStatus(FileStatus.COMPLETED.getValue());
            this.fileProcessRepository.save(fileProcess);
        } catch (final Exception e) {

        }
    }
}
