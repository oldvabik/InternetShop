package com.oldvabik.internetshop.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.ArrayList;
import java.util.List;
import lombok.Data;

@Data
public class OrderDto {
    @NotNull
    @Size(min = 1, max = 10)
    private List<OrderProductRequest> items = new ArrayList<>();

    @Data
    public static class OrderProductRequest {
        private String productName;
        private Integer quantity;
    }
}
