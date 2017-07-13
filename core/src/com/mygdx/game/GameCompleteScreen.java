package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
/**
 * 
 * @author Neil Millward P13197943
 * Class that represents the games game complete screen when the player completes the game
 *
 */
public class GameCompleteScreen implements Screen{
	
		private Stage stage; // used in order to display relevant menu objects (background, buttons) 
		private MyGdxGame gdxGame; // used to fetch the classes sprite batch for drawing menu elements
		private Music music; //menu music
		private Texture bg; // Texture object for background image
		
		/**
		 * Constructor:
		 * sets up background image
		 * initialises music
		 * sets input to screen so user can select buttons
		 * invisible buttons are set up and positioned behind background 'buttons'
		 * 
		 * @param game used to get MyGdxGame sprite batch
		 */
		public GameCompleteScreen(MyGdxGame game) {
			bg = new Texture(Gdx.files.internal("completeScreen.png"));

			this.gdxGame = game;
			//Music for menu screen
			this.music = Gdx.audio.newMusic(Gdx.files.internal("win.mp3"));
			this.music.stop();
			if (!this.music.isPlaying()) {
				this.music.play();
				this.music.setVolume(0.9f);
			}
			
			//Create stage for menu elements
			stage = new Stage();
			//allows stage to listen for mouse clicks
			Gdx.input.setInputProcessor(stage);


			//set up font, add to button style
			BitmapFont bfont = new BitmapFont();
		
			
			TextButtonStyle textButtonStyle = new TextButtonStyle();
			textButtonStyle.font = bfont;
			
			
			
			final TextButton textButton2 = new TextButton(""+Score.getScoreInstance().GetCurrentScore(), textButtonStyle);
			textButton2.setPosition(380, 160);
			stage.addActor(textButton2);
			//create textbuttons with the styling and add to stage
			final TextButton textButton = new TextButton("          ", textButtonStyle);
			textButton.setPosition(0, 50);
			stage.addActor(textButton);
			
			//Attach listeners to buttons in order to navigate between screens
			textButton.addListener(new ChangeListener() {
				public void changed(ChangeEvent event, Actor actor) {
					gdxGame.setScreen(new MenuScreen(gdxGame));
					dispose();
					
				}

			});
			textButton2.addListener(new ChangeListener() {
				public void changed(ChangeEvent event, Actor actor) {
					
					
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
			stage.getBatch().end();
			
			
			stage.draw();

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

		//dispose of sound and stage
		@Override
		public void dispose() {
			stage.dispose();
			music.dispose();
		}

	}

