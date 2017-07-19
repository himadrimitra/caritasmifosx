package org.apache.fineract.infrastructure.core.exceptionmapper;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import org.apache.fineract.infrastructure.core.data.ApiGlobalErrorResponse;
import org.apache.fineract.infrastructure.core.exception.PlatformMultipleDomainValidationException;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Provider
@Component
@Scope("singleton")
public class PlatformMultipleDomainValidationExceptionMapper implements ExceptionMapper<PlatformMultipleDomainValidationException> {

    @Override
    public Response toResponse(final PlatformMultipleDomainValidationException exception) {

        final ApiGlobalErrorResponse notFoundErrorResponse = ApiGlobalErrorResponse.badClientRequest(
                exception.getGlobalisationMessageCode(), exception.getDefaultUserMessage(), exception.getErrors());
        return Response.status(Status.BAD_REQUEST).entity(notFoundErrorResponse).type(MediaType.APPLICATION_JSON).build();
    }
}
