package br.lightbase.helios.common.responses;

import org.springframework.http.HttpStatus;

public class NotFoundResponse<T> extends Response<T> {
    
    public NotFoundResponse(T data, String errormsg) {
        super(HttpStatus.NOT_FOUND.value(), data, errormsg);
    }
}