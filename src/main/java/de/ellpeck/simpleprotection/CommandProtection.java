package de.ellpeck.simpleprotection;

import de.ellpeck.simpleprotection.subcommand.*;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CommandProtection extends CommandBase{

    public List<CommandBase> subCommands = new ArrayList<CommandBase>();

    public CommandProtection(){
        this.subCommands.add(new SubCommandHelp());
        this.subCommands.add(new SubCommandQuery());
        this.subCommands.add(new SubCommandProtect());
        this.subCommands.add(new SubCommandUnprotect());
        this.subCommands.add(new SubCommandVisualize());
        this.subCommands.add(new SubCommandAddBlock());
        this.subCommands.add(new SubCommandAddItem());
        this.subCommands.add(new SubCommandList());
        this.subCommands.add(new SubCommandRemove());
    }

    @Override
    public String getCommandName(){
        return "simpleprotection";
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
    public List<String> getCommandAliases(){
        return Arrays.asList("simpleprot", "simprot", "prot");
    }

    @Override
    public String getCommandUsage(ICommandSender sender){
        return "/simpleprotection <subcommand>, use '/simpleprotection help' to see all subcommands.";
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException{
        if(args.length > 0){
            for(CommandBase sub : this.subCommands){
                if(sub.getCommandName().equals(args[0])){
                    if(sub.checkPermission(server, sender)){
                        sub.execute(server, sender, args);
                        return;
                    }
                    else{
                        TextComponentTranslation info = new TextComponentTranslation("commands.generic.permission");
                        info.getStyle().setColor(TextFormatting.RED);
                        sender.addChatMessage(info);
                        return;
                    }
                }
            }
        }

        throw new WrongUsageException(this.getCommandUsage(sender));
    }

    @Override
    public List<String> getTabCompletionOptions(MinecraftServer server, ICommandSender sender, String[] args, BlockPos pos){
        if(args == null || args.length <= 1){
            List<String> possibilities = new ArrayList<String>();
            for(CommandBase sub : this.subCommands){
                possibilities.add(sub.getCommandName());
            }
            return getListOfStringsMatchingLastWord(args, possibilities);
        }
        else{
            for(CommandBase sub : this.subCommands){
                if(sub.getCommandName().equals(args[0])){
                    return sub.getTabCompletionOptions(server, sender, args, pos);
                }
            }

            return super.getTabCompletionOptions(server, sender, args, pos);
        }
    }
}
