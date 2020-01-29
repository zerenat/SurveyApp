package com.github.zerenat.SurveyApplication.GUI;

import java.util.HashMap;
import org.json.JSONObject;

import com.github.zerenat.SurveyApplication.Class.DatabaseMessenger;
import com.github.zerenat.SurveyApplication.Class.JSONBuilder;

import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.RowConstraints;
import javafx.scene.layout.VBox;

public class CreateSurveyPage implements IGUIPageInterface{
	private Scene constructSurveyScene;
	private TextInputDialog textInputDialog;
	private TextField surveyNameField;
	private int questionCount;
	private VBox questionList;
	private JSONObject userData;
	/**
	 * Constructor creates GUI elements and functionality of CreateSurveyPage
	 * @param mainPageInstance instance of the main page
	 * @param userData containing of logged in user information
	 */
	public CreateSurveyPage(MainPage mainPageInstance, JSONObject userData) {
		this.userData = userData;
		Button saveSurvey = new Button("Save Survey");
		questionCount = 0;
		GridPane root = new GridPane();
		constructSurveyScene = new Scene(root, 500, 600);

		Label surveyNameLabel = new Label("Survey name:");
		surveyNameLabel.setText("Survey Name");
		surveyNameField = new TextField();
		questionList = new VBox();
		questionList.setSpacing(10);

		//Generate nodes for survey
		Button addQuestionButton = new Button("Add question");
		addQuestionButton.setOnAction(event -> {
			if (surveyNameField.getLength() > 0) {
				textInputDialog = createDialog("Enter a question: ");
				String newQuestion = textInputDialog.showAndWait().get();

				questionCount++;
				HBox questionRow = new HBox();
				VBox answerList = new VBox();

				Label questionNameLabel = new Label();
				questionNameLabel.setText(questionCount + ". ");

				TextField questionNameField = new TextField();
				questionNameField.setText(newQuestion);

				Button addAnswerButton = new Button("Add Answer");
				addAnswerButtonFunctionality(answerList, addAnswerButton);

				questionRow.getChildren().addAll(questionNameLabel, questionNameField, addAnswerButton);
				questionList.getChildren().addAll(questionRow, answerList);
				saveSurvey.setDisable(false);
			} else {
				textInputDialog = createDialog("Please choose a name for your survey: ");
				surveyNameField.setText(textInputDialog.showAndWait().get());
			}
		});
		//Save survey in the system
		saveSurvey.setOnAction(event -> {
			JSONBuilder builder = new JSONBuilder();
			this.userData = builder.surveyToJSON(userData, surveyNameField.getText(),  extractSurveyData());
			DatabaseMessenger messenger = new DatabaseMessenger();
			//put saveSurvey into if statement and make a dialogbox upon receiving result from the server
			messenger.saveSurvey(this.userData);
			LoggedInPage loggedInPageInstance = new LoggedInPage(mainPageInstance, this.userData);
			mainPageInstance.getMainStage().setScene(loggedInPageInstance.getScene());
		});
		saveSurvey.setDisable(true);
		
		Button backButton = new Button("Back");
		backButton.setOnAction(event->{
			LoggedInPage loggedInPageInstance = new LoggedInPage(mainPageInstance, this.userData);
			mainPageInstance.getMainStage().setScene(loggedInPageInstance.getScene());
		});
		ColumnConstraints columnConstraints = new ColumnConstraints();
		columnConstraints.setFillWidth(false);
		root.setHgap(10);
		root.setVgap(10);

		RowConstraints rowConstraints = new RowConstraints();
		rowConstraints.setPercentHeight(10);

		root.getColumnConstraints().add(columnConstraints);
		root.getRowConstraints().add(rowConstraints);

		root.add(surveyNameLabel, 1, 0);
		root.add(surveyNameField, 2, 0);
		root.add(questionList, 2, 1);
		root.add(addQuestionButton, 2, 2);
		root.add(saveSurvey, 2, 3);
		root.add(backButton, 2, 4);
	}
	/**
	 * Method constructs a dialog box with specified message
	 * @param message that is to be displayed by the dialog box
	 * @return Dialog box with desired message
	 */
	private TextInputDialog createDialog(String message) {
		TextInputDialog textInputDialog = new TextInputDialog();
		textInputDialog.setContentText(message);
		return textInputDialog;
	}
	/**
	 * Method creates functionality for button that is passed in as an argument
	 * @param answerList VBox node in which question answers are stored in
	 * @param button which functionality is attached too
	 */
	private void addAnswerButtonFunctionality(VBox answerList, Button button) {
		button.setOnAction(event -> {
			TextField answer = new TextField();
			VBox.setMargin(answer, new Insets(3, 0, 0, 20));
			answerList.getChildren().add(answer);
		});
	}
	/**
	 * Method extracts data from the page and organises it inside a HashMap
	 * @return Hashmap <String, String []>of questions and answers
	 */
	private HashMap<String, String[]> extractSurveyData() {
		HashMap<String, String[]> surveyData = new HashMap<String, String[]>();
		String questionNumber =  "";
		String questionPlaceHolder = "";

		ObservableList<Node> questionListChildren = questionList.getChildren();
		for (Node questionListChild : questionListChildren) {
//			Note: Children of questionList. Hbox contains question and addAnswerButton, Vbox contains all the answers to a question
//			HBox@5e7bc2bd Q1
//			VBox@6184b208 Answers1
//			HBox@1889c43f Q2
//			VBox@4ecb01dc Answers2
//			...

			if (questionListChild.toString().contains("HBox")) {
				//Note: HBox questionRow consist of 3 elements - 1. Label (question number) 2. TextField (Question) 3. Button (Add answer button)
				HBox questionRow = (HBox) questionListChild;
				questionNumber = ((Label) questionRow.getChildren().get(0)).getText();
				questionPlaceHolder = ((TextField)questionRow.getChildren().get(1)).getText();
				if(questionPlaceHolder.length()<1) {
					textInputDialog = createDialog("Please insert question "+questionNumber);
					questionPlaceHolder = textInputDialog.showAndWait().get();
				}
			} 
			else if (questionListChild.toString().contains("VBox")) {
				//Note: VBox answerList consist of all the answer fields for 1 question 
				int answerCount = 1;
				VBox answerList = (VBox) questionListChild;
				String [] answers = new String[answerList.getChildren().size()];
				for (int i = 0; i < answerList.getChildren().size(); i++) {
					String answer = ((TextField)answerList.getChildren().get(i)).getText();
					if(answer.contains("||")) {
						answer = answer.replace("||", "|");
					}
					if(answer.length()<1) {
						textInputDialog = createDialog("Please insert answer "+answerCount+ " to question "+ questionNumber);
						answer = textInputDialog.showAndWait().get();
					}
					answers[i] = answer;
					answerCount++;
				}
				surveyData.put(questionPlaceHolder, answers);
			}
		}
		return surveyData;
	}

	@Override
	public Scene getScene() {
		return constructSurveyScene;
	}
}
