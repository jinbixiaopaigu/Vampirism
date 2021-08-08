package de.teamlapen.vampirism.items;

import de.teamlapen.vampirism.VampirismMod;
import de.teamlapen.vampirism.api.items.IItemWithTier;
import de.teamlapen.vampirism.config.VampirismConfig;
import de.teamlapen.vampirism.core.ModItems;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Tiers;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import java.util.List;

import de.teamlapen.vampirism.api.items.IItemWithTier.TIER;
import net.minecraft.world.item.Item.Properties;

public class HeartSeekerItem extends VampirismVampireSword implements IItemWithTier {

    public static final String regName = "heart_seeker";
    private final static int[] DAMAGE_TIER = {7, 8, 9};
    private final static float[] UNTRAINED_SPEED_TIER = {-3.6f, -3.5f, -3.4f};
    private final static float[] TRAINED_SPEED_TIER = {-2.2f, -2.1f, -2f};
    private final TIER tier;

    public HeartSeekerItem(TIER tier) {
        super(regName + "_" + tier.getName(), Tiers.IRON, DAMAGE_TIER[tier.ordinal()], UNTRAINED_SPEED_TIER[tier.ordinal()], TRAINED_SPEED_TIER[tier.ordinal()], new Properties().tab(VampirismMod.creativeTab).durability(2500));
        this.tier = tier;
        this.setTranslation_key(regName);
    }


    @OnlyIn(Dist.CLIENT)
    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level worldIn, List<Component> tooltip, TooltipFlag flagIn) {
        super.appendHoverText(stack, worldIn, tooltip, flagIn);
        addTierInformation(tooltip);
    }

    @Override
    public String getBaseRegName() {
        return regName;
    }

    @Override
    public boolean isValidRepairItem(ItemStack toRepair, ItemStack repair) {
        return (this.getVampirismTier() == TIER.NORMAL ? ModItems.blood_infused_iron_ingot : ModItems.blood_infused_enhanced_iron_ingot).equals(repair.getItem()) || super.isValidRepairItem(toRepair, repair);
    }

    @Override
    public TIER getVampirismTier() {
        return tier;
    }

    @Override
    public float getXpRepairRatio(ItemStack stack) {
        return this.getVampirismTier() == TIER.ULTIMATE ? super.getXpRepairRatio(stack) / 2f : super.getXpRepairRatio(stack);
    }

    @Override
    protected float getChargeUsage() {
        return (float) ((VampirismConfig.BALANCE.vampireSwordBloodUsageFactor.get() / 100f) * (getVampirismTier().ordinal() + 2) / 2f);
    }

    @Override
    protected float getChargingFactor(ItemStack stack) {
        return (float) (VampirismConfig.BALANCE.vampireSwordChargingFactor.get() * 2f / (getVampirismTier().ordinal() + 2f));
    }

}
