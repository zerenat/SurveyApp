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

public class RegisterPage implements IGUIPageInterface{
	
	private Scene registerPageScene; 
	/**
	 * Constructor creates GUI elements and functionality for RegisterPage
	 * @param mainPageInstance instance of the main page
	 */
	@SuppressWarnings("static-access")
	public RegisterPage(MainPage mainPageInstance) {
		BorderPane root = new BorderPane();
		registerPageScene = new Scene (root, 500, 600);
		
		TextField emailField = new TextField();
		emailField.setPromptText("E-mail");
		PasswordField passwordField = new PasswordField();
		passwordField.setPromptText("Password");
				
		Button submitButton = new Button("Submit");
		submitButton.setOnAction(event->{
			String email = emailField.getText();
			String password = passwordField.getText();
			if(!email.contains("@") || email.length()<6) {
				Alert informationAlert = new Alert(AlertType.INFORMATION);
				informationAlert.setContentText("Email address seems to be invalid. Please double check");
				informationAlert.showAndWait();
				return;
			}
			if(password.length()<6) {
				Alert informationAlert = new Alert(AlertType.INFORMATION);
				informationAlert.setContentText("Please ensure your password contains atleast 6 letters");
				informationAlert.showAndWait();
				return;
			}
			DatabaseMessenger messenger = new DatabaseMessenger();
			JSONBuilder jBuilder = new JSONBuilder();
			JSONObject registerData = jBuilder.registerToJSON(emailField.getText(), passwordField.getText());
			
			if(messenger.registerUser(registerData)) {
				Alert informationAlert = new Alert(AlertType.INFORMATION);
				informationAlert.setContentText("Registration successful");
				informationAlert.showAndWait();
				mainPageInstance.setSceneMainPage();
			}
			else {
				Alert informationAlert = new Alert(AlertType.ERROR);
				informationAlert.setContentText("Registration failed. E-mail has been used");
				informationAlert.showAndWait();
			}
		});		
		
		Button backButton = new Button("Back");
		backButton.setOnAction(event->{
			MainPage mainPage = new MainPage();
			mainPage.getMainStage().setScene(mainPage.getScene());
		});
		
		root.getChildren().addAll(emailField, passwordField);
 		Node [] nodeList = new Node [] {emailField, passwordField, submitButton, backButton};
 		
 		VBox centerContainer = new VBox ();
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
		return registerPageScene;
	}
}
