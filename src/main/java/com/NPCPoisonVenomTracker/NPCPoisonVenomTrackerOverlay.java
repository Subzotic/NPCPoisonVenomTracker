package com.NPCPoisonVenomTracker;

import java.awt.*;
import java.time.Instant;

import net.runelite.api.*;
import net.runelite.api.Point;
import net.runelite.api.coords.LocalPoint;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.OverlayUtil;

import javax.inject.Inject;

public class NPCPoisonVenomTrackerOverlay extends Overlay
{
    @Inject
    private NPCPoisonVenomTrackerPlugin plugin;
    @Inject
    private Client client;
    @Inject
    private NPCPoisonVenomTrackerConfig config;

    public NPCPoisonVenomTrackerOverlay()
    {
        setPosition(OverlayPosition.DYNAMIC);
        setLayer(OverlayLayer.ABOVE_SCENE);
    }

    @Override
    public Dimension render(Graphics2D graphics)
    {
        final int textHeight = 25;
        final float borderWidth = 2.0f;
        final Color hullColorPoison = config.poisonHighlightColor();
        final Color hullColorVenom = config.venomHighlightColor();
        final Color textColor = config.textColor();

        for (InflictedNPC iNpc : plugin.getInflictedNPCs().values()) // For each inflicted NPC
        {
            NPC npc = iNpc.getNPC();
            if (npc.isDead())
            {
                continue;
            }

            float secSinceLastTick = (Instant.now().toEpochMilli() - plugin.getLastGameTick()) / 1000.0f;
            int nextDamage = iNpc.getNextDamage();
            int ticksUntilHit = iNpc.getTicksUntilNextHit();
            int ticksRemaining = iNpc.getTicksRemaining();
            int height = npc.getLogicalHeight() + textHeight;
            int secondsUntilHit = (int)Math.ceil(ticksUntilHit * 0.6f - secSinceLastTick);
            int secondsRemaining = (int)Math.ceil(ticksRemaining * 0.6f - secSinceLastTick);

            LocalPoint localLocation = npc.getLocalLocation();
            Point npcPoint = Perspective.localToCanvas(client, localLocation, client.getPlane(), height);

            String ticksUntilHitS = ticksUntilHit <= 0 ? "?" : config.showTicksAsTime()
                    ? String.format("%02d:%02d", (secondsUntilHit / 60) % 60, secondsUntilHit % 60)
                    : Integer.toString(ticksUntilHit);
            String ticksRemainingS = ticksRemaining == -1 ? "N/A" : config.showTicksAsTime()
                    ? String.format("%02d:%02d", (secondsRemaining / 60) % 60, secondsRemaining % 60)
                    : Integer.toString(ticksRemaining);

            String text = String.format("%s | %s | %s", nextDamage, ticksUntilHitS, ticksRemainingS);
            // Next_Damage | Ticks_Until_Hit | Ticks_Remaining

            // Draw hull around NPC
            graphics.setColor(iNpc.getHitsplatType() == HitsplatID.POISON ? hullColorPoison : hullColorVenom);
            graphics.setStroke(new BasicStroke(borderWidth));
            graphics.draw(npc.getConvexHull());

            // Draw text above NPC
            OverlayUtil.renderTextLocation(graphics, npcPoint, text, textColor);
        }

        return null;
    }

}