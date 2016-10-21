package de.ellpeck.simpleprotection;

import net.minecraft.command.ICommandSender;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.Mod;

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
}
