package db;

import java.io.IOException;
import java.util.List;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class Utils {
 
    public static void crashWithMessage(final String message) {
        // Causa la terminazione dell'applicazione JavaFX
        // Platform.exit();
        System.err.println(message);
        System.exit(1);
    }

    public static void alertErrorMessage(final String alertTitle,final String headerText,final String contentText){
        Alert alert;
        alert = new Alert(AlertType.ERROR);
        alert.setTitle(alertTitle);
        alert.setHeaderText(headerText);
        alert.setContentText(contentText);
        alert.showAndWait(); 
    }

    
    public static void alertInformationMessage(final String alertTitle,final String headerText, final String contentText){
        Alert alert;
        alert = new Alert(AlertType.INFORMATION);
        alert.setTitle(alertTitle);
        alert.setHeaderText(headerText);
        alert.setContentText(contentText);
        alert.showAndWait(); // Ricordati sempre del show and wait sennò non lo vedo a schermo
    }

    public static Alert alertConfirmatioMessage(final String alertTitle,final String headerText, final String contentText){
        Alert alert;
        alert = new Alert(AlertType.INFORMATION);
        alert.setTitle(alertTitle);
        alert.setHeaderText(headerText);
        alert.setContentText(contentText);
        return alert;
    }

    
    public static void switchScene(ActionEvent event, final String uriFxml){ // Switch di Scena nello stesso Stage 
        try {
            Parent root = FXMLLoader.load(ClassLoader.getSystemResource(uriFxml));
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void refreshPurchaseList(final List<TextField> s){
    for(TextField i : s){
        i.setText("");
    }
    }


}


