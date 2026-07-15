package com.ketheroth.slots.forge;

import com.ketheroth.slots.Slots;
import com.ketheroth.slots.common.command.SlotsCommand;
import com.ketheroth.slots.common.events.ServerEvents;
import com.ketheroth.slots.common.network.SyncPlayerDataPacket;
import com.ketheroth.slots.common.networking.SlotsPacketHandler;
import com.ketheroth.slots.common.world.SlotsSavedData;
import com.mojang.serialization.Codec;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.loot.IGlobalLootModifier;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

@Mod(Slots.MOD_ID)
@Mod.EventBusSubscriber
public class SlotsForge {

	public static final DeferredRegister<Codec<? extends IGlobalLootModifier>> GLOBAL_LOOT_MODIFIER_SERIALIZER = DeferredRegister.create(ForgeRegistries.Keys.GLOBAL_LOOT_MODIFIER_SERIALIZERS, Slots.MOD_ID);


	public SlotsForge() {
    Slots.init();
    IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();

    bus.addListener(SlotsForge::onCommonSetup);

    }

	public static void onCommonSetup(FMLCommonSetupEvent event) {
		event.enqueueWork(SlotsPacketHandler::init);
	}

	@SubscribeEvent
	public static void onPlayerLoggedIn(PlayerEvent.PlayerLoggedInEvent event) {
		if (event.getEntity() instanceof ServerPlayer serverPlayer) {
			SlotsSavedData.PlayerData playerData = SlotsSavedData.getPlayerUnlockedSlots(serverPlayer);
			SlotsPacketHandler.INSTANCE.sendTo(new SyncPlayerDataPacket(playerData), serverPlayer.connection.connection, NetworkDirection.PLAY_TO_CLIENT);
		}
	}

	@SubscribeEvent
	public static void onPlayerDeath(LivingDeathEvent event) {
		ServerEvents.onPlayerDeath(event.getEntity(), event.getSource());
	}

	private static void addOrDropItems(Player player, ItemStack stack) {
		if (!player.getInventory().add(stack)) {
			// can't add to player inventory, drop the item
			ItemEntity itementity = new ItemEntity(player.level(), player.getX(), player.getEyeY(), player.getZ(), stack);
			itementity.setPickUpDelay(40);
			itementity.setThrower(player.getUUID());
			player.level().addFreshEntity(itementity);
		}
	}
	@SubscribeEvent
    public static void onRegisterCommands(RegisterCommandsEvent event) {
        SlotsCommand.register(
            event.getDispatcher(),
            event.getBuildContext()
        );
    }

}
