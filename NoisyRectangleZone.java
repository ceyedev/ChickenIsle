public class NoisyRectangleZone implements ZoneProvider
{
    private final int baseX, baseY, baseW, baseH;
    private final double noiseScale, maxExtra;
    private final NoiseGenerator noise;
    private final int type;

    public NoisyRectangleZone(int baseX, int baseY, int baseW, int baseH, double noiseScale, double maxExtra, NoiseGenerator noise, int type)
    {
        this.baseX = baseX;
        this.baseY = baseY;
        this.baseW = baseW;
        this.baseH = baseH;
        this.noiseScale = noiseScale;
        this.maxExtra = maxExtra;
        this.noise = noise;
        this.type = type;
    }

    @Override
    public int getTerrainType(int worldX, int worldY)
    {
        double left = baseX - getExtra(worldY, 100);
        double right = baseX + baseW + getExtra(worldY, 200);
        double top = baseY - getExtra(worldX, 300);
        double bottom = baseY + baseH + getExtra(worldX, 400);

        if (worldX >= left && worldX <= right && worldY >= top && worldY <= bottom) return type;
        return -1;
    }

    private double getExtra(int coord, int seedOffset) {
        double raw = noise.noise(seedOffset, coord * noiseScale, 3);
        return ((raw + 1.0) / 2.0) * maxExtra;
    }
}