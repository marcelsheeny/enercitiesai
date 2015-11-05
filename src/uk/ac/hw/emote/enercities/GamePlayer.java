package uk.ac.hw.emote.enercities;

import java.util.ArrayList;
import java.util.Map.Entry;

import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.sun.corba.se.impl.ior.GenericTaggedComponent;

import uk.ac.hw.emote.intman.dm.InteractionManager;

public class GamePlayer {
	
	
	Logger logger = Logger.getLogger(GamePlayer.class.getName());
	Database db;
	public GamePlayer(){
		db = new Database();
	}
	
	public Vector2 selectLocation(int level)
	{
		int X = db.getGridSizeX();
		int Y = db.getGridSizeY();
		
		//TODO CHECK IF IT IS RIVER OR ASFSDS TO SET PLAYABLE OR NOT
		ArrayList<Vector2> positions = new ArrayList<>();
		for (int x = 0; x < X; x++)
		{
			for (int y = 0; y < Y; y++)
			{
				logger.info("[MARCEL] cellx: " + x);
				logger.info("[MARCEL] celly: " + y);
				logger.info("[MARCEL] cell level: " + db.getCell(x, y).getLevel());
				//logger.info("[MARCEL] cell is built: " + db.getCell(x, y).getConstruction().isBuilt());
				logger.info("[MARCEL] cell playable: " + db.getCell(x, y).isPlayable());
				if (db.getCell(x, y).getLevel() == level &&
					//!db.getCell(x, y).getConstruction().isBuilt() &&
					db.getCell(x, y).isPlayable())
				{
					Vector2 pos = new Vector2();
					pos.setX(x);
					pos.setY(y);
					positions.add(pos);
					logger.info("[MARCEL] CELL ADDED!!!!!!!");
				}
			}
		}
		return positions.get((int)(Math.floor(Math.random()*positions.size())));
	}

	public Action selectAction(int level, String role, float money)
	{
		ArrayList<Integer> possibleActions = new ArrayList<>();
		possibleActions.add(0); //Skip
		possibleActions.add(1); //build structure
		//possibleActions.add(2); //upgrade
		possibleActions.add(4); //policy
	
		Action action = new Action();
		
		int mainAction = possibleActions.get((int)(Math.floor(Math.random()*possibleActions.size())));
	
		
		logger.info("[MARCEL] Main Action" + mainAction);
		
		action.setAction(mainAction);
		
		switch (mainAction) {
		case 0:  //skip
			//action.setBestActionIdentified("true");
			logger.info("[MARCEL] SKIPED!!!!!!!!!!");
			return action;
		case 1:  //build structure;
			int a = 0;
			Vector2 pos = new Vector2();
			while (a==0)
			{
				pos = selectLocation(level);
				logger.info("[MARCEL] BUILDING SELECTED!!!");
				logger.info("[MARCEL] posX: " + pos.getX());
				logger.info("[MARCEL] posY: " + pos.getY());
				a = selectStructure(level, role, money, pos.getX(), pos.getY());
				logger.info("[MARCEL] construction id: " + a);	
			}
			db.getCell(pos.getX(), pos.getY()).setBuilt(true);
			action.setPosx(Integer.toString(pos.getX()));
			action.setPosy(Integer.toString(pos.getY()));
			action.setSubaction(a);	
			return action;
		case 2:  //upgrade structure;
			return null;
		case 4:  //implement policy;
			action.setSubaction(selectPolicy(money));
			return action;
		}
		return action;
	}
	
	public int selectPolicy(float money)
	{
		ArrayList<Policy> possiblePolicies = new ArrayList<>();
		for (Entry<String,Policy> e : db.getPolicies().entrySet())
		{
			Policy p = db.getPolicies().get(e.getKey());
			if (!p.isImplemented() && p.getCost()<money)
			{
				possiblePolicies.add(p);
			}
		}
		int a = possiblePolicies.get((int)(Math.floor(Math.random()*possiblePolicies.size()))).getId();
		possiblePolicies.get(a).setImplemented(true);
		return a;
		
	}
	
	//TODO: put that isBuilt for constructed Structures
	public int selectStructure(int level, String role, float money, int posX, int posY)
	{
		ArrayList<Construction> possibleStructures = new ArrayList<>();
		String field = db.getCell(posX, posY).getType();
		for (Entry<String,Construction> e : db.getConstructions().entrySet())
		{
			Construction c = db.getConstructions().get(e.getKey());
			if (c.getLevel() == level)
			{
				if (c.getRole().equals("All") || c.getRole().equals(role))
				{
					if (c.getPrice()<money)
					{
						if (isInsideArray(c.getField(),field))
						{
							possibleStructures.add(c);
						}
					}
				}
			}
		}
		if (!possibleStructures.isEmpty())
		{
			Construction a = possibleStructures.get((int)(Math.floor(Math.random()*possibleStructures.size())));
			db.setCell(posX, posY, a);
			return possibleStructures.get((int)(Math.floor(Math.random()*possibleStructures.size()))).getId();
		}
		else
		{
			return 0;
		}	
}
	
