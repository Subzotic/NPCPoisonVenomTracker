package com.NPCPoisonVenomTracker;


import net.runelite.api.NPC;

public interface InflictedNPC
{
    NPC getNPC();
    void processTick();
    void processHitsplat(int damage);
    int getNextDamage();
    int getTicksUntilNextHit();
    int getTicksRemaining();
    int getHitsRemaining();
    int getHitsplatType();
    boolean hasExpired();
}
