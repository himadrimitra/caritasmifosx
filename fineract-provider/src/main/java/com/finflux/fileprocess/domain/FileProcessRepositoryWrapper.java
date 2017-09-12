package com.finflux.fileprocess.domain;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.finflux.fileprocess.exception.FileProcessNotFoundException;

@Service
public class FileProcessRepositoryWrapper {

    private final FileProcessRepository repository;

    @Autowired
    public FileProcessRepositoryWrapper(final FileProcessRepository repository) {
        this.repository = repository;
    }

    public FileProcess findOneWithNotFoundDetection(final Long fileProcessId) {
        final FileProcess fileProcess = this.repository.findOne(fileProcessId);
        if (fileProcess == null) { throw new FileProcessNotFoundException(fileProcessId); }
        return fileProcess;
    }

    public void save(final FileProcess fileProcess) {
        this.repository.save(fileProcess);
    }

    public void save(final List<FileProcess> fileProcesses) {
        this.repository.save(fileProcesses);
    }

    public void saveAndFlush(final FileProcess fileProcess) {
        this.repository.saveAndFlush(fileProcess);
    }

    public void delete(final FileProcess fileProcess) {
        this.repository.delete(fileProcess);
    }
}
