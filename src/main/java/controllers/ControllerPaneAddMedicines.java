package controllers;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import dto.Prodotto;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import db.ConnectionProvider;
import db.FarmaciaDB;
import db.Utils;

public class ControllerPaneAddMedicines implements Initializable{
    @FXML
    private Button addBtn;

    @FXML
    private TextField aic_ean;

    @FXML
    private TextField amount;

    @FXML
    private Button close_btn;

    @FXML
    private TableColumn<Prodotto, String> colParapharmaClass;
    
    @FXML
    private TableColumn<Prodotto, String> colEquivaCode;

    @FXML
    private TableColumn<Prodotto, String> colTypeRecipe;

    @FXML
    private TableColumn<Prodotto, String> colAic_ean;

    @FXML
    private TableColumn<Prodotto, String> colProductType;

    @FXML
    private TableColumn<Prodotto, String> colProductName;

    @FXML
    private TableColumn<Prodotto, String> colProducer;

    @FXML
    private TableColumn<Prodotto, String> colAmount;

    @FXML
    private TableColumn<Prodotto, String> colMethodAdmi;

    @FXML
    private TableColumn<Prodotto, Integer> colTotalStock;

    @FXML
    private TableView<Prodotto> product_TableView;

    @FXML
    private Button dashboard_btn;

    @FXML
    private TextField description;

    @FXML
    private TextField equivalenceCode;

    @FXML
    private FontAwesomeIcon logout_btn;

    @FXML
    private TextField methodAdministration;

    @FXML
    private TextField parahamaceuticalClass;

    @FXML
    private TextField price;

    @FXML
    private TextField producer;

    @FXML
    private TextField productName;

    @FXML
    private TextField productType;

    @FXML
    private TextField typeOfRecipe;

    @FXML
    void close(ActionEvent event) {
        System.exit(0);
    }

    public void returnDashboard(ActionEvent event){    
        dashboard_btn.getScene().getWindow().hide(); // Mette la prossima scena in mezzo allo schermo
        Utils.switchScene(event, "views/PaneDashboardPharmacy.fxml");
    }

    private ObservableList<Prodotto> medicinesList(){ // Funzione che restituisce la lista dei prodotti in ordine decrescente a seconda dell'AIC_EAN
        FarmaciaDB farmacia = new FarmaciaDB(new ConnectionProvider().getMySQLConnection());
        ObservableList<Prodotto> listData = FXCollections.observableArrayList();
        try {
            listData = farmacia.getProduct();
        } catch (Exception e) {
            Utils.crashWithMessage(e.toString());
        }
        return  FXCollections.unmodifiableObservableList(listData); // E' meglio tornare una lista non modificabile
    } 

    public void showMedicinesList(){ // Faccio vedere i prodotti su Tabella
        ObservableList<Prodotto> productList = medicinesList();
        colParapharmaClass.setCellValueFactory(new PropertyValueFactory<>("classe_parafarmaco"));
        colEquivaCode.setCellValueFactory(new PropertyValueFactory<>("codice_di_equivaleza"));
        colTypeRecipe.setCellValueFactory(new PropertyValueFactory<>("tipo_di_ricetta"));
        colAic_ean.setCellValueFactory(new PropertyValueFactory<>("aic_ean"));
        colProductType.setCellValueFactory(new PropertyValueFactory<>("tipo_prodotto"));
        colProductName.setCellValueFactory(new PropertyValueFactory<>("nome"));
        colProducer.setCellValueFactory(new PropertyValueFactory<>("produttore"));
        colAmount.setCellValueFactory(new PropertyValueFactory<>("quatità"));
        colMethodAdmi.setCellValueFactory(new PropertyValueFactory<>("metodo_di_somministrazione"));
        colTotalStock.setCellValueFactory(new PropertyValueFactory<>("giacenza_totale"));
        product_TableView.setItems(productList);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
       showMedicinesList();
    }

    public void addProduct(ActionEvent event){
        FarmaciaDB farmacia = new FarmaciaDB(new ConnectionProvider().getMySQLConnection());
        
            if (description.getText().isEmpty()
                || price.getText().isEmpty()
                || aic_ean.getText().isEmpty()
                || equivalenceCode.getText().isEmpty()
                || typeOfRecipe.getText().isEmpty()
                || productType.getText().isEmpty()
                || productName.getText().isEmpty()
                || producer.getText().isEmpty()
                || methodAdministration.getText().isEmpty()
                || amount.getText().isEmpty()){
                    Utils.alertErrorMessage("Error Massage", null, "Please fill all blank fields");

            } else {
                try {
                    if(farmacia.checkProductExist(productName.getText())){ // Se il prodotto è presente
                        Utils.alertErrorMessage("Error Message", null, "Product AIC_EAN: "+aic_ean.getText()+ " was already exist");
                    }
                    farmacia.addProduct(description.getText(), Double.parseDouble(price.getText()), aic_ean.getText(), parahamaceuticalClass.getText(), equivalenceCode.getText(), typeOfRecipe.getText(), productType.getText(), productName.getText(), producer.getText(), methodAdministration.getText(), amount.getText());
                    refreshAddList(List.of(price,aic_ean,equivalenceCode,typeOfRecipe,productType,productName,producer,methodAdministration,amount,description,parahamaceuticalClass));
                    showMedicinesList();
                    Utils.alertInformationMessage("Information Message", null, "Successfully Added!");
                } catch (Exception e) {
                    Utils.crashWithMessage(e.toString());
                }
            }
    }


private void refreshAddList(final List<TextField> s){
    for(TextField i : s){
        i.setText("");
    }
}


}
