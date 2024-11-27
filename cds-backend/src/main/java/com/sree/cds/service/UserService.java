package com.sree.cds.service;

import com.sree.cds.entity.User;
import com.sree.cds.entity.UserModel;

import java.util.Optional;

public interface UserService {

    public User registerUser(UserModel user);

}
