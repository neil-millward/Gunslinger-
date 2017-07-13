package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
/**
 * 
 * @author Neil Millward P13197943
 * Class that represents the games options screen 
 *
 *
 */
public class OptionsScreen implements Screen {
	private Stage stage;
	MyGdxGame gdxGame;
	private Texture bg;

	/**
	 * Constructor:
	 * sets up background image
	 * initialises music
	 * sets input to screen so user can select buttons
	 * invisible buttons are set up and positioned behind background 'buttons'
	 * 
	 * 
	 * @param game used to get MyGdxGame sprite batch
	 */
	public OptionsScreen(MyGdxGame game) {
		this.gdxGame = game;
		bg = new Texture(Gdx.files.internal("bg13.png"));

		stage = new Stage();
		Gdx.input.setInputProcessor(stage);
		stage.draw();

		BitmapFont bfont = new BitmapFont();
		TextButtonStyle textButtonStyle = new TextButtonStyle();
		textButtonStyle.font = bfont;

		

		final TextButton textButton = new TextButton("hi",textButtonStyle);
		textButton.setPosition(200, 200);
		stage.addActor(textButton);

		final TextButton textButton2 = new TextButton("               ", textButtonStyle);
		textButton2.setPosition(0, 50);
		stage.addActor(textButton2);

		textButton.addListener(new ChangeListener() {
			public void changed(ChangeEvent event, Actor actor) {
				
			}
		});

		textButton2.addListener(new ChangeListener() {
			public void changed(ChangeEvent event, Actor actor) {
				gdxGame.setScreen(new MenuScreen(gdxGame));
			}
		});
	}

	@Override
	public void show() {
		// TODO Auto-generated method stub

	}

	@Override
	public void render(float delta) {
		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		stage.getBatch().begin();
		stage.getBatch().draw(bg, 0, 0,650,500);
		stage.getBatch().end();
		//stage.draw();

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
		stage.clear();
		stage.dispose();

	}

}
