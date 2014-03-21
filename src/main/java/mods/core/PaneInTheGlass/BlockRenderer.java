package mods.core.PaneInTheGlass;

import org.lwjgl.opengl.GL11;

import net.minecraft.block.Block;
import net.minecraft.block.BlockPane;
import net.minecraft.block.BlockStainedGlassPane;
import net.minecraft.block.BlockStairs;
import net.minecraft.client.renderer.EntityRenderer;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import cpw.mods.fml.client.registry.ISimpleBlockRenderingHandler;

public class BlockRenderer implements ISimpleBlockRenderingHandler {

	public static int newBlockStairsRenderType;
	public static int renderPass = -2;

	// Used to access adjacent glass pane
	private static int adjacent_x, adjacent_y, adjacent_z;
	
	public BlockRenderer() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public void renderInventoryBlock(Block block, int metadata, int modelId,
			RenderBlocks renderer) {

		// Ripped from RenderBlocks.renderBlockAsItem

		int k;
		Tessellator tessellator = Tessellator.instance;
		
        for (k = 0; k < 2; ++k)
        {
            if (k == 0)
            {
            	renderer.setRenderBounds(0.0D, 0.0D, 0.0D, 1.0D, 1.0D, 0.5D);
            }

            if (k == 1)
            {
            	renderer.setRenderBounds(0.0D, 0.0D, 0.5D, 1.0D, 0.5D, 1.0D);
            }

            GL11.glTranslatef(-0.5F, -0.5F, -0.5F);
            tessellator.startDrawingQuads();
            tessellator.setNormal(0.0F, -1.0F, 0.0F);
            renderer.renderFaceYNeg(block, 0.0D, 0.0D, 0.0D, renderer.getBlockIconFromSide(block, 0));
            tessellator.draw();
            tessellator.startDrawingQuads();
            tessellator.setNormal(0.0F, 1.0F, 0.0F);
            renderer.renderFaceYPos(block, 0.0D, 0.0D, 0.0D, renderer.getBlockIconFromSide(block, 1));
            tessellator.draw();
            tessellator.startDrawingQuads();
            tessellator.setNormal(0.0F, 0.0F, -1.0F);
            renderer.renderFaceZNeg(block, 0.0D, 0.0D, 0.0D, renderer.getBlockIconFromSide(block, 2));
            tessellator.draw();
            tessellator.startDrawingQuads();
            tessellator.setNormal(0.0F, 0.0F, 1.0F);
            renderer.renderFaceZPos(block, 0.0D, 0.0D, 0.0D, renderer.getBlockIconFromSide(block, 3));
            tessellator.draw();
            tessellator.startDrawingQuads();
            tessellator.setNormal(-1.0F, 0.0F, 0.0F);
            renderer.renderFaceXNeg(block, 0.0D, 0.0D, 0.0D, renderer.getBlockIconFromSide(block, 4));
            tessellator.draw();
            tessellator.startDrawingQuads();
            tessellator.setNormal(1.0F, 0.0F, 0.0F);
            renderer.renderFaceXPos(block, 0.0D, 0.0D, 0.0D, renderer.getBlockIconFromSide(block, 5));
            tessellator.draw();
            GL11.glTranslatef(0.5F, 0.5F, 0.5F);
        }
        
	      //super.RenderInventoryBlock(renderer, block, metadata, modelId);		
	}

	@Override
	public boolean renderWorldBlock(IBlockAccess world, int x, int y, int z,
			Block block, int modelId, RenderBlocks renderer) {

		//TODO: if (!(block instanceof BlockStairs))			
				
        // Grab the adjacent block
        BlockStainedGlassPane glass = getEmbeddedGlass((BlockStairs)block, world, x, y, z);
        int glassMetaData = 0;
        boolean drewSomething = false;
             		
		// Use vanilla stairs
        drewSomething |= renderer.renderBlockStairs((BlockStairs)block, x, y, z);		
		
        if (glass != null && renderPass == 1) //MinecraftForgeClient.getRenderPass() == 1)
        {
        	glassMetaData = getEmbeddedGlassMetaData(world);
        
        	drewSomething |= renderBlockStairsGlassPane(renderer, glass, (BlockStairs)block, x, y, z, glassMetaData);
        }
        
		return drewSomething;
	}

	@Override
	public boolean shouldRender3DInInventory(int modelId) {
		return true;
	}

