package org.github.jrds.codi.core.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.github.jrds.codi.core.domain.User;

import java.util.Objects;

public class UserDto
{

    private final String id;
    private final String name;


    @JsonCreator
    public UserDto(@JsonProperty("id") String id,
                   @JsonProperty("name") String name)
    {
        this.id = Objects.requireNonNull(id);
        this.name = Objects.requireNonNull(name);
    }

    public UserDto(User user)
    {
        this(user.getId(), user.getName());
    }

    public String getId()
    {
        return id;
    }

    public String getName()
    {
        return name;
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
        UserDto userDto = (UserDto) o;
        return id.equals(userDto.id);
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(id);
    }
}