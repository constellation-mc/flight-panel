package me.melontini.flightpanel.impl;

import com.google.common.collect.Lists;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import me.melontini.dark_matter.api.base.util.MathUtil;
import me.melontini.flightpanel.api.builders.ConfigScreenBuilder;
import me.melontini.flightpanel.api.builders.elements.BaseElementBuilder;
import me.melontini.flightpanel.api.generators.GuiRegistry;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.option.OptionsScreen;
import net.minecraft.text.Text;

import java.util.List;

import static me.melontini.flightpanel.api.generators.Transformations.*;

public class TestInit implements ClientModInitializer {

    private static final RealConfig CONFIG = new RealConfig();

    @Override
    public void onInitializeClient() {
        ScreenEvents.BEFORE_INIT.register((client, screen, scaledWidth, scaledHeight) -> {
            if (screen.getClass() == OptionsScreen.class && Screen.hasShiftDown() && Screen.hasAltDown()) {
                var sb = ConfigScreenBuilder.create()
                        .title(Text.literal("NEVER GONNA GIVE YOU UP"))
                        .saveFunction(() -> System.out.println(CONFIG));

                List<List<BaseElementBuilder<?, ?, ?>>> partition = Lists.partition(GuiRegistry.withDefaults().generateForObject("test.flight-panel.config.", CONFIG, RealConfig::new), 1);
                for (int i = 0; i < partition.size(); i++) {
                    List<BaseElementBuilder<?, ?, ?>> builders = partition.get(i);
                    sb.category(Text.literal("default " + MathUtil.nextInt(1, 200))).addAll(builders);
                }

                System.out.println(CONFIG);
                client.setScreen(sb.build());
            }
        });
    }

    @ToString
    @EqualsAndHashCode
    public static class RealConfig {
        public int anInt = 12;
        public @Range(from = 0.125478, to = 0.23456) float aFloat = 0.21f;
        public @Slider @Range(from = -0.1, to = 0.4) double aDouble = 0.3;
        public List<List<@Slider @Range(from = -3, to = 14) Integer>> nestedIntegers = List.of(List.of(3, 14), List.of(1));
        public List<Integer> integers = List.of(12, 34, 56, 78);
        @RequiresRestart
        public boolean aBoolean = true;
        public String aString = "Hello World!";
        public @Slider @Range(to = 1) int tinySlider = 0;
        public @Slider SliderEnum sliderEnum = SliderEnum.TWO;
        public SliderEnum buttonEnum = SliderEnum.TWO;

        public boolean aBoolean1 = true;
        public String aString1 = "Amogus";
        public boolean aBoolean2 = false;
        public String aString2 = "stop";

        public List<@Collapsible InnerObj> innerObjs = Lists.newArrayList();
    }

    public enum SliderEnum {
        ONE, TWO, THREE
    }

    @EqualsAndHashCode
    @ToString
    public static class InnerObj {
        public @Collapsible MoreInner value = new MoreInner();
    }

    @EqualsAndHashCode
    @ToString
    public static class MoreInner {
        public @Collapsible Innerer value = new Innerer();
    }

    @EqualsAndHashCode
    @ToString
    public static class Innerer {
        public int value = 9;
        public @Slider @Range(from = 0.1, to = 0.2) float slydee = 0.1f;
    }
}
