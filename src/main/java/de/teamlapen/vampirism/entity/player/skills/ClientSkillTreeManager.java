package de.teamlapen.vampirism.entity.player.skills;

import de.teamlapen.vampirism.network.ClientboundSkillTreePacket;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;

@OnlyIn(Dist.CLIENT)
public class ClientSkillTreeManager {
    private final SkillTree skillTree = new SkillTree();

    public @NotNull SkillTree getSkillTree() {
        return skillTree;
    }

    public void init() {
        skillTree.initRootSkills();
    }

    public void loadUpdate(@NotNull ClientboundSkillTreePacket msg) {
        skillTree.loadNodes(msg.nodes());
        skillTree.updateRenderInfo();
    }
}
