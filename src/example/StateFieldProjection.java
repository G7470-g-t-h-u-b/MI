package example;

import arc.util.Time;
import mindustry.content.Fx;
import mindustry.entities.Effect;
import mindustry.entities.Units;
import mindustry.gen.Building;
import mindustry.logic.Ranged;
import mindustry.type.StatusEffect;
import mindustry.world.Block;
import mindustry.world.consumers.Consume;


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
        
        public void updateTile(){
            timer_+= Time.delta;
            if (timer_>=reload){
                Units.nearby(this.team, this.x, this.y, range, (other) -> {
                    other.apply(statusEffect,duration);
                    applyEffect.at(other, parentizeEffects);
                });
            }
        }

        @Override
        public float range() {
            return range;
        }
    }
}
