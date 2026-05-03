package example;

import mindustry.game.Team;
import mindustry.world.Tile;
import mindustry.world.blocks.storage.CoreBlock;

public class RemotAccessBox extends CoreBlock {
    public RemotAccessBox(String name) {
        super(name);
        unitCapModifier=0;
        allowSpawn=false;
        unitType=null;
    }
    public boolean canPlaceOn(Tile tile,Team team,int rotation){
        return true;
    }
    public boolean canBreak(Tile tile){
        return true;
    }
    public class RemotAccessBuild extends CoreBuild{
        public boolean canPickup(){
            return true;
        }
    }
}
