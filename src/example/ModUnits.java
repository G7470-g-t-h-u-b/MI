package example;

import mindustry.ai.types.CargoAI;
import mindustry.content.StatusEffects;
import mindustry.entities.bullet.BasicBulletType;
import mindustry.entities.bullet.LaserBoltBulletType;
import mindustry.gen.UnitEntity;
import mindustry.graphics.Pal;
import mindustry.type.UnitType;
import mindustry.type.Weapon;
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
    public static UnitType gax_37;
    public static void load0(){
        gax_37=new UnitType("gax-37"){{
            health=450;
            armor=30;
            speed=1.12f;
            constructor=UnitEntity::create;
            hitSize=12;
            itemCapacity=20;
            flying=true;
            weapons.add(new Weapon(){{
                mirror=true;
                reload=5;
                bullet=new LaserBoltBulletType(4.2f,20){{
                    lightColor= Pal.meltdownHit;
                    status=StatusEffects.melting;
//                    killShooter=true;
//                    fragBullets=5;
//                    fragRandomSpread=360;
//                    fragBullet=new BasicBulletType(10f,0){{
//                        lifetime=10;
//                        spawnUnit=UnitTypes.vela;
//                    }};
                }};
            }});
        }};
    }
    public static void loadDrone(){
        drone=new ErekirUnitType("drone"){{
            envDisabled=0;
            isEnemy=false;
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
