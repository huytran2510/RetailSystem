package com.ufm.retailsystems.services;

import com.ufm.retailsystems.dto.forcreate.CCustomer;
import com.ufm.retailsystems.entities.Customer;
import com.ufm.retailsystems.entities.Role;
import com.ufm.retailsystems.entities.enums.Gender;
import com.ufm.retailsystems.repositories.CustomerRepository;
import com.ufm.retailsystems.services.templates.ICustomerService;
import com.ufm.retailsystems.services.templates.IEmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Set;

@Service("myCustomerDetailsService")
public class CustomerServiceImpl implements ICustomerService,UserDetailsService {

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private IEmailService emailService;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private PasswordEncoder passwordEncoder;


    @Override
    public boolean login(String userName, String passWord) {
        Customer customer = customerRepository.findByUsername(userName);
        if (customer == null) {
            return false;
        }
        UserDetails userDetails = loadUserByUsername(userName);

        // Verify the password
        if (!passwordEncoder.matches(passWord, customer.getPassword())) {
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
//
        return false;
    }

    @Transactional(readOnly = true)
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Customer customer = customerRepository.findByUsername(username);
        if (customer == null) {
            throw new UsernameNotFoundException(username);
        }

        Set<GrantedAuthority> grantedAuthorities = new HashSet<>();
        grantedAuthorities.add(new SimpleGrantedAuthority("ROLE_CUSTOMER"));

        return new User(customer.getUsername(), customer.getPassword(), grantedAuthorities);
    }

    @Override
    public Customer add(CCustomer cCustomer) {
        Customer customer = new Customer();
        customer.setUsername(cCustomer.getUsername());
        customer.setPassword(passwordEncoder.encode(cCustomer.getPassword()));
        customer.setAddress(cCustomer.getAddress());
        customer.setEmail(cCustomer.getEmail());
        customer.setBirthday(cCustomer.getBirthday());
        customer.setPhone(cCustomer.getPhone());
        customer.setGender((cCustomer.getGender()));
        emailService.sendPasswordResetEmail(cCustomer.getEmail(), "ahihi");
        customerRepository.save(customer);
        return customer;
    }
}
