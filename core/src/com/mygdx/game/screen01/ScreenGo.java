package com.mygdx.game.screen01;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.utils.Logger;
import com.mygdx.game.Go;

public class ScreenGo extends ScreenAdapter {
    public static final Logger log = new Logger(ScreenGo.class.getName(), Logger.DEBUG);
    ScreenGoController controller;
    ScreenGoRenderer renderer;

    Go gameMain;

    public ScreenGo(Go game) {
        this.gameMain = game;
        controller = new ScreenGoController(5,5, game.getAssetManager());
        renderer = new ScreenGoRenderer(gameMain,controller);
    }

    @Override
    public void show() {
        super.show();

    }


    public void inputHandle() {
        if (Gdx.input.isKeyPressed(Input.Keys.ESCAPE)){
            gameMain.safeExit();
        }
        else if(Gdx.input.isKeyJustPressed(Input.Keys.P)){
            log.info("PASSED");
            controller.pass();
        }
        else if(Gdx.input.isKeyJustPressed(Input.Keys.C) && controller.gameState == ScreenGoController.GameState.REMOVING_HOPELESS){
            log.info("END");
            controller.endGame();
        }

    }

    @Override
    public void render(float delta) {
        controller.update(delta);
        renderer.render(delta);
        inputHandle();
    }

    @Override
    public void hide() {
        log.debug("hide");
        dispose();
    }

    @Override
    public void dispose() {
        renderer.dispose();
    }

    @Override
    public void resize(int width, int height) {
        super.resize(width, height);
        renderer.resize(width, height);
    }
}
