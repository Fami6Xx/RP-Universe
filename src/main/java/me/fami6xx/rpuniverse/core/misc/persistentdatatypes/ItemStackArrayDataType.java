package me.fami6xx.rpuniverse.core.misc.persistentdatatypes;

import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataAdapterContext;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.util.io.BukkitObjectInputStream;
import org.bukkit.util.io.BukkitObjectOutputStream;
import org.jetbrains.annotations.NotNull;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class ItemStackArrayDataType implements PersistentDataType<byte[], ItemStack[]> {
    @Override
    public @NotNull Class<byte[]> getPrimitiveType() {
        return byte[].class;
    }

    @Override
    public @NotNull Class<ItemStack[]> getComplexType() {
        return ItemStack[].class;
    }

    @Override
    public byte @NotNull [] toPrimitive(ItemStack[] complex, @NotNull PersistentDataAdapterContext context) {
        try {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            BukkitObjectOutputStream dataOutput = new BukkitObjectOutputStream(outputStream);

            dataOutput.writeInt(complex.length);

            for (ItemStack item : complex) {
                dataOutput.writeObject(item);
            }

            dataOutput.close();
            return outputStream.toByteArray();
        } catch (IOException e) {
            throw new IllegalStateException("Item Stack Array cannot be saved.", e);
        }
    }

    @Override
    public ItemStack @NotNull [] fromPrimitive(byte @NotNull [] primitive, @NotNull PersistentDataAdapterContext context) {
        try {
            ByteArrayInputStream inputStream = new ByteArrayInputStream(primitive);
            BukkitObjectInputStream dataInput = new BukkitObjectInputStream(inputStream);

            int length = dataInput.readInt();
            ItemStack[] items = new ItemStack[length];

            for (int i = 0; i < length; i++) {
                items[i] = (ItemStack) dataInput.readObject();
            }

            dataInput.close();
            return items;
        } catch (IOException | ClassNotFoundException e) {
            throw new IllegalStateException("Item Stack Array cannot be loaded.", e);
        }
    }
}
