package com.example.prif233.dto.user;

public class ClientCreateRequestDTO {
    private String login;
    private String password;
    private String name;
    private String surname;
    private String phoneNumber;
    private String address;

    public ClientCreateRequestDTO(String login, String password, String name, String surname, String phoneNumber, String address) {
        this.login = login;
        this.password = password;
        this.name = name;
        this.surname = surname;
        this.phoneNumber = phoneNumber;
        this.address = address;
    }

    public ClientCreateRequestDTO() {}

    public String getLogin() { return login; }
    public void setLogin(String login) { this.login = login; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getSurname() { return surname; }
    public void setSurname(String surname) { this.surname = surname; }

    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }
}
