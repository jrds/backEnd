package org.github.jrds.codi.core.messages;

import java.util.ArrayList;
import java.util.List;
import java.util.ServiceLoader;

public class MessagingContext
{
    public static MessageStats messageStats;
    public static List<MessagingExtension> messagingExtensions;

    static {
        reset();
    }

    public static void reset()
    {
        messageStats = new MessageStats();
        messagingExtensions = new ArrayList<>();
        for (MessagingExtension extension : ServiceLoader.load(MessagingExtension.class))
        {
            messagingExtensions.add(extension);
        }
    }
}
