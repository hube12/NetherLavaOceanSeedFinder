import kaptainwutax.biomeutils.source.BiomeSource;
import kaptainwutax.mcutils.block.Block;
import kaptainwutax.mcutils.state.Dimension;
import kaptainwutax.mcutils.version.MCVersion;
import kaptainwutax.terrainutils.ChunkGenerator;

public class LavaColumn {
	public static void main(String[] args) {
		BiomeSource biomeSource=BiomeSource.of(Dimension.NETHER, MCVersion.v1_16_5,1);
		ChunkGenerator generator=ChunkGenerator.of(Dimension.NETHER,biomeSource);
		Block[] blocks=generator.getColumnAt(-19,46);
		for (int y = generator.getMaxWorldHeight()-1; y >=generator.getMinWorldHeight(); y--) {
			System.out.println(y+" "+blocks[y].getName());
		}
	}
}
