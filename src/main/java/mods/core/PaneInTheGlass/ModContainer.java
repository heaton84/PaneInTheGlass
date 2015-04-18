package mods.core.PaneInTheGlass;

import java.util.Arrays;

import cpw.mods.fml.client.registry.RenderingRegistry;
import cpw.mods.fml.common.DummyModContainer;
import cpw.mods.fml.common.LoadController;
import cpw.mods.fml.common.ModMetadata;
import cpw.mods.fml.common.event.FMLConstructionEvent;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;

public class ModContainer extends DummyModContainer {

	public static BlockRenderer modBlockRenderer = null;	
	
	public static final String MOD_VERSION = "1.2.0";
	
	public ModContainer()
	{
		super(new ModMetadata());
		
		ModMetadata meta = getMetadata();
		meta.modId = "PaneInTheGlass";
		meta.name = "Pane In The Glass CoreMod";
		meta.version = MOD_VERSION;
		meta.credits = "";
		meta.authorList = Arrays.asList("heaton84", "Techokami", "Justin Aquadro", "Christopher Trumbour");
		meta.description = "Glass that connects to stairs";
		meta.url = "http://www.minecraftforum.net/topic/2450118-172forge-paneintheglass-10/";
		meta.updateUrl = "";
		meta.screenshots = new String[0];
		meta.logoFile = "";
	}
	
	@Override
	public boolean registerBus(EventBus bus, LoadController controller) {
	bus.register(this);
	return true;
	}

	@Subscribe
	public void modConstruction(FMLConstructionEvent evt){
		
	}

	@Subscribe
	public void preInit(FMLPreInitializationEvent evt) {

	}

	@Subscribe
	public void init(FMLInitializationEvent evt) {

		ModContainer.modBlockRenderer = new BlockRenderer();
    	
    	// This field will be used by BlockStairs to return it's render type 
    	BlockRenderer.newBlockStairsRenderType = RenderingRegistry.getNextAvailableRenderId();
    	
    	RenderingRegistry.registerBlockHandler(BlockRenderer.newBlockStairsRenderType, ModContainer.modBlockRenderer);    	    
		
	}


	@Subscribe
	public void postInit(FMLPostInitializationEvent evt) {

	}	
	
}
