package com.mygdx.game.hud;

import com.badlogic.gdx.math.Rectangle;


public class GameObjectResetButton implements MyClickListener {

  public boolean scheduleReset; //something else can be change in world (simulation, ...)
  Rectangle bounds;


  public Rectangle getBounds() {
    return bounds;
  }

  public GameObjectResetButton(float x, float y, float w, float h) {
    scheduleReset = false;
    bounds = new Rectangle(x,y,w,h);
  }


  @Override
  public boolean onClickEvent(float x, float y) {
    if (!scheduleReset && bounds.contains(x, y)) {
      scheduleReset = true; //schedule for future, update method will use it
      return true;
    }
    return false;
  }

}
