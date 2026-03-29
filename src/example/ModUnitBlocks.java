package example;

import mindustry.content.Items;
import mindustry.content.UnitTypes;
import mindustry.type.Category;
import mindustry.type.ItemStack;
import mindustry.type.UnitType;
import mindustry.world.blocks.units.Reconstructor;

public class ModUnitBlocks {
    public static Reconstructor secondaryModificationFactory;

    public static void load(){
        secondaryModificationFactory=new Reconstructor("secondary-modification-factory"){{
            requirements(Category.units, ItemStack.with(Items.copper,100,Items.lead,80,Items.silicon,50,Items.titanium,20));
            size=3;
            constructTime=60*20;
            consumePower(2.5f);
            consumeItems(ItemStack.with(Items.silicon,20,Items.titanium,40));
            upgrades.addAll(new UnitType[]{UnitTypes.horizon, ModUnits.raid}
                    ,new UnitType[]{UnitTypes.retusa,ModUnits.mysticSnail});
        }};
    }
}
