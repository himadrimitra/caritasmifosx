package org.apache.fineract.infrastructure.security.service;

import org.apache.fineract.useradministration.service.AppUserWritePlatformService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.security.authentication.event.AuthenticationFailureBadCredentialsEvent;
import org.springframework.stereotype.Component;

@Component
public class AuthenticationBadCredentialsEventListener implements ApplicationListener<AuthenticationFailureBadCredentialsEvent> {

    final AppUserWritePlatformService appUserWritePlatformService;

    @Autowired
    public AuthenticationBadCredentialsEventListener(final AppUserWritePlatformService appUserWritePlatformService) {
        this.appUserWritePlatformService = appUserWritePlatformService;
    }

    @Override
    public void onApplicationEvent(AuthenticationFailureBadCredentialsEvent event) {
        String userName = (String) event.getAuthentication().getPrincipal();
        if (userName != null) {
            this.appUserWritePlatformService.updateFailedLoginStatus(userName);
        }

    }

}
