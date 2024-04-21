package controllers;

import java.net.URL;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;


import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import main.ConnectionProvider;
import main.FarmaciaDB;
import main.Utils;

public class ControllerDialogNewOrder implements Initializable{
    
String[] fa = {"fa","sol","do"};

@FXML
    private ComboBox<String> dialog_comoBox;

@Override
public void initialize(URL location, ResourceBundle resources) {
    setComboBox();
}

// Setto la lista dei ComboBox
private void setComboBox(){
    FarmaciaDB farmacia = new FarmaciaDB(new ConnectionProvider().getMySQLConnection());
    List<String> stockist = new ArrayList<>();
    try {
        stockist = farmacia.getListStockist();
    } catch (SQLException e) {
        e.printStackTrace();
    }
    String[] stockistList = new String[stockist.size()];
    stockistList = stockist.toArray(stockistList); // Converto la Lista in Stringa
    dialog_comoBox.getItems().addAll(stockistList);
}

private void setOrder(final String nomeFornitore){
    FarmaciaDB farmacia = new FarmaciaDB(new ConnectionProvider().getMySQLConnection());
    try {
        farmacia.effettuazioneOrdine(nomeFornitore);
    } catch (SQLException e) {
        e.printStackTrace();
    }
}

public Boolean controllorComboBox(){
    if(dialog_comoBox.getValue()==null){
        Utils.alertErrorMessage("Error Message", null, "Choise a name of Stocklist");
        return false;
    }
    return true;
}

public void newOrder(final ActionEvent event){
    setOrder(dialog_comoBox.getValue());
    Utils.switchScene(event, "views/PaneNewOrder.fxml");
}
}