package org.freeforums.geforce.beacon.main;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;

import org.freeforums.geforce.beacon.handlers.ForgeEventHandler;
import org.freeforums.geforce.beacon.network.ConfigurationHandler;
import org.freeforums.geforce.beacon.network.Links;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.ModMetadata;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.network.NetworkCheckHandler;
import cpw.mods.fml.relauncher.Side;

@Mod(modid="beacon", name="Beacon", version=mod_Beacon.VERSION, guiFactory = "org.freeforums.geforce.beacon.gui.BeaconGuiFactory", dependencies = mod_Beacon.FORGEVERSION)
public class mod_Beacon {
	
	public static final String MODID = "beacon";
	public static final String NAME = "Beacon";
	private static final String MOTU = "First!";
    
    //TODO ********************************* This is v1.0.0 for MC 1.7.10!
	protected static final String VERSION = "v1.0.5";
	protected static final String FORGEVERSION = "required-after:Forge@[10.13.0.1180,)";
	public static final String MCVERSION = "1.7.10";
	
	@Instance("beacon")
	public static mod_Beacon instance = new mod_Beacon();
	
	public static ForgeEventHandler eventHandler = new ForgeEventHandler();
	public static ConfigurationHandler configHandler = new ConfigurationHandler();
			
	public ArrayList<String> missingMods = new ArrayList<String>();
	public ArrayList<String> addedMods = new ArrayList<String>();

	public static Configuration configFile;
	public static String mcDirectory;
	
	
	@EventHandler
	public void preInit(FMLPreInitializationEvent event){
		mod_Beacon.configFile = new Configuration(event.getSuggestedConfigurationFile());

		this.configHandler.loadConfig(configFile);
		
		try{
			Links.setupLinks();	
		}catch(IOException e){
			e.printStackTrace();
			System.out.println("*** [Beacon] Beacon has encountered a problem while attempting to download the latest modlist from www.github.com. ***");
		}
		
		Links.setupLocalMods();
		Links.setupAliases();
		
		FMLCommonHandler.instance().bus().register(mod_Beacon.eventHandler);
		
		ModMetadata modMeta = event.getModMetadata();
        modMeta.authorList = Arrays.asList(new String[] {
            "Geforce"
        });
        modMeta.autogenerated = false;
        modMeta.credits = "Thanks to Stack Overflow for help with code."; 
        modMeta.description = "Beacon adds the ability to download mods in-game. \nJoin your favorite servers without having to download every mod in your browser! \n \nThe list of downloadable mods can be found at the above URL.\n \nMessage of the update: \n" + MOTU;
        modMeta.url = "http://www.github.com/Geforce132/Beacon";
	}
		
	
	@EventHandler
	public void init(FMLInitializationEvent event){}
		
	
	@EventHandler
	public void postInit(FMLPostInitializationEvent event){
		MinecraftForge.EVENT_BUS.register(mod_Beacon.eventHandler);
	}
	
	@NetworkCheckHandler
	public boolean onConnectionReceived(Map<String,String> modList, Side side){
		
		for(int i = 0; i < modList.size(); i++){
			String modid = (String) modList.keySet().toArray()[i];
			String version = (String) modList.values().toArray()[i];
			
			if(version.toLowerCase().startsWith("v")){ version = version.replaceFirst("v", ""); }
			if(modList.containsKey("Forge") && !HelpfulMethods.getVersionOfForge(modList.get("Forge")).matches(MCVERSION)){ continue; }
            if(modid.matches("mcp") || modid.matches("FML") || modid.matches("Forge")){ continue; }
			if(instance.missingMods.contains(modid + " v" + version)){ continue; }
			if(instance.addedMods.contains(modid + " v" + version)){ continue; }
			if(HelpfulMethods.hasMod(modid, version)){ continue; }
			
			
			instance.missingMods.add((modid + " v" + version));
		}
		
		return true;
	}

}
