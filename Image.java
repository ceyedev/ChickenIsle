import greenfoot.Actor;
import greenfoot.GreenfootImage;

public class Image extends Actor
{
    private GreenfootImage[] originalFrames;
    private int fps;
    private int currentFrame;
    private long lastFrameTime;

    private double currentScale;
    private double currentAlpha;
    private double externalScale = 1.0;

    private boolean tweenAlphaActive;
    private long tweenAlphaStartTime;
    private int tweenAlphaDuration;
    private double tweenAlphaStartVal;
    private double tweenAlphaGoal;

    private boolean tweenScaleActive;
    private long tweenScaleStartTime;
    private int tweenScaleDuration;
    private double tweenScaleStartVal;
    private double tweenScaleGoal;

    private boolean bounceActive;
    private long bounceStartTime;
    private double bounceStartScale;
    private double bounceGoalScale;
    private double bounceDecay;
    private double bounceFreq;


    public Image(String path) { this(path, 1.0, 1.0); }
    public Image(String path, double scale) { this(path, scale, 1.0); }
    public Image(String path, double scale, double transparency) {
        originalFrames = new GreenfootImage[] { new GreenfootImage(path) };
        finishInit(scale, transparency);
    }
    public Image(GreenfootImage image) { this(image, 1.0, 1.0); }
    public Image(GreenfootImage image, double scale) { this(image, scale, 1.0); }
    public Image(GreenfootImage image, double scale, double transparency) {
        originalFrames = new GreenfootImage[] { new GreenfootImage(image) };
        finishInit(scale, transparency);
    }
    public Image(String[] paths, int fps) { this(paths, fps, 1.0, 1.0); }
    public Image(String[] paths, int fps, double scale) { this(paths, fps, scale, 1.0); }
    public Image(String[] paths, int fps, double scale, double transparency) {
        GreenfootImage[] imgs = new GreenfootImage[paths.length];
        for (int i = 0; i < paths.length; i++) imgs[i] = new GreenfootImage(paths[i]);
        initAnimation(imgs, fps, scale, transparency);
    }
    public Image(GreenfootImage[] images, int fps) { this(images, fps, 1.0, 1.0); }
    public Image(GreenfootImage[] images, int fps, double scale, double transparency) {
        GreenfootImage[] copies = new GreenfootImage[images.length];
        for (int i = 0; i < images.length; i++) copies[i] = new GreenfootImage(images[i]);
        initAnimation(copies, fps, scale, transparency);
    }

    public void SetFrames(GreenfootImage image)
    {
        originalFrames = new GreenfootImage[] { new GreenfootImage(image) };
    }

    public void act()
    {
        boolean displayChanged = false;
        long now = System.currentTimeMillis();

        if (fps > 0 && originalFrames.length > 1) {
            if (now - lastFrameTime >= 1000 / fps) {
                currentFrame = (currentFrame + 1) % originalFrames.length;
                lastFrameTime = now;
                displayChanged = true;
            }
        }

        if (tweenAlphaActive) {
            if (now >= tweenAlphaStartTime) {
                long elapsed = now - tweenAlphaStartTime;
                if (elapsed >= tweenAlphaDuration) {
                    currentAlpha = tweenAlphaGoal;
                    tweenAlphaActive = false;
                } else {
                    double t = (double) elapsed / tweenAlphaDuration;
                    currentAlpha = tweenAlphaStartVal + (tweenAlphaGoal - tweenAlphaStartVal) * t;
                }
                displayChanged = true;
            }
        }

        if (bounceActive) {
            if (now >= bounceStartTime) {
                double t = (now - bounceStartTime) / 1000.0;
                double amplitude = Math.abs(bounceStartScale - bounceGoalScale) * Math.exp(-bounceDecay * t);
                if (amplitude < 0.0001) {
                    currentScale = bounceGoalScale;
                    bounceActive = false;
                } else {
                    double factor = Math.exp(-bounceDecay * t) * Math.cos(2 * Math.PI * bounceFreq * t);
                    currentScale = bounceGoalScale + (bounceStartScale - bounceGoalScale) * factor;
                }
                displayChanged = true;
            }
        } else if (tweenScaleActive) {
            if (now >= tweenScaleStartTime) {
                long elapsed = now - tweenScaleStartTime;
                if (elapsed >= tweenScaleDuration) {
                    currentScale = tweenScaleGoal;
                    tweenScaleActive = false;
                } else {
                    double t = (double) elapsed / tweenScaleDuration;
                    currentScale = tweenScaleStartVal + (tweenScaleGoal - tweenScaleStartVal) * t;
                }
                displayChanged = true;
            }
        }

        if (displayChanged) updateDisplay();
    }

    public void fadeTo(double goalAlpha, int durationMs) {
        fadeTo(goalAlpha, durationMs, System.currentTimeMillis());
    }

    public void fadeTo(double goalAlpha, int durationMs, long startTime) {
        fadeTo(goalAlpha, durationMs, 0, startTime);
    }

    public void fadeTo(double goalAlpha, int durationMs, long delayMs, long startTime)
    {
        tweenAlphaStartVal = currentAlpha;
        tweenAlphaGoal = goalAlpha;
        tweenAlphaDuration = durationMs;
        tweenAlphaStartTime = startTime + delayMs;
        tweenAlphaActive = true;
    }

    public void scaleTo(double goalScale, int durationMs)
    {
        bounceActive = false;
        tweenScaleStartVal = currentScale;
        tweenScaleGoal = goalScale;
        tweenScaleDuration = durationMs;
        tweenScaleStartTime = System.currentTimeMillis();
        tweenScaleActive = true;
    }

    public void bounceTo(double goalScale, double decay, double freq) {
        bounceTo(goalScale, decay, freq, System.currentTimeMillis());
    }

    public void bounceTo(double goalScale, double decay, double freq, long startTime) {
        bounceTo(goalScale, decay, freq, 0, startTime);
    }

    public void bounceTo(double goalScale, double decay, double freq, long delayMs, long startTime)
    {
        tweenScaleActive = false;
        bounceStartScale = currentScale;
        bounceGoalScale = goalScale;
        bounceStartTime = startTime + delayMs;
        bounceDecay = decay;
        bounceFreq = freq;
        bounceActive = true;
    }

    public boolean isBouncing() { return bounceActive; }

    public void setExternalScale(double scale)
    {
        if (Math.abs(this.externalScale - scale) > 0.0001) {
            this.externalScale = scale;
            updateDisplay();
        }
    }

    private void finishInit(double scale, double transparency)
    {
        this.currentScale = scale;
        this.currentAlpha = transparency;
        updateDisplay();
    }

    private void initAnimation(GreenfootImage[] images, int fps, double scale, double transparency)
    {
        this.originalFrames = images;
        this.fps = fps;
        this.currentScale = scale;
        this.currentAlpha = transparency;
        this.lastFrameTime = System.currentTimeMillis();
        updateDisplay();
    }

    private void updateDisplay()
    {
        if (originalFrames == null || originalFrames.length == 0) return;
        GreenfootImage source = originalFrames[currentFrame];
        GreenfootImage img = new GreenfootImage(source);
        double effectiveScale = currentScale * externalScale;
        int w = (int) (source.getWidth() * effectiveScale);
        int h = (int) (source.getHeight() * effectiveScale);
        if (w < 1) w = 1;
        if (h < 1) h = 1;
        img.scale(w, h);
        img.setTransparency((int) (currentAlpha * 255));
        setImage(img);
    }
}