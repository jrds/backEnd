package org.github.jrds.codi.core.persistence;

import org.eclipse.jetty.security.UserStore;
import org.github.jrds.codi.core.domain.User;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class UsersStore
{

    private final UserStore authUserStore = new UserStore();
    private final Map<String, User> usersStore;

    public UsersStore()
    {
        this.usersStore = new HashMap<>();
        // Mocked up user store for prototype 
        // These match to the strings defined in ApplicationTest.java
        storeUser(new User("u1900", "Jordan"));
        storeUser(new User("e0001", "Educator Rebecca"));
        storeUser(new User("u1901", "Ambre"));
        storeUser(new User("u1902", "Aaron"));
        storeUser(new User("u1903", "Anthony"));
        storeUser(new User("u1904", "Juliette"));
        storeUser(new User("u9999", "Jack"));
    }

    public User getUser(String userId)
    {
        return usersStore.getOrDefault(userId, null);
    }

    public void storeUser(User user)
    {
        usersStore.put(user.getId(), user);
        authUserStore.addUser(user.getId(), user.getCredential(), new String[]{"user"});
    }

    public Set<User> getAllUsers()
    {
        return new HashSet<>(usersStore.values());
    }

    public UserStore getAuthUserStore()
    {
        return authUserStore;
    }

}
