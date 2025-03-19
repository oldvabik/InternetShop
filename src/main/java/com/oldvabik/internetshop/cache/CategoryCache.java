package com.oldvabik.internetshop.cache;

import com.oldvabik.internetshop.model.Category;
import org.springframework.stereotype.Component;

@Component
public class CategoryCache extends LfuCache<Category> {
    public CategoryCache() {
        super(3);
    }
}
