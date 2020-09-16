package de.teamlapen.vampirism.core;

import com.google.common.collect.ImmutableList;
import de.teamlapen.vampirism.api.VampirismAPI;
import de.teamlapen.vampirism.config.BalanceMobProps;
import de.teamlapen.vampirism.config.VampirismConfig;
import de.teamlapen.vampirism.util.REFERENCE;
import net.minecraft.block.Blocks;
import net.minecraft.entity.EntityClassification;
import net.minecraft.entity.EntityType;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.biome.*;
import net.minecraft.world.gen.GenerationStage;
import net.minecraft.world.gen.blockplacer.SimpleBlockPlacer;
import net.minecraft.world.gen.blockstateprovider.SimpleBlockStateProvider;
import net.minecraft.world.gen.feature.*;
import net.minecraft.world.gen.foliageplacer.BlobFoliagePlacer;
import net.minecraft.world.gen.surfacebuilders.SurfaceBuilder;
import net.minecraft.world.gen.surfacebuilders.SurfaceBuilderConfig;
import net.minecraft.world.gen.trunkplacer.StraightTrunkPlacer;
import net.minecraftforge.event.world.BiomeLoadingEvent;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.ObjectHolder;

import static de.teamlapen.lib.lib.util.UtilLib.getNull;

/**
 * Handles all biome registrations and reference.
 */
public class ModBiomes {
    @ObjectHolder("vampirism:vampire_forest")
    public static final Biome vampire_forest = getNull();
    public static final RegistryKey<Biome> VAMPIRE_FOREST_KEY = RegistryKey.func_240903_a_(Registry.BIOME_KEY, new ResourceLocation(REFERENCE.MODID, "vampire_forest"));


    static void registerBiomes(IForgeRegistry<Biome> registry) {
        Biome.Builder forestBuilder = new Biome.Builder();
        MobSpawnInfo.Builder forestSpawnBuilder = new MobSpawnInfo.Builder();
        BiomeAmbience.Builder biomeAmbienceBuilder = new BiomeAmbience.Builder();
        BiomeGenerationSettings.Builder biomeGenBuilder = new BiomeGenerationSettings.Builder();
        forestSpawnBuilder.func_242572_a(0.25f);
        forestSpawnBuilder.func_242575_a(EntityClassification.MONSTER, new MobSpawnInfo.Spawners(ModEntities.vampire, BalanceMobProps.mobProps.VAMPIRE_SPAWN_CHANCE / 2, 1, 3));
        forestSpawnBuilder.func_242575_a(EntityClassification.MONSTER, new MobSpawnInfo.Spawners(ModEntities.vampire_baron, BalanceMobProps.mobProps.VAMPIRE_BARON_SPAWN_CHANCE, 1, 1));
        forestSpawnBuilder.func_242575_a(EntityClassification.AMBIENT, new MobSpawnInfo.Spawners(ModEntities.blinding_bat, BalanceMobProps.mobProps.BLINDING_BAT_SPAWN_CHANCE, 2, 4));
        forestSpawnBuilder.func_242575_a(EntityClassification.CREATURE, new MobSpawnInfo.Spawners(ModEntities.dummy_creature, BalanceMobProps.mobProps.DUMMY_CREATURE_SPAWN_CHANCE, 3, 6));
        forestBuilder.func_242458_a(forestSpawnBuilder.func_242577_b());
        forestBuilder.precipitation(Biome.RainType.NONE).category(Biome.Category.FOREST).depth(0.1F).scale(0.025f).temperature(0.3f).downfall(0);
        biomeAmbienceBuilder.setWaterColor(0xEE2505).setWaterFogColor(0xEE2505).setMoodSound(MoodSoundAmbience.field_235027_b_);
        forestBuilder.func_235097_a_(biomeAmbienceBuilder.build());


        biomeGenBuilder.func_242517_a(SurfaceBuilder.DEFAULT.func_242929_a(new SurfaceBuilderConfig(ModBlocks.cursed_earth.getDefaultState(), ModBlocks.cursed_earth.getDefaultState(), ModBlocks.cursed_earth.getDefaultState())));

        BlockClusterFeatureConfig flowerConfig = new BlockClusterFeatureConfig.Builder(new SimpleBlockStateProvider(ModBlocks.vampire_orchid.getDefaultState()), new SimpleBlockPlacer()).tries(64).build();
        BaseTreeFeatureConfig treeConfigSmall = new BaseTreeFeatureConfig.Builder(new SimpleBlockStateProvider(Blocks.SPRUCE_LOG.getDefaultState()), new SimpleBlockStateProvider(Blocks.OAK_LEAVES.getDefaultState()), new BlobFoliagePlacer(FeatureSpread.func_242252_a(2), FeatureSpread.func_242252_a(0), 3), new StraightTrunkPlacer(4, 2, 0), new TwoLayerFeature(1, 0, 1)).setIgnoreVines().build();
        BaseTreeFeatureConfig treeConfigBig = new BaseTreeFeatureConfig.Builder(new SimpleBlockStateProvider(Blocks.SPRUCE_LOG.getDefaultState()), new SimpleBlockStateProvider(Blocks.OAK_LEAVES.getDefaultState()), new BlobFoliagePlacer(FeatureSpread.func_242252_a(3), FeatureSpread.func_242252_a(0), 5), new StraightTrunkPlacer(6, 2, 0), new TwoLayerFeature(1, 0, 1)).setIgnoreVines().build();

        biomeGenBuilder.func_242513_a(GenerationStage.Decoration.VEGETAL_DECORATION, Feature.FLOWER.withConfiguration(flowerConfig).withPlacement(Features.Placements.field_244000_k));
        biomeGenBuilder.func_242513_a(GenerationStage.Decoration.VEGETAL_DECORATION, Feature.RANDOM_SELECTOR.withConfiguration(new MultipleRandomFeatureConfig(ImmutableList.of(Feature.field_236291_c_/*NORMAL_TREE*/.withConfiguration(treeConfigSmall).withChance(0.2f), Feature.field_236291_c_/*NORMAL_TREE*/.withConfiguration(treeConfigBig).withChance(0.1f)), Feature.field_236291_c_/*NORMAL_TREE*/.withConfiguration(treeConfigSmall))));


        forestBuilder.func_242457_a(biomeGenBuilder.func_242508_a());
        registry.register(forestBuilder.func_242455_a().setRegistryName(VAMPIRE_FOREST_KEY.getRegistryName()));
    }

