package de.ellpeck.simpleprotection.subcommand;

import de.ellpeck.simpleprotection.CommandProtection;
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

public class SubCommandHelp extends CommandBase{

    @Override
    public String getCommandName(){
        return "help";
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
        return "Use '/simpleprotection help' to see help. Use '/simpleprotection help <subname>' to get information about a subcommand.";
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException{
        if(args.length == 1){
            notifyCommandListener(sender, this, "Possible subcommands:");
            for(CommandBase sub : ProtectionManager.COMMAND.subCommands){
                notifyCommandListener(sender, this, "/simpleprotection "+sub.getCommandName());
            }
            notifyCommandListener(sender, this, "It is also possible to use the following aliases: "+ProtectionManager.COMMAND.getCommandAliases());
            return;
        }
        else if(args.length == 2){
            for(CommandBase sub : ProtectionManager.COMMAND.subCommands){
                if(args[1].equals(sub.getCommandName())){
                    notifyCommandListener(sender, this, sub.getCommandUsage(sender));
                    return;
                }
            }
        }

        throw new SyntaxErrorException("Wrong number of arguments!");
    }

    @Override
    public List<String> getTabCompletionOptions(MinecraftServer server, ICommandSender sender, String[] args, @Nullable BlockPos pos){
        if(args.length == 2){
            List<String> possibilities = new ArrayList<String>();
            for(CommandBase sub : ProtectionManager.COMMAND.subCommands){
                possibilities.add(sub.getCommandName());
            }
            return getListOfStringsMatchingLastWord(args, possibilities);
        }
        else{
            return super.getTabCompletionOptions(server, sender, args, pos);
        }
    }
}
