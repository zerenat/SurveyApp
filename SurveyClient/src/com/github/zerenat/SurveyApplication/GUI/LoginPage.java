package com.github.zerenat.SurveyApplication.GUI;
import org.json.JSONObject;

import com.github.zerenat.SurveyApplication.Class.DatabaseMessenger;
import com.github.zerenat.SurveyApplication.Class.JSONBuilder;

import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;

public class LoginPage implements IGUIPageInterface{
	private Scene loginPageScene;
	/**
	 * Constructor creates GUI elements and functionality for LoginPage
	 * @param mainPageInstance instance of the main page
	 */
	@SuppressWarnings("static-access")
	public LoginPage(MainPage mainPageInstance) {
		BorderPane root = new BorderPane();
		loginPageScene = new Scene(root, 500, 600);

		TextField emailField = new TextField();
		emailField.setPromptText("Username");

		PasswordField passwordField = new PasswordField();
		passwordField.setPromptText("Password");
		
		Button loginButton = new Button("Login");
		loginButton.setOnAction(event->{
			String email = emailField.getText();
			String password = passwordField.getText();
			DatabaseMessenger messenger = new DatabaseMessenger();
			JSONBuilder jBuilder = new JSONBuilder();
			JSONObject loginData = jBuilder.loginToJSON(email, password);
			JSONObject userData = messenger.identifyUser(loginData);
			if(!(userData.length()==0)) {	
				LoggedInPage loggedInPageInstance = new LoggedInPage(mainPageInstance, userData);
				mainPageInstance.getMainStage().setScene(loggedInPageInstance.getScene());
			}
			else {
				Alert informationAlert = new Alert(AlertType.ERROR);
				informationAlert.setContentText("Login failed. Wrong e-mail or password");
				informationAlert.showAndWait();
			}		
			
		});
		
		Button backButton = new Button("Back");
		backButton.setOnAction(event->{
			
			mainPageInstance.setSceneMainPage();
		});
		
		Node [] nodeList = new Node [] {emailField, passwordField, loginButton, backButton};
		
		VBox centerContainer = new VBox();
		centerContainer.setMaxWidth(200);
 		centerContainer.setMaxHeight(200);
		centerContainer.getChildren().addAll(nodeList);
		for (Node node: nodeList) {
			centerContainer.setMargin(node, new Insets(10,0,0,0));
			node.minWidth(centerContainer.getPrefWidth());
		}
		root.setCenter(centerContainer);
	}

	@Override
	public Scene getScene() {
		return loginPageScene;
	}
}
