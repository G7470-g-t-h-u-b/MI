package example;

import arc.*;
import arc.graphics.Color;
import arc.struct.Seq;
import arc.util.*;
import mindustry.content.*;
import mindustry.entities.Effect;
import mindustry.entities.UnitSorts;
import mindustry.entities.abilities.MoveEffectAbility;
import mindustry.entities.abilities.ShieldArcAbility;
import mindustry.entities.bullet.*;
import mindustry.entities.effect.MultiEffect;
import mindustry.entities.effect.WaveEffect;
import mindustry.entities.pattern.ShootPattern;
import mindustry.game.EventType.*;
import mindustry.gen.UnitEntity;
import mindustry.graphics.Pal;
import mindustry.graphics.g3d.HexMesh;
import mindustry.graphics.g3d.NoiseMesh;
import mindustry.mod.*;
import mindustry.type.*;
import mindustry.type.unit.MissileUnitType;
import mindustry.ui.dialogs.*;
import mindustry.world.blocks.defense.turrets.ItemTurret;
import mindustry.world.blocks.defense.turrets.LiquidTurret;
import mindustry.world.blocks.defense.turrets.PowerTurret;
import mindustry.world.blocks.environment.OreBlock;
import mindustry.world.blocks.power.BeamNode;
import mindustry.world.blocks.power.ConsumeGenerator;
import mindustry.world.blocks.production.Drill;
import mindustry.world.blocks.production.GenericCrafter;
import mindustry.world.blocks.production.Separator;
import mindustry.world.consumers.ConsumeLiquidFlammable;
import mindustry.world.draw.DrawTurret;

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
            temperature=1.3f;
            viscosity=0.75f;
        }};
        ModItems.iron=new Item("iron",Color.HSVtoRGB(233,16,25));
        ModItems.frostAlloy=new Item("frost-alloy",Color.HSVtoRGB(196,46,89));


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
        ModBlocks.electrolyticSeparator=new GenericCrafter("electrolytic-separator"){{
            health=180;
            size=2;
            requirements(Category.crafting,with(Items.titanium,30,Items.copper,20,ModItems.zinc,15));
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
            consumeItems(ItemStack.with(ModItems.rock,2));
            consumePower(2f);
            outputLiquid=new LiquidStack(ModItems.lava,0.2f);
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
        ModBlocks.highSpeedDisassembler=new Separator("high-speed-disassembler"){{
            health=200;
            size=3;
            craftTime=60f;
            requirements(Category.crafting,with(Items.copper,45,Items.titanium,25,Items.silicon,30));
            consumePower(3.25f);
            consumeItems(ItemStack.with(Items.scrap,2));
            consumeLiquids(LiquidStack.with(ModItems.lava,0.25f));
            results=ItemStack.with(new Object[]{Items.silicon,1,Items.thorium,1,ModItems.zinc,1,ModItems.tin,2});
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
            powerProduction=7f;
            size=2;
            consume(new ConsumeLiquidFlammable());
        }};


        ModBlocks.oreGold=new OreBlock("ore-gold",ModItems.gold);
        ModBlocks.oreZinc=new OreBlock("ore-zinc",ModItems.zinc);
        ModBlocks.oreTin=new OreBlock("ore-tin",ModItems.tin);
        ModBlocks.oreUranium=new OreBlock("ore-uranium",ModItems.uranium);


        ModTurrets.itemTurret1=new ItemTurret("item-turret-1"){{
            requirements(Category.turret, with(Items.copper, 40,ModItems.zinc,10,ModItems.gold,5));
            ammo(ModItems.experimentalExplosives, new MissileBulletType(1.5f,32){{
                ammoMultiplier=2;
                splashDamage=4.5f;
                splashDamageRadius=2.5f;
                makeFire=true;
                lifetime=160;
            }},Items.surgeAlloy,new BasicBulletType(0,0){{

            }});
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
            ammo(Items.lead,new FlakBulletType(1.7f,12){{
                hitColor = this.backColor = this.trailColor = Pal.blastAmmoBack;
                ammoMultiplier=3;
                lifetime=160;
//                range=192;
            }},ModItems.siliconSteel,new MissileBulletType(1.5f,11){{
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
        ModTurrets.frost=new LiquidTurret("frost"){{
            size=4;
            requirements(Category.turret,with(Items.titanium,160,ModItems.zinc,100,ModItems.gold,50,ModItems.siliconSteel,100,Items.surgeAlloy,80));
            range=640;
            unitSort=UnitSorts.strongest;
            consumesPower=true;
            consumePower(12F);
            maxAmmo=120;
//            shootSound=Sounds.malignShoot;
//            loopSound=Sounds.spellLoop;
//            loopSoundVolume=1.2f;
            drawer=new DrawTurret(){{parts.addAll();}};
            ammo(Liquids.hydrogen,new FlakBulletType(8.5f,75f){{
                buildingDamageMultiplier=0.5f;
                lifetime=180f;
                shootEffect=Fx.shootSmokeSquareBig;
                trailEffect=Fx.colorSpark;
                smokeEffect=Fx.shootSmokeDisperse;
                hitColor=trailColor=lightningColor=Color.sky;
                trailWidth=2.5f;
                trailLength=20;
                homingDelay=18f;
                homingRange=240f;
                homingPower=3;
                intervalBullet=new LightningBulletType(){{
                    buildingDamageMultiplier=0.5f;
                    lightningColor=Color.sky;
                    lightningLength=20;
                    damage=16f;
                }};
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
                hitEffect=Fx.hitSquaresColor;
                splashDamage=3f;
                homingPower=0.2f;
                homingDelay=16f;
                homingRange=160f;
                collidesGround=true;
                collideTerrain=false;
                collidesTiles=false;
            }});
        }};
        ModTurrets.powerTurret4 =new PowerTurret("power-turret-4"){{
            requirements(Category.turret,with(Items.copper,50,ModItems.siliconSteel,20,Items.titanium,15));
            consumePower(5f);
            size=2;
            reload=30;
            rotateSpeed=2.3f;
            range=176;
            drawer = new DrawTurret(){{
                parts.addAll();
            }};
            shootType= new LaserBulletType(45){{
                shootEffect = Fx.instShoot;
                smokeEffect = Fx.smokeCloud;
                hitEffect = Fx.hitLancer;
                colors= new Color[]{Color.HSVtoRGB(210,70,100)};
                hitSize=4f;
                range=176;
                maxRange=28;
            }};
        }};


        ModUnits.unitType1=new UnitType("unit-type-1"){{
            constructor=UnitEntity::create;
            weapons.add(new Weapon("tutorial-weapon"){{
                bullet = new BasicBulletType(2.5f, 9);
                reload=5;
            }});
            abilities.add(new ShieldArcAbility());
            speed=3f;
            health=85;
        }};


        nodeRoot("eee",Blocks.coreShard,()->{
            node(Blocks.mechanicalDrill,()->{
                node(Blocks.graphitePress,()->{
                    node(Blocks.pneumaticDrill,()->{
                        node(ModBlocks.smallDrillBit,()->{});
                        node(Blocks.laserDrill,()->{});
                    });
                    node(Blocks.siliconSmelter,()->{
                        node(Blocks.illuminator,()->{});
                        node(Blocks.kiln,()->{});
                        node(ModBlocks.siliconSteelMixer,()->{//硅钢混合机
                            node(ModBlocks.electrolyticSeparator,()->{});//电解分离机
                        });
                        node(Blocks.pulverizer,()->{
                            node(Blocks.melter,()->{
                                node(Blocks.separator,()->{});
                                node(ModBlocks.rockDrilling,()->{
                                    node(ModBlocks.highTemperatureSmeltingPlant);
                                    node(ModBlocks.highTemperatureMeltingFurnace,()->{
                                        node(ModBlocks.highSpeedDisassembler);
                                    });
                                });
                            });
                        });
                    });
                });
                node(Blocks.combustionGenerator,()->{
                    node(ModBlocks.laserEnergyNode,()->{//激光电力节点
                        node(Blocks.steamGenerator,()->{});//涡轮发电机
                    });
                });
            });
            node(Blocks.conveyor,()->{
                node(Blocks.titaniumConveyor,()->{});
            });
            nodeProduce(ModItems.experimentalExplosives,()->{});
            node(ModBlocks.laboratory, () ->{});
            node(Blocks.duo,()->{
                node(Blocks.copperWall,()->{
                    node(Blocks.copperWallLarge,()->{
                        node(Blocks.titaniumWall,()->{
                           node(Blocks.titaniumWallLarge,()->{});
                        });
                    });
                });
                node(Blocks.hail,()->{
                    node(Blocks.salvo,()->{});
                    node(Blocks.scorch,()->{});
                });
                node(Blocks.scatter,()->{});
                node(ModTurrets.itemTurret3,()->{
                    node(ModTurrets.itemTurret2,()->{});
                });
                node(Blocks.arc,()->{
                    node(Blocks.wave,()->{});
                    node(Blocks.lancer,()->{
                        node(Blocks.foreshadow);
                        node(ModTurrets.itemTurret5,()->{
                            node(ModTurrets.frost);
                            node(ModTurrets.ash);
                        });
                        node(Blocks.meltdown);
                    });
                    node(ModTurrets.powerTurret4,()->{});
                });
            });
            nodeProduce(Items.copper,()->{
                nodeProduce(Items.lead,()->{
                    nodeProduce(Items.titanium,()->{
                        nodeProduce(Liquids.cryofluid,()->{});
                        nodeProduce(Items.thorium,()->{});
                    });
                });
                nodeProduce(ModItems.tin,()->{});
                nodeProduce(ModItems.zinc,()->{});
                nodeProduce(Items.coal,()->{
                    nodeProduce(Items.sand,()->{
                        nodeProduce(Items.scrap,()->{});
                        nodeProduce(ModItems.rock,()->{});
                    });
                    nodeProduce(Items.graphite,()->{});
                    nodeProduce(Items.silicon,()->{
                        nodeProduce(ModItems.siliconSteel,()->{
                        });
                    });
                });
                nodeProduce(ModItems.gold,()->{});
                nodeProduce(Liquids.water,()->{
                    nodeProduce(ModItems.lava,()->{});
                    nodeProduce(Liquids.hydrogen,()->{});
                });
            });
        });


        ModPlanets.planetEee=new Planet("planet-eee", Planets.serpulo, 0.2f, 3){{
            new NoiseMesh(Planets.serpulo,1,1,Color.white,
                    1,1,1f,1f,1f);
            meshLoader = () -> new HexMesh(Planets.serpulo, 6);
            new SectorPreset("testSector", Planets.serpulo, 15);
            new SectorPreset("t1",Planets.serpulo,47);
        }};
    }
}
//