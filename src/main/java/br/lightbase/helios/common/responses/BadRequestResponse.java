package br.lightbase.helios.common.responses;

import org.springframework.http.HttpStatus;

public class BadRequestResponse<T> extends Response<T> {
    
    public BadRequestResponse(T data, String errormsg) {
        super(HttpStatus.BAD_REQUEST.value(), data, errormsg);
    }
}