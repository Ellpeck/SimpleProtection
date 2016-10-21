package de.ellpeck.simpleprotection.subcommand;

import de.ellpeck.simpleprotection.ProtectedArea;
import de.ellpeck.simpleprotection.ProtectionManager;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.SyntaxErrorException;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class SubCommandRemove extends CommandBase{

    @Override
    public String getCommandName(){
        return "remove";
    }

    @Override
    public String getCommandUsage(ICommandSender sender){
        return "Use '/simpleprotection remove <name> <type> <item>' to remove an item or block from the white/blacklist of the specified area. The type can be 'interact', 'break', or 'item'.";
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException{
        if(args.length == 4){
            ProtectedArea area = ProtectionManager.byName(args[1]);
            if(area != null){
                Map<String, Integer> map = area.getListByKey(args[2]);
                if(map != null){
                    if(map.containsKey(args[3])){
                        map.remove(args[3]);
                        notifyCommandListener(sender, this, "Successfully removed "+args[3]+" from the list of area "+area+"!");
                        return;
                    }
                    else throw new CommandException("There is no item with the specified name present.");
                }
                else{
                    throw new CommandException("The type is wrong. It needs to be either 'interact', 'break', or 'item'.");
                }
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
        else if(args.length == 3){
            return getListOfStringsMatchingLastWord(args, "interact", "break", "item");
        }
        else if(args.length == 4){
            ProtectedArea area = ProtectionManager.byName(args[1]);
            if(area != null){
                Map<String, Integer> map = area.getListByKey(args[2]);
                if(map != null){
                    return getListOfStringsMatchingLastWord(args, map.keySet());
                }
            }
        }
        return super.getTabCompletionOptions(server, sender, args, pos);
    }
}
