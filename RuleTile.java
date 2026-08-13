import greenfoot.GreenfootImage;

public class RuleTile
{
    GreenfootImage cornerUL;
    GreenfootImage flatT;
    GreenfootImage cornerUR;
    GreenfootImage icLR;
    GreenfootImage icLL;
    GreenfootImage cornerULic;
    GreenfootImage cornerURic;
    GreenfootImage vertiStart;
    GreenfootImage flatL;
    GreenfootImage center;
    GreenfootImage flatR;
    GreenfootImage icUR;
    GreenfootImage icUL;
    GreenfootImage cornerLLic;
    GreenfootImage cornerLRic;
    GreenfootImage vertiMid;
    GreenfootImage icAll;
    GreenfootImage cornerLL;
    GreenfootImage flatB;
    GreenfootImage cornerLR;
    GreenfootImage single;
    GreenfootImage horiStart;
    GreenfootImage horiMid;
    GreenfootImage horiEnd;
    GreenfootImage vertiEnd;
    GreenfootImage icURLL;
    GreenfootImage icULLR;
    GreenfootImage flatLLR;
    GreenfootImage flatTLL;
    GreenfootImage flatTLR;
    GreenfootImage flatRLL;
    GreenfootImage flatTB;
    GreenfootImage flatRL;
    GreenfootImage icL;
    GreenfootImage icB;
    GreenfootImage icURLLLR;
    GreenfootImage icULLLLR;
    GreenfootImage flatBUR;
    GreenfootImage flatRUL;
    GreenfootImage flatLUR;
    GreenfootImage flatBUL;
    GreenfootImage flatLR;
    GreenfootImage flatBT;
    GreenfootImage icT;
    GreenfootImage icR;
    GreenfootImage icULURLR;
    GreenfootImage icULURLL;
    GreenfootImage error;

    public RuleTile(String tilesetName, int tileSize)
    {
        GreenfootImage sheet = new GreenfootImage("Game/Tiles/" + tilesetName + ".png");

        cornerUL = getTileSprite(sheet, 0, 0, tileSize);
        flatT = getTileSprite(sheet, 1, 0, tileSize);
        cornerUR = getTileSprite(sheet, 2, 0, tileSize);
        icLR = getTileSprite(sheet, 3, 0, tileSize);
        icLL = getTileSprite(sheet, 4, 0, tileSize);
        cornerULic = getTileSprite(sheet, 5, 0, tileSize);
        cornerURic = getTileSprite(sheet, 6, 0, tileSize);
        vertiStart = getTileSprite(sheet, 7, 0, tileSize);
        flatL = getTileSprite(sheet, 0, 1, tileSize);
        center = getTileSprite(sheet, 1, 1, tileSize);
        flatR = getTileSprite(sheet, 2, 1, tileSize);
        icUR = getTileSprite(sheet, 3, 1, tileSize);
        icUL = getTileSprite(sheet, 4, 1, tileSize);
        cornerLLic = getTileSprite(sheet, 5, 1, tileSize);
        cornerLRic = getTileSprite(sheet, 6, 1, tileSize);
        vertiMid = getTileSprite(sheet, 7, 1, tileSize);
        icAll = getTileSprite(sheet, 8, 1, tileSize);
        cornerLL = getTileSprite(sheet, 0, 2, tileSize);
        flatB = getTileSprite(sheet, 1, 2, tileSize);
        cornerLR = getTileSprite(sheet, 2, 2, tileSize);
        single = getTileSprite(sheet, 3, 2, tileSize);
        horiStart = getTileSprite(sheet, 4, 2, tileSize);
        horiMid = getTileSprite(sheet, 5, 2, tileSize);
        horiEnd = getTileSprite(sheet, 6, 2, tileSize);
        vertiEnd = getTileSprite(sheet, 7, 2, tileSize);
        icURLL = getTileSprite(sheet, 8, 2, tileSize);
        icULLR = getTileSprite(sheet, 9, 2, tileSize);
        flatLLR = getTileSprite(sheet, 0, 3, tileSize);
        flatTLL = getTileSprite(sheet, 1, 3, tileSize);
        flatTLR = getTileSprite(sheet, 2, 3, tileSize);
        flatRLL = getTileSprite(sheet, 3, 3, tileSize);
        flatTB = getTileSprite(sheet, 4, 3, tileSize);
        flatRL = getTileSprite(sheet, 5, 3, tileSize);
        icL = getTileSprite(sheet, 6, 3, tileSize);
        icB = getTileSprite(sheet, 7, 3, tileSize);
        icURLLLR = getTileSprite(sheet, 8, 3, tileSize);
        icULLLLR = getTileSprite(sheet, 9, 3, tileSize);
        flatBUR = getTileSprite(sheet, 0, 4, tileSize);
        flatRUL = getTileSprite(sheet, 1, 4, tileSize);
        flatLUR = getTileSprite(sheet, 2, 4, tileSize);
        flatBUL = getTileSprite(sheet, 3, 4, tileSize);
        flatLR = getTileSprite(sheet, 4, 4, tileSize);
        flatBT = getTileSprite(sheet, 5, 4, tileSize);
        icT = getTileSprite(sheet, 6, 4, tileSize);
        icR = getTileSprite(sheet, 7, 4, tileSize);
        icULURLR = getTileSprite(sheet, 8, 4, tileSize);
        icULURLL = getTileSprite(sheet, 9, 4, tileSize);
        error = getTileSprite(sheet, 9, 0, tileSize);
    }

    private GreenfootImage getTileSprite(GreenfootImage sheet, int tileX, int tileY, int tileSize)
    {
        GreenfootImage tile = new GreenfootImage(tileSize, tileSize);
        tile.drawImage(sheet, -tileX * tileSize, -tileY * tileSize);
        return tile; 
    }
}