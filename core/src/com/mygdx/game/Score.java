package com.mygdx.game;

import java.util.ArrayList;
import java.util.Collections;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;
import org.w3c.dom.Element;
import java.io.File;
import java.io.IOException;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.NamedNodeMap;
import org.xml.sax.SAXException;
/**
 * 
 * @author Neil Millward P13197943
 * Class that sets the high scores for the game.
 *
 */
class Score {

	//variables used to store each of the 3 high scores and the current score
	Integer highScore1 = 0;
	Integer highScore2 = 0;
	Integer highScore3 = 0;
	Integer currentScore = 0;
	//Array to store the 3 high scores in
	ArrayList<Integer> highScoreList = new ArrayList<Integer>();
	//File path of XML file which keeps high scores in order to reload them upon game restart
	private static String FilePath = "HighScores.xml";
	//sets a new score instance
	private static Score scoreInstance = new Score();
	/**
	 * Method that gets the score instance
	 * @return
	 */
	public static Score getScoreInstance() {return scoreInstance;}
	
	public Score()
	{
	}
	
	/**
	 * Method to GET the high scores.
	 * The High score XML file is loaded to read the 3 high scores to.
	 * The XML files scores are read via the score tags in said file.
	 * The scores from the XML file are read and displayed via being added to the high score list array
	 * 
	 */
	public void GetAndSetHighScores()
	{
		try {

			File fXmlFile = new File(FilePath);
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			Document doc = dBuilder.parse(fXmlFile);

			//normalise just sets the format of the XML document to a niceer layout
			doc.getDocumentElement().normalize();
			NodeList nList = doc.getElementsByTagName("Scores");

			System.out.println("----------------------------");

			for (int temp = 0; temp < nList.getLength(); temp++) {

				Node nNode = nList.item(temp);

				System.out.println("\nCurrent Element :" + nNode.getNodeName());

				if (nNode.getNodeType() == Node.ELEMENT_NODE) {

					Element eElement = (Element) nNode;

					scoreInstance.highScore1 = Integer.valueOf(eElement.getElementsByTagName("highScoreOne").item(0).getFirstChild().getNodeValue());
					scoreInstance.highScore2 = Integer.valueOf(eElement.getElementsByTagName("highScoreTwo").item(0).getFirstChild().getNodeValue());
					scoreInstance.highScore3 = Integer.valueOf(eElement.getElementsByTagName("highScoreThree").item(0).getFirstChild().getNodeValue());
					scoreInstance.highScoreList.add(scoreInstance.highScore1);
					scoreInstance.highScoreList.add(scoreInstance.highScore2);
					scoreInstance.highScoreList.add(scoreInstance.highScore3);
				}
			}
		    } catch (Exception e) {
			e.printStackTrace();
		    }
	}
	
	/**
	 * Method used to SET the high scores to the XML file to then be displayed in game.
	 * 
	 * The empty score tags in the XML file are used to then be added with the 3 scores in the
	 * high score list array.
	 * 
	 * The 3 scores from the array are then written into the high scores XML file
	 */
	public void SetHighScores()
	{
		try {
			DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
			Document doc = docBuilder.parse(FilePath);

			// Get the root element
			Node HighScores = doc.getFirstChild();

			// Get the staff element by tag name directly
			Node scores = doc.getElementsByTagName("Scores").item(0);
			
			// loop the staff child node
			NodeList list = scores.getChildNodes();

			for (int i = 0; i < list.getLength(); i++) {

	           Node node = list.item(i);

			   // get the salary element, and update the value
			   if ("highScoreOne".equals(node.getNodeName())) {
				   node.setTextContent(String.valueOf(scoreInstance.highScoreList.get(0)));
			   }
			   if ("highScoreTwo".equals(node.getNodeName())) {
				   node.setTextContent(String.valueOf(scoreInstance.highScoreList.get(1)));
			   }
			   if ("highScoreThree".equals(node.getNodeName())) {
				   node.setTextContent(String.valueOf(scoreInstance.highScoreList.get(2)));
			   }

			}

			// write the content into xml file
			TransformerFactory transformerFactory = TransformerFactory.newInstance();
			Transformer transformer = transformerFactory.newTransformer();
			DOMSource source = new DOMSource(doc);
			StreamResult result = new StreamResult(new File(FilePath));
			transformer.transform(source, result);

			System.out.println("Done");

		   } catch (ParserConfigurationException pce) {
			pce.printStackTrace();
		   } catch (TransformerException tfe) {
			tfe.printStackTrace();
		   } catch (IOException ioe) {
			ioe.printStackTrace();
		   } catch (SAXException sae) {
			sae.printStackTrace();
		   }
	}
	
	
	/**
	 * Method that checks the high scores and replaces them is the new score is higher than any of the 3 high scores.
	 * 
	 * A temp array is set up that is a clone of the high score list array. The 3 scores in the array are sorted and reversed (to set them in descending order).
	 * If the scores stored in the cloned list are the not equal to the 3 (current) high scores then the original list is set to the value of the cloned list in turn updating the high scores .
	 * 
	 * 
	 */
	public void CheckAndSetNewHighScore()
	{
		ArrayList<Integer> temporaryList = (ArrayList<Integer>) scoreInstance.highScoreList.clone();
		
		temporaryList.add(scoreInstance.currentScore);
		Collections.sort(temporaryList);
		Collections.reverse(temporaryList);
		temporaryList.remove(temporaryList.size()-1);
		
		if (!temporaryList.equals(scoreInstance.highScoreList))
		{
			scoreInstance.highScoreList = temporaryList;
			SetHighScores();
		}
	}
	
	public void SetScore(Integer score)
	{
		scoreInstance.currentScore = score;
	}
	
	public void AddScore(Integer score)
	{
		scoreInstance.currentScore += score;
	}
	
	public Integer GetCurrentScore()
	{
		return scoreInstance.currentScore;
	}
}
