package com.mygdx.game;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.maps.tiled.tiles.StaticTiledMapTile;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;

/**
 * 
 * @author Neil Millward P13197943 Class that generates the levels for the game
 */
public class Levels {
	// Box2D and Tiled map variables
	private Box2DDebugRenderer b2dDR;
	private TiledMap map;
	private TmxMapLoader mapLoader;
	private OrthogonalTiledMapRenderer renderer;
	// contact listen for collision detection
	private ContactListener contactListener;

	// arrays used to store levels enemies/bosses/bullets
	private ArrayList<RegularEnemy> allEnemies;
	private ArrayList<BossEnemy> allBosses;
	private ArrayList<Bullets> allBullets;
	private ArrayList<Bomb> allBombs;

	// arrays to store bosses/enemies/bullets for removal from physics world
	private ArrayList<Bullets> bulletsToDestroy;
	private ArrayList<Bomb> bombsToDestroy;
	private ArrayList<RegularEnemy> enemiesToDestroy;
	private ArrayList<BossEnemy> bossToDestroy;

	private boolean isMuted;

	// array to store static bodies from tiled map physics/damage layers
	private ArrayList<Body> staticEntitiesInLevel;
	// portrays the level number (1 to 5)
	public Integer levelNumber;

	private ArrayList<Music> levelTracks;

	public ArrayList<Music> getLevelTracks() {
		return levelTracks;
	}

	public void setLevelTracks(ArrayList<Music> levelTracks) {
		this.levelTracks = levelTracks;
	}

	/**
	 * Custom constructor takes in a world and tiled map object, adds map
	 * physics bodies to world and sets up tiled map renderer to render the
	 * level. //being able to add separate maps allows for simple changing of
	 * levels
	 * 
	 * @param world
	 *            physics world
	 * @param mapFile
	 *            mapFile that represents levels visuals, is passed to tiledmap
	 *            renderer for renderer of level
	 */
	public Levels(World world, String mapFile) {
		mapLoader = new TmxMapLoader();
		map = mapLoader.load(mapFile);

		allBullets = new ArrayList<Bullets>();
		allEnemies = new ArrayList<RegularEnemy>();
		allBosses = new ArrayList<BossEnemy>();
		allBombs = new ArrayList<Bomb>();

		bulletsToDestroy = new ArrayList<Bullets>();
		enemiesToDestroy = new ArrayList<RegularEnemy>();
		bossToDestroy = new ArrayList<BossEnemy>();
		bombsToDestroy = new ArrayList<Bomb>();
		staticEntitiesInLevel = new ArrayList<Body>();

		levelNumber = 1;
		levelTracks = new ArrayList<Music>();
		// level music, added index 0 is level 1 music, index 1 is level 2 music
		// etc...
		levelTracks.add(Gdx.audio.newMusic(Gdx.files.internal("levelOneTrack.mp3")));
		levelTracks.add(Gdx.audio.newMusic(Gdx.files.internal("levelTwoTrack.mp3")));
		levelTracks.add(Gdx.audio.newMusic(Gdx.files.internal("levelThreeTrack.mp3")));
		levelTracks.add(Gdx.audio.newMusic(Gdx.files.internal("levelFourTrack.mp3")));
		levelTracks.add(Gdx.audio.newMusic(Gdx.files.internal("levelFiveTrack.mp3")));

		renderer = new OrthogonalTiledMapRenderer(map);

		isMuted = false;

		// used to display debug lines
		b2dDR = new Box2DDebugRenderer();

		addStaticBodiesToLevel(world);

	}

	/**
	 * Method that adds the players physics body to the physics world
	 * 
	 * @param Player
	 *            gets players physics body attributes to add to the physics
	 *            world
	 * @param world
	 *            Physics world in which the player gets added to
	 */
	public void addPlayerToWorld(Player Player, World world) {
		FixtureDef fDef = Player.getFixtureDef();
		CircleShape circle = Player.getCircle();
		circle.setRadius(Player.getCircle().getRadius());
		fDef.shape = Player.getCircle();
		Player.setBody(world.createBody(Player.getBodyDef()));
		Player.getBody().createFixture(fDef);
		Player.SetUserData();
	}

