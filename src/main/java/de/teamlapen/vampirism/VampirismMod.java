package de.teamlapen.vampirism;

import de.teamlapen.lib.HelperRegistry;
import de.teamlapen.lib.lib.entity.IPlayerEventListener;
import de.teamlapen.lib.lib.network.AbstractPacketDispatcher;
import de.teamlapen.lib.lib.network.ISyncable;
import de.teamlapen.lib.lib.util.IInitListener;
import de.teamlapen.lib.lib.util.VersionChecker;
import de.teamlapen.lib.util.Color;
import de.teamlapen.lib.util.OptifineHandler;
import de.teamlapen.vampirism.api.VReference;
import de.teamlapen.vampirism.api.VampirismAPI;
import de.teamlapen.vampirism.api.entity.IExtendedCreatureVampirism;
import de.teamlapen.vampirism.api.entity.factions.IFactionPlayerHandler;
import de.teamlapen.vampirism.api.entity.player.hunter.IHunterPlayer;
import de.teamlapen.vampirism.api.entity.player.skills.SkillType;
import de.teamlapen.vampirism.api.entity.player.vampire.IVampirePlayer;
import de.teamlapen.vampirism.api.world.IVampirismWorld;
import de.teamlapen.vampirism.client.core.ClientRegistryHandler;
import de.teamlapen.vampirism.config.BloodValues;
import de.teamlapen.vampirism.config.VampirismConfig;
import de.teamlapen.vampirism.core.*;
import de.teamlapen.vampirism.data.*;
import de.teamlapen.vampirism.entity.ExtendedCreature;
import de.teamlapen.vampirism.entity.ModEntityEventHandler;
import de.teamlapen.vampirism.entity.SundamageRegistry;
import de.teamlapen.vampirism.entity.action.ActionManagerEntity;
import de.teamlapen.vampirism.entity.converted.DefaultConvertingHandler;
import de.teamlapen.vampirism.entity.converted.VampirismEntityRegistry;
import de.teamlapen.vampirism.entity.factions.FactionPlayerHandler;
import de.teamlapen.vampirism.entity.factions.FactionRegistry;
import de.teamlapen.vampirism.entity.player.ModPlayerEventHandler;
import de.teamlapen.vampirism.entity.player.actions.ActionManager;
import de.teamlapen.vampirism.entity.player.hunter.HunterPlayer;
import de.teamlapen.vampirism.entity.player.skills.SkillManager;
import de.teamlapen.vampirism.entity.player.skills.SkillTreeManager;
import de.teamlapen.vampirism.entity.player.vampire.BloodVision;
import de.teamlapen.vampirism.entity.player.vampire.NightVision;
import de.teamlapen.vampirism.entity.player.vampire.VampirePlayer;
import de.teamlapen.vampirism.items.VampireRefinementItem;
import de.teamlapen.vampirism.misc.VampirismLogger;
import de.teamlapen.vampirism.modcompat.IMCHandler;
import de.teamlapen.vampirism.modcompat.terrablender.TerraBlenderCompat;
import de.teamlapen.vampirism.network.ModPacketDispatcher;
import de.teamlapen.vampirism.proxy.ClientProxy;
import de.teamlapen.vampirism.proxy.IProxy;
import de.teamlapen.vampirism.proxy.ServerProxy;
import de.teamlapen.vampirism.recipes.ExtendedBrewingRecipeRegistry;
import de.teamlapen.vampirism.sit.SitHandler;
import de.teamlapen.vampirism.tests.Tests;
import de.teamlapen.vampirism.util.*;
import de.teamlapen.vampirism.world.biome.OverworldModifications;
import de.teamlapen.vampirism.world.gen.VanillaStructureModifications;
import net.minecraft.ChatFormatting;
import net.minecraft.data.BuiltinRegistries;
import net.minecraft.data.DataGenerator;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.RegisterCapabilitiesEvent;
import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.event.AddReloadListenerEvent;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.*;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.RegisterEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

