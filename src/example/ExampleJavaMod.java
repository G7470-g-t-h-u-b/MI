package example;

import arc.*;
import arc.graphics.Color;
import arc.struct.Seq;
import arc.util.*;
import mindustry.content.*;
import mindustry.entities.UnitSorts;
import mindustry.entities.bullet.*;
import mindustry.entities.pattern.ShootPattern;
import mindustry.game.EventType.*;
import mindustry.graphics.g3d.HexMesh;
import mindustry.graphics.g3d.NoiseMesh;
import mindustry.mod.*;
import mindustry.type.*;
import mindustry.ui.dialogs.*;
import mindustry.world.blocks.defense.turrets.ItemTurret;
import mindustry.world.blocks.defense.turrets.PowerTurret;
import mindustry.world.blocks.environment.OreBlock;
import mindustry.world.blocks.power.BeamNode;
import mindustry.world.blocks.production.Drill;
import mindustry.world.blocks.production.GenericCrafter;
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
                dialog.cont.image(Core.atlas.find("example-java-mod-frog")).pad(20f).row();
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


        ModBlocks.laboratory=new GenericCrafter("laboratory"){{
            health=180;
            size=2;
            requirements(Category.crafting, with(Items.copper,30,Items.lead,20,Items.titanium,10));
            consumeItem(Items.blastCompound,2);
            consumeLiquid(Liquids.oil,0.1f);
            consumePower(1f);
            outputItem = new ItemStack(ModItems.experimentalExplosives,2);
        }};
        ModBlocks.siliconSteelMixer=new GenericCrafter("silicon-steel-mixer"){{
            health=180;
            size=2;
            requirements(Category.crafting,with(Items.copper,30,Items.lead,20,ModItems.tin,10,ModItems.zinc,10));
            consumeItems(ItemStack.with(Items.silicon,2,ModItems.zinc,1));
            consumePower(1.5f);
            outputItem=new ItemStack(ModItems.siliconSteel,2);
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
        }};


        ModBlocks.oreGold=new OreBlock("ore-gold",ModItems.gold);
        ModBlocks.oreZinc=new OreBlock("ore-zinc",ModItems.zinc);
        ModBlocks.oreTin=new OreBlock("ore-tin",ModItems.tin);


        ModTurrets.itemTurret1=new ItemTurret("item-turret-1"){{
            requirements(Category.turret, with(Items.copper, 40,ModItems.zinc,10,ModItems.gold,5));
            ammo(ModItems.experimentalExplosives, new MissileBulletType(1.5f,32){{
                ammoMultiplier=2;
                splashDamage=4.5f;
                splashDamageRadius=2.5f;
                makeFire=true;
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
                ammoMultiplier=3;
                splashDamage=1.2f;
                splashDamageRadius=1f;
            }},ModItems.siliconSteel,new MissileBulletType(1.7f,28){{
                ammoMultiplier=4;
                splashDamage=1.2f;
                splashDamageRadius=1.2f;
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
            maxAmmo=16;
            drawer = new DrawTurret(){{
                parts.addAll();
            }};
            size=2;
            reload=2;
            requirements(Category.turret,with(Items.copper,45,Items.lead,15,ModItems.tin,8));
            ammo(Items.lead,new FlakBulletType(1.7f,12){{
                ammoMultiplier=2;
            }},ModItems.siliconSteel,new MissileBulletType(1.5f,11));

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
                hitEffect = Fx.hitLancer;
                shootEffect = Fx.instShoot;
                smokeEffect = Fx.smokeCloud;
                pierceEffect = Fx.railHit;
                maxRange=560;
                range=520;
                ammoMultiplier=3;
                damage=720;
                collidesTiles=false;
                collideTerrain=false;
            }},Items.titanium,new RailBulletType(){{
                maxRange=560;
                range=520;

                hitEffect = Fx.hitLancer;
                shootEffect = Fx.instShoot;
                smokeEffect = Fx.smokeCloud;
                pierceEffect = Fx.railHit;
                damage=560;
                ammoMultiplier=2;
                collidesTiles=false;
                collideTerrain=false;
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


        nodeRoot("eee",Blocks.coreShard,()->{
            node(Blocks.mechanicalDrill,()->{
                node(Blocks.graphitePress,()->{
                    node(Blocks.pneumaticDrill,()->{
                        node(Blocks.laserDrill,()->{});
                    });
                    node(Blocks.siliconSmelter,()->{
                        node(Blocks.illuminator,()->{});
                        node(Blocks.kiln,()->{});
                        node(ModBlocks.siliconSteelMixer,()->{});
                    });
                });
                node(Blocks.combustionGenerator,()->{
                    node(ModBlocks.laserEnergyNode,()->{
                        node(Blocks.steamGenerator,()->{});
                    });
                });
            });
            node(Blocks.conveyor,()->{});
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
                        node(ModTurrets.itemTurret5);
                        node(Blocks.meltdown);
                    });
                    node(ModTurrets.powerTurret4,()->{});
                });
            });
            nodeProduce(Items.copper,()->{
                nodeProduce(Items.lead,()->{
                    nodeProduce(Items.titanium,()->{
                        nodeProduce(Items.thorium,()->{});
                    });
                });
                nodeProduce(ModItems.tin,()->{});
                nodeProduce(ModItems.zinc,()->{});
                nodeProduce(Items.coal,()->{
                    nodeProduce(Items.sand,()->{});
                    nodeProduce(Items.graphite,()->{});
                    nodeProduce(Items.silicon,()->{
                        nodeProduce(ModItems.siliconSteel,()->{
                        });
                    });
                });
                nodeProduce(ModItems.gold,()->{});
                nodeProduce(Liquids.water,()->{});
            });
        });


        ModPlanets.planetEee=new Planet("planet-eee", Planets.serpulo, 0.2f, 3){{
            new NoiseMesh(Planets.serpulo,1,1,Color.white,
                    1,1,1f,1f,1f);
            meshLoader = () -> new HexMesh(Planets.serpulo, 6);
            new SectorPreset("testSector", Planets.serpulo, 15);
            new SectorPreset("t1",ModPlanets.planetEee,47);
        }};
    }
}
//