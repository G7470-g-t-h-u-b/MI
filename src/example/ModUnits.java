package example;

import mindustry.ai.types.CargoAI;
import mindustry.gen.UnitEntity;
import mindustry.type.UnitType;
import mindustry.type.unit.ErekirUnitType;

public class ModUnits {
    public static UnitType unitType1;
    public static UnitType unitType2;
    public static UnitType unitType3;
    public static UnitType raid;
    public static UnitType mysticSnail;
    public static ErekirUnitType charge;
    public static UnitType anvil;
    public static UnitType drone;
    public static void loadDrone(){
        drone=new UnitType("drone"){{
            constructor= UnitEntity::create;
            controller=u->new CargoAI();
            flying=true;
            speed=3.8f;
            drag=0.06f;
            accel=1;
            rotateSpeed=12f;
            itemCapacity=120;
            hitSize=12;
            health=180;

        }};
    }
}
