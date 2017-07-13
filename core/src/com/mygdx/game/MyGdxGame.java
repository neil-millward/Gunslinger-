package com.mygdx.game;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

/**
 * 
 * @author Neil Millward P13197943
 *Class which sole purpose is to set the width and height for the game and to use the classes spriote batch
 *to draw all sprites/animations
 */
public class MyGdxGame extends Game {
	
	//used for drawing sprites and stages
	public SpriteBatch batch;
	
	//Width and height to use for cameras
	public static final int V_WIDTH = 400;
	public static final int V_HEIGHT = 208;
	

	
	//When class is ran, sets screen to the menu screen
	@Override
	public void create() {
		batch = new SpriteBatch();

		setScreen(new MenuScreen(this));

	}

	@Override
	public void render() {

		super.render();

	}

	public void changeScreen() {

	}

	public SpriteBatch getBatch() {
		return batch;
	}

	public void setBatch(SpriteBatch batch) {
		this.batch = batch;
	}

	@Override
	public void dispose() {
		batch.dispose();

	}
	
	public static int getvWidth() {
		return V_WIDTH;
	}

	public static int getvHeight() {
		return V_HEIGHT;
	}


}
