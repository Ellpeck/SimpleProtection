package de.ellpeck.simpleprotection;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldSavedData;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.Event;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Mod.EventBusSubscriber
public final class ProtectionManager{

    public static final CommandProtection COMMAND = new CommandProtection();
    public static final Map<ICommandSender, BlockPos> TEMP_POSITIONS = new HashMap<ICommandSender, BlockPos>();
    public static final List<ProtectedArea> PROTECTED_AREAS = new ArrayList<ProtectedArea>();

    public static List<ProtectedArea> getAreasForPos(World world, BlockPos pos){
        List<ProtectedArea> areasForPos = new ArrayList<ProtectedArea>();

        for(ProtectedArea area : PROTECTED_AREAS){
            if(area.isInArea(world, pos)){
                areasForPos.add(area);
            }
        }

        return areasForPos;
    }

    public static ProtectedArea byName(String name){
        for(ProtectedArea area : PROTECTED_AREAS){
            if(area.name.equals(name)){
                return area;
            }
        }
        return null;
    }

    @SubscribeEvent
    public static void onBlockBreak(BlockEvent.BreakEvent event){
        if(!(event.getPlayer() instanceof FakePlayer) && !isOp(event.getPlayer()) && !checkAllowBlock(event.getPlayer(), event.getPos(), event.getState(), false)){
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public static void onBlockPlace(BlockEvent.PlaceEvent event){
        if(!(event.getPlayer() instanceof FakePlayer) && !isOp(event.getPlayer()) && !checkAllowBlock(event.getPlayer(), event.getPos(), event.getPlacedBlock(), false)){
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public static void onBlockInteract(PlayerInteractEvent.RightClickBlock event){
        if(!(event.getEntityPlayer() instanceof FakePlayer) && !isOp(event.getEntityPlayer())){
            boolean blockOkay = checkAllowBlock(event.getEntityPlayer(), event.getPos(), event.getWorld().getBlockState(event.getPos()), true);
            boolean itemOkay = event.getItemStack() == null || checkAllowItem(event.getEntityPlayer(), event.getPos(), event.getItemStack());
            if(!blockOkay || !itemOkay){
                event.setCanceled(true);
            }
        }
    }

    @SubscribeEvent
    public static void onItemInteract(PlayerInteractEvent.RightClickItem event){
        if(!(event.getEntityPlayer() instanceof FakePlayer) && !isOp(event.getEntityPlayer()) && event.getItemStack() != null){
            if(!checkAllowItem(event.getEntityPlayer(), event.getPos(), event.getItemStack())){
                event.setCanceled(true);
            }
        }
    }

    @SubscribeEvent
    public static void onBlockClick(PlayerInteractEvent.LeftClickBlock event){
        if(!(event.getEntityPlayer() instanceof FakePlayer) && !isOp(event.getEntityPlayer()) && !checkAllowBlock(event.getEntityPlayer(), event.getPos(), event.getWorld().getBlockState(event.getPos()), false)){
            event.setUseBlock(Event.Result.DENY);
        }
    }

    private static boolean checkAllowBlock(EntityPlayer player, BlockPos pos, IBlockState state, boolean interact){
        if(state != null){
            Block block = state.getBlock();
            if(block != null){
                String name = block.getRegistryName().toString();
                int meta = block.getMetaFromState(state);

                List<ProtectedArea> areas = getAreasForPos(player.getEntityWorld(), pos);
                if(!areas.isEmpty()){

                    boolean result = true;
                    for(ProtectedArea area : areas){
                        if(ProtectedArea.isAllowed(area.players, player.getName(), 0, !area.isPlayersWhitelist)){
                            return true;
                        }
                        else{
                            Map<String, Integer> map = interact ? area.interactBlocks : area.placeBreakBlocks;
                            boolean whitelist = interact ? area.isInteractBlocksWhitelist : area.isPlaceBreakBlocksWhitelist;
                            if(!ProtectedArea.isAllowed(map, name, meta, whitelist)){
                                result = false;
                            }
                        }
                    }
                    return result;
                }
            }
        }
        return true;
    }

    private static boolean checkAllowItem(EntityPlayer player, BlockPos pos, ItemStack stack){
        if(stack != null){
            List<ProtectedArea> areas = getAreasForPos(player.getEntityWorld(), pos);
            if(!areas.isEmpty()){
                String name = stack.getItem().getRegistryName().toString();

                boolean result = true;
                for(ProtectedArea area : areas){
                    if(ProtectedArea.isAllowed(area.players, player.getName(), 0, !area.isPlayersWhitelist)){
                        return true;
                    }
                    else if(!ProtectedArea.isAllowed(area.items, name, stack.getItemDamage(), area.isItemsWhitelist)){
                        result = false;
                    }
                }
                return result;
            }
        }
        return true;
    }

    private static boolean isOp(EntityPlayer player){
        if(player != null){
            MinecraftServer server = player.getServer();
            if(server != null){
                int level = server.getPlayerList().getOppedPlayers().getPermissionLevel(player.getGameProfile());
                int levelNeeded = server.getOpPermissionLevel();
                return level >= levelNeeded;
            }
        }
        return false;
    }

    @SubscribeEvent
    public static void onWorldLoad(WorldEvent.Load event){
        if(!event.getWorld().isRemote){
            WorldData.get(event.getWorld()).markDirty();
        }
    }

    @SubscribeEvent
    public static void onWorldSave(WorldEvent.Save event){
        if(!event.getWorld().isRemote){
            WorldData.get(event.getWorld()).markDirty();
        }
    }

    public static class WorldData extends WorldSavedData{

        private static final String DATA_ID = "ProtectionManagerData";

        public WorldData(String id){
            super(id);
        }

        @Override
        public void readFromNBT(NBTTagCompound compound){
            PROTECTED_AREAS.clear();

            NBTTagList list = compound.getTagList("Protections", 10);
            for(int i = 0; i < list.tagCount(); i++){
                ProtectedArea area = new ProtectedArea(null, 0, null);
                area.readFromNBT(list.getCompoundTagAt(i));
                PROTECTED_AREAS.add(area);
            }
        }

        @Override
        public NBTTagCompound writeToNBT(NBTTagCompound compound){
            NBTTagList list = new NBTTagList();
            for(ProtectedArea area : PROTECTED_AREAS){
                list.appendTag(area.writeToNBT(new NBTTagCompound()));
            }
            compound.setTag("Protections", list);

            return compound;
        }

        public static WorldData get(World world){
            if(world.getMapStorage() != null){
                WorldData data = (WorldData)world.getMapStorage().getOrLoadData(WorldData.class, DATA_ID);
                if(data == null){
                    data = new WorldData(DATA_ID);
                    data.markDirty();
                    world.getMapStorage().setData(DATA_ID, data);
                }
                return data;
            }
            else{
                return null;
            }
        }
    }
}
