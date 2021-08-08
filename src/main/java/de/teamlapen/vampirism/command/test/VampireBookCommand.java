package de.teamlapen.vampirism.command.test;

import com.mojang.brigadier.builder.ArgumentBuilder;
import de.teamlapen.lib.lib.util.BasicCommand;
import de.teamlapen.vampirism.util.VampireBookManager;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.server.level.ServerPlayer;

public class VampireBookCommand extends BasicCommand {

    public static ArgumentBuilder<CommandSourceStack, ?> register() {
        return Commands.literal("vampireBook")
                .requires(context -> context.hasPermission(PERMISSION_LEVEL_CHEAT))
                .executes(context -> {
                    return vampireBook(context.getSource().getPlayerOrException());
                });
    }

    private static int vampireBook(ServerPlayer asPlayer) {
        asPlayer.inventory.add(VampireBookManager.getInstance().getRandomBook(asPlayer.getRandom()));
        return 0;
    }
}
