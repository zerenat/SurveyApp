package com.github.zerenat.SurveyApplication.GUI;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.github.zerenat.SurveyApplication.Class.DatabaseMessenger;
import com.github.zerenat.SurveyApplication.Class.JSONBuilder;

import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class AnalyseSurveysPage implements IGUIPageInterface{
	private Scene AnalyseSurveysPageScene;
	/**
	 * Constructor creates GUI elements and functionality of AnalyseSurveyPage
	 * @param mainPageInstance instance of the main page
	 * @param loggedInPageInstance instance of the loggedInPage
	 */
	@SuppressWarnings("static-access")
	public AnalyseSurveysPage(MainPage mainPageInstance, LoggedInPage loggedInPageInstance) {
		JSONBuilder builder = new JSONBuilder();
		DatabaseMessenger messenger = new DatabaseMessenger();
		VBox questionContainers = new VBox();
		
		BorderPane root = new BorderPane();
		AnalyseSurveysPageScene = new Scene(root, 500, 600);
		
		TextField surveyCodeField = new TextField();
		surveyCodeField.setPromptText("Survey code");
		
		Button getSurvey = new Button("Get survey");
		getSurvey.setOnAction(event->{
			try {
				JSONObject surveyRequest = builder.surveyRequestToJSON(surveyCodeField.getText(), false);
				JSONObject survey = messenger.getSurvey(surveyRequest);	
				questionContainers.getChildren().add(displaySurveyResults(survey));
				getSurvey.setDisable(true);
			}
			catch(Exception exception) {
				Alert informationAlert = new Alert(AlertType.INFORMATION);
				informationAlert.setContentText("Could not find survey");
				informationAlert.showAndWait();
			}
		});
		
		Button backButton = new Button("Back");
		backButton.setOnAction(event->{
			mainPageInstance.getMainStage().setScene(loggedInPageInstance.getScene());
		});
		
		HBox getSurveyRow = new HBox();
		getSurveyRow.getChildren().addAll(surveyCodeField, getSurvey);
		Node [] nodeList = new Node [] {getSurveyRow, questionContainers, backButton};
		
		VBox centerContainer = new VBox();
		centerContainer.setMaxWidth(400);
 		centerContainer.setMaxHeight(200);
		centerContainer.getChildren().addAll(nodeList);
		for (Node node: nodeList) {
			centerContainer.setMargin(node, new Insets(10,0,0,0));
			node.minWidth(centerContainer.getPrefWidth());
		}
		root.setCenter(centerContainer);
	}
	/**
	 * Method extracts survey questions and answers from JSONObject and places them in a VBox
	 * @param survey JSONObject containing survey information
	 * @return VBox containing survey questions and answers
	 */
	private VBox displaySurveyResults(JSONObject survey) {
		try {
			Label surveyName = new Label((String)survey.get("name"));
			JSONArray questions = survey.getJSONArray("questions");
			VBox questionsContainer = new VBox();
			questionsContainer.getChildren().add(surveyName);
			for(int i = 0; i < questions.length(); i++) {
				JSONObject question = questions.getJSONObject(i);
				String key = (String)question.keys().next();
				Label questionContainer = new Label(key);
				questionsContainer.getChildren().add(questionContainer);
				JSONArray answers = question.getJSONArray(key);
				for(int k = 0; k < answers.length(); k++) {
					String answer = (String)answers.get(k);
					String [] splitAnswer = answer.split("\\|\\|");
					Label answerLabel = new Label(splitAnswer[0] + " - " + splitAnswer[1]);
					questionsContainer.getChildren().add(answerLabel);
				}
			}
			return questionsContainer;
		} 
		catch (JSONException jException) {
			return null;
		}
	}
	@Override
	public Scene getScene() {
		return AnalyseSurveysPageScene;
	}

}
