package controllers;

import java.sql.SQLException;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import main.ConnectionProvider;
import main.FarmaciaDB;
import main.Utils;

public class ControllerPaneStatistics {
    @FXML
    private Button back;

    @FXML
    private Button dayButton;
    @FXML
    private TextField resultDay;
    @FXML
    private TextField setDay;

    @FXML
    private Button monthButton;
    @FXML
    private TextField resultMonth;
    @FXML
    private TextField setMonth;
    
    @FXML
    private Button yearButton;
    @FXML
    private TextField resultYear;
    @FXML
    private TextField setYear;

public void back(ActionEvent event){
    back.getScene().getWindow().hide();
    Utils.switchScene(event, "views/PaneDashboardPharmacy.fxml");
}

public void operatorMaxIncomeMonth(ActionEvent event){
    FarmaciaDB farmacia = new FarmaciaDB(new ConnectionProvider().getMySQLConnection());
    try {
        if(setMonth.getText().isEmpty()){
            Utils.alertErrorMessage("Error Massage", null, "Please fill all blank fields");
        } else {
            resultMonth.setText(farmacia.operatorMaxIncomeForMonth(Integer.parseInt(setMonth.getText())));
        }
    } catch (SQLException e) {
       Utils.crashWithMessage(e.toString());
    }
}

public void maxIncomeMonthByYear(ActionEvent event){
    FarmaciaDB farmacia = new FarmaciaDB(new ConnectionProvider().getMySQLConnection());
    try {
        if(setYear.getText().isEmpty()){
            Utils.alertErrorMessage("Error Massage", null, "Please fill all blank fields");
        } else {
            resultYear.setText(farmacia.monthWithMaxIncomeByYear(Integer.parseInt(setYear.getText())));
        }
    } catch (SQLException e) {
       Utils.crashWithMessage(e.toString());
    }
}

public void dayStatistics(ActionEvent event){
    FarmaciaDB farmacia = new FarmaciaDB(new ConnectionProvider().getMySQLConnection());
    try {
        if(setDay.getText().isEmpty()){
            Utils.alertErrorMessage("Error Massage", null, "Please fill all blank fields");
        } else {
            resultDay.setText(farmacia.dayStatistics(setDay.getText()));
        }
    } catch (SQLException e) {
       Utils.crashWithMessage(e.toString());
    }
}
}
