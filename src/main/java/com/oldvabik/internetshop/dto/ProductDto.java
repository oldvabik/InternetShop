package com.oldvabik.internetshop.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ProductDto {
    @NotNull
    private String name;
    @NotNull
    @DecimalMin(value = "0.1")
    private Double price;
    @NotNull
    @Min(value = 1)
    private Integer quantity;
    @NotNull
    private String categoryName;
}
