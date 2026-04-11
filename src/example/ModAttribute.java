package example;

import mindustry.content.Blocks;
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
    }
}
