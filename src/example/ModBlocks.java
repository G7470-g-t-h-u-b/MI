package example;

import arc.graphics.Color;
import mindustry.content.Fx;
import mindustry.content.Items;
import mindustry.content.Liquids;
import mindustry.type.Category;
import mindustry.type.ItemStack;
import mindustry.type.LiquidStack;
import mindustry.world.Block;
import mindustry.world.blocks.defense.Wall;
import mindustry.world.blocks.distribution.BufferedItemBridge;
import mindustry.world.blocks.distribution.Duct;
import mindustry.world.blocks.environment.Floor;
import mindustry.world.blocks.environment.RemoveWall;
import mindustry.world.blocks.heat.HeatConductor;
import mindustry.world.blocks.heat.HeatProducer;
import mindustry.world.blocks.power.BeamNode;
import mindustry.world.blocks.power.ConsumeGenerator;
import mindustry.world.blocks.power.HeaterGenerator;
import mindustry.world.blocks.power.NuclearReactor;
import mindustry.world.blocks.production.*;
import mindustry.world.blocks.storage.CoreBlock;
import mindustry.world.draw.DrawDefault;
import mindustry.world.draw.DrawFlame;
import mindustry.world.draw.DrawMulti;

public class ModBlocks {
    public static RemoveWall explosive;
    public static Block laboratory;
    public static GenericCrafter siliconSteelMixer;
    public static GenericCrafter largeSiliconSteelMixer;
    public static Block oreGold;
    public static Block oreZinc;
    public static Block oreTin;
    public static Block oreUranium;
    public static Drill smallDrillBit;
    public static BeamDrill laserBeamDrill;
    public static BurstDrill percussionDrilling;
    public static GenericCrafter electrolyticSeparator;

    public static GenericCrafter glassAssemblyMachine;
    public static GenericCrafter waterTankFillingMachine;
    public static GenericCrafter waterDispenser;
    public static GenericCrafter bronzeSmelter;
    public static GenericCrafter blastFurnace;

    public static GenericCrafter rockDrilling;
    public static GenericCrafter rockCrusher;
    public static GenericCrafter highTemperatureMeltingFurnace;
    public static GenericCrafter highTemperatureSmeltingPlant;
    public static GenericCrafter assemblyMachine;
    public static GenericCrafter aromatizationMachine;
    public static HeatCrafter petroleumFractionatingTower;
    public static HeatConductor smallHeatTransmitter;
    public static HeatConductor heatTransmitter;
    public static GenericCrafter canyonBatteryCompressor;
    public static GenericCrafter archipelagoBatteryCompressor;
    public static GenericCrafter photoLithographyMachine;
    public static NuclearReactor largeThoriumReactor;
    public static HeatProducer electricHeater;
    public static HeaterGenerator fissionReactor;
    public static ConsumeGenerator mechanicalGenerator;
    public static ConsumeGenerator hydroelectricGenerator;
    public static ConsumeGenerator fluidThermalEnergyGenerator;
    public static ConsumeGenerator dieselGenerator;
    public static ConsumeGenerator canyonBatteryGenerator;
    public static ConsumeGenerator archipelagoBatteryGenerator;
    public static Separator highSpeedDisassembler;
    public static BeamNode laserEnergyNode;
    public static Duct itemTrack;
    public static Duct logisticsPipeline;
    public static BufferedItemBridge fastItemBridge;
    public static CoreBlock outpostCore;
    public static CoreBlock sentinelCore;
//    public static MultiFormulaFactory metalCrusher;
    public static StateFieldProjection overclockStateFieldProjection;
    public static void load1(){
        glassAssemblyMachine=new GenericCrafter("glass-assembly-machine"){{
            size=2;
            requirements(Category.crafting, ItemStack.with(Items.copper,30,Items.lead,30,Items.silicon,30,Items.metaglass,20));
            consumePower(0.5f);
            craftTime=60f;
            craftEffect=Fx.pulverize;
            consumeItems(ItemStack.with(Items.metaglass,1));
            outputItems=ItemStack.with(ModItems.metaglassBottle,1);
        }};
        waterTankFillingMachine=new GenericCrafter("water-tank-filling-machine"){{
            size=2;
            requirements(Category.crafting,ItemStack.with(Items.lead,30,Items.graphite,30,Items.metaglass,30));
            consumePower(0.25f);
            craftTime=30f;
            consumeItems(ItemStack.with(ModItems.metaglassBottle,1));
            consumeLiquids(LiquidStack.with(Liquids.water,0.8f));
            outputItems=ItemStack.with(ModItems.wateryMetaglassBottle,1);
        }};
        waterDispenser=new GenericCrafter("water-dispenser"){{
            size=2;
            requirements(Category.crafting,ItemStack.with(Items.lead,30,Items.graphite,30,Items.metaglass,30));
            consumePower(0.25f);
            craftTime=30f;
            consumeItem(ModItems.wateryMetaglassBottle,1);
            outputItems=ItemStack.with(ModItems.metaglassBottle,1);
            outputLiquids=LiquidStack.with(Liquids.water,0.8f);
        }};
        bronzeSmelter=new GenericCrafter("bronze-smelter"){{
            size=2;
            craftTime=60f;
            drawer = new DrawMulti(new DrawDefault(), new DrawFlame(Color.valueOf("ffef99")));
            consumeItems(ItemStack.with(Items.copper,3,ModItems.tin,1));
            consumePower(2f);
            outputItems=ItemStack.with(ModItems.bronze,4);
            requirements(Category.crafting,ItemStack.with(Items.copper,40,Items.lead,30,Items.graphite,40,Items.silicon,30));
        }};
        blastFurnace=new GenericCrafter("blast-furnace"){{
            size=3;
            craftTime=120;
            drawer=new DrawMulti(new DrawDefault(),new DrawFlame());
            consumeItems(ItemStack.with(ModItems.hematite,3,Items.coal,2));
            consumePower(3.5f);
            outputItems=ItemStack.with(ModItems.ferrum,1);
            requirements(Category.crafting,ItemStack.with(Items.lead,40,ModItems.siliconSteel,30,ModItems.bronze,40,Items.titanium,30));
        }};
    }
    public static Floor hematiteFloor;
    public static void loadFloor(){
        hematiteFloor=new Floor("hematite-floor"){{
            itemDrop=ModItems.hematite;
        }};
    }
    public static Wall bronzeWall;
    public static Wall bronzeWallLarge;
    public static Wall ironWall;
    public static Wall ironWallLarge;
    public static void loadWall(){
        bronzeWall=new Wall("bronze-wall"){{
            size=1;
            requirements(Category.defense,ItemStack.with(ModItems.bronze,6));
            health=480;
            armor=10;
        }};
        bronzeWallLarge=new Wall("bronze-wall-large"){{
            size=2;
            requirements(Category.defense,ItemStack.with(ModItems.bronze,24));
            health=1730;
            armor=14;
        }};
        ironWall=new Wall("iron-wall"){{
            size=1;
            requirements(Category.defense,ItemStack.with(ModItems.ferrum,6));
            health=740;
            armor=5;
        }};
        ironWallLarge=new Wall("iron-wall-large"){{
            size=2;
            requirements(Category.defense,ItemStack.with(ModItems.ferrum,24));
            health=3000;
            armor=12;
        }};
    }
}
//