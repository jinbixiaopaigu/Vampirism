package de.teamlapen.vampirism.core;

import de.teamlapen.vampirism.REFERENCE;
import de.teamlapen.vampirism.api.VampirismRegistries;
import de.teamlapen.vampirism.api.items.oil.IOil;
import de.teamlapen.vampirism.items.oil.EffectWeaponOil;
import de.teamlapen.vampirism.items.oil.EvasionOil;
import de.teamlapen.vampirism.items.oil.Oil;
import de.teamlapen.vampirism.items.oil.SmeltingOil;
import net.minecraft.world.effect.MobEffects;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public class ModOils {
    public static final DeferredRegister<IOil> OILS = DeferredRegister.create(VampirismRegistries.OILS_ID, REFERENCE.MODID);

    public static final RegistryObject<IOil> EMPTY = OILS.register("empty", () -> new Oil(16253176));
    public static final RegistryObject<IOil> PLANT = OILS.register("plant", () -> new Oil(0x7e6d27));
    public static final RegistryObject<IOil> VAMPIRE_BLOOD = OILS.register("vampire_blood", () -> new Oil(0x922847));
    public static final RegistryObject<EffectWeaponOil> POISON = OILS.register("poison", () -> new EffectWeaponOil(MobEffects.POISON, 50, 15));
    public static final RegistryObject<EffectWeaponOil> WEAKNESS = OILS.register("weakness", () -> new EffectWeaponOil(MobEffects.WEAKNESS, 50, 15));
    public static final RegistryObject<EffectWeaponOil> SLOWNESS = OILS.register("slowness", () -> new EffectWeaponOil(MobEffects.MOVEMENT_SLOWDOWN, 100, 15));
    public static final RegistryObject<EffectWeaponOil> HEALING = OILS.register("healing", () -> new EffectWeaponOil(MobEffects.HEAL, 1, 5));
    public static final RegistryObject<EffectWeaponOil> FIRE_RESISTANCE = OILS.register("fire_resistance", () -> new EffectWeaponOil(MobEffects.FIRE_RESISTANCE, 200, 20));
    public static final RegistryObject<EffectWeaponOil> SWIFTNESS = OILS.register("swiftness", () -> new EffectWeaponOil(MobEffects.MOVEMENT_SPEED, 200, 15));
    public static final RegistryObject<EffectWeaponOil> REGENERATION = OILS.register("regeneration", () -> new EffectWeaponOil(MobEffects.REGENERATION, 100, 10));
    public static final RegistryObject<EffectWeaponOil> NIGHT_VISION = OILS.register("night_vision", () -> new EffectWeaponOil(MobEffects.NIGHT_VISION, 100, 15));
    public static final RegistryObject<EffectWeaponOil> STRENGTH = OILS.register("strength", () -> new EffectWeaponOil(MobEffects.DAMAGE_BOOST, 100, 10));
    public static final RegistryObject<EffectWeaponOil> JUMP = OILS.register("jump", () -> new EffectWeaponOil(MobEffects.JUMP, 100, 20));
    public static final RegistryObject<EffectWeaponOil> WATER_BREATHING = OILS.register("water_breathing", () -> new EffectWeaponOil(MobEffects.WATER_BREATHING, 200, 15));
    public static final RegistryObject<EffectWeaponOil> INVISIBILITY = OILS.register("invisibility", () -> new EffectWeaponOil(MobEffects.INVISIBILITY, 100, 15));
    public static final RegistryObject<EffectWeaponOil> SLOW_FALLING = OILS.register("slow_falling", () -> new EffectWeaponOil(MobEffects.SLOW_FALLING, 200, 20));
    public static final RegistryObject<EffectWeaponOil> LUCK = OILS.register("luck", () -> new EffectWeaponOil(MobEffects.LUCK, 200, 20));
    public static final RegistryObject<EffectWeaponOil> HARM = OILS.register("harm", () -> new EffectWeaponOil(MobEffects.HARM, 1, 5));
    public static final RegistryObject<SmeltingOil> SMELT = OILS.register("smelt", () -> new SmeltingOil(0x123456, 30));
    public static final RegistryObject<IOil> TELEPORT = OILS.register("teleport", () -> new Oil(0x0b4d42));
    public static final RegistryObject<EvasionOil> EVASION = OILS.register("evasion", () -> new EvasionOil(0x888800, 60));

    static void register(IEventBus bus) {
        OILS.register(bus);
    }
}
