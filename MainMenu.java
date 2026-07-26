import greenfoot.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.ArrayList;
import java.awt.Desktop;
import java.io.File;
import java.net.URI;

public class MainMenu extends World
{
    private double baseSpeed = 2.0; //px per act()
    private String emptyImagePath = "Parallax/empty.png";

    private static Config[] configurations = {
        new Config(
            new String[]{"Parallax/snow/1.png", "Parallax/snow/2.png", null, null, null},
            new double[]{0.3, 0.8, 0, 0, 0}
        ),
        new Config(
            new String[]{"Parallax/ocean/1.png", "Parallax/ocean/2.png", "Parallax/ocean/3.png", "Parallax/ocean/4.png", null},
            new double[]{0.2, 0.25, 0.4, 0.6, 0}
        ),
        new Config(
            new String[]{"Parallax/nature/1.png", "Parallax/nature/2.png", "Parallax/nature/3.png", "Parallax/nature/4.png", null},
            new double[]{0.2, 0.3, 0.5, 0.8, 0}
        ),
        new Config(
            new String[]{"Parallax/mountain/1.png", "Parallax/mountain/2.png", "Parallax/mountain/3.png", "Parallax/mountain/4.png", "Parallax/mountain/5.png"},
            new double[]{0.1, 0.2, 0.25, 0.3, 0.5}
        ),
        new Config(
            new String[]{"Parallax/sunset/1.png", "Parallax/sunset/2.png", null, null, null},
            new double[]{0.2, 0.4, 0, 0, 0}
        ),
    };

    private int layerCount = getMaxLayers();

    private static int getMaxLayers() {
        int max = 0;
        for (Config config : configurations) max = Math.max(max, config.paths.length);
        return max;
    }

    private Image[] currentActors = new Image[layerCount];
    private Image[] nextActors = new Image[layerCount];

    private Map<String, GreenfootImage> imageCache = new HashMap<>();
    private GreenfootImage emptyTemplate;

    private double[] currentOffsets = new double[layerCount];
    private double[] nextOffsets = new double[layerCount];
    private int[] imageWidths = new int[layerCount];

    private int worldWidth, worldHeight;

    private Config currentConfig = null;
    private Config nextConfig = null;

    private boolean fading = false;
    private int fadeDuration = 60;
    private int fadeTimer;
    private int switchInterval = 300;
    private int switchTimer;

    private Random rand = new Random();

    private Image playerSelectorSprite;
    private Image playerSelectorIndex;
    private Image currentMap;
    private int currentSelectedPlayer = 1;
    private int currentSelectedWorld = 1;
    private int fps = 2;
    private int currentSelectedPlayerFrame;
    private long lastFrameTime;
    private long frameDuration = 1000 / fps;

    private CurrentMenu currentMenu = CurrentMenu.ClickToEnter;

    enum CurrentMenu {
        ClickToEnter,
        BootScreen,
        MainMenu,
        CharacterSelector,
        WorldSelector,
        Credits,
        SaveMenu,
    }

    private ArrayList<Actor> currentUI = new ArrayList<Actor>();

    public MainMenu()
    {
        super(640, 640, 1, false);
        Greenfoot.setSpeed(50);
        worldWidth = getWidth();
        worldHeight = getHeight();

        emptyTemplate = loadAndCacheImage(emptyImagePath);

        for (int i = 0; i < layerCount; i++) {
            currentActors[i] = new Image(copyImage(emptyTemplate));
            nextActors[i]    = new Image(copyImage(emptyTemplate));
            addObject(currentActors[i], 0, 0);
            addObject(nextActors[i], 0, 0);
            nextActors[i].getImage().setTransparency(0);
        }

        int commonWidth = emptyTemplate.getWidth();
        for (int i = 0; i < layerCount; i++) imageWidths[i] = commonWidth;

        currentConfig = configurations[rand.nextInt(configurations.length)];
        applyConfigImages(currentActors, currentConfig);

        int nextIdx;
        do {
            nextIdx = rand.nextInt(configurations.length);
        } while (configurations[nextIdx] == currentConfig);
        nextConfig = configurations[nextIdx];
        applyConfigImages(nextActors, nextConfig);

        for (int i = 0; i < layerCount; i++) nextActors[i].getImage().setTransparency(0);
        for (int i = 0; i < layerCount; i++) nextOffsets[i] = currentOffsets[i];
        updatePositions(currentActors, currentOffsets);
        updatePositions(nextActors, nextOffsets);

        SelectPanel(currentMenu);
    }

