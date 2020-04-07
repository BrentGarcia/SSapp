
import java.io.*;
import java.util.*;
import java.net.URL;
import org.json.JSONObject;
import org.json.JSONArray;
import org.json.JSONTokener;

/*
* Copyright 2020 Brent Garcia,
*
* This software is the intellectual property of the author, and can not be
distributed, used, copied, or
* reproduced, in whole or in part, for any purpose, commercial or otherwise.
The author grants the ASU
* Software Engineering program the right to copy, execute, and evaluate this
work for the purpose of
* determining performance of the author in coursework, and for Software
Engineering program evaluation,
* so long as this copyright and right-to-use statement is kept in-tact in such
use.
* All other uses are prohibited and reserved to the author.
*
* Purpose: An SeasonServer class for serializing between client and server.
*
* Ser321 Principles of Distributed Software Systems
* see http://pooh.poly.asu.edu/Ser321
* @author Brent Garcia, Tim Lindquist Tim.Lindquist@asu.edu
*
Software Engineering, CIDSE, IAFSE, ASU Poly
* @version April 2020
*/

public class SeasonServerImpl extends Object implements SeasonLibrary{

   private Hashtable<String,SeriesSeason> aLib;
   private static final String fileName="series.json";
   
   public SeasonServerImpl () {
     this.aLib = new Hashtable<String,SeriesSeason>();
     try{
         InputStream is = this.getClass().getClassLoader().getResourceAsStream(this.fileName);
         if(is==null){
            is = new FileInputStream(new File(this.fileName));
         }
         JSONObject media = new JSONObject(new JSONTokener(is));

	 JSONArray allSeries = media.getJSONArray("Series");
	
	
	for (int i=0; i<allSeries.length(); i++){
		JSONObject series = allSeries.getJSONObject(i);
		String seriesTitle = series.getString("Title");
		if (series != null){
        		SeriesSeason ss = new SeriesSeason(series);
               		aLib.put(seriesTitle, ss);
            	}
        }
      }catch (Exception ex){
         System.out.println("Exception reading "+fileName+": "+ex.getMessage());
      }
   }

   //Adds a season series to Library
   public boolean addSeriesSeason(SeriesSeason seasonSeriesToAdd){ //May need string in arguments
      boolean result = false;
      System.out.println("Adding: "+seasonSeriesToAdd.title);
      try{
         aLib.put(seasonSeriesToAdd.title,seasonSeriesToAdd);
         result = true;
      }catch(Exception ex){
         System.out.println("exception in add: "+ex.getMessage());
      }
      return result;
   }

   //Removes a season series from library
   public boolean removeSeriesSeason(SeriesSeason seasonSeriesToRemove){
      boolean result = false;
      System.out.println("Removing "+seasonSeriesToRemove);
      try{
         aLib.remove(seasonSeriesToRemove.title);
         result = true;
      }catch(Exception ex){
         System.out.println("exception in remove: "+ex.getMessage());
      }
      return result;
   }


   //Returns a SeriesSeason object from library based on title
   public SeriesSeason getSeriesSeason(String seriesSeasonTitle){
      SeriesSeason result = null;
      try{
         result = aLib.get(seriesSeasonTitle);
      }catch(Exception ex){
         System.out.println("exception in get: "+ex.getMessage());
      }
      return result;
   }

   //Returns a String[] of all series season titles in library;
   public String[] getAllSeriesSeasonTitles(){
      String[] result = null;
      try{
         Set<String> vec = aLib.keySet();
         result = vec.toArray(new String[]{});
      }catch(Exception ex){
         System.out.println("exception in getTitles: "+ex.getMessage());
      }
      return result;
   }
   
   //Saves current library to series.json
   public boolean saveLibraryToFile(){
	try (FileWriter file = new FileWriter("series.json")){
		JSONObject series = new JSONObject();

		JSONArray libraryContents = new JSONArray();
		String[] seriesTitles = this.getAllSeriesSeasonTitles();
		SeriesSeason s;
		JSONObject tempObj;
		Episodes[] episodesArray;
		for (int i = 0;i<seriesTitles.length;i++){
			s = this.getSeriesSeason(seriesTitles[i]);
			JSONArray tempEpisodes = new JSONArray();
			//convert to jsonobject
			tempObj = s.toJson();
			//get Episodes
			episodesArray = s.getAllEpisodes();
			Episodes ep = new Episodes("blank", "blank", "blank");
			for (int j = 0; j<episodesArray.length; j++){
				ep = episodesArray[j];
				tempEpisodes.put(j,ep.toJson());
   			}
			tempObj.put("Episodes",tempEpisodes);
			libraryContents.put(i,tempObj);
		}
		series.put("Series", libraryContents);
		series.write(file);
		file.flush();
		return true;
	} catch (Exception ex){
		System.out.println("Exception writing JSON file:" +ex.getMessage());
		return false;
   	}
   }

   //Restores library to state of series.json
   public boolean restoreLibraryFromFile(){
	SeasonServerImpl tempSLibrary = new SeasonServerImpl();
	this.aLib = tempSLibrary.getHashTable();
   	return true;
   }

   public Hashtable<String,SeriesSeason> getHashTable(){
	return this.aLib;
   }

}
