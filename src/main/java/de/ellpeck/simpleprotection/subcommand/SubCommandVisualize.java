package de.ellpeck.simpleprotection.subcommand;

import de.ellpeck.simpleprotection.ProtectedArea;
import de.ellpeck.simpleprotection.ProtectionManager;
import net.minecraft.command.*;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class SubCommandVisualize extends CommandBase{

    @Override
    public String getCommandName(){
        return "visualize";
    }

    @Override
    public int getRequiredPermissionLevel(){
        return 0;
    }

    @Override
    public boolean checkPermission(MinecraftServer server, ICommandSender sender){
        return true;
    }

    @Override
    public String getCommandUsage(ICommandSender sender){
        return "Use '/simpleprotection visualize <name>' to visualize a protected area.";
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException{
        if(args.length == 2){
            ProtectedArea area = ProtectionManager.byName(args[1]);
            if(area != null){
                notifyCommandListener(sender, this, "Visualizing area "+area+"!");

                World world = sender.getEntityWorld();
                if(world instanceof WorldServer && sender instanceof EntityPlayerMP){
                    for(double x = area.bound.minX; x <= area.bound.maxX; x++){
                        for(double y = area.bound.minY; y <= area.bound.maxY; y++){
                            for(double z = area.bound.minZ; z <= area.bound.maxZ; z++){
                                if(x == area.bound.minX || x == area.bound.maxX || y == area.bound.minY || y == area.bound.maxY || z == area.bound.minZ || z == area.bound.maxZ){
                                    ((WorldServer)world).spawnParticle((EntityPlayerMP)sender, EnumParticleTypes.END_ROD, true, x+0.5, y+0.5, z+0.5, 1, 0, 0, 0, 0);
                                }
                            }
                        }
                    }
                }
                else{
                    throw new CommandException("Visualization can only be done by a player!");
                }

                return;
            }
            else{
                throw new CommandException("Area by that name not found!");
            }
        }

        throw new SyntaxErrorException("Wrong number of arguments!");
    }

    @Override
    public List<String> getTabCompletionOptions(MinecraftServer server, ICommandSender sender, String[] args, @Nullable BlockPos pos){
        if(args.length == 2){
            List<String> names = new ArrayList<String>();
            for(ProtectedArea area : ProtectionManager.PROTECTED_AREAS){
                names.add(area.name);
            }
            return getListOfStringsMatchingLastWord(args, names);
        }
        else{
            return super.getTabCompletionOptions(server, sender, args, pos);
        }
    }
}
