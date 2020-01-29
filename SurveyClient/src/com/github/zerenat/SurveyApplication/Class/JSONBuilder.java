package com.github.zerenat.SurveyApplication.Class;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class JSONBuilder {
	/**
	 * Method turns survey data into JSON format
	 * @param userData JSONObject containing survey owner's data
	 * @param surveyName name of the survey
	 * @param questionsAndAnswers HashMap of questions and answers 
	 * @return JSONObject updated user object with new survey data attached 
	 */
	public JSONObject surveyToJSON(JSONObject userData, String surveyName, HashMap<String, String[]> questionsAndAnswers ) {
		try {
			userData.remove("action");
			userData.put("action", "saveSurvey");	
			
			String loggedInUser = userData.getString("email");
			int numberOfSurveys = 0;
			for (int i = 0; i < userData.getJSONArray("surveys").length(); i++){
				numberOfSurveys ++;
			}
			String surveyCode = loggedInUser + String.valueOf(numberOfSurveys);

			JSONObject surveyObject = new JSONObject();
			JSONArray questions = new JSONArray();
			ArrayList <JSONObject> tempStorage = new ArrayList<>();
			//Retrieve info from Hashmap, turn it into JSONObject
			for(@SuppressWarnings("rawtypes") Map.Entry entry: questionsAndAnswers.entrySet()) {
				String question = (String) entry.getKey();
				String [] answers = (String[]) entry.getValue();
				for(int i = 0; i < answers.length; i++) {
					answers[i] = answers[i] + "||count:0";
				}
				JSONObject object = new JSONObject();
				object.put(question, answers);
				tempStorage.add(object);
				
			}
			for(int k = 0; k < tempStorage.size(); k++) {
				questions.put(tempStorage.get(k));
			}	
			surveyObject.put("owner", userData.getString("email"));
			surveyObject.put("surveyCode", surveyCode);
			surveyObject.put("name", surveyName);
			surveyObject.put("questions", questions);
			userData.getJSONArray("surveys").put(surveyObject);
			return userData;
		}
		catch(JSONException jException) {
			jException.printStackTrace();
			return null;
		}
	}
	/**
	 * Method turns registration data into JSON format
	 * @param email registration email
	 * @param password registration password
	 * @return JSONObject containing registration data
	 */
	public JSONObject registerToJSON(String email, String password) {
		try {
			JSONObject registerObject = new JSONObject();
			registerObject.put("action", "register");
			registerObject.put("email", email);
			registerObject.put("password", password);
			registerObject.put("surveys", new JSONArray());
			return registerObject;
		}
		catch(JSONException jException) {
			jException.printStackTrace();
			return null;
		}
	}
	/**
	 * Method turns login data into JSON format
	 * @param email user email
	 * @param password user password
	 * @return JSONObject containing login data
	 */
	public JSONObject loginToJSON(String email, String password) {
		try {
			JSONObject loginObject = new JSONObject();
			loginObject.put("action", "login");
			loginObject.put("email", email);
			loginObject.put("password", password);
			return loginObject;
		}
		catch(JSONException jException) {
			jException.printStackTrace();
			return null;
		}
	}
	/**
	 * Method turns survey request into JSON format
	 * @param surveyCode survey's code
	 * @param boolean remove or keep count in answer strings
	 * @return JSONObject containing survey request information
	 */
	public JSONObject surveyRequestToJSON(String surveyCode, boolean removeCount) {
		try{
			JSONObject surveyRequest = new JSONObject();
			surveyRequest.put("action", "getSurvey");
			surveyRequest.put("removeCount", removeCount);
			surveyRequest.put("surveyCode", surveyCode);
			return surveyRequest;
		}
		catch(JSONException jException) {
			jException.printStackTrace();
			return null;
		}
	}
	/**
	 * Method turns survey response data into JSON format
	 * @param surveyCode survey's code
	 * @param surveyOwner creator of the survey
	 * @param questionAnswer question and answer
	 * @return JSONObject containing survey response data
	 */
	public JSONObject surveyResultsToJSON(String surveyCode, String surveyOwner, HashMap<String, String> questionAnswer) {
		try {
			JSONObject surveyResults= new JSONObject();
			surveyResults.put("action", "submitResults");
			surveyResults.put("surveyCode", surveyCode);
			surveyResults.put("owner", surveyOwner);
			JSONArray replies = new JSONArray();
			for(Map.Entry<String, String> entry : questionAnswer.entrySet()) {
				 String keyString = entry.getKey();
				 String valueString = entry.getValue();
				 JSONObject questionAnswerObject = new JSONObject();
				 questionAnswerObject.put(keyString, valueString);
				 replies.put(questionAnswerObject);
			}
			surveyResults.put("replies", replies);
			return surveyResults;
		}catch(Exception exception) {
			exception.printStackTrace();
			return null;
		}
	}
}

