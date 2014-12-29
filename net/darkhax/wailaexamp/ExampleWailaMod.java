package net.darkhax.wailaexamp;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLInterModComms;
import cpw.mods.fml.common.registry.GameRegistry;

@Mod(modid = ExampleWailaMod.MODID)
public class ExampleWailaMod {

    public static final String MODID = "WailaExample";
    public static final String VERSION = "1.0";

    public static Block block = new BlockWailaTestBlock();

    @EventHandler
    public void init(FMLInitializationEvent event) {

        GameRegistry.registerBlock(block, "example");

        /**
         * This is an IMC message which we are sending. IMC stands for InterModCommunication and is a way
         * for mods to send basic data back and forth to each other. Note that messages must be sent
         * after the pre-init stage. When sending an IMC message to waila, there are three strings we
         * need to provide. The first is the modid which is Waila, the second is a key which is a key for
         * this message, the key for registering with waila is register. The last string we need to
         * provide is the path to our register method. In this case the method is called callBackRegister
         * however the name can be changed. For this to work successfully, the method must be static and
         * accept IWailaRegistrar as a parameter.
         */
        FMLInterModComms.sendMessage("Waila", "register", "net.darkhax.wailaexamp.WailaTileHandler.callbackRegister");
    }
}