	/**
	 * Method that adds the bullet physics bodies to the physics world The
	 * bullet body is stored into an array which will later be used for the
	 * removal of bullet bodies from the physics world
	 * 
	 * @param bullet
	 *            gets bullet physics body attributes to add to the physics
	 *            world
	 * @param world
	 *            Physics world in which the bullet gets added to
	 */
	public void addBullets(Bullets bullet, World world) {
		FixtureDef fDef = bullet.getFixtureDef();
		CircleShape circle = bullet.getCircle();
		circle.setRadius(bullet.getCircle().getRadius());
		fDef.shape = bullet.getCircle();
		bullet.setBody(world.createBody(bullet.getBodyDef()));
		bullet.getBody().setBullet(true);
		bullet.getBody().createFixture(fDef);

		// have to set user data here... setting in bullet class causes user
		// data to == null
		bullet.SetUserData();
		// added bullet body to bullet array
		allBullets.add(bullet);

	}

	/**
	 * Method that adds the boss bomb physics bodies to the physics world The
	 * bomb body is stored into an array which will later be used for the
	 * removal of bomb bodies from the physics world
	 * 
	 * @param bomb
	 *            gets bomb physics body attributes to add to the physics world
	 * @param world
	 *            Physics world in which the bomb gets added to
	 */
	public void addBombs(Bomb bomb, World world) {
		FixtureDef fDef = bomb.getFixtureDef();
		CircleShape circle = bomb.getCircle();
		circle.setRadius(bomb.getCircle().getRadius());
		fDef.shape = bomb.getCircle();
		bomb.setBody(world.createBody(bomb.getBodyDef()));
		bomb.getBody().setBullet(true);
		bomb.getBody().createFixture(fDef);

		// have to set user data here... setting in bomb class causes user data
		// to == null
		bomb.SetUserData();
		allBombs.add(bomb);
	}

	/**
	 * Method that adds the enemy physics bodies to the physics world The enemy
	 * body is stored into an array which will later be used for the removal of
	 * enemy bodies from the physics world
	 * 
	 * @param enemy
	 *            gets bomb physics body attributes to add to the physics world
	 * @param world
	 *            Physics world in which the enemy gets added to
	 */
	public void addEnemyToWorld(RegularEnemy enemy, World world) {
		FixtureDef fDef1 = enemy.getFixtureDef();
		CircleShape circle = enemy.getCircle();
		circle.setRadius(enemy.getCircle().getRadius());
		fDef1.shape = enemy.getCircle();
		enemy.setBody(world.createBody(enemy.getBodyDef()));
		enemy.getBody().createFixture(fDef1);
		allEnemies.add(enemy);
	}

	/**
	 * Method that adds the boss enemy physics bodies to the physics world The
	 * boss enemy body is stored into an array which will later be used for the
	 * removal of boss enemy bodies from the physics world
	 * 
	 * @param BossEnemy
	 *            gets bomb physics body attributes to add to the physics world
	 * @param world
	 *            Physics world in which the boss enemy gets added to
	 */
	public void addBossEnemyToWorld(BossEnemy enemy, World world) {
		FixtureDef fDef1 = enemy.getFixtureDef();
		CircleShape circle = enemy.getCircle();
		circle.setRadius(enemy.getCircle().getRadius());
		fDef1.shape = enemy.getCircle();
		enemy.setBody(world.createBody(enemy.getBodyDef()));
		enemy.getBody().createFixture(fDef1);
		allBosses.add(enemy);
	}

	/**
	 * Method that stores defeated enemy in the enemies to destroy array for
	 * later removal from physics world checks enemies to destroy array to see
	 * if it does not contain the passed enemy AND the passed enemies HP <=0
	 * (dead) then add to the enemies to destroy array. The enemy is added to
	 * the enemies to destroy array to later be used for removal from physics
	 * world The players score is updated The enemy is then taken out of the
	 * levels all enemy array as they are dead.
	 * 
	 * @param enemy
	 *            enemy to be added to enemies to destroy array for later
	 *            removal
	 */
	public void DestroyEnemy(RegularEnemy enemy) {
		if (!enemiesToDestroy.contains(enemy) && enemy.getHp() <= 0) {
			enemiesToDestroy.add(enemy);
			Score.getScoreInstance().AddScore(50);
			allEnemies.remove(enemy);
		}
	}

