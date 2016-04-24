package net.lessqq.mc.ae2freq;

import gnu.trove.map.TObjectLongMap;
import gnu.trove.map.hash.TObjectLongHashMap;

import java.util.List;
import java.util.Random;

import mcp.mobius.waila.api.IWailaConfigHandler;
import mcp.mobius.waila.api.IWailaDataAccessor;
import mcp.mobius.waila.api.IWailaDataProvider;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;

import org.willden.wordmap.SmallDictionary;
import org.willden.wordmap.WordMapper;

import appeng.api.parts.IPart;
import appeng.integration.modules.waila.part.PartAccessor;
import appeng.integration.modules.waila.part.Tracer;
import appeng.parts.p2p.PartP2PTunnel;

import com.google.common.base.Optional;

/**
 * Contains code from appeng.integration.modules.Waila
 * 
 */

public class AE2DataProvider implements IWailaDataProvider {

	private static final long YEAR_2014 = 1388534400000l;
	private static final long YEAR_2030 = 1893456000000l;

	private static final String ID_FREQUENCY = "frequency";

	private WordMapper wordMapper = new WordMapper(new SmallDictionary());
	private TObjectLongMap<IPart> cache = new TObjectLongHashMap<IPart>();

	/**
	 * Can access parts through view-hits
	 */
	private final PartAccessor accessor = new PartAccessor();

	/**
	 * Traces views hit on blocks
	 */
	private final Tracer tracer = new Tracer();

	@Override
	public List<String> getWailaBody(ItemStack itemStack, List<String> currentToolTip, IWailaDataAccessor accessor,
			IWailaConfigHandler config) {
		final TileEntity te = accessor.getTileEntity();
		final MovingObjectPosition mop = accessor.getPosition();

		final Optional<IPart> maybePart = this.accessor.getMaybePart(te, mop);

		if (maybePart.isPresent()) {
			final IPart part = maybePart.get();
			if (part instanceof PartP2PTunnel<?>) {
				long freq = getFrequency(part, accessor.getNBTData());
				if (freq == 0) {
					currentToolTip.add(StatCollector.translateToLocal("AE2frequencyDisplay.frequency") + " "
							+ StatCollector.translateToLocal("AE2frequencyDisplay.unavailable"));
					currentToolTip.add(StatCollector.translateToLocal("AE2frequencyDisplay.unavailable.desc"));

				} else {
					if (config.getConfig(Constants.CFG_SHOW_FREQUENCY, true)) {
						currentToolTip.add(StatCollector.translateToLocal("AE2frequencyDisplay.frequency") + " "
								+ getNameForFreq(freq));
					}
					if (config.getConfig(Constants.CFG_SHOW_HEX, false)) {
						currentToolTip.add(StatCollector.translateToLocal("AE2frequencyDisplay.frequency") + " "
								+ getHexNameForFreq(freq));
					}
				}

			}
		}
		return currentToolTip;
	}

	/**
	 * Determines the frequency of the tunnel.
	 * <p/>
	 * If the client received information of the frequencies on the server, they
	 * are used, else if the cache contains a previous stored value, this will
	 * be used. Default value is 0.
	 * 
	 * @param part
	 *            part to be looked at
	 * @param tag
	 *            tag maybe containing the frequency information
	 * 
	 * @return used frequency on the tunnel
	 */
	private long getFrequency(IPart part, NBTTagCompound tag) {

		final long freq;
		if (tag.hasKey(ID_FREQUENCY)) {
			freq = tag.getLong(ID_FREQUENCY);
			this.cache.put(part, freq);
		} else if (this.cache.containsKey(part)) {
			freq = this.cache.get(part);
		} else {
			freq = 0;
		}

		return freq;
	}

	private String getNameForFreq(long freq) {
		if ((freq > YEAR_2014 && freq <= YEAR_2030)) {
			return wordMapper.intToWords(shuffleBits((int) ((freq - YEAR_2014) / 10)));
		}
		return getHexNameForFreq(freq);
	}

	private String getHexNameForFreq(long freq) {
		return String.format("%08X", freq - YEAR_2014);
	}

	private int shuffleBits(int freq) {
		return new Random(freq).nextInt(Integer.MAX_VALUE);
	}

	@Override
	public ItemStack getWailaStack(IWailaDataAccessor accessor, IWailaConfigHandler config) {
		return null;
	}

	@Override
	public List<String> getWailaHead(ItemStack itemStack, List<String> currenttip, IWailaDataAccessor accessor,
			IWailaConfigHandler config) {
		return currenttip;
	}

	@Override
	public List<String> getWailaTail(ItemStack itemStack, List<String> currenttip, IWailaDataAccessor accessor,
			IWailaConfigHandler config) {
		return currenttip;
	}

	@Override
	public NBTTagCompound getNBTData(EntityPlayerMP player, TileEntity te, NBTTagCompound tag, World world, int x,
			int y, int z) {
		final MovingObjectPosition mop = this.tracer.retraceBlock(world, player, x, y, z);

		if (mop != null) {
			final Optional<IPart> maybePart = this.accessor.getMaybePart(te, mop);

			if (maybePart.isPresent()) {
				final IPart part = maybePart.get();
				if (part instanceof PartP2PTunnel<?>) {
					tag.setLong(ID_FREQUENCY, ((PartP2PTunnel<?>) part).getFrequency());
				}
			}
		}

		return tag;
	}
}
