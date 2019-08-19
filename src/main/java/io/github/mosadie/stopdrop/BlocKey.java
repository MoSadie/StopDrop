package io.github.mosadie.stopdrop;

import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLModDisabledEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

import java.util.Map;

import org.apache.logging.log4j.Logger;

import io.github.mosadie.stopdrop.api.IBlockableKey;

@Mod(modid = BlocKey.MODID, name = BlocKey.NAME, version = BlocKey.VERSION, updateJSON = "https://github.com/MoSadie/BlocKey/raw/master/updateJSON.json")
public class BlocKey
{
    public static final String MODID = "blockkey";
    public static final String NAME = "BlocKey";
    public static final String VERSION = "1.0.0";

    static Logger logger;
    private SDEventHandler eventHandler;
    private Map<String, Map<String, IBlockableKey>> registeredMods;

    @EventHandler
    public void preInit(FMLPreInitializationEvent event)
    {
    	// Get our logger
        logger = event.getModLog();
        
        // Setup event handler
        eventHandler = new SDEventHandler(this);
        MinecraftForge.EVENT_BUS.register(eventHandler);
        
        // Create override keybind
        overrideKeyBind = new KeyBinding("blockey.key.override", 999, "blockey.key.category");
    }
    
    @EventHandler
    public void postInit(FMLPostInitializationEvent event) {
    	// Grab the original key binding
    	origKeyBind = Minecraft.getMinecraft().gameSettings.keyBindDrop;
    	
    	// Enable drop key canceling
    	enable();
    	
    	ClientRegistry.registerKeyBinding(newKeyBind);
    	
    	logger.info("Registered alternate key binding.");
    }
    
    /**
     * Enables the drop key blocking.
     */
    public void enable() {
    	Minecraft.getMinecraft().gameSettings.keyBindDrop = newKeyBind;
    	logger.info("Enabled drop key blocking!");
    }
    
    /**
     * Disables the mod/drop key blocking.
     * @param event The disabled event from FML, currently unused, can be null.
     */
    @EventHandler
    public void disable(FMLModDisabledEvent event) {
    	Minecraft.getMinecraft().gameSettings.keyBindDrop = origKeyBind;
    	logger.info("Disabled drop key blocking!");
    }
    
    /**
     * Gets if the drop key cancellation is enabled.
     * @return true if the drop key is disabled, false otherwise.
     */
    public boolean getStatus() {
    	return Minecraft.getMinecraft().gameSettings.keyBindDrop == newKeyBind;
    }
    
    /**
     * Sends a message to the player telling them if drop key cancellation is enabled.
     */
    public void sendStatusMessage() {
    	if (getStatus()) {
        	Minecraft.getMinecraft().player.sendMessage(new TextComponentTranslation("command.blockey.status.enabled"));
    	} else {
    		Minecraft.getMinecraft().player.sendMessage(new TextComponentTranslation("command.blockey.status.disabled"));
    	}
    }
}