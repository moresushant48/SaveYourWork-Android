package io.moresushant48.saveyourwork.Model;

import java.util.ArrayList;
import java.util.List;

/*
 * Model Class for Access
 */
public class Access {

    private long id;

    private String access;

    private List<File> files = new ArrayList<>();

    public Access() {
    }

    public Access(long id) {
        this.id = id;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getAccess() {
        return access;
    }

    public void setAccess(String access) {
        this.access = access;
    }

    public List<File> getFiles() {
        return files;
    }

    public void setFiles(List<File> files) {
        this.files = files;
    }
}