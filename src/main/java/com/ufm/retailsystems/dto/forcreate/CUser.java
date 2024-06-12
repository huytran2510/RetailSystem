package com.ufm.retailsystems.dto.forcreate;

import com.ufm.retailsystems.dto.uservalid.FieldMatch;
import lombok.*;

import javax.validation.constraints.Size;

@FieldMatch(first = "password", second = "passwordConfirm", message = "The password fields must match")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class CUser {
    @Size(min = 6, max = 20, message = "Length of username should be from 6 to 20")
    private String username;

    @Size(min = 6, max = 20, message = "Length of password should be from 6 to 20")
    private String password;

    @Size(min = 6, max = 20, message = "Length of password confirm should be from 6 to 20")
    private String passwordConfirm;

    public CUser(String username, String password) {
        this.username = username;
        this.password = password;
    }
}