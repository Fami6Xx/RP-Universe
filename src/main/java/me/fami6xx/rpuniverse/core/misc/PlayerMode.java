package me.fami6xx.rpuniverse.core.misc;

public enum PlayerMode {
    USER("user"),
    MODERATOR("moderator"),
    ADMIN("admin");

    private final String mode;

    PlayerMode(String mode){
        this.mode = mode;
    }

    public String getMode(){
        return mode;
    }

    public static PlayerMode getModeFromString(String mode){
        for(PlayerMode playerMode : PlayerMode.values()){
            if(playerMode.getMode().equalsIgnoreCase(mode)) return playerMode;
        }
        return null;
    }
}
