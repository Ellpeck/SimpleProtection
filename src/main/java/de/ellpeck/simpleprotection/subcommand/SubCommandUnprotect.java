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

public class SubCommandUnprotect extends CommandBase{

    @Override
    public String getCommandName(){
        return "remove";
    }

    @Override
    public String getCommandUsage(ICommandSender sender){
        return "Use '/simpleprotection remove <name>' to remove the specified area. Use '/simpleprotection remove' to remove the stored first location of a protection that is being created.";
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException{
        if(args.length == 1){
            if(ProtectionManager.TEMP_POSITIONS.containsKey(sender)){
                ProtectionManager.TEMP_POSITIONS.remove(sender);
                notifyCommandListener(sender, this, "Removed stored position for sender!");
                return;
            }
            else{
                throw new CommandException("No stored position present for sender!");
            }
        }
        else if(args.length == 2){
            ProtectedArea area = ProtectionManager.byName(args[1]);
            if(area != null){
                ProtectionManager.PROTECTED_AREAS.remove(area);
                notifyCommandListener(sender, this, "Removed area "+area.toString()+".");
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
        else{
            return super.getTabCompletionOptions(server, sender, args, pos);
        }
    }
}
