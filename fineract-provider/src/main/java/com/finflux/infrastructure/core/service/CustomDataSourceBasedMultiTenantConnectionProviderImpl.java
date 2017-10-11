package com.finflux.infrastructure.core.service;

import javax.sql.DataSource;

import org.apache.fineract.infrastructure.core.service.RoutingDataSourceServiceFactory;
import org.hibernate.engine.jdbc.connections.spi.AbstractDataSourceBasedMultiTenantConnectionProviderImpl;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

@Component
public class CustomDataSourceBasedMultiTenantConnectionProviderImpl 
	extends AbstractDataSourceBasedMultiTenantConnectionProviderImpl 
	implements ApplicationContextAware{
	
	private static ApplicationContext CONTEXT;
	
    private static RoutingDataSourceServiceFactory dataSourceServiceFactory;
    
    private static DataSource tenantDataSource;
    
    public CustomDataSourceBasedMultiTenantConnectionProviderImpl(){
    	if(CONTEXT != null){
        	if(dataSourceServiceFactory == null){
        		dataSourceServiceFactory = (RoutingDataSourceServiceFactory)CONTEXT.getBean("routingDataSourceServiceFactory");
        	}
        	if(tenantDataSource == null){
        		tenantDataSource = (DataSource)CONTEXT.getBean("tenantDataSourceJndi");
        	}
    	}
    }

	@Override
	protected DataSource selectAnyDataSource() {
		return tenantDataSource;
	}

	@Override
	protected DataSource selectDataSource(@SuppressWarnings("unused") String tenantIdentifier) {
		return dataSourceServiceFactory.determineDataSourceService().retrieveDataSource();
	}

	@Override
	public void setApplicationContext(ApplicationContext applicationContext)
			throws BeansException {
		CONTEXT = applicationContext;
	}

}
