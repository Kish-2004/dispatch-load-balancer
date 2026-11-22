package com.logistics.dispatch.dto.response;

public record ApiResponse(String message, String status) {

    public static ApiResponse success(String message) {
        return new ApiResponse(message, "success");
    }
}
