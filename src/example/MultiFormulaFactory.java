package example;

import arc.Core;
import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.TextureRegion;
import arc.scene.style.TextureRegionDrawable;
import arc.scene.ui.layout.Table;
import arc.struct.Seq;
import arc.util.*;
import arc.util.io.Reads;
import arc.util.io.Writes;
import mindustry.Vars;
import mindustry.ctype.UnlockableContent;
import mindustry.entities.units.BuildPlan;
import mindustry.gen.*;
import mindustry.graphics.Pal;
import mindustry.type.Item;
import mindustry.type.ItemStack;
import mindustry.type.LiquidStack;
import mindustry.ui.Styles;
import mindustry.world.blocks.ItemSelection;
import mindustry.world.blocks.payloads.Payload;
import mindustry.world.blocks.production.GenericCrafter;
import mindustry.world.consumers.ConsumeItems;
import mindustry.world.draw.DrawBlock;
import mindustry.world.draw.DrawDefault;
import mindustry.world.meta.Stat;
import mindustry.world.meta.StatValues;

public class MultiFormulaFactory extends GenericCrafter {
    public DrawBlock drawer = new DrawDefault();
    public int[] capacities = new int[0];
    public Seq<MultiFormulaFactory.ItemPlan> plans = new Seq(4);
    public float craftTime;
    @Nullable
    public ItemStack outputItem;
    @Nullable
    public ItemStack[] outputItems;
    @Nullable
    protected ConsumeItems consItems;
    @Nullable
    public LiquidStack[] outputLiquids;
    public MultiFormulaFactory(String name) {
        super(name);
        this.update = true;
        this.hasPower = true;
        this.hasItems = true;
        this.solid = true;
        this.configurable = true;
        this.clearOnDoubleTap = true;
        this.outputsPayload = true;
        this.rotate = true;
        this.regionRotated1 = 1;
        this.commandable = true;
        this.config(Item.class, (build, val) -> {
            if (this.configurable) {
                int next = this.plans.indexOf((p) -> ItemPlan.item.item == val);
            }
        });
    }

    public void init() {
        super.init();
        this.consItems = (ConsumeItems)this.findConsumer((c) -> c instanceof ConsumeItems);
    }

    public void afterPatch() {
        this.initCapacities();
        super.afterPatch();
    }

    public void initCapacities() {
        this.capacities = new int[Vars.content.items().size];
        this.itemCapacity = 10;

        for(MultiFormulaFactory.ItemPlan plan : this.plans) {
            for(ItemStack stack : plan.requirements) {
                this.capacities[stack.item.id] = Math.max(this.capacities[stack.item.id], stack.amount * 2);
                this.itemCapacity = Math.max(this.itemCapacity, stack.amount * 2);
            }
        }

        this.consumeBuilder.each((c) -> c.multiplier = (b) -> Vars.state.rules.unitCost(b.team));
    }

    public void setBars() {
        super.setBars();
    }

    public boolean outputsItems() {
        return true;
    }

    public void setStats() {
        this.stats.timePeriod = this.craftTime;
        super.setStats();
        this.stats.remove(Stat.itemCapacity);
        this.stats.add(Stat.output, (table) -> {
            table.row();

            for(MultiFormulaFactory.ItemPlan plan : this.plans) {
                table.table(Styles.grayPanel, (t) -> {
                    if (plan.item.item.isBanned()) {
                        t.image(Icon.cancel).color(Pal.remove).size(40.0F);
                    } else {
                        if (plan.item.item.unlockedNow()) {
                            t.image(plan.item.item.uiIcon).size(40.0F).pad(10.0F).left().scaling(Scaling.fit).with((i) -> StatValues.withTooltip(i, plan.item.item));
                            t.table((info) -> {
                                info.add(plan.item.item.localizedName).left();
                                info.row();
                                info.add(Strings.autoFixed(plan.time / 60.0F, 1) + " " + Core.bundle.get("unit.seconds")).color(Color.lightGray);
                            }).left();
                            t.table((req) -> {
                                req.right();

                                for(int i = 0; i < plan.requirements.length; ++i) {
                                    if (i % 6 == 0) {
                                        req.row();
                                    }

                                    ItemStack stack = plan.requirements[i];
                                    req.add(StatValues.displayItem(stack.item, stack.amount, plan.time, true)).pad(5.0F);
                                }

                            }).right().grow().pad(10.0F);
                        } else {
                            t.image(Icon.lock).color(Pal.darkerGray).size(40.0F);
                        }

                    }
                }).growX().pad(5.0F);
                table.row();
            }

        });
    }

