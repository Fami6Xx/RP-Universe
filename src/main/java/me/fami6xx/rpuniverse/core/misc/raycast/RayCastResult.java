package me.fami6xx.rpuniverse.core.misc.raycast;

import org.bukkit.block.Block;

public class RayCastResult {
    private final boolean hit;
    private final boolean foundEndLocation;
    private final Block hitBlock;

    public RayCastResult(boolean hit, Block hitBlock, boolean foundEndLocation){
        this.hit = hit;
        this.hitBlock = hitBlock;
        this.foundEndLocation = foundEndLocation;
    }

    public boolean hasHit() {
        return hit;
    }

    public boolean hasFoundEndLocation() {
        return foundEndLocation;
    }

    public Block getHitBlock() {
        return hitBlock;
    }
}
