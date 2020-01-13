package com.mygdx.game.screen01;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.input.GestureDetector;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Logger;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.mygdx.game.assets.AssetDescriptors;
import com.mygdx.game.game_manager.GameManager;
import com.mygdx.game.hud.HUD;
import com.mygdx.game.hud.MyClickListener;
import com.mygdx.game.util.GdxUtils;


import java.util.EnumSet;


public class ScreenGoController implements GestureDetector.GestureListener {
    private static final Logger log = new Logger("ScreenGoController", Logger.DEBUG);
    private GestureDetector gestureDetector;
    Array<MyClickListener> listeners;
    private Sound placeStoneSound;
    public Viewport boardViewport;
    HUD hudRef;
    public int width;
    public int height;
    private int capturedBlack = 0;
    private int capturedWhite = 0;
    private int territoryBlack = 0;
    private int territoryWhite = 0;
    private int scoreBlack = 0;
    private int scoreWhite = 0;
    private int consecutiveKo = 0;
    private int consecutivePass = 0;
    private int territorySize = 0;
    boolean foundWhite = false;
    boolean foundBlack = false;

    public float animationTime = 0;

    public void update(float delta) {
            animationTime += delta;
            if(animationTime > 11){
                animationTime = 0;
            }
    }

    public enum GameState{
        BEGIN,
        RUNNING,
        REMOVING_HOPELESS,
        END
    }

    public GameState gameState;

    public enum CurrentPlayer{
        BLACK,
        WHITE
    }

    CurrentPlayer currentPlayer;
    public enum CellState{
        EMPTY,
        BLACK,
        WHITE,
        HAS_LIBERTY,
        NO_LIBERTY
    }

    //Vector2 mostRecentStone;
    public Vector2 previousWhiteStone = new Vector2(-1,-1);
    public Vector2 previousBlackStone = new Vector2(-1,-1);

    public EnumSet<CellState>[][] boardState;
    EnumSet<CellState>[][] boardStateBeforePlace;

    Integer[][] territoryNumber;

