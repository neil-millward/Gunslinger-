package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.controllers.Controller;
import com.badlogic.gdx.controllers.ControllerListener;
import com.badlogic.gdx.controllers.Controllers;
import com.badlogic.gdx.controllers.PovDirection;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.Map;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.World;

/**
 * 
 * @author Neil Millward P13197943
 * This class is used to represent the users playable character in game
 * class inheritance movable<---character<---player
 * Class inherits physics body from movable, hp from character 
 */

public class Player extends Character implements ControllerListener {
	//states in order to determine what action the player is performing
	public enum State {
		FALLING, JUMPING, STANDING, RUNNING
	};
	//used for jump sound
	private Music music;
	//used to represent the current state the player is in (falling, jumping, standing, running)
	public State currentState;
	//used to represent the previous state the player is in (falling, jumping, standing, running)
	public State previousState;
	//animation object that represents the running animation
	private ObjectAnimation runAnimation;
	//animation object that represents the idle animation
	private ObjectAnimation idleAnimation;
	//animation object that represents the jump animation
	private ObjectAnimation jumpAnimation;
	
	private int shootingDistance;
	
	//level and world objects
	private Levels lvl;
	private World world;
	
	private boolean playerCanJump;
	private int jumpCount;
	public boolean isDead;
	
	/**
	 * Constructor:
	 * sets hp via super class character
	 * sets is dead to false as player is not dead when he spawns
	 * loads jump sound which is played when player jumps
	 * sets jump count to 0
	 * animations are set up
	 * idle animation set as null pointer is thrown other wise
	 * controller listener is implemented to listen for xbox 360 controller input from user
	 * @param level used for bullets to be added to physics world 
	 * @param World used for bullets to be added to physics world 
	 */
	public Player(Levels level, World World) {
		// using super class method to change hp from default 1 to 3
		super.setHp(50);
		isDead = false;
		this.movablePosition = new Vector2(0, 0);
		//music = Gdx.audio.newMusic(Gdx.files.internal("Jump.mp3"));
		this.getCircle().setRadius(10);
		
		// level and world used to add bullets to the physics world
		lvl = level;
		world = World;
		//boolean to determine if player can jump again (used for double jump mechanic)
		playerCanJump = true;
		
		jumpCount = 0;
		
		shootingDistance = 100;
		
		runAnimation = new ObjectAnimation("playerRunShootTp.txt", "Run_Shoot_One", new Vector2(435, 471), 4, 2, 0.15f,
				false);

		
		idleAnimation = new ObjectAnimation("playerIdleAimTP.txt", "Idle_Aim_One", new Vector2(428, 452), 4, 2, 0.15f,
				true);

		jumpAnimation = new ObjectAnimation("testTp.txt", "Jump_One", new Vector2(514, 497), 3, 3, 0.15f, false);
		setAnimation(idleAnimation);
		
		//FOR THE XBAWKS CONTROLLERU
		Controllers.addListener(this);

	}



