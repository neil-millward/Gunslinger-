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
/**
 * Bullet class that represents the games bullets
 * 
 * @author Neil P13197943
 *
 */
public class Bullets extends Movable {
	private int damage = 1;
	TextureRegion tR;

	TextureAtlas bulletAtlas;
	private MyGdxGame gdxGame;
	private boolean isPlayerBullet;
	
	/**
	 * Constructor:
	 * Sets up Box2D physics body for movement and collisions. 
	 * Animations are initialised.
	 * isPlayer bullet is set depending on enemy/player bullet
	 * isSensor is set so that bullets do not push back other bodies
	 *
	 * @param IsPlayerBullet used in order to determine if player or enemy shot a bullet. needed for collision detection to differentiate who shoots who
	 */
	public Bullets(boolean IsPlayerBullet){
		
		this.getBody().setBullet(true);
		this.getCircle().setRadius(3);
		this.getBodyDef().type = BodyDef.BodyType.DynamicBody;
		this.getBodyDef().gravityScale = 0;
		this.getFixtureDef().isSensor = true;
		isPlayerBullet = IsPlayerBullet;
		
		ObjectAnimation bulletAnimation = new ObjectAnimation("bulletTp.txt", "OrangeScale_",  new Vector2(82,37), 1, 1, 0.15f, true);
		bulletAnimation.setBounds(this.movablePosition.x, this.movablePosition.y, 8, 8);
		this.setAnimation(bulletAnimation);
	}

	/**
	 * Returns position of bullet
	 * @return position of bullet
	 */
	public Vector2 getBulletPosition() {
		return this.movablePosition;
	}
	

	
	/**
	 * 
	 * @return size of physics body
	 */
	public float getBodySize() {
		return this.getFixtureDef().shape.getRadius();
	}
	
	//returns texture region used for animations
	public TextureRegion gettR() {
		return this.tR;
	}
	
	//sets texture region
	public void settR(TextureRegion tR) {
		this.tR = tR;
	}
	
	

	// Sets user data to be Bullet Object
	@Override
	public void SetUserData()
	{
		getBody().setUserData(this);
	}

	/**
	 * Determines if bullet if shot from player
	 * @return determines if bullet if shot from player
	 */
	public boolean IsPlayerBullet() {
		return isPlayerBullet;
	}
	//returns damage of bullet
	public int getDamage() {
		return damage;
	}
	//sets damage of bullet
	public void setDamage(int damage) {
		this.damage = damage;
	}
	
}
