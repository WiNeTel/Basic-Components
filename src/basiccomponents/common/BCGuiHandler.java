package basiccomponents.common;

import basiccomponents.client.gui.GuiBatteryBox;
import basiccomponents.client.gui.GuiCoalGenerator;
import basiccomponents.client.gui.GuiElectricFurnace;
import basiccomponents.common.container.ContainerBatteryBox;
import basiccomponents.common.container.ContainerCoalGenerator;
import basiccomponents.common.container.ContainerElectricFurnace;
import basiccomponents.common.tileentity.TileEntityBatteryBox;
import basiccomponents.common.tileentity.TileEntityCoalGenerator;
import basiccomponents.common.tileentity.TileEntityElectricFurnace;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import cpw.mods.fml.common.network.IGuiHandler;

/**
 * Extend this class in your GuiHandler
 * 
 * Be sure to call super.getClientGuiElement and super.getServerGuiElement
 */
public class BCGuiHandler implements IGuiHandler
{
	@Override
	public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z)
	{
		TileEntity tileEntity = world.getBlockTileEntity(x, y, z);

		if (tileEntity != null)
		{
			switch (ID)
			{
				case 0:
					return new GuiBatteryBox(player.inventory, ((TileEntityBatteryBox) tileEntity));
				case 1:
					return new GuiCoalGenerator(player.inventory, ((TileEntityCoalGenerator) tileEntity));
				case 2:
					return new GuiElectricFurnace(player.inventory, ((TileEntityElectricFurnace) tileEntity));
			}
		}

		return null;
	}

	@Override
	public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z)
	{
		TileEntity tileEntity = world.getBlockTileEntity(x, y, z);

		if (tileEntity != null)
		{
			switch (ID)
			{
				case 0:
					return new ContainerBatteryBox(player.inventory, ((TileEntityBatteryBox) tileEntity));
				case 1:
					return new ContainerCoalGenerator(player.inventory, ((TileEntityCoalGenerator) tileEntity));
				case 2:
					return new ContainerElectricFurnace(player.inventory, ((TileEntityElectricFurnace) tileEntity));
			}
		}

		return null;
	}
}
