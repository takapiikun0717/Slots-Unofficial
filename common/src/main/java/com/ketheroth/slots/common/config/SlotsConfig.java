package com.ketheroth.slots.common.config;

import com.ketheroth.slots.Slots;
import com.teamresourceful.resourcefulconfig.common.annotations.Comment;
import com.teamresourceful.resourcefulconfig.common.annotations.Config;
import com.teamresourceful.resourcefulconfig.common.annotations.ConfigEntry;
import com.teamresourceful.resourcefulconfig.common.config.EntryType;


@Config(Slots.MOD_ID)
public final class SlotsConfig {

	@ConfigEntry(id = "level_per_slot", type = EntryType.INTEGER, translation = "slots.config.level_per_slot")
	@Comment("Amount of xp level needed to obtain 1 slot")
	public static int levelPerSlot = 3;

	@ConfigEntry(id = "generate_item_reward", type = EntryType.BOOLEAN, translation = "slots.config.generate_item_reward")
	@Comment("Determine if slot reward items are generated in chests")
	public static boolean generateItemReward = false;


	@ConfigEntry(id = "preserve_rewards_on_death", type = EntryType.BOOLEAN, translation = "slots.config.preserve_rewards_on_death")
	@Comment("Determine if slot reward items are preserved when you die")
	public static boolean preserveRewardsOnDeath = true;

    @ConfigEntry(id = "max_unlocked_slots", type = EntryType.INTEGER, translation = "slots.config.max_unlocked_slots")
    @Comment("Maximum number of slots players can unlock. Valid range: 1-54.")
    public static int maxUnlockedSlots = 54;    
	public static int getMaxUnlockedSlots() {

      return Math.max(1, Math.min(54, maxUnlockedSlots));

    }
}
