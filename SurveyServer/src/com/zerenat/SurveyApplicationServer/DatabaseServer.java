package com.zerenat.SurveyApplicationServer;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class DatabaseServer extends Thread{
	
	private Socket socket;
	private PrintWriter writer;
	private BufferedReader reader;
	private static ArrayList<JSONObject> registeredUsers = new ArrayList<>();
	
	public DatabaseServer(Socket socket) {
		this.socket = socket;
	}
	/**
	 * Method opens socket's input stream and calls analyseInput method
	 */
	@Override
	public void run() {
		try {
			writer = new PrintWriter(socket.getOutputStream(), true);
			reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			String inputString = reader.readLine();
			JSONObject inputObject = new JSONObject(inputString);
			analyseInput(inputObject);
		}
		catch(Exception exception){
			exception.printStackTrace();
		}
	}
	/**
	 * Method Accepts a String message, analyses it and decides how to respond
	 * @param inputArray input array containing information from client. 1st element of an array should indicate the request
	 */
	private void analyseInput(JSONObject inputObject) {
		try {
			//get action from firt key - value pair of a jsonObject
			String actionValue = (String)inputObject.get("action");
			
			switch(actionValue) {
				//Registration request
				case "register":
					inputObject.remove("action");
					
					if(registerUser(inputObject)) {
						writer.println("success");
					}
					else {
						writer.println("failed");
					}
					break;
					
				//Login request	
				case "login":
					inputObject.remove("action");
					writer.println(validateLogin(inputObject));
					break;
					
				//Store new survey request	
				case "saveSurvey":
					inputObject.remove("action");
					if(updateUserAccount(inputObject)) {
						writer.println("success");
					}
					else {
						writer.println("failed");
					}
					break;
				//Get survey request	
				case "getSurvey":
					inputObject.remove("action");
					writer.println(getSurvey(inputObject));
					break;
					
				//Submit result data request	
				case "submitResults":
					inputObject.remove("action");
					if(updateSurveyResults(inputObject)) {
						writer.println("success");
					}
					else {
						writer.println("failed");
					}
					break;
			}
		}
		catch(Exception exception) {
			exception.printStackTrace();
		}
	}
	/**
	 * Method checks for existing users to determine if entry is unique
	 * @param inputObject Contains information for requested account
	 * @return false if account already exists, true if account was made successfully
	 */
	private boolean registerUser(JSONObject inputObject) {
		try {
			String email = inputObject.getString("email");
			for(JSONObject jObject: registeredUsers) {
				if(jObject.get("email").equals(email)) {
					return false;
				}
			}
			registeredUsers.add(inputObject);
			return true;
		}
		catch(JSONException jException) {
			jException.printStackTrace();
			return false;
		}
	}
	/**
	 * Method checks if login details exist in order to validate login request.
	 * @param inputArray String array that should consist String email address value and password string.
	 * @return true if email and password match to any entries, false if there is no match
	 */
	private JSONObject validateLogin(JSONObject inputObject) {
		try {
			String email = inputObject.getString("email");
			String password = inputObject.getString("password");

			for(JSONObject jObject: registeredUsers) {
				if(jObject.get("email").equals(email)) {
					if(jObject.get("password").equals(password)) {
						return jObject;
					}
				}
			}
			return new JSONObject();
		}
		catch(JSONException jException) {
			jException.printStackTrace();
			return new JSONObject();
		}
	}
	/**
	 * Method updates user data in database
	 * @param inputObject JSONObject containing user information
	 * @return true or false depending on success or failure of update
	 */
	private boolean updateUserAccount(JSONObject inputObject) {
		try {
			String email = inputObject.getString("email");
			for(int i = 0; i < registeredUsers.size(); i++) {
				if(registeredUsers.get(i).get("email").equals(email)) {
					registeredUsers.add(i, inputObject);
					return true;
				}
			}
			return false;
		} 
		catch (JSONException jException) {
			jException.printStackTrace();
			return false;
		}
	}
	/**
	 * Retrieve a specific survey from the database
	 * @param inputObject JSONObject containing information on which survey to retrive
	 * @return JSONObject requested survey
	 */
	public JSONObject getSurvey(JSONObject inputObject) {
		try {
			boolean removeCount = inputObject.getBoolean("removeCount");
			String surveyCode = inputObject.getString("surveyCode");
			for(JSONObject user: registeredUsers) {
				//partial match (rework to remove possibility for accidental data retrieval)
				if(surveyCode.contains(user.getString("email"))){
					JSONArray surveys = user.getJSONArray("surveys");
					for(int i = 0; i < surveys.length(); i++) {
						//CLONE the original survey to avoid overwriting
						JSONObject survey = new JSONObject(surveys.getJSONObject(i).toString());
						if(survey.get("surveyCode").equals(surveyCode)) {
							if(!removeCount) {
								return survey;
							}
							JSONArray questions = survey.getJSONArray("questions");
							questions = removeCount(questions); 
							survey.put("questions", questions);
							return survey;
						}
					}
				}
			}
			
			return new JSONObject();
		}
		catch(JSONException jException) {
			jException.printStackTrace();
			return new JSONObject();
		}
	}
	/**
	 * Method updates existing survey with response data
	 * @param inputObject JSONObject containing response data
	 * @return true or false depending on success or failure of update
	 */
	public boolean updateSurveyResults(JSONObject inputObject) {
		try {
			String surveyOwner = inputObject.getString("owner");
			String surveyCode = inputObject.getString("surveyCode");
			JSONObject survey = findSurvey(surveyOwner, surveyCode);
			JSONArray questions = survey.getJSONArray("questions");
			JSONArray replies = inputObject.getJSONArray("replies");
			//Store questions and the user's answers in String containers
			for (int i = 0; i < replies.length(); i++) {
				JSONObject reply = replies.getJSONObject(i);
				String questionKey = (String) reply.keys().next();
				String replyQuestion = questionKey;
				String replyAnswer = reply.getString(questionKey);
				for (int k = 0; k < questions.length(); k++) {
					JSONObject question = questions.getJSONObject(k);
					String key = (String) question.keys().next();
					if(key.equals(replyQuestion)) {
						JSONArray answers = question.getJSONArray(key);
						for(int o = 0; o < answers.length(); o++) {
							String [] splitAnswerString = answers.get(o).toString().split("\\|\\|");
							if(splitAnswerString[0].equals(replyAnswer)){
								String [] splitCountString = splitAnswerString[1].split(":");
								int count = Integer.parseInt(splitCountString[1]);
								count++;
								String newAnswer = splitAnswerString[0]+"||count:"+String.valueOf(count);
								answers.put(o, newAnswer);
							}
						}
					}
				}
			}
			return true;
		} catch (JSONException exception) {
			exception.printStackTrace();
			return false;
		}
	}
	/**
	 * Method removes "count" variable from questions JSON array 
	 * @param questions JSON array of survey questions
	 * @return questions JSON array
	 */
	private JSONArray removeCount(JSONArray questions) {
		try {
			JSONArray processedQuestions = new JSONArray();
			for (int i = 0; i < questions.length(); i++) {
				JSONObject question = questions.getJSONObject(i);
				String key = (String) question.keys().next();
				JSONArray answersJSON = (JSONArray) question.get(key);
				JSONArray processedAnswersJSON = new JSONArray();
				String [] answers = new String [answersJSON.length()];
				for(int k = 0; k < answersJSON.length(); k++) {
					answers[k] = answersJSON.getString(k);
					String [] splitString = answers[k].split("\\|\\|");
					processedAnswersJSON.put(splitString[0]);
				}
				question.put(key, processedAnswersJSON);
				processedQuestions.put(question);
			}
			return processedQuestions;
		}
		catch(JSONException jException) {
			jException.printStackTrace();
			return null;
		}
	}
	/**
	 * Method looks through surveys to retrieve requested survey
	 * @param email user email of the survey owner
	 * @param surveyCode code for specific survey
	 * @return matching survey
	 */
	private JSONObject findSurvey(String email, String surveyCode) {
		try {
		for(JSONObject user: registeredUsers) {
			if(email.equals(user.getString("email"))){
				JSONArray surveys = user.getJSONArray("surveys");
				for(int i = 0; i < surveys.length(); i++) {
					JSONObject survey = surveys.getJSONObject(i);
					if(survey.get("surveyCode").equals(surveyCode)) {
						return survey;
					}
				}
			}
		}	
		return null;	
		}catch (JSONException jException) {
			jException.printStackTrace();
			return null;
		}
	}
	/**
	 * Main method. declares and listens to a port
	 * @param args arguments
	 * @throws IOException IOException
	 */
	@SuppressWarnings("resource")
	public static void main(String[] args) throws IOException {
        try {
            int portNumber = 40500;
            ServerSocket serverSocket = new ServerSocket(portNumber);
            while (true) {
                Socket clientSocket = serverSocket.accept();
                DatabaseServer dataServer = new DatabaseServer(clientSocket);
                dataServer.start();
            }
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }
}