	@Override
	public int getRenderId() {
		// TODO Auto-generated method stub
		return newBlockStairsRenderType;
	}
	
    public boolean renderBlockStairsGlassPane(RenderBlocks renderBlocks, Block glassPaneBlock, BlockStairs stairBlock, int x, int y, int z, int metadata)
    {
    	// TODO: This function does not factor in block bounds.
    	// We need to alter that reality.
    	
        //int l = renderBlocks.blockAccess.getHeight();
        Tessellator tessellator = Tessellator.instance;
        //setBrightness seems to cause problems now that we're in ISimpleBlockRenderingHandler
        //We can ignore it as we'll just use the brightness of the stairs which we just rendered
        //tessellator.setBrightness(glassPaneBlock.getMixedBrightnessForBlock(renderBlocks.blockAccess, x, y, z));
        int i1 = glassPaneBlock.colorMultiplier(renderBlocks.blockAccess, x, y, z);
        float red = (float)(i1 >> 16 & 255) / 255.0F;
        float green = (float)(i1 >> 8 & 255) / 255.0F;
        float blue = (float)(i1 & 255) / 255.0F;

        if (EntityRenderer.anaglyphEnable)
        {
            float f3 = (red * 30.0F + green * 59.0F + blue * 11.0F) / 100.0F;
            float f4 = (red * 30.0F + green * 70.0F) / 100.0F;
            float f5 = (red * 30.0F + blue * 70.0F) / 100.0F;
            red = f3;
            green = f4;
            blue = f5;
        }

        tessellator.setColorOpaque_F(red, green, blue);
        boolean IsStainedGlassPane = glassPaneBlock instanceof BlockStainedGlassPane;
        IIcon iicon;
        IIcon iicon1;

        if (renderBlocks.hasOverrideBlockTexture())
        {
            iicon = renderBlocks.overrideBlockTexture;
            iicon1 = renderBlocks.overrideBlockTexture;
        }
        else
        {
            int j1 = metadata;
            iicon = renderBlocks.getBlockIconFromSideAndMetadata(glassPaneBlock, 0, j1);
            iicon1 = IsStainedGlassPane ? ((BlockStainedGlassPane)glassPaneBlock).func_150104_b(j1) : ((BlockPane)glassPaneBlock).func_150097_e();
        }

        double iconMinU = (double)iicon.getMinU();
        //double d0 = (double)iicon.getInterpolatedU(7.0D);
        //double d1 = (double)iicon.getInterpolatedU(9.0D);
        double iconMaxU = (double)iicon.getMaxU();
        double iconMinV = (double)iicon.getMinV();
        double iconMaxV = (double)iicon.getMaxV();
        double d5 = (double)iicon1.getInterpolatedU(7.0D);
        double d6 = (double)iicon1.getInterpolatedU(9.0D);
        double icon1MinV = (double)iicon1.getMinV();
        //double icon1MaxV = (double)iicon1.getMaxV();
        double icon1MidV = (double)iicon1.getInterpolatedV(8.0D);
        //double d9 = (double)iicon1.getInterpolatedV(7.0D);
        //double d10 = (double)iicon1.getInterpolatedV(9.0D);
        //double dmidV = (double)iicon.getInterpolatedV(7.0D);
        double minX = (double)x;
        double maxX = (double)(x + 1);
        double minZ = (double)z;
        double maxZ = (double)(z + 1);
        double paneThickXstart = (double)x + 0.5D - 0.0625D;
        double paneThickXend = (double)x + 0.5D + 0.0625D;
        double paneThickZstart = (double)z + 0.5D - 0.0625D;
        double paneThickZend = (double)z + 0.5D + 0.0625D;
        //boolean ConnectsToNorthBlock = IsStainedGlassPane ? ((BlockStainedGlassPane)glassPaneBlock).canPaneConnectToBlock(renderBlocks.blockAccess.getBlock(x, y, z - 1)) : ((BlockPane)glassPaneBlock).canPaneConnectToBlock(renderBlocks.blockAccess.getBlock(x, y, z - 1));
        //boolean ConnectsToSouthBlock = IsStainedGlassPane ? ((BlockStainedGlassPane)glassPaneBlock).canPaneConnectToBlock(renderBlocks.blockAccess.getBlock(x, y, z + 1)) : ((BlockPane)glassPaneBlock).canPaneConnectToBlock(renderBlocks.blockAccess.getBlock(x, y, z + 1));
        //boolean ConnectsToWestBlock = IsStainedGlassPane ? ((BlockStainedGlassPane)glassPaneBlock).canPaneConnectToBlock(renderBlocks.blockAccess.getBlock(x - 1, y, z)) : ((BlockPane)glassPaneBlock).canPaneConnectToBlock(renderBlocks.blockAccess.getBlock(x - 1, y, z));
        //boolean ConnectsToEastBlock = IsStainedGlassPane ? ((BlockStainedGlassPane)glassPaneBlock).canPaneConnectToBlock(renderBlocks.blockAccess.getBlock(x + 1, y, z)) : ((BlockPane)glassPaneBlock).canPaneConnectToBlock(renderBlocks.blockAccess.getBlock(x + 1, y, z));
        //double d19 = 0.001D;
        //double d20 = 0.999D;
        //double d21 = 0.001D;
        //boolean DoesNotConnectToAnyBlock = !ConnectsToNorthBlock && !ConnectsToSouthBlock && !ConnectsToWestBlock && !ConnectsToEastBlock;
        
        double minY = 0.001D;
        double maxY = 0.999D;
        double v0, v1, um, vm;

        int stairMeta = renderBlocks.blockAccess.getBlockMetadata(x,  y,  z);
        boolean IsInverted = ((stairMeta & 4) == 4);
        
        um = iicon.getInterpolatedU(8.0D);
        vm = iicon.getInterpolatedV(8.0D);
        
        // Set top/bottom half of texture based on stair inversion
        
        if (IsInverted)
        {
        	// Stairs are inverted. Only render the lower half of the glass pane.
        	
        	minY = 0.001D;
        	maxY = 0.5D;
        	
        	v0 = vm;
        	v1 = iconMaxV;
        }
        else
        {
        
        	minY = 0.5D;
        	maxY = 0.999D;
        	
        	v0 = iconMinV;
        	v1 = vm;
        }

        switch (stairMeta & 3)
        {
        case 0: // Notch on east face

        	// North side of glass pane
	        tessellator.addVertexWithUV(minX, (double)y + maxY, paneThickZend, iconMinU, v0); //d1 was iconMinU
	        tessellator.addVertexWithUV(minX, (double)y + minY, paneThickZend, iconMinU, v1); //d1 was iconMinU
	        tessellator.addVertexWithUV(maxX - 0.5D, (double)y + minY, paneThickZend, um, v1); // dmidV was iconMaxV
	        tessellator.addVertexWithUV(maxX - 0.5D, (double)y + maxY, paneThickZend, um, v0);
        	
	        // Top of glass
	        if (!IsInverted)
	        {
		        tessellator.addVertexWithUV(minX, (double)y + maxY, paneThickZend, d6, icon1MinV);
		        tessellator.addVertexWithUV(maxX - 0.5D, (double)y + maxY, paneThickZend, d6, icon1MidV);
		        tessellator.addVertexWithUV(maxX - 0.5D, (double)y + maxY, paneThickZstart, d5, icon1MidV);
		        tessellator.addVertexWithUV(minX, (double)y + maxY, paneThickZstart, d5, icon1MinV);
	        }
	        else
	        {
		        tessellator.addVertexWithUV(minX, (double)y + minY, paneThickZstart, d5, icon1MinV);	        	
		        tessellator.addVertexWithUV(maxX - 0.5D, (double)y + minY, paneThickZstart, d5, icon1MidV);
		        tessellator.addVertexWithUV(maxX - 0.5D, (double)y + minY, paneThickZend, d6, icon1MidV);
		        tessellator.addVertexWithUV(minX, (double)y + minY, paneThickZend, d6, icon1MinV);
	        }
	        
	        // South side of glass
	        tessellator.addVertexWithUV(maxX - 0.5D, (double)y + maxY, paneThickZstart, um, v0);
	        tessellator.addVertexWithUV(maxX - 0.5D, (double)y + minY, paneThickZstart, um, v1);
	        tessellator.addVertexWithUV(minX, (double)y + minY, paneThickZstart, iconMinU, v1);
	        tessellator.addVertexWithUV(minX, (double)y + maxY, paneThickZstart, iconMinU, v0);        
	        	        
        	break;
        case 1:	// Notch on west face

        	// North side of glass pane
	        tessellator.addVertexWithUV(minX + 0.5D, (double)y + maxY, paneThickZend, um, v0); //d1 was iconMinU
	        tessellator.addVertexWithUV(minX + 0.5D, (double)y + minY, paneThickZend, um, v1); //d1 was iconMinU
	        tessellator.addVertexWithUV(maxX, (double)y + minY, paneThickZend, iconMaxU, v1); // dmidV was iconMaxV
	        tessellator.addVertexWithUV(maxX, (double)y + maxY, paneThickZend, iconMaxU, v0);
	        
	        // Top of glass
	        if (!IsInverted)
	        {
		        tessellator.addVertexWithUV(minX + 0.5D, (double)y + maxY, paneThickZend, d6, icon1MidV);
		        tessellator.addVertexWithUV(maxX, (double)y + maxY, paneThickZend, d6, icon1MinV);
		        tessellator.addVertexWithUV(maxX, (double)y + maxY, paneThickZstart, d5, icon1MinV);
		        tessellator.addVertexWithUV(minX + 0.5D, (double)y + maxY, paneThickZstart, d5, icon1MidV);
	        }
	        else
	        {
		        tessellator.addVertexWithUV(minX + 0.5D, (double)y + minY, paneThickZstart, d5, icon1MidV);	        	
		        tessellator.addVertexWithUV(maxX, (double)y + minY, paneThickZstart, d5, icon1MinV);
		        tessellator.addVertexWithUV(maxX, (double)y + minY, paneThickZend, d6, icon1MinV);
		        tessellator.addVertexWithUV(minX + 0.5D, (double)y + minY, paneThickZend, d6, icon1MidV);
	        }	        
	        
	        // South side of glass
	        tessellator.addVertexWithUV(maxX, (double)y + maxY, paneThickZstart, iconMinU, v0);
	        tessellator.addVertexWithUV(maxX, (double)y + minY, paneThickZstart, iconMinU, v1);
	        tessellator.addVertexWithUV(minX + 0.5D, (double)y + minY, paneThickZstart, um, v1);
	        tessellator.addVertexWithUV(minX + 0.5D, (double)y + maxY, paneThickZstart, um, v0);        
	        
	        
        	break;        	
        case 2: // Notch on south face
        	
        	// Draw west face
            tessellator.addVertexWithUV(paneThickXstart, (double)y + maxY, minZ, iconMinU, v0);
            tessellator.addVertexWithUV(paneThickXstart, (double)y + minY, minZ, iconMinU, v1);
            tessellator.addVertexWithUV(paneThickXstart, (double)y + minY, maxZ - 0.5D, um, v1);
            tessellator.addVertexWithUV(paneThickXstart, (double)y + maxY, maxZ - 0.5D, um, v0);
            
            if (!IsInverted)
            {
	            tessellator.addVertexWithUV(paneThickXend, (double)y + maxY, minZ, d6, icon1MinV);
	            tessellator.addVertexWithUV(paneThickXstart, (double)y + maxY, minZ, d5, icon1MinV);
	            tessellator.addVertexWithUV(paneThickXstart, (double)y + maxY, maxZ - 0.5D, d5, icon1MidV);
	            tessellator.addVertexWithUV(paneThickXend, (double)y + maxY, maxZ - 0.5D, d6, icon1MidV);
            }
            else
            {
	            tessellator.addVertexWithUV(paneThickXend, (double)y + minY, maxZ - 0.5D, d6, icon1MidV);
	            tessellator.addVertexWithUV(paneThickXstart, (double)y + minY, maxZ - 0.5D, d5, icon1MidV);
	            tessellator.addVertexWithUV(paneThickXstart, (double)y + minY, minZ, d5, icon1MinV);
	            tessellator.addVertexWithUV(paneThickXend, (double)y + minY, minZ, d6, icon1MinV);            	
            }

        	// Draw east face
            tessellator.addVertexWithUV(paneThickXend, (double)y + maxY, maxZ - 0.5D, um, v0);
            tessellator.addVertexWithUV(paneThickXend, (double)y + minY, maxZ - 0.5D, um, v1);
            tessellator.addVertexWithUV(paneThickXend, (double)y + minY, minZ, iconMinU, v1);
            tessellator.addVertexWithUV(paneThickXend, (double)y + maxY, minZ, iconMinU, v0);
            
        	break;
        case 3: // Notch on north face

        	// Draw west face
            tessellator.addVertexWithUV(paneThickXstart, (double)y + maxY, minZ + 0.5D, um, v0);
            tessellator.addVertexWithUV(paneThickXstart, (double)y + minY, minZ + 0.5D, um, v1);
            tessellator.addVertexWithUV(paneThickXstart, (double)y + minY, maxZ, iconMinU, v1);
            tessellator.addVertexWithUV(paneThickXstart, (double)y + maxY, maxZ, iconMinU, v0);
            
            if (!IsInverted)
            {
	            tessellator.addVertexWithUV(paneThickXend, (double)y + maxY, minZ + 0.5D, d6, icon1MidV);
	            tessellator.addVertexWithUV(paneThickXstart, (double)y + maxY, minZ + 0.5D, d5, icon1MidV);
	            tessellator.addVertexWithUV(paneThickXstart, (double)y + maxY, maxZ, d5, icon1MinV);
	            tessellator.addVertexWithUV(paneThickXend, (double)y + maxY, maxZ, d6, icon1MinV);
            }
            else
            {
	            tessellator.addVertexWithUV(paneThickXend, (double)y + minY, maxZ, d6, icon1MinV);
	            tessellator.addVertexWithUV(paneThickXstart, (double)y + minY, maxZ, d5, icon1MinV);
	            tessellator.addVertexWithUV(paneThickXstart, (double)y + minY, minZ + 0.5D, d5, icon1MidV);
	            tessellator.addVertexWithUV(paneThickXend, (double)y + minY, minZ + 0.5D, d6, icon1MidV);            	
            }

        	// Draw east face
            tessellator.addVertexWithUV(paneThickXend, (double)y + maxY, maxZ, iconMinU, v0);
            tessellator.addVertexWithUV(paneThickXend, (double)y + minY, maxZ, iconMinU, v1);
            tessellator.addVertexWithUV(paneThickXend, (double)y + minY, minZ + 0.5D, um, v1);
            tessellator.addVertexWithUV(paneThickXend, (double)y + maxY, minZ + 0.5D, um, v0);
        	
        	break;
        }
        

        return true;
    }
	
