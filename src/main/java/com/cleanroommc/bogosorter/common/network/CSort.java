package com.cleanroommc.bogosorter.common.network;

import com.cleanroommc.bogosorter.BogoSortAPI;
import com.cleanroommc.bogosorter.api.SortRule;
import com.cleanroommc.bogosorter.common.sort.ClientSortData;
import com.cleanroommc.bogosorter.common.sort.SortHandler;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import net.minecraft.item.ItemStack;
import net.minecraft.network.NetHandlerPlayServer;
import net.minecraft.network.PacketBuffer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class CSort implements IPacket {

    private List<ClientSortData> clientSortDataList;
    private List<SortRule<ItemStack>> sortRules;
    private int hover;
    private boolean player;

    public CSort(List<ClientSortData> clientSortDataList, List<SortRule<ItemStack>> sortRules, int hover, boolean player) {
        this.clientSortDataList = clientSortDataList;
        this.sortRules = sortRules;
        this.hover = hover;
        this.player = player;
    }

    public CSort() {
    }

    @Override
    public void encode(PacketBuffer buf) throws IOException {
        buf.writeVarInt(hover);
        buf.writeBoolean(player);
        buf.writeVarInt(clientSortDataList.size());
        for (ClientSortData sortData : clientSortDataList) {
            sortData.writeToPacket(buf);
        }
        buf.writeVarInt(sortRules.size());
        for (SortRule<ItemStack> sortRule : sortRules) {
            buf.writeVarInt(sortRule.getSyncId());
        }
    }

    @Override
    public void decode(PacketBuffer buf) throws IOException {
        hover = buf.readVarInt();
        player = buf.readBoolean();
        clientSortDataList = new ArrayList<>();
        for (int i = 0, n = buf.readVarInt(); i < n; i++) {
            clientSortDataList.add(ClientSortData.readFromPacket(buf));
        }
        sortRules = new ArrayList<>();
        for (int i = 0, n = buf.readVarInt(); i < n; i++) {
            sortRules.add(BogoSortAPI.INSTANCE.getItemSortRule(buf.readVarInt()));
        }
    }

    @Override
    public IPacket executeServer(NetHandlerPlayServer handler) {
        Int2ObjectOpenHashMap<ClientSortData> map = new Int2ObjectOpenHashMap<>();
        for(ClientSortData sortData : clientSortDataList) {
            map.put(sortData.getSlotNumber(), sortData);
        }
        SortHandler sortHandler = new SortHandler(handler.player, handler.player.openContainer, player, sortRules, map);
        sortHandler.sort(hover);
        return null;
    }
}
