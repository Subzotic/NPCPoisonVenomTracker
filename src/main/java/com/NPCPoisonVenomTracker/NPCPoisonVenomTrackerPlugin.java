package com.NPCPoisonVenomTracker;

import com.google.inject.Provides;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Actor;
import net.runelite.api.HitsplatID;
import net.runelite.api.NPC;
import net.runelite.api.events.*;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.game.NPCManager;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.overlay.OverlayManager;

import javax.inject.Inject;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@PluginDescriptor(
		name = "NPCPoisonVenomTracker",
		description = "Tracks NPCs that have been inflicted by Poison or Venom",
		tags = {"overlay", "highlight", "NPC", "poison", "venom"}
)
public class NPCPoisonVenomTrackerPlugin extends Plugin
{
	@Getter
	private final Map<NPC, InflictedNPC> inflictedNPCs = new HashMap();
	@Inject
	private OverlayManager overlayManager;
	@Inject
	private NPCPoisonVenomTrackerOverlay overlay;
	@Inject
	private NPCManager npcManager;
	@Getter
	private long lastGameTick;

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

	@Subscribe
	public void onClientTick(ClientTick event)
	{
		inflictedNPCs.entrySet().removeIf(e -> e.getValue().hasExpired()); // Clear expired inflicted NPCs
	}

	@Subscribe
	public void onGameTick(GameTick event)
	{
		inflictedNPCs.values().forEach(InflictedNPC::processTick); // Process game tick count for each inflicted NPC
		lastGameTick = Instant.now().toEpochMilli(); // Used for time remaining in overlay
	}

	@Subscribe
	public void onHitsplatApplied(HitsplatApplied event)
	{
		Actor actor = event.getActor();
		if (!(actor instanceof NPC)) // Ignored non-NPC actors
		{
			return;
		}

		NPC npc = (NPC) actor;
		if (npc.isDead()) // Ignore dead NPCs
		{
			return;
		}

		int damage = event.getHitsplat().getAmount();
		int hitsplatType = event.getHitsplat().getHitsplatType();

		if (hitsplatType != HitsplatID.POISON && hitsplatType != HitsplatID.VENOM) // Ignore irrelevant hitsplat types
		{
			return;
		}

		if (inflictedNPCs.containsKey(npc))
		{
			InflictedNPC existing = inflictedNPCs.get(npc);
			if (existing.getHitsplatType() != hitsplatType)
			{
				inflictedNPCs.remove(npc); // Allows venom to override poison
			}
			else
			{
				existing.processHitsplat(damage);
				return;
			}
		}

		if (hitsplatType == HitsplatID.POISON)
		{
			inflictedNPCs.put(npc, new PoisonedNPC(npc, damage));
		}
		else // Venom
		{
			inflictedNPCs.put(npc, new VenomedNPC(npc));
		}
	}

	@Subscribe
	public void onNpcDespawned(NpcDespawned event)
	{
		inflictedNPCs.remove(event.getNpc()); // Remove de-spawned inflicted NPCs from map
	}

	@Subscribe
	public void onActorDeath(ActorDeath event) // Remove dead inflicted NPCs from map
	{
		if (!(event.getActor() instanceof NPC))
		{
			return;
		}

		inflictedNPCs.remove(event.getActor());
	}

	@Provides
	NPCPoisonVenomTrackerConfig provideConfig(ConfigManager configManager)
	{
		return configManager.getConfig(NPCPoisonVenomTrackerConfig.class);
	}

}