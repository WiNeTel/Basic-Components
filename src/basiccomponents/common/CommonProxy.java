package basiccomponents.common;

public class CommonProxy
{
	public void preInit()
	{
	}

	public void init()
	{
		BasicComponents.registerTileEntities();
	}
}
