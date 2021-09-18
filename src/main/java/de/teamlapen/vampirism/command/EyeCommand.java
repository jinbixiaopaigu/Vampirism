package de.teamlapen.vampirism.command;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import de.teamlapen.lib.lib.util.BasicCommand;
import de.teamlapen.vampirism.REFERENCE;
import de.teamlapen.vampirism.player.vampire.VampirePlayer;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.entity.player.Player;

public class EyeCommand extends BasicCommand {

    public static ArgumentBuilder<CommandSourceStack, ?> register() {
        return Commands.literal("eye")
                .then(Commands.argument("type", IntegerArgumentType.integer(0, REFERENCE.EYE_TYPE_COUNT - 1))
                        .executes(context -> setEye(context, context.getSource().getPlayerOrException(), IntegerArgumentType.getInteger(context, "type"))));
    }

    private static int setEye(CommandContext<CommandSourceStack> context, Player player, int type) {
        if (VampirePlayer.getOpt(player).map(vampire -> vampire.setEyeType(type)).orElse(false)) {
            context.getSource().sendSuccess(new TranslatableComponent("command.vampirism.base.eye.success", type), false);
        }
        return type;
    }

}
