package com.github.zerenat.SurveyApplication.GUI;

import javafx.scene.Scene;
import javafx.scene.layout.GridPane;

public class EditSurveyPage implements IGUIPageInterface{
	private Scene editSurveyScene;
	public EditSurveyPage() {
		GridPane root = new GridPane();
		editSurveyScene = new Scene(root, 500, 600);
	}
	
	@Override
	public Scene getScene() {
		return editSurveyScene;
	}

}
