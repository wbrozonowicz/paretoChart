package sample;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Group;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.fxml.FXML;


public class Main extends Application {





    @Override
    public void start(Stage stage) throws Exception{
        Parent root = FXMLLoader.load(getClass().getResource("sample.fxml"));
        stage.setTitle("Pareto chart!");
        Scene scene = new Scene(root, 1280, 720);
        stage.setResizable(false);
        stage.setScene(scene);
        scene.getStylesheets().add
                (getClass().getResource("chartStyles.css").toExternalForm());
        stage.show();
    }


    public static void main(String[] args) {
        launch(args);
    }
}
