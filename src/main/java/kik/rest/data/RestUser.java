package kik.rest.data;

import java.util.UUID;

import kik.user.data.user.User;

/**
 * {@link RestUser} will be used by spring to create a json string.
 *
 * @author Felix Rülke
 * @version 0.0.1
 */
class RestUser {
    String name;
    String type;
    UUID uuid;

    /**
     * Default constructor of {@link RestUser}
     * 
     * @param user The {@link User} to parse
     */
    public RestUser(User user) {
        this.name = user.getName();
        this.type = user.getUserType().getName();
        this.uuid = user.getUuid();
    }

    /**
     * Getter for the name
     * 
     * @return the name
     */
    public String getName() {
        return this.name;
    }

    /**
     * Getter for the type
     * 
     * @return the type
     */
    public String getType() {
        return this.type;
    }

    public UUID getUuid() {
        return this.uuid;
    }
}