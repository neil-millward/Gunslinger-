package com.mygdx.game;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
/**
 * 
 * @author Neil Millward P13197932
 *Class that is used to implement animations into the game. Extends sprite due to requiring the classes
 *methods for sprite/animation construction
 */
public class ObjectAnimation extends Sprite{
	//texture atlas for storing passed sprite sheet from texture packer tool
	private TextureAtlas textureAtlas;
	//used to store a region from the texture atlas to start animating from
	private TextureRegion region;
	//region to be stored in the previous variable
	private String regionString;
	//used to store size of sprite
	private Vector2 imageSize;
	//the relevant animation is stored into active animation variable
	private Animation activeAnimation;
	//used to determine speed of animation
	private float animationSpeed;
	//used to update timeS
	private float stateTimer;

	/**
	 * Constructor:
	 * initialies variables to default values
	 */
	public ObjectAnimation()
	{
		this.textureAtlas = new TextureAtlas();
		this.regionString = "";
		this.imageSize = new Vector2(0,0);
		this.animationSpeed = 0;
		this.stateTimer = 0;
	}
	
	/**
	 * Method sets up class variables by the passed parameters.
	 * an array is created which is used to store the required animation frames for animating
	 * the passed texture atlas is looped through via the region string passed (to determine start position)
	 * the rows and columns are used with the image size to loop through the sprite sheet. the gathered regions are then added to the frames array.
	 * The animation is then set (has to be set or null pointer error)
	 * Finally the frames array is cleared for the next animations textures
	 * 
	 * @param atlasName name of passed texture atlas (sprite sheet)
	 * @param RegionString region from which to animate from
	 * @param ImageSize size of images in region
	 * @param NumberOfColumns number of columns of texture atlas
	 * @param NumberOfRows number of rows of texture atlas
	 * @param AnimationSpeed speed of which the animation plays at
	 * @param LoopAnim whether or not the animation needs to loop
	 */
	public ObjectAnimation(String atlasName, String RegionString,  Vector2 ImageSize, int NumberOfColumns, int NumberOfRows, float AnimationSpeed, boolean LoopAnim) {
		
		this.textureAtlas = new TextureAtlas(atlasName);
		this.regionString = RegionString;
		this.imageSize = ImageSize;
		this.animationSpeed = AnimationSpeed;
		this.stateTimer = 0;
		Array<TextureRegion> frames = new Array<TextureRegion>();
		
		TextureRegion tempRegion = new TextureRegion();
		// i = row number
		for (int i = 1; i <= NumberOfRows; i++) {
			// c = column number
			for (int c = 1; c <= NumberOfColumns; c++) {
				
				frames.add(new TextureRegion(textureAtlas.findRegion(regionString), ((c * (int)imageSize.x) - ((int)imageSize.x - 1)), ((i * (int)imageSize.y) - ((int)imageSize.y - 1)), (int)imageSize.x,(int)imageSize.y));
				tempRegion = new TextureRegion(textureAtlas.findRegion(regionString), ((c * (int)imageSize.x) - ((int)imageSize.x - 1)), ((i * (int)imageSize.y) - ((int)imageSize.y - 1)), (int)imageSize.x,(int)imageSize.y);
			}
			
		}
		activeAnimation = new Animation(animationSpeed, frames);
		if (LoopAnim)
		{
			activeAnimation.setPlayMode(Animation.PlayMode.LOOP);
		}
		frames.clear();
		
		this.setBounds(0, 0, 32, 32);
		this.setRegion(tempRegion);
		this.setPosition(0,0);
	}

	public TextureAtlas getAtlas() {
		return this.textureAtlas;
	}

	public void setAtlas(TextureAtlas atlas) {
		this.textureAtlas = atlas;
	}

	public void setAnimation() {

	}
	/**
	 * This method gets the regions from the active animation and loops through them
	 * to simulate animation (think of a flip book)
	 * 
	 * @param dt used in order to change animation frames over time
	 * @return the regions used to animate
	 */
	public TextureRegion getFrame(float dt) {
		
		region = this.activeAnimation.getKeyFrame(this.stateTimer, true);
		// does current state equal previous state? if it does then statetimer +
		// dt else = 0
		this.stateTimer =  this.stateTimer + dt;
		return region;
	}
}
