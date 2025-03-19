package com.oldvabik.internetshop.cache;

import com.oldvabik.internetshop.model.User;
import org.springframework.stereotype.Component;

@Component
public class UserCache extends LfuCache<User> {
    public UserCache() {
        super(5);
    }
}
