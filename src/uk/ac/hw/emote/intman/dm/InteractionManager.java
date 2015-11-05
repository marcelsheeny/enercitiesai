package uk.ac.hw.emote.intman.dm;

import java.awt.Point;
import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import uk.ac.hw.emote.enercities.GamePlayer;
import uk.ac.hw.emote.intman.Communication;
//attrib -r -h .classpath 
import deva.im.DialogueManager;
import emote.scenario1.Scenario1TaskManager;
import emote.scenario2.Scenario2GameAI;

public class InteractionManager {

	private static InteractionManager _inst = new InteractionManager();

	
	private InteractionManager() {
		PropertyConfigurator.configure("log4j.properties");
		
	}

	public static InteractionManager getInstance() {
		return _inst;
	}

	Logger logger = Logger.getLogger(InteractionManager.class.getName());
	
	private String dialogueScriptFile; //where all the dialogue scripts are..
	
	private String scenarioFile;
	private String scenarioName;
	
	private File scriptsDir, scenariosDir;
	
	private Integer startStep;
	private String language;
	private String recapSkill;
	
	public String lastUttId;
	
	public boolean simulateOtherUsers = false;
	
	public void setStartStep(Integer startStep) {
		this.startStep = startStep;
		logger.info("Scenario 1 will start at step " + startStep);
	}

	// Srini's stuff
	private DialogueManager dm, envSim, ecoSim;
	private String currentUtteranceId;
	private Scenario1TaskManager stm;
	//private UtteranceGen ug;
	private Scenario2GameAI ai;
	private JSONArray utteranceQueue;
	private Boolean interactionOngoing;
	// Hard-coded points for Scenario 2
	private List<Point> points;
	
	private GamePlayer gp;

	public void stop() {
		
		if (lastUttId != null){
		
			Communication.getSpeechPublisher().CancelUtterance (lastUttId);
		}
		/*
		String activityEnded = (String) dm.getStateVariable("activityEnded");
		if (activityEnded.equals("false")){
			//Communication.getSpeechPublisher ().PerformUtterance ("", "Oops. Guess we are out of time. You did well so far. I will see you again on the tablet. Bye.", "");
		}
		
		
		*/
		
		dm.setDStateVariable("interactionStatus", "stopped");
		dm.displayDialogueState();
		
		
		if (simulateOtherUsers){
			envSim.setDStateVariable("interactionStatus", "stopped");
			envSim.displayDialogueState();
			
			ecoSim.setDStateVariable("interactionStatus", "stopped");
			ecoSim.displayDialogueState();
		
		}
		
        dm.close();
		dm = null;
		
		if (simulateOtherUsers){
			envSim.close();
			envSim = null;
			
			ecoSim.close();
			ecoSim = null;
		}
		interactionOngoing = false;
        lastUttId = "null";
		/*
        if (migration){
        	MigrationData.getInstance().setHashMapData(hm);
        }
        */
			
	}
	
	public void init(String startInfo) {
		interactionOngoing = true;
		
		//Communication.getSpeechPublisher ().PerformUtterance ("", "<FACE(awake)>", "");
		
		gp = new GamePlayer();
		
		logger.info ( "start message info: " + startInfo);
		String studentName = "test";
		Boolean isEmpathic = true;
		Boolean isFirstSession = false;
		int studentId = 0;
		String studentGender = "M";
		language = "ENG";
		
		lastUttId = "null";
		
		// Set up all of Srini's components
		dm = new DialogueManager("tutor", new File(".\\im-java-s2"), "scripts\\scenario2simpleIM.xml");
		dm.reset();
		
		if (simulateOtherUsers){
			envSim = new DialogueManager("env", new File(".\\im-java-s2"), "scripts\\scenario2env.xml");
			envSim.reset();
			
			ecoSim = new DialogueManager("eco", new File(".\\im-java-s2"), "scripts\\scenario2eco.xml");
			ecoSim.reset();
		}
		
		currentUtteranceId = "null";
		
		//ug = new UtteranceGen(dir, utteranceScript);
		//ai = new Scenario2GameAI();

		
		
		
		//ug.reset();
	}

	public String getCurrentUtteranceId(){
		return currentUtteranceId;
	}
	
	public void setCurrentUtteranceId(String id) {
		currentUtteranceId = id;
	}

	
	public void setLastUtteranceId(String id){
		lastUttId = id;
		dm.setDStateVariable("lastUtteranceId", id);
	}
	

	public String getLastUtteranceId() {
		return lastUttId;
	}

	
	
