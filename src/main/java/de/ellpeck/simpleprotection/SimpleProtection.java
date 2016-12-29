package de.ellpeck.simpleprotection;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;

@Mod(modid = SimpleProtection.MOD_ID, name = SimpleProtection.NAME, version = SimpleProtection.VERSION, serverSideOnly = true, acceptableRemoteVersions = "*", acceptedMinecraftVersions = "[1.11,1.11.2]")
public class SimpleProtection{

    public static final String MOD_ID = "simpleprotection";
    public static final String NAME = "Simple Protection";
    public static final String VERSION = "@VERSION@";

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event){

    }

    @Mod.EventHandler
    public void serverStarting(FMLServerStartingEvent event){
        event.registerServerCommand(ProtectionManager.COMMAND);
    }
}
