package net.darkhax.wailaexamp;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.BlockOre;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.world.World;
import mcp.mobius.waila.api.IWailaConfigHandler;
import mcp.mobius.waila.api.IWailaDataAccessor;
import mcp.mobius.waila.api.IWailaDataProvider;
import mcp.mobius.waila.api.IWailaRegistrar;
import cpw.mods.fml.common.Optional;

/**
 * The Optional.Interface allows for an interface to be stripped from a class if the related mod is not
 * installed. Since we are using Waila the modid is Waila and the interface is IWailaDataProvider. Since
 * we can't directly reference the interface as it may not be installed, the first parameter is a string
 * which represents the class path for this interface.
 */
@Optional.Interface(iface = "mcp.mobius.waila.api.IWailaDataProvider", modid = "Waila")
public class WailaTileHandler implements IWailaDataProvider {

    /**
     * Although this is likely not necessary, you can also use the Optional.Method interface to mark a
     * method to be stripped if a mod is not detected. In this case we're doing this for all methods
     * which relate to Waila, so the modid is Waila.
     * 
     * The callbackRegister method is used by Waila to register this data provider. Note that inside this
     * method we initialize a new instance of this class, this instance is used for a lot of the
     * IWailaRegistrar methods require an instance of the data provider to work. This will also call the
     * constructor of this class, which can be used to help initialize other things. Alternatively you
     * can initialize things within this method as well.
     */
    @Optional.Method(modid = "Waila")
    public static void callbackRegister(IWailaRegistrar register) {

        WailaTileHandler instance = new WailaTileHandler();

        /**
         * In this example we will be adding two things to our new block we added earlier. When our data
         * provider is being registered by waila, we must also register which methods we are going to
         * use. This may seem redundant, however this allows us to make our provider targeted towards a
         * specific block. When registering a provided a class is required for the second parameter, this
         * class is the class of the block which you are targeting. This is instance sensitive so you can
         * use Block.class to target all blocks, or if all of your blocks extend a base block class then
         * that class can be used instead. Although the features being added in this example can work on
         * a global level, I will be making them specific to the new block we have added, and any blocks
         * which are an instance of BlockOre. This means our body provider will only be called when
         * looking at the new block or an ore block.
         */
        register.registerBodyProvider(instance, BlockWailaTestBlock.class);
        register.registerBodyProvider(instance, BlockOre.class);

        /**
         * If you plan on using nbt data you will also have to use registerNBTProvider for the block(s).
         * This method works like all the other data provider methods, however it is specifically for
         * syncing nbt data from the server to the client. To show off this feature we will be targeting
         * any block which extends BlockContainer so this should in theory apply to all tile entities. I
         * am also going to register our body provider for BlockContainer, so we can globally add data
         * for every TileEntity.
         */
        register.registerNBTProvider(instance, BlockContainer.class);
        register.registerBodyProvider(instance, BlockContainer.class);

        /**
         * Another important part to any waila plugin is configuration options. Configuration options are
         * handled using strings, and can be access wherever you have access to IWailaConfigHandler. When
         * registering a new config option there are two string parameters, the first represents a
         * category for the config option to be placed under, if one does not exist it will be generated.
         * The other represents the name of this option, this is used to retrieve the specific config
         * value, while also used as unlocalized text for the explination of this option in the
         * configuration menu. In this example mod I will be adding three features, one which shows how
         * much exp a block will drop when broken, another which shows if a block can be used as a beacon
         * block, and another which shows a tileEntity id. Config options have been added for all three.
         */
        register.addConfig("Example-Plugin", "option.example.showEXP");
        register.addConfig("Example-Plugin", "option.example.showBeacon");
        register.addConfig("Example-Plugin", "option.example.showTileID");
    }

    /**
     * This method allows you to change what ItemStack is used for the Waila tooltip. This is used by
     * Waila to make silverfish stone look like normal stone in the Waila hud. This is usually used as a
     * way to prevent people from cheating. It can also be used to correct block data. Note that there is
     * a bug with this method in that it will only affect the head of the tool tip. The body and tail
     * method will ignore any changes made here.
     */
    @Override
    @Optional.Method(modid = "Waila")
    public ItemStack getWailaStack(IWailaDataAccessor accessor, IWailaConfigHandler config) {

        return accessor.getStack();
    }

