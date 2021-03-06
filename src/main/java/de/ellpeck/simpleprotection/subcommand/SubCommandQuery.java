package de.ellpeck.simpleprotection.subcommand;

import de.ellpeck.simpleprotection.ProtectedArea;
import de.ellpeck.simpleprotection.ProtectionManager;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.SyntaxErrorException;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class SubCommandQuery extends CommandBase{

    @Override
    public String getName(){
        return "query";
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
        return "Use '/simpleprotection query' to query the current location. Use '/simpleprotection query <name>' to query the specified area. Use '/simpleprotection query *' to query all areas.";
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException{
        if(args.length == 1){
            List<ProtectedArea> areas = ProtectionManager.getAreasForPos(sender.getEntityWorld(), sender.getPosition());
            if(!areas.isEmpty()){
                sender.sendMessage(new TextComponentString("There are "+areas.size()+" protections present:"));
                for(ProtectedArea area : areas){
                    sender.sendMessage(new TextComponentString(area.toString()));
                }
                return;
            }
            else{
                sender.sendMessage(new TextComponentString("This area isn't protected."));
                return;
            }
        }
        else if(args.length == 2){
            if("*".equals(args[1])){
                sender.sendMessage(new TextComponentString("In total, there are "+ProtectionManager.PROTECTED_AREAS.size()+" protections present:"));
                for(ProtectedArea area : ProtectionManager.PROTECTED_AREAS){
                    sender.sendMessage(new TextComponentString(area.toString()));
                }
                return;
            }
            else{
                ProtectedArea area = ProtectionManager.byName(args[1]);
                if(area != null){
                    sender.sendMessage(new TextComponentString(area.toString()));
                    return;
                }
                else{
                    throw new CommandException("Area by that name not found!");
                }
            }
        }

        throw new SyntaxErrorException("Wrong number of arguments!");
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