    public ScreenGoController(int width, int height, AssetManager assetManager) {
        this.boardState = (EnumSet<CellState>[][]) new EnumSet<?>[width][height];
        this.width = width;
        this.height = height;
        territoryNumber = new Integer[width][height];

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                boardState[x][y] = EnumSet.allOf(CellState.class);
            }
        }

        clearAndInit();
        listeners = new Array<>();
        this.currentPlayer = CurrentPlayer.BLACK;

        boardStateBeforePlace = new EnumSet[width][height];
        placeStoneSound = assetManager.get(AssetDescriptors.PLACE_SOUND);
    }

    public void resize(Camera boardCam, Viewport vp, HUD hud) {
        this.boardViewport = vp;
        if (this.hudRef != null) listeners.removeValue(this.hudRef, false);
        this.hudRef = hud;
        listeners.add(hud);
        Vector3 tmp = new Vector3(1, 0, 0);
        GdxUtils.ProjectWorldCoordinatesInScreenCoordinates(boardCam, tmp);
        gestureDetector = new GestureDetector(tmp.x / 2.5f, 0.2f, 0.5f, 0.5f, this);
        Gdx.input.setInputProcessor(gestureDetector);
    }

    public void clearBoard() {
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                boardState[x][y].clear(); //ADD
                boardState[x][y].add(CellState.EMPTY);
            }
        }
    }
    public void clearAndInit(){
        clearBoard();
    }

    public void remove(int x, int y){
       if(boardState[x][y].contains(CellState.BLACK)){
           boardState[x][y].remove(CellState.BLACK);
           boardState[x][y].add(CellState.EMPTY);
       }
       else if(boardState[x][y].contains(CellState.WHITE)){
            boardState[x][y].remove(CellState.WHITE);
            boardState[x][y].add(CellState.EMPTY);
        }

    }
    public void place(int x , int y){
        consecutivePass = 0; //if stone is placed, it's not a pass
        if(boardState[x][y].contains(CellState.EMPTY)){
            if(GameManager.INSTANCE.isSound()){
                placeStoneSound.play();
            }
            if(currentPlayer == CurrentPlayer.BLACK){
                boardState[x][y].clear();
                boardState[x][y].add(CellState.BLACK);
                //currentPlayer = CurrentPlayer.WHITE;
                previousBlackStone = new Vector2(x,y);
            }
            else{
                boardState[x][y].clear();
                boardState[x][y].add(CellState.WHITE);
                //currentPlayer = CurrentPlayer.BLACK;
                previousWhiteStone = new Vector2(x,y);
            }
            switchPlayer();
           // mostRecentStone = new Vector2(x,y);
        }

        updateBoardState(x,y);


    }


    public void newGame() {
        hudRef.start();
        gameState = GameState.BEGIN;
        clearAndInit();
        //reset values for a new game
        currentPlayer = CurrentPlayer.BLACK;
        capturedBlack = 0;
        capturedWhite = 0;
        territoryBlack = 0;
        territoryWhite = 0;
        consecutiveKo = 0;
        consecutivePass = 0;
        scoreBlack = 0;
        scoreWhite = 0;
    }

    public void endGame(){
        //set score
        countTerritory();
        scoreBlack = territoryBlack + capturedBlack;
        scoreWhite = territoryWhite + capturedWhite;
        //display score
        hudRef.updateTerritory(scoreBlack, scoreWhite);

        GameManager.INSTANCE.addResult(scoreBlack, scoreWhite);
        GameManager.INSTANCE.saveResults();
    }

    void switchPlayer(){
        if(currentPlayer == CurrentPlayer.BLACK){
            currentPlayer = CurrentPlayer.WHITE;
        }
        else{
            currentPlayer = CurrentPlayer.BLACK;
        }
    }

    void pass(){
        if(currentPlayer == CurrentPlayer.BLACK){
            capturedWhite++;
        }
        else{
            capturedBlack++;
        }
        consecutivePass++;
        if(consecutivePass == 2){
            gameState = GameState.REMOVING_HOPELESS;
            //gameState = GameState.END;
            //log.info("END GAME");
        }
        switchPlayer();
    }

    boolean areLibertiesSame(EnumSet<CellState>[][] boardState1,EnumSet<CellState>[][] boardState2 ){
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                if(boardState1[x][y].contains(CellState.HAS_LIBERTY) && boardState2[x][y].contains(CellState.NO_LIBERTY)){
                    return false;
                }
                if(boardState1[x][y].contains(CellState.NO_LIBERTY) && boardState2[x][y].contains(CellState.HAS_LIBERTY)){
                    return false;
                }
            }
        }
        return true;
    }

    boolean areAnyOppositeCaptured(){
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                //Current player is reversed because of place!!!
                //Does the opposite color have any without liberty?
                if(currentPlayer == CurrentPlayer.BLACK){
                    if(boardState[x][y].contains(CellState.NO_LIBERTY) && boardState[x][y].contains(CellState.BLACK)){
                       return true;
                    }
                }
                if(currentPlayer == CurrentPlayer.WHITE){
                    if(boardState[x][y].contains(CellState.NO_LIBERTY) && boardState[x][y].contains(CellState.WHITE)){
                        return true;
                    }
                }
            }
        }
        return false;
    }

    boolean isMoveKo(){
        boolean isKo = false;
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                //Current player is reversed because of place!!!
                //Does the opposite color have any without liberty?
                if(currentPlayer == CurrentPlayer.BLACK){
                    if(boardState[x][y].contains(CellState.NO_LIBERTY) && boardState[x][y].contains(CellState.BLACK)){
                        if(isKo){
                            return false;
                        }
                        isKo = true;
                    }
                }
                if(currentPlayer == CurrentPlayer.WHITE){
                    if(boardState[x][y].contains(CellState.NO_LIBERTY) && boardState[x][y].contains(CellState.WHITE)){
                        if(isKo){
                            return false;
                        }
                        isKo = true;
                    }
                }
            }
        }
        return isKo;
    }

    public void updateBoardState(int lastPlacedStoneX, int lastPlacedStoneY){
        //prior to scan
        setStartingLiberties();

        EnumSet<CellState>[][] boardStateAtStartOfLibertySearching = new EnumSet[width][height];
        EnumSet<CellState>[][] boardStateBeforeUpdatingLiberties = new EnumSet[width][height];
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                boardStateBeforeUpdatingLiberties[x][y] = boardState[x][y].clone();
            }
        }
        //Liberty searching
        do{
            for (int y = 0; y < height; y++) {
                for (int x = 0; x < width; x++) {
                    boardStateAtStartOfLibertySearching[x][y] = boardState[x][y].clone();
                }
            }

            addLibertyToAllAdjacentOfHasLiberty();
            //log.info("Searching");
        }while(!areLibertiesSame(boardState, boardStateAtStartOfLibertySearching));
        //log.info("FINISHED SEARCHING");

        //self capture prevention
        //if self captured
        if(boardState[lastPlacedStoneX][lastPlacedStoneY].contains(CellState.NO_LIBERTY)){
            //Are any opposite captured?
            if(areAnyOppositeCaptured()){
                //Is it a Ko situation?
                if(isMoveKo()){
                    consecutiveKo++;
                }
                else{
                    consecutiveKo = 0;
                }
                if(consecutiveKo < 2){
                    //Let the place happen
                    boardState[lastPlacedStoneX][lastPlacedStoneY].remove(CellState.NO_LIBERTY);
                    boardState[lastPlacedStoneX][lastPlacedStoneY].add(CellState.HAS_LIBERTY);
                    removeAllWithNoLiberties();
                }
                else{
                    //ELSE prevent repeated Ko by
                    //setting board to state before the last stone was placed
                    for (int y = 0; y < height; y++) {
                        for (int x = 0; x < width; x++) {
                            boardState[x][y] = boardStateBeforePlace[x][y].clone();
                        }
                    }
                    //current player should be the one that tried to place (placing usually switches players)
                    switchPlayer();
                }
            }
            else{
                //prevent self capture by
                //setting board to state before the last stone was placed
                for (int y = 0; y < height; y++) {
                    for (int x = 0; x < width; x++) {
                        boardState[x][y] = boardStateBeforePlace[x][y].clone();
                    }
                }
                //current player should be the one that tried to place (placing usually switches players)
               switchPlayer();
            }

        }
        else{
            consecutiveKo = 0;
            removeAllWithNoLiberties();
        }

       // countTerritory();
    }



    /* set occupied to no liberty
        unoccupied to has liberty
     */
    public void setStartingLiberties(){
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                if(boardState[x][y].contains(CellState.BLACK) || boardState[x][y].contains(CellState.WHITE)){
                    boardState[x][y].add(CellState.NO_LIBERTY);
                    boardState[x][y].remove(CellState.HAS_LIBERTY);
                }
                else{
                    boardState[x][y].add(CellState.HAS_LIBERTY);
                    boardState[x][y].remove(CellState.NO_LIBERTY);
                }
            }
        }
    }

    public void addLibertyToAdjacentOfHasLibertyPosition(int x, int y, CellState cellState){
        //UP
        if(y+1 < height){
            if(boardState[x][y+1].contains(cellState) || boardState[x][y].contains(CellState.EMPTY)) {
                boardState[x][y+1].add(CellState.HAS_LIBERTY);
                boardState[x][y+1].remove(CellState.NO_LIBERTY);
            }
        }
        //RIGHT
        if(x+1 < width){
            if(boardState[x+1][y].contains(cellState) || boardState[x][y].contains(CellState.EMPTY)) {
                boardState[x+1][y].add(CellState.HAS_LIBERTY);
                boardState[x+1][y].remove(CellState.NO_LIBERTY);
            }
        }
        //LEFT
        if(x-1 >= 0){
            if(boardState[x-1][y].contains(cellState) || boardState[x][y].contains(CellState.EMPTY)) {
                boardState[x-1][y].add(CellState.HAS_LIBERTY);
                boardState[x-1][y].remove(CellState.NO_LIBERTY);
            }
        }
        //DOWN
        if(y-1 >= 0){
            if(boardState[x][y-1].contains(cellState) || boardState[x][y].contains(CellState.EMPTY)) {
                boardState[x][y-1].add(CellState.HAS_LIBERTY);
                boardState[x][y-1].remove(CellState.NO_LIBERTY);
            }
        }
    }

    public void addLibertyToAllAdjacentOfHasLiberty() {

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                // log.info("At: " + x + y);
                // log.info(boardState[x][y].toString());
                if (boardState[x][y].contains(CellState.HAS_LIBERTY)) {
                    //is current position occupied with black?
                    if (boardState[x][y].contains(CellState.BLACK)) {
                        //log.info("BLACK ADDING LIBERTIES " + x  +" "+ y);
                        //mark adjacent of the same color
                        addLibertyToAdjacentOfHasLibertyPosition(x, y, CellState.BLACK);
                    }


                    //is current position occupied with white?
                    else if (boardState[x][y].contains(CellState.WHITE)) {
                        addLibertyToAdjacentOfHasLibertyPosition(x, y, CellState.WHITE);
                    }
                    //current is unoccupied
                    else if (boardState[x][y].contains(CellState.EMPTY)) {
                        addLibertyToAdjacentOfHasLibertyPosition(x, y, CellState.EMPTY);
                    }//end if has liberty
                }
            }
        }
    }

    void removeAllWithNoLiberties(){
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                if(boardState[x][y].contains(CellState.NO_LIBERTY)){
                    if(boardState[x][y].contains(CellState.BLACK)){
                        //White captured black
                        capturedWhite++;
                        log.info("White captured black at x: " + x + " y: " + y +" White points: " + capturedWhite);
                    }
                    else if(boardState[x][y].contains(CellState.WHITE)){
                        //Black captured white
                        capturedBlack++;
                        log.info("Black captured white at x: " + x + " y: " + y +" Black points: " + capturedBlack);
                    }
                    boardState[x][y].clear();
                    boardState[x][y].add(CellState.EMPTY);
                }
            }
        }
    }


    /*void floodFill(int x, int y, CellState cellStateTargetColor, int newTerritoryNumber){
        if(x >= width || x < 0 || y >= height || y < 0){
            return;
        }
        if(!boardState[x][y].contains(cellStateTargetColor) || territoryNumber[x][y] == newTerritoryNumber){
            return;
        }
        else{
            territoryNumber[x][y] = newTerritoryNumber;
            log.info("Added territory: " + newTerritoryNumber + " to X: " + x + " Y: " + y);
        }

        //UP
        floodFill(x,y+1, cellStateTargetColor, newTerritoryNumber);
        //DOWN
        floodFill(x,y-1, cellStateTargetColor, newTerritoryNumber);
        //LEFT
        floodFill(x-1,y, cellStateTargetColor, newTerritoryNumber);
        //RIGHT
        floodFill(x+1,y, cellStateTargetColor, newTerritoryNumber);


    }*/


    void floodFill(int x, int y, CellState cellStateTargetColor, int newTerritoryNumber){
        if(x >= width || x < 0 || y >= height || y < 0){
            //izven plošče
            return;
        }
        else if(territoryNumber[x][y] == newTerritoryNumber){
            //že obiskan
            return;
        }
        if(boardState[x][y].contains(CellState.BLACK)){
            foundBlack = true;
            return;
        }
        else if(boardState[x][y].contains(CellState.WHITE)){
            foundWhite = true;
            return;
        }
        else{
            territoryNumber[x][y] = newTerritoryNumber;
            territorySize++;
            log.info("Added territory: " + newTerritoryNumber + " to X: " + x + " Y: " + y);
            //UP
            floodFill(x,y+1, cellStateTargetColor, newTerritoryNumber);
            //DOWN
            floodFill(x,y-1, cellStateTargetColor, newTerritoryNumber);
            //LEFT
            floodFill(x-1,y, cellStateTargetColor, newTerritoryNumber);
            //RIGHT
            floodFill(x+1,y, cellStateTargetColor, newTerritoryNumber);
        }
    }

    void floodFillTerritoryAndFindBordering(int x, int y){
        if(x >= width || x < 0 || y >= height || y < 0){
            return;
        }
        else if(boardState[x][y].contains(CellState.WHITE)){
            foundWhite = true;
            return;
        }
        else if(boardState[x][y].contains(CellState.BLACK)){
            foundBlack = true;
            return;
        }

        //UP
        floodFillTerritoryAndFindBordering(x,y+1);
        //DOWN
        floodFillTerritoryAndFindBordering(x,y-1);
        //LEFT
        floodFillTerritoryAndFindBordering(x-1,y);
        //RIGHT
        floodFillTerritoryAndFindBordering(x+1,y);
    }

    void countTerritory(){

        territoryNumber = new Integer[width][height];
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                territoryNumber[x][y] = 0;
            }
        }


        territoryBlack = 0;
        territoryWhite = 0;
        int newTerritoryNumber = 0;

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                if (territoryNumber[x][y] == 0 && boardState[x][y].contains(CellState.EMPTY)) {
                    newTerritoryNumber++;
                    territorySize = 0;
                    foundBlack = false;
                    foundWhite = false;
                    floodFill(x, y, CellState.EMPTY, newTerritoryNumber);
                    log.info("Velikost teritorija:" + territorySize);
                    if(foundBlack && !foundWhite){
                        territoryBlack += territorySize;
                    }
                    else if(!foundBlack && foundWhite){
                        territoryWhite += territorySize;
                    }
                }
            }
        }

        log.info("Black territory: " + territoryBlack);
        log.info("White territory: " + territoryWhite);
    }




    @Override
    public boolean touchDown(float x, float y, int pointer, int button) {
        return false;
    }

    public void clicked(float sx, float sy, boolean longClicked){
        log.info("User tapped at - X: " + sx + " Y:" + sy);
        Vector3 touchPoint = new Vector3(sx,sy,0);
        boardViewport.unproject(touchPoint);
        for (MyClickListener item : listeners) item.onClickEvent(touchPoint.x, touchPoint.y); //notifyAll
        if(gameState == GameState.END){
            return;
        }
        if (gameState == GameState.BEGIN) {
            gameState = GameState.RUNNING;
        }
        int x = (int) touchPoint.x;
        int y = (int) touchPoint.y;
        log.debug("Clicked:" + x + ", " + y);
        if ((x > width-1) || (y > height-1) || (x < 0) || (y < 0)){
            return;
        }

        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                boardStateBeforePlace[i][j] = boardState[i][j].clone();
            }
        }
        if(gameState == GameState.REMOVING_HOPELESS){
            remove(x,y);
            log.info("Removed at - X: " + (int)touchPoint.x + " Y: " + (int)touchPoint.y);
        }else{
            place(x, y);
            log.info("Placed at - X: " + (int)touchPoint.x + " Y: " + (int)touchPoint.y);
        }
    }
    @Override
    public boolean tap(float x, float y, int count, int button) {
        clicked(x, y, false);
        return true;
    }

    @Override
    public boolean longPress(float x, float y) {
       return false;
    }


    @Override
    public boolean fling(float velocityX, float velocityY, int button) {
        return false;
    }

    @Override
    public boolean pan(float x, float y, float deltaX, float deltaY) {
        return false;
    }

    @Override
    public boolean panStop(float x, float y, int pointer, int button) {
        return false;
    }

    @Override
    public boolean zoom(float initialDistance, float distance) {
        return false;
    }

    @Override
    public boolean pinch(Vector2 initialPointer1, Vector2 initialPointer2, Vector2 pointer1, Vector2 pointer2) {
        return false;
    }

    @Override
    public void pinchStop() {

    }

}
