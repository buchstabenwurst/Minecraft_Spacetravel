package com.buchstabenwurst.travelBlock.block;

import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;

import java.util.ArrayList;

import com.buchstabenwurst.travelBlock.ExampleMod;
import com.buchstabenwurst.travelBlock.ModBlocks;

import net.minecraft.item.Item;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.World;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class TravelBlock extends Block {
    public TravelBlock(){
        super(Material.ROCK);
        String registryname = "travelblock";
        setRegistryName(registryname);
        setUnlocalizedName(ExampleMod.MODID + ".travelblock");
        setHardness(1.0f);
        setHarvestLevel("pickaxe", 0);
        setResistance(1.0f);
        setCreativeTab(CreativeTabs.DECORATIONS);
        this.setSoundType(SoundType.STONE);
    }

    
    @Override
    public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ)
    {
        if (worldIn.isRemote)
            return true;
        
        //playerIn.openGui(GalacticraftCore.instance, SchematicRegistry.getMatchingRecipeForID(0).getGuiID(), worldIn, pos.getX(), pos.getY(), pos.getZ());
        veinOres.clear();
        getVeinOres(worldIn, pos,pos.getX(),pos.getY(),pos.getZ(),10000);
        playerIn.sendMessage(new TextComponentString("Number of Blocks: " + veinOres.size()));
        veinOres.add(pos); // add self
        DimensionManager.initDimension(-1);
        //move the blocks
        for(int i=0;i<veinOres.size();i++){
            DimensionManager.getWorld(-1).setBlockState(veinOres.get(i), worldIn.getBlockState(veinOres.get(i)));
            //worldIn.setBlockState(veinOres.get(i), Blocks.CLAY.getDefaultState());
            worldIn.setBlockToAir(veinOres.get(i));

        }
        DimensionManager.unloadWorld(-1);
        return true;
    }

    @SideOnly(Side.CLIENT)
    public void initModel() {
        //ModelResourceLocation modelResourceLocation = new ModelResourceLocation("spacetravel:travelblock", "inventory");
        //ModelBakery.registerItemVariants(Item.getItemFromBlock(this), );
        ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(this), 0, new ModelResourceLocation(ModBlocks.travelBlock.getRegistryName(), "inventory"));
        //ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(this), 0, new ModelResourceLocation(ModBlocks.travelBlock.getRegistryName(), "normal"));
    }

    ArrayList<BlockPos> veinOres = new ArrayList<BlockPos>();
    public void getVeinOres(World worldIn, BlockPos b1,final int x1, final int y1, final int z1,int maxSize) {
        int searchCubeSize = 20;
        if (veinOres.size() >= maxSize) {
            return;
        }
        for (int x = -1; x <= 1; x++) { //These 3 for loops check a 3x3x3 cube around the block in question
            for (int y = -1; y <= 1; y++) {
                for (int z = -1; z <= 1; z++) {
                    if (x == 0 && y == 0 && z == 0) { //We can skip the 0,0,0 case because that is the block in question
                        continue;
                    }
                    BlockPos b2 = b1.add(x, y, z);
                    int blockX = b2.getX();
                    int blockY = b2.getY();
                    int blockZ = b2.getZ();
                    if (blockX == x1 && blockY == y1 && blockZ == z1) { //Makes sure the original block is never added to veinOres
                        continue;
                    }
                    if (worldIn.getBlockState(b2).getBlock().getMaterial(worldIn.getBlockState(b2).getBlock().getDefaultState()) != Material.AIR) {
                        if (blockX > x1 + searchCubeSize || blockX < x1 - searchCubeSize || blockY > y1 + searchCubeSize || blockY < y1 - searchCubeSize || blockZ > z1 + searchCubeSize || blockZ < z1 - searchCubeSize) {
                            break;
                        }
                        else if (!(veinOres.contains(b2))) {
                            if (veinOres.size() >= maxSize) {
                                return;
                            }
                            veinOres.add(b2);
                            this.getVeinOres(worldIn, b2, x1, y1, z1,maxSize);
                        }
                    }
                }
            }
        }
    }
 
}
