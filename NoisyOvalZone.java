public class NoisyOvalZone implements ZoneProvider
{
    private final int cx, cy;
    private final double minRadiusX, minRadiusY;
    private final double noiseScaleX, noiseScaleY;
    private final double maxExtraX, maxExtraY;
    private final NoiseGenerator noise;
    private final int type;
    
    private final double rotationDeg;
    private final double cos;
    private final double sin;

    public NoisyOvalZone(int cx, int cy, double minRadiusX, double minRadiusY, double noiseScaleX, double noiseScaleY, double maxExtraX, double maxExtraY, double rotationDeg, NoiseGenerator noise, int type)
    {
        this.cx = cx;
        this.cy = cy;
        this.minRadiusX = minRadiusX;
        this.minRadiusY = minRadiusY;
        this.noiseScaleX = noiseScaleX;
        this.noiseScaleY = noiseScaleY;
        this.maxExtraX = maxExtraX;
        this.maxExtraY = maxExtraY;
        this.rotationDeg = rotationDeg;
        double rotationRad = Math.toRadians(rotationDeg);
        this.cos = Math.cos(rotationRad);
        this.sin = Math.sin(rotationRad);
        this.noise = noise;
        this.type = type;
    }

    @Override
    public int getTerrainType(int worldX, int worldY)
    {
        double rawX = noise.noise(worldX * noiseScaleX, worldY * noiseScaleX, 4);
        double rawY = noise.noise(worldX * noiseScaleY, worldY * noiseScaleY, 4);
        double nx = (rawX + 1.0) * 0.5;
        double ny = (rawY + 1.0) * 0.5;
        double rx = minRadiusX + nx * maxExtraX;
        double ry = minRadiusY + ny * maxExtraY;
        if (rx <= 0 || ry <= 0) return -1;

        double dx = worldX - cx;
        double dy = worldY - cy;
        double localX = dx * cos + dy * sin;
        double localY = -dx * sin + dy * cos;
        double value = (localX * localX) / (rx * rx) + (localY * localY) / (ry * ry);
        return value <= 1.0 ? type : -1;
    }
}