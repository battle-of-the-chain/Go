package com.mygdx.game.hud;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.Logger;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.mygdx.game.assets.RegionNames;
import com.mygdx.game.screen01.ScreenGoController;
import com.mygdx.game.util.GdxUtils;

public class HUD implements Disposable, MyClickListener {

    private static final Logger log = new Logger("HUD", Logger.DEBUG);

    ScreenGoController controller;

    GameObjectResetButton resetButton;
    private TextureRegion smile_happyRegion;
    //private TextureRegion smile_sadRegion;
    //private TextureRegion smile_winRegion;

    BitmapFont font;
    int blackTerritory;
    int whiteTerritory;
    Vector3 textMinesInCoordinateToScreen;
    Vector3 textDurationInCoordinateToScreen;
    GlyphLayout tmplayout;
    float x;
    float y;
    float w;
    float h;


    public HUD(float x, float y, float w, float h, TextureAtlas gamePlayAtlas, ScreenGoController controller) {
        this.x = x;
        this.y = y;
        this.w = w;
        this.h = h;
        this.controller = controller;
        textMinesInCoordinateToScreen = new Vector3();
        textDurationInCoordinateToScreen = new Vector3();
        smile_happyRegion = gamePlayAtlas.findRegion(RegionNames.SMILE_HAPPY);
        resetButton = new GameObjectResetButton(x + w / 2 - 0.5f, y - h / 2 - 0.5f, 1, 1);

    }

    public void start() {
        whiteTerritory = 0;
        blackTerritory = 0;
    }

    public void updateTerritory(int blackTerritory, int whiteTerritory) {
        this.blackTerritory = blackTerritory;
        this.whiteTerritory = whiteTerritory;
    }

    public void renderUpdate(Camera cameraFont, SpriteBatch batch, float dt) {
        batch.draw(smile_happyRegion, resetButton.bounds.x, resetButton.bounds.y, resetButton.bounds.width, resetButton.bounds.height);
        batch.setProjectionMatrix(cameraFont.combined);
        font.setColor(Color.BLACK);
        tmplayout = new GlyphLayout(font, String.format("%02d", (int) blackTerritory));
        font.draw(batch, tmplayout, textMinesInCoordinateToScreen.x, textMinesInCoordinateToScreen.y);
        font.setColor(Color.WHITE);
        tmplayout = new GlyphLayout(font, String.format("%02d", (int) whiteTerritory));
        font.draw(batch, tmplayout, textDurationInCoordinateToScreen.x-tmplayout.width, textDurationInCoordinateToScreen.y);
    }

    @Override
    public void dispose() {
        font.dispose();
    }

    @Override
    public boolean onClickEvent(float x, float y) {
        return resetButton.onClickEvent(x, y);  //delegate
    }


    public void update() {
        if (resetButton.scheduleReset) {
            controller.newGame();
            resetButton.scheduleReset = false;
        }
    }
    /*
      Font based objects must be recalculated on resize event.
      Also position of text must be changed.
   */
    private void createFontBasedObjects(Camera boardCam, Viewport boardV) {
        font = GdxUtils.getTTFFontInWorldUnits(1, boardV.getWorldHeight());
        textMinesInCoordinateToScreen.set(x, y - h / 2 + 0.5f, 0);
        GdxUtils.ProjectWorldCoordinatesInScreenCoordinates(boardCam, textMinesInCoordinateToScreen);
        textDurationInCoordinateToScreen.set(x + w, y - h / 2 + 0.5f, 0);
        GdxUtils.ProjectWorldCoordinatesInScreenCoordinates(boardCam, textDurationInCoordinateToScreen);
    }

    public void resize(int width, int height, Camera boardCam, Viewport boardV) {
        if (font != null) font.dispose();
        createFontBasedObjects(boardCam,boardV);
        start();
    }
}

