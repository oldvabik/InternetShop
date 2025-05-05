package com.oldvabik.warehousemanagement.cache;

import com.oldvabik.warehousemanagement.model.Order;
import org.springframework.stereotype.Component;

@Component
public class OrderCache extends LfuCache<Order> {
    public OrderCache() {
        super(10);
    }
}
