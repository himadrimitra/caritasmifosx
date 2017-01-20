package org.apache.fineract.infrastructure.jobs.service;



public interface JobRunner<T> {

    public void runJob(final T jobDetails, final StringBuilder sb);
    
}
