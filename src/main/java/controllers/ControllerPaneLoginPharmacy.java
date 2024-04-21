package controllers;

import java.io.IOException;
import java.sql.SQLException;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import db.ConnectionProvider;
import db.FarmaciaDB;
import db.Utils;

public class ControllerPaneLoginPharmacy {
    
    @FXML
    private Button close;

    @FXML
    private PasswordField idPharmacist;

    @FXML
    private Button loginButton;

    @FXML
    private AnchorPane main_form;

    @FXML
    private TextField username;

        
    public void login(ActionEvent event) throws IOException{
        // Stabilisco la connesione al DataBase e ho tutte le StoredProcedure di Farmacia 
        FarmaciaDB farmacia = new FarmaciaDB(new ConnectionProvider().getMySQLConnection());
        try {
            if (username.getText().isEmpty() || idPharmacist.getText().isEmpty()){
               Utils.alertErrorMessage("Error Massage", null, "Please fill all blank fields");
            } else {
                if (farmacia.checkLogin(username.getText(), Integer.parseInt(idPharmacist.getText()))){
                    Utils.alertInformationMessage("Information Message", null, "Successfuly Login");
                    // HIDE YOUR LOGIN FORM
                    loginButton.getScene().getWindow().hide();
                    // LINK MY DASHBOARD FORM
                    Parent root = FXMLLoader.load(getClass().getResource("/views/PaneDashboardPharmacy.fxml"));
                    Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                    Scene scene = new Scene(root);
                    stage.setScene(scene);
                    stage.show();
                } else {
                    Utils.alertErrorMessage("Error Massage", null, "Wrong Username/Password");
                }
            }
        } catch (NumberFormatException e ) {
            Utils.alertErrorMessage("Error Massage", null, "Wrong Username/Password");
        } catch (SQLException e){
            Utils.crashWithMessage(e.toString());
            return ;
        }
    }

    
    public void close(){ // Pulsante x sopra (Fatta da me)
        System.exit(0);
    }

}
