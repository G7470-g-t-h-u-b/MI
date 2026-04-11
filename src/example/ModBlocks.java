package example;

import mindustry.content.Fx;
import mindustry.content.Items;
import mindustry.content.Liquids;
import mindustry.type.Category;
import mindustry.type.ItemStack;
import mindustry.type.LiquidStack;
import mindustry.world.Block;
import mindustry.world.blocks.distribution.BufferedItemBridge;
import mindustry.world.blocks.distribution.Duct;
import mindustry.world.blocks.environment.RemoveWall;
import mindustry.world.blocks.heat.HeatConductor;
import mindustry.world.blocks.heat.HeatProducer;
import mindustry.world.blocks.power.BeamNode;
import mindustry.world.blocks.power.ConsumeGenerator;
import mindustry.world.blocks.power.HeaterGenerator;
import mindustry.world.blocks.power.NuclearReactor;
import mindustry.world.blocks.production.*;
import mindustry.world.blocks.storage.CoreBlock;

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
    }
}
//