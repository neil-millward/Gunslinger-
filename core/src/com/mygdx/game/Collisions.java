package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.badlogic.gdx.physics.box2d.World;
/**
 * Class used to implement the required behaviour based on what objects collide (player/bullet/floor etc)
 * @author Neil P13197943
 *
 */
public class Collisions implements ContactListener {
	private World world;
	private Player player;
	private Levels Level;

	/** 
	 * Constructor:
	 * sets the required objects for collision detection
	 * @param world world where collisons occur
	 * @param Player used in order to access the required get/setHP methods
	 * @param level Required for spike objects and floor collisions
	 */
	public Collisions(World world, Player Player, Levels level) {
		this.world = world;
		player = Player;
		Level = level;
	}
	
	/**
	 * Method that is called as soon as two physics bodies collide
	 * depending of which 2 bodies collided the relevant methods are called in order to deal damage/destroy enemies/bosses/bombs and to deal with enemy bullets hitting walls 
	 */
	@Override
	public void beginContact(Contact contact) {
		// System.out.println("begin contact");

		contact.setEnabled(true);
		Object fa = contact.getFixtureA().getBody().getUserData();
		Object fb = contact.getFixtureB().getBody().getUserData();

		if (fa == "lvl" || fb == "lvl")
			return;

		if (fa.equals("player") && fb.equals("enemy")) {
			player.setHp(player.getHp() - 1);

		} else if (fa.equals("enemy") && fb.equals("player")) {

			player.setHp(player.getHp() - 1);
			
			//Spike Collisions
		} else if ((fa instanceof Levels && fb instanceof Player)
				|| (fa instanceof Player && fb instanceof Levels)) {
				player.setHp(0);
				
				//boss and bullet collision
		} else if ((fa instanceof Bullets && fb instanceof BossEnemy)
				|| (fa instanceof BossEnemy && fb instanceof Bullets)) {
				
				if (fa instanceof BossEnemy) {
					if (((Bullets) fb).IsPlayerBullet()) {
						int bulletDamage = (((Bullets) fb).getDamage());
					((BossEnemy) fa).setHp(((BossEnemy) fa).getHp() - bulletDamage);
						Level.DestroyBoss((BossEnemy) fa);
						Level.DestroyBullet((Bullets) fb);
						
					}
				} else {
					if (((Bullets) fa).IsPlayerBullet()) {
						int bulletDamage = (((Bullets) fa).getDamage());
						((BossEnemy) fb).setHp(((BossEnemy) fb).getHp() - bulletDamage);
						Level.DestroyBoss((BossEnemy) fb);
						Level.DestroyBullet((Bullets) fa);
						//Score.getScoreInstance().AddScore(50);
					}
				}
				
		} 
		//Bullets and enemy collisions
		else if ((fa instanceof Bullets && fb instanceof RegularEnemy)
				|| (fa instanceof RegularEnemy && fb instanceof Bullets)) {

			if (fa instanceof RegularEnemy) {
				if (((Bullets) fb).IsPlayerBullet()) {
					int bulletDamage = (((Bullets) fb).getDamage());
					((RegularEnemy) fa).setHp(((RegularEnemy) fa).getHp() - bulletDamage);

					Level.DestroyEnemy((RegularEnemy) fa);
					Level.DestroyBullet((Bullets) fb);
					
				}
			} else {
				if (((Bullets) fa).IsPlayerBullet()) {
					int bulletDamage = (((Bullets) fa).getDamage());
					((RegularEnemy) fb).setHp(((RegularEnemy) fb).getHp() - bulletDamage );

					Level.DestroyEnemy((RegularEnemy) fb);
					Level.DestroyBullet((Bullets) fa);
				}
			}
			//bullet and wall collisions
		}else if ((fa.equals("StaticBody") && fb instanceof Bullets)
				|| (fa instanceof Bullets && fb.equals("StaticBody") )) {
				if (fa instanceof Bullets && ((Bullets) fa).IsPlayerBullet())
				{
					Level.DestroyBullet((Bullets) fa);
				}
				else if(((Bullets) fb).IsPlayerBullet())
				{
					Level.DestroyBullet((Bullets) fb);
				}
		//bullet and player collisions
		}else if ((fa instanceof Bullets && fb instanceof Player) || (fa instanceof Player && fb instanceof Bullets)) {

			if (fa instanceof Player) {
				if (!((Bullets) fb).IsPlayerBullet()) {
					int bulletDamage = (((Bullets) fb).getDamage());
					Level.DestroyBullet((Bullets) fb);
					if (player.getHp() != 0) {
						player.setHp(player.getHp() - bulletDamage);
					}
				}
			} 
			else {
				if (!((Bullets) fa).IsPlayerBullet()) {
					int bulletDamage = (((Bullets) fa).getDamage());
					Level.DestroyBullet((Bullets) fa);
					if (player.getHp() != 0) {
						player.setHp(player.getHp() - bulletDamage);
					}

				}

			}

		}else if ((fa instanceof Bomb && fb instanceof Player) || (fa instanceof Player && fb instanceof Bomb)) {

			if (fa instanceof Player) {
				
					int bombDamage = (((Bomb) fb).getDamage());
					Level.DestroyBomb((Bomb) fb);
					if (player.getHp() != 0) {
						player.setHp(player.getHp() - bombDamage);
					}
				
			} 
			else {
				int bombDamage = (((Bomb) fa).getDamage());
					Level.DestroyBomb((Bomb) fa);
					if (player.getHp() != 0) {
						player.setHp(player.getHp() - bombDamage);
				}

			}

		}

	}

	@Override
	public void endContact(Contact contact) {
		// System.out.println("end contact");
		Fixture fa = contact.getFixtureA();
		Fixture fb = contact.getFixtureB();

		if (fa == null || fb == null)
			return;
		if (fa.getUserData() == null || fb.getUserData() == null)
			return;

	}

	@Override
	public void preSolve(Contact contact, Manifold oldManifold) {
	}

	@Override
	public void postSolve(Contact contact, ContactImpulse impulse) {
	}

}
