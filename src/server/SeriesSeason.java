
import java.io.Serializable;
import org.json.JSONObject;
import java.util.ArrayList;

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
* Purpose: An SeriesSeason class for serializing between client and server.
*
* Ser321 Principles of Distributed Software Systems
* see http://pooh.poly.asu.edu/Ser321
* @author Brent Garcia, Tim Lindquist Tim.Lindquist@asu.edu
*
Software Engineering, CIDSE, IAFSE, ASU Poly
* @version April 2020
*/

public class SeriesSeason extends Object implements Serializable {

   public String title;
   public String season;
   public String imdbRating;
   public String genre;
   public String poster;
   public String plot;
   public ArrayList <Episodes> episodes = new ArrayList<Episodes>();

   public SeriesSeason(String aTitle, String aSeason,
                           String aImdbRating, String aGenre,
			   String aPoster, String aPlot,
  		           ArrayList<Episodes> aEpisodes
                           ){
      this.title = aTitle;
      this.season = aSeason;
      this.imdbRating = aImdbRating;
      this.genre = aGenre;
      this.poster = aPoster;
      this.plot = aPlot;
      this.episodes = aEpisodes;
   }

   public SeriesSeason(String jsonString){
      this(new JSONObject(jsonString));
      //System.out.println("constructor from json string got: "+jsonString);
      //System.out.println("constructed SS: "+this.toJsonString()+" from json");
   }

   // Constructor to serialize 2 api calls
   public SeriesSeason(String jsonString1, String jsonString2){
      this(new JSONObject(jsonString1), new JSONObject(jsonString2));
      //System.out.println("constructor from json string got: "+jsonString);
      //System.out.println("constructed SS: "+this.toJsonString()+" from json");
   }

   
   public SeriesSeason(JSONObject jsonObj){
      try{
         // System.out.println("SERIESSEASON constructor from json received: "+
         //                  jsonObj.toString());
	 Episodes episode;         

	 title = jsonObj.getString("Title");
         imdbRating = jsonObj.getString("imdbRating");
         genre = jsonObj.getString("Genre");
 	 poster = jsonObj.getString("Poster");
         plot = jsonObj.getString("Plot");
         season = jsonObj.getString("Season");
	
	 //FOR EVERY EPISODE
         for (int i=0;i<jsonObj.getJSONArray("Episodes").length();i++){
		//Create Episode and add to our List
         	episode = new Episodes(jsonObj.getJSONArray("Episodes").getJSONObject(i));		
		episodes.add(episode);
	 }

         //System.out.println("constructed "+this.toJsonString()+" from json");
      }catch(Exception ex){
         System.out.println("Exception in SeriesSeason(JSONObject): "+ex.getMessage());
      }
   }

   // Constructor to serialize 2 api calls
   public SeriesSeason(JSONObject jsonObj1, JSONObject jsonObj2){
      try{
         //System.out.println("SERIESSEASON constructor from json received: "+
         //                   jsonObj1.toString());
	 Episodes episode;
         
	 //Parse Object 1 (Translate to new value names)
         title = jsonObj1.getString("Title");
         imdbRating = jsonObj1.getString("imdbRating");
         genre = jsonObj1.getString("Genre");
 	 poster = jsonObj1.getString("Poster");
         plot = jsonObj1.getString("Plot");
	 
	 //Parse Object 2 & Episodes
	 season = jsonObj2.getString("Season");

	 //Add episodes to arraylist
         for (int i=0;i<jsonObj2.getJSONArray("Episodes").length();i++){
		//Create Episode and add to our List
         	episode = new Episodes(jsonObj2.getJSONArray("Episodes").getJSONObject(i));		
		episodes.add(episode);
	 }
         
         //System.out.println("constructed "+this.toJsonString()+" from json");
      }catch(Exception ex){
         System.out.println("Exception in SeriesSeason(JSONObject): "+ex.getMessage());
      }
   }

   public String toJsonString(){
      String ret = "{}";
      try{
         ret = this.toJson().toString(0);
      }catch(Exception ex){
         System.out.println("Exception in toJsonString: "+ex.getMessage());
      }
      return ret;
   }

   public JSONObject toJson(){
      JSONObject obj = new JSONObject();
      try{
         obj.put("Title", title);
         obj.put("imdbRating", imdbRating);
         obj.put("Genre", genre);
         obj.put("Poster", poster);
         obj.put("Plot", plot);
         obj.put("Season", season);
	 obj.put("Episodes", episodes);

      }catch(Exception ex){
         System.out.println("Exception in toJson: "+ex.getMessage());
      }
      return obj;
   }

  public String[] getAllEpisodeTitles(){
	String[] epTitles = new String[this.episodes.size()];
	try{
  	   for (int i=0; i<epTitles.length; i++){
  		epTitles[i] = this.episodes.get(i).title;
	   }
	}catch(Exception ex){
           System.out.println("Exception in getAllEpisodeTitles: "+ex.getMessage());
        }
  	return epTitles;
  }

  public Episodes[] getAllEpisodes(){
	Episodes[] episodes = new Episodes[this.episodes.size()];
	try{
  	   for (int i=0; i<episodes.length; i++){
  		episodes[i] = this.episodes.get(i);
	   }
	}catch(Exception ex){
           System.out.println("Exception in getAllEpisode: "+ex.getMessage());
        }
  	return episodes;
  }
}
