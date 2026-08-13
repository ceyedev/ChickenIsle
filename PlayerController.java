import greenfoot.*;

public class PlayerController extends LocalObject
{
    public enum Direction {
        bottom, left, right, top
    }

    public enum State {
        idle, walk,
    }

    public int playerIndex;
    private GameWorld world;

    private int tileSize;
    private int worldSize;
    private Vector2 offset;

    private float snapStrength = 0.12f;
    private boolean isSnapping = false;
    private Vector2 snapTarget = null;
    private int baseImageWidth, baseImageHeight;
    private float exactX, exactY;
    private boolean positionInitialized = false;

    private Direction currentDir = Direction.bottom;
    private State currentState = State.idle;
    private State previousState = State.idle;

    private float speed = 5;
    private float smoothTime = 3;
    private Vector2 velocity = new Vector2();
    private Vector2 lastInput = new Vector2();
    private float currentSpeed;

    private int fps = 6;
    private int currentFrame = 1;
    private int maxFrame = 4;
    private long lastFrameTime = 0;
    private long frameDuration = 1000 / fps;


    public PlayerController(GameWorld world, int currentSelectedPlayer)
    {
        this.world = world;
        playerIndex = currentSelectedPlayer;
        tileSize = GameManager.tileSize;
        worldSize = GameManager.worldSize;
        offset = new Vector2(tileSize / 2.0f, tileSize / 2.0f);
        selectPlayer(1);
        setImage(playerIndex, State.idle, Direction.bottom, 1);
    }

    @Override
    public void addedToWorld(World world)
    {
        super.addedToWorld(world);
        if (baseImageWidth <= 0 && getImage() != null) {
            baseImageWidth = getImage().getWidth();
            baseImageHeight = getImage().getHeight();
        }
        exactX = (worldSize / (tileSize * 2)) * tileSize + offset.x;
        exactY = (worldSize / (tileSize * 2)) * tileSize + offset.y;
        positionInitialized = true;
        applyPosition();
    }

    public void selectPlayer(int index)
    {
        playerIndex = index;
    }

    public void act()
    {
        if (TimeManager.instance.isPaused) return;
        if (!positionInitialized) return;

        processInput();

        long currentTime = System.currentTimeMillis();
        if (currentTime - lastFrameTime >= frameDuration) {
            currentFrame++;
            if (currentFrame > maxFrame) currentFrame = 1;
            lastFrameTime = currentTime;
        }

        setImage(playerIndex, currentState, currentDir, currentFrame);
    }

    private void processInput()
    {
        previousState = currentState;

        int rawX = 0, rawY = 0;
        if (Greenfoot.isKeyDown("w")) rawY--;
        if (Greenfoot.isKeyDown("s")) rawY++;
        if (Greenfoot.isKeyDown("a")) rawX--;
        if (Greenfoot.isKeyDown("d")) rawX++;

        boolean hasInput = (rawX != 0 || rawY != 0);

        if (hasInput) {
            isSnapping = false;
            snapTarget = null;
        } else if (!hasInput && previousState == State.walk) {
            isSnapping = true;
            snapTarget = getTileCenter(exactX, exactY);
        }

        if (hasInput) {
            if (rawX < 0) currentDir = Direction.left;
            else if (rawX > 0) currentDir = Direction.right;
            else if (rawY < 0) currentDir = Direction.top;
            else if (rawY > 0) currentDir = Direction.bottom;

            currentState = State.walk;
            currentSpeed += speed / smoothTime;

            if (currentSpeed > speed) currentSpeed = speed;
            lastInput = new Vector2(rawX, rawY);
        } else {
            currentState = State.idle;
            currentSpeed = 0;
            lastInput = new Vector2(0, 0);
        }

        if (velocity.x != rawX || (velocity.y != rawY && rawX == 0)) {
            velocity = new Vector2(rawX, rawY);
            currentFrame = 1;
            lastFrameTime = System.currentTimeMillis();
        }

        if (hasInput && currentSpeed > 0) {
            float length = (float)Math.sqrt(lastInput.x * lastInput.x + lastInput.y * lastInput.y);
            float normX = lastInput.x / length;
            float normY = lastInput.y / length;
            exactX += normX * currentSpeed;
            exactY += normY * currentSpeed;
        }

        if (isSnapping && snapTarget != null) {
            exactX = exactX + (snapTarget.x - exactX) * snapStrength;
            exactY = exactY + (snapTarget.y - exactY) * snapStrength;
            if (Math.abs(exactX - snapTarget.x) < 0.001f) exactX = snapTarget.x;
            if (Math.abs(exactY - snapTarget.y) < 0.001f) exactY = snapTarget.y;
            if (exactX == snapTarget.x && exactY == snapTarget.y) {
                isSnapping = false;
                snapTarget = null;
            }
        }

        applyPosition();

        int tileCol = Math.round((exactX - offset.x) / tileSize);
        int tileRow = Math.round((exactY - offset.y) / tileSize);

        if (getY() <= 0 || getY() >= worldSize - 1 || getX() <= 0 || getX() >= worldSize - 1) {
            exactY = clamp(exactY, tileSize, worldSize + tileSize - 1);
            exactX = clamp(exactX, tileSize, worldSize + tileSize - 1);

            if (getY() <= 0 || getY() >= worldSize - 1) {
                world.startTransition(new Vector2(0, getY() <= 0 ? 1 : -1));
                exactY = tileSize + (getY() <= 0 ? (- 2 + worldSize) : 1);
            } else {
                world.startTransition(new Vector2(getX() <= 0 ? 1 : -1, 0));
                exactX = tileSize + (getX() <= 0 ? (- 2 + worldSize) : 1);
            }
        }
    }

    private Vector2 getTileCenter(float x, float y)
    {
        int tileX = Math.round((x - offset.x) / tileSize);
        int tileY = Math.round((y - offset.y) / tileSize);
        return new Vector2(tileX * tileSize + offset.x, tileY * tileSize + offset.y);
    }

    private void applyPosition()
    {
        int w = baseImageWidth > 0 ? baseImageWidth : (getImage() != null ? getImage().getWidth() : 0);
        int h = baseImageHeight > 0 ? baseImageHeight : (getImage() != null ? getImage().getHeight() : 0);
        int drawX = Math.round(exactX) - w / 2;
        int drawY = Math.round(exactY) - h / 2;
        setLocation(drawX, drawY);
    }

    private float clamp(float value, float min, float max)
    {
        if (value < min) return min;
        if (value > max) return max;
        return value;
    }

    public void setImage(int index, State state, Direction direction, int frame)
    {
        String imagePath = "Game/Player/" + index + "/" + state.toString().charAt(0) + direction.toString().charAt(0) + frame + ".png";
        setImage(imagePath);

        GreenfootImage img = getImage();
        if (img != null) {
            img.scale(img.getWidth() * 2, img.getHeight() * 2);
            baseImageWidth = img.getWidth();
            baseImageHeight = img.getHeight();
        }

        if (positionInitialized) {
            applyPosition();
        }
    }
}