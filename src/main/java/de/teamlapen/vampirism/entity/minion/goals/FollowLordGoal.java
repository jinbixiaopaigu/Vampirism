package de.teamlapen.vampirism.entity.minion.goals;

import de.teamlapen.vampirism.api.entity.player.ILordPlayer;
import de.teamlapen.vampirism.entity.goals.MoveToPositionGoal;
import de.teamlapen.vampirism.entity.minion.MinionEntity;
import de.teamlapen.vampirism.entity.minion.management.MinionTasks;
import net.minecraft.world.phys.Vec3;
import net.minecraft.core.Vec3i;

import java.util.Optional;


public class FollowLordGoal extends MoveToPositionGoal<MinionEntity<?>> {


    private ILordPlayer lord;


    public FollowLordGoal(MinionEntity<?> entity, double followSpeedIn) {
        super(entity, followSpeedIn, 5, 15, true, true);
    }

    @Override
    public boolean canContinueToUse() {
        return super.canContinueToUse() && this.entity.getCurrentTask().filter(task -> task.getTask() == MinionTasks.follow_lord || task.getTask() == MinionTasks.protect_lord).isPresent();
    }

    @Override
    public boolean canUse() {
        if (!this.entity.getCurrentTask().filter(task -> task.getTask() == MinionTasks.follow_lord || task.getTask() == MinionTasks.protect_lord).isPresent())
            return false;
        Optional<ILordPlayer> lord = this.entity.getLordOpt();
        if (!lord.isPresent()) {
            return false;
        }
        this.lord = lord.get();
        if (!super.canUse()) {
            this.lord = null;
            return false;
        } else {
            return true;
        }
    }

    @Override
    public void stop() {
        super.stop();
        this.lord = null;
    }

    @Override
    protected Vec3 getLookPosition() {
        return lord.getPlayer().getEyePosition(1);
    }

    @Override
    protected Vec3i getTargetPosition() {
        return lord.getPlayer().blockPosition();
    }

}

