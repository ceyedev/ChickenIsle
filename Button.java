import greenfoot.*;

public class Button extends Image
{
    public boolean canClick;

    private double normalScale;
    private double hoverScale;
    private double pressScale;
    private int hoverFrames = 8;
    private int pressFrames = 5;
    private int bounceFrames = 10;
    private double bounceFreq = 5;
    private double bounceDecay = 2;
    private int clickDelay = 0;

    private double currentScale;
    private double targetScale;
    private double transitionStartScale;
    private int transitionRemaining;
    private int transitionTotal;

    private boolean mouseOver;
    private boolean pressed;
    private boolean bouncing;

    private Runnable onClickAction;
    private Runnable delayedAction;
    private long actionTime;

    public Button(String imageFile, int alpha, boolean canClick, int clickDelay, double startScale, double scaleFactor, double hoverScaleFactor, double pressScaleFactor)
    {
        this(new GreenfootImage(imageFile), alpha, canClick, clickDelay, startScale, scaleFactor, hoverScaleFactor, pressScaleFactor);
    }

    public Button(GreenfootImage image, int alpha, boolean canClick, int clickDelay, double startScale, double scaleFactor, double hoverScaleFactor, double pressScaleFactor)
    {
        super(image, startScale, alpha);
        this.canClick = canClick;
        this.clickDelay = clickDelay;
        this.normalScale = scaleFactor;
        this.currentScale = normalScale;
        this.targetScale = normalScale;
        this.hoverScale = normalScale * hoverScaleFactor;
        this.pressScale = normalScale * pressScaleFactor;
        setExternalScale(currentScale);
    }

    public void setOnClick(Runnable action)
    {
        this.onClickAction = action;
    }

    @Override
    public void act()
    {
        super.act();
        long now = System.currentTimeMillis();

        if (delayedAction != null && now >= actionTime) {
            delayedAction.run();
            delayedAction = null;
        }

        updateCanClick();
        if (canClick) updateMouseState();
        updateTransition();
        setExternalScale(currentScale);
    }

    private void updateMouseState()
    {
        MouseInfo mouse = Greenfoot.getMouseInfo();
        boolean over = isMouseOver(mouse);

        if (over && Greenfoot.mousePressed(this)) {
            pressed = true;
            startTransition(pressScale, pressFrames, false);
        }

        if (pressed && Greenfoot.mouseClicked(this)) {
            pressed = false;
            if (onClickAction != null) {
                if (clickDelay > 0) {
                    delayedAction = onClickAction;
                    actionTime = System.currentTimeMillis() + clickDelay;
                } else {
                    onClickAction.run();
                }
            }
            double bounceTarget = isMouseOver(Greenfoot.getMouseInfo()) ? hoverScale : normalScale;
            startTransition(bounceTarget, bounceFrames, true);
        }

        if (pressed && Greenfoot.mouseDragEnded(this)) {
            pressed = false;
            double bounceTarget = isMouseOver(Greenfoot.getMouseInfo()) ? hoverScale : normalScale;
            startTransition(bounceTarget, bounceFrames, true);
        }

        if (!pressed) {
            if (over && !mouseOver) {
                mouseOver = true;
                startTransition(hoverScale, hoverFrames, false);
            } else if (!over && mouseOver) {
                mouseOver = false;
                startTransition(normalScale, hoverFrames, false);
            }
        }
    }

    private boolean isMouseOver(MouseInfo mouse)
    {
        if (getWorld() == null || mouse == null) return false;
        GreenfootImage img = getImage();
        if (img == null) return false;
        int halfW = img.getWidth() / 2;
        int halfH = img.getHeight() / 2;
        return mouse.getX() >= getX() - halfW && mouse.getX() <= getX() + halfW && mouse.getY() >= getY() - halfH && mouse.getY() <= getY() + halfH;
    }

    private void startTransition(double target, int totalFrames, boolean bounce)
    {
        this.transitionStartScale = this.currentScale;
        this.targetScale = target;
        this.transitionTotal = totalFrames;
        this.transitionRemaining = totalFrames;
        this.bouncing = bounce;
    }

    private void updateTransition()
    {
        if (transitionRemaining <= 0) return;
        double t = 1.0 - (double) transitionRemaining / transitionTotal;

        if (bouncing) {
            double factor = Math.exp(-bounceDecay * t) * Math.cos(2 * Math.PI * bounceFreq * t);
            currentScale = targetScale + (transitionStartScale - targetScale) * factor;
        } else currentScale = transitionStartScale + (targetScale - transitionStartScale) * t;

        transitionRemaining--;
        if (transitionRemaining == 0) {
            currentScale = targetScale;
            if (bouncing) bouncing = false;
        }
    }

    private void updateCanClick()
    {
        boolean newCanClick = (delayedAction == null);
        if (!newCanClick) {
            pressed = false;
            mouseOver = false;
        }
        canClick = newCanClick;
    }
}