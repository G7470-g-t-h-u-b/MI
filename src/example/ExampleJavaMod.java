package example;

import arc.*;
import arc.graphics.Color;
import arc.util.*;
import mindustry.*;
import mindustry.content.*;
import mindustry.game.EventType.*;
import mindustry.gen.*;
import mindustry.mod.*;
import mindustry.type.Category;
import mindustry.type.Item;
import mindustry.type.ItemStack;
import mindustry.ui.dialogs.*;
import mindustry.world.blocks.production.GenericCrafter;

import javax.naming.directory.ModificationItem;

import static arc.input.KeyCode.f;

public class ExampleJavaMod extends Mod{
    Item experimentalExplosives;

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
        experimentalExplosives=new Item("experimental-explosives", Color.HSVtoRGB(4,100,60)){{
            explosiveness=2.2f;
            flammability=1.6f;
        }};
        new GenericCrafter("laboratory"){{
            health=180;
            size=2;
            requirements(Category.crafting, ItemStack.with(Items.copper,50,Items.lead,20,Items.titanium,10));
            consumeItem(Items.blastCompound,2);
            consumeLiquid(Liquids.oil,2f);
            consumePower(1f);
            outputItem = new ItemStack(experimentalExplosives,2);
        }};
    }

}
