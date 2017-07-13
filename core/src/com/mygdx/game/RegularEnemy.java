package com.mygdx.game;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.World;
/**
 * 
 * @author Neil Millward P13197943
 * Class that represents the regular enemies of the game.
 *
 */
public class RegularEnemy extends Enemies{
	//vector for storing enemies start postion on the level
	private Vector2 enemyStartPosition;
	//used to store the distance the enemy can shoot from
	private int shootingDistance = 100;
	//used to store the amount of delay between bullet fire
	private int shootingDelay = 400;
	//used to determine how long the player has been visable for
	private float timePlayerIsVisible;
	//animation objects to store relevant animations
	private ObjectAnimation runAnimation;
	private ObjectAnimation idleAnimation;
	private ObjectAnimation jumpAnimation;
	private ObjectAnimation deathAnimation;
	private ObjectAnimation hurtAnimation;
	//used to store the distance in which the enemies patrol
	private static float patrolDistance = 30f;
	//boolean to determine if start position has been set
	private boolean startPositionSet = false;
	// boolean to determine if the enemy is patrolling to the right
	boolean isPatrollingRight = false;
	
	/**
	 * Constructor:
	 * Uses inherited methods from character/movable to set up HP and movable position.
	 * Animations are then set up along with shooting distance and shooting delay
	 *
	 * @param HP used to set enemies HP
	 * @param ShootingDistance used to set the distance in which the enemy can shoot from
	 * @param ShootingDelay used to set the delay between enemy fire
	 */
	public RegularEnemy(Integer HP, Integer ShootingDistance, Integer ShootingDelay) {
		setHp(HP);
		getCircle().setRadius(7);
		this.movablePosition = new Vector2(0, 0);
		this.hurtAnimation = new ObjectAnimation("enemyHurtTp.txt", "Hurt_One", new Vector2(308,436), 5, 2, 0.15f, false);
		this.deathAnimation = new ObjectAnimation("enemyDeadTp.txt", "Dead_One", new Vector2(632,520), 2, 4, 0.2f, false);

		this.runAnimation = new ObjectAnimation("enemyRunTp.txt", "Run_Shoot_", new Vector2(484,448), 2, 3, 0.15f, false);
		this.idleAnimation = new ObjectAnimation("enemyIdleTP.txt", "Idle_Aim_One", new Vector2(373,431), 3, 2, 0.15f, true);
		this.jumpAnimation = new ObjectAnimation("testTp.txt", "Jump_One",  new Vector2(514,497), 3, 4, 0.15f, false);
		this.timePlayerIsVisible = 0;
		this.shootingDistance = ShootingDistance == 0 ? 100 : ShootingDistance;
		this.shootingDelay = ShootingDelay == 0 ? 400 : ShootingDelay;
		this.setAnimation(idleAnimation);
		
	}
	
	public float getBodySize(){
		return this.getFixtureDef().shape.getRadius();
	}
	
	/**
	 * Method that sets the enemies start position for patrolling
	 */
	public void setEnemyStartPosition() {
		this.enemyStartPosition = new Vector2(this.getBody().getPosition().x, this.getBody().getPosition().y);
	}
	
	public void update(float dt,Levels lvl, Player player,Camera cam){
				
	}
	
	// Sets user data to be RegularEnemy Object
	@Override
	public void SetUserData()
	{
		this.getBody().setUserData(this);
	}
	
	/** Checks if player is within distance to start shooting
	 * 
	 * @param player used to get the players position. Needed as the enemies use such position to determine if the player is withing shooting distance.
	 * The method subtracts the players x axis from the enemys x axis which is then squared, from there it is added to the resilt of the plays y axis subtracted from the enemies y axis squared. 
	 * If it is then than the shooting distance then true is returned (can start shooting at player). If false then cannot attack player
	 * @return
	 */
	public boolean WithinShootingDistance(Player player)
	{
		if (player.movablePosition.x < this.movablePosition.x)
		{
			this.setAnimation(runAnimation);
			return (float) Math.sqrt(Math.pow((this.movablePosition.x - player.movablePosition.x), 2) + Math.pow((this.movablePosition.y - player.movablePosition.y), 2)) < this.shootingDistance;
		}
		else
		{
			this.setAnimation(runAnimation);
			return (float) Math.sqrt(Math.pow((player.movablePosition.x - this.movablePosition.x), 2) + Math.pow((player.movablePosition.y - this.movablePosition.y), 2)) < this.shootingDistance;
		}
	}
	
