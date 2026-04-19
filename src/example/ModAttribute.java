package example;

import mindustry.content.Blocks;
import mindustry.content.Items;
import mindustry.world.meta.Attribute;

public class ModAttribute {
    public static Attribute metal;
    public static void load(){
        metal=Attribute.add("metal");
        Blocks.metalFloor.attributes.set(metal,1f);
        Blocks.metalFloorDamaged.attributes.set(metal,0.8f);
        Blocks.metalFloor2.attributes.set(metal,1f);
        Blocks.metalFloor3.attributes.set(metal,1f);
        Blocks.metalFloor4.attributes.set(metal,1f);
        Blocks.metalFloor5.attributes.set(metal,1f);

        Blocks.metalFloor.itemDrop=Items.scrap;
        Blocks.metalFloorDamaged.itemDrop=Items.scrap;
        Blocks.metalFloor2.itemDrop=Items.scrap;
        Blocks.metalFloor3.itemDrop=Items.scrap;
        Blocks.metalFloor4.itemDrop=Items.scrap;
        Blocks.metalFloor5.itemDrop=Items.scrap;
        Blocks.metalFloor.playerUnmineable=false;
        Blocks.metalFloorDamaged.playerUnmineable=false;
        Blocks.metalFloor2.playerUnmineable=false;
        Blocks.metalFloor3.playerUnmineable=false;
        Blocks.metalFloor4.playerUnmineable=false;
        Blocks.metalFloor5.playerUnmineable=false;
    }
}
