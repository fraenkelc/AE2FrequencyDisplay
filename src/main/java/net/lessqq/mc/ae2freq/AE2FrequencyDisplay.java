package net.lessqq.mc.ae2freq;

import mcp.mobius.waila.api.IWailaRegistrar;
import appeng.tile.networking.TileCableBus;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLInterModComms;

@Mod(name = "AE2 Frequency Display", modid = Constants.MODID, version = Constants.VERSION, acceptedMinecraftVersions = "[1.7.10]", dependencies = AE2FrequencyDisplay.MOD_DEPENDENCIES, acceptableRemoteVersions = "*")
public class AE2FrequencyDisplay {

	public final static String MOD_DEPENDENCIES = "after:Waila;" + "after:appliedenergistics2";

	@EventHandler
	public void init(FMLInitializationEvent event) {
		FMLInterModComms.sendMessage("Waila", "register", AE2FrequencyDisplay.class.getName() + ".callbackRegister");
	}

	public static void callbackRegister(IWailaRegistrar registrar) {
		registrar.addConfig(Constants.MODID, Constants.CFG_SHOW_FREQUENCY);
		registrar.addConfig(Constants.MODID, Constants.CFG_SHOW_HEX);
		registerAE2(registrar);
	}

	private static void registerAE2(IWailaRegistrar registrar) {
		AE2DataProvider provider = new AE2DataProvider();
		registrar.registerBodyProvider(provider, TileCableBus.class);
		registrar.registerNBTProvider(provider, TileCableBus.class);
	}

}