/**
 * Main class for Vampirism
 */
@Mod(value = REFERENCE.MODID)
public class VampirismMod {

    private static final Logger LOGGER = LogManager.getLogger();

    public static final AbstractPacketDispatcher dispatcher = new ModPacketDispatcher();
    public static final CreativeModeTab creativeTab = new CreativeModeTab(REFERENCE.MODID) {
        @NotNull
        @Override
        public ItemStack makeIcon() {
            return new ItemStack(ModItems.VAMPIRE_FANG.get());
        }
    };
    public static VampirismMod instance;
    public static final IProxy proxy = DistExecutor.runForDist(() -> ClientProxy::new, () -> ServerProxy::new);
    public static boolean inDev = false;
    public static boolean inDataGen = false;

    private final @NotNull RegistryManager registryManager = new RegistryManager();
    private VersionChecker.VersionInfo versionInfo;


    public VampirismMod() {
        instance = this;
        checkEnv();

        Optional<? extends net.minecraftforge.fml.ModContainer> opt = ModList.get().getModContainerById(REFERENCE.MODID);
        if (opt.isPresent()) {
            REFERENCE.VERSION = opt.get().getModInfo().getVersion();
        } else {
            LOGGER.warn("Cannot get version from mod info");
        }

        IEventBus modbus = FMLJavaModLoadingContext.get().getModEventBus();

        modbus.addListener(this::setup);
        modbus.addListener(this::enqueueIMC);
        modbus.addListener(this::processIMC);
        modbus.addListener(this::loadComplete);
        modbus.addListener(this::gatherData);
        modbus.addListener(this::registerCapabilities);
        modbus.addListener(this::finalizeConfiguration);

        DistExecutor.safeRunWhenOn(Dist.CLIENT, () -> ClientRegistryHandler::init);
        DistExecutor.runWhenOn(Dist.CLIENT, () -> () -> {
            modbus.addListener(this::setupClient);
        });

        MinecraftForge.EVENT_BUS.register(Permissions.class);
        MinecraftForge.EVENT_BUS.register(SitHandler.class);
        MinecraftForge.EVENT_BUS.addListener(this::onCommandsRegister);
        MinecraftForge.EVENT_BUS.addListener(this::onAddReloadListenerEvent);

        VampirismConfig.init();

        prepareAPI();
        RegistryManager.setupRegistries(modbus);

        if (OptifineHandler.isOptifineLoaded()) {
            LOGGER.warn("Using Optifine. Expect visual glitches and reduces blood vision functionality if using shaders.");
        }
        VanillaStructureModifications.createJigsawPool();

    }

    public VersionChecker.VersionInfo getVersionInfo() {

        return versionInfo;
    }

    public void onAddReloadListenerEvent(@NotNull AddReloadListenerEvent event) {
        SkillTreeManager.getInstance().getSkillTree().initRootSkills();//Load root skills here, so even if data pack reload fail, the root skills are available #622
        event.addListener(SkillTreeManager.getInstance());
        event.addListener(new BloodValues());

    }

    public void onCommandsRegister(@NotNull RegisterCommandsEvent event) {
        ModCommands.registerCommands(event.getDispatcher(), event.getBuildContext());
    }

    private void checkEnv() {
        String launchTarget = System.getProperty("vampirism_target");
        if (launchTarget != null && launchTarget.contains("dev")) {
            inDev = true;
        }
        if (launchTarget != null && launchTarget.contains("data")) {
            inDataGen = true;
        }
    }

