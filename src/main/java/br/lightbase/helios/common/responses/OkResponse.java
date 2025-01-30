package br.lightbase.helios.common.responses;

import org.springframework.http.HttpStatus;

public class OkResponse<T> extends Response<T> {
    
    public OkResponse(T data) {
        super(HttpStatus.OK.value(), data, null);
    }
}