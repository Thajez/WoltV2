package com.example.prif233.dto.user;

public class LoginResponseDTO {
    private int id;
    private String login;
    private String name;
    private String surname;
    private String userType;

    public LoginResponseDTO() {
    }

    public LoginResponseDTO(int id, String login, String name, String surname, String userType) {
        this.id = id;
        this.login = login;
        this.name = name;
        this.surname = surname;
        this.userType = userType;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getUserType() {
        return userType;
    }

    public void setUserType(String userType) {
        this.userType = userType;
    }
}