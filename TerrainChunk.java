import greenfoot.*;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;

public class TerrainChunk
{
    ArrayList<Tile> currentTiles = new ArrayList<Tile>();

    public TerrainChunk(Vector2 offset, Vector2 dir, GameWorld world, Map<Integer, Map.Entry<RuleTile, Color>> ruleTiles)
    {
        int tileSize = GameManager.tileSize;
        int worldSize = GameManager.worldSize;

        int[][] zones = TerrainGenerator.instance.generate((worldSize/tileSize), (worldSize/tileSize), (int)((-(offset.x)*worldSize - worldSize/2)/tileSize), (int)((-(offset.y)*worldSize- worldSize/2)/tileSize), true);

        float r = 0, g = 0, b = 0, c = 0;

        for(int x = 1; x < zones.length -1; x++) {
            for(int y = 1; y < zones[x].length -1; y++) {
                //if(zones[x][y] == -1) continue;
                Color color = ruleTiles.get(zones[x][y]).getValue();
                c++;
                r += color.getRed();
                g += color.getGreen();
                b += color.getBlue();

                Tile tile = new Tile(zones, new Vector2(x, y), tileSize, ruleTiles);
                currentTiles.add(tile);
                world.localObjects.add(tile);
                world.addObject(tile, (int)dir.x * worldSize + x*tileSize - tileSize/2, -(int)dir.y * worldSize + y*tileSize - tileSize/2);
            }
        }

        if(world.mapTiles.get(offset.x + "," + offset.y) == null) {
            r /= c;
            g /= c;
            b /= c;

            MapTile newTile = new MapTile(new Color((int)r, (int)g, (int)b), world.map);
            Vector2 pos = new Vector2(world.mapCurrent.x + dir.x*16, world.mapCurrent.y - dir.y*16);
            world.addObject(newTile, (int)pos.x, (int)pos.y);
            newTile.additionalOffset = new Vector2(worldSize - 16, worldSize - 16);
            world.localObjects.add(newTile);
            world.mapTiles.put((offset.x + "," + offset.y), newTile);
        }
        else System.out.println("exists");
    }

    public void DeleteTiles(GameWorld world)
    {
        for(int i = 0; i < currentTiles.size(); i++) {
            Tile tile = currentTiles.get(i);
            world.localObjects.remove(tile);
            world.removeObject(tile);
        }
        currentTiles.clear();
    }
}