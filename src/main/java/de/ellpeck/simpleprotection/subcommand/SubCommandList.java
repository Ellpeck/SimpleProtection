package de.ellpeck.simpleprotection.subcommand;

import de.ellpeck.simpleprotection.ProtectedArea;
import de.ellpeck.simpleprotection.ProtectionManager;
import net.minecraft.command.*;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.oredict.OreDictionary;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class SubCommandList extends CommandBase{

    @Override
    public String getName(){
        return "list";
    }

    @Override
    public String getUsage(ICommandSender sender){
        return "Use '/simpleprotection list <name> <type>' to view the white/blacklist of the specified area. The type can be 'interact', 'break', 'player' or 'item'.";
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException{
        if(args.length == 3){
            ProtectedArea area = ProtectionManager.byName(args[1]);
            if(area != null){
                Map<String, Integer> map = area.getListByKey(args[2]);
                if(map != null){
                    boolean whitelist;
                    if("interact".equals(args[2])){
                        whitelist = area.isInteractBlocksWhitelist;
                    }
                    else if("break".equals(args[2])){
                        whitelist = area.isPlaceBreakBlocksWhitelist;
                    }
                    else if("player".equals(args[2])){
                        whitelist = area.isPlayersWhitelist;
                    }
                    else{
                        whitelist = area.isItemsWhitelist;
                    }
                    sender.sendMessage(new TextComponentString("This list is in "+(whitelist ? "whitelist" : "blacklist")+" mode!"));

                    sender.sendMessage(new TextComponentString("There are "+map.size()+" items on this list: "));
                    for(String strg : map.keySet()){
                        sender.sendMessage(new TextComponentString(strg+"@"+map.get(strg)));
                    }
                    return;
                }
                else{
                    throw new CommandException("The type is wrong. It needs to be either 'interact', 'break', 'player' or 'item'.");
                }
            }
            else{
                throw new CommandException("Area by that name not found!");
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
        else if(args.length == 3){
            return getListOfStringsMatchingLastWord(args, "interact", "break", "player", "item");
        }
        else{
            return super.getTabCompletions(server, sender, args, pos);
        }
    }
}
