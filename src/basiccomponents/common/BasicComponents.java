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
import universalelectricity.core.item.ItemElectric;
import universalelectricity.prefab.RecipeHelper;
import universalelectricity.prefab.TranslationHelper;
import universalelectricity.prefab.ore.OreGenBase;
import universalelectricity.prefab.ore.OreGenReplaceStone;
import universalelectricity.prefab.ore.OreGenerator;
import basiccomponents.client.RenderCopperWire;
import basiccomponents.common.block.BlockBCOre;
import basiccomponents.common.block.BlockBasicMachine;
import basiccomponents.common.block.BlockCopperWire;
import basiccomponents.common.item.ItemBase;
import basiccomponents.common.item.ItemBattery;
import basiccomponents.common.item.ItemBlockBCOre;
import basiccomponents.common.item.ItemBlockBasicMachine;
import basiccomponents.common.item.ItemBlockCopperWire;
import basiccomponents.common.item.ItemCircuit;
import basiccomponents.common.item.ItemInfiniteBattery;
import basiccomponents.common.item.ItemIngot;
import basiccomponents.common.item.ItemPlate;
import basiccomponents.common.item.ItemWrench;
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

	public static ItemElectric itemBattery;
	public static Item itemInfiniteBattery;
	public static Item itemWrench;
	public static Item itemCircuit;
	public static Item itemMotor;
	public static Item itemPlate;

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
	public static int BLOCK_ID_PREFIX = 3970;
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
			// Recipe Registry
			// Motor
			if (itemMotor != null)
			{
				GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(BasicComponents.itemMotor), new Object[] { "@!@", "!#!", "@!@", '!', "ingotSteel", '#', Item.ingotIron, '@', "copperWire" }));
			}

			// Wrench
			if (itemWrench != null)
			{
				GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(BasicComponents.itemWrench), new Object[] { " S ", " SS", "S  ", 'S', "ingotSteel" }));
			}

			if (blockMachine != null)
			{
				// Battery Box
				GameRegistry.addRecipe(new ShapedOreRecipe(OreDictionary.getOres("batteryBox").get(0), new Object[] { "SSS", "BBB", "SSS", 'B', ElectricItemHelper.getUncharged(BasicComponents.itemBattery), 'S', "ingotSteel" }));
				// Coal Generator
				GameRegistry.addRecipe(new ShapedOreRecipe(OreDictionary.getOres("coalGenerator").get(0), new Object[] { "MMM", "MOM", "MCM", 'M', "ingotSteel", 'C', BasicComponents.itemMotor, 'O', Block.furnaceIdle }));
				GameRegistry.addRecipe(new ShapedOreRecipe(OreDictionary.getOres("coalGenerator").get(0), new Object[] { "MMM", "MOM", "MCM", 'M', "ingotBronze", 'C', BasicComponents.itemMotor, 'O', Block.furnaceIdle }));
				// Electric Furnace
				GameRegistry.addRecipe(new ShapedOreRecipe(OreDictionary.getOres("electricFurnace").get(0), new Object[] { "SSS", "SCS", "SMS", 'S', "ingotSteel", 'C', BasicComponents.itemCircuit, 'M', BasicComponents.itemMotor }));
			}

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
			if (itemBattery != null)
			{
				// Battery
				GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(itemBattery), new Object[] { " T ", "TRT", "TCT", 'T', "ingotTin", 'R', Item.redstone, 'C', Item.coal }));
			}

			if (itemCircuit != null)
			{
				// Circuit
				GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(itemCircuit, 1, 0), new Object[] { "!#!", "#@#", "!#!", '@', "plateBronze", '#', Item.redstone, '!', "copperWire" }));
				GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(itemCircuit, 1, 0), new Object[] { "!#!", "#@#", "!#!", '@', "plateSteel", '#', Item.redstone, '!', "copperWire" }));
				GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(itemCircuit, 1, 1), new Object[] { "@@@", "#?#", "@@@", '@', Item.redstone, '?', Item.diamond, '#', BasicComponents.itemCircuit }));
				GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(itemCircuit, 1, 2), new Object[] { "@@@", "?#?", "@@@", '@', Item.ingotGold, '?', new ItemStack(BasicComponents.itemCircuit, 1, 1), '#', Block.blockLapis }));
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

	public static ItemStack registerCopperWire(int id)
	{
		if (blockCopperWire == null)
		{
			BasicComponents.CONFIGURATION.load();
			BasicComponents.blockCopperWire = new BlockCopperWire(BasicComponents.CONFIGURATION.getBlock("Copper_Wire", BasicComponents.BLOCK_ID_PREFIX + 1).getInt());
			GameRegistry.registerBlock(BasicComponents.blockCopperWire, ItemBlockCopperWire.class, "Copper Wire");
			OreDictionary.registerOre("copperWire", blockCopperWire);

			BasicComponents.CONFIGURATION.save();
		}

		return new ItemStack(blockCopperWire);
	}

	public static ItemStack registerMachines(int id)
	{
		if (blockMachine == null)
		{
			BasicComponents.CONFIGURATION.load();
			BasicComponents.blockMachine = new BlockBasicMachine(BasicComponents.CONFIGURATION.getBlock("Basic Machine", BasicComponents.BLOCK_ID_PREFIX + 4).getInt(), 0);
			GameRegistry.registerBlock(BasicComponents.blockMachine, ItemBlockBasicMachine.class, "Basic Machine");
			OreDictionary.registerOre("coalGenerator", ((BlockBasicMachine) BasicComponents.blockMachine).getCoalGenerator());
			OreDictionary.registerOre("batteryBox", ((BlockBasicMachine) BasicComponents.blockMachine).getBatteryBox());
			OreDictionary.registerOre("electricFurnace", ((BlockBasicMachine) BasicComponents.blockMachine).getElectricFurnace());
			BasicComponents.CONFIGURATION.save();
		}

		return new ItemStack(blockMachine);
	}

	public static ItemStack registerCircuits(int id)
	{
		if (itemCircuit == null)
		{
			BasicComponents.CONFIGURATION.load();
			itemCircuit = new ItemCircuit(BasicComponents.CONFIGURATION.getItem("Circuit", BasicComponents.ITEM_ID_PREFIX + 3).getInt(), 16);
			OreDictionary.registerOre("basicCircuit", new ItemStack(BasicComponents.itemCircuit, 1, 0));
			OreDictionary.registerOre("advancedCircuit", new ItemStack(BasicComponents.itemCircuit, 1, 1));
			OreDictionary.registerOre("eliteCircuit", new ItemStack(BasicComponents.itemCircuit, 1, 2));
			BasicComponents.CONFIGURATION.save();
		}

		return new ItemStack(itemCircuit);
	}

	public static ItemStack registerBattery(int id)
	{
		if (itemBattery == null)
		{
			BasicComponents.CONFIGURATION.load();
			itemBattery = new ItemBattery(BasicComponents.CONFIGURATION.getItem("Battery", BasicComponents.ITEM_ID_PREFIX + 1).getInt());
			OreDictionary.registerOre("battery", BasicComponents.itemBattery);
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
			OreDictionary.registerOre("batteryInfinite", itemInfiniteBattery);
			BasicComponents.CONFIGURATION.save();
		}

		return new ItemStack(itemInfiniteBattery);
	}

	public static ItemStack registerWrench(int id)
	{
		if (itemWrench == null)
		{
			BasicComponents.CONFIGURATION.load();
			itemWrench = new ItemWrench(BasicComponents.CONFIGURATION.getItem("Universal Wrench", BasicComponents.ITEM_ID_PREFIX + 2).getInt(), 20);
			OreDictionary.registerOre("wrench", itemWrench);
			BasicComponents.CONFIGURATION.save();
		}

		return new ItemStack(itemWrench);
	}

	public static ItemStack registerMotor(int id)
	{
		if (itemMotor == null)
		{
			BasicComponents.CONFIGURATION.load();
			itemMotor = new ItemBase("motor", BasicComponents.CONFIGURATION.getItem("Motor", BasicComponents.ITEM_ID_PREFIX + 14).getInt());
			OreDictionary.registerOre("motor", itemMotor);
			BasicComponents.CONFIGURATION.save();
		}

		return new ItemStack(itemMotor);
	}

	/**
	 * 
	 * @param itemName: Steel, Bronze Copper, Tin
	 * @return
	 */
	public static ItemStack registerPlates(int id, boolean require)
	{
		if (itemPlate == null)
		{
			BasicComponents.CONFIGURATION.load();
			itemPlate = new ItemPlate(BasicComponents.CONFIGURATION.getItem("Plates", BasicComponents.ITEM_ID_PREFIX + 13).getInt());
			OreDictionary.registerOre("ingotIron", Item.ingotIron);
			OreDictionary.registerOre("ingotGold", Item.ingotGold);

			for (int i = 0; i < ItemPlate.TYPES.length; i++)
			{
				String itemName = ItemPlate.TYPES[i];

				if (OreDictionary.getOres(itemName).size() <= 0 || require)
				{
					GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(itemPlate, 1, i), new Object[] { "!!", "!!", '!', itemName.replaceAll("plate", "ingot") }));

					if (itemName.equals("ingotIron"))
					{
						GameRegistry.addRecipe(new ShapelessOreRecipe(Item.ingotIron, new Object[] { new ItemStack(itemPlate, 1, i) }));
					}
					else if (itemName.equals("ingotGold"))
					{
						GameRegistry.addRecipe(new ShapelessOreRecipe(Item.ingotGold, new Object[] { new ItemStack(itemPlate, 1, i) }));
					}

					OreDictionary.registerOre(itemName, new ItemStack(itemPlate, 1, i));
				}
			}

			BasicComponents.CONFIGURATION.save();
		}

		return new ItemStack(itemPlate);
	}

	public static Item requireIngot(String name, int id)
	{
		init();
		
		try
		{
			Field field = ReflectionHelper.findField(BasicComponents.class, "item" + Character.toUpperCase(name.charAt(0)) + name.substring(1));
			ItemIngot f = (ItemIngot) field.get(null);

			if (f == null)
			{
				CONFIGURATION.load();
				field.set(null, new ItemIngot(name, id <= 0 ? getNextItemID() : id));
				OreDictionary.registerOre(name, new ItemStack((Item) field.get(null)));
				CONFIGURATION.save();
			}

			return (Item) field.get(null);
		}
		catch (Exception e)
		{
			FMLLog.severe("Failed to require ingot: " + name);
			e.printStackTrace();
		}

		return null;
	}

	public static Item requestIngot(String name, int id)
	{
		if (OreDictionary.getOres("ingotCopper").size() <= 0)
		{
			return requireIngot(name, id);
		}
		return null;
	}

	/**
	 * Call this after the corresponding ingot is registered.
	 * 
	 * @return
	 */
	public static ItemStack registerBronzeDust(int id, boolean require)
	{
		if (itemDustBronze == null)
		{
			String itemName = "dustBronze";

			if (OreDictionary.getOres(itemName).size() <= 0 || require)
			{
				BasicComponents.CONFIGURATION.load();
				itemDustBronze = new ItemBase(itemName, BasicComponents.CONFIGURATION.getItem("Bronze Dust", BasicComponents.ITEM_ID_PREFIX + 8).getInt());
				OreDictionary.registerOre(itemName, itemDustBronze);

				RecipeHelper.addRecipe(new ShapedOreRecipe(new ItemStack(BasicComponents.itemDustBronze), new Object[] { "!#!", '!', "ingotCopper", '#', "ingotTin" }), "Bronze Dust", BasicComponents.CONFIGURATION, true);

				if (OreDictionary.getOres("ingotBronze").size() > 0)
				{
					// Bronze
					GameRegistry.addSmelting(BasicComponents.itemDustBronze.itemID, OreDictionary.getOres("ingotBronze").get(0), 0.6f);
				}

				BasicComponents.CONFIGURATION.save();
			}

		}

		return new ItemStack(itemDustBronze);
	}

	/**
	 * Call this after the corresponding ingot is registered.
	 * 
	 * @return
	 */
	public static ItemStack registerSteelDust(int id, boolean require)
	{
		if (itemDustSteel == null)
		{
			String itemName = "dustSteel";

			if (OreDictionary.getOres(itemName).size() <= 0 || require)
			{
				BasicComponents.CONFIGURATION.load();

				itemDustSteel = new ItemBase(itemName, BasicComponents.CONFIGURATION.getItem("Steel Dust", BasicComponents.ITEM_ID_PREFIX + 9).getInt());
				OreDictionary.registerOre(itemName, itemDustSteel);
				RecipeHelper.addRecipe(new ShapedOreRecipe(new ItemStack(BasicComponents.itemDustSteel), new Object[] { " C ", "CIC", " C ", 'C', new ItemStack(Item.coal, 1, 1), 'I', Item.ingotIron }), "Steel Dust", BasicComponents.CONFIGURATION, true);
				RecipeHelper.addRecipe(new ShapedOreRecipe(new ItemStack(BasicComponents.itemDustSteel), new Object[] { " C ", "CIC", " C ", 'C', new ItemStack(Item.coal, 1, 0), 'I', Item.ingotIron }), "Steel Dust", BasicComponents.CONFIGURATION, true);

				if (OreDictionary.getOres("ingotSteel").size() > 0)
				{
					GameRegistry.addSmelting(BasicComponents.itemDustSteel.itemID, OreDictionary.getOres("ingotSteel").get(0), 0.8f);
				}

				BasicComponents.CONFIGURATION.save();
			}

		}

		return new ItemStack(itemDustBronze);
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
