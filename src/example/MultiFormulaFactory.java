package example;

import arc.Core;
import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.TextureRegion;
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
import mindustry.world.blocks.production.GenericCrafter;
import mindustry.world.blocks.units.UnitFactory;
import mindustry.world.consumers.ConsumeItems;
import mindustry.world.draw.DrawBlock;
import mindustry.world.draw.DrawDefault;
import mindustry.world.meta.Stat;
import mindustry.world.meta.StatValues;

import static mindustry.Vars.state;

public class MultiFormulaFactory extends GenericCrafter {
    public DrawBlock drawer = new DrawDefault();
    public int[] capacities = new int[0];
    public Seq<ItemPlan> plans = new Seq<>(10);
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
        itemCapacity=20;
        update = true;
        hasPower = true;
        hasItems = true;
        solid = true;
        configurable = true;
        clearOnDoubleTap = true;
        outputsPayload = true;
        rotate = true;
        regionRotated1 = 1;
        commandable = true;
        config(Item.class, (UnitFactory.UnitFactoryBuild build, Item val) -> {
            if(!configurable) return;

            int next = plans.indexOf(p -> p.item.item == val);
            if(build.currentPlan == next) return;
            build.currentPlan = next;
            build.progress = 0;
        });
    }

    public void init() {
        initCapacities();
        consItems = findConsumer((c) -> c instanceof ConsumeItems);
        super.init();
    }

    public void afterPatch() {
        initCapacities();
        super.afterPatch();
    }

    public void initCapacities(){
        capacities=new int[Vars.content.items().size];
        itemCapacity=10;
        for(MultiFormulaFactory.ItemPlan plan : plans){
            for(ItemStack stack:plan.requirements){
                capacities[stack.item.id] = Math.max(capacities[stack.item.id], stack.amount * 2);
                itemCapacity=Math.max(itemCapacity, stack.amount * 2);
            }
        }

        consumeBuilder.each(c -> c.multiplier = b -> state.rules.unitCost(b.team));
    }


    public boolean outputsItems() {
        return true;
    }

    public void setStats() {
        stats.timePeriod = this.craftTime;
        super.setStats();
        stats.remove(Stat.itemCapacity);
        stats.add(Stat.output, (table) -> {
            table.row();

            for(ItemPlan plan : this.plans) {
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
        for(ItemPlan plan : this.plans) {
            if (!plan.item.item.isBanned()) {
                options.add(plan.item.item);
            }
        }

    }

    public static class ItemPlan {
        public ItemStack item;
        public ItemStack[] requirements;
        public float time;

        public ItemPlan(ItemStack item_, float time, ItemStack[] requirements) {
            this.item = item_;
            this.time = time;
            this.requirements = requirements;
        }
    }

    public class MultiFormulaFactoryBuild extends Building {
        public int configure;
        public float progress;
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

//        public void display(Table table) {
//            super.display(table);
//            TextureRegionDrawable reg = new TextureRegionDrawable();
//            table.row();
//            table.table((t) -> {
//                t.left();
//                t.image().update((i) -> {
//                    i.setDrawable(this.currentPlan == -1 ? Icon.cancel : reg.set(((ItemPlan)MultiFormulaFactory.this.plans.get(this.currentPlan)).item.item.uiIcon));
//                    i.setScaling(Scaling.fit);
//                    i.setColor(this.currentPlan == -1 ? Color.lightGray : Color.white);
//                }).size(32.0F).padBottom(-4.0F).padRight(2.0F);
//                t.label(() -> this.currentPlan == -1 ? "@none" : ((ItemPlan)MultiFormulaFactory.this.plans.get(this.currentPlan)).item.item.localizedName).wrap().width(230.0F).color(Color.lightGray);
//            }).left();
//        }

        public Object config() {
            return currentPlan;
        }

        public void draw() {
            super.draw();
        }

        public void updateTile() {
            if (!configurable) {
                currentPlan = 0;
            }
            if (currentPlan < 0 || currentPlan >= plans.size) {
                currentPlan = -1;
            }

            ItemPlan plan=plans.get(currentPlan);
            if (efficiency>0){
                progress += getProgressIncrease(craftTime);
            }
            if(progress>1) {
                if (plan.item!=null){
                    for(var output : plans.items){
                        for(int i = 0; i < plan.requirements[currentPlan].amount; i++){
                            offload(output.item.item);
                        }
                    }
                }

                if (wasVisible){
                    craftEffect.at(x,y);
                }

                consume();
            }
            dumpOutputs();
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
            return this.currentPlan != -1 && this.items.get(item) < this.getMaximumAccepted(item) && Structs.contains(MultiFormulaFactory.this.plans.get(this.currentPlan).requirements, (stack) -> stack.item == item);
        }

        @Nullable
        public ItemStack item() {
            return this.currentPlan == -1 ? null : MultiFormulaFactory.this.plans.get(this.currentPlan).item;
        }

        @Override
        public byte version() {
            return 3;
        }

        @Override
        public void write(Writes write) {
            super.write(write);
            write.f(progress);
            write.s(currentPlan);
        }

        @Override
        public void read(Reads read, byte revision) {
            super.read(read, revision);
            progress = read.f();
            currentPlan = read.s();
        }
    }
}