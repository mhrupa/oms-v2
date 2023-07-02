package com.technivaaran.utils;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.technivaaran.dto.OmsResponse;

public class ResponseUtil {
    
    private ResponseUtil() {}
    
    public static ResponseEntity<OmsResponse> createResponseEntity(String message, Object data, HttpStatus httpStatus) {
        return new ResponseEntity<>(OmsResponse.builder().message(message)
                .data(data).build(), httpStatus);
    }
}
