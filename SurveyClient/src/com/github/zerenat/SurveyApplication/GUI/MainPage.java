package com.github.zerenat.SurveyApplication.GUI;
import javafx.scene.*;
import javafx.scene.control.Button;

import java.io.IOException;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 * 
 * @author Martin Hein
 * student ID: 1705166
 */

public class MainPage extends Application {
	private static Stage mainStage;
	private static Scene mainPageScene;

	public static void main(String[] args) {
		launch(args);
	}
	/**
	 * Main class starts the application and creates GUI elements and functionality for LoggedInPage
	 */
	@SuppressWarnings("static-access")
	@Override
	public void start(Stage primaryStage) throws IOException {
		mainStage = primaryStage;
		primaryStage.setTitle("Welcome to Surveys");

		Button takeSurveyButton = new Button("Take survey");
		takeSurveyButton.setOnAction(event -> {setSceneTakeSurveyPage();});

		Button loginButton = new Button("Log in");
		loginButton.setOnAction(event -> {setSceneLoginPage();});
		
		Button registerButton = new Button("Register");
		registerButton.setOnAction(event -> {setSceneRegisterPage();});

		Node[] nodeList = new Node[] { takeSurveyButton, loginButton, registerButton };
		

		BorderPane root = new BorderPane();
		mainPageScene = new Scene(root,500,600);
		VBox centerContainer = new VBox( nodeList);
		for (Node node: nodeList) {
			centerContainer.setMargin(node, new Insets(10,0,0,0));
			node.maxWidth(centerContainer.getWidth());
		}
		centerContainer.setMaxWidth(200);
 		centerContainer.setMaxHeight(200);
		
		root.setCenter(centerContainer);
		mainStage.setScene(mainPageScene);
		mainStage.show();
	}
	public void changeActiveScene(Scene scene) {
		MainPage.mainStage.setScene(scene);
	}
	
	public void setSceneLoginPage() {
		LoginPage loginPageInstance = new LoginPage(this);
		mainStage.setScene(loginPageInstance.getScene());
	}
	
	public void setSceneRegisterPage() {
		RegisterPage registerPageInstance = new RegisterPage(this);
		mainStage.setScene(registerPageInstance.getScene());
	}
	
	public void setSceneTakeSurveyPage() {
		TakeSurveyPage takeSurveyPageInstance = new TakeSurveyPage(this);
		mainStage.setScene(takeSurveyPageInstance.getScene());
	}
	public void setSceneMainPage() {
		MainPage.mainStage.setScene(mainPageScene);
	}
	public Scene getScene() {
		return mainPageScene;
	}

	public Stage getMainStage() {
		return mainStage;
	}
}
