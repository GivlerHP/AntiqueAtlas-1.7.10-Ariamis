package hunternif.mc.atlas.signpost;

import hunternif.mc.atlas.AntiqueAtlasMod;
import hunternif.mc.atlas.api.AtlasAPI;

import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

/**
 * A signpost. Right-clicking it with the signpost marker item opens a GUI for
 * naming the location; the label then becomes a global marker visible in all
 * atlases. Breaking the block removes the marker from all atlases.
 */
public class SignpostBlock extends BlockContainer {
	/** Marker type used for signpost markers on the atlas. */
	public static final String MARKER_TYPE = "signpost";

	public SignpostBlock() {
		super(Material.wood);
		setHardness(2.0F);
		setResistance(5.0F);
		setStepSound(soundTypeWood);
		setHarvestLevel("axe", 0);
		setBlockName("signpost");
		setBlockTextureName("antiqueatlas:signpost_block");
		setCreativeTab(CreativeTabs.tabDecorations);
		// Thin column, like a standing sign:
		setBlockBounds(0.25F, 0.0F, 0.25F, 0.75F, 1.0F, 0.75F);
	}

	@Override
	public TileEntity createNewTileEntity(World world, int meta) {
		return new SignpostTileEntity();
	}

	@Override
	public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int side, float hitX, float hitY, float hitZ) {
		if (world.isRemote) {
			TileEntity te = world.getTileEntity(x, y, z);
			ItemStack stack = player.getCurrentEquippedItem();
			if (te instanceof SignpostTileEntity && !((SignpostTileEntity) te).hasMarker()
					&& stack != null && stack.getItem() instanceof SignpostMarkerItem) {
				AntiqueAtlasMod.proxy.openSignpostLabelGui(x, y, z);
			}
		}
		return false;
	}

	@Override
	public void onBlockPlacedBy(World world, int x, int y, int z, EntityLivingBase entity, ItemStack stack) {
		// 16 directions, like a standing sign:
		int facing = MathHelper.floor_double((double)(entity.rotationYaw * 16.0F / 360.0F) + 0.5D) & 15;
		world.setBlockMetadataWithNotify(x, y, z, facing, 2);
	}

	@Override
	public void breakBlock(World world, int x, int y, int z, Block block, int meta) {
		if (!world.isRemote) {
			TileEntity te = world.getTileEntity(x, y, z);
			if (te instanceof SignpostTileEntity) {
				SignpostTileEntity signpost = (SignpostTileEntity) te;
				if (signpost.hasMarker()) {
					AtlasAPI.markers.deleteGlobalMarker(world, signpost.getMarkerId());
				}
			}
		}
		super.breakBlock(world, x, y, z, block, meta);
	}

	@Override
	public boolean isOpaqueCube() {
		return false;
	}

	@Override
	public boolean renderAsNormalBlock() {
		return false;
	}

	@Override
	public int getRenderType() {
		return -1;
	}
}
