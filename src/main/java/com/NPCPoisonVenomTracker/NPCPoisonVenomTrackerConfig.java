package com.NPCPoisonVenomTracker;

import java.awt.Color;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;

@ConfigGroup("npcpoisonvenomtracker")
public interface NPCPoisonVenomTrackerConfig extends Config
{
	@ConfigItem(
			position = 0,
			keyName = "highlightColor",
			name = "Highlight Color",
			description = "The color to use for highlighting NPCs inflicted with Poison/Venom"
	)
	default String highlightColor()
	{
		return "#C8FF00";
	}

	void setHighlightColor(String color);

	default Color getHighlightColor()
	{
		return Color.decode(highlightColor());
	}
}

