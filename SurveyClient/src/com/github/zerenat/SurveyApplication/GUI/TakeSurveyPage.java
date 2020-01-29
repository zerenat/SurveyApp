package com.github.zerenat.SurveyApplication.GUI;

import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.github.zerenat.SurveyApplication.Class.DatabaseMessenger;
import com.github.zerenat.SurveyApplication.Class.JSONBuilder;
import com.github.zerenat.SurveyApplication.Class.JokeGenerator;

import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class TakeSurveyPage implements IGUIPageInterface{
	private Scene takeSurveyPageScene;
	private VBox centerContainer;
	private String surveyCode;
	private String surveyOwner;
	private Button submitButton;
	/**
	 * Constructor creates GUI elements and functionality for TakeSurveyPage
	 * @param mainPageInstance instance of the main page
	 */
	@SuppressWarnings("static-access")
	public TakeSurveyPage(MainPage mainPageInstance) {
		BorderPane root = new BorderPane();
		takeSurveyPageScene = new Scene (root, 500, 600);
		DatabaseMessenger messenger = new DatabaseMessenger();
		JSONBuilder builder = new JSONBuilder();
		
		HBox getSurveyRow = new HBox();
		VBox questionContainer = new VBox();
		
		TextField surveyCodeField = new TextField();
		surveyCodeField.setPromptText("Survey code");
		
		//getSurvey button action retrieves specific survey from the server and populates
		//the page with questions and answers
		Button getSurvey = new Button("Get survey");
		getSurvey.setOnAction(event->{
			try {
				this.surveyCode = surveyCodeField.getText();
				JSONObject surveyRequest = builder.surveyRequestToJSON(surveyCode, true);
				JSONObject survey = messenger.getSurvey(surveyRequest);
				//modify generate Question method to accept JSON
				//server returns a survey in JSONObject form
				//JSONObject contains JSONArray questions,  JSONObject question and JSONArray answers
				this.surveyOwner = survey.getString("owner");
				
				JSONArray questions = survey.getJSONArray("questions");
				JSONArray answersJSON;
				JSONObject questionJSON;
				String questionKey = "";
				
				//Break JSON into questions and answers
				//Generate page nodes using questions and answers
				
				for (int i = 0; i < questions.length();i++) {
					questionJSON = (JSONObject) questions.get(i);
					questionKey = (String) questionJSON.keys().next();
					answersJSON = (JSONArray) questionJSON.get(questionKey);
					String [] answers = new String [answersJSON.length()];
					for(int k = 0; k < answersJSON.length(); k++) {
						answers[k] = answersJSON.getString(k);
					}
					questionContainer.getChildren().add(generateQuestion(questionKey, answers));	
				}
				submitButton.setDisable(false);
			}
			catch(JSONException jException) {
				Alert informationAlert = new Alert(AlertType.INFORMATION);
				informationAlert.setContentText("Could not find survey");
				informationAlert.showAndWait();
			}
		});
		
		getSurveyRow.getChildren().addAll(surveyCodeField, getSurvey);
		
		Button backButton = new Button("Back");
		backButton.setOnAction(event->{
			mainPageInstance.setSceneMainPage();
		});
		
		submitButton = new Button("Submit");
		submitButton.setOnAction(event->{
			try {
				Alert jokeDisplay = new Alert(AlertType.CONFIRMATION);
				JokeGenerator jokeGenerator = new JokeGenerator();
				JSONObject surveyResults = builder.surveyResultsToJSON(surveyCode, surveyOwner, getDataFromSurvey(questionContainer));
				messenger.submitResults(surveyResults);
				jokeDisplay.setContentText("Here's a bonus joke:\n\n"+jokeGenerator.getJoke());
				jokeDisplay.setHeaderText("You finished the survey! well done!");
				jokeDisplay.showAndWait();
				mainPageInstance.setSceneMainPage();
			}
			catch(Exception exception) {
				exception.printStackTrace();
			}
		});
		submitButton.setDisable(true);
		
		ArrayList <Node> nodeList = new ArrayList<Node>();
		
		nodeList.add(getSurveyRow);
		nodeList.add(questionContainer);
		nodeList.add(submitButton);
		nodeList.add(backButton);
		centerContainer = new VBox();
		centerContainer.setMaxWidth(400);
		centerContainer.getChildren().addAll(nodeList);
		for (Node node: nodeList) {
			centerContainer.setMargin(node, new Insets(10,0,0,0));
			node.maxWidth(centerContainer.getWidth());
		}
		root.setCenter(centerContainer);	
	}
	/**
	 * Method generates GUI elements for survey questions. Returns HBox of nodes. 
	 * Accepts question name string and a string array of answers, which are used in node generation.
	 * 
	 */
	private VBox generateQuestion(String question, String [] answers) {
		VBox questionContainer = new VBox();
		questionContainer.setMinWidth(100);
		questionContainer.setPadding(new Insets(10,0,0,0));
		Node [] nodeList = new Node [answers.length + 1];
		
		ToggleGroup toggleGroup = new ToggleGroup();
		Label questionLabel = new Label();
		questionLabel.setText(question);
		
		nodeList[0] = questionLabel;
		for (int i = 0; i < answers.length; i++) {
			RadioButton radioButton = new RadioButton();
			radioButton.setText(answers[i]);
			radioButton.setToggleGroup(toggleGroup);
			nodeList[i+1] = radioButton;
		}
		questionContainer.getChildren().addAll(nodeList);
		return questionContainer;
	}
	
	/**
	 * Method extracts answer data from survey page. 
	 * @param questionContainer node for the method
	 * @return Returns a HashMap of data.
	 */
	public HashMap <String, String> getDataFromSurvey(VBox questionContainer) {
		HashMap <String, String > surveyData = new HashMap<String, String>();
		String answerString = "";
		Label questionLabel = new Label();
		//questionContainer contains VBox(s). amount depending on number of questions
		//loop through nodes on the survey page and extract user input
		for(Node node: questionContainer.getChildren()) {
			VBox questionAnswerContainer = (VBox) node;
			for(Node innerNode: questionAnswerContainer.getChildren()) {
				if(innerNode.getClass().toString().contains("Label")) {
					questionLabel = (Label) innerNode;
				}else {
					RadioButton answerButton = (RadioButton) innerNode;
					if(answerButton.isSelected()) {
						answerString = answerButton.getText();
				}
			}
		}	
		surveyData.put(questionLabel.getText(), answerString);
		}
		return surveyData;
	}

	@Override
	public Scene getScene() {
		return takeSurveyPageScene;
	}
}
