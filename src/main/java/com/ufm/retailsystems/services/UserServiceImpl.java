package com.ufm.retailsystems.services;



import com.ufm.retailsystems.dto.forcreate.CCustomer;
import com.ufm.retailsystems.entities.User;
import com.ufm.retailsystems.entities.enums.ERole;
import com.ufm.retailsystems.repositories.RoleRepository;
import com.ufm.retailsystems.repositories.UserRepository;
import com.ufm.retailsystems.services.templates.UserService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashSet;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private PasswordEncoder bCryptPasswordEncoder;

    @Override
    public boolean login(String userName, String passWord) {
        User user = userRepository.findUserByUsername(userName);
        return user != null && passWord.equals(user.getPassword());
    }

    @Override
    public void save(CCustomer cUser) {
        User user = new User(cUser);
        user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
        user.setRoles((roleRepository.findByName(ERole.USER)));
        userRepository.save(user);
    }

    @Override
    public User findByUsername(String username) {
        return userRepository.findUserByUsername(username);
    }
}
