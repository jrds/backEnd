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

    public Role getUserRole(User user){
        if (educator.equals(user)){
            return Role.EDUCATOR;
        }
        else if (learners.contains(user)) {
            return Role.LEARNER;
        }
        else {
            return Role.NONE;
        }
    }

    @Override
    public String toString() {
        return "Lesson [Educator Name =" + educator.getName() + ", Lesson id=" + id + ", learners=" + learners + "]";
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
        Lesson other = (Lesson) obj;
        if (id == null) {
            if (other.id != null)
                return false;
        } else if (!id.equals(other.id))
            return false;
        return true;
    }
}
