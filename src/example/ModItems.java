package example;

import arc.graphics.Color;
import mindustry.type.Item;

public class ModItems {
    public static Item industrialExplosives;
    public static Item tin;
    public static Item uranium;
    public static Item zinc;
    public static Item siliconSteel;
    public static Item gold;
    public static Item rock;
    public static Item hematite;
    public static Item ferrum;
    public static Item metaglassBottle;
    public static Item wateryMetaglassBottle;
    public static Item frostAlloy;
    public static Item canyonBattery;
    public static Item archipelagoBattery;
    public static Item heatConductionComponent;
    public static Item processor;
    public static Item bronze;
    public static void load2(){
        ModItems.hematite=new Item("hematite",Color.HSVtoRGB(11,35,49)){{
            hardness=3;
            cost=1.2f;
        }};
    }
    public static void load(){
        ModItems.tin=new Item("tin",Color.HSVtoRGB(233,16,44)){{
            cost=0.8f;
            hardness=1;
        }};
        ModItems.zinc=new Item("zinc",Color.HSVtoRGB(240,12,71)){{
            cost=0.8f;
            hardness=2;
        }};
        ModItems.gold=new Item("gold",Color.HSVtoRGB(50,93,100)){{
            cost=0.6f;
            hardness=1;
        }};
        bronze=new Item("bronze", Color.HSVtoRGB(23,57,77)){{
            cost=1.2f;
        }};
        ModItems.siliconSteel=new Item("silicon-steel",Color.HSVtoRGB(240,14,53)){{cost=1f;}};
    }
}
//