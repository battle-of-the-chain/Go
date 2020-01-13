package com.mygdx.game.game_manager;

public class Result {

    public int scoreBlack;
    public int scoreWhite;

    public Result(int scoreBlack, int scoreWhite) {
        this.scoreBlack = scoreBlack;
        this.scoreWhite = scoreWhite;
    }

    public Result(){

    }

    @Override
    public String toString(){
        return "Black: " + scoreBlack + " White: " + scoreWhite ;
    }
}
