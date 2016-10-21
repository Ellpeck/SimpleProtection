package de.ellpeck.simpleprotection;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.oredict.OreDictionary;

import java.util.HashMap;
import java.util.Map;

public class ProtectedArea{
    public String name;
    public int dimension;
    public AxisAlignedBB bound;

    public boolean isBlockWhitelist;
    public Map<Block, Integer> blockList = new HashMap<Block, Integer>();

    public boolean isItemWhitelist;
    public Map<Item, Integer> itemList = new HashMap<Item, Integer>();

    public ProtectedArea(String name, int dimension, AxisAlignedBB bound){
        this.name = name;
        this.dimension = dimension;
        this.bound = bound;
    }

    public boolean isInArea(World world, BlockPos pos){
        return world.provider.getDimension() == this.dimension && this.bound.isVecInside(new Vec3d(pos));
    }

    public boolean isAllowed(Block block, int meta){
        if(this.blockList.containsKey(block)){
            int expected = this.blockList.get(block);
            if(expected == OreDictionary.WILDCARD_VALUE || meta == expected){
                return this.isBlockWhitelist;
            }
        }
        return !this.isBlockWhitelist;
    }

    public boolean isAllowed(Item item, int meta){
        if(this.itemList.containsKey(item)){
            int expected = this.itemList.get(item);
            if(expected == OreDictionary.WILDCARD_VALUE || meta == expected){
                return this.isItemWhitelist;
            }
        }
        return !this.isItemWhitelist;
    }

    @Override
    public String toString(){
        return "'"+this.name+"', Dim: "+this.dimension+", Area: "+this.bound;
    }
}
