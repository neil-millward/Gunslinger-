

package com.mygdx.game;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.World;

/**
 * @author Neil Millward
 * super class used to implement physics bodies, used also for  positioning and applying Sprites. 
 * 
 * character/enemies/bullet/player/boss inherit from this class as they all require physics bodies for movement.
 * The class also extends sprite for the relevant methods required to implement sprites into the game
 */

public class Movable extends Sprite {
//box2D variables
	private Body body;
	private BodyDef bodyDef;
	private FixtureDef fixtureDef;
	private CircleShape circle;
	private World world;
	public ObjectAnimation objectAnimation;
	public Vector2 movablePosition;
	private boolean facingRight;
	private float overallTime;
	
	/**
	 * sets up standard physics body that can be modified by classes that inherit from this class 
	 */
	public Movable() {
		world = new World(new Vector2(0, 0), true);
		bodyDef = new BodyDef();
		bodyDef.type = BodyDef.BodyType.DynamicBody;
		//sets position of physics body
		bodyDef.position.set(200, 43);
		fixtureDef = new FixtureDef();
		circle = new CircleShape();
		circle.setRadius(100);		
		fixtureDef.shape = circle;
		body = world.createBody(bodyDef);
		body.createFixture(fixtureDef);
		objectAnimation = new ObjectAnimation();
		facingRight = true;
		movablePosition = new Vector2(0,0);
		overallTime = 0;
	}




	/**
	 * Method used to continuously update players position and animation position
	 * animations are also drawn here
	 * @param delta used as time is needed for certain methods to function. i.e. determining what animation to set depending on actions taken etc
	 * @param batch sprite batch used to draw the animations
	 */
	public void Update(float delta, SpriteBatch batch)
	{	
		movablePosition = new Vector2(getBody().getPosition().x - (objectAnimation.getWidth() / 2), getBody().getPosition().y - (objectAnimation.getHeight() / 2));
		this.objectAnimation.setRegion(this.objectAnimation.getFrame(delta));
		if (this.objectAnimation != null)
		{
			if (this.facingRight == true) {
				// used to flip sprite. true on x axis, false on y
				if (this.objectAnimation.isFlipX())
				{
					this.objectAnimation.flip(true,false);
				}
			} else if (facingRight == false) {
				
				if (!this.objectAnimation.isFlipX())
				{
					this.objectAnimation.flip(true,false);
				}
			}
				this.objectAnimation.setPosition(this.movablePosition.x , this.movablePosition.y);
				this.objectAnimation.draw(batch);
		}	
	}
	
	public Vector2 getMovablePosition() {
		return movablePosition;
	}

	/**
	 * Takes the passed in x and y co-ordinates and sets movablePosition to these values
	 * 
	 * @param posX
	 * @param posY
	 */
	public void setMovablePosition(float posX, float posY) {
		this.movablePosition = new Vector2(posX, posY);
		
	}


	
	
	//getters and setters
	//----------------------------------------------------
	public World getWorld() {
		return world;
	}

	public void setWorld(World world) {
		this.world = world;
	}
	
	public Body getBody() {
		return body;
	}

	public void setBody(Body body) {
		this.body = body;
		
	}

	public BodyDef getBodyDef() {
		return bodyDef;
	}

	public void setBodyDef(BodyDef bodyDef) {
		this.bodyDef = bodyDef;
	}

	public FixtureDef getFixtureDef() {
		return fixtureDef;
	}

	public void setFixtureDef(FixtureDef fixtureDef) {
		this.fixtureDef = fixtureDef;
	}

	public CircleShape getCircle() {
		return circle;
	}

	public void setCircle(CircleShape circle) {
		this.circle = circle;
	}
	
	public void setAnimation(ObjectAnimation newAnimation)
	{
		this.objectAnimation = newAnimation;
	}
	public void setFacingRight(Boolean isRight)
	{
		this.facingRight = isRight;
	}
	
	public void SetUserData()
	{
		
	}
	
	public boolean IsFacingRight()
	{
		return facingRight;
	}
	//----------------------------------------------------
}
