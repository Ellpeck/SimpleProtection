package de.ellpeck.simpleprotection.subcommand;

import de.ellpeck.simpleprotection.ProtectedArea;
import de.ellpeck.simpleprotection.ProtectionManager;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.SyntaxErrorException;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class SubCommandExpand extends CommandBase{
    @Override
    public String getCommandName(){
        return "expand";
    }

    @Override
    public String getCommandUsage(ICommandSender sender){
        return "Use '/simpleprotection expand <area> <x1> <y1> <z1> <x2> <y2> <z2>' to expand the specified area's bounds.";
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException{
        if(args.length == 8){
            ProtectedArea area = ProtectionManager.byName(args[1]);
            if(area != null){
                try{
                    int x1 = Integer.parseInt(args[2]);
                    int y1 = Integer.parseInt(args[3]);
                    int z1 = Integer.parseInt(args[4]);
                    int x2 = Integer.parseInt(args[5]);
                    int y2 = Integer.parseInt(args[6]);
                    int z2 = Integer.parseInt(args[7]);
                    area.bound = new AxisAlignedBB(area.bound.minX-x1, area.bound.minY-y1, area.bound.minZ-z1, area.bound.maxX+x2, area.bound.maxY+y2, area.bound.maxZ+z2);

                    notifyCommandListener(sender, this, "Expanded area "+area+" by "+x1+", "+y1+", "+z1+", "+x2+", "+y2+", "+z2+"!");
                    return;
                }
                catch(Exception e){
                    throw new CommandException("The coordinates couldn't be parsed!");
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
        else{
            return super.getTabCompletionOptions(server, sender, args, pos);
        }
    }
}
