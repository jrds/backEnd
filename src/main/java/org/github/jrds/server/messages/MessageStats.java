package org.github.jrds.server.messages;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class MessageStats
{
    private final Map<String, UserStats> stats = new ConcurrentHashMap<>();

    void incrementSent(String userId)
    {
        synchronized (this)
        {
            if (!stats.containsKey(userId))
            {
                stats.put(userId, new UserStats(userId));
            }
        }
        stats.get(userId).messagesSent.incrementAndGet();
    }

    void incrementReceived(String userId)
    {
        synchronized (this)
        {
            if (!stats.containsKey(userId))
            {
                stats.put(userId, new UserStats(userId));
            }
        }
        stats.get(userId).messagesReceived.incrementAndGet();
    }

    public UserStats forUser(String userId)
    {
        return stats.get(userId);
    }

    public class UserStats
    {
        private String userId;
        private AtomicInteger messagesReceived = new AtomicInteger();
        private AtomicInteger messagesSent = new AtomicInteger();

        UserStats(String userId)
        {
            this.userId = userId;
        }

        public int getSent()
        {
            return messagesSent.get();
        }

        public int getReceived()
        {
            return messagesReceived.get();
        }
    }
}
