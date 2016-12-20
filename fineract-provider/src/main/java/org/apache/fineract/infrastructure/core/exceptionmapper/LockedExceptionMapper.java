package org.apache.fineract.infrastructure.core.exceptionmapper;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import org.apache.fineract.infrastructure.core.data.ApiGlobalErrorResponse;
import org.springframework.context.annotation.Scope;
import org.springframework.security.authentication.LockedException;
import org.springframework.stereotype.Component;

@Provider
@Component
@Scope("singleton")
public class LockedExceptionMapper implements ExceptionMapper<LockedException> {

    @Override
    public Response toResponse(@SuppressWarnings("unused") LockedException exception) {
        return Response.status(Status.UNAUTHORIZED).entity(ApiGlobalErrorResponse.accountLocked()).type(MediaType.APPLICATION_JSON).build();
    }

}
