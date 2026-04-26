package example;

import arc.graphics.Color;
import mindustry.content.StatusEffects;
import mindustry.type.StatusEffect;

public class ModStatusEffects {
    public static StatusEffect erosionX;
    public static StatusEffect interference;
    public static void load(){
        interference=new StatusEffect("interference"){{
            damage=5;
            reloadMultiplier=0.7f;
            damageMultiplier=0.65f;
            speedMultiplier=0.7f;
            healthMultiplier=0.8f;
            color=Color.valueOf("ffa166");
            init(()->{
                opposite(StatusEffects.overclock,StatusEffects.fast);
                affinity(StatusEffects.overdrive,(unit,result,time)->{
                    unit.damage(30);
                });
                affinity(StatusEffects.electrified,(unit,result,time)->{
                    unit.damage(50);
                    result.set(StatusEffects.electrified,result.time+time);
                });
                affinity(StatusEffects.shocked,(unit,result,time)->{
                    unit.damage(30);
                });
            });
        }};
    }
}