    private GreenfootImage loadAndCacheImage(String path)
    {
        if (!imageCache.containsKey(path)) {
            GreenfootImage img = new GreenfootImage(path);
            img.scale(img.getWidth() * 2, img.getHeight() * 2);
            img.setTransparency(255);
            imageCache.put(path, img);
        }
        return imageCache.get(path);
    }

    private GreenfootImage copyImage(GreenfootImage source) {
        return new GreenfootImage(source);
    }

    private void applyConfigImages(Image[] actors, Config config)
    {
        for (int i = 0; i < layerCount; i++) {
            String path = (config.paths[i] != null) ? config.paths[i] : emptyImagePath;
            GreenfootImage template = loadAndCacheImage(path);
            actors[i].setImage(copyImage(template));
        }
    }

    public void act()
    {
        if (currentConfig != null) {
            for (int i = 0; i < layerCount; i++) {
                currentOffsets[i] -= baseSpeed * currentConfig.speeds[i];
                if (currentOffsets[i] + imageWidths[i] <= worldWidth) currentOffsets[i] += imageWidths[i] / 2.0;
            }
            updatePositions(currentActors, currentOffsets);
        }

        if (nextConfig != null) {
            for (int i = 0; i < layerCount; i++) {
                nextOffsets[i] -= baseSpeed * nextConfig.speeds[i];
                if (nextOffsets[i] + imageWidths[i] <= worldWidth) nextOffsets[i] += imageWidths[i] / 2.0;
            }
            updatePositions(nextActors, nextOffsets);
        }

        if (!fading) {
            switchTimer++;
            if (switchTimer >= switchInterval) {
                fading = true;
                fadeTimer = 0;
                switchTimer = 0;
            }
        } else {
            fadeTimer++;
            double progress = (double) fadeTimer / fadeDuration;
            int alphaCurrent = (int) ((1.0 - progress) * 255);
            int alphaNext = (int) (progress * 255);

            for (int i = 0; i < layerCount; i++) {
                currentActors[i].getImage().setTransparency(alphaCurrent);
                nextActors[i].getImage().setTransparency(alphaNext);
            }

            if (fadeTimer >= fadeDuration) {
                Image[] tempActors = currentActors;
                currentActors = nextActors;
                nextActors = tempActors;

                double[] tempOffsets = currentOffsets;
                currentOffsets = nextOffsets;
                nextOffsets = tempOffsets;

                Config tempConfig = currentConfig;
                currentConfig = nextConfig;
                nextConfig = null;

                int nextIdx;
                do {
                    nextIdx = rand.nextInt(configurations.length);
                } while (configurations[nextIdx] == currentConfig);
                nextConfig = configurations[nextIdx];
                applyConfigImages(nextActors, nextConfig);

                for (int i = 0; i < layerCount; i++) nextActors[i].getImage().setTransparency(0);
                for (int i = 0; i < layerCount; i++) nextOffsets[i] = currentOffsets[i];
                updatePositions(nextActors, nextOffsets);

                fading = false;
                fadeTimer = 0;
            }
        }

        if (currentMenu == CurrentMenu.CharacterSelector) {
            long currentTime = System.currentTimeMillis();
            if (currentTime - lastFrameTime >= frameDuration) {
                currentSelectedPlayerFrame++;
                if (currentSelectedPlayerFrame >= 4) currentSelectedPlayerFrame = 0;
                lastFrameTime = currentTime;
            } else return;

            String[] frameNames = new String[]{"b", "l", "t", "r"};

            playerSelectorSprite.setImage("Player/" + currentSelectedPlayer + "/i" + frameNames[currentSelectedPlayerFrame] + "1.png");

            GreenfootImage playerSelectorImg = playerSelectorSprite.getImage();
            if (playerSelectorImg != null) playerSelectorImg.scale(playerSelectorImg.getWidth() * 6, playerSelectorImg.getHeight() * 6);
        }
    }

