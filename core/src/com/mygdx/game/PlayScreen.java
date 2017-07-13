package com.mygdx.game;

import java.util.ArrayList;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
/**
 * 
 * @author Neil Millward P13197943
 * The main screen of the game. This is the screen that is used when the user starts a new game.
 *
 */
public class PlayScreen extends MyGdxGame implements Screen {
	
	private Levels lvl;
	private Hud hud;
	private OrthographicCamera cam;
	private Viewport gamePort;
	private Player player;
	private MyGdxGame gdxGame;
	private Music music;
	private float overallTime;
	private World world;
	public static float PPM = 32f;
	private boolean gamePaused;
	
	private boolean startLevelTextShowing = false;
	private static float textMaxTimeisplayed = 7f;
	private float textTotalTimeDisplayed = 0f;
	/**
	 * Constructor:
	 * starts playing in game music.
	 * sets up camera
	 * physics world is set up.
	 * Level one is set up to be added to physics
	 * player is set and added to the physics world.
	 * Enemies/bosses are added to the level/world
	 * scores are got from XML file and set
	 * HUD is initialised 
	 * Contact listener is set to physics world in order to listen for collisions
	 * 
	 * @param game used to get width/height values and for sprite batch drawing
	 */
	public PlayScreen(MyGdxGame game) {
		
		cam = new OrthographicCamera(Gdx.graphics.getWidth() / PPM, Gdx.graphics.getHeight() / PPM);
		this.setGdxGame(game);
		// Initialising world and adding player and enemies
		world = new World(new Vector2(0, -250), true);
		World.setVelocityThreshold(1);
		overallTime = 0;
		lvl = new Levels(world, "levelOne.tmx");

		// Setting up camera,viewport enables black bars for resizing and focus
		gamePort = new FitViewport(MyGdxGame.V_WIDTH, MyGdxGame.V_HEIGHT, cam);
		cam.position.set(gamePort.getWorldWidth() / 2, gamePort.getWorldHeight() / 2, 0);
		
		player = new Player(lvl,world);
		lvl.addPlayerToWorld(player, world);
		lvl.loadEnemies(cam, world);
		Score.getScoreInstance().GetAndSetHighScores();
		
		// Initialise Hud, use game sprite batch for stage to draw
		hud = new Hud(player, game.getBatch(), lvl.levelNumber, 50);
		world.setContactListener(new Collisions(world, player, lvl));
		
		gamePaused = false;
		startLevelTextShowing = true;
		player.getBody().setTransform(new Vector2(100, 40),0);
	}

	@Override
	public void show() {

	}

	/**
	 * Main method that encapsulates all classes that require constant updating.
	 * Firstly logic for the pause mechanic is implemented. as all the games physics occur in the this method the game can be paused by evaluating a boolean
	 * first. if false (player pressed p) then the games physics cease in turn pausing the game. If false, then the game functions as intended. 
	 * The world takes a 'step' which is essentially the world moving in time.
	 * During the world step everything required for updating is then updated. The tiled map sprites get rendered to screen, the HUD is constantly being updated, the cams position
	 * follows the player.
	 * 
	 * The players control, attack and update methods are called.
	 * All additional movable objects are updated appropriately.
	 * checks are made to determine if the level is complete by checking if the boss is dead and it is not level 5 (completing the game).
	 * All dead/used objects are removed from the level
	 * Condition is evaluated to determine if the player has completed the game (boss dead and current level is level 5 (last level)).
	 * Condition is evaluated to determine if the player is dead, in turn producing a game over if true (HP <= 0).
	 * Finally input handling is used to determine if the 'p' key has been pressed in turn pausing the game
	 * 
	 */
	@Override
	public void render(float delta) {
		
		
		if(gamePaused == false)
		{
			Gdx.gl.glClearColor(0, 0, 0, 1);
			Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
			
			overallTime += delta;
			
			if (!startLevelTextShowing)
			{
				//makes the world "move in time" 
				world.step(1 / 60f, 6, 2);
				
				//renders  tiled map sprites to world
				lvl.getRenderer().render();
				
				// renders debugger lines
				//lvl.getB2dDR().render(world, cam.combined);

				// draws hud to screen
				hud.Update(delta, player, Score.getScoreInstance().GetCurrentScore(), lvl.levelNumber);
				//hud.getStage().draw();
					
				//update camera to follow player
				gdxGame.batch.setProjectionMatrix(cam.combined);
				MoveCameraWithPlayer(delta);
				cam.update();
				
				lvl.getRenderer().setView(cam);
				gdxGame.batch.begin();
			
				//allows player control
				player.control(delta);
				player.attack(delta,lvl,gdxGame.batch);
				player.Update(delta, gdxGame.batch);
				
				
				UpdateAllMovableObjects(delta);
				
				// if there are no more enemies or bosses, load next level.
				if (lvl.GetBoss().isEmpty() && lvl.levelNumber != 5 )
				{
					LoadNextLevel();
				}
				
				
				
				// method for removing dead entities from the world
				lvl.RemoveCurrentDestroyedEntities(world);
				
				if (lvl.GetBoss().isEmpty() && lvl.levelNumber == 5)
				{
					gameComplete();
					
				}
				
				// if player no longer exists, its game over.
				if (player != null && player.isDead)
				{
					GameOver();
				}
				gdxGame.batch.end();
			}
			else
			{
				DisplayText(delta);
			}
			
		}
		
		if (Gdx.input.isKeyJustPressed(Input.Keys.P)) {
			gamePaused = !gamePaused;
			}
	}
	
