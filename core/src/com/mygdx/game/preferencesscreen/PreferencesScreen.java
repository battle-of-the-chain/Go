package com.mygdx.game.preferencesscreen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.ButtonGroup;
import com.badlogic.gdx.scenes.scene2d.ui.CheckBox;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.mygdx.game.Go;
import com.mygdx.game.assets.AssetDescriptors;
import com.mygdx.game.game_manager.GameManager;
import com.mygdx.game.leaderboardscreen.LeaderboardScreen;
import com.mygdx.game.mainmenuscreen.MainMenuScreen;
import com.mygdx.game.screen01.ScreenGo;

public class PreferencesScreen implements Screen{

        final Go game;
        private OrthographicCamera camera;
        private Stage stage;
        private Skin skin;


        public PreferencesScreen(final Go game){
            this.game = game;
            stage = new Stage(new ScreenViewport());

            game.getAssetManager().load(AssetDescriptors.MENU_SKIN);
            skin = game.getAssetManager().get(AssetDescriptors.MENU_SKIN);
        }

        @Override
        public void render(float delta){
            Gdx.gl.glClearColor(0,0,0.2f,1);
            Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

            stage.act(Math.min(Gdx.graphics.getDeltaTime(), 1/30f));
            stage.draw();

        }

        @Override
        public void resize(int width, int height){

        }

        @Override
        public void show(){
            Gdx.input.setInputProcessor(stage);
            Table table = new Table();
            table.setFillParent(true);
            table.setDebug(false);
            stage.addActor(table);


            TextButton back= new TextButton("Back", skin);

            final CheckBox checkBoxSound = new CheckBox("Sound", skin);
            checkBoxSound.setChecked(GameManager.INSTANCE.isSound());
            final CheckBox checkBoxAnimation = new CheckBox("Animation", skin);
            checkBoxAnimation.setChecked(GameManager.INSTANCE.isAnimated());




            table.add(checkBoxSound).fillX().uniformX();
            table.row().pad(10,0,10,0);
            table.add(checkBoxAnimation).fillX().uniformX();
            table.row();
            table.add(back).fillX().uniformX();



            checkBoxSound.addListener(new ChangeListener() {
                @Override
                public void changed(ChangeEvent event, Actor actor) {
                    GameManager.INSTANCE.setSound(checkBoxSound.isChecked());
                }
            });

            checkBoxAnimation.addListener(new ChangeListener() {
                @Override
                public void changed(ChangeEvent event, Actor actor) {
                    GameManager.INSTANCE.setAnimated(checkBoxAnimation.isChecked());
                }
            });

            back.addListener(new ChangeListener() {
                @Override
                public void changed(ChangeEvent event, Actor actor) {
                    game.setScreen(new MainMenuScreen(game));
                }
            });


        }

        @Override
        public void hide(){

        }

        @Override
        public void pause(){

        }

        @Override
        public void resume(){

        }

        @Override
        public void dispose(){

        }

}
