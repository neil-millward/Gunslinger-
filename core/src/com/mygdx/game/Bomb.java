/**
 * @author Neil Millward P13197943
 * 
 * This class represents a 'bomb' damage object. This objects sole use is for one of the 3
 * 'boss' mechanics
 */
package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.BodyDef;

public class Bomb extends Movable {
	private int damage = 2;
	TextureRegion tR;
	private float dropSpeed;

	TextureAtlas bulletAtlas;
	private MyGdxGame gdxGame;
	
	/**constructor
	*Constructs physics body and animation
	*/
	public Bomb(){
		
		this.getBody().setBullet(true);
		this.getCircle().setRadius(10);
		this.movablePosition = new Vector2(0, 0);
		this.getBodyDef().type = BodyDef.BodyType.DynamicBody;
		this.getBodyDef().gravityScale = 0;
		this.getFixtureDef().isSensor = true;
		dropSpeed = 0.1f;
		
		ObjectAnimation bulletAnimation = new ObjectAnimation("bulletTp.txt", "OrangeScale_",  new Vector2(82,37), 1, 1, 0.15f, true);
		bulletAnimation.setBounds(0, 0, 15, 15);
		this.setAnimation(bulletAnimation);
	}

	//returns movable position of this object
	public Vector2 getBombPosition() {
		return this.movablePosition;
	}
	
	
	public void update(float dt,SpriteBatch batch){
	}
	
	public float getBodySize() {
		return this.getFixtureDef().shape.getRadius();
	}
	
	public TextureRegion gettR() {
		return this.tR;
	}

	public void settR(TextureRegion tR) {
		this.tR = tR;
	}
	
	/**
	 * Method that drops bomb on player, the speed that the bomb drops slowly increases over time
	 * @param delta used to continuously update bomb speed and set the new position
	 */
	public void MoveBomb(float delta)
	{
			
			dropSpeed += 0.001;
			getBody().applyLinearImpulse(new Vector2(0, -1 * dropSpeed), movablePosition, true);
			this.movablePosition = new Vector2(this.getBody().getWorldCenter().x * 32, this.getBody().getWorldCenter().y *32);
	}

	// Sets user data to be bomb object
	@Override
	public void SetUserData()
	{
		getBody().setUserData(this);
	}
	//returns the damage of the bomb
	public int getDamage() {
		return damage;
	}
	
	//sets the damage of the bomb
	public void setDamage(int damage) {
		this.damage = damage;
	}
	
}
