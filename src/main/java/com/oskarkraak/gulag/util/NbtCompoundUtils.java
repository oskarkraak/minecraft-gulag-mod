package com.oskarkraak.gulag.util;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;

import java.util.Set;

public class NbtCompoundUtils {

    /**
     * Adds all elements from two NbtCompound into a new one.
     * Overwrites the element from nbtCompound1 if the key is present in both.
     */
    public static NbtCompound merge(NbtCompound nbtCompound1, NbtCompound nbtCompound2) {
        NbtCompound merged = new NbtCompound();
        Set<String> keys1 = nbtCompound1.getKeys();
        for (String key : keys1) {
            merged.put(key, nbtCompound1.get(key));
        }
        Set<String> keys2 = nbtCompound2.getKeys();
        for (String key : keys2) {
            merged.put(key, nbtCompound2.get(key));
        }
        return merged;
    }

    public static void removeAllElementsWithPrefix(String prefix, NbtCompound nbtCompound) {
        Object[] keys = nbtCompound.getKeys().toArray();
        for (int i = 0; i < keys.length; i++) {
            String key = (String) keys[i];
            if (key.startsWith(prefix)) {
                nbtCompound.remove(key);
            }
        }
    }

    public static void removeAllElementsWithoutPrefix(String prefix, NbtCompound nbtCompound) {
        Object[] keys = nbtCompound.getKeys().toArray();
        for (int i = 0; i < keys.length; i++) {
            String key = (String) keys[i];
            if (!key.startsWith(prefix)) {
                nbtCompound.remove(key);
            }
        }
    }

    public static void addPrefix(String prefix, NbtCompound nbtCompound) {
        Object[] keys = nbtCompound.getKeys().toArray();
        for (int i = 0; i < keys.length; i++) {
            String key = (String) keys[i];
            editKey(key, prefix + key, nbtCompound);
        }
    }

    public static void removePrefix(String prefix, NbtCompound nbtCompound) {
        Object[] keys = nbtCompound.getKeys().toArray();
        for (int i = 0; i < keys.length; i++) {
            String key = (String) keys[i];
            editKey(key, key.replaceFirst(prefix, ""), nbtCompound);
        }
    }

    public static void editKey(String before, String after, NbtCompound nbtCompound) {
        NbtElement element = nbtCompound.get(before);
        nbtCompound.remove(before);
        nbtCompound.put(after, element);
    }

}
