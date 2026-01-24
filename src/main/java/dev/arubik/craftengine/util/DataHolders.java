package dev.arubik.craftengine.util;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class DataHolders {
    public final Set<DataHolder> holders = ConcurrentHashMap.newKeySet();

    public void addHolder(DataHolder holder) {
        holders.add(holder);
        holder.onLoad();
    }

    public void removeHolder(DataHolder holder) {
        holders.remove(holder);
        holder.onUnload();
    }

    public void destroyHolder(DataHolder holder) {
        if (holders.remove(holder)) {
            holder.destroy();
        }
    }

    public void removeHolders() {
        for (DataHolder holder : holders) {
            holder.onUnload();
        }
        holders.clear();
    }

    public static DataHolders getInstance() {
        return INSTANCE;
    }

    public static final DataHolders INSTANCE = new DataHolders();
}
