package dev.arubik.craftengine.util;

public interface DataHolder {
    void saveToData();
    void loadFromData();
    default void onLoad() {
        loadFromData();
    }
    default void onUnload(){
        saveToData();
    }

    default void destroy() {
        
    }
}
