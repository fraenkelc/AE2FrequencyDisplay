package net.lessqq.mc.ae2freq;

import java.util.List;

import mcp.mobius.waila.api.IWailaConfigHandler;
import mcp.mobius.waila.api.IWailaDataAccessor;
import mcp.mobius.waila.api.IWailaDataProvider;
import mcp.mobius.waila.api.IWailaFMPAccessor;
import mcp.mobius.waila.api.IWailaFMPProvider;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.StatCollector;
import net.minecraft.util.Vec3;
import net.minecraftforge.common.util.ForgeDirection;
import appeng.api.parts.IPartHost;
import appeng.api.parts.SelectedPart;
import appeng.parts.p2p.PartP2PTunnel;

/**
 * Contains code from appeng.integration.modules.Waila
 * 
 * @author Christian
 * 
 */

public class AE2DataProvider implements IWailaFMPProvider, IWailaDataProvider {

	private static final long YEAR_2014 = 1388534400000l;

	@Override
	public List<String> getWailaHead(ItemStack itemStack,
			List<String> currenttip, IWailaFMPAccessor accessor,
			IWailaConfigHandler config) {
		return currenttip;
	}

	@Override
	public List<String> getWailaBody(ItemStack itemStack,
			List<String> currentToolTip, IWailaDataAccessor accessor,
			IWailaConfigHandler config) {
		TileEntity te = accessor.getTileEntity();
		MovingObjectPosition mop = accessor.getPosition();

		NBTTagCompound nbt = null;

		try {
			nbt = accessor.getNBTData();
		} catch (NullPointerException ignored) {
		}

		return this.getBody(itemStack, currentToolTip, accessor.getPlayer(),
				nbt, te, mop, config);
	}

	@Override
	public List<String> getWailaBody(ItemStack itemStack,
			List<String> currentToolTip, IWailaFMPAccessor accessor,
			IWailaConfigHandler config) {
		TileEntity te = accessor.getTileEntity();
		MovingObjectPosition mop = accessor.getPosition();

		NBTTagCompound nbt = null;

		try {
			nbt = accessor.getNBTData();
		} catch (NullPointerException ignored) {
		}

		return this.getBody(itemStack, currentToolTip, accessor.getPlayer(),
				nbt, te, mop, config);
	}

	public List<String> getBody(ItemStack itemStack,
			List<String> currentToolTip, EntityPlayer player,
			NBTTagCompound nbt, TileEntity te, MovingObjectPosition mop,
			IWailaConfigHandler config) {

		Object ThingOfInterest = te;
		ForgeDirection side = ForgeDirection.UNKNOWN;

		if (te instanceof IPartHost) {
			Vec3 Pos = mop.hitVec.addVector(-mop.blockX, -mop.blockY,
					-mop.blockZ);
			SelectedPart sp = ((IPartHost) te).selectPart(Pos);
			if (sp.facade != null) {
				ThingOfInterest = sp.facade;
			} else if (sp.part != null) {
				ThingOfInterest = sp.part;
			}
			side = sp.side;
		}

		if (ThingOfInterest instanceof PartP2PTunnel<?>) {

			if (nbt.hasKey("extra:" + side.ordinal())) {
				NBTTagCompound extra = nbt.getCompoundTag("extra:"
						+ side.ordinal());
				if (extra != null && extra.hasKey("freq")) {
					long freq = extra.getLong("freq");
					if (config.getConfig(Constants.CFG_SHOW_HEX, false)) {
						currentToolTip
								.add(StatCollector
										.translateToLocal("AE2frequencyDisplay.frequency")
										+ " " + getHexNameForFreq(freq));
					}
				}
			}
		}

		return currentToolTip;
	}

	private String getHexNameForFreq(long freq) {
		return String.format("%08X", freq - YEAR_2014);
	}

	@Override
	public List<String> getWailaTail(ItemStack itemStack,
			List<String> currenttip, IWailaFMPAccessor accessor,
			IWailaConfigHandler config) {
		return currenttip;
	}

	@Override
	public ItemStack getWailaStack(IWailaDataAccessor accessor,
			IWailaConfigHandler config) {
		return null;
	}

	@Override
	public List<String> getWailaHead(ItemStack itemStack,
			List<String> currenttip, IWailaDataAccessor accessor,
			IWailaConfigHandler config) {
		return currenttip;
	}

	@Override
	public List<String> getWailaTail(ItemStack itemStack,
			List<String> currenttip, IWailaDataAccessor accessor,
			IWailaConfigHandler config) {
		return currenttip;
	}
}
