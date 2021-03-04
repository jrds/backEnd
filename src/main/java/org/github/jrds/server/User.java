package org.github.jrds.server;

import org.eclipse.jetty.util.security.Credential;
import org.eclipse.jetty.util.security.Password;

public class User {
    
    private String id;
    private String name;
    private String password = "pw";

    public User(String id, String name) {
        this.id = id;
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public boolean changePassword(String oldPassword, String newPassword){
        if (oldPassword.equals(password)){
            password = newPassword;
            return true;
        }
        else {
            return false;
        }
    }

    public Credential getCredential(){
        return new Password(password);
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((id == null) ? 0 : id.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        User other = (User) obj;
        if (id == null) {
            if (other.id != null)
                return false;
        } else if (!id.equals(other.id))
            return false;
        return true;
    }

}
