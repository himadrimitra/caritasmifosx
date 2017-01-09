package org.apache.fineract.infrastructure.core.exception;

public class ExceptionHelper {

    public static String fetchExceptionMessage(Exception e) {
        String rootCause = null;
        if (e instanceof AbstractPlatformResourceNotFoundException) {
            AbstractPlatformResourceNotFoundException execption = (AbstractPlatformResourceNotFoundException) e;
            rootCause = execption.getGlobalisationMessageCode();
        } else if (e instanceof AbstractPlatformDomainRuleException) {
            AbstractPlatformDomainRuleException execption = (AbstractPlatformDomainRuleException) e;
            rootCause = execption.getGlobalisationMessageCode();
        } else if (e instanceof AbstractPlatformServiceUnavailableException) {
            AbstractPlatformServiceUnavailableException execption = (AbstractPlatformServiceUnavailableException) e;
            rootCause = execption.getGlobalisationMessageCode();
        } else if (e instanceof PlatformApiDataValidationException) {
            PlatformApiDataValidationException execption = (PlatformApiDataValidationException) e;
            rootCause = execption.getGlobalisationMessageCode();
        } else if (e instanceof PlatformDataIntegrityException) {
            PlatformDataIntegrityException execption = (PlatformDataIntegrityException) e;
            rootCause = execption.getGlobalisationMessageCode();
        } else if (e instanceof PlatformInternalServerException) {
            PlatformInternalServerException execption = (PlatformInternalServerException) e;
            rootCause = execption.getGlobalisationMessageCode();
        } else if (e.getCause() != null) {
            rootCause = e.getCause().getMessage();
        }
        return rootCause;
    }
}
