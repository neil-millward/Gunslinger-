package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

/**
 * 
 * @author Neil Millward P13197943
 * Class that represents the in game HUD and also used to display the story/level loading text
 *
 */
public class Hud implements Disposable {
	private Integer playerHp;
	private Integer Score;
	private Integer LevelNumber;

	private Label playerLivesLabel;
	private Label playerHpLabel;
	private Label ScoreLabel;
	private Label levelLabel;
	private Label startLevelTextLabel;
	
	
	private Label lives;
	private Label hp;
	private Label level;
	private Label time;
	
	private static String levelText = "";
	private static String levelOneText = "An evil corporation:\n'Bad Guys Incorporated' is trying to take over "
					+ "the world\nstop them before all hope is lost. It's Gunslinging Time!";
	private static String levelTwoText = "Level Two is Loading";
	private static String levelThreeText = "Level Three is Loading";
	private static String levelFourText = "Level Four is Loading";
	private static String levelFiveText = "Level Five is Loading";

	private Stage stage;

	private Table table;
	private Table textTable;
	private Viewport viewport;
	private SpriteBatch sB;

	/**
	 * Constructor:
	 * 
	 * @param player
	 * @param sB
	 * @param levelNum
	 * @param score
	 */
	public Hud(Player player, SpriteBatch sB, Integer levelNum, Integer score) {

		viewport = new FitViewport(MyGdxGame.getvWidth(), MyGdxGame.getvHeight(), new OrthographicCamera());
		//create stage with a set view, uses  game class spritebatch to draw stage
		stage = new Stage(viewport, sB);

		//table used to store hud elements
		table = new Table();
		table.top();
		table.setFillParent(true);
		
		//table for level loading/story text elements
		textTable = new Table();
		textTable.top();
		textTable.setFillParent(true);
		
		//setting placeholder values
		playerHp = player.getHp();
		Score = score;
		LevelNumber = levelNum;

		//creating elements to be added to top row of HUD
		levelLabel = new Label("Level", new Label.LabelStyle(new BitmapFont(), Color.WHITE));
		playerLivesLabel = new Label("Player Lives", new Label.LabelStyle(new BitmapFont(), Color.WHITE));
		ScoreLabel = new Label("Score", new Label.LabelStyle(new BitmapFont(), Color.WHITE));
		startLevelTextLabel = new Label(levelText, new Label.LabelStyle(new BitmapFont(), Color.WHITE));
		
		//creating elements to be added to second row of HUD
		lives = new Label(String.format("%02d", playerHp),
				new Label.LabelStyle(new BitmapFont(), Color.WHITE));

		level = new Label(String.format("%02d", LevelNumber),
				new Label.LabelStyle(new BitmapFont(), Color.WHITE));

		time = new Label(String.format("%02d", Score),
				new Label.LabelStyle(new BitmapFont(), Color.WHITE));

		//adding elements to table
		table.add(playerLivesLabel).expandX().padTop(10);
		table.add(levelLabel).expandX().padTop(10);
		table.add(playerHpLabel).expandX().padTop(10);
		table.add(ScoreLabel).expandX().padTop(10);
		table.row();
		table.add(lives).expandX();
		table.add(level).expandX();
		table.add(hp).expandX();
		table.add(time).expandX();
		
		textTable.add(startLevelTextLabel).expandX();
		
		//adding table to stage
		stage.addActor(table);
	}

	public Stage getStage() {
		return stage;
	}

	public void setStage(Stage stage) {
		this.stage = stage;
	}

	public SpriteBatch getsB() {
		return sB;
	}

	public void setsB(SpriteBatch sB) {
		this.sB = sB;
	}

	public Integer getPlayerLives() {
		return playerHp;
	}

	public void setPlayerLives(Integer playerLives) {
		this.playerHp = playerLives;
	}
	
	public void SetLevelNumber(Integer levelNum)
	{
		LevelNumber = levelNum;
	}
	
	public void SetScore(Integer score)
	{
		Score = score;
	}
	
	@Override
	public void dispose() {
		stage.dispose();
	}
	
	/**constantly updates HUD elements  by calling the required methods. getHP for example for when the player gets hit to update HP
	 * 
	 * @param delta to constantly update the HUD elements 
	 * @param Player used in order to get the players HP to be dispayed by HUD
	 * @param CurrentScore same as Player except constantly updates the score 
	 * @param CurrentLevel used to set the HUDs level element
	 */
	public void Update(float delta, Player Player, Integer CurrentScore, Integer CurrentLevel)
	{
		//if the HUD does not contain the HUD table then pop the text table and add the hud table
		if(stage.getActors().size > 0 && !stage.getActors().contains(table, false))
		{
			stage.getActors().pop();
			stage.addActor(table);
		}
		
		lives.act(delta);
		playerHp = Player.getHp();
		lives.setText(String.format("%02d", playerHp));
		Score = CurrentScore;
		time.setText(String.format("%02d", Score));
		LevelNumber = CurrentLevel;
		level.setText(String.format("%02d", LevelNumber));
		stage.act(Math.min(Gdx.graphics.getDeltaTime(), 1 / 60f));
		stage.draw();
	}
	
	/**
	 * 
	 * @param levelNumber used to determine what text to display. If level one display story text
	 * else display level loading text
	 */
	public void UpdateStartLevelText(int levelNumber)
	{
		switch(levelNumber)
		{
		case 1:
			startLevelTextLabel.setText(levelOneText);
			break;
		case 2:
			startLevelTextLabel.setText(levelTwoText);
			break;
		case 3:
			startLevelTextLabel.setText(levelThreeText);
			break;
		case 4:
			startLevelTextLabel.setText(levelFourText);
			break;
		case 5:
			startLevelTextLabel.setText(levelFiveText);
			break;
		}
		//if the HUD does not contain the text table then pop the HUD table and add the text table
		if(stage.getActors().size > 0 && !stage.getActors().contains(textTable, false))
		{
			stage.getActors().pop();
			stage.addActor(textTable);
		}
		
		stage.act(Math.min(Gdx.graphics.getDeltaTime(), 1 / 60f));
		stage.draw();
	}
}
