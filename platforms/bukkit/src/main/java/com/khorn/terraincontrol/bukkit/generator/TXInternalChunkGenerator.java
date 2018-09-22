package com.khorn.terraincontrol.bukkit.generator;

import com.google.common.base.Preconditions;
import com.khorn.terraincontrol.bukkit.BukkitWorld;
import net.minecraft.server.v1_13_R2.BiomeBase;
import net.minecraft.server.v1_13_R2.BlockPosition;
import net.minecraft.server.v1_13_R2.EnumCreatureType;
import org.bukkit.craftbukkit.v1_13_R2.generator.CustomChunkGenerator;
import org.bukkit.generator.ChunkGenerator;

import java.util.List;

public class TXInternalChunkGenerator extends CustomChunkGenerator
{

    private final BukkitWorld localWorld;

    public TXInternalChunkGenerator(BukkitWorld world, ChunkGenerator generator)
    {
        super(world.getWorld(), world.getSeed(), generator);
        Preconditions.checkArgument(generator instanceof TXChunkGenerator, "Generator must be of the plugin");

        this.localWorld = world;
    }

    @Override
    public List<BiomeBase.BiomeMeta> getMobsFor(EnumCreatureType type, BlockPosition position)
    {
//        WorldConfig worldConfig = localWorld.getConfigs().getWorldConfig();
        BiomeBase biomebase = localWorld.getWorld().getBiome(position);
//        if (type == EnumCreatureType.MONSTER
//                && worldConfig.rareBuildingsEnabled
//                && localWorld.rareBuildingGen.isWitchHutAt(position))
//        {
//            return localWorld.rareBuildingGen.getWitchHutMobs();
//        }

//        if (type == EnumCreatureType.MONSTER
//                && worldConfig.oceanMonumentsEnabled
//                && localWorld.oceanMonumentGen.a(localWorld.getWorld(), position))
//        {
//            return localWorld.oceanMonumentGen.getMobs();
//        }

        return biomebase.getMobs(type);
    }

}
