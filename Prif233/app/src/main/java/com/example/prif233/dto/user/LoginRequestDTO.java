package com.example.prif233.dto.user;

public class LoginRequestDTO {
    private String login;
    private String password;

    public LoginRequestDTO() {
    }

    public LoginRequestDTO(String login, String password) {
        this.login = login;
        this.password = password;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}

