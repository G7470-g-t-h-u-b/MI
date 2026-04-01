package example;

import mindustry.game.Team;
import mindustry.world.Block;
import mindustry.world.Tile;
import mindustry.world.blocks.storage.CoreBlock;

public class OutPostCoreBlock extends CoreBlock {
    public OutPostCoreBlock(String name) {
        super(name);
    }
    public boolean canReplace(Block other){
        return other.alwaysReplace;
    }
    public boolean canPlaceOn(Tile tile, Team team, int rotation) {
        return true;
    }
}
