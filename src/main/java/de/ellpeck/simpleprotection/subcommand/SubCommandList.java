package de.ellpeck.simpleprotection.subcommand;

import de.ellpeck.simpleprotection.ProtectedArea;
import de.ellpeck.simpleprotection.ProtectionManager;
import net.minecraft.command.*;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.oredict.OreDictionary;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class SubCommandList extends CommandBase{

    @Override
    public String getCommandName(){
        return "list";
    }

    @Override
    public String getCommandUsage(ICommandSender sender){
        return "Use '/simpleprotection list <name> <type>' to view the white/blacklist of the specified area. The type can be 'interact', 'break', or 'item'.";
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException{
        if(args.length == 3){
            ProtectedArea area = ProtectionManager.byName(args[1]);
            if(area != null){
                Map<String, Integer> map = area.getListByKey(args[2]);
                if(map != null){
                    notifyCommandListener(sender, this, "There are "+map.size()+" items on this list: ");
                    for(String strg : map.keySet()){
                        notifyCommandListener(sender, this, strg+"@"+map.get(strg));
                    }
                    return;
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
        else{
            return super.getTabCompletionOptions(server, sender, args, pos);
        }
    }
}
