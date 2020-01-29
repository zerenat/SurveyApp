package com.github.zerenat.SurveyApplication.GUI;

import org.json.JSONObject;

import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;

public class LoggedInPage implements IGUIPageInterface{
	private Scene loggedInPageScene;
	/**
	 * Constructor creates GUI elements and functionality for LoggedInPage
	 * @param mainPageInstance instance of the main page
	 * @param userData JSONObject containing logged in user's data
	 */
	@SuppressWarnings("static-access")
	public LoggedInPage(MainPage mainPageInstance, JSONObject userData) {
		BorderPane root = new BorderPane();
		loggedInPageScene = new Scene(root, 500, 600);
		
		Button createNewSurveyButton = new Button("New survey");
		createNewSurveyButton.setOnAction(event->{
			CreateSurveyPage createSurveyPageInstance = new CreateSurveyPage(mainPageInstance, userData);	
			mainPageInstance.getMainStage().setScene(createSurveyPageInstance.getScene());
		});
		
		//Button editExistingSurveyButton = new Button("Edit survey");
		
		Button analyseResponsesButton = new Button("Analyse responses");
		analyseResponsesButton.setOnAction(event->{
			AnalyseSurveysPage analyseSurveysPageInstance = new AnalyseSurveysPage(mainPageInstance, this);
			mainPageInstance.getMainStage().setScene(analyseSurveysPageInstance.getScene());
		});
		
		Button logOutButton = new Button("Log out");
		logOutButton.setOnAction(event->{
			mainPageInstance.setSceneMainPage();
		});
		
		Node [] nodeList = new Node [] {createNewSurveyButton, analyseResponsesButton, logOutButton};
		
		VBox centerContainer = new VBox();
		centerContainer.getChildren().addAll(nodeList);
		centerContainer.setMaxWidth(200);
 		centerContainer.setMaxHeight(200);
 		for (Node node: nodeList) {
			centerContainer.setMargin(node, new Insets(10,0,0,0));
			node.minWidth(centerContainer.getPrefWidth());
		}
		
		root.setCenter(centerContainer);
		
	}

	@Override
	public Scene getScene() {
		return loggedInPageScene;
	}
}
