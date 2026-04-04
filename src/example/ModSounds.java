package example;

import arc.audio.Sound;
import arc.files.Fi;
import mindustry.Vars;

public class ModSounds {
    public static Sound shootSharpSpear=new Sound(new Fi("shootSharpSpear"));
    public static Sound shoot1;

    public void load(){
        Vars.tree.loadSound("shootSharpSpear");
    }
}
