package com.NPCPoisonVenomTracker;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.util.HashSet;
import java.util.Set;

import javax.inject.Inject;

import net.runelite.api.Actor;
import net.runelite.api.NPC;
import net.runelite.api.Point;
import net.runelite.api.coords.LocalPoint;
import net.runelite.api.events.NpcDespawned;
import net.runelite.api.events.HitsplatApplied;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayPosition;

import static net.runelite.api.HitsplatID.POISON;
import static net.runelite.api.HitsplatID.VENOM;

public class NPCPoisonVenomTrackerOverlay extends Overlay
{
    private final Set<NPC> poisonedNpcs = new HashSet<>();

    @Inject
    public NPCPoisonVenomTrackerOverlay()
    {
        setPosition(OverlayPosition.DYNAMIC);
        setLayer(OverlayLayer.ABOVE_SCENE);
    }

    @Subscribe
    public void onNpcHit(HitsplatApplied event)
    {
        Actor actor = event.getActor();
        if (!(actor instanceof NPC))
        {
            return;
        }

        NPC npc = (NPC) actor;
        if (event.getHitsplat().getHitsplatType() == POISON || event.getHitsplat().getHitsplatType() == VENOM)
        {
            poisonedNpcs.add(npc);
        }
    }

    @Subscribe
    public void onNpcDespawned(NpcDespawned event)
    {
        poisonedNpcs.remove(event.getNpc());
    }

    @Override
    public Dimension render(Graphics2D graphics)
    {
        for (NPC npc : poisonedNpcs)
        {
            Point npcScreenLocation = npc.getCanvasTextLocation(graphics, npc.getName(), 0);

            if (npcScreenLocation != null)
            {
                graphics.setColor(Color.GREEN);
                LocalPoint canvasLocation = npc.getLocalLocation();
                int width = npc.getConvexHull().getBounds().width;
                int height = npc.getConvexHull().getBounds().height;
                graphics.drawRect(canvasLocation.getX(), canvasLocation.getY(), width, height);
            }
        }

        return null;
    }
}