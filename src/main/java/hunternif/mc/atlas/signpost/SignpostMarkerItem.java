package hunternif.mc.atlas.signpost;

import java.util.List;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.StatCollector;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

/**
 * Admin tool for naming signposts. Right-click an unnamed signpost with it
 * and the label will be visible in all atlases. Not craftable.
 */
public class SignpostMarkerItem extends Item {
	public SignpostMarkerItem() {
		setMaxStackSize(1);
		setUnlocalizedName("signpostMarker");
		setCreativeTab(CreativeTabs.tabTools);
		setTextureName("antiqueatlas:signpost_marker");
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack stack, EntityPlayer player, List list, boolean flag) {
		list.add(EnumChatFormatting.GRAY + StatCollector.translateToLocal("item.signpostMarker.desc"));
	}
}
