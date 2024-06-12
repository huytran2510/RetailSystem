package com.ufm.retailsystems.repositories;


import com.ufm.retailsystems.entities.Customer;
import com.ufm.retailsystems.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CustomerRepository extends JpaRepository<Customer,Long> {
    Customer findByUsername(String username);
}
