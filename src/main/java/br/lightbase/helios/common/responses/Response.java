package br.lightbase.helios.common.responses;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@RequiredArgsConstructor
@AllArgsConstructor
public abstract class Response<T> {
    
    final Integer status;

    @Setter
    private T data;

    @Setter
    private String errormsg;

    
}