	/**
	 * Method that controls the players movement.
	 * Linear impulses are applied to the players x/y axis based on keys pressed.
	 * The linear impulses are what moves the players object
	 * finally the state of the player is set by calling the get state method (run, jump, falling, idle)
	 * @param dt used in order to constantly update player position and listen for input
	 */
	public void control(float dt) {

		if (Gdx.input.isKeyPressed(Input.Keys.D)) {
			super.getBody().applyLinearImpulse(new Vector2(5, 0), getBody().getWorldCenter(), true);
		}
		if (Gdx.input.isKeyPressed(Input.Keys.A)) {
			
			super.getBody().applyLinearImpulse(new Vector2(-5, 0), getBody().getWorldCenter(), true);
		}
		if (Gdx.input.isKeyJustPressed(Input.Keys.W)  && playerCanJump) {
			
			if (getBody().getLinearVelocity().x != 0)
			{
				super.getBody().applyLinearImpulse(new Vector2(0, 150), getBody().getWorldCenter(), true);
			}
			else
			{
				super.getBody().applyLinearImpulse(new Vector2(0, 100), getBody().getWorldCenter(), true);
			}
			jumpCount += 1;
		}

		if (Gdx.input.isKeyJustPressed(Input.Keys.M)) {
			
				lvl.muteTracks();
		}
		
		currentState = getState();
	}
	/**
	 * Method that enables the player to attack
	 * shoots bullets in a direction dependent on button input.
	 * Applies linear impulse to bullet body which is then fired from the correct postion from the player
	 * @param dt used in order to constantly update bullet position and listen for input
	 * @param lvl
	 * @param b
	 */
	public void attack(float dt, Levels lvl, SpriteBatch b) {
		if (Gdx.input.isKeyJustPressed(Input.Keys.DPAD_RIGHT)) {
			Shoot(new Vector2(this.getBody().getPosition().x +15 ,this.getBody().getPosition().y), new Vector2(750,0));

		} else if (Gdx.input.isKeyJustPressed(Input.Keys.DPAD_LEFT)) {
			
			Shoot(new Vector2(this.getBody().getPosition().x - 15 ,this.getBody().getPosition().y), new Vector2(-750,0));

		} else if (Gdx.input.isKeyJustPressed(Input.Keys.DPAD_UP)) {
			Shoot(new Vector2(this.getBody().getPosition().x, this.getBody().getPosition().y + 15), new Vector2(0,750));
			
		} else if (Gdx.input.isKeyJustPressed(Input.Keys.DPAD_DOWN)) {;
		Shoot(new Vector2(this.getBody().getPosition().x, this.getBody().getPosition().y - 15), new Vector2(0,-750));
		}

	}

	public float getBodySize() {
		return getFixtureDef().shape.getRadius();
	}
	/**
	 * Method that returns the state of the player depending on x/y axis
	 * if y axis greater than 0 then player jumping, if x axis greater than 0 running right etc
	 * @return the current state the player is in (idle, run, jump, falling)
	 */
	public State getState() {
		if (getBody().getLinearVelocity().y > 0) {
			setAnimation(jumpAnimation);
			return State.JUMPING;
		} else if (getBody().getLinearVelocity().y < 0)
			return State.FALLING;
		else if (getBody().getLinearVelocity().x != 0) {
			setAnimation(runAnimation);
			return State.RUNNING;
		} else if (getBody().getLinearVelocity().x <= 0.2 && getBody().getLinearVelocity().x >= -0.2) {
			setAnimation(idleAnimation);
			return State.STANDING;
		} else
			return currentState;
	}

	@Override
	public void connected(Controller controller) {
		// TODO Auto-generated method stub

	}

	@Override
	public void disconnected(Controller controller) {
		// TODO Auto-generated method stub

	}
	
	/**
	 * Same as attack method except input is xbox controller button presses as opposed to keyboard input 
	 */
	@Override
	public boolean buttonDown(Controller controller, int buttonCode) {

		 if (buttonCode ==  Xbox360Pad.BUTTON_A)
		 {
			super.getBody().applyLinearImpulse(new Vector2(0,150), getBody().getWorldCenter(), true);
		} else if (buttonCode == Xbox360Pad.BUTTON_B){
			Shoot(new Vector2(this.getBody().getPosition().x +15 ,this.getBody().getPosition().y), new Vector2(500,0));
		}else if(buttonCode == Xbox360Pad.BUTTON_X){
			Shoot(new Vector2(this.getBody().getPosition().x - 15, this.getBody().getPosition().y), new Vector2(-500,0));
		}else if(buttonCode ==  Xbox360Pad.BUTTON_Y){
			Shoot(new Vector2(this.getBody().getPosition().x, this.getBody().getPosition().y + 15), new Vector2(0,500));
		}
		return false;

	}
	
	
	/**
	 * Method that shoots bullet by applying impulses (used in attack method and xbox control method)
	 * @param StartPosition postion where to shoot from
	 * @param LinearImpulse the impulse applied to the bullet in order to move it
	 */
	private void Shoot(Vector2 StartPosition, Vector2 LinearImpulse) {
		Bullets bullet = new Bullets(true);
		bullet.getBodyDef().position.x = StartPosition.x;
		bullet.getBodyDef().position.y = StartPosition.y;
		lvl.addBullets(bullet, world);
		bullet.getBody().applyLinearImpulse(new Vector2(LinearImpulse.x, LinearImpulse.y), bullet.movablePosition, true);
		
	}

