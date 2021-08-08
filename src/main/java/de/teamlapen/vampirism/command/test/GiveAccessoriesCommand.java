package de.teamlapen.vampirism.command.test;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import de.teamlapen.lib.lib.util.BasicCommand;
import de.teamlapen.vampirism.api.VampirismAPI;
import de.teamlapen.vampirism.api.entity.factions.IFaction;
import de.teamlapen.vampirism.api.entity.player.refinement.IRefinementSet;
import de.teamlapen.vampirism.command.arguments.RefinementSetArgument;
import de.teamlapen.vampirism.core.ModItems;
import de.teamlapen.vampirism.items.VampireRefinementItem;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.network.chat.TranslatableComponent;


public class GiveAccessoriesCommand extends BasicCommand {

    public static ArgumentBuilder<CommandSourceStack, ?> register() {
        return Commands.literal("giveAccessories")
                .requires(context -> context.hasPermission(PERMISSION_LEVEL_CHEAT))
                .then(Commands.argument("slot", IntegerArgumentType.integer(1, 3))
                        .then(Commands.argument("set", RefinementSetArgument.actions())
                                .executes(context -> give(context, context.getSource().getPlayerOrException(), IntegerArgumentType.getInteger(context, "slot"), RefinementSetArgument.getSet(context, "set")))))
                .then(Commands.literal("random")
                        .executes(context -> random(context, context.getSource().getPlayerOrException(), 1))
                        .then(Commands.argument("amount", IntegerArgumentType.integer(1))
                                .executes(context -> random(context, context.getSource().getPlayerOrException(), IntegerArgumentType.getInteger(context, "amount")))))
                .then(Commands.literal("help")
                        .executes(GiveAccessoriesCommand::help));
    }

    private static int give(CommandContext<CommandSourceStack> context, ServerPlayer asPlayer, int number, IRefinementSet set) {
        VampireRefinementItem i;
        switch (number) {
            case 1:
                i = ModItems.amulet;
                break;
            case 2:
                i = ModItems.ring;
                break;
            default:
                i = ModItems.obi_belt;
                break;
        }
        ItemStack s = new ItemStack(i);
        if (i.applyRefinementSet(s, set)) {
            asPlayer.addItem(s);
            context.getSource().sendSuccess(new TranslatableComponent("command.vampirism.test.give_accessories.success", set.getName(), number), false);
        } else {
            context.getSource().sendSuccess(new TranslatableComponent("command.vampirism.test.give_accessories.incompatible", set.getName(), number), false);
        }

        return 0;
    }

    private static int help(CommandContext<CommandSourceStack> context) {
        context.getSource().sendSuccess(new TranslatableComponent("command.vampirism.test.give_accessories.help"), false);
        return 0;
    }

    private static int random(CommandContext<CommandSourceStack> context, ServerPlayer entity, int amount) {
        IFaction<?> faction = VampirismAPI.factionRegistry().getFaction(entity);
        for (int i = 0; i < amount; ++i) {
            ItemStack stack = VampireRefinementItem.getRandomRefinementItem(faction);
            if (!stack.isEmpty()) {
                entity.addItem(stack);
            } else {
                context.getSource().sendSuccess(new TranslatableComponent("command.vampirism.test.give_accessories.no_item"), false);
            }
        }
        return 0;
    }

}
