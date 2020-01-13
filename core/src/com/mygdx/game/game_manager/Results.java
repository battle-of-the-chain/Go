package com.mygdx.game.game_manager;


import java.util.ArrayList;

public class Results {
    public ArrayList<Result> all;

    public Results(){
        all = new ArrayList<>();
    }

    public void add(int scoreBlack, int scoreWhite){
        all.add(new Result(scoreBlack, scoreWhite));
    }

    public Result get(int index){
        return all.get(index);
    }

    public int size(){
        return all.size();
    }

}
