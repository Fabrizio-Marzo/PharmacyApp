package controllers;

import java.net.URL;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

import dto.VoceScontrino;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import main.ConnectionProvider;
import main.FarmaciaDB;
import main.Utils;

public class ControllerPanePurchaseMedicinesServices implements Initializable{
    
    @FXML
    private Button btn_AddReceipt;

    @FXML
    private Button btn_Modify;

    @FXML
    private Button btn_Pay;

    @FXML
    private Button close_btn;

    @FXML
    private Button dashboard_btn;

    @FXML
    private TextField purchase_CF;

    @FXML
    private TextField purchase_IDObject;

    @FXML
    private ComboBox<String> purchase_PayMethod;

    @FXML
    private TextField purchase_Qty;

    @FXML
    private Label purchase_Total;
    
    @FXML
    private TableView<VoceScontrino> purchase_TableView;
   
    @FXML
    private TableColumn<VoceScontrino, Integer> col_IDObject;

    @FXML
    private TableColumn<VoceScontrino, Double> col_PartialPrice;

    @FXML
    private TableColumn<VoceScontrino, Integer> col_Position;

    @FXML
    private TableColumn<VoceScontrino, Integer> col_Quantity;
    
    @FXML
    private TableColumn<VoceScontrino, Integer> col_ReceiptNumber;

    private ObservableList<VoceScontrino> voceScotrinoList = FXCollections.observableArrayList();

    private String[] payMethod ={"Carta di Credito","Contanti"};
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
       newPurchase();
       refreshReceiptList();
       purchase_PayMethod.getItems().addAll(payMethod);
    }

    private void newPurchase(){ // Aggiungo un nuovo scontrino all'ingresso
    FarmaciaDB farmacia = new FarmaciaDB(new ConnectionProvider().getMySQLConnection());
    try {
        farmacia.newPurchase();
    } catch (SQLException e) {
        Utils.crashWithMessage(e.toString());
    }
    return ;
    }

    public void addReceiptItem(ActionEvent event){
        FarmaciaDB farmacia = new FarmaciaDB(new ConnectionProvider().getMySQLConnection());
    if (purchase_IDObject.getText().isEmpty() || purchase_Qty.getText().isEmpty()){
        Utils.alertErrorMessage("Error Message", null, "Please fill all blank fields");
    } else {
        try {
            if (!farmacia.checkGiacenza(Integer.parseInt(purchase_IDObject.getText()), Integer.parseInt(purchase_Qty.getText()))){
                Utils.alertErrorMessage("Error Massage", null, "No product on the Shop");
                Utils.refreshPurchaseList(List.of(purchase_IDObject,purchase_Qty));
            } else {
            farmacia.creazioneVoceScontrino(Integer.parseInt(purchase_IDObject.getText()), Integer.parseInt(purchase_Qty.getText()));
            Utils.refreshPurchaseList(List.of(purchase_IDObject,purchase_Qty)); 
            showLastReceiptItem();
            showTotalReceipt();
            }
        } catch (NumberFormatException e) {
            Utils.crashWithMessage(e.toString());
            return ;
        } catch (Exception e){
            Utils.crashWithMessage(e.toString());
            return ;
        }
    }
    }

    private VoceScontrino getLastReceiptitem(){ // Ottengo l'ultima Voce Scontrino
        FarmaciaDB farmacia = new FarmaciaDB(new ConnectionProvider().getMySQLConnection());
        VoceScontrino lst;
        try {
             lst = farmacia.lastReceiptitem();
        } catch (Exception e) {
            Utils.crashWithMessage(e.toString());
           return null ;
        }
        return lst;
    }

    private void showLastReceiptItem(){
        voceScotrinoList.add(getLastReceiptitem());
        col_Position.setCellValueFactory(new PropertyValueFactory<>("posizioneElenco"));
        col_IDObject.setCellValueFactory(new PropertyValueFactory<>("idOggettoVendita"));
        col_Quantity.setCellValueFactory(new PropertyValueFactory<>("quantità"));
        col_PartialPrice.setCellValueFactory(new PropertyValueFactory<>("prezzoParziale"));
        col_ReceiptNumber.setCellValueFactory(new PropertyValueFactory<>("numeroProgressivo"));
        purchase_TableView.setItems(voceScotrinoList);
    }

    private void showTotalReceipt(){
        FarmaciaDB farmacia = new FarmaciaDB(new ConnectionProvider().getMySQLConnection());
        Double lastTotalReceipt = 0.0;
        try {
            lastTotalReceipt = farmacia.lastTotalReceipt();
        } catch (SQLException e) {
            Utils.crashWithMessage(e.toString());
        }
        purchase_Total.setText("€" + Double.toString(lastTotalReceipt));
    }
    
    public void pay(ActionEvent event) throws SQLException{
        FarmaciaDB farmacia = new FarmaciaDB(new ConnectionProvider().getMySQLConnection());
        if (farmacia.lastTotalReceipt() == 0.0){
            Utils.alertErrorMessage("Error Message", null, "The Receipt is empty!");
        } else {
        Alert alert = Utils.alertConfirmatioMessage("Confirmation Message", null, "Are you sure you want to pay?");
        Optional<ButtonType> option = alert.showAndWait();
            if (option.get().equals(ButtonType.OK)){
                btn_Pay.getScene().getWindow().hide(); // Mette la prossima scena in mezzo allo schermo
                Utils.switchScene(event, "views/PaneDashboardPharmacy.fxml");
                Utils.alertInformationMessage("Information Message", null, "Successfuly Payment");
            } 
        }
    }

    public void modify(ActionEvent event){
        FarmaciaDB farmacia = new FarmaciaDB(new ConnectionProvider().getMySQLConnection());
        try {
            farmacia.modifyMethodPayCF(purchase_PayMethod.getValue(), purchase_CF.getText());
            Utils.alertInformationMessage("Information Message", null, "Successfuly Modify");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void returnDashboard(ActionEvent event){    
        dashboard_btn.getScene().getWindow().hide(); // Mette la prossima scena in mezzo allo schermo
        Utils.switchScene(event, "views/PaneDashboardPharmacy.fxml");
    }

    public void close(ActionEvent event) {
     System.exit(0);
    }

   

    private void refreshReceiptList(){
        voceScotrinoList.clear();
    }
}