    public TextureRegion[] icons() {
        return new TextureRegion[]{region};
    }

    public void drawPlanRegion(BuildPlan plan, Eachable<BuildPlan> list) {
        Draw.rect(this.region, plan.drawx(), plan.drawy());
    }

    public void getPlanConfigs(Seq<UnlockableContent> options) {
        for(MultiFormulaFactory.ItemPlan plan : this.plans) {
            if (!plan.item.item.isBanned()) {
                options.add(plan.item.item);
            }
        }

    }

    public static class ItemPlan {
        public static ItemStack item;
        public ItemStack[] requirements;
        public float time;

        public ItemPlan(ItemStack unit, float time, ItemStack[] requirements) {
            this.item = unit;
            this.time = time;
            this.requirements = requirements;
        }
    }

    public class MultiFormulaFactoryBuild extends Building {
        public float progress;
        public float counter;
        public Item outputItem;
        public int currentPlan = -1;
        public MultiFormulaFactoryBuild() {}
        public void drawSelect() {
            super.drawSelect();
            this.drawItemSelection(this.outputItem);
        }

        public void buildConfiguration(Table table) {
            ItemSelection.buildTable(MultiFormulaFactory.this, table, Vars.content.items(), () -> this.outputItem, this::configure, MultiFormulaFactory.this.selectionRows, MultiFormulaFactory.this.selectionColumns);
        }

        public boolean acceptPayload(Building source, Payload payload) {
            return false;
        }

        public void display(Table table) {
            super.display(table);
            TextureRegionDrawable reg = new TextureRegionDrawable();
            table.row();
            table.table((t) -> {
                t.left();
                t.image().update((i) -> {
                    i.setDrawable(this.currentPlan == -1 ? Icon.cancel : reg.set(((ItemPlan)MultiFormulaFactory.this.plans.get(this.currentPlan)).item.item.uiIcon));
                    i.setScaling(Scaling.fit);
                    i.setColor(this.currentPlan == -1 ? Color.lightGray : Color.white);
                }).size(32.0F).padBottom(-4.0F).padRight(2.0F);
                t.label(() -> this.currentPlan == -1 ? "@none" : ((ItemPlan)MultiFormulaFactory.this.plans.get(this.currentPlan)).item.item.localizedName).wrap().width(230.0F).color(Color.lightGray);
            }).left();
        }

        public Object config() {
            return this.currentPlan;
        }

        public void draw() {
            MultiFormulaFactory.this.drawer.draw(this);
        }

        public void updateTile() {
            if (this.efficiency > 0.0F) {
                this.progress += this.getProgressIncrease(MultiFormulaFactory.this.craftTime);
                if (MultiFormulaFactory.this.outputLiquids != null) {
                    float inc = this.getProgressIncrease(1.0F);

                    for(LiquidStack output : MultiFormulaFactory.this.outputLiquids) {
                        this.handleLiquid(this, output.liquid, Math.min(output.amount * inc, MultiFormulaFactory.this.liquidCapacity - this.liquids.get(output.liquid)));
                    }
                }
            }

            this.dumpOutputs();
        }

        public void dumpOutputs() {
            if (MultiFormulaFactory.this.outputItems != null && this.timer(MultiFormulaFactory.this.timerDump, (float)MultiFormulaFactory.this.dumpTime / this.timeScale)) {
                for(ItemStack output : MultiFormulaFactory.this.outputItems) {
                    this.dump(output.item);
                }
            }

        }

        public boolean shouldConsume() {
            return true;
        }

        public boolean acceptItem(Building source, Item item) {
            return this.currentPlan != -1 && this.items.get(item) < this.getMaximumAccepted(item) && Structs.contains(((MultiFormulaFactory.ItemPlan)MultiFormulaFactory.this.plans.get(this.currentPlan)).requirements, (stack) -> stack.item == item);
        }

        @Nullable
        public ItemStack item() {
            return this.currentPlan == -1 ? null : ((MultiFormulaFactory.ItemPlan)MultiFormulaFactory.this.plans.get(this.currentPlan)).item;
        }

        public byte version() {
            return 3;
        }

        public void write(Writes write) {
            super.write(write);
            write.f(this.progress);
            write.s(this.currentPlan);
        }

        public void read(Reads read, byte revision) {
            super.read(read, revision);
            this.progress = read.f();
            this.currentPlan = read.s();
        }
    }
}