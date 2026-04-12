package example;

import arc.*;
import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Fill;
import arc.graphics.g2d.Lines;
import arc.math.Angles;
import arc.math.Interp;
import arc.util.*;
import mindustry.content.*;
import mindustry.entities.Effect;
import mindustry.entities.UnitSorts;
import mindustry.entities.abilities.*;
import mindustry.entities.bullet.*;
import mindustry.entities.effect.ExplosionEffect;
import mindustry.entities.effect.MultiEffect;
import mindustry.entities.part.*;
import mindustry.entities.pattern.ShootAlternate;
import mindustry.entities.pattern.ShootPattern;
import mindustry.entities.pattern.ShootSpread;
import mindustry.game.EventType.*;
import mindustry.gen.*;
import mindustry.graphics.Drawf;
import mindustry.graphics.Pal;
import mindustry.graphics.g3d.*;
import mindustry.maps.planet.SerpuloPlanetGenerator;
import mindustry.mod.*;
import mindustry.type.*;
import mindustry.type.unit.ErekirUnitType;
import mindustry.type.unit.TankUnitType;
import mindustry.type.weapons.PointDefenseBulletWeapon;
import mindustry.ui.dialogs.*;
import mindustry.world.blocks.defense.turrets.ItemTurret;
import mindustry.world.blocks.defense.turrets.LiquidTurret;
import mindustry.world.blocks.defense.turrets.PowerTurret;
import mindustry.world.blocks.distribution.BufferedItemBridge;
import mindustry.world.blocks.distribution.Duct;
import mindustry.world.blocks.environment.OreBlock;
import mindustry.world.blocks.environment.RemoveWall;
import mindustry.world.blocks.heat.HeatConductor;
import mindustry.world.blocks.heat.HeatProducer;
import mindustry.world.blocks.power.BeamNode;
import mindustry.world.blocks.power.ConsumeGenerator;
import mindustry.world.blocks.power.HeaterGenerator;
import mindustry.world.blocks.power.NuclearReactor;
import mindustry.world.blocks.production.*;
import mindustry.world.blocks.storage.CoreBlock;
import mindustry.world.consumers.ConsumeItemRadioactive;
import mindustry.world.draw.*;
import mindustry.world.meta.BuildVisibility;


import static mindustry.content.TechTree.*;
import static mindustry.type.ItemStack.with;

public class ExampleJavaMod extends Mod{

    public ExampleJavaMod(){
        Log.info("Loaded ExampleJavaMod constructor.");

        //listen for game load event
        Events.on(ClientLoadEvent.class, e -> {
            //show dialog upon startup
            Time.runTask(10f, () -> {
                BaseDialog dialog = new BaseDialog("frog");
                dialog.cont.add("behold").row();
                //mod sprites are prefixed with the mod name (this mod is called 'example-java-mod' in its config)
                dialog.cont.image(Core.atlas.find("g7470mod-1-frog")).pad(20f).row();
                dialog.cont.button("I see", dialog::hide).size(100f, 50f);
                dialog.show();
            });
        });
    }