	public void updateInput(JSONObject systemInput) {
		// TODO Auto-generated method stub
		if (dm == null){
			logger.info("No active DM!!!!!");
			return;
		}
		
		synchronized(dm) {
			try {
				logger.info("DM Input: " + systemInput.toString(2));
				dm.updateInputVariables(systemInput);
				dm.executeUpdateRules();
				dm.displayDialogueState();
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		if (simulateOtherUsers){
			synchronized(envSim) {
				try {
					logger.info("Env Input: " + systemInput.toString(2));
					envSim.updateInputVariables(systemInput);
					envSim.executeUpdateRules();
					envSim.displayDialogueState();
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			
			synchronized(ecoSim) {
				try {
					logger.info("Eco Input: " + systemInput.toString(2));
					ecoSim.updateInputVariables(systemInput);
					ecoSim.executeUpdateRules();
					ecoSim.displayDialogueState();
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}
	
	
	// TODO change to return List<Utterance>
	public Utterance processInput(JSONObject systemInput) throws JSONException {
		if (dm == null){
			logger.info("No active DM!!!!!");
			return null;
		}
		
		if (systemInput.has("fromModule")){
			if (systemInput.getString("fromModule").equals("enercities")){
				JSONObject gpReco = gp.run(systemInput);
				if (gpReco != null){
					logger.info ("GP out:" + gpReco.toString());
					Iterator<String> k = gpReco.keys();
					while (k.hasNext()){
						String key = k.next();
						if (!key.equals("fromModule")){
							systemInput.put(key, gpReco.get(key));
						}
					}
				}
			}
				
		}
		
		synchronized(dm) {
			
			
			if (interactionOngoing == false){
				return null;
			}
			
			while (dm.isRunning()){
				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			
			try {
				
				while (true) {
					logger.info("DM Input: " + systemInput.toString(2));
					JSONArray resultArray = null;
					
					boolean dialogueActionsInQueue = false; 
					if (systemInput.has("fromModule") && systemInput.getString("fromModule").equals("tts")){
						if (systemInput.has("utteranceStatus") && systemInput.getString("utteranceStatus").equals("delivered")
								&& utteranceQueue != null && utteranceQueue.length() > 0){
							logger.info ("Utterance queue not empty!!!!");
							if (systemInput.getString("utteranceId").equals(lastUttId)){
								logger.info ("Utterance: " + lastUttId +" finished. Trying to retrieve next DA from queue");
								//if utterance is finished and the queue has more utterances, use them..
								resultArray = new JSONArray();
								for (int i = 0; i < utteranceQueue.length(); i++){
									resultArray.put(utteranceQueue.getJSONObject(i));
									dialogueActionsInQueue = true; 
									logger.info ("Retrieved next DA from queue");
								}
							} else {
								logger.info ("Utterance: " + systemInput.getString("utteranceId") +" finished. Ignoring utterance finished message."
										+ "Waiting for " + lastUttId + " to finish.");
								return null;
							}
						}
					}
					
					JSONArray resultArray2 = null;
					JSONArray resultArray3 = null;
					
					//if there are no DA is queue.. then send systemInput to DM..
					if (!dialogueActionsInQueue){
						resultArray = dm.run(systemInput, 0.0);
						if (simulateOtherUsers){
							resultArray2 = envSim.run(systemInput, 0.0);
							resultArray3 = ecoSim.run(systemInput, 0.0);
						}
					} else {
						logger.info ("DM not consulted");
					}
					
					logger.info("DM Output: " + resultArray.toString(2));
					
					if (simulateOtherUsers){
						logger.info("Env Output: " + resultArray2.toString(2));
						logger.info("Eco Output: " + resultArray3.toString(2));
					}
					
					if (resultArray2 != null && resultArray2.length() > 0){
						JSONObject result = resultArray2.getJSONObject(0);
						return executeResult(result);
					}
					
					else if (resultArray2 != null && resultArray3.length() > 0){
						JSONObject result = resultArray3.getJSONObject(0);
						return executeResult(result);
					}
					
					else if (resultArray.length() == 0){ return null;}
					
					else {
						JSONObject result = resultArray.getJSONObject(0);
						
						utteranceQueue = new JSONArray();
						
						if (resultArray.length() > 1){
							for (int i = 1; i < resultArray.length(); i++){
								utteranceQueue.put(resultArray.getJSONObject(i));
								logger.info("Utterance Queue: " + utteranceQueue.toString());
							}
						} 
						
						return executeResult(result);
						
					}
				}
			
			} catch (RuntimeException ex) {
				ex.printStackTrace();
				logger.warn("Some exception:" + ex);
			}
			return null;
		}
	}

	
	private Utterance executeResult(JSONObject result) throws JSONException{
		Utterance nextUtterance = new Utterance();
		
		// Check which module to send it to now
		if (result.has("toModule")) {
			if ("skene".equals(result.get("toModule"))) {
				logger.info("Processing Skene message");
				String commFunction = result
						.getString("communicativeFunction");
				if ("executeGameAction".equals(commFunction)) {
					// Is it an actual action? If so, send it
					// directly
					// to Enercities
					
					int gameActionType = result.getInt("gameActionType");
					
					if (gameActionType == 1) {
						//build structure
						int subType = result.getInt("gameActionSubtype");
						if (subType != 0){
							String structure = getEnercitiesStructure(subType);
							int posX = result.getInt("gamePositionX");
							int posY = result.getInt("gamePositionY");
							logger.info("Construction location: (" + posX + "," + posY + ")");

							Communication.getEnercitiesPublisher().ConfirmConstruction(structure,posX, posY);
							//nextUtterance.content = "Its my turn. I built a " + structure.replaceAll("_", " ");
							//nextUtterance.standalone = true;
						}
						return null;
					}
					else if (gameActionType == 4) {
						//implement policy
						int subType = result.getInt("gameActionSubtype");
						if (subType != 0){
							String policy = getEnercitiesPolicy(subType);
							//Communication.getEnercitiesPublisher().ImplementPolicy(policy);
							//nextUtterance.content = "Its my turn. I am implementing policy.";
							//nextUtterance.standalone = true;
							Communication.getEnercitiesPublisher().ConfirmConstruction("implementPolicy:" + policy,0, 0);
							return null;
						}
						return nextUtterance;
					}
					else if (gameActionType == 2) {
						//upgrade structure				
						int subType = result.getInt("gameActionSubtype");
						if (subType != 0){
							String upgrade = getEnercitiesUpgrades(subType);
							int posX = result.getInt("gamePositionX");
							int posY = result.getInt("gamePositionY");
							//Communication.getEnercitiesPublisher().PerformUpgrade(upgrade, posX, posY);
							//nextUtterance.content = "Its my turn. I am upgrading";
							//nextUtterance.standalone = true;
							Communication.getEnercitiesPublisher().ConfirmConstruction("performUpgrade:" + upgrade,posX, posY);
							
							int gameActionType2 = result.getInt("gameActionType2");
							if (gameActionType2 != 0) {
								int subType2 = result.getInt("gameActionSubtype2");
								String upgrade2 = getEnercitiesUpgrades(subType2);
								int posX2 = result.getInt("gamePositionX2");
								int posY2 = result.getInt("gamePositionY2");
								Communication.getEnercitiesPublisher().ConfirmConstruction("performUpgrade:" + upgrade2,posX2, posY2);
								int gameActionType3 = result.getInt("gameActionType3");
								if (gameActionType3 != 0) {
									int subType3 = result.getInt("gameActionSubtype3");
									String upgrade3 = getEnercitiesUpgrades(subType3);
									int posX3 = result.getInt("gamePositionX3");
									int posY3 = result.getInt("gamePositionY3");
									Communication.getEnercitiesPublisher().ConfirmConstruction("performUpgrade:" + upgrade3,posX3, posY3);
									
								} else {
									Communication.getEnercitiesPublisher().ConfirmConstruction("skipTurn",0, 0);
									return null;
								}
							} else {
								Communication.getEnercitiesPublisher().ConfirmConstruction("skipTurn",0, 0);
								return null;
							}
							
							return null;
						}
						return nextUtterance;
					}
					else if (gameActionType == 0) {
						//skip
						Communication.getEnercitiesPublisher().ConfirmConstruction("skipTurn",0, 0);
						//nextUtterance.content = "I skip.";
						//nextUtterance.standalone = true;
						//return nextUtterance;
						return null;
					}
				} else {
					nextUtterance.content = null;
					
					if (result.has("effectPolarity")){
						commFunction += ";" + result.getString("effectPolarity");
					}
					nextUtterance.cf = commFunction;
					
					Map<String, String> tags = new HashMap<String, String>();
					if (result.has("gameTurnHolder")){
						tags.put("currentPlayerRole", result.getString("gameTurnHolder"));
						tags.put("currentPlayerName", result.getString("gameTurnHolder"));
					}
					if (result.has("gameStructure")){
						tags.put("structure", result.getString("gameStructure" ));
					}
					if (result.has("gameStructure")){
						tags.put("structure", result.getString("gameStructure" ));
					}
					
					tags.put("currentPlayerSide", "left");
					nextUtterance.tags = tags;
					nextUtterance.standalone = false;
					return nextUtterance;
				}

			} else {
				return null;
			}
		} else {
			return null;
		}
		return nextUtterance;
	}
	
	
	private String getEnercitiesUpgrades(int subType) {
		switch(subType){
			case 0: return "NotUsed";
			case 1: return "Solar_Roofs";
			case 2: return "Improved_Insulation";
			case 3: return "Rainwater_Storage";
			case 4: return "Bus_Stop";
			case 5: return "Rooftop_Windmills";
			case 6: return "Thermal_Storage";
			case 7: return "Birdhouse";
			case 8: return "Eco_Roofs";
			case 9: return "Subway_Station";
			case 10: return "Energy_Efficient_Lightbulbs";
			case 11: return "Recycling_Facilities";
			case 12: return "CO2_Reduction_Plan";
			case 13: return "Cradle_2_Cradle";
			case 14: return "Bio_Food";
			case 15: return "Veggie_Food";
			case 16: return "Watch_Tower";
			case 17: return "Forest_Health_Plan";
			case 18: return "Wildlife_Preservation";
			case 19: return "Exhaust_Scrubbers";
			case 20: return "Coal_Washing";
			case 21: return "CO2_Storage";
			case 22: return "Bigger_Rotor_Blades";
			case 23: return "Next_Gen_Solar_Cells";
			case 24: return "Moving_Solar_Pads";
			case 25: return "Improved_Uranium_Storage";
			case 26: return "Uranium_Recycling";
		}
		return "NotUsed";
	}

	private String getEnercitiesPolicy(int subType) {
		
		switch(subType){
			case 0: return "NotUsed";
			case 1: return "CO2_Taxes";
			case 2: return "Electric_Car_Grid";
			case 3: return "Energy_Education_Program";
			case 4: return "Eco_Tourism_Program";
			case 5: return "Sustainable_Technology_Fund";
		}
		return "NotUsed";
	}

	private String getEnercitiesStructure(int subType) {
		switch(subType){
		case 0: 
			return "NotUsed";
		case 1:
	        return "Park";
		case 2:
	        return "Forest";
		case 3:
	        return "Wildlife_Reserve";
		case 4:
	        return "Suburban";
		case 5:
	        return "Urban";
		case 6:
	        return "Stadium";
		case 7:
	        return "Light_Industry";
		case 8:
	        return "Heavy_Industry";
		case 9:
	        return "Commercial";
		case 10:
	        return "Coal_Plant";
		case 11:
	        return "Nuclear_Plant";
		case 12:
		    return "Windmills";
		case 13:
			return "Solar_Plant";
		case 14:
			return "Hydro_Plant";
		case 15:
		    return "City_Hall";
		case 16:
		    return " Coal_Plant_Small";
		case 17:
		    return "Residential_Tower";
		case 18:
		    return "Super_Solar";
		case 19:
		    return "Super_WindTurbine";
		case 20:
		    return "Nuclear_Fusion";
		case 21:
		    return "Market";
		case 22:
		    return "Public_Services";
		}
	    
		
		return "NotUsed";
	}

	public void setRecapInfo(String learnerStateInfo_learnerState) {
		//parsing the learnerStateInfo message
		try {
			JSONObject startInput = new JSONObject(new JSONTokener(learnerStateInfo_learnerState));
			logger.info ( "LearnerStateInfo message decoded: " + startInput.toString());
			JSONArray j = startInput.getJSONArray("competencyItems");
			Double max = 0.0;
			String maxCompetency = "null";
			for (int i=0; i<j.length();i++){
				JSONObject c1 = j.getJSONObject(0);
				Double d = c1.getDouble("comptencyValue");
				if (d >= max){
					max = d;
					maxCompetency = c1.getString("competencyType");
				}
			}
			if (maxCompetency.equals("10")){
				maxCompetency = "distance";
			}
			else if (maxCompetency.equals("11")){
				maxCompetency = "direction";
			}
			else if (maxCompetency.equals("12")){
				maxCompetency = "symbol";
			}
			
			do {
				try {
					Thread.sleep(1000);
					logger.info ( "LearnerStateInfo message waiting for dm to be initialized..");
				} catch (Exception e) {
					e.printStackTrace();
				}
			} while (dm == null);
			
			synchronized(dm){
				recapSkill = maxCompetency;
				if (dm != null && recapSkill != null){
					dm.setDStateVariable("lastSessionSkill", recapSkill);
				} else {
					dm.setDStateVariable("lastSessionSkill", "null");
				}
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	

	

}
