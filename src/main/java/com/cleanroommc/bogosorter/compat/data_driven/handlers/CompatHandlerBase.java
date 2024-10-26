package com.cleanroommc.bogosorter.compat.data_driven.handlers;

import com.cleanroommc.bogosorter.api.IBogoSortAPI;
import com.cleanroommc.bogosorter.compat.data_driven.BogoCompatHandler;
import net.minecraft.inventory.Container;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

/**
 * @author ZZZank
 */
public abstract class CompatHandlerBase implements BogoCompatHandler {
    public final String targetName;

    public CompatHandlerBase(String className) {
        this.targetName = Objects.requireNonNull(className);
    }

    public static <T> @NotNull Class<? extends T> toClass(String className, Class<T> typeFilter) {
        Class<?> c;
        try {
            c = Class.forName(
                className,
                false,
                GeneralCompatHandler.class.getClassLoader()
            );
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        if (typeFilter.isAssignableFrom(c)) {
            return (Class<? extends T>) c;
        }
        throw new IllegalArgumentException();
    }

    @Nullable
    public Class<? extends Container> toClass() {
        return toClass(this.targetName, Container.class);
    }

    @Override
    public abstract void handle(IBogoSortAPI api);
}
