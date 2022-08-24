package lithiumapiexample.lithiumapiexample;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.Material;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class LithiumApiExample implements ModInitializer {
    public static final String MOD_ID = "lithium_api_example";
    public static final DamagingBlock DAMAGING_BLOCK = new DamagingBlock(FabricBlockSettings.of(Material.METAL).strength(4.0f).noCollision());

    @Override
    public void onInitialize() {
        // register our damaging block
        Identifier id = new Identifier(MOD_ID, "damaging_block");

        Registry.register(Registry.BLOCK, id, DAMAGING_BLOCK);
        Registry.register(Registry.ITEM, id, new BlockItem(DAMAGING_BLOCK, new Item.Settings().group(ItemGroup.MISC)));
    }
}
