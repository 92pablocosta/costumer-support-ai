package io.pablo.aicustomersupport.chat.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ChatRequest(
        @NotBlank @Size(max = 10000) String message,
        @Size(max = 100) String model
) {
}
