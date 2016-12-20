package org.apache.fineract.infrastructure.security.service;

import org.apache.fineract.useradministration.domain.AppUser;
import org.apache.fineract.useradministration.service.AppUserWritePlatformService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.security.authentication.event.AuthenticationSuccessEvent;
import org.springframework.stereotype.Component;

@Component
public class AuthenticationSuccessEventListener implements ApplicationListener<AuthenticationSuccessEvent> {

    final AppUserWritePlatformService appUserWritePlatformService;

    @Autowired
    public AuthenticationSuccessEventListener(final AppUserWritePlatformService appUserWritePlatformService) {
        this.appUserWritePlatformService = appUserWritePlatformService;
    }

    @Override
    public void onApplicationEvent(AuthenticationSuccessEvent event) {

        AppUser currentUser = null;
        if (event.getAuthentication().getPrincipal() != null && event.getAuthentication().getPrincipal() instanceof AppUser) {
            currentUser = (AppUser) event.getAuthentication().getPrincipal();
            this.appUserWritePlatformService.updateSuccessLoginStatus(currentUser);
        }

    }

}
