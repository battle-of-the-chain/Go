package com.mygdx.game.leaderboardscreen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.List;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.mygdx.game.Go;
import com.mygdx.game.assets.AssetDescriptors;
import com.mygdx.game.game_manager.GameManager;
import com.mygdx.game.game_manager.Results;
import com.mygdx.game.mainmenuscreen.MainMenuScreen;
import com.mygdx.game.screen01.ScreenGo;

public class LeaderboardScreen implements Screen {

    final Go game;
    private OrthographicCamera camera;
    private Stage stage;
    private Skin skin;
    private List<String> list;
    private Results results;
    private ScrollPane scrollPane;
    private TextButton back;


    public LeaderboardScreen(final Go game){
        this.game = game;
        stage = new Stage(new FitViewport(700,700));

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
        stage.getViewport().update(width, height);
    }

    @Override
    public void show(){
        Gdx.input.setInputProcessor(stage);

        list = new List<String>(skin);
        //list.setItems(new String[] {"onetwoeajngjanjgnjaknfjanndjnjnjnjn","two","three", "one","two","three", "one","two","three", "one","two","three", "one","two","three", "one","two","three", "one","two","three", "one","two","three", "one","two","three", "one","two","three", "one","two","three"});

        results = GameManager.INSTANCE.getResults();

        String resultsStringArray[] = new String[results.size()];
        for(int i = 0; i < results.size(); i++){
            resultsStringArray[i] = results.get(i).toString();
        }
        list.setItems(resultsStringArray);

        scrollPane = new ScrollPane(list, skin);
        scrollPane.setFadeScrollBars(false);

        back = new TextButton("Back", skin);
        back.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                game.setScreen(new MainMenuScreen(game));
            }
        });


        Table table = new Table(skin);
        table.setFillParent(true);
        table.setDebug(true);
        stage.addActor(table);

        table.add("HISTORY").fillX().uniformX().row();
        table.add(scrollPane).fillX().uniformX().row();
        table.add(back);

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
        stage.dispose();
        skin.dispose();
    }



}


