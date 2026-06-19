package com.example.forms.onglets.login;

public class LoginInformations {
    public String email;
    public String password;

    public LoginInformations(String email,  String password) {
        this.email = email;
        this.password =  password;

    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }



    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }


}
