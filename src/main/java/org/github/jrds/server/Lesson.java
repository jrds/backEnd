package org.github.jrds.server;

import java.util.Set;

public class Lesson {

    private String id;
    private User educator;
    private Set<User> learners; 

    public Lesson(String id, User educator, Set<User> learners) { 
        this.id = id;                                                 
        this.educator = educator;
        this.learners = learners;
    }

    public String getId() {
        return id;
    }

    public User getEducator() {
        return educator;
    }

    public Set<User> getLearners() {
        return learners;
    }

    public void addLearner(User learner) { // could pass in other detail to construct user??
        learners.add(learner);
    }

    public boolean isRegisteredLearner(String learner) {
        return learners.contains(Main.usersStore.getUser(learner));
    }

    public boolean canConnect(String user) {
        return isRegisteredLearner(user) || educator.getId().equals(user);
    }

    @Override
    public String toString() {
        return "Lesson [Educator Name =" + educator.getName() + ", Lesson id=" + id + ", learners=" + learners + "]";
    }

}
