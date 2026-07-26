public class CircleZone implements ZoneProvider
{
    private final int cx, cy, radius;
    private final int type;

    public CircleZone(int cx, int cy, int radius, int type)
    {
        this.cx = cx; this.cy = cy; this.radius = radius; this.type = type;
    }

    @Override
    public int getTerrainType(int worldX, int worldY)
    {
        int dx = worldX - cx, dy = worldY - cy;
        if (dx*dx + dy*dy <= radius*radius) return type;
        return -1;
    }
}