    @SuppressWarnings("unchecked")
    private void enqueueIMC(final @NotNull InterModEnqueueEvent event) {
        onInitStep(IInitListener.Step.ENQUEUE_IMC, event);
        HelperRegistry.registerPlayerEventReceivingCapability((Capability<IPlayerEventListener>) (Object) VampirePlayer.CAP, VampirePlayer.class);
        HelperRegistry.registerPlayerEventReceivingCapability((Capability<IPlayerEventListener>) (Object) HunterPlayer.CAP, HunterPlayer.class);
        HelperRegistry.registerSyncableEntityCapability((Capability<ISyncable.ISyncableEntityCapabilityInst>) (Object) ExtendedCreature.CAP, REFERENCE.EXTENDED_CREATURE_KEY, ExtendedCreature.class);
        HelperRegistry.registerSyncablePlayerCapability((Capability<ISyncable.ISyncableEntityCapabilityInst>) (Object) VampirePlayer.CAP, REFERENCE.VAMPIRE_PLAYER_KEY, VampirePlayer.class);
        HelperRegistry.registerSyncablePlayerCapability((Capability<ISyncable.ISyncableEntityCapabilityInst>) (Object) HunterPlayer.CAP, REFERENCE.HUNTER_PLAYER_KEY, HunterPlayer.class);
        HelperRegistry.registerSyncablePlayerCapability((Capability<ISyncable.ISyncableEntityCapabilityInst>) (Object) FactionPlayerHandler.CAP, REFERENCE.FACTION_PLAYER_HANDLER_KEY, FactionPlayerHandler.class);
    }

    private void registerCapabilities(@NotNull RegisterCapabilitiesEvent event) {
        event.register(IExtendedCreatureVampirism.class);
        event.register(IFactionPlayerHandler.class);
        event.register(IHunterPlayer.class);
        event.register(IVampirePlayer.class);
        event.register(IVampirismWorld.class);
    }

    private void finalizeConfiguration(RegisterEvent event) {
        VampirismConfig.finalizeAndRegisterConfig();
    }

    /**
     * Finish API during InterModProcessEvent
     */
    private void finishAPI() {
        ((FactionRegistry) VampirismAPI.factionRegistry()).finish();
        ((VampirismEntityRegistry) VampirismAPI.entityRegistry()).finishRegistration();
    }

    private void gatherData(final @NotNull GatherDataEvent event) {
        registryManager.onGatherData(event);
        DataGenerator gen = event.getGenerator();

        ModBlockFamilies.init();
        TagGenerator.register(event, gen);
        gen.addProvider(event.includeServer(), new LootTablesGenerator(gen));
        gen.addProvider(event.includeServer(), new AdvancementGenerator(gen));
        gen.addProvider(event.includeServer(), new RecipesGenerator(gen));
        gen.addProvider(event.includeServer(), new SkillNodeGenerator(gen));
        BiomeGenerator.register(event, gen);

        gen.addProvider(event.includeClient(), new BlockStateGenerator(event.getGenerator(), event.getExistingFileHelper()));
        gen.addProvider(event.includeClient(), new ItemModelGenerator(event.getGenerator(), event.getExistingFileHelper()));
    }

    private void loadComplete(final @NotNull FMLLoadCompleteEvent event) {
        onInitStep(IInitListener.Step.LOAD_COMPLETE, event);
        event.enqueueWork(OverworldModifications::addBiomesToOverworldUnsafe);
        VampirismAPI.skillManager().registerSkillType(SkillType.LEVEL);
        VampirismAPI.skillManager().registerSkillType(SkillType.LORD);
        DistExecutor.safeRunWhenOn(Dist.DEDICATED_SERVER, () -> VampirismLogger::init);
    }


    /**
     * Called during constructor to set up the API as well as VReference
     */
    private void prepareAPI() {

        VampirismAPI.setUpRegistries(new FactionRegistry(), new SundamageRegistry(), new VampirismEntityRegistry().setDefaultConvertingHandlerCreator(DefaultConvertingHandler::new), new ActionManager(), new SkillManager(), new VampireVisionRegistry(), new ActionManagerEntity(), new ExtendedBrewingRecipeRegistry());
        DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> proxy::setupAPIClient);

