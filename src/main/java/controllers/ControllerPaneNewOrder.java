package controllers;

import java.net.URL;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

import dto.Offerta;
import dto.VoceOrdine;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import db.ConnectionProvider;
import db.FarmaciaDB;
import db.Utils;

public class ControllerPaneNewOrder implements Initializable{

    @FXML
    private Button btn_Order;

    @FXML
    private TextField addOrder_AicEan;

    @FXML
    private TextField addOrder_Qty;

    @FXML
    private Button dashboard_btn;
    
    @FXML
    private TableView<Offerta> purchase_TableViewOffer;

    @FXML
    private TableColumn<Offerta, String> view_NameStocklist;

    @FXML
    private TableColumn<Offerta, Double> view_Price;
    
    @FXML
    private TableColumn<Offerta, String> view_AicEan;

    @FXML
    private TableView<VoceOrdine> purchase_TableViewOrder;

    @FXML
    private TableColumn<VoceOrdine, Integer> order_Position;

    @FXML
    private TableColumn<VoceOrdine, String> order_AicEan;

    @FXML
    private TableColumn<VoceOrdine, Integer> order_Quantity;

    @FXML
    private TableColumn<VoceOrdine, Double> order_UnitCost;

    @FXML
    private TableColumn<VoceOrdine, Integer> order_Number;

    private ObservableList<VoceOrdine> voceOrdineList = FXCollections.observableArrayList();

    public void close(ActionEvent event) {
     System.exit(0);
    }

    public void returnDashboard(ActionEvent event){    
        dashboard_btn.getScene().getWindow().hide(); // Mette la prossima scena in mezzo allo schermo
        Utils.switchScene(event, "views/PaneDashboardPharmacy.fxml");
    }

    private ObservableList<Offerta> offertListByStockist(){ // Funzione che restituisce la lista dei prodotti in ordine decrescente a seconda dell'AIC_EAN
        FarmaciaDB farmacia = new FarmaciaDB(new ConnectionProvider().getMySQLConnection());
        ObservableList<Offerta> listOffer = FXCollections.observableArrayList();
        try {
            listOffer = farmacia.getOffer();
        } catch (Exception e) {
            Utils.crashWithMessage(e.toString());
        }
        return  FXCollections.unmodifiableObservableList(listOffer); // E' meglio tornare una lista non modificabile
    } 

    public void showOfferList(){ // Faccio vedere i prodotti su Tabella
        ObservableList<Offerta> offerList = offertListByStockist();
        view_NameStocklist.setCellValueFactory(new PropertyValueFactory<>("nome"));
        view_Price.setCellValueFactory(new PropertyValueFactory<>("costo"));
        view_AicEan.setCellValueFactory(new PropertyValueFactory<>("aic_ean"));
        purchase_TableViewOffer.setItems(offerList);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        showOfferList();
    }

    public void addAnOrder(ActionEvent event){
        FarmaciaDB farmacia = new FarmaciaDB(new ConnectionProvider().getMySQLConnection());
        if(addOrder_AicEan.getText().isEmpty() || addOrder_Qty.getText().isEmpty()){
            Utils.alertErrorMessage("Error Message", null,"Please fill all blank fields");
        } else {
            try {
                if (!farmacia.checkCatalogo(addOrder_AicEan.getText())) {
                    Utils.alertErrorMessage("Error Message", null, "Stockist hasn't got the product");
                    Utils.refreshPurchaseList(List.of(addOrder_AicEan,addOrder_Qty));
                } else {
                farmacia.creazioneVoceOrdine(addOrder_AicEan.getText(),Integer.parseInt(addOrder_Qty.getText()));
                Utils.refreshPurchaseList(List.of(addOrder_AicEan,addOrder_Qty));
                showLastOrderItem();
                }
            } catch (NumberFormatException e) {
                Utils.crashWithMessage(e.toString());
                return ;
            } catch (Exception e){
                Utils.crashWithMessage(e.toString());
                return ;
            }
        }
        return;
    }

    private VoceOrdine getLastOrderItem(){ // Ottengo l'ultima Voce Scontrino
        FarmaciaDB farmacia = new FarmaciaDB(new ConnectionProvider().getMySQLConnection());
        VoceOrdine lst;
        try {
             lst = farmacia.lastOrderItem();
        } catch (Exception e) {
            Utils.crashWithMessage(e.toString());
           return null ;
        }
        return lst;
    }    

    private void showLastOrderItem(){
        voceOrdineList.add(getLastOrderItem());
        order_Position.setCellValueFactory(new PropertyValueFactory<>("posizione_in_elenco"));
        order_AicEan.setCellValueFactory(new PropertyValueFactory<>("aic_ean"));
        order_Quantity.setCellValueFactory(new PropertyValueFactory<>("quantità"));
        order_UnitCost.setCellValueFactory(new PropertyValueFactory<>("costo_unitario"));
        order_Number.setCellValueFactory(new PropertyValueFactory<>("numero_ordine"));
        purchase_TableViewOrder.setItems(voceOrdineList);
    }

    public void order(ActionEvent event){
        Alert alert = Utils.alertConfirmatioMessage("Confirmation Message", null, "Are you sure you want to do the order?");
        Optional<ButtonType> option = alert.showAndWait();
            if (option.get().equals(ButtonType.OK)){
                btn_Order.getScene().getWindow().hide(); // Mette la prossima scena in mezzo allo schermo
                Utils.switchScene(event, "views/PaneDashboardPharmacy.fxml");
                Utils.alertInformationMessage("Information Message", null, "Successfuly Order");
            } else {
                 return;
            }
           
    }
}
