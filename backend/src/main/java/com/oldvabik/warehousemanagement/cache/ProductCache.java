package com.oldvabik.warehousemanagement.cache;

import com.oldvabik.warehousemanagement.model.Product;
import org.springframework.stereotype.Component;

@Component
public class ProductCache extends LfuCache<Product> {
    public ProductCache() {
        super(10);
    }
}
