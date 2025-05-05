package com.oldvabik.warehousemanagement.cache;

import com.oldvabik.warehousemanagement.model.Category;
import org.springframework.stereotype.Component;

@Component
public class CategoryCache extends LfuCache<Category> {
    public CategoryCache() {
        super(3);
    }
}
