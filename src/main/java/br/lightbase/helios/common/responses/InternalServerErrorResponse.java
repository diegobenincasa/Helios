package br.lightbase.helios.common.responses;

import org.springframework.http.HttpStatus;

public class InternalServerErrorResponse<T> extends Response<T> {
    
    public InternalServerErrorResponse(T data, String errormsg) {
        super(HttpStatus.INTERNAL_SERVER_ERROR.value(), data, errormsg);
    }
}