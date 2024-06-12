package com.ufm.retailsystems.services;



import com.ufm.retailsystems.entities.Role;
import com.ufm.retailsystems.repositories.RoleRepository;
import com.ufm.retailsystems.services.templates.IRoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class RoleServiceImpl implements IRoleService {
    @Autowired
    private RoleRepository roleRepository;

    @Override
    public void save(Role role) {
        roleRepository.save(role);
    }
}
