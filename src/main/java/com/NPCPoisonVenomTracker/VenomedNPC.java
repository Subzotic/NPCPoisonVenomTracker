package com.NPCPoisonVenomTracker;

import lombok.Getter;
import net.runelite.api.HitsplatID;
import net.runelite.api.NPC;

@Getter
public class VenomedNPC implements InflictedNPC
{
    // Information sourced from: https://oldschool.runescape.wiki/w/Venom

    // Starts at damage value of 6
    // Venom applies every 18 seconds (30 ticks)
    // Every hit, the venom damage value increases by 2
    // Damage caps at 20
    // Unlike poison, the timer is not reset when applying venom again with another attack

    private final int TICKS_PER_HIT = 30;
    private final int INITIAL_DAMAGE = 6;
    private final int DAMAGE_INCREASE_PER_HIT = 2;
    private final int MAX_DAMAGE_PER_HIT = 20;
    private int lastDamage;
    private int nextDamage;
    private int ticksUntilNextHit;
    private final NPC NPC;

    public VenomedNPC(NPC npc)
    {
        this.NPC = npc;
        this.lastDamage = INITIAL_DAMAGE;
        this.ticksUntilNextHit = TICKS_PER_HIT + 1;
        updateNextDamage();
    }

    public void processTick()
    {
        ticksUntilNextHit--;
    }

    // We technically don't need the below method for Venom, we could process the damage increase in the above method only
    // but if the client hangs for a few frames (or when debugging) it can sometimes de-sync the timer, this solves that issue by processing it manually instead.

    public void processHitsplat(int damage)
    {
        lastDamage = damage;
        ticksUntilNextHit = TICKS_PER_HIT + 1; // Offset by 1 tick because next hitsplat occurs 1 tick after damage calculation

        updateNextDamage();
    }

    public int getTicksRemaining() // Infinite until cured
    {
        return -1;
    }

    public int getHitsRemaining() // Infinite until cured
    {
        return -1;
    }

    public int getHitsplatType()
    {
        return HitsplatID.VENOM;
    }

    @Override
    public boolean hasExpired() // Venom can't expire with time, only when the target is dead or the venom is cured
    {
        return false;
    }

    private void updateNextDamage()
    {
        nextDamage = Math.min(lastDamage + DAMAGE_INCREASE_PER_HIT, MAX_DAMAGE_PER_HIT);
    }

}
