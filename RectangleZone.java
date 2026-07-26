public class RectangleZone implements ZoneProvider
{
    private final int x, y, w, h;
    private final int type;

    public RectangleZone(int x, int y, int w, int h, int type)
    {
        this.x = x; this.y = y; this.w = w; this.h = h; this.type = type;
    }

    @Override
    public int getTerrainType(int worldX, int worldY)
    {
        if (worldX >= x && worldX < x + w && worldY >= y && worldY < y + h) return type;
        return -1;
    }
}