	//
	//////////////////////////////// BlockStairs Methods ////////////////////////////////
	//
	
	// Should be worked into BlockStairs
	// If it returns non-null, use that block to render the glass
	public static BlockStainedGlassPane getEmbeddedGlass(BlockStairs stairBlock, IBlockAccess parBlockAccess, int x, int y, int z)	
	{
		// Temporary logic
		int metadata = parBlockAccess.getBlockMetadata(x, y, z);
		Block adjacent = null;
		
		adjacent_x = x;
		adjacent_y = y;
		adjacent_z = z;
		
		switch (metadata & 3)
		{
		case 0:
			adjacent_x--;
			break;
		case 1:
			adjacent_x++;
			break;
		case 2:
			adjacent_z--;
			break;
		case 3:
			adjacent_z++;
			break;
		default:
			return null;
		}

		adjacent = parBlockAccess.getBlock(adjacent_x, adjacent_y, adjacent_z);
		
		if (adjacent instanceof BlockStainedGlassPane)
			return (BlockStainedGlassPane)adjacent;
		else
		{
			adjacent_x = x;
			adjacent_z = z;		
			
			// Check top/bottom
			if ((metadata & 4) == 4)
				adjacent_y--;
			else
				adjacent_y++;
			
			adjacent = parBlockAccess.getBlock(adjacent_x, adjacent_y, adjacent_z);
			
			if (adjacent instanceof BlockStainedGlassPane)
				return (BlockStainedGlassPane)adjacent;
			else
				return null;
		}
	}

	// Should be worked into BlockStairs
	// If it returns non-null, use that block to render the glass
	public static int getEmbeddedGlassMetaData(IBlockAccess parBlockAccess) //BlockStairs stairBlock, IBlockAccess parBlockAccess, int x, int y, int z)	
	{		
		return parBlockAccess.getBlockMetadata(adjacent_x, adjacent_y, adjacent_z);
		
		/*switch (metadata & 3)
		{
		case 0:
			return parBlockAccess.getBlockMetadata(x - 1, y, z);

		case 1:
			return parBlockAccess.getBlockMetadata(x + 1, y, z);

		case 2:
			return parBlockAccess.getBlockMetadata(x, y, z - 1);

		case 3:
			return parBlockAccess.getBlockMetadata(x, y, z + 1);

		default:
			return 0;
		}*/				
	}
	
}
