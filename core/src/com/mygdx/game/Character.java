

package com.mygdx.game;
/**
 * 
 * @author Neil Millward P13197943
 * Class that is inherited by enemy/boss/player to get and set health points
 *
 */
public class Character extends Movable {
private int hp;

	public Character() {
		this.hp = 1;
	}
	public int getHp() {
		return hp;
	}
	public void setHp(int hp) {
		this.hp = hp;
	}

}
