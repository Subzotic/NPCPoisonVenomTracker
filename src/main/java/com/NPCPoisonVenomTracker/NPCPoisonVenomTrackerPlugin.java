package com.NPCPoisonVenomTracker;

import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.overlay.OverlayManager;

import javax.inject.Inject;

@PluginDescriptor(
		name = "NPC Poison/Venom Tracker",
		description = "Tracks NPCs that have been inflicted by Poison or Venom",
		tags = {"overlay", "highlight", "NPC", "poison", "venom"}
)
public class NPCPoisonVenomTrackerPlugin extends Plugin
{
	@Inject
	private OverlayManager overlayManager;

	@Inject
	private NPCPoisonVenomTrackerOverlay overlay;

	@Override
	protected void startUp() throws Exception
	{
		overlayManager.add(overlay);
	}

	@Override
	protected void shutDown() throws Exception
	{
		overlayManager.remove(overlay);
	}
}