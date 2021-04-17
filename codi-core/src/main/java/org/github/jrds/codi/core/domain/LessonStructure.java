package org.github.jrds.codi.core.domain;


import java.util.*;

public class LessonStructure
{
    private final String id;
    private final User educator;
    private final Set<User> learners;
    private final List<Instruction> instructions = new ArrayList<>();

    public LessonStructure(String id, User educator, Set<User> learners)
    {
        this.id = Objects.requireNonNull(id);
        this.educator = Objects.requireNonNull(educator);
        this.learners = learners == null ? new HashSet<>() : learners;
    }

    public String getId()
    {
        return id;
    }

    public User getEducator()
    {
        return educator;
    }

    public Set<User> getLearners()
    {
        return Collections.unmodifiableSet(learners);
    }

    public void addLearner(User learner)
    { // could pass in other detail to construct user??
        learners.add(learner);
    }

    public boolean isRegisteredLearner(User learner)
    {
        return learners.contains(learner);
    }

    public boolean canConnect(User user)
    {
        return isRegisteredLearner(user) || educator.equals(user);
    }

    public Role getUserRole(User user)
    {
        if (educator.equals(user))
        {
            return Role.EDUCATOR;
        }
        else if (learners.contains(user))
        {
            return Role.LEARNER;
        }
        else
        {
            return Role.NONE;
        }
    }

    public Instruction createInstruction(String title, String body, User author)
    {
        if (author.equals(educator))
        {
            String id = UUID.randomUUID().toString();
            // it's already established that author and educator are equal, is there any reason to use one over the other?
            Instruction i = new Instruction(id, this, title, body, author);
            instructions.add(i);
            return i;
        }
        else
        {
            throw new IllegalArgumentException("Only the educator of this lesson can add instructions");
        }
    }

    public Instruction getInstruction(String id)
    {
        return instructions.stream().filter(i -> i.getId().equals(id)).findFirst().orElse(null);
    }

    public List<Instruction> getAllInstructions()
    {
        return Collections.unmodifiableList(instructions);
    }

    public void removeInstruction(Instruction instruction)
    {
        instructions.remove(instruction);
    }

    public void removeAllInstructions()
    {
        instructions.clear();
    }

    void moveUp(Instruction instruction)
    {
        int index = instructions.indexOf(instruction);
        if (index > 0)
        {
            instructions.set(index, instructions.get(index - 1));
            instructions.set(index - 1, instruction);
        }
    }

    void moveDown(Instruction instruction)
    {
        int index = instructions.indexOf(instruction);
        if (index < instructions.size() - 1 && index != -1)
        {
            instructions.set(index, instructions.get(index + 1));
            instructions.set(index + 1, instruction);
        }
    }

    @Override
    public String toString()
    {
        return "Lesson [Educator Name =" + educator.getName() + ", Lesson id=" + id + ", learners=" + learners + "]";
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
        LessonStructure lessonStructure = (LessonStructure) o;
        return id.equals(lessonStructure.id);
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(id);
    }
}
