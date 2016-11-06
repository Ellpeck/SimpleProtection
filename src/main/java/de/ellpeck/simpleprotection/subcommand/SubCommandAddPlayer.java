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

public class SubCommandAddPlayer extends CommandBase{

    @Override
    public String getCommandName(){
        return "addplayer";
    }

    @Override
    public String getCommandUsage(ICommandSender sender){
        return "Use '/simpleprotection addplayer <name> <playername>' to add the specified player to the specified area's white/blacklist.";
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException{
        if(args.length == 3){
            ProtectedArea area = ProtectionManager.byName(args[1]);
            if(area != null){
                area.players.put(args[2], 0);
                notifyCommandListener(sender, this, "Successfully added player "+args[2]+" to area "+area+"!");
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
        else if(args.length == 3){
            return getListOfStringsMatchingLastWord(args, server.getPlayerList().getAllUsernames());
        }
        else{
            return super.getTabCompletionOptions(server, sender, args, pos);
        }
    }
}
