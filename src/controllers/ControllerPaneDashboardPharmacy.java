package controllers;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.chart.AreaChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.DialogPane;
import javafx.scene.control.Label;
import javafx.stage.StageStyle;
import main.ConnectionProvider;
import main.FarmaciaDB;
import main.Utils;

public class ControllerPaneDashboardPharmacy implements Initializable{
    @FXML
    private Button btnStatistics;
    @FXML
    private Button addMedicines_btn;
    @FXML
    private Button close_btn;
    @FXML
    private Label dashboard_availableMed;
    @FXML
    private Button dashboard_btn;
    @FXML
    private Label dashboard_chart;
    @FXML
    private Label dashboard_totalCustomers;
    @FXML
    private Label dashboard_totalIncome;
    @FXML
    private FontAwesomeIcon logout_btn;
    @FXML
    private Button purchase_btn;
    @FXML
    private Label username;
    @FXML
    private AreaChart<String, Double> showdashboard_chart;
    
    public void close(){ // Chiude il programma 
        System.exit(0);
    }

    public void logout(ActionEvent event){
        Alert alert = Utils.alertConfirmatioMessage("Confirmation Message", null, "Are you sure you want to logout");
        Optional<ButtonType> option = alert.showAndWait();
            if (option.get().equals(ButtonType.OK)){
                logout_btn.getScene().getWindow().hide(); // Mette la prossima scena in mezzo allo schermo
                Utils.switchScene(event, "views/PaneLoginPharmacy.fxml");
            }
    }

    public void addProduct(ActionEvent event){
        Utils.switchScene(event, "views/PaneAddMedicines.fxml");
    }

    public void statistics(ActionEvent event){
        btnStatistics.getScene().getWindow().hide();
        Utils.switchScene(event, "views/PaneStatistics.fxml");
    }

    public void newReceipt(ActionEvent event){
        Alert alert = Utils.alertConfirmatioMessage("Confirmation Message", null, "Are you sure you want to do a new Receipt?");
        Optional<ButtonType> option = alert.showAndWait();
            if (option.get().equals(ButtonType.OK)){
                purchase_btn.getScene().getWindow().hide(); // Mette la prossima scena in mezzo allo schermo
                Utils.switchScene(event, "views/PanePurchaseMedicinesServices.fxml");
            }
    }


    public void newOrder(ActionEvent event){
        try {
        FXMLLoader dialogloader = new FXMLLoader(getClass().getResource("/views/PaneDialogNewOrder.fxml"));
        DialogPane newOrderPane = dialogloader.load();
        ControllerDialogNewOrder controllerDialogNewOrder = dialogloader.getController();
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("New Order Setting!");
        dialog.initStyle(StageStyle.TRANSPARENT);    
        dialog.setDialogPane(newOrderPane);
        Optional<ButtonType> clickeButton = dialog.showAndWait();
        if (clickeButton.get().equals(ButtonType.NEXT) && controllerDialogNewOrder.controllorComboBox()){
            controllerDialogNewOrder.newOrder(event);
        }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public void getAvailableMedicines(){
        FarmaciaDB farmacia = new FarmaciaDB(new ConnectionProvider().getMySQLConnection());
        Integer aM ;
        try {
            aM = farmacia.availableMedicines();
        } catch (Exception e) {
           return ;
        }
        dashboard_availableMed.setText(""+aM);
    }

    public void getTotalIncome(){
        FarmaciaDB farmacia = new FarmaciaDB(new ConnectionProvider().getMySQLConnection());
        Double aM ;
        try {
            aM = farmacia.totalIncome();
        } catch (Exception e) {
           return ;
        }
        dashboard_totalIncome.setText("€"+aM);
    }

    public void getTotalCustomers(){
        FarmaciaDB farmacia = new FarmaciaDB(new ConnectionProvider().getMySQLConnection());
        Integer aM ;
        try {
            aM = farmacia.totalCustomers();
        } catch (Exception e) {
           return ;
        }
        dashboard_totalCustomers.setText(""+aM);
    }

    public void dashboardChart() {
        FarmaciaDB farmacia = new FarmaciaDB(new ConnectionProvider().getMySQLConnection());
        XYChart.Series series = new XYChart.Series<>();
        List<String> listDate = new ArrayList<>();
        List<Double> listValue = new ArrayList<>();
    
        try {
            listDate = farmacia.getDateForChart();
            listValue = farmacia.getValueForChart();
            var iterDate = listDate.iterator();
            var iterValue = listValue.iterator();        
            while ((iterDate.hasNext() && iterValue.hasNext())) {
                series.getData().add(new XYChart.Data<>(iterDate.next(),iterValue.next()));
            }
        } catch (SQLException e) {
            return ;
        }
        showdashboard_chart.getData().add(series);            
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
       getAvailableMedicines();
       getTotalIncome();
       getTotalCustomers();
       dashboardChart();
    }
}
