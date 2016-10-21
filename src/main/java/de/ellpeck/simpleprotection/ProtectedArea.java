package de.ellpeck.simpleprotection;

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

    public boolean isPlaceBreakBlocksWhitelist;
    public Map<String, Integer> placeBreakBlocks = new HashMap<String, Integer>();

    public boolean isInteractBlocksWhitelist;
    public Map<String, Integer> interactBlocks = new HashMap<String, Integer>();

    public boolean isItemsWhitelist;
    public Map<String, Integer> items = new HashMap<String, Integer>();

    public ProtectedArea(String name, int dimension, AxisAlignedBB bound){
        this.name = name;
        this.dimension = dimension;
        this.bound = bound;
    }

    public boolean isInArea(World world, BlockPos pos){
        return world.provider.getDimension() == this.dimension && this.bound.isVecInside(new Vec3d(pos));
    }

    public static boolean isAllowed(Map<String, Integer> list, String item, int meta, boolean isWhitelist){
        if(list.containsKey(item)){
            int expected = list.get(item);
            if(expected == OreDictionary.WILDCARD_VALUE || meta == expected){
                return isWhitelist;
            }
        }
        return !isWhitelist;
    }

    public Map<String, Integer> getListByKey(String key){
        if("interact".equals(key)){
            return this.interactBlocks;
        }
        else if("break".equals(key)){
            return this.placeBreakBlocks;
        }
        else if("item".equals(key)){
            return this.items;
        }
        return null;
    }

    @Override
    public String toString(){
        return "'"+this.name+"', Dim: "+this.dimension+", Area: "+this.bound;
    }
}
