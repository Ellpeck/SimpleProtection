package de.ellpeck.simpleprotection.subcommand;

import de.ellpeck.simpleprotection.CommandProtection;
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

public class SubCommandHelp extends CommandBase{

    @Override
    public String getName(){
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
    public String getUsage(ICommandSender sender){
        return "Use '/simpleprotection help' to see help. Use '/simpleprotection help <subname>' to get information about a subcommand.";
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException{
        if(args.length == 1){
            sender.sendMessage(new TextComponentString("Possible subcommands: (use '/simpleprotection help <subname>' to get information about a specific one.)"));
            for(CommandBase sub : ProtectionManager.COMMAND.subCommands){
                sender.sendMessage(new TextComponentString("/simpleprotection "+sub.getName()));
            }
            sender.sendMessage(new TextComponentString("It is also possible to use the following aliases: "+ProtectionManager.COMMAND.getAliases()));
            return;
        }
        else if(args.length == 2){
            for(CommandBase sub : ProtectionManager.COMMAND.subCommands){
                if(args[1].equals(sub.getName())){
                    sender.sendMessage(new TextComponentString(sub.getUsage(sender)));
                    return;
                }
            }
        }

        throw new SyntaxErrorException("Wrong number of arguments!");
    }

    @Override
    public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, @Nullable BlockPos pos){
        if(args.length == 2){
            List<String> possibilities = new ArrayList<String>();
            for(CommandBase sub : ProtectionManager.COMMAND.subCommands){
                possibilities.add(sub.getName());
            }
            return getListOfStringsMatchingLastWord(args, possibilities);
        }
        else{
            return super.getTabCompletions(server, sender, args, pos);
        }
    }
}
