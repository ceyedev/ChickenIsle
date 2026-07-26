import java.util.ArrayList;
import java.util.List;

public class LayeredZoneProvider implements ZoneProvider
{
    private final List<ZoneProvider> layers = new ArrayList<>();
    private ZoneProvider maskLayer = null;
    private int maskType = -2;

    public void addLayer(ZoneProvider layer) {
        layers.add(layer);
    }

    public void setMaskLayer(ZoneProvider mask) {
        this.maskLayer = mask;
    }

    public void setMaskType(int type) {
        this.maskType = type;
    }

    @Override
    public int getTerrainType(int worldX, int worldY)
    {
        if (maskType != -2) {
            boolean insideMask = false;

            for (int i = layers.size() - 1; i >= 0; i--) {
                int t = layers.get(i).getTerrainType(worldX, worldY);
                if (t == maskType) {
                    insideMask = true;
                    break;
                }
            }
            if (!insideMask) {
                for (int i = layers.size() - 1; i >= 0; i--) {
                    int t = layers.get(i).getTerrainType(worldX, worldY);
                    if (t != -1) {
                        return (t == maskType) ? t : -1;
                    }
                }
                return -1;
            }
            for (int i = layers.size() - 1; i >= 0; i--) {
                int t = layers.get(i).getTerrainType(worldX, worldY);
                if (t != -1) return t;
            }
            return -1;
        }

        for (int i = layers.size() - 1; i >= 0; i--) {
            int t = layers.get(i).getTerrainType(worldX, worldY);
            if (t != -1) return t;
        }
        return -1;
    }
}