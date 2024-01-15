package me.fami6xx.rpuniverse.core.jobs.types;

public interface JobType {
    /**
     * Retrieves the name of the job type.
     *
     * @return the name of the job type as a String.
     */
    String getName();

    /**
     * Retrieves the description of the job type.
     *
     * @return the description of the job type as a String.
     */
    String getDescription();

    /**
     * Opens the main menu.
     * <p>
     * This method is responsible for opening the main menu. It provides access to various
     * functionality and options available to the user.
     * <p>
     * Usage:
     * In order to use this method, simply call it without any parameters. The main menu will then be displayed
     * to the user.
     * <p>
     * Example:
     * openMainMenu();
     */
    void openMainMenu();

    /**
     * Returns a string representation of the object.
     * <p>
     * The toString method returns a string representation of the object. The returned string should be a concise
     * and human-readable representation that can be used for debugging or logging purposes.
     * <p>
     * IT IS CRUCIAL THAT YOU USE GSON TO SERIALIZE THE OBJECT TO A STRING.
     *
     * @return a String representation of the object.
     */
    String toString();

    /**
     * Converts a string representation of a JobType to an actual JobType object.
     *
     * @param string the string representation of the JobType to convert
     * @return the JobType object
     */
    JobType fromString(String string);
}
