package org.github.jrds.server;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.eclipse.jetty.security.UserStore;

public class UsersStore {

    private UserStore authUserStore = new UserStore();
    private Map<String, User> usersStore;

    public UsersStore() {
        this.usersStore = new HashMap<>();
        // Mocked up user store for prototype 
        // These match to the strings defined in ApplicationTest.java
        storeUser(new User("u1900", "Jordan")); 
        storeUser(new User("e0001", "Educator Rebecca"));
        storeUser(new User("u1901", "Savanna"));
        storeUser(new User("u9999", "Jack"));
    }
    
    public User getUser(String userId) {
        if (usersStore.containsKey(userId)) {
            return usersStore.get(userId);
        } else {
            return null;
        }
    }

    public void storeUser(User user) {
        usersStore.put(user.getId(), user);
        authUserStore.addUser(user.getId(), user.getCredential(), new String[] { "user"});
    }

    public Set<User> getAllUsers() {
        return new HashSet<>(usersStore.values());
    } 

    public UserStore getAuthUserStore() {
        return authUserStore;
    } 

}
