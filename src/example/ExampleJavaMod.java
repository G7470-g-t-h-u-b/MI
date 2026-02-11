package example;

import arc.*;
import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Fill;
import arc.graphics.g2d.Lines;
import arc.struct.Seq;
import arc.util.*;
import mindustry.content.*;
import mindustry.entities.Effect;
import mindustry.entities.UnitSorts;
import mindustry.entities.abilities.*;
import mindustry.entities.bullet.*;
import mindustry.entities.effect.ExplosionEffect;
import mindustry.entities.effect.MultiEffect;
import mindustry.entities.effect.WaveEffect;
import mindustry.entities.part.DrawPart;
import mindustry.entities.part.HaloPart;
import mindustry.entities.part.ShapePart;
import mindustry.entities.pattern.ShootAlternate;
import mindustry.entities.pattern.ShootPattern;
import mindustry.entities.pattern.ShootSpread;
import mindustry.game.EventType.*;
import mindustry.gen.TankUnit;
import mindustry.graphics.Pal;
import mindustry.graphics.g3d.*;
import mindustry.maps.planet.SerpuloPlanetGenerator;
import mindustry.mod.*;
import mindustry.type.*;
import mindustry.type.unit.MissileUnitType;
import mindustry.type.unit.TankUnitType;
import mindustry.ui.dialogs.*;
import mindustry.world.blocks.defense.turrets.ItemTurret;
import mindustry.world.blocks.defense.turrets.LiquidTurret;
import mindustry.world.blocks.defense.turrets.PowerTurret;
import mindustry.world.blocks.distribution.BufferedItemBridge;
import mindustry.world.blocks.distribution.Duct;
import mindustry.world.blocks.environment.OreBlock;
import mindustry.world.blocks.heat.HeatConductor;
import mindustry.world.blocks.heat.HeatProducer;
import mindustry.world.blocks.power.BeamNode;
import mindustry.world.blocks.power.ConsumeGenerator;
import mindustry.world.blocks.power.HeaterGenerator;
import mindustry.world.blocks.power.NuclearReactor;
import mindustry.world.blocks.production.Drill;
import mindustry.world.blocks.production.GenericCrafter;
import mindustry.world.blocks.production.HeatCrafter;
import mindustry.world.blocks.production.Separator;
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
            sparkRad=10;
            sparkLen=6;
            sparkStroke=5;
            smokeSizeBase=1;
            smokeSize=1.7f;
            waveRad=30;
            smokeRad=30;
            smokes=5;
            sparks=30;
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
                Lines.circle(e.x,e.y,40);
                Lines.circle(e.x,e.y,50);
                Lines.square(e.x,e.y,60,0);
                Lines.square(e.x,e.y,60,45);
            });
        });


        ModStatusEffects.erosionX=new StatusEffect("erosion-x"){{
            healthMultiplier=0.8f;
            reloadMultiplier=0.8f;
            speedMultiplier=0.6f;
            damage=0.2f;
        }};


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
        ModItems.tin=new Item("tin",Color.HSVtoRGB(233,16,44)){{
            hardness=0;
        }};
        ModItems.zinc=new Item("zinc",Color.HSVtoRGB(240,12,71)){{
            hardness=2;
        }};
        ModItems.siliconSteel=new Item("silicon-steel",Color.HSVtoRGB(240,14,53));
        ModItems.gold=new Item("gold",Color.HSVtoRGB(50,93,100)){{
            hardness=1;
        }};
        ModItems.rock=new Item("rock",Color.HSVtoRGB(240,7,50));
        ModItems.lava=new Liquid("lava",Color.red){{
            temperature=1.4f;
            viscosity=0.75f;
            effect=StatusEffects.melting;
        }};
        ModItems.iron=new Item("iron",Color.HSVtoRGB(233,16,25));
        ModItems.frostAlloy=new Item("frost-alloy",Color.HSVtoRGB(196,46,89));
        ModItems.canyonBattery=new Item("canyon-battery",Color.HSVtoRGB(232,47,77)){{charge=0.4f;}};
        ModItems.archipelagoBattery=new Item("archipelago-battery",Color.HSVtoRGB(97,58,75)){{charge=0.6f;}};
        ModItems.processor=new Item("processor",Color.HSVtoRGB(226,24,65));
        ModItems.kerosene=new Liquid("kerosene",Color.HSVtoRGB(29,43,97)){{
            flammability=1.2f;
            explosiveness=1.2f;
        }};//煤油
        ModItems.diesel=new Liquid("diesel",Color.HSVtoRGB(32,63,91)){{
            flammability=0.2f;
            explosiveness=0.4f;
        }};//柴油
        ModItems.gasoline=new Liquid("gasoline",Color.HSVtoRGB(32,81,86)){{
            flammability=1;
            explosiveness=0.9f;
        }};//汽油


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
            requirements(Category.crafting,with(Items.titanium,30,Items.copper,20,Items.metaglass,20,ModItems.zinc,15));
            consumeLiquids(LiquidStack.with(Liquids.water,0.3f));
            consumePower(1f);
            outputLiquids=new LiquidStack[]{new LiquidStack(Liquids.hydrogen, 0.2f)};
