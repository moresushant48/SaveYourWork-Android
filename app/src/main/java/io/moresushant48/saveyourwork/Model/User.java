package io.moresushant48.saveyourwork.Model;

public class User {

    private int id;

    private String email;

    private String username;

    private String password;

    private String publicPass;

    private Role role;

    public User(int id, String email, String username, String password, String publicPass, Role role) {
        this.id = id;
        this.email = email;
        this.username = username;
        this.password = password;
        this.publicPass = publicPass;
        this.role = role;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPublicPass() {
        return publicPass;
    }

    public void setPublicPass(String publicPass) {
        this.publicPass = publicPass;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }
}
