package uk.ac.hw.emote.enercities;

import java.io.File;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.Map;
import java.util.Map.Entry;
import java.util.HashMap;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class Database {

	private Map<String,Construction> constructions;
	private Map<String,Upgrade> upgrades;
	private Map<String,Policy> policies;
	private Cell[][] grid;
	private int gridSizeX = 10;
	private int gridSizeY = 7;
	private String pathGrid = "EnercitiesAI/EnercitiesData/Level/grid.xml";
	private String pathStructures = "EnercitiesAI/EnercitiesData/Level/structures.xml";
	private String pathUpgrades = "EnercitiesAI/EnercitiesData/Level/upgrades.xml";
	private String pathStructureUpgrades = "EnercitiesAI/EnercitiesData/Level/structureupgrades.xml";
	private String pathSurfaces = "EnercitiesAI/EnercitiesData/Level/surfaces.xml";
	private String pathPolicies = "EnercitiesAI/EnercitiesData/Level/policies.xml";

	
	public Cell getCell(int x, int y)
	{
		return grid[x][y];
	}
	
	public int getGridSizeX()
	{
		return gridSizeX;
	}
	
	public int getGridSizeY()
	{
		return gridSizeY;
	}

	
	public Database ()
	{
		constructions = new HashMap<>();
		upgrades = new HashMap<>();
		policies = new HashMap<>();
		setGrid(new Cell[gridSizeX][gridSizeY]);
		
		init();
		
	}
	
	public Map<String, Upgrade> getUpgrades() {
		return upgrades;
	}


	public void setUpgrades(Map<String, Upgrade> upgrades) {
		this.upgrades = upgrades;
	}


	public Map<String, Policy> getPolicies() {
		return policies;
	}


	public void setPolicies(Map<String, Policy> policies) {
		this.policies = policies;
	}

	
	
	private void readConstructions()
	{
		try {
			File fXmlFile = new File(pathStructures);
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = dbf.newDocumentBuilder();
			Document doc = dBuilder.parse(fXmlFile);
			doc.getDocumentElement().normalize();
			
			NodeList nList = doc.getElementsByTagName("structure");
			
			for (int i = 0; i < nList.getLength()-1; i++)
			{
				Node nNode = nList.item(i);
				Element element = (Element) nNode;
				
				Construction c = new Construction();
				
				c.setName(element.getAttribute("name"));
				String cat = element.getElementsByTagName("category").item(0).getTextContent();
				
				if (cat.equals("Environment"))
				{
					c.setRole("Environmentalist");
				}
				else if (cat.equals("Residential") || cat.equals("Energy"))
				{
					c.setRole("All");
				}
				else if (cat.equals("Wellbeing"))
				{
					c.setRole("Mayor");
				}
				else if (cat.equals("Economy"))
				{
					c.setRole("Economist");
				}
				int level = Integer.parseInt(element.getElementsByTagName("unlocklevel").item(0).getTextContent());
				c.setLevel(level);
				
				float price = Float.parseFloat(element.getElementsByTagName("buildingcost").item(0).getTextContent());
				c.setPrice(price);
				
				
				//System.out.println("DATABASE ITEM "+ i);
				//System.out.println(c.getName());
				//System.out.println(c.getPrice());
				//System.out.println(c.getRole());
				//System.out.println(c.getLevel());
				//System.out.println();
				
				constructions.put(c.getName(), c);
			}
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		Construction city = constructions.get("City_Hall");
		city.canBeBuilt(false);
		city.setField(new ArrayList<String>());
		constructions.put("City_Hall",city);
	}
	
	private void readStructures()
	{
		readConstructions();
		readSurfaces();
	}

	
	private void readMapGrid()
	{
		try {
			File fXmlFile = new File(pathGrid);
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = dbf.newDocumentBuilder();
			Document doc = dBuilder.parse(fXmlFile);
			doc.getDocumentElement().normalize();
			
			NodeList nListUnit = doc.getElementsByTagName("unit");
			NodeList nListSurface = doc.getElementsByTagName("surface");
			NodeList nListStructure = doc.getElementsByTagName("structure");
			
			for (int i = 0; i < nListUnit.getLength(); i++)
			{
				Node nNode = nListUnit.item(i);
				Element element = (Element) nNode;
				
				Cell c = new Cell();
				int x = Integer.parseInt(element.getAttribute("x"));
				int y = Integer.parseInt(element.getAttribute("y"));
				int l = Integer.parseInt(element.getAttribute("level"));
			
				
				Node nNodeSurface = nListSurface.item(i);
				Element elementSurface = (Element) nNodeSurface;
				String type = elementSurface.getAttribute("type");
				
				Node nNodeStructure = nListStructure.item(i);
				Element elementStructure = (Element) nNodeStructure;
				String struct = elementStructure.getAttribute("name");
				
				//private String type;
				//private int level;
				//private boolean playable;
				//private Construction construction = null;
				
				Cell cell = new Cell();
				cell.setType(type);
				cell.setLevel(l);
				
				if (struct.equals(""))
				{
					cell.setPlayable(true);
				}
				else
				{
					cell.setConstruction(constructions.get(struct));
					cell.setPlayable(false);
				}
				
				
				grid[x][y] = cell;
				
				//System.out.println("NUmber of Elements"+i);
				//System.out.println("DATABASE ITEM "+ 0);
				//System.out.println(x);
				//System.out.println(y);
				//System.out.println(l);
				//System.out.println(type);
				//System.out.println(struct);
				//System.out.println();
				
				
			}
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	
		for (int x = 0; x < gridSizeX; x++)
		{
			for (int y = 0; y < gridSizeY; y++)
			{
				if (grid[x][y] == null)
				{
					grid[x][y] = new Cell();
				}
			}
		}
		
		
		
	}
	
	private void readPolicy()
	{
		try {
			File fXmlFile = new File(pathPolicies);
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = dbf.newDocumentBuilder();
			Document doc = dBuilder.parse(fXmlFile);
			doc.getDocumentElement().normalize();
			
			NodeList nList = doc.getElementsByTagName("policy");
			
			for (int i = 1; i < nList.getLength(); i++)
			{
				Node nNode = nList.item(i);
				Element element = (Element) nNode;
				String name = element.getAttribute("type");
				
				float cost = Float.parseFloat(element.getElementsByTagName("researchcost").item(0).getTextContent());
				//System.out.println();
				//System.out.println(name);
				//System.out.println(cost);
				//System.out.println();
			
				Policy p = new Policy();
				p.setName(name);
				p.setCost(cost);
				
				
				policies.put(name, p);
				
			}
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}

	private void readUpgrades()
	{
		readUpgradeFile();
		readStructureUpgradeFile();
	}
	
	private void readSurfaces()
	{
		try {
			File fXmlFile = new File(pathSurfaces);
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = dbf.newDocumentBuilder();
			Document doc = dBuilder.parse(fXmlFile);
			doc.getDocumentElement().normalize();
			
			NodeList nList = doc.getElementsByTagName("surface");
			
			for (int i = 1; i < nList.getLength(); i++)
			{
				Node nNode = nList.item(i);
				Element element = (Element) nNode;
				String name = element.getAttribute("name");
				
				Node nodeBuild = element.getElementsByTagName("build-rules").item(0);
				Element elementBuild = (Element) nodeBuild;
				NodeList nodeStructures = elementBuild.getElementsByTagName("structure");
				
				//System.out.println("DATABASE ITEM "+ i);
				//System.out.println(name);
				ArrayList<String> structures = new ArrayList<>();
				for (int j = 0; j < nodeStructures.getLength(); j++)
				{
					//System.out.println(name);
					String supp = nodeStructures.item(j).getTextContent();
					structures.add(supp);
					//System.out.println(supp);
					Construction c = constructions.get(supp);
					c.addField(name);
					constructions.put(supp, c);
				}
				//System.out.println();
						
				//System.out.println();
				//System.out.println(c.getRole());
				//System.out.println(c.getLevel());
				//System.out.println();
				
				
			}
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
	}
	
	private void readStructureUpgradeFile()
	{
		try {
			File fXmlFile = new File(pathStructureUpgrades);
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = dbf.newDocumentBuilder();
			Document doc = dBuilder.parse(fXmlFile);
			doc.getDocumentElement().normalize();
			
			NodeList nList = doc.getElementsByTagName("upgrade");
			
			for (int i = 1; i < nList.getLength(); i++)
			{
				Node nNode = nList.item(i);
				Element element = (Element) nNode;
				String name = element.getAttribute("name");
				
				NodeList nodeStructures = element.getElementsByTagName("supportedstructure");
				//System.out.println("DATABASE ITEM "+ i);
				//System.out.println(name);
				ArrayList<String> structures = new ArrayList<>();
				for (int j = 0; j < nodeStructures.getLength(); j++)
				{
					//System.out.println(name);
					String supp = element.getElementsByTagName("supportedstructure").item(j).getTextContent();
					structures.add(supp);
					//System.out.println(supp);
				}
				
				
				Upgrade u = upgrades.get(name);
				u.setSupportedStructures(structures);
				upgrades.put(name, u);
			
				//System.out.println();
				//System.out.println(c.getRole());
				//System.out.println(c.getLevel());
				//System.out.println();
				
				
			}
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
	}
	
	
	private void readUpgradeFile()
	{
		try {
			File fXmlFile = new File(pathUpgrades);
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = dbf.newDocumentBuilder();
			Document doc = dBuilder.parse(fXmlFile);
			doc.getDocumentElement().normalize();
			
			NodeList nList = doc.getElementsByTagName("upgrade");
			
			for (int i = 0; i < nList.getLength(); i++)
			{
				Node nNode = nList.item(i);
				Element element = (Element) nNode;
				
				Upgrade u = new Upgrade();
				
				u.setName(element.getAttribute("name"));
				
				DecimalFormatSymbols symbols =  new DecimalFormatSymbols();
				symbols.setDecimalSeparator(',');
				DecimalFormat format = new DecimalFormat("0.#");
				format.setDecimalFormatSymbols(symbols);
				float cost = format.parse(element.getElementsByTagName("researchcost").item(0).getTextContent()).floatValue();
				
				
				u.setCost(cost);
				
				//System.out.println("DATABASE ITEM "+ i);
				//System.out.println(u.getName());
				//System.out.println(u.getCost());
				//System.out.println(c.getRole());
				//System.out.println(c.getLevel());
				//System.out.println();
				
				upgrades.put(u.getName(), u);
			}
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}		

	}
	
	
	public void init()
	{
		readStructures();
		readMapGrid();
		readUpgrades();
		readPolicy();
		
		setIDPolicy();
		setIDStructure();
		setIDUpgrade();
	}


	public Cell[][] getGrid() {
		return grid;
	}

	public void setCell(int posx, int posy, Construction c) {
		Construction cc = c.copy();
		cc.setIsBuilt(true);
		grid[posx][posy].setConstruction(cc);
		grid[posx][posy].setPlayable(false);
	}
	
	public void setGrid(Cell[][] grid) {
		this.grid = grid;
	}


	public Map<String, Construction> getConstructions() {
		return constructions;
	}


	public void setConstructions(Map<String, Construction> hashMap) {
		this.constructions = hashMap;
	}
	
	public void setIDStructure()
	{
		for (Entry<String,Construction> e : constructions.entrySet())
		{
			Construction c = constructions.get(e.getKey());
			c.setId(getStructureCode(e.getKey()));
			constructions.put(e.getKey(), c);
		}
	}
	
	public void setIDUpgrade()
	{
		for (Entry<String,Upgrade> e : upgrades.entrySet())
		{
			Upgrade u = upgrades.get(e.getKey());
			u.setId(getUpgradeCode(e.getKey()));
			upgrades.put(e.getKey(), u);
		}
	}
	
	public void setIDPolicy()
	{
		for (Entry<String,Policy> e : policies.entrySet())
		{
			Policy p = policies.get(e.getKey());
			p.setId(getPolicyCode(e.getKey()));
			policies.put(e.getKey(), p);
		}
	}
	
	public int getStructureCode(String structure)
	{
		int code = 0;
		switch (structure) {
		case "Park": code = 1; break;
		case "Forest": code = 2; break;
		case "Wildlife_Reserve": code = 3; break;
		case "Suburban": code = 4; break;
		case "Urban": code = 5; break;
		case "Stadium": code = 6; break;
		case "Light_Industry": code = 7; break;
		case "Heavy_Industry": code = 8; break;
		case "Commercial": code = 9; break;
		case "Coal_Plant": code = 10; break;	
		case "Nuclear_Plant": code = 11; break;
		case "Windmills": code = 12; break;
		case "Solar_Plant": code = 13; break;
		case "Hydro_Plant": code = 14; break;
		case "City_Hall": code = 15; break;
		case "Coal_Plant_Small": code = 16; break;
		case "Residential_Tower": code = 17; break;
		case "Super_Solar": code = 18; break;
		case "Super_WindTurbine": code = 19; break;
		case "Nuclear_Fusion": code = 20; break;
		case "Market": code = 21; break;
		case "Public_Services": code = 22; break;
		}
		return code;
	}


	//////////////Function to return value of upgrade
	public int getUpgradeCode(String upgrade)
	{
		int code = 0;
		switch (upgrade) {
		case "Solar_Roofs": code = 1; break;
		case "Improved_Insulation": code = 2; break;
		case "Rainwater_Storage": code = 3; break;
		case "Bus_Stop": code = 4; break;
		case "Rooftop_Windmills": code = 5; break;
		case "Thermal_Storage": code = 6; break;
		case "Birdhouse": code = 7; break;
		case "Eco_Roofs": code = 8; break;
		case "Subway_Station": code = 9; break;
		case "Energy_Efficient_Lightbulbs": code = 10; break;	
		case "Recycling_Facilities": code = 11; break;
		case "CO2_Reduction_Plan": code = 12; break;
		case "Cradle_2_Cradle": code = 13; break;
		case "Bio_Food": code = 14; break;
		case "Veggie_Food": code = 15; break;
		case "Watch_Tower": code = 16; break;
		case "Forest_Health_Plan": code = 17; break;
		case "Wildlife_Preservation": code = 18; break;
		case "Exhaust_Scrubbers": code = 19; break;
		case "Coal_Washing": code = 20; break;
		case "CO2_Storage": code = 21; break;
		case "Bigger_Rotor_Blades": code = 22; break;
		case "Next_Gen_Solar_Cells": code = 23; break;
		case "Moving_Solar_Pads": code = 24; break;
		case "Improved_Uranium_Storage": code = 25; break;
		case "Uranium_Recycling": code = 26; break;
		}
		return code;
	}


	//////////////Function to return value of upgrade
	public int getPolicyCode(String policy)
	{
		int code = 0;
		switch (policy) {
		case "CO2_Taxes": code = 1; break;
		case "Electric_Car_Grid": code = 2; break;
		case "Energy_Education_Program": code = 3; break;
		case "Eco_Tourism_Program": code = 4; break;
		case "Sustainable_Technology_Fund": code = 5; break;
		}
		return code;
	}
	
	public Construction getConstruction(String s)
	{
		return constructions.get(s);
	}


}


//ArrayList<String> fields = new ArrayList<String>();
//fields.add("Plains");fields.add("Plains2");fields.add("Hills");
//ArrayList<String> fields2 = new ArrayList<String>();
//fields2.add("Plains");fields2.add("Plains2");fields2.add("Hills");fields2.add("Ocean");
//ArrayList<String> fields3 = new ArrayList<String>();
//fields2.add("Hydro_River");
//String env = "Enviromentalist";
//String all = "All";
//String eco = "Economist";
//String may = "Mayor";
//constructions.add(new Construction("Park",1,env,8,fields));
//constructions.add(new Construction("Forest",2,env,24,fields));
//constructions.add(new Construction("Wildlife_Reserve",3,env,75,fields));
//constructions.add(new Construction("Suburban",1,all,10,fields));
//constructions.add(new Construction("Urban",2,all,30,fields));
//constructions.add(new Construction("Residential_Tower",3,all,99,fields));
//constructions.add(new Construction("Light_Industry",1,eco,10,fields));
//constructions.add(new Construction("Heavy_Industry",2,eco,20,fields));
//constructions.add(new Construction("Commercial",3,eco,75,fields));
//constructions.add(new Construction("Market",1,may,10,fields));
//constructions.add(new Construction("Public_Services",2,may,25,fields));
//constructions.add(new Construction("Stadium",3,may,50,fields));
//constructions.add(new Construction("Windmills",1,all,8,fields2));
//constructions.add(new Construction("Coal_Plant_Small",1,all,8,fields));
//constructions.add(new Construction("Coal_Plant",2,all,25,fields));
//constructions.add(new Construction("Solar_Plant",2,all,20,fields));
//constructions.add(new Construction("Nuclear_Plant",3,all,50,fields));
//constructions.add(new Construction("Hydro_Plant",3,all,60,fields3));
//constructions.add(new Construction("Super_WindTurbine",4,all,60,fields));
//constructions.add(new Construction("Super_Solar",4,all,100,fields));
//constructions.add(new Construction("Nuclear_Fusion",4,all,200,fields));
////constructions.add(new Construction("City_Hall",1,all,2,fields));
//
////Init MAP
//mapRules = new String[10][8];
//mapRules[0][0] = "";mapRules[0][1] = "";mapRules[0][2] = "";mapRules[0][3] = "";mapRules[0][0] = "";mapRules[0][0] = "";mapRules[0][0] = "";mapRules[0][0] = "";
//mapRules[1][0] = "";mapRules[1][1] = "";mapRules[1][2] = "";mapRules[1][3] = "";mapRules[0][0] = "";mapRules[0][0] = "";mapRules[0][0] = "";mapRules[0][0] = "";
//mapRules[2][0] = "";mapRules[2][1] = "";mapRules[2][2] = "";mapRules[2][3] = "";mapRules[0][0] = "";mapRules[0][0] = "";mapRules[0][0] = "";mapRules[0][0] = "";
//mapRules[3][0] = "";mapRules[3][1] = "";mapRules[3][2] = "";mapRules[3][3] = "";mapRules[0][0] = "";mapRules[0][0] = "";mapRules[0][0] = "";mapRules[0][0] = "";
//mapRules[4][0] = "";mapRules[4][1] = "";mapRules[4][2] = "";mapRules[4][3] = "";mapRules[0][0] = "";mapRules[0][0] = "";mapRules[0][0] = "";mapRules[0][0] = "";
//mapRules[5][0] = "";mapRules[5][1] = "";mapRules[5][2] = "";mapRules[5][3] = "";mapRules[0][0] = "";mapRules[0][0] = "";mapRules[0][0] = "";mapRules[0][0] = "";
//mapRules[6][0] = "";mapRules[6][1] = "";mapRules[6][2] = "";mapRules[6][3] = "";mapRules[0][0] = "";mapRules[0][0] = "";mapRules[0][0] = "";mapRules[0][0] = "";
//mapRules[7][0] = "";mapRules[7][1] = "";mapRules[7][2] = "";mapRules[7][3] = "";mapRules[0][0] = "";mapRules[0][0] = "";mapRules[0][0] = "";mapRules[0][0] = "";
//mapRules[8][0] = "";mapRules[8][1] = "";mapRules[8][2] = "";mapRules[8][3] = "";mapRules[0][0] = "";mapRules[0][0] = "";mapRules[0][0] = "";mapRules[0][0] = "";
//mapRules[9][0] = "";mapRules[9][1] = "";mapRules[9][2] = "";mapRules[9][3] = "";mapRules[0][0] = "";mapRules[0][0] = "";mapRules[0][0] = "";mapRules[0][0] = "";
//