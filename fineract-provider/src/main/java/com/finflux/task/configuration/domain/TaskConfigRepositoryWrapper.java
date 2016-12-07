package com.finflux.task.configuration.domain;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.finflux.task.configuration.exception.TaskConfigNotFoundException;

@Service
public class TaskConfigRepositoryWrapper {

    private final TaskConfigRepository repository;

    @Autowired
    public TaskConfigRepositoryWrapper(final TaskConfigRepository repository) {
        this.repository = repository;
    }

    public TaskConfig findOneWithNotFoundDetection(final Long taskConfigId) {
        final TaskConfig taskConfig = this.repository.findOne(taskConfigId);
        if (taskConfig == null) { throw new TaskConfigNotFoundException(taskConfigId); }
        return taskConfig;
    }

    public void save(final TaskConfig taskConfig) {
        this.repository.save(taskConfig);
    }

    public void save(final List<TaskConfig> taskConfiges) {
        this.repository.save(taskConfiges);
    }

    public void saveAndFlush(final TaskConfig taskConfig) {
        this.repository.saveAndFlush(taskConfig);
    }

    public void delete(final TaskConfig taskConfig) {
        this.repository.delete(taskConfig);
    }
}