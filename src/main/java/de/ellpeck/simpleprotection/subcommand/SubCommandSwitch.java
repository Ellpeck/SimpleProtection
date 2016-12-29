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

public class SubCommandSwitch extends CommandBase{

    @Override
    public String getName(){
        return "switch";
    }

    @Override
    public String getUsage(ICommandSender sender){
        return "Use '/simpleprotection switch <name> <type>' to switch a specified area's whitelist to a blacklist and vice versa. The type can be 'interact', 'break', 'player' or 'item'.";
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException{
        if(args.length == 3){
            ProtectedArea area = ProtectionManager.byName(args[1]);
            if(area != null){
                if("interact".equals(args[2])){
                    area.isInteractBlocksWhitelist = !area.isInteractBlocksWhitelist;
                    sender.sendMessage(new TextComponentString("Switched the block interaction of the area "+area+" to be a "+(area.isInteractBlocksWhitelist ? "whitelist" : "blacklist")+"."));
                    return;
                }
                else if("break".equals(args[2])){
                    area.isPlaceBreakBlocksWhitelist = !area.isPlaceBreakBlocksWhitelist;
                    sender.sendMessage(new TextComponentString("Switched the block breaking and placing of the area "+area+" to be a "+(area.isPlaceBreakBlocksWhitelist ? "whitelist" : "blacklist")+"."));
                    return;
                }
                else if("item".equals(args[2])){
                    area.isItemsWhitelist = !area.isItemsWhitelist;
                    sender.sendMessage(new TextComponentString("Switched the item usage of the area "+area+" to be a "+(area.isItemsWhitelist ? "whitelist" : "blacklist")+"."));
                    return;
                }
                else if("player".equals(args[2])){
                    area.isPlayersWhitelist = !area.isPlayersWhitelist;
                    sender.sendMessage(new TextComponentString("Switched the player list of the area "+area+" to be a "+(area.isPlayersWhitelist ? "whitelist" : "blacklist")+"."));
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
