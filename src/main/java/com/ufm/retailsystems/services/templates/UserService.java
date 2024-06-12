package com.ufm.retailsystems.services.templates;


import com.ufm.retailsystems.dto.forcreate.CCustomer;
import com.ufm.retailsystems.entities.User;

public interface UserService {
    boolean login(String username, String password);

    void save(CCustomer CUser);

    User findByUsername(String username);
}
