package com.ticketmaster.eventservice.result;

import org.springframework.http.HttpStatus;

import static com.ticketmaster.eventservice.result.ApiError.*;

public abstract class BaseController {

    protected HttpStatus resolveStatus(Result<?> result) {
        if (result == null || result.getError() == null) {
            return HttpStatus.INTERNAL_SERVER_ERROR;
        }

        return switch (result.getError().getCode()) {
            case BAD_REQ_ERR_CODE -> HttpStatus.BAD_REQUEST;
            case FORBIDDEN_ERR_CODE -> HttpStatus.FORBIDDEN;
            case UNAUTHORIZED_ERR_CODE -> HttpStatus.UNAUTHORIZED;
            case NOT_FOUND_ERR_CODE -> HttpStatus.NOT_FOUND;
            case CONFLICT_ERR_CODE -> HttpStatus.CONFLICT;
            case NONE_ERR_CODE -> HttpStatus.OK;
            default -> HttpStatus.INTERNAL_SERVER_ERROR;
        };
    }
}
