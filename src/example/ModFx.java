package example;

import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Lines;
import arc.math.Angles;
import arc.math.Mathf;
import mindustry.entities.Effect;
import mindustry.entities.effect.*;
import mindustry.graphics.Pal;

public class ModFx {
    public static ExplosionEffect explosionEffect1;
    public static Effect shapeEffect1;
    public static Effect shapeEffect2;
    public static Effect teachBlueBomb;
    public static Effect shootFireFx;
    public static final Effect bronzeShoot = new Effect(12.0F, (e) -> {
        Draw.color(Color.white, TIColor.bronzeColor, e.fin());
        Lines.stroke(e.fout() * 1.2F + 0.5F);
        Angles.randLenVectors(e.id, 7, 25.0F * e.finpow(), e.rotation, 50.0F, (x, y) -> Lines.lineAngle(e.x + x, e.y + y, Mathf.angle(x, y), e.fin() * 5.0F + 2.0F));
    });
    public static final Effect feShoot = new Effect(12.0F, (e) -> {
        Draw.color(Color.white, TIColor.feColor, e.fin());
        Lines.stroke(e.fout() * 1.2F + 0.5F);
        Angles.randLenVectors(e.id, 7, 25.0F * e.finpow(), e.rotation, 50.0F, (x, y) -> Lines.lineAngle(e.x + x, e.y + y, Mathf.angle(x, y), e.fin() * 5.0F + 2.0F));
    });
}
