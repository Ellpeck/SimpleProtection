package de.ellpeck.simpleprotection.subcommand;

import de.ellpeck.simpleprotection.ProtectedArea;
import de.ellpeck.simpleprotection.ProtectionManager;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.SyntaxErrorException;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class SubCommandVisualize extends CommandBase{

    @Override
    public String getName(){
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
    public String getUsage(ICommandSender sender){
        return "Use '/simpleprotection visualize <name>' to visualize a protected area. Use '/simpleprotection visualize *' to visualize all areas.";
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException{
        if(args.length == 2){
            World world = sender.getEntityWorld();
            if(world instanceof WorldServer && sender instanceof EntityPlayerMP){
                if("*".equals(args[1])){
                    for(ProtectedArea area : ProtectionManager.PROTECTED_AREAS){
                        visualize(area, world, sender);
                    }
                    sender.sendMessage(new TextComponentString("Visualizing all areas!"));
                    return;
                }
                else{
                    ProtectedArea area = ProtectionManager.byName(args[1]);
                    if(area != null){
                        sender.sendMessage(new TextComponentString("Visualizing area "+area+"!"));
                        visualize(area, world, sender);
                        return;
                    }
                    else{
                        throw new CommandException("Area by that name not found!");
                    }
                }
            }
            else{
                throw new CommandException("Visualization can only be done by a player!");
            }
        }

        throw new SyntaxErrorException("Wrong number of arguments!");
    }

    private static void visualize(ProtectedArea area, World world, ICommandSender sender){
        for(double x = area.bound.minX; x <= area.bound.maxX; x++){
            for(double y = area.bound.minY; y <= area.bound.maxY; y++){
                for(double z = area.bound.minZ; z <= area.bound.maxZ; z++){
                    int count = 0;
                    if(x == area.bound.minX || x == area.bound.maxX){
                        count++;
                    }
                    if(y == area.bound.minY || y == area.bound.maxY){
                        count++;
                    }
                    if(z == area.bound.minZ || z == area.bound.maxZ){
                        count++;
                    }

                    if(count >= 2){
                        ((WorldServer)world).spawnParticle((EntityPlayerMP)sender, EnumParticleTypes.BARRIER, true, x+0.5, y+0.5, z+0.5, 1, 0, 0, 0, 0);
                    }
                }
            }
        }
    }

    @Override
    public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, @Nullable BlockPos pos){
        if(args.length == 2){
            List<String> names = new ArrayList<String>();
            for(ProtectedArea area : ProtectionManager.PROTECTED_AREAS){
                names.add(area.name);
            }
            return getListOfStringsMatchingLastWord(args, names);
        }
        else{
            return super.getTabCompletions(server, sender, args, pos);
        }
    }
}