    @Override
    public void loadContent(){
        Log.info("Loading some example content.");
//        Sounds.load();


        ModFx.explosionEffect1=new ExplosionEffect(){{
            waveColor=Pal.gray;
            smokeColor=Color.gray;
            sparkColor=Pal.lighterOrange;
            waveRadBase=waveStroke=6;
            sparkRad=2;
            sparkLen=6;
            sparkStroke=5;
            smokeSizeBase=1;
            smokeSize=1.7f;
            waveRad=30;
            smokeRad=30;
            smokes=3;
            sparks=15;
        }};
        ModFx.shapeEffect1 =new Effect(50, e->{
            e.scaled(50,b->{
                Fill.circle(e.x,e.y,5);
                Lines.circle(e.x,e.y,80);
                Lines.circle(e.x,e.y,100);
                Lines.square(e.x,e.y,120,0);
                Lines.square(e.x,e.y,120,45);
            });
        });
        ModFx.shapeEffect2=new Effect(45,e->{
            e.scaled(45,b->{
                Draw.color(Color.sky);
                Fill.circle(e.x,e.y,3);
                Lines.circle(e.x,e.y,32);
                Lines.circle(e.x,e.y,40);
                Lines.square(e.x,e.y,56,0);
                Lines.square(e.x,e.y,56,45);
            });
        });
        ModFx.teachBlueBomb=new Effect(30,100,e->{
            e.scaled(45,b->{
                Draw.color(Pal.techBlue);
                Lines.circle(e.x,e.y,20);

                for(int i=0;i<4;i++){
                    Drawf.tri(e.x,e.y,3,26,(float) i*90);
                }

                Draw.color();

                for(int i=0;i<4;i++){
                    Drawf.tri(e.x,e.y,1.2f,20,(float) i*90);
                }
            });
        });
        ModFx.shootFireFx= (new Effect(70, (e) -> {
            Draw.color(Pal.lightFlame, Pal.darkFlame, Color.gray, e.fin());
            Angles.randLenVectors(e.id, 18,105+e.fin()*180, e.rotation, 18.0F, (x, y) ->
                    Fill.circle(e.x + x, e.y + y, 0.2F + e.fout() * 1.5F));
        })).followParent(false);


        ModStatusEffects.erosionX=new StatusEffect("erosion-x"){{
            healthMultiplier=0.8f;
            reloadMultiplier=0.8f;
            speedMultiplier=0.6f;
            damage=0.2f;
            color=Color.HSVtoRGB(296,60,37);
        }};


        ModItems.load();
        ModItems.experimentalExplosives=new Item("experimental-explosives", Color.HSVtoRGB(4,100,60)){{
            explosiveness=2.8f;
            flammability=1.8f;
            hardness=0;
        }};
        ModItems.uranium=new Item("uranium",Color.HSVtoRGB(125,47,70)){{
            explosiveness=0.2f;
            radioactivity=1.5f;
            hardness=4;
        }};
        ModItems.rock=new Item("rock",Color.HSVtoRGB(240,7,50));
        ModItems.iron=new Item("iron",Color.HSVtoRGB(233,16,25));
        ModItems.metaglassBottle=new Item("metaglass-bottle",Color.HSVtoRGB(240,7,88));
        ModItems.wateryMetaglassBottle=new Item("watery-metaglass-bottle",Color.HSVtoRGB(232,52,91));
        ModItems.frostAlloy=new Item("frost-alloy",Color.HSVtoRGB(196,46,89));
        ModItems.canyonBattery=new Item("canyon-battery",Color.HSVtoRGB(232,47,77)){{charge=0.4f;}};
        ModItems.archipelagoBattery=new Item("archipelago-battery",Color.HSVtoRGB(97,58,75)){{charge=0.6f;}};
        ModItems.heatConductionComponent=new Item("heat-conduction-component",Color.HSVtoRGB(8,49,92));
        ModItems.processor=new Item("processor",Color.HSVtoRGB(226,24,65));
        ModLiquids.lava=new Liquid("lava",Color.HSVtoRGB(17,60,100)){{
            temperature=1.4f;
            viscosity=0.75f;
            effect=StatusEffects.melting;
            lightColor=Color.HSVtoRGB(29,56,100);
        }};
        ModLiquids.kerosene=new Liquid("kerosene",Color.HSVtoRGB(29,43,97)){{
            flammability=1.4f;
            explosiveness=1.2f;
        }};//煤油
        ModLiquids.diesel=new Liquid("diesel",Color.HSVtoRGB(32,63,91)){{
            flammability=0.2f;
            explosiveness=0.8f;
        }};//柴油
        ModLiquids.gasoline=new Liquid("gasoline",Color.HSVtoRGB(32,81,86)){{
            flammability=1;
            explosiveness=0.9f;
        }};//汽油

        ModBlocks.explosive=new RemoveWall("explosive"){{
            requirements(Category.effect,with(Items.lead,50,Items.pyratite,30,Items.blastCompound,10));
        }};

        ModBlocks.laboratory=new GenericCrafter("laboratory"){{
            health=180;
            size=2;
            requirements(Category.crafting, with(Items.copper,30,Items.lead,20,Items.titanium,10));
            consumeItem(Items.blastCompound,2);
            consumeLiquid(Liquids.oil,0.1f);
            consumePower(1f);
            outputItems = new ItemStack[]{(new ItemStack(ModItems.experimentalExplosives,2))};
        }};
        ModBlocks.siliconSteelMixer=new GenericCrafter("silicon-steel-mixer"){{
            health=180;
            size=2;
            requirements(Category.crafting,with(Items.copper,30,Items.lead,20,ModItems.tin,10,ModItems.zinc,10));
            consumeItems(ItemStack.with(Items.silicon,2,ModItems.zinc,1));
            consumePower(1.5f);
            outputItems=new ItemStack[]{new ItemStack(ModItems.siliconSteel,2)};
        }};
        ModBlocks.largeSiliconSteelMixer=new GenericCrafter("large-silicon-steel-mixer"){{
            size=3;
            health=320;
            requirements(Category.crafting,with(Items.copper,50,Items.silicon,50,Items.titanium,20,ModItems.gold,10));
            consumeItems(ItemStack.with(Items.silicon,4,ModItems.zinc,2));
            consumeLiquids(LiquidStack.with(Liquids.water,0.1f));
            consumePower(3f);
            outputItems=new ItemStack[]{new ItemStack(ModItems.siliconSteel,10)};
        }};
        ModBlocks.electrolyticSeparator=new GenericCrafter("electrolytic-separator"){{
            health=180;
            size=2;
            drawer=new DrawMulti(
                    new DrawDefault(),
                    new DrawLiquidTile(Liquids.hydrogen,2) );
            requirements(Category.crafting,with(Items.titanium,30,Items.graphite,25,Items.copper,20,Items.metaglass,20,ModItems.zinc,15));
            consumeLiquids(LiquidStack.with(Liquids.water,0.3f));
            consumePower(1f);
            outputLiquids=new LiquidStack[]{new LiquidStack(Liquids.hydrogen, 0.2f)};
//            drawer=new DrawMulti(){{
//                new DrawDefault();
//                new DrawLiquidTile(Liquids.water,1f);
//            }};
        }};
        ModBlocks.load1();
        ModBlocks.rockDrilling=new GenericCrafter("rock-drilling"){{
            health=240;
            size=3;
            craftTime=60;
            requirements(Category.crafting,with(Items.copper,55,Items.titanium,40,Items.graphite,20));
            consumePower(3f);
            consumeLiquid(Liquids.water,0.2f);
            outputItems=ItemStack.with(ModItems.rock,1);
            craftEffect = Fx.pulverize;
        }};
        ModBlocks.rockCrusher=new GenericCrafter("rock-crusher"){{
            requirements(Category.crafting,with(Items.copper,25,Items.lead,20,Items.graphite,10));
            size=1;
            craftTime=45;
            craftEffect = Fx.pulverize;
            consumeItem(ModItems.rock,1);
            consumePower(0.5f);
            outputItems=ItemStack.with(Items.sand,2);
        }};
        ModBlocks.highTemperatureMeltingFurnace=new GenericCrafter("high-temperature-melting-furnace"){{
            health=200;
            size=2;
            craftTime=30f;
            requirements(Category.crafting,with(Items.copper,35,Items.titanium,25,Items.graphite,10));
            consumeItems(ItemStack.with(ModItems.rock,1));
            consumePower(1.5f);
            outputLiquid=new LiquidStack(ModLiquids.lava,0.4f);
        }};
        ModBlocks.highTemperatureSmeltingPlant=new GenericCrafter("high-temperature-smelting-plant"){{
            health=200;
            size=2;
            requirements(Category.crafting,with(Items.copper,30,Items.titanium,30,Items.graphite,15));
            consumePower(2f);
            consumeLiquids(LiquidStack.with(Liquids.hydrogen,0.25f));
            consumeItems(ItemStack.with(ModItems.rock,2));
            outputItems=ItemStack.with(Items.silicon,1);
        }};
        ModBlocks.assemblyMachine=new GenericCrafter("assembly-machine"){{
            size=2;
            requirements(Category.crafting,with(Items.copper,50,Items.copper,50,ModItems.siliconSteel,25,ModItems.gold,10,ModItems.processor,10));
            consumePower(1.5f);
            consumeItems(ItemStack.with(Items.silicon,1,Items.graphite,1,Items.plastanium,1));
            outputItems=ItemStack.with(ModItems.heatConductionComponent,2);
            craftTime=60;
            craftEffect = Fx.pulverize;
        }};
        ModBlocks.aromatizationMachine=new GenericCrafter("aromatization-machine"){{
            size=3;
            requirements(Category.crafting,with(Items.lead,40,Items.titanium,30,Items.metaglass,20,Items.silicon,20));
            consumePower(2.5f);
            consumeItems(ItemStack.with(Items.graphite,1));
            consumeLiquids(LiquidStack.with(Liquids.water,0.75f,Liquids.oil,0.5f));
            outputLiquids=LiquidStack.with(Liquids.arkycite,1.5f);
            craftTime=60;
            drawer=new DrawMulti(new DrawRegion("-bottom"),new DrawLiquidTile(Liquids.arkycite,2),new DrawDefault());
        }};


        ModBlocks.largeThoriumReactor=new NuclearReactor("large-thorium-reactor"){{
            size=4;
            health=850;
            requirements(Category.power,with(Items.titanium,30,Items.silicon,20,Items.graphite,10,ModItems.siliconSteel,15));
            powerProduction=20f;
            consumeItem(Items.thorium);
            consumeLiquid(Liquids.cryofluid,heating/coolantPower*1.2f).update(false);
        }};
        ModBlocks.highSpeedDisassembler=new Separator("high-speed-disassembler"){{
            health=240;
            size=3;
            craftTime=60f;
            requirements(Category.crafting,with(Items.copper,45,Items.titanium,25,Items.silicon,30));
            consumePower(3.25f);
//            consumeItems(ItemStack.with(Items.scrap,1));
            consumeLiquids(LiquidStack.with(ModLiquids.lava,0.2f));
            drawer=new DrawMulti(new DrawRegion("-b"),new DrawLiquidTile(ModLiquids.lava),new DrawDefault());
            results=ItemStack.with(Items.thorium,1,ModItems.zinc,1,ModItems.tin,2,ModItems.gold,1);
        }};
        ModBlocks.mechanicalGenerator=new ConsumeGenerator("mechanical-generator"){{
            size=1;
            health=260;
            requirements(Category.power,with(Items.copper,50,Items.graphite,30,Items.lead,50,Items.silicon,20));
            powerProduction=0.75f;
        }};
        ModBlocks.hydroelectricGenerator=new ConsumeGenerator("hydroelectric-generator"){{
            health=300;
            size=2;
            powerProduction=2.25f;
            consumeLiquids(LiquidStack.with(Liquids.water,0.3f));
            requirements(Category.power,with(Items.lead,60,Items.graphite,30,Items.silicon,20,Items.metaglass,30));
        }};


        ModBlocks.electricHeater=new HeatProducer("electric-heater"){{
            size=2;
            requirements(Category.crafting,with(Items.copper,30,Items.lead,40,Items.silicon,30,ModItems.tin,20));
            consumePower(1.5f);
            heatOutput=8;
            drawer=new DrawMulti(new DrawDefault(),new DrawHeatOutput());
        }};
        ModBlocks.fissionReactor=new HeaterGenerator("fission-reactor"){{
            size=3;
            requirements(Category.power,with(Items.lead,300,Items.metaglass,80,Items.titanium,100,Items.graphite,150,Items.thorium,60,Items.silicon,80,ModItems.heatConductionComponent,80));
            powerProduction=12f;
            consume(new ConsumeItemRadioactive());
            consumeLiquid(Liquids.cryofluid,0.75f);
            heatOutput=32;
            drawer=new DrawMulti(new DrawDefault(),new DrawHeatOutput());
        }};
        ModBlocks.smallHeatTransmitter=new HeatConductor("small-heat-transmitter"){{
            size=1;
            requirements(Category.crafting,with(Items.graphite,10,Items.lead,8,ModItems.heatConductionComponent,5));
            drawer=new DrawMulti(new DrawDefault(), new DrawHeatOutput(), new DrawHeatInput("-heat"));
            regionRotated1=1;
        }};
        ModBlocks.heatTransmitter=new HeatConductor("heat-transmitter"){{
            size=2;
            requirements(Category.crafting,with(Items.graphite,20,Items.lead,10));
            drawer=new DrawMulti(new DrawDefault(), new DrawHeatOutput(), new DrawHeatInput("-heat"));
            regionRotated1=1;
        }};


        ModBlocks.petroleumFractionatingTower=new HeatCrafter("petroleum-fractionating-tower"){{
            health=240;
            size=3;
            craftTime=60f;
            requirements(Category.crafting,with(Items.titanium,70,Items.silicon,30,Items.plastanium,20,ModItems.processor,5));
            consumePower(2.5f);
            heatRequirement=16;
            maxEfficiency=2.4f;
            consumeLiquids(LiquidStack.with(Liquids.oil,1));
            outputLiquids = LiquidStack.with(ModLiquids.diesel,0.2,ModLiquids.kerosene,0.2,ModLiquids.gasoline,0.2);
        }};
        ModBlocks.canyonBatteryCompressor=new GenericCrafter("canyon-battery-compressor"){{
            health=200;
            size=2;
            requirements(Category.crafting,with(Items.copper,30,Items.lead,45,Items.titanium,30,Items.silicon,20));
            consumePower(3);
            craftTime=120;
            outputItems=ItemStack.with(ModItems.canyonBattery,1);
        }};
        ModBlocks.archipelagoBatteryCompressor=new GenericCrafter("archipelago-battery-compressor"){{
            health=200;
            size=2;
            requirements(Category.crafting,with(Items.copper,30,Items.lead,45,Items.plastanium,30,Items.silicon,20));
            consumePower(3);
            craftTime=120;
            outputItems=ItemStack.with(ModItems.archipelagoBattery,1);
        }};
        ModBlocks.photoLithographyMachine=new GenericCrafter("photo-lithography-machine"){{
            health=210;
            size=2;
            requirements(Category.crafting,with(Items.copper,40,Items.lead,30,Items.plastanium,30,Items.titanium,20,Items.metaglass,10,ModItems.siliconSteel,10));
            consumePower(8f);
            craftTime=120;
            consumeItems(ItemStack.with(Items.silicon,6));
            outputItems=ItemStack.with(ModItems.processor,5);
        }};
        ModBlocks.laserEnergyNode =new BeamNode("laser-energy-node"){{
            health=100;
            size=1;
            requirements(Category.power, with(Items.copper, 8,Items.lead,5,ModItems.zinc,5));
            range=15;
            consumesPower=outputsPower=true;
            consumePowerBuffered(1000f);
        }};
        ModBlocks.smallDrillBit=new Drill("small-drill-bit"){{
            health=65;
            size=1;
            tier=3;
            requirements(Category.production,with(Items.copper,10,Items.graphite,5));
            drillTime=400f;
            hardnessDrillMultiplier=2;
        }};
        ModBlocks.laserBeamDrill =new BeamDrill("laser-beam-drill"){{
            requirements(Category.production,with(Items.titanium,40,Items.silicon,30,ModItems.siliconSteel,20,Items.lead,20));
            size=2;
            tier=4;
            consumePower(0.15f);
            range=4;
        }};
        ModBlocks.percussionDrilling=new BurstDrill("percussion-drilling"){{
            requirements(Category.production,with(Items.copper,30,Items.graphite,40,Items.titanium,50,ModItems.siliconSteel,30,Items.silicon,30,Items.plastanium,30));
            size=4;
            tier=5;
            drillTime=280f;
            shake=4.2f;
            hasPower=true;
            itemCapacity=50;
            arrows=1;
            liquidBoostIntensity=4.2f;
            drillEffect = new MultiEffect(Fx.mineImpact, Fx.drillSteam, Fx.mineImpactWave.wrap(Pal.redLight, 40.0F));
            consumePower(3f);
            consumeLiquid(Liquids.water,1f);
            consumeLiquid(Liquids.hydrogen,0.2f).boost();
        }};


        ModBlocks.fluidThermalEnergyGenerator=new ConsumeGenerator("fluid-thermal-energy-generator"){{
            requirements(Category.power,with(Items.copper,30,Items.lead,50,Items.titanium,20,Items.silicon,10,ModItems.zinc,10));
            powerProduction=7f;
            size=2;
            hasLiquids=true;
            drawer=new DrawMulti(new DrawDefault(),new DrawRegion("-cap"),
                    new DrawLiquidRegion());
            consumeLiquid(Liquids.slag,0.5f);
        }};
        ModBlocks.dieselGenerator=new ConsumeGenerator("diesel-generator"){{
            requirements(Category.power,with(Items.copper,35,Items.lead,40,Items.titanium,20,ModItems.siliconSteel,20));
            powerProduction=10f;
            size=2;
            hasLiquids=true;
            drawer=new DrawMulti(new DrawDefault(),new DrawRegion("-cap"),
                    new DrawLiquidRegion());
            consumeLiquid(ModLiquids.diesel,0.2f);
        }};
        ModBlocks.canyonBatteryGenerator=new ConsumeGenerator("canyon-battery-generator"){{
            requirements(Category.power,with(Items.copper,30,Items.lead,50,Items.titanium,30,Items.silicon,30));
            powerProduction=3f;
            size=2;
            consumeItems(ItemStack.with(ModItems.canyonBattery,1));
        }};
        ModBlocks.archipelagoBatteryGenerator=new ConsumeGenerator("archipelago-battery-generator"){{
            requirements(Category.power,with(Items.copper,30,Items.lead,50,Items.titanium,30,Items.silicon,30));
            powerProduction=5f;
            size=2;
            consumeItems(ItemStack.with(ModItems.archipelagoBattery,1));
        }};


        ModBlocks.oreGold=new OreBlock("ore-gold",ModItems.gold);
        ModBlocks.oreZinc=new OreBlock("ore-zinc",ModItems.zinc);
        ModBlocks.oreTin=new OreBlock("ore-tin",ModItems.tin);
        ModBlocks.oreUranium=new OreBlock("ore-uranium",ModItems.uranium);


        ModBlocks.itemTrack=new Duct("item-track"){{
            requirements(Category.distribution,with(Items.phaseFabric,1));
            health=60;
            speed=0.19f;
        }};
        ModBlocks.logisticsPipeline=new Duct("logistics-pipeline"){{
            requirements(Category.distribution,with(Items.titanium,1,Items.copper,1,ModItems.siliconSteel,1));
            health=80;
            speed=5f;
        }};
        ModBlocks.fastItemBridge=new BufferedItemBridge("titanium-conveyor-bridge"){{
            requirements(Category.distribution,with(Items.titanium,5,Items.copper,5,Items.silicon,5));
            health=60;
            range=10;
            speed=74;
            fadeIn=moveArrows=true;
            arrowSpacing=6;
            bufferCapacity=15;
        }};


        ModBlocks.outpostCore=new OutPostCoreBlock("outpost-core"){{
            requirements(Category.effect, BuildVisibility.shown,with(Items.titanium,1000,Items.copper,1200,Items.silicon,800,ModItems.bronze,400));
            health=1200;
            size=3;
            unitType = UnitTypes.alpha;
        }};


        ModBlocks.overclockStateFieldProjection=new StateFieldProjection("overclock-state-field-projection"){{
            size=2;
            requirements(Category.effect,with(Items.lead,80,Items.titanium,50,Items.silicon,30,ModItems.processor,10,ModItems.bronze,20));
            statusEffect=StatusEffects.overclock;
            consumePower(2f);
            duration=150f;
            reload=120f;
            range=80;
        }};
//        ModBlocks.metalCrusher=new MultiFormulaFactory("metal-crusher"){{
//            health=100;
//            size=1;
//            requirements(Category.crafting,with(Items.copper,10,Items.lead,8,Items.graphite,5));
//            consumePower(2);
//            plans=Seq.with(
//                    new ItemPlan(new ItemStack(Items.copper,1),40f,with(Items.lead,1)),
//                    new ItemPlan(new ItemStack(Items.lead,1),60f,with(Items.copper,1))
//            );
//        }};


        //1x1
        ModTurrets.itemTurret1=new ItemTurret("item-turret-1"){{
            requirements(Category.turret, with(Items.copper, 40,ModItems.zinc,10,ModItems.gold,5));
            ammo(ModItems.experimentalExplosives, new MissileBulletType(1.5f,32){{
                ammoMultiplier=2;
                splashDamage=4.5f;
                splashDamageRadius=2.5f;
                makeFire=true;
                lifetime=160;
            }},Items.surgeAlloy,new BasicBulletType(0,0));
            displayAmmoMultiplier=true;
            range=160;
            shoot = new ShootPattern();
            drawer = new DrawTurret(){{
                parts.addAll();
            }};
            consumePower(2f);
            coolant = consumeCoolant(0.1f);
        }};
        //2x2-Item
        ModTurrets.itemTurret2=new ItemTurret("item-turret-2"){{
            requirements(Category.turret,with(Items.copper,45,ModItems.zinc,20
                    ,ModItems.siliconSteel,10,ModItems.gold,5));
            ammo(Items.silicon,new MissileBulletType(1.6f,25){{
                hitColor = this.backColor = this.trailColor = Pal.blastAmmoBack;
                ammoMultiplier=3;
                splashDamage=1.2f;
                splashDamageRadius=1f;
                lifetime=160;
            }},ModItems.siliconSteel,new MissileBulletType(1.7f,28){{
                hitColor = this.backColor = this.trailColor = Pal.blastAmmoBack;
                ammoMultiplier=4;
                splashDamage=1.2f;
                splashDamageRadius=1.2f;
                lifetime=160;
            }},ModItems.zinc,new BasicBulletType(1.7f,21){{
                hitColor = this.backColor = this.trailColor = Pal.blastAmmoBack;
                ammoMultiplier=5;
                lifetime=160;
            }});
            range=176;
            inaccuracy=2f;
            drawer = new DrawTurret(){{
                parts.addAll();
            }};
            maxAmmo=20;
            size=2;
        }};
        ModTurrets.sharpSpear=new ItemTurret("sharp-spear"){{
            shootSound=Sounds.shootScatter;
            targetGround=false;
            range=240;
            inaccuracy=3f;
            maxAmmo=30;
            size=2;
            reload=1;
            rotateSpeed=8f;
            requirements(Category.turret,with(Items.copper,45,Items.lead,20,Items.titanium,20,ModItems.tin,10));
            ammo(Items.lead,new BasicBulletType(6.7f,12){{
                hitColor = this.backColor = this.trailColor = Pal.graphiteAmmoBack;
                width=4;
                height=10;
                ammoMultiplier=3;
                lifetime=140;
            }},ModItems.siliconSteel,new MissileBulletType(3.8f,11){{
                hitColor = this.backColor = this.trailColor = Pal.siliconAmmoBack;
                width=5;
                height=10;
                trailLength=12;
                trailWidth=2;
                ammoMultiplier=5;
                lifetime=140;
            }});
            coolant=consumeCoolant(1.4f);
            ammoUseEffect=Fx.casing2;
        }};
        ModTurrets.longsword=new ItemTurret("longsword"){{
            requirements(Category.turret,ItemStack.with(Items.copper,30,Items.graphite,20,Items.thorium,30,ModItems.bronze,30));
            maxAmmo=60;
            size=2;
            reload=10f;
            range=125f;
            final float len=range+10;
            shootSound= Sounds.shootFuse;
            ammo(Items.titanium,new ShrapnelBulletType(){{
                reloadMultiplier=1.4f;
                damage=90;
                ammoMultiplier=4;
                width=17f;
                length=len;
            }},ModItems.bronze,new ShrapnelBulletType(){{
                damage=80;
                ammoMultiplier=5;
                width=17f;
                length=len+40f;
                rangeChange=36;
                toColor=TIColor.bronzeLight;
                shootEffect=smokeEffect=ModFx.bronzeShoot;
            }},Items.thorium,new ShrapnelBulletType(){{
                damage=180;
                ammoMultiplier=6;
                width=17f;
                length=len;
                toColor=Pal.thoriumPink;
                shootEffect=smokeEffect=Fx.thoriumShoot;
            }});
            coolant=consumeCoolant(0.5f);
        }};
        //2x2-Power
        ModTurrets.powerTurret4 =new PowerTurret("power-turret-4"){{
            requirements(Category.turret,with(Items.copper,50,ModItems.siliconSteel,20,Items.titanium,15,ModItems.processor,10));
            consumePower(5f);
            size=2;
            reload=30;
            rotateSpeed=2.3f;
            range=176;
            drawer = new DrawTurret(){{
                parts.addAll();
            }};
            shootType= new LaserBulletType(45){{
                shootEffect = Fx.lancerLaserShoot;
                smokeEffect = Fx.smokeCloud;
                hitEffect = Fx.hitLancer;
                colors= new Color[]{Color.HSVtoRGB(210,50,100)};
                hitSize=4f;
                range=176;
                maxRange=28;
                status=StatusEffects.freezing;
                statusDuration=60f;
            }};
        }};
        ModTurrets.powerTurret6=new PowerTurret("power-turret-6"){{
            requirements(Category.turret,with(Items.titanium,45,ModItems.siliconSteel,20,Items.metaglass,10));
            consumePower(6f);
            size=2;
            reload=5;
            rotateSpeed=2.8f;
            range=280;
            drawer=new DrawTurret(){{parts.addAll();}};
            targetHealing=true;
            shootSound=Sounds.shootAlpha;
            shootType=new LaserBoltBulletType(6.2f,15){{
                buildingDamageMultiplier=0.5f;
                lifetime=60;
                healPercent=6.5f;
                collidesTeam=true;
                backColor=Pal.heal;
                frontColor=Color.white;
            }};
        }};
        ModTurrets.itemTurret5=new ItemTurret("item-turret-5"){{//huguang
            requirements(Category.turret,with(Items.copper,600,Items.titanium,400,
                    ModItems.zinc,350,Items.surgeAlloy,50));
            shootEffect = Fx.instShoot;
            smokeEffect = Fx.smokeCloud;
            range=550;
            recoil=4.5f;
            maxAmmo=30;
            unitSort=UnitSorts.strongest;
            drawer=new DrawTurret(){{parts.addAll();}};
            size=4;
            reload=110;
            consumePower(4.5f);
            ammo(Items.surgeAlloy,new RailBulletType(){{
                length=560;
                hitColor=trailColor = Pal.blastAmmoBack;
                hitEffect = Fx.instHit;
                despawnEffect = Fx.instBomb;
                shootEffect = Fx.instShoot;
                smokeEffect = Fx.smokeCloud;
                pierceEffect = Fx.railHit;
                pointEffect = Fx.instTrail;
                buildingDamageMultiplier=0.7f;
                lifetime=290;
//                maxRange=560;
//                range=520;
                ammoMultiplier=3;
                damage=720;
                collidesTiles=false;
                collideTerrain=false;
            }},Items.titanium,new RailBulletType(){{
//                maxRange=560;
//                range=520;
                length=560;
                lifetime=290;
                hitColor=trailColor=Pal.blastAmmoBack;
                hitEffect = Fx.instHit;
                despawnEffect = Fx.instBomb;
                shootEffect = Fx.instShoot;
                smokeEffect = Fx.smokeCloud;
                pierceEffect = Fx.railHit;
                pointEffect = Fx.instTrail;
                damage=560;
                ammoMultiplier=2;
                collidesTiles=false;
                collideTerrain=false;
            }});
        }};
        ModTurrets.burst=new ItemTurret("burst"){{
            size=3;
            requirements(Category.turret,with(Items.titanium,120,Items.silicon,50,Items.graphite,50));
            reload=90;
            range=240;
            heatRequirement=8;
            ammo(Items.plastanium,new ArtilleryBulletType(5.6f,120f){{
                lightColor=trailColor=hitColor=backColor=Pal.techBlue;
                frontColor=Color.white;
                trailLength=20;
                trailWidth=2.8f;
                lifetime=70;
                width=3;
                splashDamage=64;
                splashDamageRadius=80;
                fragBullets=10;
                fragLifeMin=0.8f;
                fragRandomSpread=120;
                ammoMultiplier=1;
                fragBullet=new BasicBulletType(6.2f,28f){{
                    lightColor=trailColor=hitColor=backColor=Pal.techBlue;
                    shootEffect=Fx.shootBigColor;
                    smokeEffect=Fx.shootSmokeSquareBig;
                    lifetime=24;
                    pierceCap=3;
                    width=4;
                    trailWidth=4;
                    trailLength=24;
                }};
            }});
            shoot = new ShootAlternate(7.2f){{barrels=2;}};
            drawer=new DrawTurret(){{
                parts.addAll(new RegionPart(){{
                    under=true;
                    suffix="-l";
                    moveY=-1.5f;
                    recoilIndex=0;
                    progress=PartProgress.recoil;
                }},new RegionPart(){{
                    under=true;
                    suffix="-r";
                    moveY=-1.5f;
                    recoilIndex=1;
                    progress=PartProgress.recoil;
                }});
            }};
        }};
        //3x3-Item
        ModTurrets.puncture=new ItemTurret("puncture"){{
            inaccuracy=0.1f;
            size=3;
            requirements(Category.turret,with(Items.titanium,100,Items.graphite,80,ModItems.zinc,20,ModItems.siliconSteel,20));
            range=280;
            reload=15;
            maxAmmo=30;
            drawer=new DrawTurret(){{parts.addAll();}};
            shootSound=Sounds.shootBreach;
            ammo(Items.titanium,new BasicBulletType(6f,25){{
                lifetime=60;
                width=12f;
                hitSize=20;
                shootEffect=new MultiEffect(new Effect[]{Fx.shootBigColor,Fx.colorSparkBig});
                ammoMultiplier=2;
                trailWidth=2.8f;
                trailLength=10;
                pierceCap=3;
                pierce=true;
                pierceBuilding=true;
                hitColor=backColor=trailColor=Pal.techBlue;
                hitEffect=despawnEffect=Fx.hitBulletColor;
                buildingDamageMultiplier=0.5f;
            }},Items.thorium,new BasicBulletType(6f,45){{
                lifetime=60;
                width=12f;
                hitSize=20;
                shootEffect=new MultiEffect(new Effect[]{Fx.shootBigColor,Fx.colorSparkBig});
                reloadMultiplier=0.8f;
                ammoMultiplier=3;
                trailWidth=2.8f;
                trailLength=10;
                pierceCap=5;
                pierce=true;
                pierceBuilding=true;
                hitColor=backColor=trailColor=Pal.thoriumAmmoBack;
                hitEffect=despawnEffect=Fx.hitBulletColor;
                buildingDamageMultiplier=0.5f;
            }},ModItems.siliconSteel,new BasicBulletType(6f,20){{
                lifetime=60;
                width=12f;
                hitSize=20;
                shootEffect=new MultiEffect(Fx.shootBigColor,Fx.colorSparkBig);
                ammoMultiplier=3;
                trailWidth=2.8f;
                trailLength=10;
                pierceCap=3;
                pierce=true;
                pierceBuilding=true;
                hitColor=backColor=trailColor=Pal.siliconAmmoBack;
                hitEffect=despawnEffect=Fx.hitBulletColor;
                buildingDamageMultiplier=0.5f;
                fragBullet=new LightningBulletType(){{
                    lightningColor=Color.white;
                    lightningLength=5;
                    lightningDamage=10;
                }};
            }});
        }};

        ModTurrets.blaze=new ItemTurret("blaze"){{
            inaccuracy=0.5f;
            requirements(Category.turret,with(Items.copper,30,Items.graphite,20,Items.titanium,10));
            reload=2;
            recoil=1;
            coolantMultiplier=3;
            range=200;
            shootCone=45;
            ammoUseEffect=Fx.none;
            health=380;
            coolant=this.consumeCoolant(0.15F);
            maxAmmo=60;
            ammo(Items.coal,new BulletType(6,22){{
                size=2;
                ammoMultiplier=5;
                lifetime=120;
                hitSize=7.2f;
                pierce=true;
                pierceCap=-1;
                statusDuration=300;
                shootEffect=ModFx.shootFireFx;
                hitEffect=Fx.hitFlameSmall;
                despawnEffect=Fx.none;
                status=StatusEffects.burning;
                hittable=false;
                keepVelocity=false;
            }},Items.pyratite,new BulletType(6.2f,35){{
                ammoMultiplier=5;
                lifetime=120;
                hitSize=7.2f;
                pierce=true;
                pierceCap=-1;
                statusDuration=600;
                shootEffect=ModFx.shootFireFx;
                hitEffect=Fx.hitFlameSmall;
                despawnEffect=Fx.none;
                status=StatusEffects.burning;
                hittable=false;
                keepVelocity=false;
            }},Items.blastCompound,new BulletType(6.2f,55){{
                ammoMultiplier=5;
                lifetime=120;
                hitSize=7.5f;
                pierce=true;
                pierceCap=-1;
                splashDamage=35;
                splashDamageRadius=32;
                statusDuration=900;
                shootEffect=ModFx.shootFireFx;
                hitEffect=Fx.hitFlameSmall;
                despawnEffect=Fx.none;
                status=StatusEffects.burning;
                hittable=false;
                keepVelocity=false;
            }},ModItems.experimentalExplosives,new BulletType(6.2f,65){{
                ammoMultiplier=5;
                lifetime=120;
                hitSize=7.5f;
                pierce=true;
                pierceCap=-1;
                splashDamage=40;
                splashDamageRadius=40;
                statusDuration=900;
                shootEffect=ModFx.shootFireFx;
                hitEffect=Fx.hitFlameSmall;
                despawnEffect=Fx.none;
                status=StatusEffects.burning;
                hittable=false;
                keepVelocity=false;
            }});
        }};
        ModTurrets.pureEmptiness=new ItemTurret("pure-emptiness"){{
            size=4;
            shake=3;
            recoil=1.6f;
            inaccuracy=3;
            shootCone=10;
            reload=5;
            maxAmmo=60;
            range=320;
            coolant = consumeCoolant(0.2f);
            heatRequirement=24;
            ammoUseEffect = Fx.casing3;
            shootSound=Sounds.shootDisperse;
            requirements(Category.turret,with(Items.copper,800,Items.lead,100,Items.thorium,80,Items.titanium,80,ModItems.siliconSteel,50,ModItems.processor,20));
            ammo(Items.lead,new BasicBulletType(8f,12){{
                height=14;
                width=4;
                velocityRnd=0.1f;
                collidesTiles=false;
                shootEffect=Fx.sparkShoot;
                smokeEffect=Fx.shootSmokeDisperse;
                frontColor=Pal.techBlue;
                trailEffect=Fx.disperseTrail;
                trailChance=0.4f;
                trailSpread=2.4f;
                hitEffect=despawnEffect=Fx.hitBulletColor;
                backColor=trailColor=hitColor=Color.gray;
                ammoMultiplier=1f;
                lifetime=40;
            }},Items.titanium,new BasicBulletType(8.5f,45){{
                height=14;
                width=4;
                velocityRnd=0.1f;
                collidesTiles=false;
                shootEffect=Fx.sparkShoot;
                smokeEffect=Fx.shootSmokeDisperse;
                frontColor=Pal.techBlue;
                trailEffect=Fx.disperseTrail;
                trailChance=0.4f;
                trailSpread=2.4f;
                hitEffect=despawnEffect=Fx.hitBulletColor;
                backColor=trailColor=hitColor=Pal.techBlue;
                ammoMultiplier=2f;
                lifetime=40;
            }},Items.graphite,new BasicBulletType(8.5f,36){{
                height=14;
                width=4;
                velocityRnd=0.1f;
                collidesTiles=false;
                shootEffect=Fx.sparkShoot;
                smokeEffect=Fx.shootSmokeDisperse;
                frontColor=Pal.graphiteAmmoFront;
                hitEffect=despawnEffect=Fx.hitBulletColor;
                backColor=trailColor=hitColor=Pal.graphiteAmmoBack;
                ammoMultiplier=3f;
                lifetime=40;
                trailLength=6;
            }},ModItems.siliconSteel,new BasicBulletType(8.5f,40){{
                height=14;
                width=4;
                velocityRnd=0.1f;
                collidesTiles=false;
                shootEffect=Fx.sparkShoot;
                smokeEffect=Fx.shootSmokeDisperse;
                frontColor=Pal.graphiteAmmoFront;
                hitEffect=despawnEffect=Fx.hitBulletColor;
                backColor=trailColor=hitColor=Pal.graphiteAmmoBack;
                ammoMultiplier=4f;
                lifetime=40;
                trailLength=4;
                homingPower=0.8f;
                homingRange=200;
                splashDamage=5;
                splashDamageRadius=12;
                drawer=new DrawMulti(){{parts.addAll(new FlarePart(){{
                    progress=PartProgress.life.slope().curve(Interp.pow2In);
                    x=0;
                    y=0;
                    sides=4;
                    radius=3;
                    radiusTo=9;
                    stroke=3.2f;
                    rotation=45;
                    followRotation=true;
                }});
                }};
            }},Items.thorium,new BasicBulletType(8.2f,45){{
                reloadMultiplier=0.8f;
                height=14;
                width=4;
                velocityRnd=0.1f;
                collidesTiles=false;
                shootEffect=Fx.sparkShoot;
                smokeEffect=Fx.shootSmokeDisperse;
                frontColor=Pal.thoriumAmmoFront;
                trailEffect=Fx.disperseTrail;
                trailChance=0.4f;
                trailSpread=1f;
                hitEffect=despawnEffect=Fx.hitBulletColor;
                backColor=trailColor=hitColor=Pal.thoriumAmmoBack;
                ammoMultiplier=3f;
                lifetime=40;
            }},Items.plastanium,new BasicBulletType(8.2f,24){{
                height=14;
                width=4;
                velocityRnd=0.1f;
                collidesTiles=false;
                shootEffect = Fx.sparkShoot;
                smokeEffect=Fx.shootSmokeDisperse;
                frontColor=Pal.plastaniumFront;
                trailEffect=Fx.disperseTrail;
                trailChance=0.4f;
                trailSpread=1f;
                hitEffect=despawnEffect=Fx.hitBulletColor;
                backColor=trailColor=hitColor=Pal.plastaniumBack;
                ammoMultiplier=2f;
                lifetime=40;
                fragBullet=new BasicBulletType(8.2f,20){{
                    width=height=16;
                    velocityRnd=0.1f;
                    collidesTiles=false;
                    shootEffect = Fx.sparkShoot;
                    smokeEffect=Fx.shootSmokeDisperse;
                    frontColor=Pal.plastaniumFront;
                    trailEffect=Fx.disperseTrail;
                    trailChance=0.4f;
                    trailSpread=1f;
                    hitEffect=despawnEffect=Fx.hitBulletColor;
                    backColor=trailColor=hitColor=Pal.plastaniumBack;
                    lifetime=3;
                }};
            }},Items.blastCompound,new BasicBulletType(8.2f,45){{
                splashDamage=30;
                splashDamageRadius=60;
                reloadMultiplier=0.8f;
                height=14;
                width=4;
                velocityRnd=0.1f;
                collidesTiles=false;
                shootEffect=Fx.sparkShoot;
                smokeEffect=Fx.shootSmokeDisperse;
                frontColor=Pal.blastAmmoFront;
                trailEffect=Fx.disperseTrail;
                trailChance=0.4f;
                trailSpread=1f;
                hitEffect=Fx.hitBulletColor;
                despawnEffect=ModFx.explosionEffect1;
                backColor=trailColor=hitColor=Pal.blastAmmoBack;
                ammoMultiplier=3f;
                lifetime=40;
            }},Items.surgeAlloy,new BasicBulletType(8.2f,60){{
                height=14;
                width=4;
                velocityRnd=0.1f;
                collidesTiles=false;
                shootEffect=Fx.sparkShoot;
                smokeEffect=Fx.shootSmokeDisperse;
                frontColor=Pal.surgeAmmoFront;
                trailEffect=Fx.disperseTrail;
                trailChance=0.4f;
                trailSpread=1f;
                hitEffect=despawnEffect=Fx.hitBulletColor;
                backColor=trailColor=hitColor=Pal.surgeAmmoBack;
                ammoMultiplier=3f;
                lifetime=40;
                bulletInterval=4;
                intervalBullet=new BulletType(){{
                    lightningLengthRand=4;
                    lightningLength=3;
                    lightningCone=30;
                    lightningDamage=25;
                    lightning=2;
                    hittable=false;
                    instantDisappear=true;
                    hitEffect=despawnEffect=Fx.none;
                }};
            }});
            drawer=new DrawTurret(){{parts.addAll(new ShapePart(){{
                y=-12;
                color=Color.HSVtoRGB(214,28,74);
                hollow=true;
                circle=true;
                stroke=0;
                strokeTo=1.8f;
                radius=0;
                radiusTo=4;
                rotateSpeed=3;
            }},new ShapePart(){{
                y=-12;
                color=Color.HSVtoRGB(214,28,74);
                hollow=true;
                circle=true;
                stroke=0;
                strokeTo=1.8f;
                radius=0;
                radiusTo=10;
                rotateSpeed=3;
            }},new ShapePart(){{
                y=-12;
                color=Color.HSVtoRGB(214,28,74);
                hollow=true;
                circle=false;
                stroke=0;
                strokeTo=1.8f;
                radius=0;
                radiusTo=10;
                rotateSpeed=2;
                sides=3;
                rotation=0;
            }},new ShapePart(){{
                y=-12;
                color=Color.HSVtoRGB(214,28,74);
                hollow=true;
                circle=false;
                stroke=0;
                strokeTo=1.8f;
                radius=0;
                radiusTo=10;
                rotateSpeed=2;
                sides=3;
                rotation=180;
            }}
            );}};
            shoot=new ShootAlternate(){{
                spread=4.2f;
                shots=4;
                barrels=4;
            }};
        }};
        ModTurrets.daytime=new ItemTurret("daytime"){{
            shootSound=Sounds.shootAfflict;
            recoil=3.2f;
            shake=6;
            inaccuracy=1;
            range=380;
            size=4;
            shootY=4;
            maxAmmo=20;
            reload=120;
            final Effect sfe = new MultiEffect(new Effect[]{Fx.shootBigColor, Fx.colorSparkBig});
            requirements(Category.turret,with(Items.copper,1000,Items.lead,400,Items.titanium,280,Items.plastanium,100,ModItems.siliconSteel,50,ModItems.processor,30));
            ammo(Items.titanium,new BasicBulletType(9.3f,20){{
                inaccuracy=10;
                velocityRnd=0.08f;
                shootCone=10;
                width = 10.0F;
                height = 21.0F;
                hitSize = 7.0F;
                shootEffect=sfe;
                smokeEffect=Fx.shootBigSmoke;
                pierce=true;
                pierceCap=5;
                hittable=false;
                ammoMultiplier=1f;
                reloadMultiplier=0.6f;
                hitColor=backColor=trailColor=Color.sky;
                frontColor = Color.white;
                trailWidth = 2.2F;
                trailLength = 11;
                trailEffect = Fx.disperseTrail;
                trailInterval = 2.0F;
                hitEffect=despawnEffect = Fx.hitBulletColor;
                buildingDamageMultiplier = 0.5f;
                trailRotation=true;
            }},Items.surgeAlloy,new BasicBulletType(9f,50){{
                inaccuracy=10;
                velocityRnd=0.08f;
                shootCone=10;
                width = 10.0F;
                height = 21.0F;
                hitSize = 7.0F;
                shootEffect=sfe;
                smokeEffect=Fx.shootBigSmoke;
                pierce=true;
                pierceCap=5;
                hittable=false;
                ammoMultiplier=1f;
                hitColor=backColor=trailColor=Color.valueOf("ab8ec5");
                frontColor = Color.white;
                trailWidth = 2.2F;
                trailLength = 11;
                trailEffect = Fx.disperseTrail;
                trailInterval = 2.0F;
                hitEffect=despawnEffect = Fx.hitBulletColor;
                buildingDamageMultiplier = 0.5f;
                trailRotation=true;
            }});
            drawer=new DrawTurret(){{parts.addAll(new ShapePart(){{
                y=-12;
                color=Color.HSVtoRGB(214,28,74);
                hollow=true;
                circle=true;
                stroke=0;
                strokeTo=1.8f;
                radius=0;
                radiusTo=4;
                rotateSpeed=3;
            }},new ShapePart(){{
                y=-12;
                color=Color.HSVtoRGB(214,28,74);
                hollow=true;
                circle=true;
                stroke=0;
                strokeTo=1.8f;
                radius=0;
                radiusTo=10;
                rotateSpeed=3;
            }},new HaloPart(){{
                sides=4;
                tri=true;
                y=-12;
                color=Color.HSVtoRGB(214,28,74);
                hollow=true;
                stroke=0;
                strokeTo=1.8f;
                radius=0;
                radiusTo=8;
                triLength=0;
                triLengthTo=10;
                haloRotateSpeed=3;
                haloRadius=16;
            }},new ShapePart(){{
                y=-12;
                color=Color.HSVtoRGB(214,28,74);
                hollow=true;
                circle=false;
                sides=3;
                stroke=0;
                strokeTo=1.8f;
                radius=0;
                radiusTo=10;
                rotateSpeed=-3;
            }},new ShapePart(){{
                y=-12;
                color=Color.HSVtoRGB(214,28,74);
                hollow=true;
                circle=false;
                sides=3;
                stroke=0;
                strokeTo=1.8f;
                radius=0;
                radiusTo=10;
                rotateSpeed=3;
            }}
            );}};
            shoot=new ShootSpread(){{
                spread=0.5f;
                shots=40;
            }};
            ammoPerShot=10;
            consumeAmmoOnce=true;
            coolant = consumeCoolant(0.5f);
        }};
        ModTurrets.end=new ItemTurret("end"){{
            recoil=2;
            inaccuracy=3;
            size=4;
            requirements(Category.turret,with(Items.copper,1200,Items.lead,600,Items.titanium,400,ModItems.processor,200,Items.plastanium,100,Items.surgeAlloy,100,Items.silicon,50));
            unitSort=UnitSorts.strongest;
            consumesPower=true;
            consumePower(20f);
            maxAmmo=80;
            shootY=12;
            range=800;
            ammo(Items.surgeAlloy,new ArtilleryBulletType(7f,200f){{
                ammoMultiplier=3;
                lifetime=280;
                trailWidth=1.8f;
                trailLength=56;
                shootEffect=Fx.shootSmokeSquareBig;
                trailEffect=Fx.colorSpark;
                smokeEffect=Fx.shootSmokeDisperse;
                hitColor=trailColor=lightningColor=Color.black;
                homingDelay=1f;
                homingRange=200f;
                homingPower=3.2f;
                collidesAir=true;
                collidesGround=true;
                fragSpread=30;
                fragBullets=5;
                fragBullet=new EmpBulletType(){{
                    splashDamage=80;
                    splashDamageRadius=100;
                    splashDamagePierce=true;
                    damage=100;
                    speed=6.4f;
                    hitColor=trailColor=lightningColor=Color.black;
                    homingDelay=2f;
                    homingRange=120f;
                    homingPower=3f;
                    lifetime=60;
                    hitEffect=ModFx.shapeEffect1;
                    trailEffect=Fx.colorSpark;
                    makeFire=true;
                    status=StatusEffects.unmoving;
                    collidesAir=true;
                    collidesGround=true;
                }};
            }});
            shoot=new ShootSpread(){{
                spread=6;
                shots=5;
            }};
        }};
        ModTurrets.frost=new LiquidTurret("frost"){{
            recoil=0;
            inaccuracy=5;
            xRand=2f;
            size=4;
            requirements(Category.turret,with(Items.copper,1000,Items.lead,500,Items.titanium,240,ModItems.zinc,100,ModItems.gold,50,ModItems.siliconSteel,100,Items.surgeAlloy,80,ModItems.processor,50));
            range=800;
            unitSort=UnitSorts.strongest;
            consumesPower=true;
            consumePower(30F);
            maxAmmo=120;
            shootSound=Sounds.shootMalign;
            final float circleRotSpeed=3.5f;
            final DrawPart.PartProgress haloProgress = DrawPart.PartProgress.warmup;
            final float circleY = 20f;
            final DrawPart.PartProgress circleProgress = DrawPart.PartProgress.warmup.delay(0.9F);
            final float circleRad = 11.0F;
            final float circleStroke = 1.6F;
            final float haloY=-12f;
//            shootSound=Sounds.malignShoot;
//            loopSound=Sounds.spellLoop;
//            loopSoundVolume=1.2f;
            shootY=12;
            ammo(Liquids.hydrogen,new FlakBulletType(8.9f,75f){{
                buildingDamageMultiplier=0.5f;
                lifetime=180f;
                hitEffect=ModFx.shapeEffect2;
                shootEffect=Fx.shootSmokeSquareBig;
                trailEffect=Fx.colorSpark;
                smokeEffect=Fx.shootSmokeDisperse;
                hitColor=trailColor=lightningColor=Color.sky;
                trailWidth=2.5f;
                trailLength=20;
                homingDelay=18f;
                homingRange=240f;
                homingPower=3;
                fragBullet=new LaserBulletType(55f){{
                    colors= new Color[]{Color.sky};
                    buildingDamageMultiplier=0.5f;
                    hitEffect=Fx.hitLancer;
                    sideAngle=175.0F;
                    sideWidth=1f;
                    sideLength=42f;
                    lifetime=25f;
                    drawSize=400f;
                    pierceCap=2;
                }};
                intervalBullets=8;
                bulletInterval=10f;
                splashDamage=20;
                splashDamageRadius=72;
                splashDamagePierce=true;
                homingPower=0.2f;
                homingDelay=16f;
                homingRange=160f;
                collidesGround=true;
                collideTerrain=false;
                collidesTiles=false;
            }});
            drawer=new DrawTurret(){{parts.addAll(new ShapePart(){{
                rotateSpeed=circleRotSpeed;
                progress=haloProgress;
                color=Color.sky;
                circle=true;
                hollow=true;
                stroke=0;
                strokeTo=2;
                radius=10;
                y=haloY;
            }}, new ShapePart(){{
                progress=haloProgress;
                color=Color.sky;
                circle=false;
                hollow=true;
                stroke=0;
                strokeTo=1.4f;
                radius=6;
                sides=3;
                y=haloY;
                rotateSpeed=0;
                rotation=0;
            }}, new ShapePart(){{
                progress=haloProgress;
                color=Color.sky;
                circle=false;
                hollow=true;
                stroke=0;
                strokeTo=1.4f;
                radius=6;
                sides=3;
                y=haloY;
                rotateSpeed=0;
                rotation=180;
            }}, new ShapePart(){{
                rotateSpeed=circleRotSpeed;
                progress=haloProgress;
                color=Color.sky;
                circle=false;
                hollow=true;
                stroke=0;
                strokeTo=2;
                radius=14;
                y=haloY;
                sides=3;
            }}, new HaloPart(){{
                haloRotateSpeed=-circleRotSpeed;
                progress=haloProgress;
                shapes=3;
                tri=true;
                color=Color.sky;
                hollow=true;
                stroke=0;
                strokeTo=2;
                triLength=0;
                triLengthTo=14;
                radius=8;
                y=haloY;
                haloRadius=20;
            }}, new RegionPart(){{
                layerOffset = -0.3F;
                suffix="-a-l";
                moveX=-2f;
                moveY=1;
            }}, new RegionPart(){{
                layerOffset = -0.3F;
                suffix="-a-r";
                moveX=2f;
                moveY=1;
            }}, new RegionPart(){{
                layerOffset = -0.3F;
                layer=20;
                mirror=true;
                progress=PartProgress.warmup;
                suffix="-b-1";
                color=Color.sky;
                outline=true;
                rotation=15;
                moveX=4.8f;
            }}, new RegionPart(){{
                layerOffset = -0.3F;
                layer=20;
                mirror=true;
                progress=PartProgress.warmup;
                suffix="-b-2";
                color=Color.sky;
                outline=true;
                rotation=15;
                moveX=4.8f;
            }}, new RegionPart(){{
                layerOffset = -0.3F;
                layer=20;
                mirror=true;
                progress=PartProgress.warmup;
                suffix="-b-3";
                color=Color.sky;
                outline=true;
                moveX=4.8f;
                rotation=45;
            }});
            }};
        }};
        ModTurrets.disaster=new PowerTurret("disaster"){{
            size=3;
            final float circleY = 10f;
            final DrawPart.PartProgress circleProgress = DrawPart.PartProgress.warmup.delay(0.9F);
            final float circleRad = 11.0F;
            final float circleStroke = 1.4F;
            final float haloY=-10f;
            final float circleRotSpeed=3.5f;
            shootSound=Sounds.shootCorvus;
            requirements(Category.turret,with(Items.thorium,40,Items.titanium,35,Items.silicon,20,Items.metaglass,20));
            consumePower(18f);
            range=450;
            shootType=new LaserBulletType(){{
                shake=6;
                length=460;
                width=70;
                lifetime=55;
                damage=560;
                chargeEffect=Fx.greenLaserCharge;
                colors=new Color[]{Pal.meltdownHit.cpy().a(0.4F), Pal.meltdownHit, Color.white};
                reload=90;
            }};
            drawer=new DrawTurret(){{parts.addAll(new ShapePart(){{
                progress=circleProgress;
                y=circleY;
                color=Pal.meltdownHit;
                rotateSpeed=-circleRotSpeed;
                hollow=true;
                stroke=0;
                strokeTo=circleStroke;
                sides=4;
                radius=6;
            }}, new ShapePart(){{
                progress=circleProgress;
                y=circleY;
                color=Pal.meltdownHit;
                rotateSpeed=circleRotSpeed;
                hollow=true;
                stroke=0;
                strokeTo=circleStroke;
                sides=4;
                radius=6;
            }}, new HaloPart(){{
                triLength=0;
                triLengthTo=10;
                progress=circleProgress;
                y=circleY;
                tri=true;
                color=Pal.meltdownHit;
                haloRotateSpeed=circleRotSpeed;
                hollow=true;
                stroke=0;
                strokeTo=circleStroke;
                sides=4;
                radius=6;
            }}, new RegionPart(){{
                rotate=true;
                suffix="-l";
                moveX=-0.8f;
                rotation=5;
                progress=PartProgress.warmup;
            }}, new RegionPart(){{
                rotate=true;
                suffix="-r";
                moveX=0.8f;
                rotation=-5;
                progress=PartProgress.warmup;
            }});
            }};
            recoil=3;
            coolant=consumeCoolant(0.3F);
        }};


        ModUnits.unitType1=new UnitType("unit-type-1"){{
            canBoost=true;
            constructor=TankUnit::create;
            weapons.add(new Weapon("t-weapon"){{
                bullet = new BasicBulletType(2.5f, 9);
                reload=5;
            }});
            abilities.add(new ShieldArcAbility());
            abilities.add(new RepairFieldAbility(1,120,80));
            speed=1.5f;
            health=100;
        }};
        ModUnits.unitType2=new UnitType("unit-type-2"){{
            rotateSpeed=5.6f;
            canBoost=true;
            constructor=TankUnit::create;
            weapons.add(new Weapon("unit-type-2-weapon"){{
                layerOffset = 1.0E-4F;
                bullet=new LaserBulletType(30){{
                    length=160;
                    width=2;
                    lifetime=25;
                    reload=10;
                    colors= new Color[]{Pal.heal};
                }};
                x=0;
                y=0;
                reload=5;
                mirror=false;
                rotate=true;
                top=false;
            }});
            abilities.add(new RegenAbility());
            abilities.add(new RepairFieldAbility(1,120,96));
            buildSpeed=0.75f;
            mineSpeed=2f;
            mineTier=2;
            speed=1.2f;
            health=150;
        }};
        ModUnits.unitType3=new TankUnitType("unit-type-3"){{
            hitSize=20;
            constructor=TankUnit::create;
            treadFrames=60;
            weapons.add(new Weapon("unit-3-weapon"){{
                layerOffset = 1.0E-4F;
                bullet=new LaserBulletType(30){{
                    length=200;
                    width=14;
                    lifetime=25;
                    reload=45;
                    colors= new Color[]{Pal.heal};
                }};;
                recoil=1.4f;
                top=false;
                mirror=false;
                reload=90;
                shake=5;
                rotate=true;
                x=0;
                y=0;
            }});
            engineSize=3.6f;
            canBoost=true;
            buildSpeed=1.5f;
            mineSpeed=6;
            speed=0.75f;
            health=12000;
            abilities.add(new RepairFieldAbility(10,180,96));
            abilities.add(new ShieldRegenFieldAbility(20,60,300,80));
        }};
        ModUnits.charge=new ErekirUnitType("charge"){{
            outlineColor=Pal.gray;
            drag=0.07f;
            accel=0.09f;
            buildSpeed=4f;
            mineSpeed=3.2f;
            hitSize=10;
            hovering=true;
            constructor=ElevationMoveUnit::create;
            canDrown=false;
            shadowElevation=0.1f;
            speed=2;
            rotateSpeed=5.6f;
            health=360;
            engineOffset=7;
            engineSize=2;
            itemCapacity=10;
            useEngineElevation=false;
            researchCostMultiplier=0;
            abilities.add(new MoveEffectAbility(0.0F,-7.0f,Pal.sapBulletBack,Fx.missileTrailShort,4.0F){{teamColor=true;}});
            abilities.add(new RepairFieldAbility(18,150,96));
            for(final float f:new float[]{6f,-6f}){
                parts.add(new HoverPart(){{
                    x=6f;
                    y=f;
                    mirror=true;
                    radius=5f;
                    color=Pal.heal;
                    phase=90;
                    layerOffset=-0.001f;
                    stroke=2;
                }});
            }
            weapons.add(new Weapon("charge-weapon"){{
                recoil=1.2f;
                rotate=true;
                rotateSpeed=3.2f;
                top=false;
                y=-2;
                x=4;
                mirror=true;
                shoot=new ShootSpread(2,11.0F);
                reload=40;
                shootCone=360;
                bullet=new LaserBoltBulletType(6.4f,22){{
                    collidesTeam=true;
                    trailLength=14;
                    hitColor=trailColor=lightningColor=backColor=Pal.heal;
                    homingPower=1;
                    width=2.4f;
                    lifetime=45;
                    healPercent=6;
                }};
            }});
        }};
        ModUnits.raid=new UnitType("raid"){{
            outlineColor=Pal.gray;
            drag=0.05f;
            accel=0.06f;
            flying=true;
            hitSize=30f;
            health=1200;
            speed=2f;
            constructor=UnitEntity::create;
            targetAir=false;
            weapons.add(new Weapon(){{
                reload=20;
                mirror=false;
                x=0;
                y=0;
                bullet=new BombBulletType(30,24){{
                    lifetime=28;
                    hitColor=trailColor=lightningColor=backColor=Pal.techBlue;
                    frontColor=Color.white;
                    despawnEffect=ModFx.teachBlueBomb;
                    collidesAir=false;
                    splashDamage=50;
                    splashDamagePierce=true;
                    splashDamageRadius=30;
                }};
            }});
        }};
        ModUnits.mysticSnail=new UnitType("mystic-snail"){{
            constructor=UnitWaterMove::create;
            health=600f;
            hitSize=14f;
            drag=0.14f;
            armor=5f;
            accel=0.4f;
            speed=1.1f;
            rotateSpeed=5f;
            mineSpeed=1.8f;
            abilities.add(new RegenAbility());
            abilities.add(new RepairFieldAbility(30,120,120));
            weapons.add(new Weapon(){{
                reload=15;
                mirror=true;
                x=4;
                y=6;
                recoil=0.6f;
                shake=0.2f;
                rotate=false;
                bullet=new LaserBoltBulletType(6.4f,18){{
                    autoTarget=true;
                    collidesTeam=true;
                    backColor=frontColor=hitColor=Pal.heal;
                    healPercent=4.8f;
                }};
            }});
            weapons.add(new Weapon("mystic-snail-weapon-1"){{
                reload=45;
                mirror=false;
                rotate=true;
                rotateSpeed=5.4f;
                x=0;
                y=0;
                shake=0.5f;
                recoil=0.85f;
                bullet=new LaserBulletType(64){{
                    collidesTeam=true;
                    healPercent=7.2f;
                    colors=new Color[]{Pal.heal};
                }};
            }});
        }};
        ModUnits.anvil=new UnitType("anvil"){{
            constructor=PayloadUnit::create;
            drag=0.015f;
            hitSize=8*1.6f;
            health=800f;
            armor=16f;
            buildSpeed=3;
            buildRange=8*20;
            mineTier=3;
            mineSpeed=4f;
            flying=true;
            speed=3.6f;
            circleTarget=true;
            faceTarget=true;
            coreUnitDock=true;
            payloadCapacity=4096;
            trailLength=14;
            weapons.add(new PointDefenseBulletWeapon(){{
                range=80;
            }});
            abilities.add(new RegenAbility());
            abilities.add(new SuppressionFieldAbility());
            weapons.add(new Weapon(){{
                reload=10;
                mirror=false;
                x=0;
                y=0;
                rotate=false;
                bullet=new MissileBulletType(9,50){{
                    splashDamage=160;
                    splashDamageRadius=24;
                    homingDelay=30;
                    homingPower=0.5f;
                    homingRange=200;
                    trailEffect=Fx.none;
                    trailLength=8;
                    trailWidth=1.6f;
                    shootEffect=Fx.none;
                    trailColor=TIColor.bronzeColor;
                    frontColor=backColor=TIColor.bronzeColor;
                    hitEffect = new ExplosionEffect() {{
                        lifetime=36;
                        sparks=10;
                        sparkRad=28;
                        sparkStroke=1.2f;
                        sparkLen=15;
                        waveStroke=3;
                        waveLife=6;
                        waveRadBase=0.8f;
                        waveColor=TIColor.bronzeColor;
                        waveRad=26;
                        smokes=11;
                        smokeColor=sparkColor=TIColor.bronzeColor.cpy().a(0.85f);
                    }};
                }};
            }});
            immunities.addAll(
                    StatusEffects.wet,StatusEffects.freezing,
                    StatusEffects.burning,StatusEffects.melting,
                    StatusEffects.disarmed,StatusEffects.electrified,
                    StatusEffects.sapped,StatusEffects.slow,
                    StatusEffects.tarred,StatusEffects.unmoving,
                    StatusEffects.sporeSlowed,StatusEffects.corroded
            );
        }};


        ModBlocks.sentinelCore=new CoreBlock("sentinel-core"){{
            alwaysUnlocked=true;
            health=1400;
            size=4;
            unitType=ModUnits.unitType2;
            requirements(Category.effect,with(Items.copper,1000,Items.lead,200,Items.titanium,200));
        }};

        ModUnitBlocks.load();

        ModPlanets.planetEee=new Planet("planet-eee", Planets.sun, 1f, 3){{
            generator=new SerpuloPlanetGenerator();
            new NoiseMesh(Planets.serpulo,2,1,Color.sky,
                    1,1,1f,1f,1f);
            meshLoader = () -> new HexMesh(Planets.serpulo, 6);
            cloudMeshLoader = () -> new MultiMesh(new HexSkyMesh(this, 11, 0.15F, 0.13F, 5, (new Color()).set(Pal.spore).mul(0.9F).a(0.75F), 2, 0.45F, 0.9F, 0.38F), new HexSkyMesh(this, 1, 0.6F, 0.16F, 5, Color.white.cpy().lerp(Pal.spore, 0.55F).a(0.75F), 2, 0.45F, 1.0F, 0.41F));
            alwaysUnlocked=true;
            atmosphereRadIn=0.02f;
            atmosphereRadOut=0.3f;
            landCloudColor=Color.HSVtoRGB(210,44,92);
            allowSectorInvasion=true;
            iconColor=Color.HSVtoRGB(210,44,92);
            allowLaunchSchematics=true;
        }};
        ModSectorPresets.t1=new SectorPreset("testSector",ModPlanets.planetEee,0){{
            alwaysUnlocked=true;
        }};
        ModSectorPresets.testSector=new SectorPreset("043", ModPlanets.planetEee, 172);
        ModSectorPresets.t174=new SectorPreset("t174",ModPlanets.planetEee,174);
        ModSectorPresets.Sector15=new SectorPreset("15",Planets.serpulo,15);


        nodeRoot("e",Blocks.coreShard,()->{
            node(ModTurrets.sharpSpear,()->{
                node(ModTurrets.longsword);
                node(ModTurrets.blaze);
                node(ModTurrets.puncture,()->{
                    node(ModTurrets.burst);
                    node(ModTurrets.pureEmptiness);
                    node(ModTurrets.daytime);
                });
                node(ModTurrets.itemTurret2);
                node(ModTurrets.powerTurret4,()->{
                    node(ModTurrets.powerTurret6);
                    node(ModTurrets.itemTurret5,()->{
                        node(ModTurrets.frost);
                        node(ModTurrets.disaster);
                    });
                });
            });
            node(Blocks.mechanicalDrill,()->{
                node(ModBlocks.rockDrilling, () -> {
                    node(ModBlocks.rockCrusher);
                    node(ModBlocks.highTemperatureSmeltingPlant);
                    node(ModBlocks.highTemperatureMeltingFurnace, () -> {
                        node(ModBlocks.highSpeedDisassembler,()->{
                            node(ModBlocks.bronzeSmelter);
                        });
                    });
                });
                node(ModBlocks.assemblyMachine,()->{
                    node(ModBlocks.electricHeater,()->{
                        node(ModBlocks.heatTransmitter,()->{
                            node(ModBlocks.smallHeatTransmitter);
                        });
                        node(ModBlocks.fissionReactor);
                    });
                });
                node(ModBlocks.canyonBatteryCompressor,()->{
                    node(ModBlocks.archipelagoBatteryCompressor);
                });
                node(ModBlocks.canyonBatteryGenerator,()->{
                    node(ModBlocks.archipelagoBatteryGenerator);
                });
                node(ModBlocks.largeThoriumReactor);
                node(ModBlocks.smallDrillBit,()->{
                    node(ModBlocks.laserBeamDrill,()->{
                        node(ModBlocks.percussionDrilling);
                    });
                });
                node(ModBlocks.siliconSteelMixer, () -> {//硅钢混合机
                    node(ModBlocks.photoLithographyMachine);
                    node(ModBlocks.aromatizationMachine);
                    node(ModBlocks.electrolyticSeparator, () -> {
                        node(ModBlocks.glassAssemblyMachine,()->{
                            node(ModBlocks.waterTankFillingMachine);
                            node(ModBlocks.waterDispenser);
                        });
                    });//电解分离机
                    node(Blocks.plastaniumCompressor, () -> {
                        node(ModBlocks.largeSiliconSteelMixer);
                        node(ModBlocks.petroleumFractionatingTower);
                    });
                });
                node(ModBlocks.laserEnergyNode,()->{
                    node(ModBlocks.overclockStateFieldProjection);
                    node(ModBlocks.fluidThermalEnergyGenerator,()->{
                        node(ModBlocks.dieselGenerator);
                    });
                    node(ModBlocks.mechanicalGenerator,()->{
                        node(ModBlocks.hydroelectricGenerator);
                    });
                });
            });
            node(Blocks.titaniumConveyor,()->{
                node(ModBlocks.fastItemBridge,()->{
                    node(ModBlocks.itemTrack);
                    node(ModBlocks.logisticsPipeline);
                });
            });
            node(ModUnitBlocks.secondaryModificationFactory,()->{
                node(ModUnits.raid);
                node(ModUnits.mysticSnail);
            });
            node(ModBlocks.explosive);
            node(ModBlocks.outpostCore);
            nodeProduce(Items.copper, () -> {
                nodeProduce(Items.lead, () -> {
                    nodeProduce(ModItems.canyonBattery,()->{
                        nodeProduce(ModItems.archipelagoBattery,()->{});
                    });
                    nodeProduce(Items.titanium, () -> {
                        nodeProduce(Liquids.cryofluid, () -> {});
                        nodeProduce(Items.thorium, () -> {});
                    });
                });
                nodeProduce(ModItems.metaglassBottle,()->{
                    nodeProduce(ModItems.wateryMetaglassBottle,()->{});
                });
                nodeProduce(ModItems.tin, () -> {
                    nodeProduce(ModItems.bronze,()->{});
                });
                nodeProduce(ModItems.zinc, () -> {});
                nodeProduce(Items.coal, () -> {
                    nodeProduce(Items.sand, () -> {
                        nodeProduce(Items.scrap, () -> {});
                        nodeProduce(ModItems.rock, () -> {});
                    });
                    nodeProduce(Liquids.oil, () -> {
                        nodeProduce(Liquids.arkycite,()->{});
                        nodeProduce(ModLiquids.gasoline, () -> {});
                        nodeProduce(ModLiquids.diesel, () -> {});
                        nodeProduce(ModLiquids.kerosene, () -> {});
                    });
                    nodeProduce(Items.graphite, () -> {
                    });
                    nodeProduce(Items.silicon, () -> {
                        nodeProduce(ModItems.heatConductionComponent,()->{});
                        nodeProduce(ModItems.processor,()->{});
                        nodeProduce(ModItems.siliconSteel, () -> {});
                    });
                });
                nodeProduce(ModItems.gold, () -> {});
                nodeProduce(Liquids.water, () -> {
                    nodeProduce(ModLiquids.lava, () -> {});
                    nodeProduce(Liquids.hydrogen, () -> {});
                });
            });
            node(ModSectorPresets.Sector15);
        });


        ModPlanets.planetEee.techTree=TechTree.nodeRoot("PlanetEee",ModBlocks.sentinelCore,()->{});
//        ModPlanets.planetEee.techTree= TechTree.nodeRoot("eee", Blocks.coreShard, () -> {
//            node(ModSectorPresets.t1, () -> {
//                node(ModSectorPresets.testSector, () -> {
//                    node(ModSectorPresets.t174);
//                });
//            });
//            node(Blocks.mechanicalDrill, () -> {
//                node(Blocks.graphitePress, () -> {
//                    node(Blocks.pneumaticDrill, () -> {
//                        node(ModBlocks.smallDrillBit, () -> {
//                        });
//                        node(Blocks.laserDrill, () -> {
//                        });
//                    });
//                    node(Blocks.siliconSmelter, () -> {
//                        node(Blocks.illuminator, () -> {
//                        });
//                        node(Blocks.kiln, () -> {
//                        });
//                        node(ModBlocks.siliconSteelMixer, () -> {//硅钢混合机
//                            node(ModBlocks.electrolyticSeparator, () -> {
//                            });//电解分离机
//                            node(Blocks.plastaniumCompressor, () -> {
//                                node(ModBlocks.petroleumFractionatingTower);
//                            });
//                        });
//                        node(Blocks.pulverizer, () -> {
//                            node(Blocks.melter, () -> {
//                                node(Blocks.separator, () -> {
//                                });
//                                node(ModBlocks.rockDrilling, () -> {
//                                    node(ModBlocks.highTemperatureSmeltingPlant);
//                                    node(ModBlocks.highTemperatureMeltingFurnace, () -> {
//                                        node(ModBlocks.highSpeedDisassembler);
//                                    });
//                                });
//                            });
//                        });
//                    });
//                });
//                node(Blocks.combustionGenerator, () -> {
//                    node(ModBlocks.laserEnergyNode, () -> {//激光电力节点
//                        node(Blocks.steamGenerator, () -> {
//                            node(ModBlocks.fluidThermalEnergyGenerator, () -> {
//                                node(ModBlocks.dieselGenerator);
//                            });
//                        });//涡轮发电机
//                    });
//                });
//            });
//            node(Blocks.conveyor, () -> {
//                node(Blocks.titaniumConveyor, () -> {
//                    node(ModBlocks.fastItemBridge);
//                });
//            });
//            nodeProduce(ModItems.experimentalExplosives, () -> {
//            });
//            node(ModBlocks.laboratory, () -> {
//            });
//            node(Blocks.duo, () -> {
//                node(Blocks.copperWall, () -> {
//                    node(Blocks.copperWallLarge, () -> {
//                        node(Blocks.titaniumWall, () -> {
//                            node(Blocks.titaniumWallLarge, () -> {
//                            });
//                        });
//                    });
//                });
//                node(Blocks.hail, () -> {
//                    node(Blocks.salvo, () -> {
//                        node(ModTurrets.puncture);
//                    });
//                    node(Blocks.scorch, () -> {
//                    });
//                });
//                node(Blocks.scatter, () -> {
//                });
//                node(ModTurrets.sharpSpear, () -> {
//                    node(ModTurrets.itemTurret2, () -> {
//                    });
//                });
//                node(Blocks.arc, () -> {
//                    node(ModTurrets.powerTurret7, () -> {
//                        node(ModTurrets.powerTurret6);
//                    });
//                    node(Blocks.wave, () -> {
//                    });
//                    node(Blocks.lancer, () -> {
//                        node(Blocks.foreshadow);
//                        node(ModTurrets.itemTurret5, () -> {
//                            node(ModTurrets.frost);
//                            node(ModTurrets.ash);
//                        });
//                        node(Blocks.meltdown, () -> {
//                            node(ModTurrets.disaster);
//                        });
//                    });
//                    node(ModTurrets.powerTurret4, () -> {
//                    });
//                });
//            });
//            nodeProduce(Items.copper, () -> {
//                nodeProduce(Items.lead, () -> {
//                    nodeProduce(Items.titanium, () -> {
//                        nodeProduce(Liquids.cryofluid, () -> {
//                        });
//                        nodeProduce(Items.thorium, () -> {
//                        });
//                    });
//                });
//                nodeProduce(ModItems.tin, () -> {
//                });
//                nodeProduce(ModItems.zinc, () -> {
//                });
//                nodeProduce(Items.coal, () -> {
//                    nodeProduce(Items.sand, () -> {
//                        nodeProduce(Items.scrap, () -> {
//                        });
//                        nodeProduce(ModItems.rock, () -> {
//                        });
//                    });
//                    nodeProduce(Liquids.oil, () -> {
//                        nodeProduce(ModItems.gasoline, () -> {
//                        });
//                        nodeProduce(ModItems.diesel, () -> {
//                        });
//                        nodeProduce(ModItems.kerosene, () -> {
//                        });
//                    });
//                    nodeProduce(Items.graphite, () -> {
//                    });
//                    nodeProduce(Items.silicon, () -> {
//                        nodeProduce(ModItems.siliconSteel, () -> {
//                        });
//                    });
//                });
//                nodeProduce(ModItems.gold, () -> {
//                });
//                nodeProduce(Liquids.water, () -> {
//                    nodeProduce(ModItems.lava, () -> {
//                    });
//                    nodeProduce(Liquids.hydrogen, () -> {
//                    });
//                });
//            });
//        });

    }
}
//????/??/??