	/** 
	 * Method used for moving and updating bullets, bombs, enemy and boss objects
	 * @param delta
	 */
	private void UpdateAllMovableObjects(float delta) {
		if(lvl.GetBullets() != null)
		{
			if (!lvl.GetBullets().isEmpty())
			{
				ArrayList<Bullets> bulletsToUpdate = lvl.GetBullets();
				for (Bullets bullet : bulletsToUpdate) {
					bullet.Update(delta, gdxGame.batch);
				}
			}
			
		}
		
		if(lvl.GetBombs() != null)
		{
			if (!lvl.GetBombs().isEmpty())
			{
				ArrayList<Bomb> bombsToUpdate = lvl.GetBombs();
				for (Bomb bomb : bombsToUpdate) {
					bomb.MoveBomb(delta);
					bomb.Update(delta, gdxGame.batch);
				}
			}
			
		}
		
		if(lvl.GetEnemies() != null)
		{
			if (!lvl.GetEnemies().isEmpty())
			{
				ArrayList<RegularEnemy> enemiesToUpdate = lvl.GetEnemies();
				for (RegularEnemy enemy : enemiesToUpdate) {
					enemy.Update(delta, gdxGame.batch);
					enemy.LookAtPlayer(player);
					enemy.ShootPlayer(player, delta, lvl, world);
				}
			}
			
		}
		
		if(lvl.GetBoss() != null)
		{
			if (!lvl.GetBoss().isEmpty())
			{
				ArrayList<BossEnemy> BossToUpdate = lvl.GetBoss();
				for (BossEnemy bossEnemy : BossToUpdate) {
					bossEnemy.LookAtPlayer(player);
					bossEnemy.Update(delta, gdxGame.batch);
					bossEnemy.bossMechanics(player, delta, cam, lvl, world);
					
				}
			}
			
		}
	} 
	
	//used for resizing when window size changes
	@Override
	public void resize(int width, int height) {
		gamePort.update(width, height);

	}

	@Override
	public void pause() {
		// TODO Auto-generated method stub

	}

	@Override
	public void resume() {
		// TODO Auto-generated method stub

	}

	@Override
	public void hide() {
		// TODO Auto-generated method stub

	}

	@Override
	public void dispose() {
		// TODO Auto-generated method stub
		world.dispose();

		
	}

	public Viewport getGamePort() {
		return gamePort;
	}

	public void setGamePort(Viewport gamePort) {
		this.gamePort = gamePort;
	}

	public MyGdxGame getGdxGame() {
		return gdxGame;
	}

	public void setGdxGame(MyGdxGame gdxGame) {
		this.gdxGame = gdxGame;
	}
	
	
	/**
	 * Method that moves the camera with the player, keeping the player in the centre of the screen
	 * @param delta
	 */
	private void MoveCameraWithPlayer(float delta)
	{
		Vector2 cameraPosition = new Vector2(player.getBody().getWorldCenter().x, player.getBody().getWorldCenter().y);
		cam.position.x = cameraPosition.x;
		cam.position.y = cameraPosition.y + 50;
	}
	
	/**
	 * Method used to load the next level. 
	 * Increments the level number.
	 * Destroys all bodies from previous level.
	 * levels changeLevel method is called
	 * levels load enemies method is called to add the enemies/boss to the new world
	 * starts the levels loading screen text
	 * Dependent on what level is being loaded, the player is positioned correctly in said level
	 */
	public void LoadNextLevel()
	{
		lvl.levelNumber += 1;
		player.getBody().setLinearVelocity(0, 0);
		lvl.DestroyAllEntities(world);
		lvl.changeLevel(cam, world, player);
		lvl.loadEnemies(cam, world);
		startLevelTextShowing = true;
		world.setContactListener(new Collisions(world, player, lvl));
		
		if(lvl.levelNumber == 1)
		{
			player.getBody().setTransform(new Vector2(0, 0),0);
		}
		if(lvl.levelNumber == 2)
		{
			player.getBody().setTransform(new Vector2(100, 130),0);
		}
		if(lvl.levelNumber == 3)
		{
			player.getBody().setTransform(new Vector2(150, 100),0);
		}
		if(lvl.levelNumber == 4)
		{
			player.getBody().setTransform(new Vector2(220, 50),0);
		}
		if(lvl.levelNumber == 5)
		{
			player.getBody().setTransform(new Vector2(200, 50),0);
		}
	}
	
	/**
	 * Method that sets the screen to the game over screen
	 */
	public void GameOver()
	{
		Score.getScoreInstance().CheckAndSetNewHighScore();
		gdxGame.setScreen(new GameOverScreen(gdxGame));
		dispose();
		lvl.getLevelTracks().forEach(lT -> lT.dispose());
	}
	
	/**
	 * Method that sets the screen to the game complete screen
	 */
	public void gameComplete(){
		Score.getScoreInstance().CheckAndSetNewHighScore();
		dispose();
		gdxGame.setScreen(new GameCompleteScreen(gdxGame));
		lvl.getLevelTracks().forEach(lT -> lT.dispose());
		
	}
	
	/**
	 * Method used to display level/story text
	 * @param delta used to determine the time the text is displayed
	 */
	private void DisplayText(float delta)
	{
		// if total time displayed is less than max, continue displaying text.
		// else reset timer, set bool to false and remove text.
		textTotalTimeDisplayed += delta;
		if (textTotalTimeDisplayed < textMaxTimeisplayed)
		{
			hud.UpdateStartLevelText(lvl.levelNumber);
		}
		else
		{
			startLevelTextShowing = false;
			textTotalTimeDisplayed = 0f;
		}
	}
}
