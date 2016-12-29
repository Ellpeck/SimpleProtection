package de.ellpeck.simpleprotection.subcommand;

import de.ellpeck.simpleprotection.ProtectedArea;
import de.ellpeck.simpleprotection.ProtectionManager;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.SyntaxErrorException;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;

public class SubCommandProtect extends CommandBase{

    @Override
    public String getName(){
        return "protect";
    }

    @Override
    public String getUsage(ICommandSender sender){
        return "Use '/simpleprotection protect' to mark the first position at your location. Then, use use '/simpleprotection protect <name>' to mark the second position at your location and save the area.";
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException{
        if(args.length == 1){
            if(ProtectionManager.TEMP_POSITIONS.containsKey(sender)){
                throw new SyntaxErrorException("First point already marked! Add a name to save the area. Use '/simpleprotection remove' to remove marked point!");
            }
            else{
                ProtectionManager.TEMP_POSITIONS.put(sender, sender.getPosition());
                sender.sendMessage(new TextComponentString("First point marked at "+sender.getPosition()+"."));
                return;
            }
        }
        else if(args.length == 2){
            if(ProtectionManager.TEMP_POSITIONS.containsKey(sender)){
                if(ProtectionManager.byName(args[1]) == null){
                    BlockPos firstPos = ProtectionManager.TEMP_POSITIONS.get(sender);
                    ProtectionManager.TEMP_POSITIONS.remove(sender);

                    AxisAlignedBB bound = new AxisAlignedBB(firstPos, sender.getPosition());
                    ProtectedArea area = new ProtectedArea(args[1], sender.getEntityWorld().provider.getDimension(), bound);
                    ProtectionManager.PROTECTED_AREAS.add(area);

                    sender.sendMessage(new TextComponentString("Area "+area+" created!"));
                    return;
                }
                else throw new CommandException("There is already an area with the name "+args[1]+"!");
            }
            else{
                throw new SyntaxErrorException("First point not marked yet!");
            }
        }

        throw new SyntaxErrorException("Wrong number of arguments!");
    }
}
