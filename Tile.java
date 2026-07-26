import greenfoot.*;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;

public class Tile extends LocalObject
{
    private int[][] neighbours = new int[3][3]; // von 0|0 bis 2|2, 1|1 ist diese tile

    private enum TestType {
        theSame,
        notTheSame
    }

    public Tile(int[][] zones, Vector2 position, int tileSize, Map<Integer, Map.Entry<RuleTile, Color>> ruleTiles)
    {
        for(int x = 0; x < 3; x++) {
            for(int y = 0; y < 3; y++) {
                neighbours[x][y] = zones[(int)position.x + x - 1][(int)position.y + y - 1];

            }
        }

        ArrayList<TestType> testArr = new ArrayList<TestType>();

        for(int y = 0; y < 3; y++) {
            for(int x = 0; x < 3; x++) {
                testArr.add(getTileRelation(neighbours[1][1], neighbours[x][y]));
            }
        }

        setImage(getSprite(testArr, ruleTiles.get(neighbours[1][1]).getKey()));
        getImage().scale(tileSize, tileSize);
    }


    private TestType getTileRelation(int thisTile, int otherTile)
    {
        if(thisTile == otherTile) return TestType.theSame;
        else return TestType.notTheSame;
    }


    private GreenfootImage getSprite(ArrayList<TestType> t, RuleTile ruleTile)
    {
        TestType c = t.get(5);

        if(o(t, 1) && o(t, 3) && s(t, 5) && s(t, 7) && s(t, 8)) return ruleTile.cornerUL;
        if(o(t, 1) && s(t, 3) && s(t, 5) && s(t, 6) && s(t, 7) && s(t, 8)) return ruleTile.flatT;
        if(o(t, 1) && s(t, 3) && o(t, 5) && s(t, 6) && s(t, 7)) return ruleTile.cornerUR;
        if(s(t, 0) && s(t, 1) && s(t, 2) && s(t, 3) && s(t, 5) && s(t, 6) && s(t, 7) && o(t, 8)) return ruleTile.icLR;
        if(s(t, 0) && s(t, 1) && s(t, 2) && s(t, 3) && s(t, 5) && o(t, 6) && s(t, 7) && s(t, 8)) return ruleTile.icLL;
        if(o(t, 1) && o(t, 3) && s(t, 5) && s(t, 7) && o(t, 8)) return ruleTile.cornerULic;
        if(o(t, 1) && s(t, 3) && o(t, 5) && o(t, 6) && s(t, 7)) return ruleTile.cornerURic;
        if(o(t, 1) && o(t, 3) && o(t, 5) && s(t, 7)) return ruleTile.vertiStart;
        if(s(t, 1) && s(t, 2) && o(t, 3) && s(t, 5) && s(t, 7) && s(t, 8)) return ruleTile.flatL;
        if(s(t, 0) && s(t, 1) && s(t, 2) && s(t, 3) && s(t, 5) && s(t, 6) && s(t, 7) && s(t, 8)) return ruleTile.center;
        if(s(t, 0) && s(t, 1) && s(t, 3) && o(t, 5) && s(t, 6) && s(t, 7)) return ruleTile.flatR;
        if(s(t, 0) && s(t, 1) && o(t, 2) && s(t, 3) && s(t, 5) && s(t, 6) && s(t, 7) && s(t, 8)) return ruleTile.icUR;
        if(o(t, 0) && s(t, 1) && s(t, 2) && s(t, 3) && s(t, 5) && s(t, 6) && s(t, 7) && s(t, 8)) return ruleTile.icUL;
        if(s(t, 1) && o(t, 2) && o(t, 3) && s(t, 5) && o(t, 7)) return ruleTile.cornerLLic;
        if(o(t, 0) && s(t, 1) && s(t, 3) && o(t, 5) && o(t, 7)) return ruleTile.cornerLRic;
        if(s(t, 1) && o(t, 3) && o(t, 5) && s(t, 7)) return ruleTile.vertiMid;
        if(o(t, 0) && s(t, 1) && o(t, 2) && s(t, 3) && s(t, 5) && o(t, 6) && s(t, 7) && o(t, 8)) return ruleTile.icAll;
        if(s(t, 1) && s(t, 2) && o(t, 3) && s(t, 5) && o(t, 7)) return ruleTile.cornerLL;
        if(s(t, 0) && s(t, 1) && s(t, 2) && s(t, 3) && s(t, 5) && o(t, 7)) return ruleTile.flatB;
        if(s(t, 0) && s(t, 1) && s(t, 3) && o(t, 5) && o(t, 7)) return ruleTile.cornerLR;
        if(o(t, 1) && o(t, 3) && o(t, 5) && o(t, 7)) return ruleTile.single;
        if(o(t, 1) && o(t, 3) && s(t, 5) && o(t, 7)) return ruleTile.horiStart;
        if(o(t, 1) && s(t, 3) && s(t, 5) && o(t, 7)) return ruleTile.horiMid;
        if(o(t, 1) && s(t, 3) && o(t, 5) && o(t, 7)) return ruleTile.horiEnd;
        if(s(t, 1) && o(t, 3) && o(t, 5) && o(t, 7)) return ruleTile.vertiEnd;
        if(s(t, 0) && s(t, 1) && o(t, 2) && s(t, 3) && s(t, 5) && o(t, 6) && s(t, 7) && s(t, 8)) return ruleTile.icURLL;
        if(o(t, 0) && s(t, 1) && s(t, 2) && s(t, 3) && s(t, 5) && s(t, 6) && s(t, 7) && o(t, 8)) return ruleTile.icULLR;
        if(s(t, 1) && s(t, 2) && o(t, 3) && s(t, 5) && s(t, 7) && o(t, 8)) return ruleTile.flatLLR;
        if(o(t, 1) && s(t, 3) && s(t, 5) && o(t, 6) && s(t, 7) && s(t, 8)) return ruleTile.flatTLL;
        if(o(t, 1) && s(t, 3) && s(t, 5) && s(t, 6) && s(t, 7) && o(t, 8)) return ruleTile.flatTLR;
        if(s(t, 0) && s(t, 1) && s(t, 3) && o(t, 5) && o(t, 6) && s(t, 7)) return ruleTile.flatRLL;
        if(o(t, 1) && s(t, 3) && s(t, 5) && o(t, 6) && s(t, 7) && o(t, 8)) return ruleTile.flatTB;
        if(o(t, 0) && s(t, 1) && s(t, 3) && o(t, 5) && o(t, 6) && s(t, 7)) return ruleTile.flatRL;
        if(s(t, 0) && s(t, 1) && o(t, 2) && s(t, 3) && s(t, 5) && s(t, 6) && s(t, 7) && o(t, 8)) return ruleTile.icL;
        if(s(t, 0) && s(t, 1) && s(t, 2) && s(t, 3) && s(t, 5) && o(t, 6) && s(t, 7) && o(t, 8)) return ruleTile.icB;
        if(s(t, 0) && s(t, 1) && o(t, 2) && s(t, 3) && s(t, 5) && o(t, 6) && s(t, 7) && o(t, 8)) return ruleTile.icURLLLR;
        if(o(t, 0) && s(t, 1) && s(t, 2) && s(t, 3) && s(t, 5) && o(t, 6) && s(t, 7) && o(t, 8)) return ruleTile.icULLLLR;
        if(s(t, 0) && s(t, 1) && o(t, 2) && s(t, 3) && s(t, 5) && o(t, 7)) return ruleTile.flatBUR;
        if(o(t, 0) && s(t, 1) && s(t, 3) && o(t, 5) && s(t, 6) && s(t, 7)) return ruleTile.flatRUL;
        if(s(t, 1) && o(t, 2) && o(t, 3) && s(t, 5) && s(t, 7) && s(t, 8)) return ruleTile.flatLUR;
        if(o(t, 0) && s(t, 1) && s(t, 2) && s(t, 3) && s(t, 5) && o(t, 7)) return ruleTile.flatBUL;
        if(s(t, 1) && o(t, 2) && o(t, 3) && s(t, 5) && s(t, 7) && o(t, 8)) return ruleTile.flatLR;
        if(o(t, 0) && s(t, 1) && o(t, 2) && s(t, 3) && s(t, 5) && o(t, 7)) return ruleTile.flatBT;
        if(o(t, 0) && s(t, 1) && o(t, 2) && s(t, 3) && s(t, 5) && s(t, 6) && s(t, 7) && s(t, 8)) return ruleTile.icT;
        if(o(t, 0) && s(t, 1) && s(t, 2) && s(t, 3) && s(t, 5) && o(t, 6) && s(t, 7) && s(t, 8)) return ruleTile.icR;
        if(o(t, 0) && s(t, 1) && o(t, 2) && s(t, 3) && s(t, 5) && s(t, 6) && s(t, 7) && o(t, 8)) return ruleTile.icULURLR;
        if(o(t, 0) && s(t, 1) && o(t, 2) && s(t, 3) && s(t, 5) && o(t, 6) && s(t, 7) && s(t, 8)) return ruleTile.icULURLL;

        else return ruleTile.error;
    }

    private boolean s(ArrayList<TestType> t, int compIndex) {return t.get(4) == t.get(compIndex);}
    private boolean o(ArrayList<TestType> t, int compIndex) {return t.get(4) != t.get(compIndex);}
}
