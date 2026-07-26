import greenfoot.*;
import java.util.Random;

public class TerrainGenerator
{
    public static TerrainGenerator instance;

    private NoiseGenerator noiseGen;
    private double seed;
    private ZoneProvider customZoneProvider = null;

    public TerrainGenerator(double seed)
    {
        instance = this;
        this.seed = seed;
        this.noiseGen = new NoiseGenerator(seed);
    }

    public TerrainGenerator()
    {
        this(new Random().nextInt());
        instance = this;
    }

    public NoiseGenerator getNoiseGenerator() {
        return noiseGen;
    }

    public void setZoneProvider(ZoneProvider provider) {
        this.customZoneProvider = provider;
    }

    public int[][] generate(int width, int height, int offsetX, int offsetY, boolean addBorder)
    {
        if(customZoneProvider == null) {
            System.err.println("Warning: No customZoneProvider set");
            return new int[0][0];
        }

        int zoneWidth  = addBorder ? width + 2  : width;
        int zoneHeight = addBorder ? height + 2 : height;
        int zoneOffX   = addBorder ? offsetX - 1 : offsetX;
        int zoneOffY   = addBorder ? offsetY - 1 : offsetY;

        int[][] zones = new int[zoneWidth][zoneHeight];

        for (int x = 0; x < zoneWidth; x++) {
            for (int y = 0; y < zoneHeight; y++) {
                int worldX = zoneOffX + x;
                int worldY = zoneOffY + y;

                int type;
                if (customZoneProvider != null) type = customZoneProvider.getTerrainType(worldX, worldY);
                else type = -1;

                zones[x][y] = type;

                double terrainNoise = noiseGen.noise(worldX * 0.03, worldY * 0.03, 6);
                double brightness = 0.8 + terrainNoise * 0.2;
            }
        }

        boolean[][] outline = new boolean[zoneWidth][zoneHeight];
        if (addBorder) outline = createOutline(zones, zoneWidth, zoneHeight);

        return zones;
    }

    private boolean[][] createOutline(int[][] zones, int width, int height)
    {
        boolean[][] outline = new boolean[width][height];

        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                int z = zones[x][y];
                if (z == -1) continue;

                if (x < width - 1) {
                    int zr = zones[x+1][y];
                    if (zr != -1 && z != zr) {
                        if (z < zr) outline[x][y] = true;
                        else outline[x+1][y] = true;
                    }
                }

                if (y < height - 1) {
                    int zd = zones[x][y+1];
                    if (zd != -1 && z != zd) {
                        if (z < zd) outline[x][y] = true;
                        else outline[x][y+1] = true;
                    }
                }

                if (x > 0 && zones[x-1][y] == -1) outline[x][y] = true;
                if (x < width-1 && zones[x+1][y] == -1) outline[x][y] = true;
                if (y > 0 && zones[x][y-1] == -1) outline[x][y] = true;
                if (y < height-1 && zones[x][y+1] == -1) outline[x][y] = true;
            }
        }

        enforceTwoNeighbors(outline, width, height);
        return outline;
    }

    private void enforceTwoNeighbors(boolean[][] outline, int width, int height)
    {
        boolean changed;
        do
        {
            changed = false;
            boolean[][] newOutline = new boolean[width][height];
            for (int x = 0; x < width; x++) System.arraycopy(outline[x], 0, newOutline[x], 0, height);

            for (int x = 0; x < width; x++) {
                for (int y = 0; y < height; y++) {
                    if (!outline[x][y]) continue;

                    int count = 0;
                    if (x > 0 && outline[x-1][y]) count++;
                    if (x < width-1 && outline[x+1][y]) count++;
                    if (y > 0 && outline[x][y-1]) count++;
                    if (y < height-1 && outline[x][y+1]) count++;

                    if (count != 2) {
                        newOutline[x][y] = false;
                        changed = true;
                    }
                }
            }

            for (int x = 0; x < width; x++) System.arraycopy(newOutline[x], 0, outline[x], 0, height);
        } while (changed);
    }
}