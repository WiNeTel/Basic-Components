package basiccomponents.common;

import java.io.File;
import java.lang.reflect.Field;
import java.util.ArrayList;

import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipes;
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
import basiccomponents.common.block.BlockBCOre;
import basiccomponents.common.block.BlockBasicMachine;
import basiccomponents.common.item.ItemBase;
import basiccomponents.common.item.ItemBattery;
import basiccomponents.common.item.ItemBlockBCOre;
import basiccomponents.common.item.ItemBlockBasicMachine;
import basiccomponents.common.item.ItemInfiniteBattery;
import basiccomponents.common.item.ItemIngot;
import basiccomponents.common.item.ItemPlate;
import basiccomponents.common.tileentity.TileEntityBatteryBox;
import basiccomponents.common.tileentity.TileEntityCoalGenerator;
import basiccomponents.common.tileentity.TileEntityCopperWire;
import basiccomponents.common.tileentity.TileEntityElectricFurnace;
import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.common.FMLLog;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.ReflectionHelper;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

/**
 * The main class for managing Basic Component items and blocks. Reference objects from this class
 * to add them to your recipes and such.
 * 
 * @author Calclavia
 */

public class BasicComponents
{
	public static final String NAME = "Basic Components";
	public static final String CHANNEL = "BasicComponents";

	public static final String RESOURCE_PATH = "/mods/basiccomponents/";

	/**
	 * The Universal Electricity configuration file.
	 */
	public static final Configuration CONFIGURATION = new Configuration(new File(Loader.instance().getConfigDir(), "BasicComponents.cfg"));

	public static CreativeTabs TAB;

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

	public static Block blockBasicOre;
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
	public static Item itemPlateStee;
	public static Item itemPlateIron;
	public static Item itemPlateGold;

	public static Item itemIngotCopper;
	public static Item itemIngotTin;
	public static Item itemIngotSteel;
	public static Item itemIngotBronze;

	public static Item itemDustSteel;
	public static Item itemDustBronze;

	public static OreGenBase copperOreGeneration;
	public static OreGenBase tinOreGeneration;

	public static boolean INITIALIZED = false;
	public static boolean REGISTER_RECIPES = false;

	private static boolean registeredTileEntities = false;
	private static boolean registeredTileEntityRenderers = false;

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
	 * Creates a specific Basic Component item. Require ingots first before dusts to register
	 * recipes correctly.
	 * 
	 * @param name - Name of the item: e.g ingotCopper, ingotSteel
	 * @param id - The specified ID of the item. Use 0 for a default value to be used.
	 * @return The Item class.
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

					GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(item), "!!", "!!", '!', name.replaceAll("plate", "ingot")));

					if (name.equals("plateIron"))
					{
						GameRegistry.addRecipe(new ShapelessOreRecipe(Item.ingotIron, item));
					}
					else if (name.equals("plateGold"))
					{
						GameRegistry.addRecipe(new ShapelessOreRecipe(Item.ingotGold, item));
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
				else
				{
					field.set(null, new ItemBase(name, id <= 0 ? getNextItemID() : id).setCreativeTab(CreativeTabs.tabMaterials));
					Item item = (Item) field.get(null);

					if (name.equals("basicCircuit"))
					{
						GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(item), "!#!", "#@#", "!#!", '@', "plateBronze", '#', Item.redstone, '!', "copperWire"));
						GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(item), "!#!", "#@#", "!#!", '@', "plateSteel", '#', Item.redstone, '!', "copperWire"));
					}
					else if (name.equals("advancedCircuit"))
					{
						GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(item), "@@@", "#?#", "@@@", '@', Item.redstone, '?', Item.diamond, '#', "basicCircuit"));
					}
					else if (name.equals("eliteCircuit"))
					{
						GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(item), "@@@", "?#?", "@@@", '@', Item.ingotGold, '?', "advancedCircuit", '#', Block.blockLapis));
					}
					else if (name.equals("wrench"))
					{
						GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(item), " S ", " SS", "S  ", 'S', "ingotSteel"));
					}
					else if (name.equals("motor"))
					{
						GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(item), "@!@", "!#!", "@!@", '!', "ingotSteel", '#', Item.ingotIron, '@', "copperWire"));
					}

				}

				Item item = (Item) field.get(null);
				OreDictionary.registerOre(name, item);
				CONFIGURATION.save();

				FMLLog.info("Successfully requested item: " + name);
				return item;
			}

			return f;
		}
		catch (Exception e)
		{
			FMLLog.severe("Failed to require ingot: " + name);
			e.printStackTrace();
		}

		return null;
	}

	public static Item requestItem(String name, int id)
	{
		if (OreDictionary.getOres(name).size() <= 0)
		{
			return requireItem(name, id);
		}

		return null;
	}

	/**
	 * Call this function in your mod init stage, after all the appropriate blocks are registered.
	 */
	public static void register(Object modInstance)
	{
		bcDependants.add(modInstance);

		if (!REGISTER_RECIPES)
		{
			/**
			 * Register Recipes
			 */
			// Copper
			if (blockBasicOre != null)
			{
				FurnaceRecipes.smelting().addSmelting(BasicComponents.blockBasicOre.blockID, 0, OreDictionary.getOres("ingotCopper").get(0), 0.7f);
			}

			// Tin
			if (blockBasicOre != null)
			{
				FurnaceRecipes.smelting().addSmelting(BasicComponents.blockBasicOre.blockID, 1, OreDictionary.getOres("ingotTin").get(0), 0.7f);
			}

			if (blockCopperWire != null)
			{
				// Sets the network status as active.
				UniversalElectricity.isNetworkActive = true;
				// Copper Wire
				GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(blockCopperWire, 6), new Object[] { "!!!", "@@@", "!!!", '!', Block.cloth, '@', "ingotCopper" }));
			}
		}

		REGISTER_RECIPES = true;
	}

	public static ItemStack registerOres(int id, boolean require)
	{
		BasicComponents.CONFIGURATION.load();

		if (blockBasicOre == null)
		{
			blockBasicOre = new BlockBCOre(BasicComponents.CONFIGURATION.getBlock("Ore", BasicComponents.BLOCK_ID_PREFIX + 0).getInt());
			GameRegistry.registerBlock(BasicComponents.blockBasicOre, ItemBlockBCOre.class, "Ore");
		}

		if (copperOreGeneration == null)
		{
			copperOreGeneration = new OreGenReplaceStone("Copper Ore", "oreCopper", new ItemStack(BasicComponents.blockBasicOre, 1, 0), 60, 23, 4).enable(BasicComponents.CONFIGURATION);
			OreGenerator.addOre(BasicComponents.copperOreGeneration);
		}

		if (tinOreGeneration == null)
		{
			tinOreGeneration = new OreGenReplaceStone("Tin Ore", "oreTin", new ItemStack(BasicComponents.blockBasicOre, 1, 1), 60, 19, 4).enable(BasicComponents.CONFIGURATION);
			OreGenerator.addOre(BasicComponents.tinOreGeneration);
		}

		BasicComponents.CONFIGURATION.save();

		return new ItemStack(blockBasicOre);
	}

	public static ItemStack registerBattery(int id)
	{
		if (itemBattery == null)
		{
			BasicComponents.CONFIGURATION.load();
			itemBattery = new ItemBattery(BasicComponents.CONFIGURATION.getItem("Battery", BasicComponents.ITEM_ID_PREFIX + 1).getInt());
			// Battery
			GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(itemBattery), " T ", "TRT", "TCT", 'T', "ingotTin", 'R', Item.redstone, 'C', Item.coal));
			OreDictionary.registerOre("battery", ElectricItemHelper.getUncharged(BasicComponents.itemBattery));
			BasicComponents.CONFIGURATION.save();
		}

		return new ItemStack(itemBattery);
	}

	public static ItemStack registerInfiniteBattery(int id)
	{
		if (itemInfiniteBattery == null)
		{
			BasicComponents.CONFIGURATION.load();
			itemInfiniteBattery = new ItemInfiniteBattery(BasicComponents.CONFIGURATION.getItem("Infinite Battery", BasicComponents.ITEM_ID_PREFIX + 0).getInt());
			OreDictionary.registerOre("batteryInfinite", ElectricItemHelper.getUncharged(itemInfiniteBattery));
			BasicComponents.CONFIGURATION.save();
		}

		return new ItemStack(itemInfiniteBattery);
	}

	public static ItemStack registerMachines(int id)
	{
		if (blockMachine == null)
		{
			BasicComponents.CONFIGURATION.load();
			BasicComponents.blockMachine = new BlockBasicMachine(BasicComponents.CONFIGURATION.getBlock("Basic Machine", BasicComponents.BLOCK_ID_PREFIX + 4).getInt(), 0);
			GameRegistry.registerBlock(BasicComponents.blockMachine, ItemBlockBasicMachine.class, "Basic Machine");
			// Battery Box
			GameRegistry.addRecipe(new ShapedOreRecipe(OreDictionary.getOres("batteryBox").get(0), new Object[] { "SSS", "BBB", "SSS", 'B', ElectricItemHelper.getUncharged(BasicComponents.itemBattery), 'S', "ingotSteel" }));
			// Coal Generator
			GameRegistry.addRecipe(new ShapedOreRecipe(OreDictionary.getOres("coalGenerator").get(0), new Object[] { "MMM", "MOM", "MCM", 'M', "ingotSteel", 'C', BasicComponents.itemMotor, 'O', Block.furnaceIdle }));
			GameRegistry.addRecipe(new ShapedOreRecipe(OreDictionary.getOres("coalGenerator").get(0), new Object[] { "MMM", "MOM", "MCM", 'M', "ingotBronze", 'C', BasicComponents.itemMotor, 'O', Block.furnaceIdle }));
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
			GameRegistry.registerTileEntity(TileEntityCopperWire.class, "UECopperWire");
			registeredTileEntities = true;
		}
	}

	/**
	 * Call this in your client proxy to bind copper wire renderer
	 * 
	 * @return
	 */
	@SideOnly(Side.CLIENT)
	public static void registerTileEntityRenderers()
	{
		if (!registeredTileEntityRenderers)
		{
			ClientRegistry.bindTileEntitySpecialRenderer(TileEntityCopperWire.class, new RenderCopperWire());
			registeredTileEntityRenderers = true;
		}
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
