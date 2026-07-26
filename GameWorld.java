import greenfoot.*;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.util.Random;

public class GameWorld extends World
{
    public List<LocalObject> localObjects = new ArrayList<LocalObject>();
    private PlayerController playerController;
    private Vector2 currentPosition = new Vector2(0, 0);

    private long transitionStartTime;
    private int transitionDuration = 500;
    private boolean isTransitioning = false;
    private Vector2 transitionDirection = new Vector2(0, 0);

    private ArrayList<LocalObject> transitionObjects = new ArrayList<LocalObject>();
    private ArrayList<Vector2> transitionStartPositions = new ArrayList<Vector2>();
    private ArrayList<Vector2> transitionEndPositions = new ArrayList<Vector2>();

    public TerrainChunk currentChunk;
    private TerrainChunk oldChunk;

    public Map<String, MapTile> mapTiles = new HashMap<>();
    public WorldManager worldManager;

    public Vector2 mapCurrent = new Vector2(0,0);
    public MapFront map;
    private MapBack mapBack;

    public GameWorld(int currentSelectedPlayer, int currentSelectedWorld)
    {
        super(640, 640, 1, false);
        GameManager.worldSize = 640;

        Greenfoot.setSpeed(50);
        
        setPaintOrder(
            Button.class,
            MapFront.class,
            MapTile.class,
            MapBack.class,
            PlayerController.class,
            MouseAction.class,
            TerrainChunk.class
        );

        addObject(new MouseAction(), GameManager.worldSize / 2, GameManager.worldSize / 2);
        new TimeManager();
        map = new MapFront();
        mapBack = new MapBack();
        mapBack.additionalOffset = new Vector2(GameManager.worldSize - 16, GameManager.worldSize - 16);
        mapCurrent = new Vector2(map.startPos.x, map.startPos.y);
        addObject(mapBack, (int)map.startPos.x, (int)map.startPos.y);
        addObject(map, (int)map.startPos.x, (int)map.startPos.y);
        localObjects.add(mapBack);

        worldManager = new WorldManager(new Random().nextInt(), currentSelectedWorld, 1);
        generateTile(new Vector2(0, 0));

        playerController = new PlayerController(this, currentSelectedPlayer);
        addObject(playerController, (int)(GameManager.worldSize / 2), (int)(GameManager.worldSize / 2));
        localObjects.add(playerController);
    }


    public void act()
    {
        String key = Greenfoot.getKey();
        if (key != null && key.equals("m")) {
            map.active = !map.active;
            int a = map.active ? 255 : 0;
            map.getImage().setTransparency(a);
            mapBack.getImage().setTransparency(a);
            for (MapTile tile : mapTiles.values()) tile.getImage().setTransparency(a);
        }

        handleTransition();
    }

    private void handleTransition()
    {
        if (!isTransitioning) return;

        float progress = Math.min(1f, (System.currentTimeMillis() - transitionStartTime) / (float)transitionDuration);

        for (int i = 0; i < transitionObjects.size(); i++) {
            LocalObject obj = transitionObjects.get(i);
            Vector2 start = transitionStartPositions.get(i);
            Vector2 end = transitionEndPositions.get(i);

            obj.setLocation((int)(start.x + (end.x - start.x) * progress), (int)(start.y + (end.y - start.y) * progress));
        }

        if (progress >= 1f) {
            for (int i = 0; i < transitionObjects.size(); i++) {
                LocalObject obj = transitionObjects.get(i);
                Vector2 end = transitionEndPositions.get(i);
                obj.setLocation((int)end.x, (int)end.y);
            }

            isTransitioning = false;
            for (LocalObject obj : localObjects) if(obj.tpBack) obj.setLocation((int)obj.tpBackPos.x, (int)obj.tpBackPos.y);

            oldChunk.DeleteTiles(this);
            TimeManager.instance.setPaused(false);
        }
    }

    public void startTransition(Vector2 direction)
    {
        if(direction.x > 0 || direction.y < 0) mapBack.setLocation((int)(mapCurrent.x), (int)(mapCurrent.y));
        else if(direction.x < 0 || direction.y > 0) mapBack.setLocation((int)(mapCurrent.x - 16*direction.x), (int)(mapCurrent.y - 16*direction.y));

        currentPosition.x += direction.x;
        currentPosition.y += direction.y;
        showText("D: (" + direction.x + ", " + direction.y + ")\nC: (" + currentPosition.x + ", " + currentPosition.y + ")\nT: (" + transitionObjects.size() + ")\n", 90, 100);

        direction = new Vector2(-direction.x, direction.y);
        generateTile(direction);

        TimeManager.instance.setPaused(true);
        transitionObjects.clear();
        transitionStartPositions.clear();
        transitionEndPositions.clear();

        for (LocalObject obj : localObjects) {
            transitionObjects.add(obj);
            transitionStartPositions.add(new Vector2(obj.getX(), obj.getY()));
            Vector2 offset = new Vector2(0,0);
            if (direction.x != 0) offset.x = obj.additionalOffset.x * direction.x;
            if (direction.y != 0) offset.y = obj.additionalOffset.y * -direction.y;
            transitionEndPositions.add(new Vector2(obj.getX() + -direction.x * GameManager.worldSize + offset.x, obj.getY() + direction.y * GameManager.worldSize + offset.y));
        }

        transitionStartTime = System.currentTimeMillis();
        transitionDirection = direction;
        isTransitioning = true;
    }

    private void generateTile(Vector2 dir)
    {
        oldChunk = currentChunk;
        currentChunk = new TerrainChunk(currentPosition, dir, this, worldManager.ruleTiles);
    }
}