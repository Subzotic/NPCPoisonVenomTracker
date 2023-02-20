package com.NPCPoisonVenomTracker;

import lombok.Getter;
import net.runelite.api.HitsplatID;
import net.runelite.api.NPC;

@Getter
public class PoisonedNPC implements InflictedNPC
{
    // Information sourced from: https://oldschool.runescape.wiki/w/Poison and https://oldschoolrunescape.fandom.com/wiki/Poison

    // Starts at a random damage value dependent on source
    // Poison applies every 18 seconds (30 ticks)
    // Every 4* hits, the poison damage value decreases by 1 (* wiki says 5 hits, fandom says 4 hits)
    // The poison timer is reset if poison is successfully applied again while poisoned (damage is also reset)
    // 'Some NPCs with their own timers for actions like speaking can't be poisoned' (untested, no logic to handle this yet)

    private final int TICKS_PER_HIT = 30;
    private final int HITS_PER_DAMAGE_CHANGE = 5;
    private final int TICKS_PER_HP_REGEN = 60;
    private int lastDamage;
    private int nextDamage;
    private int hitsSinceDamageChange;
    private int ticksUntilNextHit;
    private final NPC NPC;

    public PoisonedNPC(NPC npc, int damage)
    {
        this.NPC = npc;
        this.lastDamage = damage;
        this.ticksUntilNextHit = TICKS_PER_HIT + 1; // Start offset by 1 tick
        this.hitsSinceDamageChange = 0;
        updateNextDamage();
    }

    public void processTick() // We process ticks for the timer overlay
    {
        ticksUntilNextHit--;
    }

    public void processHitsplat(int damage) // Unlike with Venom, we need to process each hitsplat manually just in case poison has been re-applied (which would reset the internal timer and damage)
    {
        ticksUntilNextHit = TICKS_PER_HIT + 1; // Reset, offset by 1 tick

        if (damage != nextDamage) // Damage mismatch - Poison has been re-applied, reset damage and hit counter
        {
            lastDamage = damage;
            hitsSinceDamageChange = 0;
        }
        else // Normal poison damage tick
        {
            hitsSinceDamageChange++;
        }

        if (hitsSinceDamageChange == HITS_PER_DAMAGE_CHANGE)
        {
            lastDamage--;
            hitsSinceDamageChange = 0;
        }

        updateNextDamage();
    }

    public int getTicksRemaining()
    {
        return ticksUntilNextHit + getHitsRemaining() * TICKS_PER_HIT;
    }

    public int getHitsRemaining()
    {
        return (HITS_PER_DAMAGE_CHANGE - hitsSinceDamageChange) + ((lastDamage - 1) * HITS_PER_DAMAGE_CHANGE);
    }

    public int getHitsplatType()
    {
        return HitsplatID.POISON;
    }

    @Override
    public boolean hasExpired()
    {
        return nextDamage == -1;
    }

    private void updateNextDamage() // Updates the next hit's predicted damage value
    {
        if (hitsSinceDamageChange + 1 == HITS_PER_DAMAGE_CHANGE)
        {
            nextDamage = lastDamage - 1;
        }
        else if (lastDamage <= 0)
        {
            nextDamage = -1;
        }
        else
        {
            nextDamage = lastDamage;
        }
    }

}
