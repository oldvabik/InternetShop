package com.oldvabik.internetshop.cache;

import com.oldvabik.internetshop.model.Order;
import org.springframework.stereotype.Component;

@Component
public class OrderCache extends LfuCache<Order> {
    public OrderCache() {
        super(10);
    }
}