	/**
	 * Method that initiates shooting towards the player.
	 * Firstly the previous 'withinShootingDistance' method is evaluated to determine if the enemy can start shooting the player.
	 * If the enemy can start shooting then the enemy stops, the idle animation is set to represent the enemy stopping, the bullet is then created,
	 * added to the world and fired towards the players position.
	 * 
	 * If the player is not within shooting distance then the enemy patrol method is called in turn making the enemy patrol up and done until the player is within distance.
	 * 
	 * @param player used to get position to shoot at
	 * @param delta used to constantly update the time the player is visible and apply forces to continuously move bullets
	 * @param cam used to initialise patrol method
	 * @param lvl used to add bullets to the physics world by calling levels addBullets method and passing world object
	 * @param world world object in which bullets are added to
	 */
	public void ShootPlayer(Player player, float delta,  Levels lvl, World world)
	{
		if (WithinShootingDistance(player))
		{
			this.setAnimation(idleAnimation);
			this.getBody().setLinearVelocity(0, 0);
			this.getBody().applyLinearImpulse(new Vector2(0, 0), this.getBody().getPosition(), true);
			this.timePlayerIsVisible += delta;
			if (this.timePlayerIsVisible > this.shootingDelay)
			{
				Vector2 directionToShoot = new Vector2(player.getBody().getPosition().x - this.getBody().getPosition().x, player.getBody().getPosition().y - this.getBody().getPosition().y);
				
				Bullets newBullet = new Bullets(false);
				newBullet.getBodyDef().position.x = this.getBody().getPosition().x + 15;
				newBullet.getBodyDef().position.y = this.getBody().getPosition().y;
				newBullet.setMovablePosition(this.getBody().getPosition().x + 15, this.getBody().getPosition().y);
				lvl.addBullets(newBullet, world);
				newBullet.getBody().applyLinearImpulse(new Vector2(directionToShoot.x * 15, directionToShoot.y * 15), this.movablePosition, true);
				
				this.timePlayerIsVisible = 0;
			}
		}
		else
		{
			this.Patrol();
		}
	}
	
	/**
	 * Method that flips the enemies sprite depandant on if the player is infront or behind the enemy.
	 * @param player used to check the players positions to determine if player in infront or behind of enemy
	 */
	public void LookAtPlayer(Player player)
	{
		if(WithinShootingDistance(player)){
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
	
	/**
	 * Method that makes the enemies patrol up and done
	 * The logic behind the method is that if the position of the enemy if less than the set
	 * start position then isPatrollingRight is set to true. If the position of the enemy is greater than the
	 * enemies start position then isPatrolling left is set to true.
	 * 
	 * Dependent on if isPatrolling right/left is true, the enemy will start moving  right/left
	 * 
	 */
	public void Patrol()
	{
		if (!startPositionSet)
		{
			this.setEnemyStartPosition();
			this.startPositionSet = true;
		}
		
		if (this.getBody().getPosition().x < this.enemyStartPosition.x - this.patrolDistance)
		{
			isPatrollingRight = true;
			this.setFacingRight(true);
		}
		if (this.getBody().getPosition().x > this.enemyStartPosition.x + this.patrolDistance)
		{
			isPatrollingRight = false;
			this.setFacingRight(false);
		}
		
		if (isPatrollingRight)
		{
			this.getBody().setLinearVelocity(20, this.getBody().getLinearVelocity().y);
		}
		else
		{
			this.getBody().setLinearVelocity(-20, this.getBody().getLinearVelocity().y);
		}
		
		
		
	}
}
