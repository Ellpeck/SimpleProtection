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

public class SubCommandAddItem extends CommandBase{

    @Override
    public String getName(){
        return "additem";
    }

    @Override
    public String getUsage(ICommandSender sender){
        return "Use '/simpleprotection additem <name>' to add the item that is currently held to the specified area's white/blacklist. Append a * at the end to make the entry wildcard, meaning metadata will be ignored.";
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException{
        if(args.length == 2 || (args.length == 3 && "*".equals(args[2]))){
            boolean wildcard = args.length == 3;

            ProtectedArea area = ProtectionManager.byName(args[1]);
            if(area != null){
                if(sender instanceof EntityPlayer){
                    EntityPlayer player = (EntityPlayer)sender;
                    ItemStack held = player.getHeldItemMainhand();
                    if(held != null){
                        ItemStack copy = held.copy();
                        if(wildcard){
                            copy.setItemDamage(OreDictionary.WILDCARD_VALUE);
                        }

                        area.items.put(copy.getItem().getRegistryName().toString(), copy.getItemDamage());
                        sender.sendMessage(new TextComponentString("Item "+copy+" was successfully added to list of area "+area+"!"));
                        return;
                    }
                    else{
                        throw new CommandException("Player isn't holding an item!");
                    }
                }
                else{
                    throw new CommandException("Only players can add items!");
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
        else{
            return super.getTabCompletions(server, sender, args, pos);
        }
    }
}
