package de.teamlapen.vampirism.items;

import de.teamlapen.vampirism.VampirismMod;
import de.teamlapen.vampirism.api.VReference;
import de.teamlapen.vampirism.api.items.IItemWithTier;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Tiers;
import net.minecraft.core.NonNullList;
import net.minecraft.network.chat.Component;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.teamlapen.vampirism.api.items.IItemWithTier.TIER;
import net.minecraft.world.item.Item.Properties;

public class HunterAxeItem extends VampirismHunterWeapon implements IItemWithTier {
    private static final String regName = "hunter_axe";

    private final TIER tier;


    public HunterAxeItem(TIER tier) {
        super(regName + "_" + tier.getName(), Tiers.IRON, 10, -2.9f, new Properties().tab(VampirismMod.creativeTab));
        this.tier = tier;
        this.setTranslation_key(regName);
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level worldIn, List<Component> tooltip, TooltipFlag flagIn) {
        super.appendHoverText(stack, worldIn, tooltip, flagIn);
        addTierInformation(tooltip);
        tooltip.add(new TranslatableComponent("text.vampirism.deals_more_damage_to", Math.round((getVampireMult() - 1) * 100), VReference.VAMPIRE_FACTION.getNamePlural()).withStyle(ChatFormatting.GRAY));
    }

    @Override
    public void fillItemCategory(CreativeModeTab group, NonNullList<ItemStack> items) {
        if (this.allowdedIn(group)) {
            items.add(getEnchantedStack());
        }
    }

    @Override
    public String getBaseRegName() {
        return regName;
    }

    @Override
    public float getDamageMultiplierForFaction(@Nonnull ItemStack stack) {
        return getVampireMult();
    }

    /**
     * @return A itemstack with the correct knockback enchantment applied
     */
    public ItemStack getEnchantedStack() {
        ItemStack stack = new ItemStack(this);
        Map<Enchantment, Integer> map = new HashMap<>();
        map.put(Enchantments.KNOCKBACK, getKnockback());
        EnchantmentHelper.setEnchantments(map, stack);
        return stack;
    }

    @Override
    public int getMinLevel(@Nonnull ItemStack stack) {
        return getMinLevel();
    }

    @Override
    public TIER getVampirismTier() {
        return tier;
    }

    @Override
    public boolean isFoil(ItemStack stack) {
        return false;
    }

    private int getKnockback() {
        switch (tier) {
            case ULTIMATE:
                return 4;
            case ENHANCED:
                return 3;
            default:
                return 2;
        }
    }

    private int getMinLevel() {
        switch (tier) {
            case ULTIMATE:
                return 8;
            case ENHANCED:
                return 6;
            default:
                return 4;
        }
    }

    private float getVampireMult() {
        switch (tier) {
            case ULTIMATE:
                return 1.5F;
            case ENHANCED:
                return 1.3F;
            default:
                return 1.2F;
        }
    }


}
