package net.firefoxsalesman.dungeonslibs.data.util;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.mojang.serialization.Codec;
import com.mojang.serialization.JsonOps;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.OnDatapackSyncEvent;
import net.neoforged.neoforge.network.PacketDistributor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Consumer;
import java.util.function.Function;

public class CodecJsonDataManager<T> extends SimpleJsonResourceReloadListener {
	private static final Gson STANDARD_GSON = new Gson();
	private static final Logger LOGGER = LogManager.getLogger();

	private final Codec<T> codec;
	private final String folderName;
	protected Map<ResourceLocation, T> data = new HashMap<>();

	public CodecJsonDataManager(String folderName, Codec<T> codec) {
		this(folderName, codec, STANDARD_GSON);
	}

	public CodecJsonDataManager(String folderName, Codec<T> codec, Gson gson) {
		super(gson, folderName);
		this.folderName = folderName;
		this.codec = codec;
	}

	public Map<ResourceLocation, T> getData() {
		return data;
	}

	public void setData(Map<ResourceLocation, T> data) {
		this.data = data;
	}

	@Override
	protected void apply(Map<ResourceLocation, JsonElement> jsons, ResourceManager resourceManager,
			ProfilerFiller profiler) {
		LOGGER.info("Beginning loading of data for data loader: {}", folderName);
		Map<ResourceLocation, T> newMap = new HashMap<>();

		for (Entry<ResourceLocation, JsonElement> entry : jsons.entrySet()) {
			ResourceLocation key = entry.getKey();
			JsonElement element = entry.getValue();
			codec.parse(JsonOps.INSTANCE, element)
					.ifSuccess(result -> newMap.put(key, result))
					.ifError(partial -> LOGGER.error("Failed to parse data json for {} due to: {}",
							key, partial.message()));
		}

		data = newMap;
		LOGGER.info("Data loader for {} loaded {} jsons", folderName, data.size());
	}

	public <PACKET extends CustomPacketPayload> CodecJsonDataManager<T> subscribeAsSyncable(
			final Function<Map<ResourceLocation, T>, PACKET> packetFactory) {
		NeoForge.EVENT_BUS.addListener(getDatapackSyncListener(packetFactory));
		return this;
	}

	private <PACKET extends CustomPacketPayload> Consumer<OnDatapackSyncEvent> getDatapackSyncListener(
			final Function<Map<ResourceLocation, T>, PACKET> packetFactory) {
		return event -> {
			ServerPlayer player = event.getPlayer();
			PACKET packet = packetFactory.apply(data);
			if (player == null) {
				PacketDistributor.sendToAllPlayers(packet);
			} else {
				PacketDistributor.sendToPlayer(player, packet);
			}
		};
	}
}
