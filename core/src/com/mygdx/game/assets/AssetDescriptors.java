package com.mygdx.game.assets;

import com.badlogic.gdx.assets.AssetDescriptor;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

public class AssetDescriptors {

    public static final AssetDescriptor<BitmapFont> FONT =
            new AssetDescriptor<BitmapFont>(AssetPaths.UI_FONT, BitmapFont.class);

    public static final AssetDescriptor<TextureAtlas> GAME_PLAY =
            new AssetDescriptor<TextureAtlas>(AssetPaths.GAME_PLAY, TextureAtlas.class);

    public static final AssetDescriptor<TextureAtlas> GAME_PLAY1 =
            new AssetDescriptor<TextureAtlas>(AssetPaths.GAME_PLAY1, TextureAtlas.class);

    public static final AssetDescriptor<Sound> PLACE_SOUND =
            new AssetDescriptor<Sound>(AssetPaths.PLACE_STONE_SOUND, Sound.class);

    public static final AssetDescriptor<Skin> MENU_SKIN=
            new AssetDescriptor<Skin>(AssetPaths.MENU_SKIN, Skin.class);


    private AssetDescriptors() {}
}
