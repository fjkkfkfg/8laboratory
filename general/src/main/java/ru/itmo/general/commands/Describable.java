package ru.itmo.general.commands;

/**
 * Interface for objects that can be named and described.
 *
 */
public interface Describable {
    /**
     * Gets the name of the object.
     *
     * @return the name of the object
     */
    String getName();

    /**
     * Gets the description of the object.
     *
     * @return the description of the object
     */
    String getDescription();
}

