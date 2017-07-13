package com.mygdx.game;

import java.util.Random;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.World;
/**
 * 
 * @author Neil Millward P13197943
 * 
 * This class represents the 'Boss' enemy object that is present at the end of each level. 
 *
 */


public class BossEnemy extends Enemies{
	Vector2 enemyPosition;
	private int shootingDistance = 300;
	private int shootingDelayAbilityOne = 2;
	private int shootingDelayAbilityTwo = 5;
	private int shootingDelayAbilityThree = 2;
	private float timePlayerIsVisibleOne;
	private float timePlayerIsVisibleTwo;
	private float timePlayerIsVisibleThree;
	private boolean hasAbilityOne = false;
	private boolean hasAbilityTwo = false;
	private boolean hasAbilityThree = false;
	public Boolean isDead;
	private ObjectAnimation runAnimation;
	private ObjectAnimation idleAnimation;
	private ObjectAnimation jumpAnimation;
	private ObjectAnimation deathAnimation;
	private ObjectAnimation hurtAnimation;
	
	/** 
	 * Constructor:
	 * 
	 * Sets up Box2D physics body for movement and collisions. 
	 * Animations are initialised.
	 * A random unique ability is selected to attack the player with
	 * idle animation is set as the enemys do not move
	 *
	 */
	public BossEnemy(Levels Level, Integer HP, Integer ShootingDistance, Integer ShootingDelay, Integer NumberOfAbilities) {
		setHp(HP);
		getCircle().setRadius(7);
		this.movablePosition = new Vector2(0, 0);
		this.hurtAnimation = new ObjectAnimation("enemyHurtTp.txt", "Hurt_One", new Vector2(308,436), 5, 2, 0.15f, false);
		this.deathAnimation = new ObjectAnimation("enemyDeadTp.txt", "Dead_One", new Vector2(632,520), 2, 4, 0.2f, false);

		this.runAnimation = new ObjectAnimation("Run.pack", "Run_One", new Vector2(385,471), 2, 2, 0.15f, false);
		this.idleAnimation = new ObjectAnimation("bossAimTP.txt", "Idle_Aim_", new Vector2(431,425), 4, 2, 0.15f, true);
		this.jumpAnimation = new ObjectAnimation("testTp.txt", "Jump_One",  new Vector2(514,497), 3, 4, 0.15f, false);
		this.timePlayerIsVisibleOne = 0;
		this.timePlayerIsVisibleTwo = 0;
		this.timePlayerIsVisibleThree = 0;
		this.shootingDistance = ShootingDistance == 0 ? 100 : ShootingDistance;
		this.shootingDelayAbilityOne = ShootingDelay == 0 ? 400 : ShootingDelay;
		
		SetRandomBossAbilities(NumberOfAbilities);
		
		this.setAnimation(idleAnimation);
		runAnimation.flip(true, false);
	}
	
	//returns the size of the physics body
	public float getBodySize(){
		return getFixtureDef().shape.getRadius();
	}
	
	//returns the position of the boss
	public Vector2 getEnemyPosition() {
		return enemyPosition;
	}
	
	
	

	

	
	// Sets user data to be boss Object
	@Override
	public void SetUserData()
	{
		getBody().setUserData(this);
	}
	
	/** Checks if player is within distance to start shooting 
	 * 
	 * @param player used to get position of player 
	 * @return true/false depending if player is within shooting distance
	 */
	public boolean WithinShootingDistance(Player player)
	{
		if (player.movablePosition.x < this.movablePosition.x)
		{
			return (float) Math.sqrt(Math.pow((this.movablePosition.x - player.movablePosition.x), 2) + Math.pow((this.movablePosition.y - player.movablePosition.y), 2)) < this.shootingDistance;
		}
		else
		{
			return (float) Math.sqrt(Math.pow((player.movablePosition.x - this.movablePosition.x), 2) + Math.pow((player.movablePosition.y - this.movablePosition.y), 2)) < this.shootingDistance;
		}
	}
	
	/**
	 * 
	 * @param NumberOfAbilities used to set max number to increment through. 
	 */
	private void SetRandomBossAbilities(Integer NumberOfAbilities)
	{
		for(int i = 1; i <= NumberOfAbilities; i++)
		{
			RandomSelectAbility();
		}
	}
	
	/** randomly selects an ability that this boss has
	// Recursive (not best way to do this) to recall the method if the ability has already been set and hopefully set another one to true until it finds one that hasnt been set.
	 * 
	 */
	private void RandomSelectAbility() {
		Random rand = new Random();
		double abilityToChoose = rand.nextInt(3) + 1;
		switch((int)abilityToChoose)
		{
		case 1:
			if (hasAbilityOne)
			{
				RandomSelectAbility();
				break;
			}
			hasAbilityOne = true;
			break;
		case 2:
			if (hasAbilityTwo)
			{
				RandomSelectAbility();
				break;
			}
			hasAbilityTwo = true;
			break;
		case 3:
			if (hasAbilityThree)
			{
				RandomSelectAbility();
				break;
			}
			hasAbilityThree = true;
			break;
		}
	}
	
