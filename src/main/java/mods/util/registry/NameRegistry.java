package mods.util.registry;

import net.minecraft.block.Block;

import org.objectweb.asm.*;
import org.objectweb.asm.tree.MethodNode;

import cpw.mods.fml.common.FMLLog;

import java.util.*;

public class NameRegistry {
	
	// Searge Data Format:
	// PK: <ignore these lines for now>
	// CL: net/minecraft/block/Block ahu
	// MD: net/minecraft/item/ItemTool/onBlockDestroyed (Lnet/minecraft/item/ItemStack;Lnet/minecraft/world/World;Lnet/minecraft/block/Block;IIILnet/minecraft/entity/EntityLivingBase;)Z aas/a (Labp;Lafn;Lahu;IIILrh;)Z
	
	private boolean isObfuscated = false;
	
	private class SeargeData
	{
		//public String objectType;
		
		public String uniqueName; // Used to index object in array. Namely: Methods must include a signature!
		
		public String objectName;
		public String objectNameObfuscated;
		
		public String objectSignature;
		public String objectSignatureObfuscated;
		
		public SeargeData(String src)
		{
			String[] srcToken = src.split("\\s+");
			
			if (srcToken[0].equals("PK:"))
			{
				// Ignore package types for now
			}
			else if (srcToken[0].equals("CL:"))
			{
				// Class descriptor
				//objectType = "CL";
				
				if (srcToken.length == 3)
				{
					objectName = srcToken[1];
					objectNameObfuscated = srcToken[2];
					
					uniqueName = objectName;
				}
				else
				{
					// TODO
				}
			}
			else if (srcToken[0].equals("MD:"))
			{
				// Method descriptor
				//objectType = "MD";
				
				if (srcToken.length == 5)
				{
					objectName = srcToken[1];
					objectNameObfuscated = srcToken[3];

					objectSignature = srcToken[2];
					objectSignatureObfuscated = srcToken[4];
					
					uniqueName = objectName + " " + objectSignature;
				}
				else
				{
					// TODO
				}
			}
		}
	}
	
	private HashMap<String, SeargeData> m_NameList;
	
	public NameRegistry() {
		// Determine if we are obfuscated or not by checking out the Minecraft Block classname

		m_NameList = new HashMap<String, SeargeData>();

		FMLLog.info("NameRegistry is testing for obfuscation");
		
		// TODO: Figure out how to handle the error thrown when we spin up a Block in obfuscated MC
		
		/*try
		{
			TestForBlock();
			isObfuscated = false;
		}
		catch (ClassNotFoundException ex)
		{
			isObfuscated = true;
		}
		catch (Exception ex2)
		{
			isObfuscated = true;
		}*/
		isObfuscated = true;
				
		/*if (testType.getInternalName().endsWith("Block"))
			isObfuscated = false;
		else
			isObfuscated = true;*/

		FMLLog.info("NameRegistry: isObfuscated=" + isObfuscated);
	}
		
	private void TestForBlock() throws ClassNotFoundException
	{
		Type testType = Type.getType(net.minecraft.block.Block.class);
	}
	
	
	//
	// getClassName
	//
	// Returns the appropriate obfuscated or unobfuscated class name for a Minecraft class
	//
	public String getClassName(String className)
	{
		String indexer = normalizeName(className);
		SeargeData srg = m_NameList.get(indexer);
		
		return (srg == null ? null : (isObfuscated ? srg.objectNameObfuscated : srg.objectName));
	}	
	
	//
	// getMethodNameWithClass(String, String)
	//
	// Given a method name in path/to/class/methodname or path.to.class.methodname format
	// Returns proper method name in path/to/class/methodname format
	public String getMethodNameWithClass(String methodName, String methodSignature)
	{
		String indexer = normalizeName(methodName + " " + methodSignature);
		SeargeData srg = m_NameList.get(indexer);
		
		return (srg == null ? null : (isObfuscated ? srg.objectNameObfuscated : srg.objectName));
	}

	//
	// getMethodName(String, String)
	//
	// Given a method name in path/to/class/methodname or path.to.class.methodname format
	// Returns only the method name
	public String getMethodName(String methodName, String methodSignature)
	{
		String nameWithClass = getMethodNameWithClass(methodName, methodSignature);
		
		if (nameWithClass != null)
		{
			// Chomp off everything up to '/'
			int slash = nameWithClass.lastIndexOf('/');
			
			if (slash > -1)
			{
				nameWithClass = nameWithClass.substring(slash + 1);
			}
		}
		
		return nameWithClass;
	}
	
	//
	// getMethodSignature(String, String)
	//
	// Given a method name in path/to/class/methodname or path.to.class.methodname format
	public String getMethodSignature(String methodName, String methodSignature)
	{
		String indexer = normalizeName(methodName + " " + methodSignature);
		SeargeData srg = m_NameList.get(indexer);
		
		return (srg == null ? null : (isObfuscated ? srg.objectSignatureObfuscated : srg.objectSignature));
	}

	//
	// MethodIs
	//
	// methodNode: Node to test
	// methodNameWithSignature: Unobfuscated name WITH signature (must be separated by a space)
	//
	public boolean MethodIs(MethodNode methodNode, String methodNameWithSignature)
	{
		String indexer = normalizeName(methodNameWithSignature);
		SeargeData srg = m_NameList.get(indexer);
		
		String methodName = "/" + normalizeName(methodNode.name);
		String methodSig = normalizeName(methodNode.desc);
		
		// BROKE: methodName=/a methodSig=(Lahu;)Z srgNameObf=amm/a srgSigObf=(Lahu;)Z
		//FMLLog.fine("MethodIs: methodName=" + methodName + " methodSig=" + methodSig + " srgNameObf=" + srg.objectNameObfuscated + " srgSigObf=" + srg.objectSignatureObfuscated);
			
		if (srg != null)
		{
			// srg.objectName = net/path/to/class/myMethodName
			// methodName = /myMethodName
						
			if (!isObfuscated)
				return srg.objectName.endsWith(methodName) && methodSig.equals(srg.objectSignature);
			else
				return srg.objectNameObfuscated.endsWith(methodName) && methodSig.equals(srg.objectSignatureObfuscated);
		}
		else
			return false;
	}
	
	public void RegisterSrgName(String data)
	{
		SeargeData item = new SeargeData(data);
		m_NameList.put(item.uniqueName, item);		
	}
	
	///////// private methods /////////
	
	private String normalizeName(String name)
	{
		return name.replace('.', '/');
	}		
}
