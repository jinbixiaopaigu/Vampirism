package de.teamlapen.vampirism.player.tasks;

import com.google.common.base.Objects;
import de.teamlapen.vampirism.api.entity.player.IFactionPlayer;
import de.teamlapen.vampirism.api.entity.player.task.ITaskInstance;
import de.teamlapen.vampirism.api.entity.player.task.ITaskRewardInstance;
import de.teamlapen.vampirism.api.entity.player.task.Task;
import de.teamlapen.vampirism.core.ModRegistries;
import de.teamlapen.vampirism.player.TaskManager;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class TaskInstance implements ITaskInstance {

    /**
     * @return {@code null} if the task does not exist
     */
    @Nullable
    public static TaskInstance readNBT(@Nonnull CompoundTag nbt) {
        Task task = ModRegistries.TASKS.getValue(new ResourceLocation(nbt.getString("task")));
        if (task == null) return null;
        UUID id = nbt.getUUID("id");
        UUID insId = nbt.getUUID("insId");
        boolean accepted = nbt.getBoolean("accepted");
        long taskTimer = nbt.getLong("taskTimer");
        CompoundTag statsNBT = nbt.getCompound("stats");
        Map<ResourceLocation, Integer> stats = new HashMap<>();
        statsNBT.getAllKeys().forEach(name -> {
            stats.put(new ResourceLocation(name), statsNBT.getInt(name));
        });
        ResourceLocation rewardId = new ResourceLocation(nbt.getString("rewardId"));
        ITaskRewardInstance reward = TaskManager.createReward(rewardId, nbt);
        long taskDuration = nbt.getLong("taskDuration");
        return new TaskInstance(id, task, stats, accepted, taskTimer, insId, reward, taskDuration);
    }

    public static TaskInstance decode(FriendlyByteBuf buffer) {
        UUID id = buffer.readUUID();
        Task task = ModRegistries.TASKS.getValue(buffer.readResourceLocation());
        UUID insId = buffer.readUUID();
        boolean accepted = buffer.readBoolean();
        long taskTimer = buffer.readVarLong();
        int statsAmount = buffer.readVarInt();
        Map<ResourceLocation, Integer> stats = new HashMap<>();
        for (int i = 0; i < statsAmount; i++) {
            stats.put(buffer.readResourceLocation(), buffer.readVarInt());
        }
        ResourceLocation rewardId = buffer.readResourceLocation();
        ITaskRewardInstance reward = TaskManager.createReward(rewardId, buffer);
        long taskDuration = buffer.readVarLong();
        return new TaskInstance(id, task, stats, accepted, taskTimer, insId, reward, taskDuration);
    }
    @Nonnull
    private final UUID taskGiver;
    @Nonnull
    private final Task task;
    @Nonnull
    private final UUID instanceId;
    @Nonnull
    private final Map<ResourceLocation, Integer> stats;
    @Nonnull
    private final ITaskRewardInstance reward;
    private final long taskDuration;
    private boolean accepted;
    private long taskTimeStamp;
    private boolean completed;

    public TaskInstance(@Nonnull Task task, @Nonnull UUID taskGiver, @Nonnull IFactionPlayer<?> player, long taskDuration) {
        this.task = task;
        this.taskGiver = taskGiver;
        this.instanceId = UUID.randomUUID();
        this.stats = new HashMap<>();
        this.taskTimeStamp = -1;
        this.taskDuration = taskDuration;
        this.reward = this.task.getReward().createInstance(player);
    }

    private TaskInstance(@Nonnull UUID taskGiver, @Nonnull Task task, @Nonnull Map<ResourceLocation, Integer> stats, boolean accepted, long taskTimeStamp, @Nonnull UUID instanceId, @Nonnull ITaskRewardInstance taskRewardInstance, long taskDuration) {
        this.taskGiver = taskGiver;
        this.task = task;
        this.stats = stats;
        this.accepted = accepted;
        this.taskTimeStamp = taskTimeStamp;
        this.instanceId = instanceId;
        this.reward = taskRewardInstance;
        this.taskDuration = taskDuration;
    }

    public void aboardTask() {
        this.accepted = false;
        this.stats.clear();
        this.taskTimeStamp = -1;
    }

    public void complete() {
        this.completed = true;
    }

    public void encode(FriendlyByteBuf buffer) {
        buffer.writeUUID(this.taskGiver);
        buffer.writeResourceLocation(this.task.getRegistryName());
        buffer.writeUUID(this.instanceId);
        buffer.writeBoolean(this.accepted);
        buffer.writeVarLong(this.taskTimeStamp);
        buffer.writeVarInt(this.stats.size());
        this.stats.forEach((loc, val) -> {
            buffer.writeResourceLocation(loc);
            buffer.writeVarInt(val);
        });
        buffer.writeResourceLocation(this.reward.getId());
        this.reward.encode(buffer);
        buffer.writeVarLong(this.taskDuration);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TaskInstance instance = (TaskInstance) o;
        return accepted == instance.accepted && taskTimeStamp == instance.taskTimeStamp && Objects.equal(taskGiver, instance.taskGiver) && Objects.equal(instanceId, instance.instanceId) && Objects.equal(task, instance.task) && Objects.equal(stats, instance.stats);
    }

    @Nonnull
    public UUID getId() {
        return instanceId;
    }

    @Nonnull
    @Override
    public ITaskRewardInstance getReward() {
        return this.reward;
    }

    @Nonnull
    public Map<ResourceLocation, Integer> getStats() {
        return stats;
    }

    public void setStats(@Nonnull Map<ResourceLocation, Integer> newStats) {
        this.stats.clear();
        this.stats.putAll(newStats);
    }

    @Nonnull
    public Task getTask() {
        return task;
    }

    @Override
    public UUID getTaskBoard() {
        return this.taskGiver;
    }

    public long getTaskDuration() {
        return taskDuration;
    }

    public long getTaskTimeStamp() {
        return taskTimeStamp;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(taskGiver, task, instanceId);
    }

    public boolean isAccepted() {
        return accepted;
    }

    public boolean isCompleted() {
        return completed;
    }

    public boolean isUnique() {
        return this.task.isUnique();
    }

    public void startTask(long timestamp) {
        this.taskTimeStamp = timestamp;
        this.accepted = true;
    }

    public CompoundTag writeNBT(@Nonnull CompoundTag nbt) {
        nbt.putUUID("id", this.taskGiver);
        nbt.putString("task", this.task.getRegistryName().toString());
        nbt.putUUID("insId", this.instanceId);
        nbt.putBoolean("accepted", this.accepted);
        nbt.putLong("taskTimer", this.taskTimeStamp);
        CompoundTag stats = new CompoundTag();
        this.stats.forEach((loc, amount) -> {
            stats.putInt(loc.toString(), amount);
        });
        nbt.put("stats", stats);
        nbt.putString("rewardId", this.reward.getId().toString());
        this.reward.writeNBT(nbt);
        nbt.putLong("taskDuration", this.taskDuration);
        return nbt;
    }
}
