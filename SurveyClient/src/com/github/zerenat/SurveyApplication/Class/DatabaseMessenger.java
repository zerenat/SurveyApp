package com.github.zerenat.SurveyApplication.Class;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import org.json.JSONObject;

public class DatabaseMessenger extends Thread{
	private Socket socket;
	private int portNumber;
	private String IPAddress;
	private PrintWriter writer;
	private BufferedReader reader;
	
	/**
	 * Default constructor
	 * Default server IP address set to localhost, communication port set to 40500
	 */
	public DatabaseMessenger() {
		this.portNumber = 40500;
		this.IPAddress = "localHost";
	}
	/**
	 * Custom constructor, accepting the values for IP address and port number
	 * @param IPAddress IP address for the server
	 * @param portNumber communication port number
	 */
	public DatabaseMessenger(String IPAddress, int portNumber) {
		this.IPAddress = IPAddress;
		this.portNumber = portNumber;
	}
	
	/**
	 * Method set up byte stream tools for server-client communication
	 * Communication uses PrintWriter and BufferedReader to access byte stream 
	 */
	public void connect() {
		try {
			this.socket = new Socket(IPAddress, portNumber);	
			this.writer = new PrintWriter(socket.getOutputStream(), true);
			this.reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		} 
		catch(IOException exception) {
			exception.printStackTrace();
		}
	}
	/**
	 * Method terminates the connection to the server
	 */
	public void disconnect() {
		try {
			this.socket.close();
		} catch (IOException exception) {
			exception.printStackTrace();
		}
	}
	/**
	 * Method makes registration request to the server.
	 * @param registerData JSONObject should consist {String : email, String : password, String : action, String[] : surveys}. 
	 * @return boolean true or false depending on if the credentials were found in the database.
	 */
	public boolean registerUser(JSONObject registerData) {
		try {
			connect();
			System.out.println("[DatabaseMessenger] Sending message: "+registerData);
			writer.println(registerData);
			String response = reader.readLine();
			disconnect();
			if(response.equals("success")) {
				return true;
			}
			else {
				return false;
			}
		}
		catch(Exception exception) {
			exception.printStackTrace();
			return false;
		}
	}
	/**
	 * Method makes an user identification request to the server
	 * @param loginData JSONObject should consist {String : email, String : password, String : action}
	 * @return JSONObject with user data if identification was successful. No data if identification has failed 
	 */
	public JSONObject identifyUser(JSONObject loginData) {
		JSONObject userData = new JSONObject();
		try {
			connect();
			System.out.println("[DatabaseMessenger] Sending message: "+loginData);
			writer.println(loginData);
			String response = reader.readLine();
			userData = new JSONObject(response);
			disconnect();
			return userData;
		}
		catch(Exception exception) {
			exception.printStackTrace();
			return userData;
		}
	}
	/**
	 * Method sends user data along with survey data to the server for storage
	 * @param userData JSONObject should consist {String : email, JSONArray : surveys, String : surveyCode, String : action}
	 * @return boolean true or false depending on if server successfully updated the user data
	 */
	public boolean saveSurvey(JSONObject userData) {
		try {
			connect();
			System.out.println("[DatabaseMessenger] Sending message: "+userData);
			writer.println(userData);
			String response = reader.readLine();
			disconnect();
			if(response.equals("success")) {
				return true;
			}
			else {
				return false;
			}
		}
		catch(Exception exception) {
			exception.printStackTrace();
			return false;
		}
	}
	/**
	 * Method sends GET request for a survey 
	 * @param surveyRequest JSONObject should consist {String : surveyCode, String : action}
	 * @return JSONObject with survey data
	 * @return JSONObject with no data if survey was not found
	 */
	public JSONObject getSurvey(JSONObject surveyRequest) {
		try {
			connect();
			System.out.println("[DatabaseMessenger] Sending message: "+surveyRequest);
			writer.println(surveyRequest);
			String response = reader.readLine();
			disconnect();
			JSONObject survey = new JSONObject(response);
			return survey;
		}
		catch(Exception exception) {
			exception.printStackTrace();
			return new JSONObject();
		}
	}
	/**
	 * Method sends survey result data to the server for storage
	 * @param surveyResults should contain {String : surveyCode, String : owner, String : action}
	 * @return boolean true or false depending on if server successfully updated the survey result data
	 */
	public boolean submitResults(JSONObject surveyResults) {
		try {
			connect();
			System.out.println("[DatabaseMessenger] Sending message: "+surveyResults);
			writer.println(surveyResults);
			String response = reader.readLine();
			disconnect();
			if(response.equals("success")) {
				return true;
			}
			else {
				return false;
			}
		}
		catch(Exception exception) {
			exception.printStackTrace();
			return false;
		}
	}
}
