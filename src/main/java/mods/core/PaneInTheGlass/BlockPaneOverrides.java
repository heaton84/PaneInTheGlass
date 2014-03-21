package mods.core.PaneInTheGlass;

import net.minecraft.block.Block;
import net.minecraft.block.BlockPane;
import net.minecraft.block.BlockStairs;
import net.minecraft.init.Blocks;

public class BlockPaneOverrides {

	public BlockPaneOverrides() {
		// TODO Auto-generated constructor stub
	}

    public static boolean canPaneConnectToBlock(Block p_150098_1_)
    {
        return p_150098_1_.func_149730_j()
        		/*|| p_150098_1_ == this      obviously won't work in a static */
        		|| p_150098_1_ == Blocks.glass
        		|| p_150098_1_ == Blocks.stained_glass
        		|| p_150098_1_ == Blocks.stained_glass_pane
        		|| p_150098_1_ instanceof BlockPane
        		|| p_150098_1_ instanceof BlockStairs;     // JRH: Should be able to connect to stairs now
    }	
}
