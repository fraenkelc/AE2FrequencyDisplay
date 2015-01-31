package net.lessqq.mc.ae2freq;

import mcp.mobius.waila.api.IWailaRegistrar;
import net.minecraft.block.Block;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLInterModComms;
import cpw.mods.fml.common.registry.GameRegistry;

@Mod(modid = Constants.MODID, version = Constants.VERSION, acceptedMinecraftVersions = "[1.7.10]", dependencies = AE2FrequencyDisplay.MOD_DEPENDENCIES)
public class AE2FrequencyDisplay {

	public final static String MOD_DEPENDENCIES = "after:Waila;"
			+ "after:appliedenergistics2;" +
			// depend on version of forge used for build.
			"required-after:Forge@[" // require forge.
			+ net.minecraftforge.common.ForgeVersion.majorVersion + '.' // majorVersion
			+ net.minecraftforge.common.ForgeVersion.minorVersion + '.' // minorVersion
			+ net.minecraftforge.common.ForgeVersion.revisionVersion + '.' // revisionVersion
			+ net.minecraftforge.common.ForgeVersion.buildVersion + ",)"; // buildVersion

	@EventHandler
	public void init(FMLInitializationEvent event) {
		FMLInterModComms.sendMessage("Waila", "register",
				AE2FrequencyDisplay.class.getName() + ".callbackRegister");
	}

	public static void callbackRegister(IWailaRegistrar registrar) {
		registrar.addConfig(Constants.MODID, Constants.CFG_SHOW_HEX);
		registerAE2(registrar);
	}

	private static void registerAE2(IWailaRegistrar registrar) {
		Block cableBus = GameRegistry.findBlock("appliedenergistics2",
				"tile.BlockCableBus");
		if (cableBus != null) {
			AE2DataProvider provider = new AE2DataProvider();
			Class<? extends Block> clazz = cableBus.getClass();
			registrar.registerBodyProvider(provider, clazz);
			registrar.registerBodyProvider(provider, "ae2_cablebus");
			registrar.registerSyncedNBTKey("*", clazz);

			// registrar.registerNBTProvider(provider, clazz);

		}

	}

}
