package com.mygdx.game.screen01;



import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.Logger;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.mygdx.game.Go;
import com.mygdx.game.assets.AssetDescriptors;
import com.mygdx.game.assets.RegionNames;
import com.mygdx.game.debug.DebugCameraController;
import com.mygdx.game.game_manager.GameManager;
import com.mygdx.game.hud.HUD;
import com.mygdx.game.util.GdxUtils;

public class ScreenGoRenderer implements Disposable {
    public static final Logger log = new Logger(ScreenGoRenderer.class.getName(), Logger.DEBUG);
    public SpriteBatch batch;
    public OrthographicCamera camera;
    public OrthographicCamera cameraFont;
    public Viewport viewportFont;

    private TextureRegion emptyCell;
    private TextureRegion goPieceBlack;
    private TextureRegion goPieceWhite;
    private Animation<TextureRegion> lastPlacedAnimationBlack;
    private Animation<TextureRegion> lastPlacedAnimationWhite;



    AssetManager am;

    DebugCameraController dcc;
    ShapeRenderer sr;

    ScreenGoController controller;

    Go gameMain;


    public OrthographicCamera boardCam;
    public Viewport boardV;



    SpriteBatch sb;
    HUD hud;


    public static final int HUD_H = 2;


    public ScreenGoRenderer(Go game, ScreenGoController controller) {
        this.gameMain = game;
        am = gameMain.getAssetManager();
        TextureAtlas gamePlayAtlas = am.get(AssetDescriptors.GAME_PLAY1);
        TextureAtlas gamePlayAtlasForHUD = am.get(AssetDescriptors.GAME_PLAY);

        goPieceBlack = gamePlayAtlas.findRegion(RegionNames.GO_PIECE_BLACK);
        goPieceWhite = gamePlayAtlas.findRegion(RegionNames.GO_PIECE_WHITE);
        emptyCell = gamePlayAtlas.findRegion(RegionNames.EMPTY_CELL);

        lastPlacedAnimationBlack = new Animation(0.18f,gamePlayAtlas.findRegions(RegionNames.BLACK_APPEAR), Animation.PlayMode.LOOP);
        lastPlacedAnimationWhite = new Animation(0.18f,gamePlayAtlas.findRegions(RegionNames.WHITE_APPEAR), Animation.PlayMode.LOOP);

        this.controller = controller;

        cameraFont = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        viewportFont = new ScreenViewport(cameraFont);

        camera = new OrthographicCamera();
        batch = new SpriteBatch();
        sr = new ShapeRenderer();

        boardCam = new OrthographicCamera(controller.width, controller.height + HUD_H);
        boardV = new FitViewport(controller.width, controller.height + HUD_H, boardCam);
        boardV.update(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), true);

        controller.gameState = ScreenGoController.GameState.BEGIN;
        hud = new HUD(0, (float) (controller.height + HUD_H), (float) controller.width, HUD_H, gamePlayAtlasForHUD, controller); //??
        hud.resize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), boardCam, boardV);
        hud.start();
        dcc = new DebugCameraController();
        dcc.setStartPosition((float)controller.width / 2, (float)(controller.height + HUD_H) / 2);
    }

    public void inputHandle() {
        if (Gdx.input.isKeyPressed(Input.Keys.ESCAPE)) gameMain.safeExit();

    }

    public void render(float delta) {
        update(); //Also state
        dcc.handleDebugInput(delta);
        dcc.applyTo(boardCam);
        batch.totalRenderCalls = 0;
        boardCam.update();
        GdxUtils.clearScreen(Color.LIGHT_GRAY);
        //DebugViewportUtils.drawGrid(boardV,sr);
        batch.begin();
        batch.setProjectionMatrix(boardCam.combined);
        hud.renderUpdate(cameraFont, batch, delta);
        //log.debug("Board1:"+batch.totalRenderCalls); uncomment later
        drawBoard();
        batch.end();
        //log.debug("Total RenderCalls:"+batch.totalRenderCalls); uncomment later
        inputHandle();
    }


    private void drawBoardNet4Debug() {
        sr.setColor(Color.LIGHT_GRAY);
        sr.setProjectionMatrix(boardCam.combined);
        sr.begin(ShapeRenderer.ShapeType.Filled);
        {
            sr.rect(0, 0, controller.width, controller.height + HUD_H);
        }
        sr.end();

        sr.setProjectionMatrix(boardCam.combined);
        sr.setColor(Color.DARK_GRAY);
        sr.begin(ShapeRenderer.ShapeType.Line);
        {
            for (int x = 0; x <= controller.width; x++) { //vertical lines
                sr.line(x, 0, x, controller.height);
            }

            for (int y = 0; y <= controller.height; y++) { //horizontal lines
                sr.line(0, y, controller.width, y);
            }
        }
        sr.end();
    }

    private void drawBoard() {
        batch.setProjectionMatrix(boardCam.combined);
        for (int x = 0; x < controller.width; x++) {
            for (int y = 0; y < controller.height; y++) {
                if(controller.boardState[x][y].contains(ScreenGoController.CellState.EMPTY)){
                    batch.draw(emptyCell,x,y,1,1);
                }
                else if(controller.boardState[x][y].contains(ScreenGoController.CellState.BLACK)){
                    if(x == (int)controller.previousBlackStone.x && y == (int)controller.previousBlackStone.y && GameManager.INSTANCE.isAnimated()){
                         batch.draw(lastPlacedAnimationBlack.getKeyFrame(controller.animationTime), x, y, 1, 1);
                    }
                    else{
                        //batch.draw(lastPlacedAnimationBlack.getKeyFrame(controller.animationTime), x, y, 1, 1);
                        batch.draw(goPieceBlack,x,y,1,1);
                    }
                }
                else if(controller.boardState[x][y].contains(ScreenGoController.CellState.WHITE)){
                    if(x == (int)controller.previousWhiteStone.x && y == (int)controller.previousWhiteStone.y && GameManager.INSTANCE.isAnimated()){
                        batch.draw(lastPlacedAnimationWhite.getKeyFrame(controller.animationTime), x, y, 1, 1);
                    }
                    else{
                        batch.draw(goPieceWhite,x,y,1,1);
                    }
                    //batch.draw(lastPlacedAnimationBlack.getKeyFrame(controller.animationTime), x, y, 1, 1);
                }
            }
        }
    }

    private void update() {
        hud.update();
    }


    public void dispose() {
        batch.dispose();
        sr.dispose();
        hud.dispose();
        log.debug("Dispose Screen");
    }

    public void resize(int width, int height) {
        viewportFont.update(width, height, true);
        boardV.update(width, height, true);
        controller.resize(boardCam,boardV,hud);
        hud.resize(width, height,boardCam,boardV);
    }


}

