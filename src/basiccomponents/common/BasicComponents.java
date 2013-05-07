package basiccomponents.common;

import java.io.File;
import java.lang.reflect.Field;
import java.util.ArrayList;

import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.Configuration;
import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.oredict.ShapedOreRecipe;
import net.minecraftforge.oredict.ShapelessOreRecipe;
import universalelectricity.core.UniversalElectricity;
import universalelectricity.core.item.ElectricItemHelper;
import universalelectricity.prefab.TranslationHelper;
import universalelectricity.prefab.ore.OreGenBase;
import universalelectricity.prefab.ore.OreGenReplaceStone;
import universalelectricity.prefab.ore.OreGenerator;
import basiccomponents.client.RenderCopperWire;
import basiccomponents.common.block.BlockBase;
import basiccomponents.common.block.BlockBasicMachine;
import basiccomponents.common.block.BlockCopperWire;
import basiccomponents.common.item.ItemBase;
import basiccomponents.common.item.ItemBattery;
import basiccomponents.common.item.ItemBlockBasicMachine;
import basiccomponents.common.item.ItemBlockCopperWire;
import basiccomponents.common.item.ItemInfiniteBattery;
import basiccomponents.common.item.ItemIngot;
import basiccomponents.common.item.ItemPlate;
import basiccomponents.common.item.ItemWrench;
import basiccomponents.common.tileentity.TileEntityBatteryBox;
import basiccomponents.common.tileentity.TileEntityCoalGenerator;
import basiccomponents.common.tileentity.TileEntityCopperWire;
import basiccomponents.common.tileentity.TileEntityElectricFurnace;
import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.FMLLog;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.ReflectionHelper;

/**
 * The main class for managing Basic Component items and blocks. Reference objects from this class
 * to add them to your recipes and such.
 * 
 * @author Calclavia
 */

public class BasicComponents
{
	public static final String NAME = "Basic Components";
	public static String CHANNEL = "";

	public static final String RESOURCE_PATH = "/mods/basiccomponents/";

	/**
	 * The Universal Electricity configuration file.
	 */
	public static final Configuration CONFIGURATION = new Configuration(new File(Loader.instance().getConfigDir(), "BasicComponents.cfg"));

	@SidedProxy(clientSide = "basiccomponents.client.ClientProxy", serverSide = "basiccomponents.common.CommonProxy")
	public static CommonProxy proxy;

	public static final String TEXTURE_DIRECTORY = RESOURCE_PATH + "textures/";
	public static final String GUI_DIRECTORY = TEXTURE_DIRECTORY + "gui/";
	public static final String BLOCK_TEXTURE_DIRECTORY = TEXTURE_DIRECTORY + "blocks/";
	public static final String ITEM_TEXTURE_DIRECTORY = TEXTURE_DIRECTORY + "items/";
	public static final String MODEL_TEXTURE_DIRECTORY = TEXTURE_DIRECTORY + "models/";

	public static final String TEXTURE_NAME_PREFIX = "basiccomponents:";

	public static final String LANGUAGE_PATH = RESOURCE_PATH + "languages/";
	private static final String[] LANGUAGES_SUPPORTED = new String[] { "en_US", "zh_CN", "es_ES", "it_IT", "nl_NL", "de_DE" };

	public static Block blockOreCopper;
	public static Block blockOreTin;
	public static Block blockCopperWire;
	public static Block blockMachine;

	public static Item itemBattery;
	public static Item itemInfiniteBattery;
	public static Item itemWrench;
	public static Item itemMotor;

	public static Item itemCircuitBasic;
	public static Item itemCircuitAdvanced;
	public static Item itemCircuitElite;

	public static Item itemPlateCopper;
	public static Item itemPlateTin;
	public static Item itemPlateBronze;
	public static Item itemPlateSteel;
	public static Item itemPlateIron;
	public static Item itemPlateGold;

	public static Item itemIngotCopper;
	public static Item itemIngotTin;
	public static Item itemIngotSteel;
	public static Item itemIngotBronze;

	public static Item itemDustSteel;
	public static Item itemDustBronze;

	public static OreGenBase generationOreCopper, generationOreTin;

	public static boolean INITIALIZED = false;

