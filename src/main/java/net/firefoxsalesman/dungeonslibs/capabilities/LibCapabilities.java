package net.firefoxsalesman.dungeonslibs.capabilities;

import net.firefoxsalesman.dungeonslibs.DungeonsLibs;
import net.firefoxsalesman.dungeonslibs.capabilities.artifact.ArtifactUsage;
import net.firefoxsalesman.dungeonslibs.capabilities.builtinenchantments.BuiltInEnchantments;
import net.firefoxsalesman.dungeonslibs.capabilities.elite.EliteMob;
import net.firefoxsalesman.dungeonslibs.capabilities.enchantedprojectile.EnchantedProjectile;
import net.firefoxsalesman.dungeonslibs.capabilities.minionmaster.Follower;
import net.firefoxsalesman.dungeonslibs.capabilities.minionmaster.Leader;
import net.firefoxsalesman.dungeonslibs.capabilities.playerrewards.PlayerRewards;
import net.firefoxsalesman.dungeonslibs.capabilities.soulcaster.SoulCaster;
import net.firefoxsalesman.dungeonslibs.capabilities.timers.Timers;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

import java.util.function.Supplier;

public class LibCapabilities {
	public static final DeferredRegister<AttachmentType<?>> ATTACHMENT_TYPES = DeferredRegister.create(
			NeoForgeRegistries.ATTACHMENT_TYPES, DungeonsLibs.MOD_ID);

	public static final Supplier<AttachmentType<Timers>> TIMERS_ATTACHMENT = ATTACHMENT_TYPES.register(
			"timers", () -> AttachmentType.serializable(Timers::new).build());

	public static final Supplier<AttachmentType<ArtifactUsage>> ARTIFACT_USAGE_ATTACHMENT = ATTACHMENT_TYPES.register(
			"artifact_usage", () -> AttachmentType.serializable(ArtifactUsage::new).build());

	public static final Supplier<AttachmentType<Follower>> FOLLOWER_ATTACHMENT = ATTACHMENT_TYPES.register(
			"follower", () -> AttachmentType.serializable(Follower::new).build());

	public static final Supplier<AttachmentType<Leader>> LEADER_ATTACHMENT = ATTACHMENT_TYPES.register(
			"leader", () -> AttachmentType.serializable(Leader::new).build());

	public static final Supplier<AttachmentType<SoulCaster>> SOUL_CASTER_ATTACHMENT = ATTACHMENT_TYPES.register(
			"soul_caster", () -> AttachmentType.serializable(SoulCaster::new).build());

	public static final Supplier<AttachmentType<EnchantedProjectile>> ENCHANTED_PROJECTILE_ATTACHMENT = ATTACHMENT_TYPES.register(
			"enchanted_projectile", () -> AttachmentType.serializable(EnchantedProjectile::new).build());

	public static final Supplier<AttachmentType<PlayerRewards>> PLAYER_REWARDS_ATTACHMENT = ATTACHMENT_TYPES.register(
			"player_rewards", () -> AttachmentType.serializable(PlayerRewards::new).build());

	public static final Supplier<AttachmentType<EliteMob>> ELITE_MOB_ATTACHMENT = ATTACHMENT_TYPES.register(
			"elite_mob", () -> AttachmentType.serializable(EliteMob::new).build());

	public static void register(IEventBus modEventBus) {
		ATTACHMENT_TYPES.register(modEventBus);
	}

	public static void setupCapabilities() {
		// Old Forge capability setup no longer required in NeoForge
	}
}