	/** Boss Mechanic Logic
	 * Method that fires the randomly selected boss ability towards the player
	 * 
	 * @param player used to determine where to shoot
	 * @param delta used to update movement variables
	 * @param lvl used to call required method to add bullets physics body to the physics world
	 * @param world used in conjunction with 'lvl' to add physics body to physics world
	 */
	public void bossMechanics(Player player, float delta, Camera cam, Levels lvl, World world)
	{
		if (WithinShootingDistance(player))
		{
			if (hasAbilityOne)
			{
				timePlayerIsVisibleOne += delta;
				if (timePlayerIsVisibleOne > this.shootingDelayAbilityOne)
				{
					RegularShot(player, delta, cam, lvl, world);
					timePlayerIsVisibleOne = 0;
				}
			}
			
			if (hasAbilityTwo)
			{
				timePlayerIsVisibleTwo += delta;
				if (timePlayerIsVisibleTwo > this.shootingDelayAbilityTwo)
				{
					FanShot(player, delta, cam, lvl, world);
					timePlayerIsVisibleTwo = 0;
				}
			}
			
			if (hasAbilityThree)
			{
				timePlayerIsVisibleThree += delta;
				if (timePlayerIsVisibleThree > this.shootingDelayAbilityThree)
				{
					DropBombs(player, delta, cam, lvl, world);
					timePlayerIsVisibleThree = 0;
				}
			}
		}
	}
	
	/**
	 * One of three boss abilities. Regular shot just sends a single bullet towards the player
	 * @param player used in order to get direction of player to shoot at
	 * @param delta used to continuously update required variables
	 * @param lvl used to call required method to add bullets physics body to the physics world
	 * @param world used to add bullet to the physics world
	 */
	public void RegularShot(Player player, float delta, Camera cam, Levels lvl, World world)
	{
				Vector2 directionToShoot = new Vector2(player.getBody().getPosition().x - this.getBody().getPosition().x, player.getBody().getPosition().y - this.getBody().getPosition().y);
				
				Bullets newBullet = new Bullets(false);
				newBullet.getBodyDef().position.x = this.getBody().getPosition().x;
				newBullet.getBodyDef().position.y = this.getBody().getPosition().y;
				newBullet.setMovablePosition(this.getBody().getPosition().x + 15, this.getBody().getPosition().y);
				lvl.addBullets(newBullet, world);
				newBullet.getBody().applyLinearImpulse(new Vector2(directionToShoot.x * 15, directionToShoot.y * 15), newBullet.getBody().getPosition(), true);
				
	}
	
	/**
	 * One of three boss abilities. Fan shot shoots bullets in a fan shape towards the player.
	 * Each bullets position is incremented by 45 degrees in order to assume the shape of a fan.
	 * @param player used in order to get direction of player to shoot at
	 * @param delta used to continuously update required variables
	 * @param lvl used to call required method to add bullets physics bodies to the physics world
	 * @param world used to add bullet to the physics world
	 */
	public void FanShot(Player player, float delta, Camera cam, Levels lvl, World world)
	{
		
				float angleValue = 0;
				int angleIncrease = 10;
				Vector2 shootingPosition = player.getBody().getPosition();
				Vector2 startPoint = new Vector2(this.getBody().getPosition().x, shootingPosition.y);
				Vector2 directionToShoot = new Vector2();

				directionToShoot = new Vector2(shootingPosition.x - startPoint.x, (shootingPosition.y - startPoint.y));
				
				angleValue = directionToShoot.angle() - 45;
				for(int angleCount = 0; angleCount <= 90; angleCount += angleIncrease)
				{
					Bullets newBullet = new Bullets(false);
					newBullet.getBodyDef().position.x = this.getBody().getPosition().x;
					newBullet.getBodyDef().position.y = this.getBody().getPosition().y;
					newBullet.setMovablePosition(this.movablePosition.x + 15, this.movablePosition.y);
					lvl.addBullets(newBullet, world);
					newBullet.getBody().applyLinearImpulse(new Vector2(directionToShoot.x * 15, directionToShoot.y * 15), newBullet.getBody().getPosition(), true);
					
					angleValue += angleIncrease;
					directionToShoot.setAngle(angleValue);
				}
				
	}
	
	/**
	 * One of three boss abilities. Drop Bombs slowly drops bombs towards the player that slowly increase in speed
	 * over time
	 * @param player used in order to get direction of player to shoot at
	 * @param delta used to continuously update required variables
	 * @param cam used to set the position for where the bullet is fired from  (infront of where boss is facing)
	 * @param lvl used to call required method to add bullets physics bodies to the physics world
	 * @param world used to add bullet to the physics world
	 */
	public void DropBombs(Player player, float delta, Camera cam, Levels lvl, World world)
	{
		Bomb newBomb = new Bomb();
		newBomb.getBodyDef().position.x = player.getBody().getPosition().x;
		newBomb.getBodyDef().position.y = player.getBody().getPosition().y + 100;
		lvl.addBombs(newBomb, world);
	}
	
	/**
	 * Method that makes the boss face the players direction
	 * @param player
	 */
	public void LookAtPlayer(Player player)
	{
		getBody().setLinearVelocity(0, 0);
		if(player.getBody().getPosition().x < this.getBody().getPosition().x)
		{
			if (this.IsFacingRight())
			{
				this.setFacingRight(false);
			}
		}
		else
		{
			if (!this.IsFacingRight())
			{
				this.setFacingRight(true);
			}
		}
	}
}

