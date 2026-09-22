package net.firefoxsalesman.dungeonslibs.data.util;

import com.google.common.collect.ImmutableMap;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.mojang.serialization.Codec;
import com.mojang.serialization.JsonOps;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimplePreparableReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.OnDatapackSyncEvent;
import net.neoforged.neoforge.network.PacketDistributor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.Reader;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;

public class MergeableCodecDataManager<RAW, FINE> extends SimplePreparableReloadListener<Map<ResourceLocation, FINE>> {
	private static final Logger LOGGER = LogManager.getLogger();

	protected Map<ResourceLocation, FINE> data = new HashMap<>();

	private final String folderName;
	private final Codec<RAW> codec;
	private final Function<List<RAW>, FINE> merger;

	public MergeableCodecDataManager(final String folderName, final Codec<RAW> codec,
			final Function<List<RAW>, FINE> merger) {
		this.folderName = folderName;
		this.codec = codec;
		this.merger = merger;
	}

	public Map<ResourceLocation, FINE> getData() {
		return this.data;
	}

	public void setData(Map<ResourceLocation, FINE> data) {
		this.data = data;
	}

	@Override
	protected Map<ResourceLocation, FINE> prepare(final ResourceManager resourceManager,
			final ProfilerFiller profiler) {
		LOGGER.info("Beginning loading of data for data loader: {}", this.folderName);
		final Map<ResourceLocation, FINE> map = new HashMap<>();
		Map<ResourceLocation, List<Resource>> map1 = resourceManager.listResourceStacks(this.folderName,
				file -> file.getPath().endsWith(".json"));

		for (Map.Entry<ResourceLocation, List<Resource>> entry : map1.entrySet()) {
			ResourceLocation fullId = entry.getKey();
			String path = fullId.getPath();
			String prefix = this.folderName + "/";
			String idPath = path.substring(prefix.length(), path.length() - ".json".length());
			ResourceLocation id = ResourceLocation.fromNamespaceAndPath(fullId.getNamespace(), idPath);

			List<RAW> raws = new ArrayList<>();
			for (Resource resource : entry.getValue()) {
				try (Reader reader = resource.openAsReader()) {
					JsonElement jsonElement = JsonParser.parseReader(reader);
					this.codec.parse(JsonOps.INSTANCE, jsonElement)
							.ifSuccess(raws::add)
							.ifError(error -> LOGGER.error(
									"Error parsing json for resource {} from pack {}: {}",
									id, resource.sourcePackId(), error.message()));
				} catch (Exception e) {
					LOGGER.error(String.format(Locale.ENGLISH,
							"Error reading resource %s in folder %s from pack %s: ", id,
							this.folderName, resource.sourcePackId()), e);
				}
			}
			map.put(id, this.merger.apply(raws));
		}

		LOGGER.info("Data loader for {} loaded {} finalized objects", this.folderName, map.size());
		return ImmutableMap.copyOf(map);
	}

	@Override
	protected void apply(final Map<ResourceLocation, FINE> processedData, final ResourceManager resourceManager,
			final ProfilerFiller profiler) {
		this.data = processedData;
	}

	public <PACKET extends CustomPacketPayload> MergeableCodecDataManager<RAW, FINE> subscribeAsSyncable(
			final Function<Map<ResourceLocation, FINE>, PACKET> packetFactory) {
		NeoForge.EVENT_BUS.addListener(this.getDatapackSyncListener(packetFactory));
		return this;
	}

	private <PACKET extends CustomPacketPayload> Consumer<OnDatapackSyncEvent> getDatapackSyncListener(
			final Function<Map<ResourceLocation, FINE>, PACKET> packetFactory) {
		return event -> {
			ServerPlayer player = event.getPlayer();
			PACKET packet = packetFactory.apply(this.data);
			if (player == null) {
				PacketDistributor.sendToAllPlayers(packet);
			} else {
				PacketDistributor.sendToPlayer(player, packet);
			}
		};
	}
}
