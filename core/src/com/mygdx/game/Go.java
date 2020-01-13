package com.mygdx.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.mygdx.game.assets.AssetDescriptors;
import com.badlogic.gdx.utils.Logger;
import com.mygdx.game.game_manager.GameManager;
import com.mygdx.game.mainmenuscreen.MainMenuScreen;
import com.mygdx.game.screen01.ScreenGo;

public class Go extends Game {
	AssetManager assetManager;

	public AssetManager getAssetManager() {
		return assetManager;
	}
	public SpriteBatch batch;
	public BitmapFont font;
	
	@Override
	public void create () {
		batch = new SpriteBatch();
		font = new BitmapFont();
		assetManager = new AssetManager();
		Gdx.app.setLogLevel(Logger.DEBUG);

		assetManager.load(AssetDescriptors.GAME_PLAY);
		assetManager.load(AssetDescriptors.GAME_PLAY1);
		assetManager.load(AssetDescriptors.PLACE_SOUND);
		assetManager.load(AssetDescriptors.MENU_SKIN);
		assetManager.finishLoading();

		this.setScreen(new MainMenuScreen(this));
		//selectFirstScreen();
	}

	public void selectFirstScreen() {
		setScreen(new ScreenGo(this));
	}



	/*public void changeScreen(int screen){
		switch(screen){
			case MENU:
				if(menuScreen == null) menuScreen = new MenuScreen();
				this.setScreen(menuScreen);
				break;
			case PREFERENCES:
				if(preferencesScreen == null) preferencesScreen = new PreferencesScreen();
				this.setScreen(preferencesScreen);
				break;
			case APPLICATION:
				if(mainScreen == null) mainScreen = new MainScreen();
				this.setScreen(mainScreen);
				break;
			case ENDGAME:
				if(endScreen == null) endScreen = new EndScreen();
				this.setScreen(endScreen);
				break;
		}
	}*/

	public void safeExit() {
		Gdx.app.exit();
	}

	@Override
	public void render () {
		super.render();

		if(Gdx.input.isKeyJustPressed(Input.Keys.M)){
			this.setScreen(new MainMenuScreen(this));
		}
	}
	
	@Override
	public void dispose () {
		batch.dispose();
		font.dispose();
		//screen.dispose();
	}
}
