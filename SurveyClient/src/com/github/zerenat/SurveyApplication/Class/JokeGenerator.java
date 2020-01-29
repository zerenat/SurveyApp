package com.github.zerenat.SurveyApplication.Class;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * 
 * @author jokes.one 
 * Code on this page is created by jokes.one, slightly modified by Martin Hein.
 * Jokes API for free use. Available at: jokes.one.
 * API is used to generate a joke after a survey is completed but any application user.
 */

public class JokeGenerator {
	/**
	 * Method connects to external jokes.one API to retrieve a joke of the day
	 * @throws IOException IOException
	 * @return String joke
	 */
	public String getJoke() throws IOException {
        URL url = new URL("https://api.jokes.one/jod?category=animal");
        String storeJoke = "";
        try{
            //make connection
            HttpURLConnection URLConnection = (HttpURLConnection) url.openConnection();
            URLConnection.setRequestMethod("GET");
            // set the content type
            URLConnection.setRequestProperty("Content-Type", "application/json");
            URLConnection.setRequestProperty("X-JokesOne-Api-Secret", "YOUR API KEY HERE");
            System.out.println("Connect to: " + url.toString());
            URLConnection.setAllowUserInteraction(false);
            URLConnection.connect();
            			
            //get result
            String joke = null;
            BufferedReader APIReader = new BufferedReader(new InputStreamReader(URLConnection.getInputStream()));
            while ((joke = APIReader.readLine())!=null) {
                storeJoke = joke;
            }
            String jokeString = cleanString(storeJoke);
            APIReader.close();
            return jokeString;
        } catch (Exception exception){
            exception.printStackTrace();
            return null;
        }
    }
	/**
	 * Method extracts the joke from JSON, provided by jokes.one API
	 * @param inputString String received from external jokes.one API
	 */
	private String cleanString (String inputString) {
		try {
			JSONArray jokes = new JSONObject(inputString).getJSONObject("contents").getJSONArray("jokes");
			JSONObject joke = jokes.getJSONObject(0).getJSONObject("joke");
			String jokeText = joke.getString("text");
			
			return jokeText;
		} 
		catch (JSONException exception) {
			// TODO Auto-generated catch block
			exception.printStackTrace();
			return null;
		}
	}
}
