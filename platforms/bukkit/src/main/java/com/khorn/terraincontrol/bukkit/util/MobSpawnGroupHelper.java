package com.khorn.terraincontrol.bukkit.util;

import com.khorn.terraincontrol.TerrainControl;
import com.khorn.terraincontrol.configuration.WeightedMobSpawnGroup;
import com.khorn.terraincontrol.configuration.standard.MojangSettings.EntityCategory;
import com.khorn.terraincontrol.logging.LogMarker;

import net.minecraft.server.v1_13_R2.BiomeBase;
import net.minecraft.server.v1_13_R2.BiomeBase.BiomeMeta;
import net.minecraft.server.v1_13_R2.EntityInsentient;
import net.minecraft.server.v1_13_R2.EntityTypes;
import net.minecraft.server.v1_13_R2.EnumCreatureType;
import net.minecraft.server.v1_13_R2.MinecraftKey;
import net.minecraft.server.v1_13_R2.WeightedRandom.WeightedRandomChoice;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.bukkit.entity.EntityType;

/**
 * Methods for conversion between mob lists in Minecraft and in the plugin.
 *
 */
public final class MobSpawnGroupHelper
{
    private static final Field WEIGHT_FIELD;

    static
    {
        try
        {
            WEIGHT_FIELD = WeightedRandomChoice.class.getDeclaredField("a");
            WEIGHT_FIELD.setAccessible(true);
        } catch (Exception e)
        {
            throw new RuntimeException("Reflection error", e);
        }
    }

    /**
     * Transforms our MobType into Minecraft's EnumCreatureType.
     * @param type Our type.
     * @return Minecraft's type.
     */
    private static EnumCreatureType toEnumCreatureType(EntityCategory type)
    {
        switch (type)
        {
            case MONSTER:
                return EnumCreatureType.MONSTER;
            case CREATURE:
                return EnumCreatureType.CREATURE;
            case AMBIENT_CREATURE:
                return EnumCreatureType.AMBIENT;
            case WATER_CREATURE:
                return EnumCreatureType.WATER_CREATURE;
        }
        throw new AssertionError("Unknown mob type: " + type);
    }

    /**
     * Transforms a single Minecraft BiomeMeta into our type.
     * @param biomeMeta Minecraft's type.
     * @return Our type.
     */
    private static WeightedMobSpawnGroup fromMinecraftGroup(BiomeMeta biomeMeta)
    {
        return new WeightedMobSpawnGroup(fromMinecraftClass(biomeMeta.b), getWeight(biomeMeta), biomeMeta.c, biomeMeta.d);
    }

    /**
     * For some reason, the weight field in the BiomeMeta class is protected
     * and has no getter. This method uses reflection to get around that.
     * @param biomeMeta The mob spawn entry.
     * @return The weight of the mob spawn entry.
     */
    private static int getWeight(BiomeMeta biomeMeta)
    {
        try
        {
            return WEIGHT_FIELD.getInt(biomeMeta);
        } catch (Exception e)
        {
            throw new RuntimeException(e);
        }
    }

    /**
     * Gets the spawn list of the given biome for the given category.
     * @param biome The biome.
     * @param type  The category.
     * @return The spawn list for the given category.
     */
    public static List<WeightedMobSpawnGroup> getListFromMinecraftBiome(BiomeBase biome, EntityCategory type)
    {
        Collection<BiomeMeta> mobList = biome.getMobs(toEnumCreatureType(type));
        return fromMinecraftList(mobList);
    }

    /**
     * Converts a BiomeMeta collection to a WeightedMobSpawnGroup list. This
     * method is the inverse of {@link #toMinecraftlist(Collection)}.
     * @param biomeMetas The BiomeMeta collection.
     * @return The WeightedMobSpawnGroup list.
     */
    static List<WeightedMobSpawnGroup> fromMinecraftList(Collection<BiomeMeta> biomeMetas)
    {
        List<WeightedMobSpawnGroup> result = new ArrayList<WeightedMobSpawnGroup>();
        for (BiomeMeta meta : biomeMetas)
        {
            result.add(fromMinecraftGroup(meta));
        }
        return result;
    }

    /**
     * Converts a WeightedMobSpawnGroup collection to a BiomeMeta collection.
     * This method is the inverse of {@link #fromMinecraftList(Collection)}.
     * @param weightedMobSpawnGroups The WeighedMobSpawnGroup collection.
     * @return The BiomeMeta list.
     */
    public static List<BiomeMeta> toMinecraftlist(Collection<WeightedMobSpawnGroup> weightedMobSpawnGroups)
    {
        List<BiomeMeta> biomeList = new ArrayList<BiomeMeta>();
        for (WeightedMobSpawnGroup mobGroup : weightedMobSpawnGroups)
        {
            EntityTypes<? extends EntityInsentient> entityClass = toMinecraftClass(mobGroup.getInternalName());
            if (entityClass != null)
            {
                biomeList.add(new BiomeMeta(entityClass, mobGroup.getWeight(), mobGroup.getMin(), mobGroup.getMax()));
            } else
            {
                TerrainControl.log(LogMarker.WARN, "Mob type {} not found",
                        mobGroup.getInternalName());
            }
        }
        return biomeList;
    }
    
    /**
     * Gets the entity class corresponding to the given entity name. This
     * method is the inverse of {@link #fromMinecraftClass(Class)}.
     * @param mobName The mob name.
     * @return The enti	ty class, or null if not found.
     */
    static EntityTypes<? extends EntityInsentient> toMinecraftClass(String mobName)
    {
		Class<EntityType> clazz = null;//EntityTypes.clsToTypeMap.get(new MinecraftKey(mobName)).getDeclaringClass();
        if (clazz == null)
        {
            return null;
        }
        if (EntityInsentient.class.isAssignableFrom(clazz))
        {
            //return (EntityTypes<? extends EntityInsentient>) clazz.asSubclass(EntityInsentient.class);
        }
        return null;
    }

    /**
     * Gets the entity name corresponding to the given entity class.
     * @param b The entity class.
     * @return The entity name, or null if not found.
     */
    private static String fromMinecraftClass(EntityTypes<? extends EntityInsentient> b)
    {
        return b.toString();
    }
}
