package de.ellpeck.simpleprotection;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.oredict.OreDictionary;

import java.util.*;

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

    public boolean isPlayersWhitelist;
    public Map<String, Integer> players = new HashMap<String, Integer>();

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
        else if("player".equals(key)){
            return this.players;
        }
        return null;
    }

    public NBTTagCompound writeToNBT(NBTTagCompound compound){
        compound.setString("Name", this.name);
        compound.setInteger("Dimension", this.dimension);

        compound.setDouble("MinX", this.bound.minX);
        compound.setDouble("MinY", this.bound.minY);
        compound.setDouble("MinZ", this.bound.minZ);
        compound.setDouble("MaxX", this.bound.maxX);
        compound.setDouble("MaxY", this.bound.maxY);
        compound.setDouble("MaxZ", this.bound.maxZ);

        compound.setTag("Interact", saveList(this.interactBlocks));
        compound.setTag("Break", saveList(this.placeBreakBlocks));
        compound.setTag("Item", saveList(this.items));
        compound.setTag("Player", saveList(this.players));

        compound.setBoolean("InteractWhite", this.isInteractBlocksWhitelist);
        compound.setBoolean("BreakWhite", this.isPlaceBreakBlocksWhitelist);
        compound.setBoolean("ItemWhite", this.isItemsWhitelist);
        compound.setBoolean("PlayersWhite", this.isPlayersWhitelist);

        return compound;
    }

    public void readFromNBT(NBTTagCompound compound){
        this.name = compound.getString("Name");
        this.dimension = compound.getInteger("Dimension");

        this.bound = new AxisAlignedBB(compound.getDouble("MinX"), compound.getDouble("MinY"), compound.getDouble("MinZ"), compound.getDouble("MaxX"), compound.getDouble("MaxY"), compound.getDouble("MaxZ"));

        this.interactBlocks = loadList(compound.getTagList("Interact", 10));
        this.placeBreakBlocks = loadList(compound.getTagList("Break", 10));
        this.items = loadList(compound.getTagList("Item", 10));
        this.players = loadList(compound.getTagList("Player", 10));

        this.isInteractBlocksWhitelist = compound.getBoolean("InteractWhite");
        this.isPlaceBreakBlocksWhitelist = compound.getBoolean("BreakWhite");
        this.isItemsWhitelist = compound.getBoolean("ItemWhite");
        this.isPlayersWhitelist = compound.getBoolean("PlayersWhite");
    }

    @Override
    public String toString(){
        return "'"+this.name+"', Dim: "+this.dimension+", Area: "+this.bound;
    }

    private static NBTTagList saveList(Map<String, Integer> map){
        NBTTagList interact = new NBTTagList();
        for(String key : map.keySet()){
            NBTTagCompound data = new NBTTagCompound();
            data.setString("Key", key);
            data.setInteger("Value", map.get(key));
            interact.appendTag(data);
        }
        return interact;
    }

    private static Map<String, Integer> loadList(NBTTagList list){
        Map<String, Integer> map = new HashMap<String, Integer>();
        for(int i = 0; i < list.tagCount(); i++){
            NBTTagCompound data = list.getCompoundTagAt(i);
            map.put(data.getString("Key"), data.getInteger("Value"));
        }
        return map;
    }
}