	public boolean isInsideArray(ArrayList<String> a,String s)
	{
		for (String str : a)
		{
			if (str.equals(s))
					{
						return true;
					}
		}
		return false;
	}
	
	public JSONObject run(JSONObject gameUpdate) throws JSONException{
		
		//GamePlayer update:{"fromModule":"enercities","gameStructure":"Coal_Plant_Small","cellX":2,"cellY":1,"gameStructureConstructed":"true"}
		//GamePlayer update:{"fromModule":"enercities","upgrade":"improved_insulation","cellX":5,"cellY":1,"structureUpgraded":"true"}
		//GamePlayer update:{"fromModule":"enercities","policyImplemented":"true","policy":"sustainable_technology_fund"}
		//GamePlayer update:{"fromModule":"enercities","turnSkipped":"true"}
		//GamePlayer update:{"fromModule":"enercities","EnvironmentScore":2,"EconomyScore":2,"gameTurnHolder":"Environmentalist","WellbeingScore":2,"turnChanged":"true","MoneyEarning":2,"PowerConsumption":0,"Money":104,"Oil":1800,"Population":1,"TargetPopulation":15,"Level":1,"PowerProduction":4,"GlobalScore":6}
		
		logger.info("GamePlayer update:" + gameUpdate.toString());
		
		// here u get all the enercities game updates from IM.
		// use it to update ur learning agent
		
		if (gameUpdate.has("gameStructure") && gameUpdate.getString("gameStructureConstructed").equals("true"))
		{
			String structure = gameUpdate.getString("gameStructure");
			int code = db.getStructureCode(structure);
			
			int x = gameUpdate.getInt("cellX");
			int y = gameUpdate.getInt("cellY");
			
			
			db.setCell(x, y, db.getConstruction(structure).copy());
			db.getCell(x, y).setBuilt(true);
		}
		/*
		if (gameUpdate.has("upgrade") && gameUpdate.getString("structureUpgraded").equals("true"))
		{
			String upgrade = gameUpdate.getString("upgrade");
			int code = db.getUpgradeCode(upgrade);
			int cellx = gameUpdate.getInt("cellX");
			int celly = gameUpdate.getInt("cellY");
			
			
			db.setCell(x, y, db.getConstruction(structure).copy());
			db.getCell(x, y).setBuilt(true);
		}
*/		
		if (gameUpdate.has("turnChanged") && gameUpdate.getString("turnChanged").equals("true")){
			
			
			
			/// need a game action decision here..
			JSONObject gpOut = new JSONObject();
			
			int level = gameUpdate.getInt("Level");
			String role = gameUpdate.getString("gameTurnHolder");
			float money = (float)(gameUpdate.getDouble("Money"));

			logger.info("GamePlayer update:" + gameUpdate.toString());
			
			logger.info("[MARCEL] Money: " + money);
			logger.info("[MARCEL] role: " + role);
			logger.info("[MARCEL] level: " + level);
			
			if (role.equals("Mayor"))
			{
				Action action = selectAction(level,role,money);
				
				logger.info("[MARCEL] best action: " + action.getBestActionIdentified());
				logger.info("[MARCEL] Action: " + action.getAction());
				logger.info("[MARCEL] Subaction: " + action.getSubaction());
				logger.info("[MARCEL] PosX: " + action.getPosx());
				logger.info("[MARCEL] posY: " + action.getPosy());
				
				gpOut.put("bestActionIdentified", action.getBestActionIdentified());
				gpOut.put("bestActionType", action.getAction());
				gpOut.put("bestActionSubtype", action.getSubaction());
				gpOut.put("bestActionPosX", action.getPosx());
				gpOut.put("bestActionPosY", action.getPosy());
	
				if (action.getAction() == 2 ) //(i.e. upgrade) then use the following to communicate the other two upgrade actions
				{
					
					gpOut.put("bestActionType2", action.getAction2());
					gpOut.put("bestActionSubtype2", action.getSubaction2());
					gpOut.put("bestActionPosX2", action.getPosx2());
					gpOut.put("bestActionPosY2", action.getPosy2());
									
					gpOut.put("bestActionType3", action.getAction3());
					gpOut.put("bestActionSubtype3", action.getSubaction3());
					gpOut.put("bestActionPosX3", action.getPosx3());
					gpOut.put("bestActionPosY3", action.getPosy3());
					
				}
			}
			else
			{
				gpOut.put("bestActionType", 0);
				gpOut.put("bestActionSubtype", 0);
				gpOut.put("bestActionPosX", 0);
				gpOut.put("bestActionPosY", 0);
			}

			
			return gpOut;
		} 
		
		
		return null;
		
	}
	
}