    /**
     * The Waila hud is devided up into three sections, this is to allow for data to be aranged in a nice
     * way. This method adds data to the header of the waila tool tip. This is where the game displays
     * the name of the block. The accessor is an object wrapper which contains all relevant data while
     * the config parameter allows you to take advantage of the ingame config gui.
     */
    @Override
    @Optional.Method(modid = "Waila")
    public List<String> getWailaHead(ItemStack itemStack, List<String> currenttip, IWailaDataAccessor accessor, IWailaConfigHandler config) {

        return currenttip;
    }

    /**
     * This method adds data to the body of the waila tool tip. This is where you should place the
     * majority of your data. The accessor is an object wrapper which contains all relevant data while
     * the config parameter allows you to take advantage of the ingame config gui.
     */
    @Override
    @Optional.Method(modid = "Waila")
    public List<String> getWailaBody(ItemStack itemStack, List<String> currenttip, IWailaDataAccessor accessor, IWailaConfigHandler config) {

        /**
         * In all data provider methods we are given access to several things. The first is itemStack
         * which is an ItemStack representation of the block being looked at. The second is currenttip
         * which is a list of string, each string in this list represents a line in the body of the
         * tooltip, this can be used to add (and remove) information to the tooltip. The third is
         * accessor which is a wrapper object in Waila which holds all relevant information. The final
         * thing we are given is access to the configuration. (note: this method is only registered to
         * work with blocks which extend BlockOre BlockContainer and BlockWailaTestBlock. It will only be
         * called when looking at those blocks.)
         */

        /**
         * The first thing we are going to add is the amount of exp dropped by a block. Since any block
         * can have this value, we only need to check if this feature is enabled in the configuration.
         * For this tutorial we are going to assume the player isn't using the fortune effect, although
         * the level of fortune can be obtained by grabbing the players held item. It's also worth noting
         * that getExpDrop() is not meant to be called for grabbing information. Although some blocks
         * like the test block provide a constant values, blocks like coal ore will have this value
         * change based on random factors.
         */
        if (config.getConfig("option.wawla.showEXP"))
            currenttip.add("EXP Dropped: " + accessor.getBlock().getExpDrop(accessor.getWorld(), accessor.getMetadata(), 0));

        /**
         * The second bit of data we are going to add is if this block is applicable for being a beacon
         * base. Keep in mind that this is only being called for our custom block, ore blocks and tile
         * entities, so blocks like emerald blocks will not trigger this method (to do this globally for
         * all blocks we would have registered with Block.class). It's also worth noting that the results
         * of this can change based on the position of the block, and the position of the beacon. In this
         * example we are using 0 for those values.
         */
        if (config.getConfig("option.wawla.showBeacon")) {

            String tip = (accessor.getBlock().isBeaconBase(accessor.getWorld(), 0, 0, 0, 0, 0, 0) ? (EnumChatFormatting.GREEN + "YES") : (EnumChatFormatting.RED + "NO"));
            currenttip.add("Beacon Block: " + tip);
        }

        /**
         * Finally we are going to print the tileid for all tile entities. For this we are going to check
         * the config, and if the TileEntity is not null.
         * 
         */

        if (config.getConfig("option.wawla.showTileID") && accessor.getTileEntity() != null)
            currenttip.add("Tile id: " + accessor.getNBTData().getString("id"));

        return currenttip;
    }

    /**
     * This method adds data to the tail of the waila tool tip. This is where the game displays the name
     * of the mod which adds this block to the game. The accessor is an object wrapper which contains all
     * relevant data while the config parameter allows you to take advantage of the ingame config gui.
     */
    @Override
    @Optional.Method(modid = "Waila")
    public List<String> getWailaTail(ItemStack itemStack, List<String> currenttip, IWailaDataAccessor accessor, IWailaConfigHandler config) {

        return currenttip;
    }

    /**
     * This method is used to sync data between server and client easily. The tag parameter is the nbt
     * tag which is provided when accessor.getNBTData() is called. Luckily for us, most of the time you
     * can simply use te.writeToNBT(tag) to take advantage of the built in nbt save function for tile
     * entities.
     */
    @Override
    public NBTTagCompound getNBTData(EntityPlayerMP player, TileEntity te, NBTTagCompound tag, World world, int x, int y, int z) {

        if (te != null)
            te.writeToNBT(tag);

        return tag;
    }
}