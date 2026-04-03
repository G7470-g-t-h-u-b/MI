package example;

import arc.audio.Sound;
import mindustry.Vars;

public class ModSounds {
    public static Sound shootSharpSpear=new Sound();
    public static Sound shoot1;

    public void load(){
        Vars.tree.loadSound("shootSharpSpear");
    }
}
