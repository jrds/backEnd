package org.github.jrds.codi.core.domain;

import org.eclipse.jetty.util.security.Credential;
import org.eclipse.jetty.util.security.Password;

import java.util.Objects;

public class User
{

    private final String id;
    private final String name;
    private String password = "pw";

    public User(String id, String name)
    {
        this.id = Objects.requireNonNull(id);
        this.name = Objects.requireNonNull(name);
    }

    public String getId()
    {
        return id;
    }

    public String getName()
    {
        return name;
    }

    public boolean changePassword(String oldPassword, String newPassword)
    {
        if (oldPassword.equals(password))
        {
            password = newPassword;
            return true;
        }
        else
        {
            return false;
        }
    }

    public Credential getCredential()
    {
        return new Password(password);
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o)
        {
            return true;
        }
        if (o == null || getClass() != o.getClass())
        {
            return false;
        }
        User user = (User) o;
        return id.equals(user.id);
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(id);
    }
}
