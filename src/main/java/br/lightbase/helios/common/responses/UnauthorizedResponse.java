package br.lightbase.helios.common.responses;

import org.springframework.http.HttpStatus;

public class UnauthorizedResponse<T> extends Response<T> {
    
    public UnauthorizedResponse(T data, String errormsg) {
        super(HttpStatus.UNAUTHORIZED.value(), data, errormsg);
    }
}