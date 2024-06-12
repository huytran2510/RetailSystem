package com.ufm.retailsystems.repositories;


import com.ufm.retailsystems.entities.Role;
import com.ufm.retailsystems.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Set;

@Repository
public interface UserRepository extends JpaRepository<User,Long> {
    User findUserByUsername(String username);

    @Query("SELECT u.roles FROM User u WHERE u.username = :username")
    Set<Role> findRolesByUsername(@Param("username") String username);
}
