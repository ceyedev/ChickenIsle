import greenfoot.*;

public class MouseAction extends Actor
{
    private int targetX;
    private int targetY;
    private boolean hasTarget;
    private boolean isHoldingLeft;
    private boolean canInteract;
    private double speed = 0.5;
    private int tileSize;
    private int halfTileSize;
    private int worldSize;

    public MouseAction()
    {
        setImage("MouseLocation.png");
        getImage().scale(getImage().getWidth() * 2, getImage().getHeight() * 2);
        tileSize = GameManager.tileSize;
        halfTileSize = (int)(GameManager.tileSize / 2);
        worldSize = GameManager.worldSize;
    }

    public void act()
    {
        MouseInfo mouse = Greenfoot.getMouseInfo();

        if (!TimeManager.instance.isPaused && canInteract && mouse != null && Greenfoot.mousePressed(null) && mouse.getButton() == 1) {
            setImage("MouseLocationPressed.png");
            getImage().scale(getImage().getWidth() * 2, getImage().getHeight() * 2);
            isHoldingLeft = true;
        }

        GameWorld world = (GameWorld)getWorld();
        if (isHoldingLeft && mouse != null && Greenfoot.mouseClicked(null) && mouse.getButton() == 1) {
            setImage("MouseLocation.png");
            getImage().scale(getImage().getWidth() * 2, getImage().getHeight() * 2);
            isHoldingLeft = false;
            if(TimeManager.instance.isPaused) return;
            LocalObject object = world.currentChunk.currentTiles.get((int)((getX() - halfTileSize)/tileSize) * (worldSize/tileSize) + (int)((getY() - halfTileSize)/tileSize));
            world.localObjects.remove(object);
            world.removeObject(object);
        }

        if(isHoldingLeft) return;

        if(mouse != null) {
            world.showText("Mouse: (" + mouse.getX() + ", " + mouse.getY() + ")\nObj: (" + getX() + ", " + getY() + ")\nDel: (" + ((getX() - halfTileSize)/tileSize) + ", " + ((getY() - halfTileSize)/tileSize) + ")\n", 90, 100);

            int offsetX = -5*2;
            int offsetY = -6*2;
            targetX = Math.round((mouse.getX()) / tileSize) * tileSize + 26 + offsetX;
            targetY = Math.round((mouse.getY()) / tileSize) * tileSize + 28 + offsetY;
            hasTarget = true;
        }

        if(hasTarget) {
            double x = getX();
            double y = getY();
            x += (targetX - x) * speed;
            y += (targetY - y) * speed;

            if(Math.abs(targetX - x) < 1 && Math.abs(targetY - y) < 1) {
                x = targetX;
                y = targetY;
                canInteract = true;
            }
            else canInteract = false;

            setLocation((int)x, (int)y);
        }
    }
}