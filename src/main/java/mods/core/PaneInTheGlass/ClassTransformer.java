package mods.core.PaneInTheGlass;

import cpw.mods.fml.common.FMLLog;

import java.util.*;

import org.objectweb.asm.*;
import org.objectweb.asm.tree.*;

import mods.util.registry.NameRegistry;
import net.minecraft.launchwrapper.IClassTransformer;

public class ClassTransformer implements IClassTransformer {

	private NameRegistry nameRegistry;
	
	private String blockPaneClassName;
	private String blockStairsClassName;
	private String worldRendererClassName;
	
	public ClassTransformer() {
				
		try
		{		
			nameRegistry = new NameRegistry();
			
			// BlockPane
			nameRegistry.RegisterSrgName("CL: net/minecraft/block/BlockPane amm");
			nameRegistry.RegisterSrgName("MD: net/minecraft/block/BlockPane/canPaneConnectToBlock (Lnet/minecraft/block/Block;)Z amm/a (Lahu;)Z");
	
			// BlockStairs
			nameRegistry.RegisterSrgName("CL: net/minecraft/block/BlockStairs ame");		
			nameRegistry.RegisterSrgName("MD: net/minecraft/block/BlockStairs/getRenderBlockPass ()I ame/w ()I");
			nameRegistry.RegisterSrgName("MD: net/minecraft/block/BlockStairs/getRenderType ()I ame/b ()I");
					
			// WorldRenderer
			nameRegistry.RegisterSrgName("CL: net/minecraft/client/renderer/WorldRenderer blg");
			nameRegistry.RegisterSrgName("MD: net/minecraft/client/renderer/WorldRenderer/preRenderBlocks (I)V blg/b (I)V");
			
			FMLLog.info("Getting class names");
			
			blockPaneClassName = nameRegistry.getClassName("net/minecraft/block/BlockPane").replace('/', '.');
			blockStairsClassName = nameRegistry.getClassName("net/minecraft/block/BlockStairs").replace('/', '.');
			worldRendererClassName = nameRegistry.getClassName("net/minecraft/client/renderer/WorldRenderer").replace('/', '.');			
		}
		catch (Exception e)
		{
			FMLLog.severe("UH OH: " + e.toString());			
		}
	}

	@Override
	public byte[] transform(String className, String transformedName, byte[] basicClass) {
		
		if (className.equals(blockPaneClassName) || className.equals(blockStairsClassName) || className.equals(worldRendererClassName))
		{
			return patchClass(className, basicClass);
		}
		else
		{
			return basicClass;
		}				
	}
		
	private byte[] patchClass(String className, byte[] classData)
	{
		// Walk through all methods, invoking dispatchTransformMethod as needed
		ClassNode classNode = new ClassNode();
		ClassReader classReader = new ClassReader(classData);
		classReader.accept(classNode, 0);

		Iterator<MethodNode> methods = classNode.methods.iterator();		
						
		while(methods.hasNext())
		{
			MethodNode m = methods.next();
			
			if (className.equals(blockPaneClassName)
					&& nameRegistry.MethodIs(m, "net/minecraft/block/BlockPane/canPaneConnectToBlock (Lnet/minecraft/block/Block;)Z"))
			{
				patchBlockPane_canPaneConnectToBlock(m);
			}
			else if (className.equals(blockStairsClassName)
					&& nameRegistry.MethodIs(m, "net/minecraft/block/BlockStairs/getRenderBlockPass ()I"))
			{
				patchBlockStairs_getRenderBlockPass(m);
			}
			else if (className.equals(blockStairsClassName)
					&& nameRegistry.MethodIs(m, "net/minecraft/block/BlockStairs/getRenderType ()I"))
			{
				patchBlockStairs_getRenderType(m);
			}
			else if (className.equals(worldRendererClassName)
					&& nameRegistry.MethodIs(m, "net/minecraft/client/renderer/WorldRenderer/preRenderBlocks (I)V"))
			{
				patchWorldRenderer_preRenderBlocks(m);
			}
		}

		ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_MAXS | ClassWriter.COMPUTE_FRAMES);
		classNode.accept(writer);
		return writer.toByteArray();
	}

	private void patchBlockPane_canPaneConnectToBlock(MethodNode m)
	{		
		Type targetClass = Type.getType(mods.core.PaneInTheGlass.BlockPaneOverrides.class);
		String targetSig = nameRegistry.getMethodSignature("net/minecraft/block/BlockPane/canPaneConnectToBlock", "(Lnet/minecraft/block/Block;)Z");
												
		m.instructions.clear();
		m.instructions.add(new VarInsnNode(Opcodes.ALOAD, 1)); // aload_1 [Block]
		m.instructions.add(new MethodInsnNode(Opcodes.INVOKESTATIC, targetClass.getInternalName(), "canPaneConnectToBlock", targetSig));
		m.instructions.add(new InsnNode(Opcodes.IRETURN));	   // ireturn
	}

	private void patchBlockStairs_getRenderBlockPass(MethodNode m)
	{
		// return 1;

		m.instructions.clear();		
		m.instructions.add(new InsnNode(Opcodes.ICONST_1));
		m.instructions.add(new InsnNode(Opcodes.IRETURN));			
	}

	private void patchBlockStairs_getRenderType(MethodNode m)
	{
		// return mods.core.PaneInTheGlass.BlockRenderer.newBlockStairsRenderType
				
		Type modBlockRenderer = Type.getType(mods.core.PaneInTheGlass.BlockRenderer.class);
		
		m.instructions.clear();
		m.instructions.add(new FieldInsnNode(Opcodes.GETSTATIC, modBlockRenderer.getInternalName(), "newBlockStairsRenderType", "I"));
		m.instructions.add(new InsnNode(Opcodes.IRETURN));			
	}
	
	private void patchWorldRenderer_preRenderBlocks(MethodNode m)
	{
		// Insert statement that stores render pass in mods.core.PaneInTheGlass.BlockRenderer.renderPass
				
		Type modBlockRenderer = Type.getType(mods.core.PaneInTheGlass.BlockRenderer.class);
		InsnList payload = new InsnList(); // List of our additional instructions to invoke
		
		payload.add(new VarInsnNode(Opcodes.ILOAD, 1));
		payload.add(new FieldInsnNode(Opcodes.PUTSTATIC, modBlockRenderer.getInternalName(), "renderPass", "I"));
		
		// These go at the top of the method, so everything is normal after our code
		m.instructions.insert(payload);
	}		
}
