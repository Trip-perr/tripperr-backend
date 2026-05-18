package com.tripperr.api.common;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.List;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record ApiError(
        String code,
        String message,
        Map<String, String> fieldErrors,
        List<String> details,
        String path
) {
    public static ApiError of(String code, String message, String path) {
        return new ApiError(code, message, null, null, path);
    }

    public static ApiError validation(String message, Map<String, String> fieldErrors, String path) {
        return new ApiError("VALIDATION_ERROR", message, fieldErrors, null, path);
    }
}
