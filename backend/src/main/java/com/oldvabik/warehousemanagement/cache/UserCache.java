package com.oldvabik.warehousemanagement.cache;

import com.oldvabik.warehousemanagement.model.User;
import org.springframework.stereotype.Component;

@Component
public class UserCache extends LfuCache<User> {
    public UserCache() {
        super(5);
    }
}
