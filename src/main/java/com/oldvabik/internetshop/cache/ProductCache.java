package com.oldvabik.internetshop.cache;

import com.oldvabik.internetshop.model.Product;
import org.springframework.stereotype.Component;

@Component
public class ProductCache extends LfuCache<Product> {
    public ProductCache() {
        super(10);
    }
}
