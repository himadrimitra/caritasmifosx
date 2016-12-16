package com.finflux.task.domain;

import java.util.List;

import com.finflux.task.exception.TaskNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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

    public Task save(final Task task) {
        return this.repository.save(task);
    }

    public List<Task> save(final List<Task> tasks) {
        return this.repository.save(tasks);
    }

    public void saveAndFlush(final Task task) {
        this.repository.saveAndFlush(task);
    }

    public void delete(final Task task) {
        this.repository.delete(task);
    }
}