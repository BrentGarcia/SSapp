

import java.io.Serializable;
import org.json.JSONObject;

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
* Purpose: An Episodes class for serializing between client and server.
*
* Ser321 Principles of Distributed Software Systems
* see http://pooh.poly.asu.edu/Ser321
* @author Brent Garcia, Tim Lindquist Tim.Lindquist@asu.edu
*
Software Engineering, CIDSE, IAFSE, ASU Poly
* @version April 2020
*/

public class Episodes extends Object implements Serializable {

   public String title;
   public String imdbRating;
   public String episode;

   public Episodes(String aTitle, String aImdbRating, String aEpisode){
      this.title = aTitle;
      this.imdbRating = aImdbRating;
      this.episode = aEpisode;
   }

   public Episodes(String jsonString){
      this(new JSONObject(jsonString));
      //System.out.println("constructor from json string got: "+jsonString);
      //System.out.println("constructed MD: "+this.toJsonString()+" from json");
   }

   public Episodes(JSONObject jsonObj){
      try{
         //System.out.println("constructor from json received: "+
         //                   jsonObj.toString());
         title = jsonObj.getString("Title");
         imdbRating = jsonObj.getString("imdbRating");
         episode = jsonObj.getString("Episode");
         //System.out.println("constructed "+this.toJsonString()+" from json");
      }catch(Exception ex){
         System.out.println("Exception in Episodes(JSONObject): "+ex.getMessage());
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
         obj.put("Episode", episode);
      }catch(Exception ex){
         System.out.println("Exception in toJson: "+ex.getMessage());
      }
      return obj;
   }

}