	/**
	 * Method that stores defeated boss in the boss to destroy array for later
	 * removal from physics world checks boss to destroy array to see if it does
	 * not contain the passed boss AND the passed boss HP <=0 (dead) then add to
	 * the boss to destroy array. The boss is added to the boss to destroy array
	 * to later be used for removal from physics world The players score is
	 * updated The boss is then taken out of the levels all boss array as they
	 * are dead.
	 * 
	 * @param boss
	 *            boss to be added to boss to destroy array for later removal
	 */
	public void DestroyBoss(BossEnemy boss) {
		if (!bossToDestroy.contains(boss) && boss.getHp() <= 0) {
			bossToDestroy.add(boss);
			Score.getScoreInstance().AddScore(100);

			allBosses.remove(boss);
		}
	}

	/**
	 * Method that stores used bullets in the bullets to destroy array for later
	 * removal from physics world checks bullet to destroy array to see if it
	 * does not contain the passed bullet then add to the boss to destroy array.
	 * The bullet is added to the bullet to destroy array to later be used for
	 * removal from physics world The bullet is then taken out of the levels all
	 * bullets array as they are used.
	 * 
	 * @param bullet
	 *            bullet to be added to boss to destroy array for later removal
	 */
	public void DestroyBullet(Bullets bullet) {
		if (!bulletsToDestroy.contains(bullet)) {
			bulletsToDestroy.add(bullet);
			allBullets.remove(bullet);
		}
	}

	/**
	 * Method that stores used bombs in the bombs to destroy array for later
	 * removal from physics world checks bombs to destroy array to see if it
	 * does not contain the passed bullet then add to the boss to destroy array.
	 * The bomb is added to the bombs to destroy array to later be used for
	 * removal from physics world The bomb is then taken out of the levels all
	 * bombs array as they are used.
	 * 
	 * @param bomb
	 *            bomb to be added to bombs to destroy array for later removal
	 */
	public void DestroyBomb(Bomb bomb) {
		if (!bombsToDestroy.contains(bomb)) {
			bombsToDestroy.add(bomb);
			allBombs.remove(bomb);
		}
	}

	/**
	 * Removes all boss/enemy/bullet/bomb objects from the physics world by
	 * checking if the relevant 'toDestroy' arrays are not empty. said arrays
	 * are then cleared for the next objects
	 * 
	 * @param world
	 *            world in which to remove bodies from
	 */
	public void RemoveCurrentDestroyedEntities(World world) {
		// Destroy enemies added to list of Enemies To Destroy
		if (!enemiesToDestroy.isEmpty()) {
			for (RegularEnemy enemy : enemiesToDestroy) {
				world.destroyBody(enemy.getBody());
			}
			enemiesToDestroy.clear();
		}

		// Destroy bullets added to list of bullets To Destroy
		if (!bulletsToDestroy.isEmpty()) {
			for (Bullets bullet : bulletsToDestroy) {
				world.destroyBody(bullet.getBody());
			}
			bulletsToDestroy.clear();
		}

		// Destroy boss added to list of bosses To Destroy

		if (!bossToDestroy.isEmpty()) {
			for (BossEnemy boss : bossToDestroy) {
				world.destroyBody(boss.getBody());
			}
			bossToDestroy.clear();
		}

		if (!bombsToDestroy.isEmpty()) {
			for (Bomb bomb : bombsToDestroy) {
				world.destroyBody(bomb.getBody());
			}
			bombsToDestroy.clear();
		}
	}

	/**
	 * adds all objects in level to lists to destroy and calls
	 * RemoveCurrentDestroyedEntities() (above method) each array iis then
	 * clearer. This method is used when switching to the next level
	 * 
	 * @param world
	 */
	public void DestroyAllEntities(World world) {
		if (!allBullets.isEmpty()) {
			for (Bullets bullet : allBullets) {
				bulletsToDestroy.add(bullet);
			}
		}
		if (!allBombs.isEmpty()) {
			for (Bomb bomb : allBombs) {
				bombsToDestroy.add(bomb);
			}
		}
		if (!allBosses.isEmpty()) {
			for (BossEnemy boss : allBosses) {
				bossToDestroy.add(boss);
			}
		}

		if (!allEnemies.isEmpty()) {
			for (RegularEnemy enemy : allEnemies) {
				DestroyEnemy(enemy);
			}
		}

		RemoveCurrentDestroyedEntities(world);
		allBombs.clear();
		allBosses.clear();
		allBullets.clear();
		allEnemies.clear();
	}

