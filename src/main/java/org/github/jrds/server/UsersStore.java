package org.github.jrds.server;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.jetty.util.security.Password;

public class UsersStore {

    Map<String, User> usersStore;

    public UsersStore() {
        this.usersStore = new HashMap<>();
    }
    
    public User getUser(String userId) {
        if (usersStore.containsKey(userId)) {
            return usersStore.get(userId);
        } else {
            return null;
        }
    }

    public void addUser(String id, String name) {
        usersStore.put(id, new User(id, name));
        Main.authUserStore.addUser(id, new Password("pw"), new String[] { "user"});
    }

    // TODO - QUESTION 0 - I have approached this differently to Lesson, 
    // here userStore is incharge of creating the user, and adding it to both the userStore and authStore
    // not sure if lesson should be the same for continuity ?   


    // This is just to have the option of not using the default password.
    public void addUser(String id, String name, String password) {
        usersStore.put(id, new User(id, name));
        Main.authUserStore.addUser(id, new Password(password), new String[] { "user"});
    } 

    public Map<String, User> getUsersStore() {
        return usersStore;
    } 
}
