package example;

import arc.scene.ui.layout.Table;
import arc.util.Time;
import mindustry.content.Fx;
import mindustry.entities.Effect;
import mindustry.entities.Units;
import mindustry.gen.Building;
import mindustry.logic.Ranged;
import mindustry.type.StatusEffect;
import mindustry.world.Block;
import mindustry.world.consumers.Consume;
import mindustry.world.meta.Stat;
import mindustry.world.meta.StatUnit;


public class StateFieldProjection extends Block {
    public float reload=90f;
    public float range=60f;
    public float timer_;
    public StatusEffect statusEffect;
    public float duration=90f;
    public Effect applyEffect= Fx.none;
    public boolean parentizeEffects;
    
    public StateFieldProjection(String name) {
        super(name);
        update=true;
        hasPower=true;
    }
    public class StateFieldBuilding extends Building implements Ranged{
        public void consume() {
            for(Consume cons : this.block.consumers) {
                cons.trigger(this);
            }

        }
        public void addStats(){
            stats.add(Stat.range, range / 8.0f,StatUnit.blocks);
            stats.add(Stat.reload,reload/60,StatUnit.seconds);
        }
        public void updateTile(){
            timer_+=1;
            if (timer_>=reload){
                Units.nearby(this.team, this.x, this.y, range, (other) -> {
                    other.apply(statusEffect,duration);
                    applyEffect.at(other, parentizeEffects);
                });
                timer_=0;
            }
        }

        @Override
        public float range() {
            return range;
        }
    }
}