	private static boolean registeredTileEntities = false;

	public static final ArrayList bcDependants = new ArrayList();

	/**
	 * Auto-incrementing configuration IDs. Use this to make sure no config ID is the same.
	 */
	public static final int BLOCK_ID_PREFIX = 3970;
	public static final int ITEM_ID_PREFIX = 13970;

	private static int NEXT_BLOCK_ID = BLOCK_ID_PREFIX;
	private static int NEXT_ITEM_ID = ITEM_ID_PREFIX;

	public static int getNextBlockID()
	{
		NEXT_BLOCK_ID++;
		return NEXT_BLOCK_ID;
	}

	public static int getNextItemID()
	{
		NEXT_ITEM_ID++;
		return NEXT_ITEM_ID;
	}

	public static void init()
	{
		if (!INITIALIZED)
		{
			System.out.println("Basic Components Loaded: " + TranslationHelper.loadLanguages(BasicComponents.LANGUAGE_PATH, LANGUAGES_SUPPORTED) + " Languages.");
			INITIALIZED = true;
		}
	}

	/**
	 * Call all of this in Init stage. Use "requestItem" or "requestBlock" instead to make it so
	 * then if another mod adds in a item with the same name, it will use that mod's item instead.
	 * 
	 * Creates a specific Basic Component item/Block.
	 * 
	 * If you want correct recipes, make sure you register required items in the following order:
	 * 
	 * Ingot, Ores, Dust, Plate, Copper Wire, Circuits, Motor, Wrench
	 * 
	 * @param name - Name of the item: e.g ingotCopper, ingotSteel
	 * @param id - The specified ID of the item. Use 0 for a default value to be used.
	 * @return The Item/Block class.
	 */
	public static Item requireItem(String name, int id)
	{
		init();

		try
		{
			Field field = ReflectionHelper.findField(BasicComponents.class, "item" + Character.toUpperCase(name.charAt(0)) + name.substring(1));
			Item f = (Item) field.get(null);

			if (f == null)
			{
				CONFIGURATION.load();

				if (name.contains("ingot"))
				{
					field.set(null, new ItemIngot(name, id <= 0 ? getNextItemID() : id));
				}
				else if (name.contains("plate"))
				{
					field.set(null, new ItemPlate(name, id <= 0 ? getNextItemID() : id));
					Item item = (Item) field.get(null);

					String ingotName = name.replaceAll("plate", "ingot");
					GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(item), "!!", "!!", '!', ingotName));

					Item itemIngot = null;

					if (OreDictionary.getOres(ingotName).size() > 0)
					{
						itemIngot = OreDictionary.getOres(ingotName).get(0).getItem();
					}

					if (name.equals("plateIron"))
					{
						itemIngot = Item.ingotIron;
					}
					else if (name.equals("plateGold"))
					{
						itemIngot = Item.ingotGold;
					}

					if (itemIngot != null)
					{
						GameRegistry.addRecipe(new ShapelessOreRecipe(new ItemStack(itemIngot, 4), item));
					}
				}
				else if (name.contains("dust"))
				{
					field.set(null, new ItemBase(name, id <= 0 ? getNextItemID() : id).setCreativeTab(CreativeTabs.tabMaterials));
					Item item = (Item) field.get(null);

					if (name.equals("dustBronze"))
					{
						GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(item), "!#!", '!', "ingotCopper", '#', "ingotTin"));

						if (OreDictionary.getOres("ingotBronze").size() > 0)
						{
							GameRegistry.addSmelting(item.itemID, OreDictionary.getOres("ingotBronze").get(0), 0.6f);
						}
					}
					else if (name.equals("dustSteel"))
					{
						GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(item), " C ", "CIC", " C ", 'I', Item.ingotIron, 'C', Item.coal));

						if (OreDictionary.getOres("ingotSteel").size() > 0)
						{
							GameRegistry.addSmelting(item.itemID, OreDictionary.getOres("ingotSteel").get(0), 0.8f);
						}
					}
				}
				else if (name.equals("wrench"))
				{
					field.set(null, new ItemWrench(id <= 0 ? getNextItemID() : id));
					Item item = (Item) field.get(null);

					if (OreDictionary.getOres("ingotSteel").size() > 0)
					{
						GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(item), " S ", " SS", "S  ", 'S', "ingotSteel"));
					}
					else
					{
						GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(item), " S ", " SS", "S  ", 'S', Item.ingotIron));
					}
				}
				else
				{
					field.set(null, new ItemBase(name, id <= 0 ? getNextItemID() : id).setCreativeTab(CreativeTabs.tabMaterials));
					Item item = (Item) field.get(null);

					if (name.equals("circuitBasic"))
					{
						if (OreDictionary.getOres("copperWire").size() > 0)
						{
							GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(item), "!#!", "#@#", "!#!", '@', "plateBronze", '#', Item.redstone, '!', "copperWire"));
							GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(item), "!#!", "#@#", "!#!", '@', "plateSteel", '#', Item.redstone, '!', "copperWire"));
						}
						else
						{
							GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(item), "!#!", "#@#", "!#!", '@', "plateBronze", '#', Item.redstone, '!', Block.redstoneComparatorIdle));
							GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(item), "!#!", "#@#", "!#!", '@', "plateSteel", '#', Item.redstone, '!', Block.redstoneComparatorIdle));
						}
					}
					else if (name.equals("circuitAdvanced"))
					{
						GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(item), "@@@", "#?#", "@@@", '@', Item.redstone, '?', Item.diamond, '#', "circuitBasic"));
					}
					else if (name.equals("circuitElite"))
					{
						GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(item), "@@@", "?#?", "@@@", '@', Item.ingotGold, '?', "circuitAdvanced", '#', Block.blockLapis));
					}
					else if (name.equals("motor"))
					{
						if (OreDictionary.getOres("copperWire").size() > 0)
						{
							GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(item), "@!@", "!#!", "@!@", '!', "ingotSteel", '#', Item.ingotIron, '@', "copperWire"));
						}
						else
						{
							GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(item), "@!@", "!#!", "@!@", '!', "ingotSteel", '#', Item.ingotIron, '@', Block.redstoneComparatorIdle));
						}
					}
				}

				Item item = (Item) field.get(null);
				OreDictionary.registerOre(name, item);
				CONFIGURATION.save();

				FMLLog.info("Basic Components: Successfully requested item: " + name);
				return item;
			}

			return f;
		}
		catch (Exception e)
		{
			FMLLog.severe("Basic Components: Failed to require ingot: " + name);
			e.printStackTrace();
		}

		return null;
	}

	public static Item requestItem(String name, int id)
	{
		if (OreDictionary.getOres(name).size() <= 0 && !(name.equals("wrench") && Loader.isModLoaded("BuildCraft|Core")))
		{
			return requireItem(name, id);
		}

		FMLLog.info("Basic Components: " + name + " already exists in Ore Dictionary, using the ore instead.");

		if (OreDictionary.getOres(name).size() > 0)
		{
			return OreDictionary.getOres(name).get(0).getItem();
		}

		return null;
	}

	public static Block requireBlock(String name, int id)
	{
		init();

		try
		{
			Field field = ReflectionHelper.findField(BasicComponents.class, "block" + Character.toUpperCase(name.charAt(0)) + name.substring(1));
			Block f = (Block) field.get(null);

			if (f == null)
			{
				CONFIGURATION.load();

				if (name.equals("copperWire"))
				{
					field.set(null, new BlockCopperWire(id <= 0 ? getNextBlockID() : id));
					GameRegistry.registerBlock((Block) field.get(null), ItemBlockCopperWire.class, name);

					GameRegistry.registerTileEntity(TileEntityCopperWire.class, "copperWire");

					if (FMLCommonHandler.instance().getEffectiveSide().isClient())
					{
						ClientRegistry.bindTileEntitySpecialRenderer(TileEntityCopperWire.class, new RenderCopperWire());
					}

					GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(blockCopperWire, 6), new Object[] { "WWW", "CCC", "WWW", 'W', Block.cloth, 'C', "ingotCopper" }));

					UniversalElectricity.isNetworkActive = true;
				}
				else if (name.contains("ore"))
				{
					field.set(null, new BlockBase(name, id <= 0 ? getNextBlockID() : id));
					Block block = (Block) field.get(null);
					GameRegistry.registerBlock(block, name);

					String ingotName = name.replaceAll("ore", "ingot");

					if (OreDictionary.getOres(ingotName).size() > 0)
					{
						GameRegistry.addSmelting(block.blockID, OreDictionary.getOres(ingotName).get(0), 0.6f);
					}

					Field generationField = ReflectionHelper.findField(BasicComponents.class, "generation" + Character.toUpperCase(name.charAt(0)) + name.substring(1));
					generationField.set(null, new OreGenReplaceStone(name, name, new ItemStack(block), 60, 22, 4).enable(BasicComponents.CONFIGURATION));
					OreGenerator.addOre((OreGenReplaceStone) generationField.get(null));
				}

				Block block = (Block) field.get(null);
				OreDictionary.registerOre(name, block);
				CONFIGURATION.save();

				FMLLog.info("Basic Components: Successfully requested block: " + name);
				return block;
			}

			return f;
		}
		catch (Exception e)
		{
			FMLLog.severe("Basic Components: Failed to require ingot: " + name);
			e.printStackTrace();
		}

		return null;
	}

	public static Block requestBlock(String name, int id)
	{
		if (OreDictionary.getOres(name).size() <= 0)
		{
			return requireBlock(name, id);
		}

		FMLLog.info("Basic Components: " + name + " already exists in Ore Dictionary, using the ore instead.");

		if (OreDictionary.getOres(name).get(0).getItem() instanceof ItemBlock)
		{
			return Block.blocksList[((ItemBlock) OreDictionary.getOres(name).get(0).getItem()).getBlockID()];
		}

		return null;
	}

	public static ItemStack requireBattery(int id)
	{
		if (itemBattery == null)
		{
			BasicComponents.CONFIGURATION.load();
			itemBattery = new ItemBattery(BasicComponents.CONFIGURATION.getItem("Battery", id <= 0 ? getNextItemID() : id).getInt(id));
			// Battery
			GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(itemBattery), " T ", "TRT", "TCT", 'T', "ingotTin", 'R', Item.redstone, 'C', Item.coal));
			OreDictionary.registerOre("battery", ElectricItemHelper.getUncharged(BasicComponents.itemBattery));
			BasicComponents.CONFIGURATION.save();
		}

		return new ItemStack(itemBattery);
	}

	public static ItemStack requireInfiniteBattery(int id)
	{
		if (itemInfiniteBattery == null)
		{
			BasicComponents.CONFIGURATION.load();
			itemInfiniteBattery = new ItemInfiniteBattery(BasicComponents.CONFIGURATION.getItem("Infinite Battery", id <= 0 ? getNextItemID() : id).getInt(id));
			OreDictionary.registerOre("batteryInfinite", ElectricItemHelper.getUncharged(itemInfiniteBattery));
			BasicComponents.CONFIGURATION.save();
		}

		return new ItemStack(itemInfiniteBattery);
	}

	public static ItemStack requireMachines(int id)
	{
		if (blockMachine == null)
		{
			BasicComponents.CONFIGURATION.load();
			BasicComponents.blockMachine = new BlockBasicMachine(BasicComponents.CONFIGURATION.getBlock("Basic Machine", id <= 0 ? getNextBlockID() : id).getInt(id), 0);
			GameRegistry.registerBlock(BasicComponents.blockMachine, ItemBlockBasicMachine.class, "Basic Machine");
			// Battery Box
			GameRegistry.addRecipe(new ShapedOreRecipe(OreDictionary.getOres("batteryBox").get(0), new Object[] { "SSS", "BBB", "SSS", 'B', "battery", 'S', "ingotSteel" }));
			// Coal Generator
			GameRegistry.addRecipe(new ShapedOreRecipe(OreDictionary.getOres("coalGenerator").get(0), new Object[] { "MMM", "MOM", "MCM", 'M', "ingotSteel", 'C', "motor", 'O', Block.furnaceIdle }));
			GameRegistry.addRecipe(new ShapedOreRecipe(OreDictionary.getOres("coalGenerator").get(0), new Object[] { "MMM", "MOM", "MCM", 'M', "ingotBronze", 'C', "motor", 'O', Block.furnaceIdle }));
			// Electric Furnace
			GameRegistry.addRecipe(new ShapedOreRecipe(OreDictionary.getOres("electricFurnace").get(0), new Object[] { "SSS", "SCS", "SMS", 'S', "ingotSteel", 'C', "circuitAdvanced", 'M', "motor" }));

			OreDictionary.registerOre("coalGenerator", ((BlockBasicMachine) BasicComponents.blockMachine).getCoalGenerator());
			OreDictionary.registerOre("batteryBox", ((BlockBasicMachine) BasicComponents.blockMachine).getBatteryBox());
			OreDictionary.registerOre("electricFurnace", ((BlockBasicMachine) BasicComponents.blockMachine).getElectricFurnace());
			BasicComponents.CONFIGURATION.save();
		}

		return new ItemStack(blockMachine);
	}

	/**
	 * Call this to register Tile Entities
	 * 
	 * @return
	 */
	public static void registerTileEntities()
	{
		if (!registeredTileEntities)
		{
			GameRegistry.registerTileEntity(TileEntityBatteryBox.class, "UEBatteryBox");
			GameRegistry.registerTileEntity(TileEntityCoalGenerator.class, "UECoalGenerator");
			GameRegistry.registerTileEntity(TileEntityElectricFurnace.class, "UEElectricFurnace");
			registeredTileEntities = true;
		}
	}

	/**
	 * Called when all items are registered. Only call once per mod.
	 */
	public static void register(Object mod, String channel)
	{
		bcDependants.add(mod);
		CHANNEL = channel;

		if (registeredTileEntities && blockMachine != null)
		{
			NetworkRegistry.instance().registerGuiHandler(mod, new BCGuiHandler());
		}

		/**
		 * Register Smelting Recipes
		 * 
		 * Registers recipes that are dependent on order.
		 */

		if (itemDustBronze != null)
		{
			if (OreDictionary.getOres("ingotBronze").size() > 0)
			{
				GameRegistry.addSmelting(itemDustBronze.itemID, OreDictionary.getOres("ingotBronze").get(0), 0.6f);
			}
		}

		if (itemDustSteel != null)
		{
			if (OreDictionary.getOres("ingotSteel").size() > 0)
			{
				GameRegistry.addSmelting(itemDustSteel.itemID, OreDictionary.getOres("ingotSteel").get(0), 0.6f);
			}
		}

		// Copper
		if (blockOreCopper != null)
		{
			GameRegistry.addSmelting(blockOreCopper.blockID, OreDictionary.getOres("ingotCopper").get(0), 0.7f);
		}

		// Tin
		if (blockOreTin != null)
		{
			GameRegistry.addSmelting(blockOreTin.blockID, OreDictionary.getOres("ingotTin").get(0), 0.7f);
		}
	}

	/**
	 * Requests all items in Basic Components
	 */
	public static void requestAll()
	{
		BasicComponents.requestItem("ingotCopper", 0);
		BasicComponents.requestItem("ingotTin", 0);

		BasicComponents.requestBlock("oreCopper", 0);
		BasicComponents.requestBlock("oreTin", 0);

		BasicComponents.requestItem("ingotSteel", 0);
		BasicComponents.requestItem("dustSteel", 0);
		BasicComponents.requestItem("plateSteel", 0);

		BasicComponents.requestItem("ingotBronze", 0);
		BasicComponents.requestItem("dustBronze", 0);
		BasicComponents.requestItem("plateBronze", 0);

		BasicComponents.requestItem("plateCopper", 0);
		BasicComponents.requestItem("plateTin", 0);
		BasicComponents.requestItem("plateIron", 0);
		BasicComponents.requestItem("plateGold", 0);

		BasicComponents.requestBlock("copperWire", 0);

		BasicComponents.requestItem("circuitBasic", 0);
		BasicComponents.requestItem("circuitAdvanced", 0);
		BasicComponents.requestItem("circuitElite", 0);

		BasicComponents.requestItem("motor", 0);

		BasicComponents.requestItem("wrench", 0);

		requireBattery(0);
		requireInfiniteBattery(0);
		requireMachines(0);
	}

	public static Object getFirstDependant()
	{
		if (bcDependants.size() > 0)
		{
			return bcDependants.get(0);
		}

		return null;
	}
}