	@Override
	public boolean buttonUp(Controller controller, int buttonCode) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean axisMoved(Controller controller, int axisCode, float value) {
		return false;
	}

	/**
	 * Same as control method except input is xbox controller button presses as opposed to keyboard input  
	 */
	@Override
	public boolean povMoved(Controller controller, int povCode, PovDirection value) {
		if (value == Xbox360Pad.BUTTON_DPAD_LEFT){
			super.getBody().applyLinearImpulse(new Vector2(-250, 0), getBody().getWorldCenter(), true);
		}
		if (value == Xbox360Pad.BUTTON_DPAD_RIGHT){
			super.getBody().applyLinearImpulse(new Vector2(250, 0), getBody().getWorldCenter(), true);
		}
		if (value == Xbox360Pad.BUTTON_DPAD_UP){
		}
		

		if (value == Xbox360Pad.BUTTON_DPAD_DOWN){
			
		}

		return false;

	}

	@Override
	public boolean xSliderMoved(Controller controller, int sliderCode, boolean value) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean ySliderMoved(Controller controller, int sliderCode, boolean value) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean accelerometerMoved(Controller controller, int accelerometerCode, Vector3 value) {
		// TODO Auto-generated method stub
		return false;
	}
	
	/**
	 * Overridden method from movable that sets the sprite over the physics body via movable position.
	 * Logic is also implemented to flip the sprites x axis dependent on which direction the player is facing.
	 * The double jump logic is implemented, it determines if the jump count is == or > than 2, if so then the player cannot jump again
	 * Finally getHp is evaluated to determine if the player is still alive
	 */
	@Override
	public void Update(float delta, SpriteBatch batch)
	{
		this.movablePosition = new Vector2(getBody().getPosition().x - (objectAnimation.getWidth() / 2), getBody().getPosition().y - (objectAnimation.getHeight() / 2));
		this.objectAnimation.setRegion(this.objectAnimation.getFrame(delta));
		if (this.objectAnimation != null)
		{
			if ((this.getBody().getLinearVelocity().x < 0) && !this.objectAnimation.isFlipX()) {
				// used to flip sprite. true on x axis, false on y
				this.objectAnimation.flip(true,false);
				setFacingRight(false);
			} else if ((this.getBody().getLinearVelocity().x > 0) && this.objectAnimation.isFlipX()) {
				this.objectAnimation.flip(true, false);
				setFacingRight(true);

			}
			
			this.objectAnimation.setPosition(this.movablePosition.x , this.movablePosition.y);
			this.objectAnimation.draw(batch);
		}	
		
		if (jumpCount >= 2)
		{
			playerCanJump = false;
		}
		
		if (this.currentState != State.JUMPING && this.currentState != State.FALLING)
		{
			playerCanJump = true;
			jumpCount = 0;
		}
		
		if (getHp() <= 0)
		{
			isDead = true;
		}
	}
	
	// Sets user data to be Player Object
	@Override
	public void SetUserData()
	{
		getBody().setUserData(this);
	}
	
	public int getShootingDisatance() {
		return shootingDistance;
	}

	public void setShootingDisatance(int shootingDisatance) {
		this.shootingDistance = shootingDisatance;
	}
	
	
}
