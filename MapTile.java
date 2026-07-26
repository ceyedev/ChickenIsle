import greenfoot.*;

public class MapTile extends LocalObject
{
    private int alpha = 0;
    private boolean spawning = true;
    private MapFront map;

    public MapTile(Color color, MapFront map)
    {
        this.map = map;
        GreenfootImage image = new GreenfootImage(map.tileSize, map.tileSize);
        image.setColor(color);
        image.fill();
        image.setTransparency(0);
        if(!map.active) spawning = false;
        setImage(image);
    }

    public void act()
    {
        if(!map.active) return;
        int minX = (int)map.startPos.x - 3 * map.tileSize;
        int minY = (int)map.startPos.y - 3 * map.tileSize;
        int maxX = minX + map.tileSize * (map.tileCount + 1);
        int maxY = minY + map.tileSize * (map.tileCount + 1);
        boolean active = getX() > minX && getX() < maxX && getY() > minY && getY() < maxY;

        if (spawning) {
            alpha += 5;

            if (alpha >= 255) {
                alpha = 255;
                spawning = false;
            }

            getImage().setTransparency(alpha);
        } else getImage().setTransparency(active ? 255 : 0);
    }
}