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
			keyName = "poisonHighlightColor",
			name = "Poison Highlight Color",
			description = "The color to use for highlighting NPCs inflicted with Poison"
	)
	default Color poisonHighlightColor()
	{
		return new Color(200, 255, 0); //aka #C8FF00
	}

	@ConfigItem(
			position = 1,
			keyName = "venomHighlightColor",
			name = "Venom Highlight Color",
			description = "The color to use for highlighting NPCs inflicted with Venom"
	)
	default Color venomHighlightColor()
	{
		return new Color(0, 255, 111);
	}

	@ConfigItem(
			position = 2,
			keyName = "textColor",
			name = "Text Color",
			description = "The color to use for text above inflicted NPCs"
	)
	default Color textColor()
	{
		return new Color(255, 255, 255);
	}

	@ConfigItem(
			position = 3,
			keyName = "showTicksAsTime",
			name = "Show as time remaining",
			description = "Replaces ticks with time remaining"
	)
	default boolean showTicksAsTime()
	{
		return true;
	}
}