    static void addBiome() {
//        BiomeDictionary.addTypes(vampire_forest, BiomeDictionary.Type.OVERWORLD, BiomeDictionary.Type.FOREST, BiomeDictionary.Type.DENSE, BiomeDictionary.Type.MAGICAL, BiomeDictionary.Type.SPOOKY); TODO 1.16
//        if (!VampirismConfig.SERVER.disableVampireForest.get()) {
//            BiomeManager.addBiome(BiomeManager.BiomeType.WARM, new BiomeManager.BiomeEntry(vampire_forest, VampirismConfig.BALANCE.vampireForestWeight.get()));
//        }
    }

    /**
     * Use only for adding to biome lists
     * <p>
     * Registered in mod contructor
     */
    public static void onBiomeLoadingEventAdditions(BiomeLoadingEvent event) {
        if (event.getSpawns().getEntityTypes().contains(EntityType.ZOMBIE)) {
            event.getSpawns().func_242575_a(EntityClassification.MONSTER, new MobSpawnInfo.Spawners(ModEntities.vampire, BalanceMobProps.mobProps.VAMPIRE_SPAWN_CHANCE, 1, 2));
            event.getSpawns().func_242575_a(EntityClassification.MONSTER, new MobSpawnInfo.Spawners(ModEntities.advanced_vampire, BalanceMobProps.mobProps.ADVANCED_VAMPIRE_SPAWN_PROBE, 1, 1));
        }
        event.getGeneration().func_242513_a(GenerationStage.Decoration.UNDERGROUND_STRUCTURES, ModFeatures.vampire_dungeon.withConfiguration(IFeatureConfig.NO_FEATURE_CONFIG).func_242729_a(VampirismConfig.BALANCE.vampireDungeonWeight.get()));
        if (!VampirismConfig.SERVER.disableHunterTentGen.get() && VampirismAPI.worldGenRegistry().canStructureBeGeneratedInBiome(ModFeatures.hunter_camp.getRegistryName(), event.getName(), event.getCategory())) {
            event.getGeneration().func_242516_a(ModFeatures.hunter_camp.func_236391_a_/*withConfiguration*/(IFeatureConfig.NO_FEATURE_CONFIG));
        }


    }
}
