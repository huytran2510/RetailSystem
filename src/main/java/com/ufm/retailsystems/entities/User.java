package com.ufm.retailsystems.entities;

import com.ufm.retailsystems.dto.forcreate.CCustomer;
import com.ufm.retailsystems.entities.enums.Gender;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.Pattern;
import java.time.LocalDate;
import java.util.Set;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "username", unique = true)
    private String username;

    @Column(name = "password")
    private String password;

    @ManyToOne
    private Role roles;

    @Enumerated(EnumType.STRING)
    private Gender gender;


    private String firstName;
    private String lastName;
    private String email;
    private String phone;
    private LocalDate birthday;

    @OneToMany(mappedBy = "user")
    private Set<Order> orderSet;

    public User(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public User (CCustomer customer) {
        this.username = customer.getUsername();
        this.password = customer.getPassword();
    }

}
