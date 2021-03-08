package org.github.jrds.server;


import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

public class Lesson {

    private String id;
    private User educator;
    private Set<User> learners; 
    private Map<String, Instruction> instructions = new TreeMap<>();

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

    public void createInstruction(String title, String body, User author){
        if(author.equals(educator)){
            if (!instructions.containsKey(title)) {
                storeInstruction(new Instruction(title, body, author)); // it's already established that author and educator are equal, is there any reason to use one over the other? 
            }
            else {
                //TODO - test this
                System.out.println("This title already exists"); //Could swap the if else to make it more readable?
            }
        }
        else {
            //TODO - test this
            System.out.println("Only the educator of this lesson can add instructions");
        }
    }

    private void storeInstruction(Instruction i) {
        instructions.put(i.getTitle(), i);
    }

    public Instruction getInstruction(String instructionTitle) {
        if (instructions.containsKey(instructionTitle)) { 
            return instructions.get(instructionTitle);
        } else {
            return null;
        }
    }

    public List<Instruction> getAllInstructions(){
        return new ArrayList<>(instructions.values());
    }   

    //TODO - how to edit and remove instructions when the storeInstruction is private, should these be too?

    public void removeAllInstructions(){
        instructions.clear();
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
