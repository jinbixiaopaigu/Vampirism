package de.teamlapen.vampirism.entity.player.skills;

import com.google.gson.*;
import de.teamlapen.vampirism.VampirismMod;
import de.teamlapen.vampirism.network.ClientboundSkillTreePacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.GsonHelper;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraftforge.server.ServerLifecycleHooks;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;


public class SkillTreeManager extends SimpleJsonResourceReloadListener {

    private static final Logger LOGGER = LogManager.getLogger();
    private static final Gson GSON = new GsonBuilder().registerTypeHierarchyAdapter(SkillNode.Builder.class, (JsonDeserializer<SkillNode.Builder>) (json, typeOfT, context) -> {
        JsonObject asObject = GsonHelper.convertToJsonObject(json, "skillnode");
        return SkillNode.Builder.deserialize(asObject, context);
    }).create();
    private static SkillTreeManager instance;

    public static @NotNull SkillTreeManager getInstance() {
        if (instance == null) {
            instance = new SkillTreeManager();
        }
        return instance;
    }

    private final SkillTree skillTree = new SkillTree();

    private SkillTreeManager() {
        super(GSON, "vampirismskillnodes");
    }

    public @NotNull SkillTree getSkillTree() {
        return skillTree;
    }

    @Override
    protected void apply(@NotNull Map<ResourceLocation, JsonElement> resourceLocationJsonObjectMap, @NotNull ResourceManager iResourceManager, @NotNull ProfilerFiller iProfiler) {
        Map<ResourceLocation, SkillNode.Builder> parsed = new HashMap<>();
        resourceLocationJsonObjectMap.forEach((id, object) -> {
            try {
                SkillNode.Builder builder = GSON.fromJson(object, SkillNode.Builder.class);
                if (builder != null) {
                    parsed.put(id, builder);
                }
            } catch (IllegalArgumentException | JsonParseException e) {
                LOGGER.error("Failed to load skill node {}: {}", id, e.getMessage());
            }
        });

        skillTree.loadNodes(parsed);
        MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
        if (server != null) { //On first pack load server will be null, so sending package crashes
            VampirismMod.dispatcher.sendToAll(new ClientboundSkillTreePacket(skillTree.getCopy()));
        }
    }


}
