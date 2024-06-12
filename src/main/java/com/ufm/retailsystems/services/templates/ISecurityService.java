package com.ufm.retailsystems.services.templates;

public interface ISecurityService {
    String findLoggedInUsername();
    void autoLogin(String username, String password);
}
