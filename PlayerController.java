import greenfoot.*;

public class PlayerController extends LocalObject
{
    GameWorld world;
    public int playerIndex;

    private int minBounds = 1;
    private int maxBounds;
    private int tileSize;
    private Vector2 offset;
    private float speed = 5;
    private float smoothTimeStart = 6;
    private float walkSnapStrength = 0.04f;
    private int bounceDuration = 6;
    private float bounceIntensity = 0.05f;

    private float exactX, exactY;
    private boolean positionInitialized = false;

    public PlayerController(GameWorld world, int currentSelectedPlayer)
    {
        selectPlayer(1);
        maxBounds = GameManager.worldSize - 2;
        tileSize = GameManager.tileSize;
        this.world = world;
        playerIndex = currentSelectedPlayer;
        setImage(playerIndex, State.idle, Direction.bottom, 1);
        offset = new Vector2(15, 15);
    }

    @Override
    public void addedToWorld(World world)
    {
        super.addedToWorld(world);
        exactX = getX();
        exactY = getY();
        positionInitialized = true;
    }

    @Override
    public void setLocation(int x, int y)
    {
        super.setLocation(x, y);
        if (positionInitialized) {
            exactX = x;
            exactY = y;
        }
    }

    public void selectPlayer(int index)
    {
        playerIndex = index;
    }

    public enum Direction {
        bottom, left, right, top
    }

    public enum State {
        idle, walk,
    }

    private Direction currentDir = Direction.bottom;
    private State currentState = State.idle;
    private State previousState = State.idle;

    private Vector2 velocity = new Vector2(0, 0);
    private Vector2 lastInput = new Vector2(0, 0);
    private float currentSpeed = 0;

    private int fps = 6;
    private int currentFrame = 1;
    private int maxFrame = 4;
    private long lastFrameTime = 0;
    private long frameDuration = 1000 / fps;

    private int bounceTimer = 0;
    private boolean bounceActive = false;
    private int baseImageWidth, baseImageHeight;


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

        if (bounceActive && bounceTimer > 0) applyBounceEffect();
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
            if (rawX < 0) currentDir = Direction.left;
            else if (rawX > 0) currentDir = Direction.right;
            else if (rawY < 0) currentDir = Direction.top;
            else if (rawY > 0) currentDir = Direction.bottom;

            currentState = State.walk;
            currentSpeed += speed / smoothTimeStart;
            if (currentSpeed > speed) currentSpeed = speed;

            lastInput = new Vector2(rawX, rawY);
        } else {
            currentState = State.idle;
            currentSpeed = 0;
            lastInput = new Vector2(0, 0);

            if (previousState == State.walk && currentState == State.idle) {
                bounceTimer = bounceDuration;
                bounceActive = true;
            }
        }

        if (velocity.x != rawX || (velocity.y != rawY && rawX == 0)) {
            velocity = new Vector2(rawX, rawY);
            currentFrame = 1;
            lastFrameTime = System.currentTimeMillis();
        }

        if (hasInput && currentSpeed > 0) {
            float length = (float) Math.sqrt(lastInput.x * lastInput.x + lastInput.y * lastInput.y);
            float normX = lastInput.x / length;
            float normY = lastInput.y / length;
            exactX += normX * currentSpeed;
            exactY += normY * currentSpeed;
        }

        float snapTargetX = snapToGrid(exactX, offset.x);
        float snapTargetY = snapToGrid(exactY, offset.y);

        if (!hasInput) {
            exactX = snapTargetX;
            exactY = snapTargetY;
            exactX = clamp(exactX, minBounds, maxBounds);
            exactY = clamp(exactY, minBounds, maxBounds);
        } else {
            exactX = lerp(exactX, snapTargetX, walkSnapStrength);
            exactY = lerp(exactY, snapTargetY, walkSnapStrength);
        }
        setLocation(Math.round(exactX), Math.round(exactY));

        if (getY() <= 0) world.startTransition(new Vector2(0, 1));
        else if (getY() >= GameManager.worldSize - 1) world.startTransition(new Vector2(0, -1));
        else if (getX() <= 0) world.startTransition(new Vector2(1, 0));
        else if (getX() >= GameManager.worldSize - 1) world.startTransition(new Vector2(-1, 0));
    }

    private void applyBounceEffect()
    {
        bounceTimer--;
        if (bounceTimer <= 0) {
            bounceActive = false;
            return;
        }

        float progress = 1.0f - (float) bounceTimer / bounceDuration;
        float squashAmount = (float) Math.sin(progress * Math.PI) * bounceIntensity;
        float scaleX = 1.0f + squashAmount;
        float scaleY = 1.0f - squashAmount;

        GreenfootImage img = getImage();
        if (img != null && baseImageWidth > 0 && baseImageHeight > 0) img.scale((int) (baseImageWidth * scaleX), (int) (baseImageHeight * scaleY));
    }

    private float snapToGrid(float value, float off)
    {
        return Math.round((value - off) / tileSize) * tileSize + off;
    }

    private float lerp(float a, float b, float t)
    {
        return a + (b - a) * t;
    }

    private float clamp(float value, float min, float max)
    {
        if (value < min) return min;
        if (value > max) return max;
        return value;
    }

    public void setImage(int index, State state, Direction direction, int frame)
    {
        String imagePath = "Player/" + index + "/" + state.toString().charAt(0) + direction.toString().charAt(0) + frame + ".png";
        setImage(imagePath);

        GreenfootImage img = getImage();
        if (img != null) {
            img.scale(img.getWidth() * 2, img.getHeight() * 2);
            baseImageWidth = img.getWidth();
            baseImageHeight = img.getHeight();
        }
    }
}