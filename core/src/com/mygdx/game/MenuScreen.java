package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
/**
 * 
 * @author Neil Millward P13197943
 * Class that represents the games game main menu screen when the player starts the game
 *
 */
public class MenuScreen implements Screen {
	private Stage stage;
	private MyGdxGame gdxGame;
	private Music music;
	private Texture bg;
	private ObjectAnimation runAnimation;
	
	/**
	 * Constructor:
	 * sets up background image
	 * initialises music
	 * sets input to screen so user can select buttons
	 * invisible buttons are set up and positioned behind background 'buttons'
	 * each button switches to either the playscreen options screen or high score screen
	 * 
	 * 
	 * @par
	 */
	public MenuScreen(MyGdxGame game) {
		this.gdxGame = game;
		
		//placed for sprite at bottom left corner of menu, aim is to make it run across screen
		runAnimation = new ObjectAnimation("playerRunShootTp.txt", "Run_Shoot_One", new Vector2(435, 471), 4, 2, 0.15f,
				false);
		bg = new Texture(Gdx.files.internal("bg1.png"));
		
		//Music for menu screen
		music = Gdx.audio.newMusic(Gdx.files.internal("song.mp3"));
		music.stop();
		if (!music.isPlaying()) {
			music.play();
			music.setVolume(0.2f);
		}
		
		//Create stage for menu elements
		stage = new Stage();
		//allows stage to listen for mouse clicks
		Gdx.input.setInputProcessor(stage);


		//set up font, add to button style
		BitmapFont bfont = new BitmapFont();
		TextButtonStyle textButtonStyle = new TextButtonStyle();
		textButtonStyle.font = bfont;

		
		//create textbuttons with the styling and add to stage
		//new game
		final TextButton textButton = new TextButton("                               ", textButtonStyle);
		textButton.setPosition(230, 310);
		stage.addActor(textButton);
		
		
		//high scores
		final TextButton textButton3 = new TextButton("                               ", textButtonStyle);
		textButton3.setPosition(215, 240);
		stage.addActor(textButton3);
		
		//options
		final TextButton textButton4 = new TextButton("                               ", textButtonStyle);
		textButton4.setPosition(245, 180);
		stage.addActor(textButton4);
		

		
		//Attach listeners to buttons in order to navigate between screens
		textButton.addListener(new ChangeListener() {
			public void changed(ChangeEvent event, Actor actor) {
				gdxGame.setScreen(new PlayScreen(gdxGame));
				dispose();
				
			}

		});

		

		textButton3.addListener(new ChangeListener() {
			public void changed(ChangeEvent event, Actor actor) {
				gdxGame.setScreen(new HighScoreScreen(gdxGame));
				dispose();

			}

		});

		textButton4.addListener(new ChangeListener() {
			public void changed(ChangeEvent event, Actor actor) {
				gdxGame.setScreen(new OptionsScreen(gdxGame));
				dispose();
			}

		});
	}

	@Override
	public void show() {
		// TODO Auto-generated method stub

	}

	//renders the menu to screen
	@Override
	public void render(float delta) {
		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		stage.getBatch().begin();
		stage.getBatch().draw(bg, 0, 0,650,500);
		
		//try and make bill run across the screen yo
		//runAnimation.getFrame(delta);
		runAnimation.draw(stage.getBatch());
		
		
		stage.getBatch().end();

	}

	@Override
	public void resize(int width, int height) {

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
		stage.dispose();
		music.dispose();
	}

}
