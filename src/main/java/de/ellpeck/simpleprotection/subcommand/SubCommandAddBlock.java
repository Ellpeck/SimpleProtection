package de.ellpeck.simpleprotection.subcommand;

import de.ellpeck.simpleprotection.ProtectedArea;
import de.ellpeck.simpleprotection.ProtectionManager;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.command.*;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.oredict.OreDictionary;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class SubCommandAddBlock extends CommandBase{

    @Override
    public String getCommandName(){
        return "addblock";
    }

    @Override
    public String getCommandUsage(ICommandSender sender){
        return "Use '/simpleprotection addblock <name> <type>' to add the block that is currently looked at to the specified area's white/blacklist. The type can be 'interact' or 'break', the latter also applies for placing. Append a * at the end to make the entry wildcard, meaning metadata will be ignored.";
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException{
        if(args.length == 3 || (args.length == 4 && "*".equals(args[3]))){
            boolean wildcard = args.length == 4;

            boolean interact = "interact".equals(args[2]);
            if(interact || "break".equals(args[2])){
                ProtectedArea area = ProtectionManager.byName(args[1]);
                if(area != null){
                    if(sender instanceof EntityPlayerMP){
                        EntityPlayerMP player = (EntityPlayerMP)sender;
                        RayTraceResult result = ForgeHooks.rayTraceEyes(player, player.interactionManager.getBlockReachDistance());
                        if(result != null && result.getBlockPos() != null){
                            IBlockState state = player.worldObj.getBlockState(result.getBlockPos());
                            Block block = state.getBlock();
                            int meta = wildcard ? OreDictionary.WILDCARD_VALUE : block.getMetaFromState(state);

                            (interact ? area.interactBlocks : area.placeBreakBlocks).put(block.getRegistryName().toString(), meta);
                            notifyCommandListener(sender, this, "Block "+block.getRegistryName()+" with meta "+meta+" was successfully added to "+(interact ? "interact" : "place/break")+" list of area "+area+"!");
                            return;
                        }
                        else{
                            throw new CommandException("Not looking at anything!");
                        }
                    }
                    else{
                        throw new CommandException("Only players can add blocks!");
                    }
                }
                else{
                    throw new CommandException("Area by that name not found!");
                }
            }
            else{
                throw new CommandException("Third parameter must either be 'interact' or 'break'!");
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
            return getListOfStringsMatchingLastWord(args, "interact", "break");
        }
        else{
            return super.getTabCompletionOptions(server, sender, args, pos);
        }
    }
}