    private void ChangeCurrentPlayer(int diff)
    {
        lastFrameTime = System.currentTimeMillis() + frameDuration;
        currentSelectedPlayer += diff;
        if (currentSelectedPlayer == 0) currentSelectedPlayer = 6;
        else if (currentSelectedPlayer == 7) currentSelectedPlayer = 1;

        String[] frameNames = new String[]{"b", "l", "t", "r"};
        playerSelectorSprite.setImage("Player/" + currentSelectedPlayer + "/i" + frameNames[currentSelectedPlayerFrame] + "1.png");
        GreenfootImage playerSelectorImg = playerSelectorSprite.getImage();
        playerSelectorSprite.SetFrames(playerSelectorImg);
        if (playerSelectorImg != null) playerSelectorImg.scale(playerSelectorImg.getWidth() * 6, playerSelectorImg.getHeight() * 6);
        playerSelectorIndex.setLocation(215 + currentSelectedPlayer* 30, 164);
    }

    private void ChangeCurrentWorld(int diff)
    {
        currentSelectedWorld += diff;
        if (currentSelectedWorld == 0) currentSelectedWorld = 2;
        else if (currentSelectedWorld == 3) currentSelectedWorld = 1;
        currentMap.setImage("mapPreview/" + currentSelectedWorld + ".png");
        currentMap.SetFrames(currentMap.getImage());
    }

