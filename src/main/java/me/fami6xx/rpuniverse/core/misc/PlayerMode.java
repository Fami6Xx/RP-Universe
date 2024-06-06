package me.fami6xx.rpuniverse.core.misc;

/**
 * The PlayerMode enum is used to determine the mode of the player
 * @author Fami6xx
 * @version 1.0
 */
public enum PlayerMode {
    USER("user"),
    MODERATOR("moderator"),
    ADMIN("admin");

    private final String mode;

    PlayerMode(String mode){
        this.mode = mode;
    }

    /**
     * Get the string of the PlayerMode
     * @return The string of the PlayerMode
     */
    public String getMode(){
        return mode;
    }

    /**
     * Get the PlayerMode from a string
     * @param mode The string to get the PlayerMode from
     * @return The PlayerMode if found, null otherwise
     */
    public static PlayerMode getModeFromString(String mode){
        for(PlayerMode playerMode : PlayerMode.values()){
            if(playerMode.getMode().equalsIgnoreCase(mode)) return playerMode;
        }
        return null;
    }
}
