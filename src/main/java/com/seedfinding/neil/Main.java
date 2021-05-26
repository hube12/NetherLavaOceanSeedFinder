package com.seedfinding.neil;

import kaptainwutax.biomeutils.source.BiomeSource;
import kaptainwutax.featureutils.structure.Fortress;
import kaptainwutax.mcutils.block.Block;
import kaptainwutax.mcutils.block.Blocks;
import kaptainwutax.mcutils.rand.ChunkRand;
import kaptainwutax.mcutils.state.Dimension;
import kaptainwutax.mcutils.util.data.Pair;
import kaptainwutax.mcutils.util.math.DistanceMetric;
import kaptainwutax.mcutils.util.pos.BPos;
import kaptainwutax.mcutils.util.pos.CPos;
import kaptainwutax.mcutils.util.pos.RPos;
import kaptainwutax.mcutils.version.MCVersion;
import kaptainwutax.terrainutils.ChunkGenerator;

import java.util.Scanner;
import java.util.function.Predicate;
import java.util.stream.LongStream;

public class Main {
	public static final MCVersion VERSION = MCVersion.v1_16_5;
	public static final Predicate<Block> OK_BLOCK = b -> b == Blocks.LAVA || b == Blocks.AIR;
	public static final int SUITABLE_OFFSET = 7; // how many block above sea level should be ok
	public static final int circleRadius = 128;
	public static final int circleRadiusSqr = circleRadius * circleRadius;
	public static final int centerX = 0;
	public static final int centerZ = 0;
	public static final int tilingSpacing = 8;
	public static final int tiling1Dimension = (int) Math.ceil(((double) circleRadius * 2) / tilingSpacing);
	public static final Fortress FORTRESS = new Fortress(VERSION);
	public static final RPos[] QUADS = {
			new RPos(-1, -1, FORTRESS.getSpacing()),
			new RPos(-1, 0, FORTRESS.getSpacing()),
			new RPos(0, -1, FORTRESS.getSpacing()),
			new RPos(0, 0, FORTRESS.getSpacing())
	};

	public static final BPos[] POSITIONS = new BPos[tiling1Dimension * tiling1Dimension];

	static {
		int idx = 0;
		for (int x = -circleRadius; x <= circleRadius; x += tilingSpacing) {
			for (int z = -circleRadius; z <= circleRadius; z += tilingSpacing) {
				if (DistanceMetric.EUCLIDEAN_SQ.getDistance(x - centerX, 0, z - centerZ) <= circleRadiusSqr) {
					POSITIONS[idx++] = new BPos(x, 0, z);
				}
			}
		}
	}

	public static void main(String[] args) {
		LongStream.range(0, 1L << 48).parallel().forEach(Main::kernel);
		System.out.println("Done, press any key to exit");
		Scanner scanner = new Scanner(System.in);
		scanner.nextLine();
	}


	public static void kernel(long structureSeed) {
		BiomeSource biomeSource = BiomeSource.of(Dimension.NETHER, VERSION, structureSeed);
		ChunkGenerator generator = ChunkGenerator.of(Dimension.NETHER, biomeSource);
		Pair<BPos[], Integer> fortressPos = closeFortresses(QUADS, structureSeed);
		if (fortressPos.getSecond() < 1) return;
		for (BPos pos : POSITIONS) {
			if (pos == null) continue;
			Block[] blocks = generator.getColumnAt(pos.getX(), pos.getZ());
			for (int y = generator.getSeaLevel(); y < Math.min(generator.getSeaLevel() + SUITABLE_OFFSET, generator.getMaxWorldHeight()); y++) {
				if (!OK_BLOCK.test(blocks[y])) return;
			}
		}
		System.out.println("----");
		System.out.printf("Seed %d works for radius %d and tiling %d in version %s%n", structureSeed, circleRadius, tilingSpacing, VERSION);
		for (BPos pos : fortressPos.getFirst()) {
			System.out.printf("Fortress can be found at /tp @p %d ~ %d%n", pos.getX(), pos.getZ());
		}
	}

	public static Pair<BPos[], Integer> closeFortresses(RPos[] quads, long structureSeed) {
		BPos[] positions = new BPos[quads.length];
		int idx = 0;
		ChunkRand rand = new ChunkRand();
		for (RPos quad : quads) {
			CPos cPos = FORTRESS.getInRegion(structureSeed, quad.getX(), quad.getZ(), rand);
			if (cPos != null) { //fortress share with bastion
				BPos bPos = cPos.toBlockPos();
				if (DistanceMetric.EUCLIDEAN_SQ.getDistance(bPos.getX() - centerX, 0, bPos.getZ() - centerZ) <= circleRadiusSqr) {
					positions[idx++] = bPos;
				}
			}
		}
		return new Pair<>(positions, idx);
	}
}
