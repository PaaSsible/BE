package com.paassible.common.response;


import com.paassible.common.exception.FieldErrorDetail;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;


@Getter
@AllArgsConstructor
public class ApiResponse<T> {
    private boolean success;
    private String message;
    private T data;
    private String code;
    private List<FieldErrorDetail> errors;

    public static <T> ApiResponse<T> success(BaseResponseCode code, T data) {
        return new ApiResponse<>(true, code.getMessage(), data, code.getCode(), null);
    }

    public static ApiResponse<Void> success(BaseResponseCode code) {
        return new ApiResponse<>(true, code.getMessage(), null, code.getCode(), null);
    }

    public static <T> ApiResponse<T> fail(BaseResponseCode code) {
        return new ApiResponse<>(false, code.getMessage(), null, code.getCode(), null);
    }

    public static <T> ApiResponse<T> fail(BaseResponseCode code, List<FieldErrorDetail> errors) {
        return new ApiResponse<>(false, code.getMessage(), null, code.getCode(), errors);
    }
}
