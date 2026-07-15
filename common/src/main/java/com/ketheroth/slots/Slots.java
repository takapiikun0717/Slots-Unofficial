package com.ketheroth.slots;

import com.ketheroth.slots.common.config.SlotsConfig;
import com.ketheroth.slots.common.registry.ModMenus;
import com.teamresourceful.resourcefulconfig.common.config.Configurator;

public class Slots {

	public static final String MOD_ID = "slots_unofficial";
	public static final Configurator CONFIGURATOR = new Configurator();

	public static void init() {
		CONFIGURATOR.registerConfig(SlotsConfig.class);

		ModMenus.MENUS.init();
	}

}
