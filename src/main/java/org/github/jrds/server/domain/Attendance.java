package org.github.jrds.server.domain;

import org.github.jrds.server.extensions.chat.ChatMessage;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Attendance
{

    private final User user;
    private final LessonStructure lessonStructure;
    private final Role role;
    private List<ChatMessage> chatHistory;
    private Code code;

    public Attendance(User user, LessonStructure lessonStructure)
    {
        this.user = Objects.requireNonNull(user, "Invalid user");
        this.lessonStructure = Objects.requireNonNull(lessonStructure, "Invalid lesson");
        this.role = lessonStructure.getUserRole(user);
        this.chatHistory = new ArrayList<>();
        this.code = new Code();

        if (this.role == Role.NONE)
        {
            throw new IllegalArgumentException("User cannot attend this lesson");
        }
        // TODO - Make tests for role.

    }

    public Code getCode()
    {
        return code;
    }

    public User getUser()
    {
        return user;
    }

    public LessonStructure getLesson()
    {
        return lessonStructure;
    }

    public Role getRole()
    {
        return role;
    }

    public List<ChatMessage> getChatHistory(){
        return chatHistory;
    }

    public List<ChatMessage> addMessageToChatHistory(ChatMessage newMessage){
        chatHistory.add(newMessage);
        return chatHistory;
    }

//    public void setCode(String code)
//    {
//        this.code = code;
//    }

//    public void compileCode()
//    {
//        long t0 = System.currentTimeMillis();
//        Matcher matcher = CLASS.matcher(code);
//        if (matcher.matches())
//        {
//            try
//            {
//                Path file = codeDirectory.resolve(matcher.group(1) + ".java");
//                Files.writeString(file, code);
//                // Path to javac should be an application configuration
//                Process compile = new ProcessBuilder().command("c:\\Program Files\\AdoptOpenJDK\\jdk-11.0.9.101-hotspot\\bin\\javac", file.toString()).directory(codeDirectory.toFile()).start();
//                int compileResult = compile.waitFor();
//                if (compileResult == 0)
//                {
//                    System.out.println("Compile worked");
//                    Process execute = new ProcessBuilder().command("c:\\Program Files\\AdoptOpenJDK\\jdk-11.0.9.101-hotspot\\bin\\java", matcher.group(1)).directory(codeDirectory.toFile()).start();
//                    int executeResult = compile.waitFor();
//                    if (executeResult == 0)
//                    {
//                        System.out.println("Execute worked");
//                        String stdOut = new BufferedReader(new InputStreamReader(execute.getInputStream()))
//                                .lines()
//                                .collect(Collectors.joining("\n"));
//                        System.out.println(stdOut);
//                    }
//                    else
//                    {
//                        System.out.println("Execute failed");
//                        String stdErr = new BufferedReader(new InputStreamReader(compile.getErrorStream()))
//                                .lines()
//                                .collect(Collectors.joining("\n"));
//                        System.out.println(stdErr);
//                    }
//                }
//                else
//                {
//                    System.out.println("Compile failed");
//                    String stdErr = new BufferedReader(new InputStreamReader(compile.getErrorStream()))
//                            .lines()
//                            .collect(Collectors.joining("\n"));
//                    System.out.println(stdErr);
//                }
//            }
//            catch (IOException e)
//            {
//                throw new RuntimeException(e);
//            }
//            catch (InterruptedException e)
//            {
//                throw new RuntimeException(e);
//            }
//        }
//        else
//        {
//            throw new IllegalArgumentException("Not a valid class definition");
//        }
//        System.out.println("Time taken: " + (System.currentTimeMillis() - t0));
//    }

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
        Attendance that = (Attendance) o;
        return user.equals(that.user) && lessonStructure.equals(that.lessonStructure);
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(user, lessonStructure);
    }
}
