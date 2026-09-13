package com.researchmate.mcp.exception;

import lombok.Getter;

@Getter
public class AcademicProviderException extends RuntimeException {

    private final int statusCode;

    public AcademicProviderException(
            String message,
            int statusCode,
            Throwable cause
    ) {
        super(message, cause);
        this.statusCode = statusCode;
    }
}