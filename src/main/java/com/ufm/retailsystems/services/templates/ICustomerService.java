package com.ufm.retailsystems.services.templates;

import com.ufm.retailsystems.dto.forcreate.CCustomer;
import com.ufm.retailsystems.entities.Customer;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

public interface ICustomerService {
    boolean login(String userName, String passWord);
    Customer add(CCustomer cCustomer);

    Customer findByUsername(String username);
    void updateProfile(Customer updatedCustomer);
    boolean changePassword(String username, String currentPassword, String newPassword, String confirmPassword);
}
