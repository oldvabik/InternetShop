package com.oldvabik.warehousemanagement.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CategoryDto {
    @NotNull
    private String name;
}
