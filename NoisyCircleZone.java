public class NoisyCircleZone implements ZoneProvider
{
    private final int cx, cy;
    private final double minRadius;
    private final double noiseScale, maxExtra;
    private final NoiseGenerator noise;
    private final int type;

    public NoisyCircleZone(int cx, int cy, double minRadius, double noiseScale, double maxExtra, NoiseGenerator noise, int type)
    {
        this.cx = cx;
        this.cy = cy;
        this.minRadius = minRadius;
        this.noiseScale = noiseScale;
        this.maxExtra = maxExtra;
        this.noise = noise;
        this.type = type;
    }

    @Override
    public int getTerrainType(int worldX, int worldY)
    {
        double dx = worldX - cx;
        double dy = worldY - cy;
        double dist = Math.sqrt(dx * dx + dy * dy);
        double raw = noise.noise(worldX * noiseScale, worldY * noiseScale, 4);
        double effectiveRadius = minRadius + ((raw + 1.0) / 2.0) * maxExtra;
        if (dist <= effectiveRadius) return type;
        return -1;
    }
}