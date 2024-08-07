package com.cupcakecorner.models;

public class admin {
    private String username = "admin";
    private String password = "password";
    private String email = "admin@gmail.com";

    public String getAdminUsername(){ return this.username; }
    public String getAdminPassword(){ return this.password; }
    public String getAdminEmail(){ return this.email; }
    public boolean checkAdmin(String username, String password,String email) {
        return this.username.equals(username) && this.password.equals(password) && this.email.equals(email);
    }
}
