import greenfoot.*;

public class MapFront extends Actor
{
    public int tileSize = 16;
    public int tileCount = 5;
    public Vector2 startPos = new Vector2(640-56, 56);
    public boolean active = true;

    public MapFront()
    {
        setImage("MapFront.png");
    }
}