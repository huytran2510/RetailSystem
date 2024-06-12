package com.ufm.retailsystems.repositories;


import com.ufm.retailsystems.entities.Role;
import com.ufm.retailsystems.entities.enums.ERole;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RoleRepository extends JpaRepository<Role, Long> {

    Role findByName(ERole name);
}
