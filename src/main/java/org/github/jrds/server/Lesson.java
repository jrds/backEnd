package org.github.jrds.server;

import java.util.Set;

public class Lesson {

    private String id;
    private String educator; // TODO - (not yet) user eventually ?, user & userStore classes 
    private Set<String> learners; // TODO - (not yet) user not string 

    public Lesson(String id, String educator, Set<String> learners) {
        this.id = id;
        this.educator = educator;
        this.learners = learners;
    }

    public String getId() {
        return id;
    }

    public String getEducator() {
        return educator;
    }

    public Set<String> getLearners() {
        return learners;
    }

    public void addLearner(String learner) { // TODO this will eventually by passing in user, and user.getName user.getID etc will be used
        learners.add(learner);
    }

    public boolean checkForALearner(String learner) {
        if (learners.contains(learner)) {
            return true;
        }
        else {
            return false;
        }
    }

}
