package com.ufm.retailsystems.dto.forcreate;

import com.ufm.retailsystems.entities.enums.Gender;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;
import java.time.LocalDate;


@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class CCustomer {

    @Size(min = 6, max = 20, message = "length")
    @NotEmpty(message = "Please enter username")
    private String username;

    @Size(min = 6, max = 20, message = "length")
    @NotEmpty(message = "Please enter password")
    private String password;

    @Size(min = 6, max = 20, message = "length")
    @NotEmpty(message = "Please enter password")
    private String passwordConfirm;

    private String firstName;
    private String lastName;
    private String email;
    private String phone;
    private String address;
    private LocalDate birthday;

    @Enumerated(EnumType.STRING)
    private Gender gender;
}
