package example;

import arc.*;
import arc.graphics.Color;
import arc.util.*;
import mindustry.content.*;
import mindustry.entities.bullet.MissileBulletType;
import mindustry.entities.pattern.ShootPattern;
import mindustry.game.EventType.*;
import mindustry.gen.Bullet;
import mindustry.graphics.g3d.HexMesh;
import mindustry.graphics.g3d.NoiseMesh;
import mindustry.mod.*;
import mindustry.type.*;
import mindustry.ui.dialogs.*;
import mindustry.world.blocks.defense.turrets.ItemTurret;
import mindustry.world.blocks.environment.OreBlock;
import mindustry.world.blocks.power.BeamNode;
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
            flammability=1.6f;
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
            hardness=1;
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
        ModBlocks.LaserEnergyNode=new BeamNode("laser-energy-node"){{
            health=100;
            size=1;
            requirements(Category.power, with(Items.copper, 8,Items.lead,5,ModItems.zinc,5));
            range=15;
            consumesPower=outputsPower=true;
            consumePowerBuffered(1000f);
        }};


        ModBlocks.oreGold=new OreBlock("ore-gold",ModItems.gold);
        ModBlocks.oreZinc=new OreBlock("ore-zinc",ModItems.zinc);
        ModBlocks.oreTin=new OreBlock("ore-tin",ModItems.tin);


        nodeRoot("eee",Blocks.coreShard,()->{
            node(Blocks.mechanicalDrill,()->{
                node(Blocks.graphitePress,()->{
                    node(Blocks.siliconSmelter,()->{
                        node(Blocks.kiln,()->{});
                        node(ModBlocks.siliconSteelMixer,()->{});
                    });
                });
            });
            node(Blocks.conveyor,()->{});
            nodeProduce(ModItems.experimentalExplosives,()->{});
            node(ModBlocks.laboratory, () ->{});
            nodeProduce(Items.copper,()->{
                nodeProduce(Items.lead,()->{
                    nodeProduce(Items.titanium,()->{
                        nodeProduce(Items.thorium,()->{});
                    });
                });
                nodeProduce(ModItems.zinc,()->{});
                nodeProduce(Items.coal,()->{
                    nodeProduce(Items.sand,()->{});
                    nodeProduce(Items.graphite,()->{});
                    nodeProduce(Items.silicon,()->{
                        nodeProduce(ModItems.siliconSteel,()->{});
                    });
                });
                nodeProduce(ModItems.gold,()->{});
                nodeProduce(Liquids.water,()->{});
            });
        });


        new ItemTurret("item-turret-eee"){{
            requirements(Category.turret, with(Items.copper, 40,ModItems.zinc,10,ModItems.gold,5));
            ammo(ModItems.experimentalExplosives, new MissileBulletType(1.5f,32){{
                ammoMultiplier=2;
                splashDamage=4.5f;
                splashDamageRadius=2.5f;
                makeFire=true;
            }});

            displayAmmoMultiplier=true;
            range=20;
            shoot = new ShootPattern();
            drawer = new DrawTurret(){{
                parts.addAll();
            }};
            consumePower(2f);
            coolant = consumeCoolant(0.1f);
        }};


        ModPlanets.planetEee=new Planet("planet-eee", Planets.serpulo, 0.2f, 3){{
            new NoiseMesh(Planets.serpulo,1,1,Color.white,
                    1,1,1f,1f,1f);
            meshLoader = () -> new HexMesh(Planets.serpulo, 6);
            new SectorPreset("testSector", Planets.serpulo, 15);
//            new SectorPreset("t1",ModPlanets.planetEee,155);
        }};
    }
}
//