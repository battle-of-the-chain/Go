package com.mygdx.game.game_manager;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Json;
import com.mygdx.game.Go;

public class GameManager {
    private static final String ANIMATED = "Animated";
    private static final String SOUND = "Sound";
    //public String userID;
    private Preferences PREFS;
    public Results results;
    public static final GameManager INSTANCE = new GameManager();

    public void saveResults() {
        json=new Json();
        FileHandle file = Gdx.files.local("results.json");
        file.writeString(json.toJson(results), false);
    }
    Json json;

    public void loadResults() {
        json=new Json();
        FileHandle file = Gdx.files.local("results.json");
        if (!file.exists()) {
            results = new Results();
        } else {
            results = json.fromJson(Results.class, file.readString());
        }
    }
    private GameManager() {
        json = new Json();
        PREFS = Gdx.app.getPreferences(Go.class.getSimpleName());
        loadResults();
    }

    public void addResult(int scoreBlack, int scoreWhite) {
        Gdx.app.log("save result", "Black:"  + scoreBlack + " White: " + scoreWhite);
        results.add(scoreBlack,scoreWhite);
        saveResults();

    }


    public Results getResults(){
        return results;
    }
    public Result getResult(int index) {
        return results.all.get(index);
    }

    public boolean isAnimated() {
        return PREFS.getBoolean(ANIMATED, true);
    }
    public void setAnimated(boolean checked) {
        PREFS.putBoolean(ANIMATED,checked);
        PREFS.flush();
    }
    public void setSound(boolean checked) {
        PREFS.putBoolean(SOUND,checked);
        PREFS.flush();
    }
    public boolean isSound() {
        return PREFS.getBoolean(SOUND, true);
    }
}
