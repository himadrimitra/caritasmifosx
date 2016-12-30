package org.apache.fineract.infrastructure.security.service;

import java.util.concurrent.Executor;

import org.apache.fineract.infrastructure.core.domain.FineractPlatformTenant;
import org.apache.fineract.infrastructure.core.service.ThreadLocalContextUtil;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.AbstractApplicationEventMulticaster;

public class FinfluxApplicationEventMulticaster extends AbstractApplicationEventMulticaster {

    private Executor taskExecutor;

    /**
     * Create a new SimpleApplicationEventMulticaster.
     */
    public FinfluxApplicationEventMulticaster() {}

    /**
     * Create a new SimpleApplicationEventMulticaster for the given BeanFactory.
     */
    public FinfluxApplicationEventMulticaster(BeanFactory beanFactory) {
        setBeanFactory(beanFactory);
    }

    /**
     * Set the TaskExecutor to execute application listeners with.
     * <p>
     * Default is a SyncTaskExecutor, executing the listeners synchronously in
     * the calling thread.
     * <p>
     * Consider specifying an asynchronous TaskExecutor here to not block the
     * caller until all listeners have been executed. However, note that
     * asynchronous execution will not participate in the caller's thread
     * context (class loader, transaction association) unless the TaskExecutor
     * explicitly supports this.
     * 
     * @see org.springframework.core.task.SyncTaskExecutor
     * @see org.springframework.core.task.SimpleAsyncTaskExecutor
     */
    public void setTaskExecutor(Executor taskExecutor) {
        this.taskExecutor = taskExecutor;
    }

    /**
     * Return the current TaskExecutor for this multicaster.
     */
    protected Executor getTaskExecutor() {
        return this.taskExecutor;
    }

    @Override
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public void multicastEvent(final ApplicationEvent event) {
        FineractPlatformTenant tenant = ThreadLocalContextUtil.getTenant();
        for (final ApplicationListener listener : getApplicationListeners(event)) {
            Executor executor = getTaskExecutor();
            if (executor != null) {
                executor.execute(new Runnable() {
                    @Override
                    public void run() {
                        if (tenant != null) {
                            ThreadLocalContextUtil.setTenant(tenant);
                        }
                        listener.onApplicationEvent(event);
                    }
                });
            } else {
                listener.onApplicationEvent(event);
            }
        }
    }

}
