package com.web.helpforce.domain.user.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class SuccessResponse<T> {
    private boolean success;
    private T data;

    public static <T> SuccessResponse<T> of(T data) {
        return new SuccessResponse<>(true, data);
    }
}