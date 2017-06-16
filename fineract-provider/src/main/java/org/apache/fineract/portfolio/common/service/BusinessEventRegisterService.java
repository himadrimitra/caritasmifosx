package org.apache.fineract.portfolio.common.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.apache.fineract.infrastructure.core.domain.FineractPlatformTenant;
import org.apache.fineract.infrastructure.core.service.RoutingDataSource;
import org.apache.fineract.infrastructure.core.service.ThreadLocalContextUtil;
import org.apache.fineract.infrastructure.security.service.TenantDetailsService;
import org.apache.fineract.portfolio.common.BusinessEventNotificationConstants.BUSINESS_EVENTS;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
public class BusinessEventRegisterService {

    private final JdbcTemplate jdbcTemplate;
    private final BusinessEventNotifierService businessEventNotifierService;
    private final TenantDetailsService tenantDetailsService;
    private final ApplicationContext applicationContext;
    private final String sql = "select bel.business_event_name as eventName, bel.pre_listeners as preListners,bel.post_listners as postListners from f_business_event_listners bel";
    private final String VALUE_SEPARATOR = ",";

    @Autowired
    public BusinessEventRegisterService(final RoutingDataSource dataSource,
            final BusinessEventNotifierService businessEventNotifierService, TenantDetailsService tenantDetailsService,
            final ApplicationContext applicationContext) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
        this.businessEventNotifierService = businessEventNotifierService;
        this.tenantDetailsService = tenantDetailsService;
        this.applicationContext = applicationContext;
    }

    @PostConstruct
    public void registerAllListners() {
        final List<FineractPlatformTenant> allTenants = this.tenantDetailsService.findAllTenants();
        for (final FineractPlatformTenant tenant : allTenants) {
            ThreadLocalContextUtil.setTenant(tenant);
            final Map<BUSINESS_EVENTS, List<BusinessEventListner>> preListners = new HashMap<>(5);
            final Map<BUSINESS_EVENTS, List<BusinessEventListner>> postListners = new HashMap<>(5);
            
            final List<Map<String, Object>> eventListners = this.jdbcTemplate.queryForList(sql);
            for (Map<String, Object> listners : eventListners) {
                final String businessEventName = (String) listners.get("eventName");
                final BUSINESS_EVENTS businessEvent = BUSINESS_EVENTS.from(businessEventName);
                final String preListnersString = (String) listners.get("preListners");
                final String postListnersString = (String) listners.get("postListners");
                if (!StringUtils.isEmpty(preListnersString)) {
                    String[] preListnersArray = preListnersString.split(VALUE_SEPARATOR);
                    registerForNotification(businessEvent, preListnersArray, preListners);
                }
                if (!StringUtils.isEmpty(postListnersString)) {
                    String[] postListnersArray = postListnersString.split(VALUE_SEPARATOR);
                    registerForNotification(businessEvent, postListnersArray, postListners);
                }
            }
            this.businessEventNotifierService.addBusinessEventTenantBasedPreListners(preListners);
            this.businessEventNotifierService.addBusinessEventTenantBasedPostListners(postListners);
        }
    }
    
    private void registerForNotification(BUSINESS_EVENTS businessEvent , String[] beanNames,final Map<BUSINESS_EVENTS, List<BusinessEventListner>> listners){
        for(String beanName : beanNames){
            final BusinessEventListner businessEventListner = (BusinessEventListner) this.applicationContext.getBean(beanName);
            addBusinessEventListners(businessEvent, businessEventListner, listners);
        }
    }
    
    private void addBusinessEventListners(BUSINESS_EVENTS businessEvent, BusinessEventListner businessEventListner,
            final Map<BUSINESS_EVENTS, List<BusinessEventListner>> businessEventListnerMap) {
        List<BusinessEventListner> businessEventListners = businessEventListnerMap.get(businessEvent);
        if (businessEventListners == null) {
            businessEventListners = new ArrayList<>();
            businessEventListnerMap.put(businessEvent, businessEventListners);
        }
        businessEventListners.add(businessEventListner);
    }

}