	/**
	 * Method that changes to the next level. it destroys all static bodies from
	 * the tiled map it then gets all the bodies from the previous level and
	 * adds them to an array all bodies apart from the player are removed the
	 * old level map file is disposed, the new one is then rendered finally the
	 * addStaticBodies to level method is called which takes the new maps static
	 * bodies (floor, spike objects) and adds them to the physics world
	 * 
	 * @param level
	 *            string for the map file to be passed
	 * @param world
	 *            world to add map objects to
	 * 
	 */
	public void ChangeLevel(final String level, World world) {
		DestroyStaticObjects(world);
		Array<Body> bodiesToRemove = new Array<Body>();
		world.getBodies(bodiesToRemove);
		for (Body body : bodiesToRemove) {
			if (!(body.getUserData() instanceof Player)) {
				world.destroyBody(body);
			}
		}

		// dispose old map and load new map level
		map.dispose();
		map = new TmxMapLoader().load(level);
		renderer.getMap().dispose();
		renderer.setMap(map);

		addStaticBodiesToLevel(world);

	}

	/**
	 * Method that adds the selected collision/damage tiles to the physics
	 * world. Physics bodies are set up for the selected layers to be added to.
	 * A for loop gets the layers from the tiled map editor level where layers 2
	 * and 3 are the floor and damage object tiles The tiles from the layer are
	 * then placed to the set up static physics bodies before before correctly
	 * positioned and added to the physics world Finally the bodies are also
	 * added to a static entities in level array for layer removal upon level
	 * switch
	 * 
	 * @param world
	 *            world to add map objects to
	 */
	public void addStaticBodiesToLevel(World world) {
		BodyDef bdef = new BodyDef();
		Body body;
		PolygonShape shape = new PolygonShape();
		FixtureDef fdef = new FixtureDef();
		for (MapObject obj : map.getLayers().get(2).getObjects().getByType(RectangleMapObject.class)) {
			Rectangle rect = ((RectangleMapObject) obj).getRectangle();
			bdef.type = BodyDef.BodyType.StaticBody;

			// this equation makes sure the boxes are placed exactly like in
			// tiled map editor
			bdef.position.set(rect.getX() + rect.getWidth() / 2, rect.getY() + rect.getHeight() / 2);

			body = world.createBody(bdef);

			shape.setAsBox(rect.getWidth() / 2, rect.getHeight() / 2);
			fdef.shape = shape;
			body.createFixture(fdef);

			body.setUserData("StaticBody");

			staticEntitiesInLevel.add(body);
		}

		for (MapObject obj : map.getLayers().get(3).getObjects().getByType(RectangleMapObject.class)) {
			Rectangle rect = ((RectangleMapObject) obj).getRectangle();
			bdef.type = BodyDef.BodyType.StaticBody;

			// this equation makes sure the boxes are placed exactly like in
			// tiled
			bdef.position.set(rect.getX() + rect.getWidth() / 2, rect.getY() + rect.getHeight() / 2);

			body = world.createBody(bdef);

			shape.setAsBox(rect.getWidth() / 2, rect.getHeight() / 2);
			fdef.shape = shape;
			body.createFixture(fdef);

			body.setUserData(this);
			staticEntitiesInLevel.add(body);

		}
	}

