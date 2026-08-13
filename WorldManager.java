import greenfoot.*;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;

public class WorldManager  
{
    public Map<Integer, Map.Entry<RuleTile, Color>> ruleTiles = new HashMap<>();

    public WorldManager(double seed, int worldIndex, int saveIndex)
    {
        TerrainGenerator gen = new TerrainGenerator(seed);
        NoiseGenerator noise = gen.getNoiseGenerator();
        LayeredZoneProvider stack = new LayeredZoneProvider();

        switch (worldIndex) {
            case 1:
            {
                NoisyOvalZone base1 = new NoisyOvalZone(-5,-25, 60,30, 0.05,0.05, 20,20, 0, noise, 0);
                NoisyCircleZone base2 = new NoisyCircleZone(-35,20, 22, 0.05,20, noise, 0);
                NoisyCircleZone base3 = new NoisyCircleZone(35,10, 8, 0.05, 20, noise, 0);
                NoisyCircleZone base4 = new NoisyCircleZone(45,45, 5, 0.05, 10, noise, 0);
                NoisyCircleZone base5 = new NoisyCircleZone(38,52, 2, 0.05, 10, noise, 0);
                NoisyOvalZone redForest = new NoisyOvalZone(15,-5, 35,12, 0.05,0.05, 20,40, 0, noise, 2);
                NoisyCircleZone beach = new NoisyCircleZone(45,35, 20, 0.15, 20, noise, 3);
                NoisyCircleZone beach2 = new NoisyCircleZone(-45,65, 20, 0.15, 20, noise, 3);
                NoisyCircleZone beach3 = new NoisyCircleZone(-8,50, 10, 0.15, 20, noise, 3);
                NoisyOvalZone iceLand = new NoisyOvalZone(0,-60, 60,25, 0.1,0.1, 10,20, 0, noise, 1);
                NoisyCircleZone iceLand2 = new NoisyCircleZone(50,-30, 20, 0.15, 15, noise, 1);
                NoisyCircleZone redForest2 = new NoisyCircleZone(-70,-20, 8, 0.15, 15, noise, 2);
                NoisyCircleZone pink1 = new NoisyCircleZone(45,45, 3, 0.05, 10, noise, 4);
                NoisyCircleZone pink2 = new NoisyCircleZone(38,52, 0, 0.05, 10, noise, 4);

                stack.addLayer(base1);
                stack.addLayer(base2);
                stack.addLayer(base3);
                stack.addLayer(base4);
                stack.addLayer(base5);
                stack.addLayer(redForest);
                stack.addLayer(beach);
                stack.addLayer(beach2);
                stack.addLayer(beach3);
                stack.addLayer(iceLand);
                stack.addLayer(iceLand2);
                stack.addLayer(redForest2);
                stack.addLayer(pink1);
                stack.addLayer(pink2);
            
                stack.setMaskType(0);
                gen.setZoneProvider(stack);
                
                ruleTiles.put(-1, Map.entry(new RuleTile("water/tilemap", 16), Color.BLUE));
                ruleTiles.put(0, Map.entry(new RuleTile("green-grass/tilemap", 16), Color.GREEN));
                ruleTiles.put(1, Map.entry(new RuleTile("snow/tilemap", 16), Color.WHITE));
                ruleTiles.put(2, Map.entry(new RuleTile("orange-grass/tilemap", 16), Color.RED));
                ruleTiles.put(3, Map.entry(new RuleTile("sand/tilemap", 16), Color.YELLOW));
                ruleTiles.put(4, Map.entry(new RuleTile("weird/tilemap", 16), Color.PINK));
                ruleTiles.put(10, Map.entry(new RuleTile("water/tilemap", 16), Color.BLUE));

                break;
            }

            case 2:
            {
                NoisyOvalZone base1 = new NoisyOvalZone(-5,-25, 60,30, 0.05,0.05, 20,20, 0, noise, 0);

                stack.addLayer(base1);
            
                stack.setMaskType(0);
                gen.setZoneProvider(stack);
                
                ruleTiles.put(-1, Map.entry(new RuleTile("water/tilemap", 16), Color.BLUE));
                ruleTiles.put(0, Map.entry(new RuleTile("green-grass/tilemap", 16), Color.GREEN));

                break;
            }
        }
    }
}