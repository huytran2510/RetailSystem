package com.ufm.retailsystems;

import com.ufm.retailsystems.entities.Customer;
import com.ufm.retailsystems.entities.Role;
import com.ufm.retailsystems.entities.User;
import com.ufm.retailsystems.entities.enums.ERole;
import com.ufm.retailsystems.repositories.CustomerRepository;
import com.ufm.retailsystems.repositories.RoleRepository;
import com.ufm.retailsystems.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.HashSet;

@SpringBootApplication
public class RetailSystemsApplication implements CommandLineRunner {

    public static void main(String[] args) {
        SpringApplication.run(RetailSystemsApplication.class, args);
    }
    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private PasswordEncoder bCryptPasswordEncoder;

    @Override
    public void run(String... args) throws Exception {
//        Role roleUser = new Role(ERole.USER);
//        Role roleAdmin = new Role(ERole.ADMIN);
//        roleRepository.save(roleUser);
//        roleRepository.save(roleAdmin);
//        User user = new User("huyle", bCryptPasswordEncoder.encode("123456"));
//        user.setRoles(roleRepository.findByName(ERole.ADMIN));
//        userRepository.save(user);

//        Customer customer = new Customer("huyle", bCryptPasswordEncoder.encode("123456"));
//        customerRepository.save(customer);
    }
}
