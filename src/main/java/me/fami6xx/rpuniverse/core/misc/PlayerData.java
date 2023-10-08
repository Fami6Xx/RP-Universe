package me.fami6xx.rpuniverse.core.misc;

import org.bukkit.entity.Player;

/**
 * This class is used to store player data. You can extend and add fields to this class, but be aware that the added fields might not be saved depending on the selected Data Handler.
 * <p>
 * It is serialized and deserialized by the selected Data Handler.
 * <p>
 * To ignore a field from being serialized, use the transient keyword.
 */
public class PlayerData {
    transient Player bindedPlayer;

    // Some testing data
    public int testInt = 0;
    public String testString = "Hello World!";
    private transient String testTransientString = "This string is transient and will not be saved.";
    private int hunger = 20;
    private int health = 20;

}
