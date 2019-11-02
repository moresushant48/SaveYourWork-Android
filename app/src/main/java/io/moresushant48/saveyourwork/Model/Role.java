package io.moresushant48.saveyourwork.Model;

import java.util.ArrayList;
import java.util.List;

public class Role {

    private long id;

    private String role;

    private String desc;

    private List<User> users = new ArrayList<User>();


    public Role(long id, String role, String desc, List<User> users) {
        this.id = id;
        this.role = role;
        this.desc = desc;
        this.users = users;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public List<User> getUsers() {
        return users;
    }

    public void setUsers(List<User> users) {
        this.users = users;
    }
}
