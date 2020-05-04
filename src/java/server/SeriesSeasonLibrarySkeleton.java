

import java.net.*;
import java.io.*;
import java.util.*;
import org.json.JSONObject;
import org.json.JSONArray;

/**
 * Copyright 2016 Tim Lindquist,
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 
 * A class for client-server connections with a threaded server.
 * The student collection server creates a server socket.
 * When a client request arrives, which should be a JsonRPC request, a new
 * thread is created to service the call and create the appropriate response.
 * Byte arrays are used for communication to support multiple langs.
 *
 * @author Tim Lindquist ASU Polytechnic Department of Engineering
 * @version July 2016
 */
public class SeriesSeasonLibrarySkeleton extends Object {

   private static final boolean debugOn = false;
   SeriesSeasonLibrary ssLib;

   public SeriesSeasonLibrarySkeleton (SeriesSeasonLibrary ssLib){
      this.ssLib = ssLib;
   }

   private void debug(String message) {
      if (debugOn)
         System.out.println("debug: "+message);
   }

   public String callMethod(String request){
      JSONObject result = new JSONObject();
      try{
         JSONObject theCall = new JSONObject(request);
         debug("Request is: "+theCall.toString());
         String method = theCall.getString("method");
         int id = theCall.getInt("id");
         JSONArray params = null;
         if(!theCall.isNull("params")){
            params = theCall.getJSONArray("params");
         }
         result.put("id",id);
         result.put("jsonrpc","2.0");
         if(method.equals("addSeriesSeason")){
            JSONObject ssJson = params.getJSONObject(0);
            SeriesSeason ssToAdd = new SeriesSeason(ssJson);
            debug("adding series: "+ssToAdd.toJsonString());
            ssLib.addSeriesSeason(ssToAdd);
            result.put("result",true);
         }else if(method.equals("removeSeriesSeason")){
            JSONObject ssJson = params.getJSONObject(0);
            SeriesSeason ssToRemove = new SeriesSeason(ssJson);
            debug("Removing Series Season "+ssToRemove.toJsonString());
            ssLib.removeSeriesSeason(ssToRemove);
            result.put("result",true);
         }else if(method.equals("restoreLibraryFromFile")){
	    ssLib.restoreLibraryFromFile();
            result.put("result",true);
         }else if(method.equals("getSeriesSeason")){
            String ssTitle = params.getString(0);
            SeriesSeason ss = ssLib.getSeriesSeason(ssTitle);
            JSONObject ssJson = ss.toJson();
            System.out.println("getSeriesSeason found: "+ssJson.toString());
            result.put("result",ssJson);
         }else if(method.equals("getAllSeriesSeasonTitles")){
            String[] titles = ssLib.getAllSeriesSeasonTitles();
            JSONArray resArr = new JSONArray();
            for (int i=0; i<titles.length; i++){
               resArr.put(titles[i]);
            }
            debug("getAllSeriesSeasonTitles request found: "+resArr.toString());
            result.put("result",resArr);
         }else{
            debug("Unable to match method: "+method+". Returning 0.");
            result.put("result",0.0);
         }
      }catch(Exception ex){
         System.out.println("exception in callMethod: "+ex.getMessage());
      }
      return result.toString();
   }
}

