package example;

import arc.math.Mathf;
import arc.util.Tmp;
import mindustry.Vars;
import mindustry.content.Fx;
import mindustry.entities.Effect;
import mindustry.entities.Units;
import mindustry.gen.Building;
import mindustry.graphics.Drawf;
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
    public void setStats(){
        super.setStats();
        stats.add(Stat.reload, (float)((int)(reload / 60.0F)), StatUnit.seconds);
        stats.add(Stat.range, this.range / 8.0F, StatUnit.blocks);
    }
    public void drawPlace(int x, int y, int rotation, boolean valid) {
        super.drawPlace(x, y, rotation, valid);
        Drawf.dashCircle((float)(x * 8) + this.offset, (float)(y * 8) + this.offset, this.range, TIColor.bronzeDark);
        Vars.indexer.eachBlock(Vars.player.team(), (float)(x * 8) + this.offset, (float)(y * 8) + this.offset, this.range, (other) -> true, (other) -> Drawf.selected(other, Tmp.c1.set(TIColor.bronzeDark).a(Mathf.absin(4.0F, 1.0F))));
    }
    public class StateFieldBuilding extends Building implements Ranged{
        public void consume() {
            for(Consume cons : this.block.consumers) {
                cons.trigger(this);
            }
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