//            drawer=new DrawMulti(){{
//                new DrawDefault();
//                new DrawLiquidTile(Liquids.water,1f);
//            }};
        }};
        ModBlocks.rockDrilling=new GenericCrafter("rock-drilling"){{
            health=240;
            size=3;
            craftTime=60;
            requirements(Category.crafting,with(Items.copper,55,Items.titanium,40,Items.graphite,20));
            consumePower(3f);
            consumeLiquid(Liquids.water,0.5f);
            outputItems=ItemStack.with(ModItems.rock,1);
        }};
        ModBlocks.highTemperatureMeltingFurnace=new GenericCrafter("high-temperature-melting-furnace"){{
            health=200;
            size=2;
            craftTime=30f;
            requirements(Category.crafting,with(Items.copper,35,Items.titanium,25,Items.graphite,10));
            consumeItems(ItemStack.with(ModItems.rock,1));
            consumePower(2f);
            outputLiquid=new LiquidStack(ModItems.lava,0.4f);
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
        ModBlocks.largeThoriumReactor=new NuclearReactor("large-thorium-reactor"){{
            size=4;
            health=850;
            requirements(Category.power,with(Items.titanium,30,Items.silicon,20,Items.graphite,10,ModItems.siliconSteel,15));
            powerProduction=18f;
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
            consumeLiquids(LiquidStack.with(ModItems.lava,0.2f));
            results=ItemStack.with(new Object[]{Items.thorium,1,ModItems.zinc,1,ModItems.tin,2,ModItems.gold,1});
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
            requirements(Category.power,with(Items.lead,300,Items.metaglass,80,Items.titanium,100,Items.graphite,150,Items.thorium,60,Items.silicon,80));
            powerProduction=12f;
            consume(new ConsumeItemRadioactive());
            consumeLiquid(Liquids.cryofluid,0.75f);
            heatOutput=32;
            drawer=new DrawMulti(new DrawDefault(),new DrawHeatOutput());
        }};
        ModBlocks.smallHeatTransmitter=new HeatConductor("small-heat-transmitter"){{
            size=1;
            requirements(Category.crafting,with(Items.graphite,10,Items.lead,8));
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
            outputLiquids = LiquidStack.with(ModItems.diesel,0.2,ModItems.kerosene,0.2,ModItems.gasoline,0.2);
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
            requirements(Category.production,with(Items.copper,10,Items.graphite,5));
            drillTime=400f;
            blockedItems=Seq.with(ModItems.uranium,Items.thorium);
            hardnessDrillMultiplier=2;
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
            consumeLiquid(ModItems.diesel,0.2f);
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
            requirements(Category.distribution,with(Items.phaseFabric,2));
            health=60;
            speed=100;
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


        ModBlocks.outpostCore=new CoreBlock("outpost-core"){{
            requirements(Category.effect, BuildVisibility.shown,with(Items.titanium,2000,Items.copper,1800,Items.silicon,1200));
            health=1200;
            size=3;
            unitType = UnitTypes.alpha;
        }};


        ModBlocks.metalCrusher=new MultiFormulaFactory("metal-crusher"){{
            health=100;
            size=1;
            requirements(Category.crafting,with(Items.copper,10,Items.lead,8,Items.graphite,5));
            consumePower(2);
            plans=Seq.with(
                    new ItemPlan(new ItemStack(Items.copper,1),40f,with(Items.lead,1)),
                    new ItemPlan(new ItemStack(Items.lead,1),40f,with(Items.copper,1))
            );
        }};


        ModTurrets.itemTurret1=new ItemTurret("item-turret-1"){{
            requirements(Category.turret, with(Items.copper, 40,ModItems.zinc,10,ModItems.gold,5));
            ammo(ModItems.experimentalExplosives, new MissileBulletType(1.5f,32){{
                ammoMultiplier=2;
                splashDamage=4.5f;
                splashDamageRadius=2.5f;
                makeFire=true;
                lifetime=160;
            }},Items.surgeAlloy,new BasicBulletType(0,0){{}});
            displayAmmoMultiplier=true;
            range=160;
            shoot = new ShootPattern();
            drawer = new DrawTurret(){{
                parts.addAll();
            }};
            consumePower(2f);
            coolant = consumeCoolant(0.1f);
        }};
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
        ModTurrets.itemTurret3=new ItemTurret("item-turret-3"){{
            targetGround=false;
            range=192;
            inaccuracy=3f;
            maxAmmo=30;
            drawer = new DrawTurret(){{
                parts.addAll();
            }};
            size=2;
            reload=2;
            requirements(Category.turret,with(Items.copper,45,Items.lead,15,ModItems.tin,8));
            ammo(Items.lead,new FlakBulletType(4f,12){{
                hitColor = this.backColor = this.trailColor = Pal.blastAmmoBack;
                ammoMultiplier=3;
                lifetime=160;
//                range=192;
            }},ModItems.siliconSteel,new MissileBulletType(3.8f,11){{
                hitColor = this.backColor = this.trailColor = Pal.blastAmmoBack;
                ammoMultiplier=5;
//                range=192;
                lifetime=160;
            }});

        }};
        ModTurrets.itemTurret5=new ItemTurret("item-turret-5"){{
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
        ModTurrets.ash=new ItemTurret("ash"){{
            requirements(Category.turret,with(Items.titanium,160,ModItems.zinc,120,ModItems.gold,80,ModItems.siliconSteel,100,Items.surgeAlloy,50));
            size=4;
            reload=35f;
            range=320f;
            ammo(new Object[]{Items.surgeAlloy,new BulletType(0f,0f){ {
                shootEffect=Fx.shootBig;
                smokeEffect=Fx.shootSmokeMissileColor;
                hitColor=Pal.redLight;
                spawnUnit=new MissileUnitType("ash-missile"){{
                    speed=5f;
//                    range=280f;
                    lifetime=480f;
                    engineColor=trailColor=Pal.redLight;
                    engineSize=3.1f;
                    engineOffset=10f;
                    rotateSpeed=0.125f;
                    trailLength=20;
                    missileAccelTime=45f;
                    targetAir=true;
                    targetUnderBlocks=false;
                    health=220f;
                    homingPower=3f;
                    homingDelay=20f;
                    homingRange=240f;
                    weapons.add(new Weapon(){{
                        shootCone=360;
                        reload=1f;
                        deathExplosionEffect=Fx.massiveExplosion;
                        shootOnDeath=true;
                        shake=10;
                        bullet=new ExplosionBulletType(800f, 70f);
                        hitColor=Pal.redLight;
                        shootEffect=new MultiEffect(new Effect[]{Fx.massiveExplosion, Fx.scatheExplosion, Fx.scatheLight, new WaveEffect() {{
                            lifetime = 11f;
                            strokeFrom = 4f;
                            sizeTo = 130.0F;
                        }}});
                        buildingDamageMultiplier=0.8f;
                        fragLifeMin=0.1f;
                        fragBullets=8;
                        fragBullet=new ArtilleryBulletType(3.5f,28f){{
                            buildingDamageMultiplier=0.9f;
                            drag=0.03f;
                            hitEffect=Fx.massiveExplosion;
                            lifetime=20;
                            width=height=20f;
                            splashDamageRadius=45f;
                            splashDamage=50;
                            backColor=trailColor=hitColor=Pal.redLight;
                            smokeEffect=Fx.shootBigSmoke2;
                            despawnShake=5f;
                            lightRadius=32f;
                            lightColor=Pal.redLight;
                            trailLength=22;
                            trailWidth=3.6f;
                        }};
                    }});
                    abilities.add(new MoveEffectAbility(){{
                        effect=Fx.missileTrailSmoke;
                        rotation=180;
                        color=Color.grays(0.6F).lerp(Pal.redLight,0.5F).a(0.4F);
                        interval=6.4f;
                    }});
                }};

            }},
            ModItems.siliconSteel,new BulletType(0f,0f){{
                shootEffect=Fx.shootBig;
                smokeEffect=Fx.shootSmokeMissileColor;
                hitColor=Pal.redLight;
                spawnUnit=new MissileUnitType("ash-missile-silicon-steel"){{
                    speed=5f;
//                    range=280f;
                    lifetime=480f;
                    engineColor=trailColor=Pal.redLight;
                    engineSize=3.1f;
                    engineOffset=10f;
                    rotateSpeed=0.125f;
                    trailLength=20;
                    missileAccelTime=45f;
                    targetAir=true;
                    targetUnderBlocks=false;
                    health=220f;
                    homingPower=1f;
                    homingDelay=20f;
                    homingRange=240f;
                    weapons.add(new Weapon(){{
                        shootCone=360;
                        reload=1f;
                        deathExplosionEffect=Fx.massiveExplosion;
                        shootOnDeath=true;
                        shake=10;
                        bullet=new ExplosionBulletType(500f, 60f);
                        hitColor=Pal.redLight;
                        shootEffect=new MultiEffect(new Effect[]{Fx.massiveExplosion, Fx.scatheExplosion, Fx.scatheLight, new WaveEffect() {{
                            lifetime = 11f;
                            strokeFrom = 4f;
                            sizeTo = 130.0F;
                        }}});
                        buildingDamageMultiplier=0.8f;
                        fragLifeMin=0.1f;
                        fragBullets=8;
                        fragBullet=new ArtilleryBulletType(3.5f,28f){{
                            buildingDamageMultiplier=0.9f;
                            drag=0.03f;
                            hitEffect=Fx.massiveExplosion;
                            lifetime=20;
                            width=height=20f;
                            splashDamageRadius=45f;
                            splashDamage=50;
                            backColor=trailColor=hitColor=Pal.redLight;
                            smokeEffect=Fx.shootBigSmoke2;
                            despawnShake=5f;
                            lightRadius=32f;
                            lightColor=Pal.redLight;
                            trailLength=22;
                            trailWidth=3.6f;
                        }};
                    }});
                    abilities.add(new MoveEffectAbility(){{
                        effect=Fx.missileTrailSmoke;
                        rotation=180;
                        color=Color.grays(0.6F).lerp(Pal.redLight,0.5F).a(0.4F);
                        interval=6.4f;
                    }});
                }};
            }},ModItems.frostAlloy,new BasicBulletType(0,0){{
                shootEffect=Fx.shootBig;
                smokeEffect=Fx.shootSmokeMissileColor;
                hitColor=Pal.redLight;
                spawnUnit=new MissileUnitType("ash-missile-frost-alloy"){{//导弹
                    speed=5f;
                    lifetime=80;
                    engineColor=this.trailColor=Color.valueOf("52a3cc");
                    engineLayer=100;
                    engineSize=3f;
                    engineOffset=10f;
                    rotateSpeed=0.875f;
                    trailLength=21;
                    missileAccelTime=24f;
                    lowAltitude=false;
                    targetAir=true;
                    weapons.add(new Weapon(){ {
                        shootCone=360f;
                        rotate=true;
                        rotationLimit=rotateSpeed=0;
                        reload=0.1f;
                        deathExplosionEffect=Fx.massiveExplosion;
                        shootOnDeath=true;
                        shake=10f;
                        bullet=new ExplosionBulletType(1500,40){{
                            lightning=2;
                            lightColor=Color.sky;
                            lightningDamage=40;
                            lightningLength=20;
                            shootEffect=new MultiEffect(new Effect[]{Fx.massiveExplosion, Fx.scatheExplosionSmall});
                            buildingDamageMultiplier=0.9f;
                            fragLifeMin=80;
                            fragBullets=5;
                            fragSpread=20;
                            fragBullet=new BulletType(){{
                                shootEffect=Fx.shootBig;
                                smokeEffect=Fx.shootSmokeMissileColor;
                                hitColor=engineColor;
                                spawnUnit = new MissileUnitType("ash-missile-frost-alloy-frag"){{//碎片导弹
                                    speed=5f;
                                    lifetime=280;
                                    engineColor=trailColor=Color.valueOf("52a3cc");
                                    engineLayer=110f;
                                    engineOffset=8f;
                                    trailLength=16;
                                    lowAltitude=false;
                                    health=200;
                                    weapons.add(new Weapon(){{
                                        shootCone=360;
                                        shake=10;
                                        bullet = new ExplosionBulletType(200f, 35f){{
                                            lightning=2;
                                            lightColor=Color.sky;
                                            lightningDamage=40;
                                            lightningLength=20;
                                            shootEffect = new MultiEffect(new Effect[]{Fx.massiveExplosion, Fx.scatheExplosionSmall, Fx.scatheLightSmall, new WaveEffect(){{
                                                lifetime=10;

                                            }}});
                                        }};
                                    }});
                                    abilities.add(new MoveEffectAbility(){{
                                        effect=Fx.missileTrailSmokeSmall;
                                        rotation=180;
                                        color = Color.grays(0.6f).lerp(Color.valueOf("52a3cc"), 0.8f).a(0.6f);
                                        interval=5;
                                    }});
                                }};
                            }};
                        }};
                    }});
                    abilities.add(new MoveEffectAbility(){{
                        effect=Fx.missileTrailSmokeSmall;
                        rotation=180;
                        color = Color.grays(0.6f).lerp(Color.valueOf("52a3cc"), 0.8f).a(0.6f);
                        interval=5;
                    }});
                }};
            }}});
        }};
        ModTurrets.puncture=new ItemTurret("puncture"){{
            inaccuracy=0.1f;
            size=3;
            requirements(Category.turret,with(Items.titanium,100,Items.graphite,80,ModItems.zinc,20,ModItems.siliconSteel,20));
            range=280;
            reload=15;
            maxAmmo=30;
            drawer=new DrawTurret(){{parts.addAll();}};
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
                shootEffect=new MultiEffect(new Effect[]{Fx.shootBigColor,Fx.colorSparkBig});
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
            range=180;
            shootCone=45;
            ammoUseEffect=Fx.none;
            health=380;
            coolant=this.consumeCoolant(0.15F);
            ammo(Items.coal,new BulletType(5,22){{
                size=2;
                ammoMultiplier=5;
                lifetime=50;
                hitSize=7.2f;
                pierce=true;
                pierceCap=-1;
                statusDuration=300;
                shootEffect=Fx.shootSmallFlame;
                hitEffect=Fx.hitFlameSmall;
                despawnEffect=Fx.none;
                status=StatusEffects.burning;
                hittable=false;
                keepVelocity=false;
                drawer=new DrawTurret(){{parts.addAll();}};
            }},Items.pyratite,new BulletType(5.2f,35){{
                ammoMultiplier=5;
                lifetime=120;
                hitSize=7.2f;
                pierce=true;
                pierceCap=-1;
                statusDuration=600;
                shootEffect=Fx.shootSmallFlame;
                hitEffect=Fx.hitFlameSmall;
                despawnEffect=Fx.none;
                status=StatusEffects.burning;
                hittable=false;
                keepVelocity=false;
            }},Items.blastCompound,new BulletType(5.2f,55){{
                ammoMultiplier=5;
                lifetime=120;
                hitSize=7.5f;
                pierce=true;
                pierceCap=-1;
                splashDamage=35;
                splashDamageRadius=32;
                statusDuration=900;
                shootEffect=Fx.shootSmallFlame;
                hitEffect=Fx.hitFlameSmall;
                despawnEffect=Fx.none;
                status=StatusEffects.burning;
                hittable=false;
                keepVelocity=false;
            }},ModItems.experimentalExplosives,new BulletType(5.2f,65){{
                ammoMultiplier=5;
                lifetime=120;
                hitSize=7.5f;
                pierce=true;
                pierceCap=-1;
                splashDamage=40;
                splashDamageRadius=40;
                statusDuration=900;
                shootEffect=Fx.shootSmallFlame;
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
            shootCone=3;
            reload=5;
            maxAmmo=60;
            range=320;
            coolant = consumeCoolant(0.2f);
            heatRequirement=24;
            requirements(Category.turret,with(Items.copper,800,Items.lead,100,Items.thorium,80,Items.titanium,80,ModItems.siliconSteel,50,ModItems.processor,20));
            ammo(Items.lead,new BasicBulletType(8f,12){{
                width=height=16;
                velocityRnd=0.1f;
                collidesTiles=false;
                shootEffect=Fx.shootBig2;
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
                width=height=16;
                velocityRnd=0.1f;
                collidesTiles=false;
                shootEffect=Fx.shootBig2;
                smokeEffect=Fx.shootSmokeDisperse;
                frontColor=Pal.techBlue;
                trailEffect=Fx.disperseTrail;
                trailChance=0.4f;
                trailSpread=2.4f;
                hitEffect=despawnEffect=Fx.hitBulletColor;
                backColor=trailColor=hitColor=Pal.techBlue;
                ammoMultiplier=2f;
                lifetime=40;
            }},Items.graphite,new BasicBulletType(8.5f,38){{
                width=height=16;
                velocityRnd=0.1f;
                collidesTiles=false;
                shootEffect=Fx.shootBig2;
                smokeEffect=Fx.shootSmokeDisperse;
                frontColor=Pal.graphiteAmmoFront;
                hitEffect=despawnEffect=Fx.hitBulletColor;
                backColor=trailColor=hitColor=Pal.graphiteAmmoBack;
                ammoMultiplier=3f;
                lifetime=40;
                trailLength=6;
            }},ModItems.siliconSteel,new BasicBulletType(8.5f,40){{
                width=height=16;
                velocityRnd=0.1f;
                collidesTiles=false;
                shootEffect=Fx.shootBig2;
                smokeEffect=Fx.shootSmokeDisperse;
                frontColor=Pal.graphiteAmmoFront;
                hitEffect=despawnEffect=Fx.hitBulletColor;
                backColor=trailColor=hitColor=Pal.graphiteAmmoBack;
                ammoMultiplier=4f;
                lifetime=40;
                trailLength=6;
                homingPower=5;
                homingRange=200;
                splashDamage=5;
                splashDamageRadius=12;
            }},Items.thorium,new BasicBulletType(8.2f,45){{
                reloadMultiplier=0.8f;
                width=height=16;
                velocityRnd=0.1f;
                collidesTiles=false;
                shootEffect=Fx.shootBig2;
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
                width=height=16;
                velocityRnd=0.1f;
                collidesTiles=false;
                shootEffect=Fx.shootBig2;
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
                    shootEffect=Fx.shootBig2;
                    smokeEffect=Fx.shootSmokeDisperse;
                    frontColor=Pal.plastaniumFront;
                    trailEffect=Fx.disperseTrail;
                    trailChance=0.4f;
                    trailSpread=1f;
                    hitEffect=despawnEffect=Fx.hitBulletColor;
                    backColor=trailColor=hitColor=Pal.plastaniumBack;
                    lifetime=8;
                }};
            }},Items.blastCompound,new BasicBulletType(8.2f,45){{
                splashDamage=30;
                splashDamageRadius=60;
                reloadMultiplier=0.8f;
                width=height=16;
                velocityRnd=0.1f;
                collidesTiles=false;
                shootEffect=Fx.shootBig2;
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
                width=height=16;
                velocityRnd=0.1f;
                collidesTiles=false;
                shootEffect=Fx.shootBig2;
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
                haloRotateSpeed=-3;
                haloRadius=16;
            }}
            );}};
            shoot=new ShootAlternate(){{
                spread=4.2f;
                shots=4;
                barrels=4;
            }};
        }};
        ModTurrets.daytime=new ItemTurret("daytime"){{
            recoil=3.2f;
            shake=6;
            inaccuracy=1;
            range=380;
            size=4;
            shootY=4;
            maxAmmo=10;
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
            final Color turretC=Color.black;
            ammo(Items.surgeAlloy,new FlakBulletType(7f,200f){{
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
            recoil=3;
            inaccuracy=5;
            xRand=2f;
            size=4;
            requirements(Category.turret,with(Items.copper,1000,Items.lead,500,Items.titanium,240,ModItems.zinc,100,ModItems.gold,50,ModItems.siliconSteel,100,Items.surgeAlloy,80,ModItems.processor,50));
            range=800;
            unitSort=UnitSorts.strongest;
            consumesPower=true;
            consumePower(30F);
            maxAmmo=120;
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
            drawer=new DrawTurret(){{parts.addAll(new DrawPart[]{new ShapePart(){{
                rotateSpeed=circleRotSpeed;
                progress=haloProgress;
                color=Color.sky;
                circle=true;
                hollow=true;
                stroke=0;
                strokeTo=2;
                radius=10;
                y=haloY;
            }},new ShapePart(){{
                progress=haloProgress;
                color=Color.sky;
                circle=false;
                hollow=true;
                stroke=0;
                strokeTo=2;
                radius=6;
                sides=3;
                y=haloY;
                rotateSpeed=0;
                rotation=0;
            }},new ShapePart(){{
                progress=haloProgress;
                color=Color.sky;
                circle=false;
                hollow=true;
                stroke=0;
                strokeTo=2;
                radius=6;
                sides=3;
                y=haloY;
                rotateSpeed=0;
                rotation=180;
            }},new ShapePart(){{
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
            }},new HaloPart(){{
                rotateSpeed=circleRotSpeed;
                haloRotateSpeed=circleRotSpeed;
                progress=haloProgress;
                shapes=3;
                tri=true;
                color=Color.sky;
                hollow=false;
                stroke=0;
                strokeTo=2;
                triLength=6;
                triLengthTo=10;
                radius=8;
                y=haloY;
            }},new HaloPart(){{
                haloRotateSpeed=-circleRotSpeed;
                progress=haloProgress;
                shapes=6;
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
            }},new HaloPart(){{
                haloRotateSpeed=0;
                haloRotation=180;
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
            }}
            });
            }};
        }};
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
            shootType=new LaserBoltBulletType(6.2f,15){{
                buildingDamageMultiplier=0.5f;
                lifetime=60;
                healPercent=6.5f;
                collidesTeam=true;
                backColor=Pal.heal;
                frontColor=Color.white;
            }};
        }};
        ModTurrets.powerTurret7=new PowerTurret("power-turret-7"){{
            health=180;
            requirements(Category.turret,with(Items.copper,45,Items.lead,20,Items.titanium,10));
            consumePower(4f);
            size=1;
            reload=30f;
            range=120f;
            shootEffect=Fx.lightningShoot;
            coolant=consumeCoolant(0.1F);
            drawer=new DrawTurret(){{parts.addAll();}};
            shootType=new LightningBulletType(){{
                damage=22;
                lightningLength=32;
                buildingDamageMultiplier=0.25f;
                healPercent=6;
                lightningType=new BulletType(90,2){{
                    lifetime=Fx.lightning.lifetime;
                    hitEffect=Fx.hitLancer;
                    lightColor=Pal.heal;
                    status=StatusEffects.shocked;
                }};
            }};
        }};
        ModTurrets.disaster=new PowerTurret("disaster"){{
            size=3;
            final DrawPart.PartProgress haloProgress = DrawPart.PartProgress.warmup;
            final float circleY = 10f;
            final DrawPart.PartProgress circleProgress = DrawPart.PartProgress.warmup.delay(0.9F);
            final float circleRad = 11.0F;
            final float circleStroke = 1.4F;
            final float haloY=-10f;
            final float circleRotSpeed=3.5f;
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
            drawer=new DrawTurret(){{parts.addAll(new DrawPart[]{new ShapePart(){{
                progress=circleProgress;
                y=circleY;
                color=Pal.meltdownHit;
                rotateSpeed=-circleRotSpeed;
                hollow=true;
                stroke=0;
                strokeTo=circleStroke;
                sides=4;
                radius=6;
            }},new ShapePart(){{
                progress=circleProgress;
                y=circleY;
                color=Pal.meltdownHit;
                rotateSpeed=circleRotSpeed;
                hollow=true;
                stroke=0;
                strokeTo=circleStroke;
                sides=4;
                radius=6;
            }},new HaloPart(){{
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
            }}
            });
            }};
            recoil=3;
            coolant=consumeCoolant(0.3F);
        }};


        ModUnits.unitType1=new UnitType("unit-type-1"){{
            canBoost=true;
            constructor=TankUnit::create;
            weapons.add(new Weapon("tutorial-weapon"){{
                bullet = new BasicBulletType(2.5f, 9);
                reload=5;
            }});
            abilities.add(new ShieldArcAbility());
            abilities.add(new RepairFieldAbility(1,120,80));
            speed=1.5f;
            health=100;
        }};
        ModUnits.unitType2=new UnitType("unit-type-2"){{
            rotateSpeed=4;
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
                top=true;
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
                recoil=1;
                top=true;
                mirror=false;
                reload=90;
                shake=5;
                rotate=true;
            }});
            engines.add(new UnitEngine(0,-12.5f,4,0));
            canBoost=true;
            buildSpeed=1.5f;
            mineSpeed=6;
            speed=0.75f;
            health=12000;
            abilities.add(new RepairFieldAbility(10,180,96));
            abilities.add(new ShieldRegenFieldAbility(20,60,300,80));
        }};
        ModBlocks.sentinelCore=new CoreBlock("sentinel-core"){{
            alwaysUnlocked=true;
            health=1400;
            size=4;
            unitType=ModUnits.unitType2;
            requirements(Category.effect,with(Items.copper,1000,Items.lead,200,Items.titanium,200));
        }};


        ModPlanets.planetEee=new Planet("planet-eee", Planets.sun, 1f, 3){{
            generator=new SerpuloPlanetGenerator();
            new NoiseMesh(Planets.serpulo,1,1,Color.sky,
                    1,1,1f,1f,1f);
            meshLoader = () -> new HexMesh(Planets.serpulo, 6);
            cloudMeshLoader = () -> new MultiMesh(new GenericMesh[]{new HexSkyMesh(this, 11, 0.15F, 0.13F, 5, (new Color()).set(Pal.spore).mul(0.9F).a(0.75F), 2, 0.45F, 0.9F, 0.38F), new HexSkyMesh(this, 1, 0.6F, 0.16F, 5, Color.white.cpy().lerp(Pal.spore, 0.55F).a(0.75F), 2, 0.45F, 1.0F, 0.41F)});
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


        nodeRoot("e",Blocks.coreShard,()->{
            node(ModTurrets.itemTurret3,()->{
                node(ModTurrets.puncture,()->{
                    node(ModTurrets.pureEmptiness);
                    node(ModTurrets.daytime);
                });
                node(ModTurrets.itemTurret2);
                node(ModTurrets.powerTurret7,()->{
                    node(ModTurrets.powerTurret6);
                    node(ModTurrets.powerTurret4,()->{
                        node(ModTurrets.itemTurret5,()->{
                            node(ModTurrets.frost);
                            node(ModTurrets.disaster);
                            node(ModTurrets.ash);
                        });
                    });
                });
            });
            node(Blocks.mechanicalDrill,()->{
                node(ModBlocks.electricHeater,()->{
                    node(ModBlocks.heatTransmitter);
                    node(ModBlocks.fissionReactor);
                });
                node(ModBlocks.canyonBatteryCompressor,()->{
                    node(ModBlocks.archipelagoBatteryCompressor);
                });
                node(ModBlocks.canyonBatteryGenerator,()->{
                    node(ModBlocks.archipelagoBatteryGenerator);
                });
                node(ModBlocks.largeThoriumReactor);
                node(ModBlocks.smallDrillBit);
                node(ModBlocks.siliconSteelMixer, () -> {//硅钢混合机
                    node(ModBlocks.photoLithographyMachine);
                    node(ModBlocks.electrolyticSeparator, () -> {});//电解分离机
                    node(Blocks.plastaniumCompressor, () -> {
                        node(ModBlocks.largeSiliconSteelMixer);
                        node(ModBlocks.petroleumFractionatingTower);
                    });
                    node(ModBlocks.rockDrilling, () -> {
                        node(ModBlocks.highTemperatureSmeltingPlant);
                        node(ModBlocks.highTemperatureMeltingFurnace, () -> {
                            node(ModBlocks.highSpeedDisassembler);
                        });
                    });
                });
                node(ModBlocks.laserEnergyNode,()->{
                    node(ModBlocks.fluidThermalEnergyGenerator,()->{
                        node(ModBlocks.dieselGenerator);
                    });
                });
            });
            node(Blocks.titaniumConveyor,()->{
                node(ModBlocks.fastItemBridge,()->{
                    node(ModBlocks.itemTrack);
                    node(ModBlocks.logisticsPipeline);
                });
            });
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
                nodeProduce(ModItems.tin, () -> {});
                nodeProduce(ModItems.zinc, () -> {});
                nodeProduce(Items.coal, () -> {
                    nodeProduce(Items.sand, () -> {
                        nodeProduce(Items.scrap, () -> {
                        });
                        nodeProduce(ModItems.rock, () -> {
                        });
                    });
                    nodeProduce(Liquids.oil, () -> {
                        nodeProduce(ModItems.gasoline, () -> {});
                        nodeProduce(ModItems.diesel, () -> {});
                        nodeProduce(ModItems.kerosene, () -> {});
                    });
                    nodeProduce(Items.graphite, () -> {
                    });
                    nodeProduce(Items.silicon, () -> {
                        nodeProduce(ModItems.processor,()->{});
                        nodeProduce(ModItems.siliconSteel, () -> {});
                    });
                });
                nodeProduce(ModItems.gold, () -> {});
                nodeProduce(Liquids.water, () -> {
                    nodeProduce(ModItems.lava, () -> {});
                    nodeProduce(Liquids.hydrogen, () -> {});
                });
            });
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
//                node(ModTurrets.itemTurret3, () -> {
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
//