        VReference.VAMPIRE_FACTION = VampirismAPI.factionRegistry()
                .createPlayableFaction(REFERENCE.VAMPIRE_PLAYER_KEY, IVampirePlayer.class, () -> VampirePlayer.CAP)
                .color(Color.MAGENTA_DARK.getRGB())
                .chatColor(ChatFormatting.DARK_PURPLE)
                .name("text.vampirism.vampire")
                .namePlural("text.vampirism.vampires")
                .hostileTowardsNeutral()
                .highestLevel(REFERENCE.HIGHEST_VAMPIRE_LEVEL)
                .lord().lordLevel(REFERENCE.HIGHEST_VAMPIRE_LORD).lordTitle(LordTitles::getVampireTitle).enableLordSkills().build()
                .village(VampireVillage::vampireVillage)
                .refinementItems(VampireRefinementItem::getItemForType)
                .register();
        VReference.HUNTER_FACTION = VampirismAPI.factionRegistry()
                .createPlayableFaction(REFERENCE.HUNTER_PLAYER_KEY, IHunterPlayer.class, () -> HunterPlayer.CAP)
                .color(Color.BLUE.getRGB())
                .chatColor(ChatFormatting.BLUE)
                .name("text.vampirism.hunter")
                .namePlural("text.vampirism.hunters")
                .highestLevel(REFERENCE.HIGHEST_HUNTER_LEVEL)
                .lord().lordLevel(REFERENCE.HIGHEST_HUNTER_LORD).lordTitle(LordTitles::getHunterTitle).enableLordSkills().build()
                .village(HunterVillage::hunterVillage)
                .register();

        VReference.vision_nightVision = VampirismAPI.vampireVisionRegistry().registerVision("nightVision", new NightVision());
        VReference.vision_bloodVision = VampirismAPI.vampireVisionRegistry().registerVision("bloodVision", new BloodVision());

        VampirismAPI.onSetupComplete();
    }

    private void processIMC(final @NotNull InterModProcessEvent event) {
        finishAPI();
        onInitStep(IInitListener.Step.PROCESS_IMC, event);
        IMCHandler.handleInterModMessage(event);
        if (inDev) {
            Tests.runBackgroundTests();
        }
    }

    private void setup(final @NotNull FMLCommonSetupEvent event) {
        dispatcher.registerPackets();
        onInitStep(IInitListener.Step.COMMON_SETUP, event);

        if (!VampirismConfig.COMMON.versionCheck.get()) {
            versionInfo = new VersionChecker.VersionInfo(REFERENCE.VERSION);
        } else {
            versionInfo = VersionChecker.executeVersionCheck(REFERENCE.VERSION_UPDATE_FILE, REFERENCE.VERSION, !inDev && VampirismConfig.COMMON.collectStats.get());
        }

        GeneralEventHandler eventHandler = new GeneralEventHandler();
        MinecraftForge.EVENT_BUS.register(eventHandler);

        MinecraftForge.EVENT_BUS.register(new ModPlayerEventHandler());

        MinecraftForge.EVENT_BUS.register(new ModEntityEventHandler());
        MinecraftForge.EVENT_BUS.addListener(ModLootTables::onLootLoad);

        SupporterManager.getInstance().initAsync();
        VampireBookManager.getInstance().init();
        ModEntitySelectors.registerSelectors();
        event.enqueueWork(TerraBlenderCompat::registerBiomeProviderIfPresentUnsafe);
        VanillaStructureModifications.addVillageStructures(BuiltinRegistries.ACCESS);

    }

    private void setupClient(@NotNull FMLClientSetupEvent event) {
        onInitStep(IInitListener.Step.CLIENT_SETUP, event);
    }

    private void onInitStep(IInitListener.@NotNull Step step, @NotNull ParallelDispatchEvent event) {
        registryManager.onInitStep(step, event);
        proxy.onInitStep(step, event);
    }

}
