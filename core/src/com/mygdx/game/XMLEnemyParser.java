package com.mygdx.game;

import java.util.ArrayList;

import java.util.List;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import com.badlogic.gdx.graphics.Camera;

/**
 * @author Neil Millward P13197943
 * Class used for parsing enemies and bosses to and from levels. This Is achieved via reading a set up XML file
 * 
 */
public class XMLEnemyParser extends DefaultHandler{

	
	//array of regular enemies
	public ArrayList<RegularEnemy> enemyList = new ArrayList<RegularEnemy>();
	//array of boss enemies
	public ArrayList<BossEnemy> bossList = new ArrayList<BossEnemy>();
	//regular enemy object
	private RegularEnemy enemy;
	//boss object
	private BossEnemy boss;
	private String currentEnemy;
	Levels level;
	boolean createEnemy = false;
	boolean hasUserData = false;
	boolean hasPositionX = false;
	boolean hasPositionY = false;
	boolean hasHP = false;
	boolean hasShootDistance = false;
	boolean hasShootDelay = false;
	boolean hasBossAbility = false;
	String currentUserData;
	Integer currentPositionX = 0;
	Integer currentPositionY = 0;
	Integer currentHP = 1;
	Integer currentShootDistance = 0;
	Integer currentShootDelay = 0;
	Integer currentBossAbilities = 0;
	
	/**
	 * Constructor:
	 * Sets the level to the current games level. Used to add enemies to the correct level
	 * @param Level used to add enemies/boss to the correct levels world
	 */
	public XMLEnemyParser(Levels Level)
	{	
		
		level = Level;
	}
	
	
	/**
	 * Method that finds the headings of the XML file and sets a boolean to true if they are present.
	 * The bools are used in the characters method to determine where in the XML document to get the required values.
	 * 
	 */
	@Override
	public void startElement(String uri, String localName, String qName, Attributes attributes)
	      throws SAXException {
	      if (qName.equalsIgnoreCase("Enemy")|| qName.equalsIgnoreCase("Boss")) {
	    	  createEnemy = true;
	      } else if (qName.equalsIgnoreCase("userData")) {
	    	  hasUserData = true;
	      } else if (qName.equalsIgnoreCase("posX")) {
	         hasPositionX = true;
	      } else if (qName.equalsIgnoreCase("posY")) {
	    	  hasPositionY = true;
	      }
	      else if (qName.equalsIgnoreCase("HP")) {
	    	  hasHP = true;
	      }
	      else if (qName.equalsIgnoreCase("ShootDistance")) {
	    	  hasShootDistance = true;
	      }
	      else if (qName.equalsIgnoreCase("ShootDelay")) {
	    	  hasShootDelay = true;
	      }
	      else if (qName.equalsIgnoreCase("BossAbility")){
	    	  hasBossAbility = true;
	      }
	    	  
	      
	   }
	
	// This is the method used for the end of an element (i.e. Player, so it needs to have an </Enemy> in xml) Here we set its anim position, and add it to list of enemies.
	/**
	 * Method that determines the end of the element by evaluating each closing heading of each enemy/boss. If the end tags are boss/ enemy then the
	 * boss/enemy object(s) are added to an array which is evaluated in the level classes 'loadEnemies' method
	 */
	   @Override
	   public void endElement(String uri, 
	   String localName, String qName) throws SAXException {
	      if (qName.equalsIgnoreCase("Enemy")) {
	    	 enemy = new RegularEnemy(currentHP, currentShootDistance, currentShootDelay);
	    	 enemy.getBodyDef().position.x = currentPositionX;
	    	 enemy.getBodyDef().position.y = currentPositionY;
	    	 enemy.setMovablePosition(currentPositionX + (enemy.getWidth() / 2), currentPositionY + (enemy.getHeight() / 2));
	    	 enemyList.add(enemy);
	         System.out.println("End Element :" + qName);
	      }
	      else if(qName.equalsIgnoreCase("Boss")){
	    	  boss = new BossEnemy(level, currentHP, currentShootDistance, currentShootDelay, currentBossAbilities);
	    	  boss.getBodyDef().position.x = currentPositionX;
	    	  boss.getBodyDef().position.y = currentPositionY;
	    	  boss.setMovablePosition(currentPositionX + (boss.getWidth() / 2), currentPositionY + (boss.getHeight() / 2));
		    	 bossList.add(boss);
		    	 System.out.println(bossList.size());
		         System.out.println("End Element :" + qName +boss.getHp());
	      }
	   }
	   
	   /**
	    *  This converts the characters found within tags in the XML doc, this is why bools need to be set in StartElement method as it will go into this one to actually get the required values
	    */
	   @Override
	   public void characters(char ch[],int start, int length) throws SAXException {
	      // userdata - Not currently used as it has to be set after adding to world (all of this can probably be done in EndElement instead of in playerscreen)
		   if (hasUserData) {
	    	  hasUserData = false;
	      } else if (hasPositionX) {
	    	 // gets current position from xml doc and sets the body def position.
	    	 currentPositionX = Integer.valueOf(new String(ch, start, length));
	    	 // you have to set the bool back to false.
	    	 hasPositionX = false;
	      } else if (hasPositionY) {
	    	  currentPositionY = Integer.valueOf(new String(ch, start, length));
	         hasPositionY = false;
	      } else if (hasHP) {
		    	  currentHP = Integer.valueOf(new String(ch, start, length));
		          hasHP = false;
	      }else if (hasShootDistance) {
	    	  currentShootDistance = Integer.valueOf(new String(ch, start, length));
	    	  hasShootDistance = false;
	      }else if (hasShootDelay) {
    	  currentShootDelay = Integer.valueOf(new String(ch, start, length));
    	  hasShootDelay = false;
	      }else if (hasBossAbility) {
	    	  currentBossAbilities = Integer.valueOf(new String(ch, start, length));
	    	  hasBossAbility = false;
	      }
	   }
}