	/**
	 * Method that loads the enemies based on the level number each level has
	 * its own XML file which contains enemies/bosses positions and attack
	 * attributes. The relevant XML file is read, the enemy/boss tags in the
	 * file represent the number of such the aforementioned enemy/boss objects
	 * are then added to the level
	 * 
	 * @param cam
	 *            used for setting the enemies animation position
	 * @param world
	 *            world in which the enemies/bosses are added to
	 */
	public void loadEnemies(OrthographicCamera cam, World world) {

		XMLEnemyParser enemyhandler = new XMLEnemyParser(this);
		String levelToLoad = "";
		switch (levelNumber) {
		case 1:
			levelToLoad = "Level" + "One" + ".xml";
			levelTracks.get(0).play();
			levelTracks.get(0).setVolume(0.2f);
			levelTracks.get(0).setPosition(16);
			levelTracks.get(0).setLooping(true);
			break;
		case 2:
			levelToLoad = "Level" + "Two" + ".xml";
			levelTracks.get(0).dispose();
			levelTracks.get(1).play();
			levelTracks.get(1).setVolume(0.2f);
			levelTracks.get(1).setLooping(true);

			break;
		case 3:
			levelToLoad = "Level" + "Three" + ".xml";
			levelTracks.get(1).dispose();
			levelTracks.get(2).play();
			levelTracks.get(2).setVolume(0.2f);
			levelTracks.get(2).setLooping(true);

			break;
		case 4:
			levelToLoad = "Level" + "Four" + ".xml";
			levelTracks.get(2).dispose();
			levelTracks.get(3).play();
			levelTracks.get(3).setVolume(0.6f);
			levelTracks.get(3).setLooping(true);

			break;
		case 5:
			levelToLoad = "Level" + "Five" + ".xml";
			levelTracks.get(3).dispose();
			levelTracks.get(4).play();
			levelTracks.get(4).setVolume(0.2f);
			levelTracks.get(4).setLooping(true);

			break;
		}

		try {
			File inputFile = new File(levelToLoad);
			SAXParserFactory factory = SAXParserFactory.newInstance();
			SAXParser saxParser = factory.newSAXParser();
			saxParser.parse(inputFile, enemyhandler);

		} catch (Exception e) {
			e.printStackTrace();
		}

		for (RegularEnemy enemy : enemyhandler.enemyList) {
			addEnemyToWorld(enemy, world);
			enemy.SetUserData();
		}
		for (BossEnemy boss : enemyhandler.bossList) {
			addBossEnemyToWorld(boss, world);
			boss.SetUserData();

		}

	}

	/**
	 * Method that destroys the static objects in the level by removing them
	 * from the world
	 * 
	 * @param world
	 *            world in whihc the static bodies are being removed from
	 */
	public void DestroyStaticObjects(World world) {
		for (Body body : staticEntitiesInLevel) {
			world.destroyBody(body);
		}
		staticEntitiesInLevel.clear();
	}

	/**
	 * Method that gets the relevant levels tiled map file to be added to the
	 * OTHER changeLevel method
	 * 
	 * @param cam
	 * @param world
	 * @param player
	 */
	public void changeLevel(OrthographicCamera cam, World world, Player player) {
		String levelToLoad = "";
		switch (levelNumber) {
		case 1:
			levelToLoad = "level" + "One" + ".tmx";
			break;
		case 2:
			levelToLoad = "level" + "Two" + ".tmx";
			break;
		case 3:
			levelToLoad = "level" + "Three" + ".tmx";
			break;
		case 4:
			levelToLoad = "level" + "Four" + ".tmx";
			break;
		case 5:
			levelToLoad = "level" + "Five" + ".tmx";
			break;
		}

		ChangeLevel(levelToLoad, world);
	}

	//mutes level music
	public void muteTracks() {
		if (isMuted) {
			this.levelTracks.get(this.levelNumber - 1).play();
			isMuted = false;
		} else {
			this.levelTracks.get(this.levelNumber - 1).pause();
			isMuted = true;
		}

	}

	// ----------------------------------------------
	// Getters and setters
	public ContactListener getContactListener() {
		return contactListener;
	}

	public void setContactListener(ContactListener contactListener) {
		this.contactListener = contactListener;
	}

	public TiledMap getMap() {
		return this.map;
	}

	public void setMap(TiledMap map) {
		this.map = map;
	}

	public TmxMapLoader getMapLoader() {
		return this.mapLoader;
	}

	public void setMapLoader(TmxMapLoader mapLoader) {
		this.mapLoader = mapLoader;
	}

	public ArrayList<Bullets> GetBullets() {
		return allBullets;
	}

	public ArrayList<RegularEnemy> GetEnemies() {
		return allEnemies;
	}

	public ArrayList<BossEnemy> GetBoss() {
		return allBosses;
	}

	public ArrayList<Bomb> GetBombs() {
		return allBombs;
	}

	public OrthogonalTiledMapRenderer getRenderer() {
		return renderer;
	}

	public void setRenderer(OrthogonalTiledMapRenderer renderer) {
		this.renderer = renderer;
	}

	public Box2DDebugRenderer getB2dDR() {
		return b2dDR;
	}

	public void setB2dDR(Box2DDebugRenderer b2dDR) {
		this.b2dDR = b2dDR;
	}

	// --------------------------------------------------
}
