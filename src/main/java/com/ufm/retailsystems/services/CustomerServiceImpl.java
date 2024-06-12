package com.ufm.retailsystems.services;

import com.ufm.retailsystems.entities.Customer;
import com.ufm.retailsystems.entities.Role;
import com.ufm.retailsystems.entities.User;
import com.ufm.retailsystems.repositories.CustomerRepository;
import com.ufm.retailsystems.services.templates.ICustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Set;

@Service
public class CustomerServiceImpl implements ICustomerService {

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;
    @Override
    public boolean login(String userName, String passWord) {
        Customer customer = customerRepository.findByUsername(userName);
        if (customer == null) {
            return false;
        }
        UserDetails userDetails = loadUserByUsername(userName);

        // Verify the password
        if (!bCryptPasswordEncoder.matches(passWord, customer.getPassword())) {
            return false;
        }

        // Create authentication token
        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(userDetails, passWord, userDetails.getAuthorities());

        try {
            Authentication authentication = authenticationManager.authenticate(authenticationToken);

            if (authentication.isAuthenticated()) {
                SecurityContextHolder.getContext().setAuthentication(authentication);
                return true;
            }
        } catch (AuthenticationException e) {
            // Handle authentication failure
            return false;
        }

        return false;
    }

    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Customer customer = customerRepository.findByUsername(username);
        if (customer == null){
            throw new UsernameNotFoundException(username);
        }
        Set<GrantedAuthority> grantedAuthorities = new HashSet<>();
            grantedAuthorities.add(new SimpleGrantedAuthority("ROLE_USER"));


        return new org.springframework.security.core.userdetails.User(customer.getUsername(), customer.getPassword(),
                grantedAuthorities);
    }
}