    private void SelectPanel(CurrentMenu newMenu)
    {
        for (int i = currentUI.size() - 1; i >= 0; i--) {
            Actor rem = currentUI.get(i);
            if(rem instanceof Button) ((Button)rem).canClick = false;
            removeObject(rem);
            currentUI.remove(rem);
        }

        long syncTime = System.currentTimeMillis();

        switch (newMenu) {
            case CurrentMenu.ClickToEnter:
            {
                Image bg = new Image("BootAnim/BlackBG.png", 1, 0.2);
                addUI(bg, 320, 320);

                Button startBtn = new Button("BootAnim/TapToFocus.png", 1, true, 1, 1, 1, 1, 1);
                startBtn.setOnClick(() -> SelectPanel(CurrentMenu.BootScreen));
                addUI(startBtn, 320, 320);

                break;
            }
            case CurrentMenu.BootScreen:
            {
                Image bg = new Image("BootAnim/BlackBG.png", 1, 0.2);
                bg.fadeTo(0, 1000, 3200, syncTime);
                addUI(bg, 320, 320);

                Image Outline = new Image("BootAnim/Outline.png", 4, 0);
                Outline.bounceTo(2, 18, 2, 3200, syncTime);
                Outline.fadeTo(1, 500, 3200, syncTime);
                addUI(Outline, 320, 180);

                Image Cloud = new Image("BootAnim/Cloud.png", 0, 0);
                Cloud.bounceTo(1, 8, 1, 900 + 800, syncTime);
                Cloud.fadeTo(1, 500, 900 + 800, syncTime);
                addUI(Cloud, 320, 320);

                Image Title = new Image("BootAnim/Title.png", 0.5, 0);
                Title.bounceTo(1, 15, 2.5, 100 + 500, syncTime);
                Title.fadeTo(1, 800, 100 + 500, syncTime);
                addUI(Title, 320, 320);

                Image ChickenText = new Image("BootAnim/ChickenText.png", 5, 0);
                ChickenText.bounceTo(1, 15, 2.5, 750 + 800, syncTime);
                ChickenText.fadeTo(1, 400, 750 + 800, syncTime);
                addUI(ChickenText, 320, 320);

                Image IsleText = new Image("BootAnim/IsleText.png", 5, 0);
                IsleText.bounceTo(1, 15, 2.5, 1100 + 800, syncTime);
                IsleText.fadeTo(1, 400, 1100 + 800, syncTime);
                addUI(IsleText, 320, 320);

                Image WingShadow = new Image("BootAnim/WingShadow.png", 0, 0);
                WingShadow.bounceTo(1, 15, 2, 1700 + 800, syncTime);
                WingShadow.fadeTo(1, 400, 1700 + 800, syncTime);
                addUI(WingShadow, 320, 320);

                Image Wing = new Image("BootAnim/Wing.png", 2, 0);
                Wing.bounceTo(1, 12, 3, 1500 + 800, syncTime);
                Wing.fadeTo(1, 400, 1500 + 800, syncTime);
                addUI(Wing, 320, 320);

                Image TapToStart = new Image("BootAnim/TapToStart.png", 0.95, 0);
                TapToStart.bounceTo(1, 0, 0.25, 4000 + 800, syncTime);
                TapToStart.fadeTo(1, 1000, 4000 + 800, syncTime);
                addUI(TapToStart, 320, 565);

                Image Crown = new Image("BootAnim/Crown.png", 0, 0);
                Crown.bounceTo(1, 8, 10, 2000 + 800, syncTime);
                Crown.fadeTo(1, 500, 2000 + 800, syncTime);
                addUI(Crown, 264, 106);

                Button startBtn = new Button("BootAnim/BlackBG.png", 0, true, 0, 1, 1, 1, 1);
                startBtn.setOnClick(() -> SelectPanel(CurrentMenu.MainMenu));
                addUI(startBtn, 320, 320);

                break;
            }
            case CurrentMenu.MainMenu:
            {
                Image bg = new Image("MainMenuBG.png", 0, 0);
                bg.bounceTo(6, 15, 2.5, 100, syncTime);
                bg.fadeTo(1, 400, 100, syncTime); 
                addUI(bg, 320, 320);

                Button playBtn = new Button("PlayButton.png", 0, true, 0, 0, 6, 1.05, 0.95);
                playBtn.setOnClick(() -> SelectPanel(CurrentMenu.SaveMenu));
                playBtn.bounceTo(1, 7, 2);
                playBtn.fadeTo(1, 400);
                addUI(playBtn, 320, 200);

                Button optionsBtn = new Button("OptionsButton.png", 0, true, 200, 0, 6, 1.05, 0.95);
                optionsBtn.setOnClick(() -> System.out.println("Button wurde geklickt!"));
                optionsBtn.bounceTo(1, 10, 2.5, 300, syncTime);
                optionsBtn.fadeTo(1, 600, 300, syncTime);
                addUI(optionsBtn, 320, 353);

                Button creditsBtn = new Button("CreditsButton.png", 0, true, 0, 0, 6, 1.05, 0.95);
                creditsBtn.setOnClick(() -> SelectPanel(CurrentMenu.Credits));
                creditsBtn.bounceTo(1, 10, 2.5, 500, syncTime);
                creditsBtn.fadeTo(1, 600, 500, syncTime);
                addUI(creditsBtn, 320, 467);

                break;
            }
            case CurrentMenu.CharacterSelector:
            {
                Image bg = new Image("CharacterSelector.png", 0, 0);
                bg.bounceTo(6, 12, 2.5, 0, syncTime);
                bg.fadeTo(1, 400, 0, syncTime); 
                addUI(bg, 320, 320);

                Image csnBg = new Image("CharacterSelectorNature.png", 0, 0);
                csnBg.bounceTo(6, 12, 2.5, 0, syncTime);
                csnBg.fadeTo(1, 200, 300, syncTime); 
                addUI(csnBg, 320, 275);

                playerSelectorIndex = new Image("SelectedCharacterIndexActive.png", 6, 0);
                playerSelectorIndex.fadeTo(1, 500, 300, syncTime); 
                addUI(playerSelectorIndex, 215 + currentSelectedPlayer* 30, 164);

                playerSelectorSprite = new Image("Player/" + currentSelectedPlayer + "/ib1.png", 0, 0);
                playerSelectorSprite.bounceTo(6, 9, 3, 400, syncTime);
                playerSelectorSprite.fadeTo(1, 400, 400, syncTime); 
                int[] counter = {currentSelectedPlayer};
                Image[] psimage = {playerSelectorSprite};
                addUI(playerSelectorSprite, 320 - 3, 290);

                Button selectBtn = new Button("SelectButton.png", 0, true, 0, 0, 6, 1.05, 0.95);
                selectBtn.setOnClick(() -> Greenfoot.setWorld(new GameWorld(currentSelectedPlayer, currentSelectedWorld)));
                selectBtn.bounceTo(1, 8, 2.5, 200, syncTime);
                selectBtn.fadeTo(1, 400, 200, syncTime); 
                addUI(selectBtn, 320, 431);

                Button leftBtn = new Button("ButtonLeft.png", 0, true, 0, 0, 6, 1.05, 0.95);
                leftBtn.setOnClick(() -> ChangeCurrentPlayer(-1));
                leftBtn.bounceTo(1, 8, 3, 300, syncTime);
                leftBtn.fadeTo(1, 400, 300, syncTime); 
                addUI(leftBtn, 134, 278);

                Button rightBtn = new Button("ButtonRight.png", 0, true, 0, 0, 6, 1.05, 0.95);
                rightBtn.setOnClick(() -> ChangeCurrentPlayer(1));
                rightBtn.bounceTo(1, 8, 3, 400, syncTime);
                rightBtn.fadeTo(1, 400, 400, syncTime); 
                addUI(rightBtn, 506, 278);

                Button backBtn = new Button("BackButton.png", 0, true, 0, 0, 3, 1.05, 0.95);
                backBtn.setOnClick(() -> SelectPanel(CurrentMenu.WorldSelector));
                backBtn.bounceTo(1.0, 10, 2.5);
                backBtn.fadeTo(1, 1000);
                addUI(backBtn, 75, 602);

                lastFrameTime = syncTime + 1200;
                currentSelectedPlayerFrame = 0;

                break;
            }
            case CurrentMenu.WorldSelector:
            {
                Image bg = new Image("WorldSelectBG.png", 0, 0);
                bg.bounceTo(6, 12, 2.5, 0, syncTime);
                bg.fadeTo(1, 400, 0, syncTime); 
                addUI(bg, 320, 320);

                currentMap = new Image("mapPreview/" + currentSelectedWorld + ".png", 0, 0);
                currentMap.bounceTo(1, 12, 2.5, 150, syncTime);
                currentMap.fadeTo(1, 400, 150, syncTime); 
                addUI(currentMap, 320, 320-6);

                Button leftWorldBtn = new Button("ButtonLeft.png", 0, true, 0, 0, 6, 1.05, 0.95);
                leftWorldBtn.setOnClick(() -> ChangeCurrentWorld(-1));
                leftWorldBtn.bounceTo(1, 8, 3, 300, syncTime);
                leftWorldBtn.fadeTo(1, 400, 300, syncTime); 
                addUI(leftWorldBtn, 49, 320);

                Button rightWorldBtn = new Button("ButtonRight.png", 0, true, 0, 0, 6, 1.05, 0.95);
                rightWorldBtn.setOnClick(() -> ChangeCurrentWorld(1));
                rightWorldBtn.bounceTo(1, 8, 3, 400, syncTime);
                rightWorldBtn.fadeTo(1, 400, 400, syncTime); 
                addUI(rightWorldBtn, 640-49, 320);

                Button backBtn = new Button("BackButton.png", 0, true, 0, 0, 3, 1.05, 0.95);
                backBtn.setOnClick(() -> SelectPanel(CurrentMenu.SaveMenu));
                backBtn.bounceTo(1.0, 10, 2.5);
                backBtn.fadeTo(1, 1000);
                addUI(backBtn, 75, 602);

                Button nextBtn = new Button("NextButton.png", 0, true, 0, 0, 3, 1.05, 0.95);
                nextBtn.setOnClick(() -> SelectPanel(CurrentMenu.CharacterSelector));
                nextBtn.bounceTo(1.0, 10, 2.5);
                nextBtn.fadeTo(1, 1000);
                addUI(nextBtn, 640-75, 602);

                break;
            }
            case CurrentMenu.Credits:
            {
                Image bg = new Image("Credits.png", 0, 0);
                bg.fadeTo(1, 400, 0, syncTime);
                bg.bounceTo(1.0, 15, 2.5, 0, syncTime);
                addUI(bg, 320, 320);

                Image creditsAnim = new Image(new String[]{"CreditsAnim1.png", "CreditsAnim2.png", "CreditsAnim3.png", "CreditsAnim4.png", "CreditsAnim5.png", "CreditsAnim6.png", "CreditsAnim7.png", "CreditsAnim8.png"}, 12, 0, 0);
                creditsAnim.fadeTo(1, 400, 0, syncTime);  
                creditsAnim.bounceTo(1.0, 15, 2.5, 0, syncTime);
                addUI(creditsAnim, 320, 320);

                Button eyeBtn = new Button("eye.png", 0, true, 200, 0, 1, 1.05, 0.95);
                eyeBtn.setOnClick(() -> {try {Desktop.getDesktop().browse(new URI("https://ceyedev.com"));} catch (Exception e) {e.printStackTrace();}});
                eyeBtn.bounceTo(1.0, 6, 3, 0, syncTime);
                eyeBtn.fadeTo(1, 600, 0, syncTime);
                addUI(eyeBtn, 176, 178);

                Button creditsSpritesBtn = new Button("SpritesButton.png", 0, true, 200, 0, 3, 1.05, 0.95);
                creditsSpritesBtn.setOnClick(() -> {try {Desktop.getDesktop().open(new File("credits/sprites.txt"));} catch (Exception  e) {e.printStackTrace();}});
                creditsSpritesBtn.bounceTo(1.0, 10, 2.5, 350, syncTime);
                creditsSpritesBtn.fadeTo(1, 400, 350, syncTime);
                addUI(creditsSpritesBtn, 219, 435);

                Button creditsAudioBtn = new Button("AudioButton.png", 0, true, 200, 0, 3, 1.05, 0.95);
                creditsAudioBtn.setOnClick(() -> {try {Desktop.getDesktop().open(new File("credits/audio.txt"));} catch (Exception  e) {e.printStackTrace();}});
                creditsAudioBtn.bounceTo(1.0, 10, 2.5, 550, syncTime);
                creditsAudioBtn.fadeTo(1, 400, 550, syncTime);
                addUI(creditsAudioBtn, 420, 435);

                Button backBtn = new Button("BackButton.png", 0, true, 0, 0, 3, 1.05, 0.95);
                backBtn.setOnClick(() -> SelectPanel(CurrentMenu.MainMenu));
                backBtn.bounceTo(1.0, 10, 2.5);
                backBtn.fadeTo(1, 1000);
                addUI(backBtn, 75, 602);

                break;
            }
            case CurrentMenu.SaveMenu:
            {
                boolean[] saveActive = new boolean[]{true, true, false};

                for (int i = 0; i < 3; i++) {
                    Image saveBG = new Image("SaveBG.png", 0, 0);
                    saveBG.fadeTo(1, 400, 100*i, syncTime);
                    saveBG.bounceTo(4.0, 15, 2.5, 100 + 100*i, syncTime);
                    addUI(saveBG, 320, 200+i*120);

                    if (saveActive[i]) {
                        Image preview = new Image("mapPreview/1_48.png", 0, 0);
                        preview.fadeTo(1, 400, 400 + 100*i, syncTime);
                        preview.bounceTo(1.0, 8, 3, 400 + 100*i, syncTime);
                        addUI(preview, 128, 194+i*120);

                        Image name = new Image("Save" + (i+1) + ".png", 0, 0);
                        name.fadeTo(1, 400, 150 + 100*i, syncTime);
                        name.bounceTo(4.0, 15, 2.5, 150 + 100*i, syncTime);
                        addUI(name, 126+ 4*28, 194+i*120);

                        Button editBtn = new Button("SaveEdit.png", 0, true, 0, 0, 4, 1.05, 0.95);
                        //editBtn.setOnClick(() -> {try {Desktop.getDesktop().open(new File("credits/audio.txt"));} catch (Exception  e) {e.printStackTrace();}});
                        editBtn.fadeTo(1, 400, 300 + 100*i, syncTime);
                        editBtn.bounceTo(1, 10, 2.5, 300 + 100*i, syncTime);
                        addUI(editBtn, 454, 196+i*120);

                        Button startBtn = new Button("SaveStart.png", 0, true, 0, 0, 4, 1.05, 0.95);
                        startBtn.setOnClick(() -> Greenfoot.setWorld(new GameWorld(currentSelectedPlayer, currentSelectedWorld)));
                        startBtn.fadeTo(1, 400, 400 + 100*i, syncTime);
                        startBtn.bounceTo(1, 10, 2.5, 400 + 100*i, syncTime);
                        addUI(startBtn, 514, 196+i*120);
                    } else {
                        Image name = new Image("emptySave.png", 0, 0);
                        name.fadeTo(1, 400, 150 + 100*i, syncTime);
                        name.bounceTo(4.0, 15, 2.5, 150 + 100*i, syncTime);
                        addUI(name, 126+ 4*28, 194+i*120);

                        Button newGame = new Button("NewSave.png", 0, true, 0, 0, 4, 1.05, 0.95);
                        newGame.setOnClick(() -> SelectPanel(CurrentMenu.WorldSelector));
                        newGame.bounceTo(1, 10, 2.5, 300 + 100*i, syncTime);
                        newGame.fadeTo(1, 400, 300 + 100*i, syncTime);
                        addUI(newGame, 452 - 4*5, 196+i*120);
                    }

                }

                Button backBtn = new Button("BackButton.png", 0, true, 0, 0, 3, 1.05, 0.95);
                backBtn.setOnClick(() -> SelectPanel(CurrentMenu.MainMenu));
                backBtn.bounceTo(1.0, 10, 2.5);
                backBtn.fadeTo(1, 1000);
                addUI(backBtn, 75, 602);

                break;
            }
        }

        currentMenu = newMenu;
    }

    private void addUI(Actor obj, int x, int y)
    {
        addObject(obj, x, y);
        currentUI.add(obj);
    }

    private void updatePositions(Image[] actors, double[] offsets)
    {
        for (int i = 0; i < layerCount; i++) {
            int centerX = (int) Math.round(offsets[i] + imageWidths[i] / 2.0);
            actors[i].setLocation(centerX, worldHeight / 2);
        }
    }

    private static class Config
    {
        String[] paths;
        double[] speeds;

        Config(String[] paths, double[] speeds) {
            this.paths = paths;
            this.speeds = speeds;
        }
    }
}