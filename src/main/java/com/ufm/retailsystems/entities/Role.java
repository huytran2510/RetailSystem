package com.ufm.retailsystems.entities;

import com.ufm.retailsystems.entities.enums.ERole;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.Set;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Role {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long roleId;

    @Column(unique = true)
    @Enumerated(EnumType.STRING)
    private ERole name;

    @OneToMany(mappedBy = "roles")
    private Set<User> user;
    

    public Role(ERole name) {
        this.name = name;
    }
}