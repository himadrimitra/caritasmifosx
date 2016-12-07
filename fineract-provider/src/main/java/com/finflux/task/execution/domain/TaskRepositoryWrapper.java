package com.finflux.task.execution.domain;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.finflux.task.execution.exception.TaskNotFoundException;

@Service
public class TaskRepositoryWrapper {

    private final TaskRepository repository;

    @Autowired
    public TaskRepositoryWrapper(final TaskRepository repository) {
        this.repository = repository;
    }

    public Task findOneWithNotFoundDetection(final Long taskId) {
        final Task task = this.repository.findOne(taskId);
        if (task == null) { throw new TaskNotFoundException(taskId); }
        return task;
    }

    public void save(final Task task) {
        this.repository.save(task);
    }

    public void save(final List<Task> tasks) {
        this.repository.save(tasks);
    }

    public void saveAndFlush(final Task task) {
        this.repository.saveAndFlush(task);
    }

    public void delete(final Task task) {
        this.repository.delete(task);
    }
}