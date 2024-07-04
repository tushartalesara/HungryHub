package com.dev.services;

import com.dev.dao.UserJdbcDao;
import com.dev.models.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

@Service
public class AuthenticatedUser {

    private final UserJdbcDao userJdbcDao;

    @Autowired
    public AuthenticatedUser(UserJdbcDao userJdbcDao) {
        this.userJdbcDao = userJdbcDao;
    }
    @Cacheable(value = "user", key = "#userDetails.username")
    public User getAuthenticatedUser(UserDetails userDetails) {
        try{
            return userJdbcDao.getUserByUsername(userDetails.getUsername());
        }
        catch (Exception e){
//            System.out.println(e.getMessage());
            return null;
        }
    }
}
