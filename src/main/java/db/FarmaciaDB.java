package db;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import dto.Offerta;
import dto.Prodotto;
import dto.VoceOrdine;
import dto.VoceScontrino;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import java.sql.CallableStatement;

public class FarmaciaDB {
    private static Connection connection;
    @SuppressWarnings("unused") // Prova di Annotazione dove tolgo il Warning
    private static FarmaciaDB db;

    // Costruttore
    public FarmaciaDB(final Connection connection){
        FarmaciaDB.connection = Objects.requireNonNull(connection);
    }

    
    /*
    *  1a) Query Principale AggiuntaScontrino()
    */
    public void newPurchase() throws SQLException{
        CallableStatement s;
        try{
        s = connection.prepareCall("{call AggiuntaScontrino(?,?)}");
        s.setString(1,"Contanti");
        s.setString(2, "");
        } catch (SQLException e){
            Utils.crashWithMessage(e.toString()); 
            return ;
        }
        s.execute();
        s.close();
    }
    /*
     *  1b) Query di controllo sulla Giacenza, per la Stored Procedure AggiuntaScontrino()
     */
     public Boolean checkGiacenza(final Integer id_oggetto, final Integer quantità_input) throws SQLException{
        CallableStatement s;
        try{
        s = connection.prepareCall("{call checkGiacenza(?,?,?)}");
        s.setInt(1, id_oggetto);
        s.setInt(2, quantità_input); 
        s.registerOutParameter(3, Types.BOOLEAN);
        } catch (SQLException e){
            Utils.crashWithMessage(e.toString());
            return null; // Questo non verrà ma eseguito perchè andando sul crashWithMessage finisce tutto e mi tira un messagio d'errore
        }
        s.execute();
        return s.getBoolean(3); // Restituisce il valore booleano dalla Stored Procedure
    }
    /*
    *  1c) Query di Aggiunta Voce Scontrino, per la StoredProcedure AggiuntaScontrino
    */
    public void creazioneVoceScontrino(final Integer id_prodotto ,final Integer quantità_input ) throws SQLException{
        CallableStatement s;
        try{
            s = connection.prepareCall("{call CreazioneVoceScontrino(?,?)}");
            s.setInt(1, id_prodotto);
            s.setInt(2, quantità_input);
        }catch(SQLException e){
            Utils.crashWithMessage(e.toString());
            return ; // Non verrà mai eseguito 
        }
        s.execute();
        s.close();
    }
    /*
     *  2a) Query di Controllo Login, per la StoredProcedure checkLogin
     */
    public boolean checkLogin(final String username, final Integer idDipendete) throws SQLException{
    CallableStatement s;
    try {
        s = connection.prepareCall("{call CheckLogin(?,?,?)}");
        s.setString(1, username);
        s.setInt(2, idDipendete);
        s.registerOutParameter(3, Types.BOOLEAN);
    } catch (Exception e) {
       Utils.crashWithMessage(e.toString());
        return false; // Non verrà mai eseguito
    }
     s.execute();
    return s.getBoolean(3);
}
/*
 * 3a) Visualizzazione di tutti i Prodotti, per la Stored Procedure VisualizzazioneProdotti 
 */
public ObservableList<Prodotto> getProduct() throws SQLException{
    CallableStatement s;
    try {
        //Preparo la chiamata alla StoredProcedure
        s = connection.prepareCall("{call VisualizzazioneProdotti()}");
    } catch (Exception e) {
        return null;
    }
    Boolean result = s.execute();
    // Creo un ArrayList di Prodotti
    ObservableList<Prodotto> producList = FXCollections.observableArrayList();
    while (result) {
        ResultSet rs = s.getResultSet();
        while (rs.next()) {
            String classe_parafaraco = rs.getString(1);
            String codice_di_equivaleza =rs.getString(2);
            String tipo_di_ricetta =rs.getString(3);
            String aic_ean= rs.getString(4);
            String tipo_prodotto=rs.getString(5);
            String nome = rs.getString(6);
            String produttore =rs.getString(7);
            String quatità =rs.getString(8);
            String metodo_di_somministrazione = rs.getString(9);
            Integer giacenza_totale = rs.getInt(10);
            // Aggiungo alla mia lista di Prodotti un NUOVO  Prodotto
            producList.add(new Prodotto(classe_parafaraco, codice_di_equivaleza, tipo_di_ricetta, aic_ean, tipo_prodotto, nome, produttore, quatità, metodo_di_somministrazione, giacenza_totale));
        }
        result = s.getMoreResults();
    }
    s.close();
    return producList;
}
/*
 * 3b) Aggiunta di un Prodotto, Stored Procedure AggiuntaProdotto
 */
public void addProduct(final String descrizione, final Double prezzo, final String aic_ean, final String classeParafarmaco,
                          final String codice_equivalenza, final String tipo_ricetta, final String tipo_prodotto, final String nome,
                          final String produttore, final String metodo_somministrazione, final String quantità ) throws SQLException{
    CallableStatement s; // Per fare la StoredProcedured
    try{
    s = connection.prepareCall("{call AggiuntaProdotto(?,?,?,?,?,?,?,?,?,?,?)}");
    s.setString(1, descrizione);
    s.setDouble(2, prezzo);
    s.setString(3, aic_ean);
    s.setString(4,classeParafarmaco);
    s.setString(5,codice_equivalenza);
    s.setString(6,tipo_ricetta);
    s.setString(7,tipo_prodotto);
    s.setString(8,nome);
    s.setString(9,produttore);
    s.setString(10, metodo_somministrazione);
    s.setString(11,quantità);
    }catch(SQLException e){
        Utils.crashWithMessage(e.toString());
        return ;
    }
    s.execute();
    s.close();
}
/*
 * 3c) Controllo se il prodotto che sto inserendo già esiste, Stored Procedure checkProdottoPresente
 */
public Boolean checkProductExist(final String nome_prodotto) throws SQLException{
    CallableStatement s;
    try{
    s = connection.prepareCall("{call checkProdottoPresente(?,?)}");
    s.setString(1, nome_prodotto);
    s.registerOutParameter(2, Types.BOOLEAN);
    } catch (SQLException e){
     Utils.crashWithMessage(e.toString());
     return null; // Questo non verrà ma eseguito perchè andando sul crashWithMessage finisce tutto e mi tira un messagio d'errore
    }
    s.execute();
    return s.getBoolean(2); // Restituisce il valore booleano dalla Stored Procedure
}
/*
 * 4a) Ottego l'ultimo Voce Scontrino, Stored Procedure OttengoUltimoScontrino
 */
public VoceScontrino lastReceiptitem() throws SQLException{
    CallableStatement s;
    try {
        s = connection.prepareCall("{call OttengoUltimoVoceScontrino()}");
    } catch (SQLException e) {
        Utils.crashWithMessage(e.toString());
        return null; // will never run
    }
    s.execute();
    ResultSet rs = s.getResultSet();
    rs.next();
    Integer idOggettoVendita = rs.getInt(1);
    Integer numeroProgressivo = rs.getInt(2);
    Integer posizioneElenco = rs.getInt(3);
    Integer quantità = rs.getInt(4);
    Double prezzoParziale = rs.getDouble(5);
    return new VoceScontrino(idOggettoVendita, numeroProgressivo, posizioneElenco, quantità, prezzoParziale);
}
/*
 * 4b) Ottego Totale dell'ultimo Scontrino, Stored Procedure OttengoUltimoTotale
 */
public Double lastTotalReceipt() throws SQLException{
    CallableStatement s;
    try {
        s = connection.prepareCall("{call OttegoUltimoTotale(?)}");
        s.registerOutParameter(1, Types.DOUBLE);
    } catch (Exception e) {
        Utils.crashWithMessage(e.toString());
        return null; // will never run
    }
    s.execute();
    //Double total = s.getDouble(1);
    //s.close();
    return s.getDouble(1);
}
/*
 * 4c) Modifico il tipo di Pagamento o CF, Stored Procedure ModificaPagamento
 */
public void modifyMethodPayCF(final String payMethod, final String cf) throws SQLException{
    CallableStatement s;
    try {
        s = connection.prepareCall("{call ModificaPagamentoCF(?,?)}");
        s.setString(1,payMethod);
        s.setString(2, cf);
    } catch (SQLException e) {
        Utils.crashWithMessage(e.toString());
        return ;
    }
    s.execute();
    s.close();
}
/*
 * 5a) Ottengo i nomi dei vari Fornitori
 */
public List<String> getListStockist() throws SQLException{
    CallableStatement s;
    try {
        s = connection.prepareCall("{call VedoFornitoriConOfferta()}");
    } catch (SQLException e) {
        Utils.crashWithMessage(e.toString());
        return null;
    }
    Boolean result = s.execute();
    List<String> stockist = new ArrayList<>();
    while (result) {
        ResultSet rs = s.getResultSet();
        while (rs.next()) {
            stockist.add(rs.getString(1));
        }
        result = s.getMoreResults();
    }
    s.close();
    return stockist;
}
/*
 * 5b) Effettuo un ordine, Con la Stored Procedure
 */
public void effettuazioneOrdine(final String nomeFornitore) throws SQLException{
    CallableStatement s;
    try{
        s = connection.prepareCall("{call EffettuazioneOrdine(?)}");
        s.setString(1, nomeFornitore);
    } catch(SQLException e){
        Utils.crashWithMessage(e.toString());
        return ;
    }
    s.execute();
    s.close();
    return ;
}
/*
 * 5c) Visualizzo solo le offerte per il Fornitore scelto, 
 */
public ObservableList<Offerta> getOffer() throws SQLException{
    CallableStatement s;
    try {
        //Preparo la chiamata alla StoredProcedure
        s = connection.prepareCall("{call VedoOffertaByFornitore()}");
    } catch (Exception e) {
        return null;
    }
    Boolean result = s.execute();
    // Creo un ArrayList di Prodotti
    ObservableList<Offerta> offertList = FXCollections.observableArrayList();
    while (result) {
        ResultSet rs = s.getResultSet();
        while (rs.next()) {
            String nome = rs.getString(1);
            Double costo = rs.getDouble(2);
            String aic_ean =rs.getString(3);
            // Aggiungo alla mia lista di Prodotti un NUOVO  Prodotto
            offertList.add(new Offerta(nome,costo,aic_ean));
        }
        result = s.getMoreResults();
    }
    s.close();
    return offertList;
}
/*
 * 5d) Query di controllo sul catalogo offerto dal Fornitore, per la Stored Procedure EffetuazioneOrdine()
 */
public Boolean checkCatalogo(final String aic_ean ) throws SQLException{
    CallableStatement s;
    try{
        s = connection.prepareCall("{call CheckCatalogoUltimoOrdine(?,?)}");
        s.setString(1,aic_ean );
        s.registerOutParameter(2,Types.BOOLEAN);
    } catch( SQLException e ){
        Utils.crashWithMessage(e.toString());
        return false;
    }
    s.execute();
    return s.getBoolean(2);
}
/*
 * 5e) Query di controllo dove creo una VoceOrdine, 
 */
public void creazioneVoceOrdine(final String aic_ean, final Integer quantità_Da_Ordinare) throws SQLException{
    CallableStatement s;
    try{
        s = connection.prepareCall("{call CreazioneVoceOrdine(?,?)}");
        s.setString(1, aic_ean);
        s.setInt(2, quantità_Da_Ordinare);
    }catch(SQLException e) {
        Utils.crashWithMessage(e.toString());
        return ; // Non verrà mai eseguito 
    }
    s.execute();
    s.close();
    return ;
}
/*
 * 5e) Ottengo l'ultimo Voce Ordine
 */
public VoceOrdine lastOrderItem() throws SQLException{
    CallableStatement s;
    try {
        s = connection.prepareCall("{call OttengoUltimoVoceOrdine()}");
    } catch (SQLException e) {
        Utils.crashWithMessage(e.toString());
        return null; // will never run
    }
    s.execute();
    ResultSet rs = s.getResultSet();
    rs.next();
    String aic_ean = rs.getString(1);
    Integer numero_ordine = rs.getInt(2);
    Integer posizione_in_elenco = rs.getInt(3);
    Integer quantità = rs.getInt(4);
    Double costo_unitario = rs.getDouble(5);
    return new VoceOrdine(aic_ean, numero_ordine, posizione_in_elenco, quantità, costo_unitario);
}
/*
 * 6a) Ottego Totale di tutti i dipendenti per il singolo Dipendente di turno giornalmente , Stored Procedure TotaleClientiGiornalieriDipendente
 */
public Integer totalCustomers() throws SQLException{
    CallableStatement s;
    try {
        s = connection.prepareCall("{call TotaliClietiOggiPerDipendente(?)}");
        s.registerOutParameter(1, Types.INTEGER);
    } catch (Exception e) {
        Utils.crashWithMessage(e.toString());
        return null; // will never run
    }
    s.execute();
    //Dou total = s.getDouble(1);
    //s.close();
    return s.getInt(1);
}
/*
 * 6b) Ottego Totale di tutti i Prodotti disponibili fino a questo momento , Stored Procedure TotaliProdottiDisponibili
 */
public Integer availableMedicines() throws SQLException{
    CallableStatement s;
    try {
        s = connection.prepareCall("{call TotaliProdottiDisponibili(?)}");
        s.registerOutParameter(1, Types.INTEGER);
    } catch (Exception e) {
        Utils.crashWithMessage(e.toString());
        return null; // will never run
    }
    s.execute();
    //Double total = s.getDouble(1);
    //s.close();
    return s.getInt(1);
}
/*
 * 6c) Ottego Totale dell'ultimo Scontrino, Stored Procedure OttengoUltimoTotale
 */
public Double totalIncome() throws SQLException{
    CallableStatement s;
    try {
        s = connection.prepareCall("{call TotaleIntroito(?)}");
        s.registerOutParameter(1, Types.DOUBLE);
    } catch (Exception e) {
        Utils.crashWithMessage(e.toString());
        return null; // will never run
    }
    s.execute();
    //Double total = s.getDouble(1);
    //s.close();
    return s.getDouble(1);
}
/*
 * 7a) Prendo per le statistiche le date da considerare
 */
public List<String> getDateForChart() throws SQLException{
    CallableStatement s;
        try {
            s = connection.prepareCall("{call StatisticaDashboardPerDipendenteData()}");
        } catch (SQLException e) {
            Utils.crashWithMessage(e.toString());
            return null; // will never run
        }
        Boolean result = s.execute();
        List<String> listDate = new ArrayList<String>();
        while (result) {
            ResultSet rs = s.getResultSet();
            while (rs.next()) {
                String date = rs.getString(1);
                listDate.add(date);
            }
            result = s.getMoreResults();
        }
        s.close();
        Collections.reverse(listDate);
        return listDate;
}
/*
 * 7b) 
 */
public List<Double> getValueForChart() throws SQLException{
    CallableStatement s;
    try {
        s = connection.prepareCall("{call StatisticaDashboardPerDipendenteFatturato()}");
    } catch (SQLException e) {
        Utils.crashWithMessage(e.toString());
        return null;
    }
    Boolean result = s.execute();
    List<Double> listValue = new ArrayList<>();
    while (result) {
        ResultSet rs = s.getResultSet();
        while (rs.next()) {
            Double value = rs.getDouble(1);
            listValue.add(value);
        }
        result = s.getMoreResults();
    }
    s.close();
    Collections.reverse(listValue);
    return listValue;
}
/*
 * 7c) Prendo per le statistiche le date da considerare MAX
 */
public List<String> getDateForChartMaxIncome() throws SQLException{
    CallableStatement s;
        try {
            s = connection.prepareCall("{call StatisticaDashboardPerDipendenteMiglioreData()}");
        } catch (SQLException e) {
            Utils.crashWithMessage(e.toString());
            return null; // will never run
        }
        Boolean result = s.execute();
        List<String> listDate = new ArrayList<String>();
        while (result) {
            ResultSet rs = s.getResultSet();
            while (rs.next()) {
                String date = rs.getString(1);
                listDate.add(date);
            }
            result = s.getMoreResults();
        }
        s.close();
        Collections.reverse(listDate);
        return listDate;
}
/*
 *  7d) Predo per le statistiche i volari da cosidrare Max
 */
public List<Double> getValueForChartMaxIncome() throws SQLException{
    CallableStatement s;
    try {
        s = connection.prepareCall("{call StatisticaDashboardPerDipendenteMiglioreFatturato()}");
    } catch (Exception e) {
        Utils.crashWithMessage(e.toString());
        return null;
    }
    Boolean result = s.execute();
    List<Double> listValue = new ArrayList<>();
    while (result) {
        ResultSet rs = s.getResultSet();
        while (rs.next()) {
            Double value = rs.getDouble(1);
            listValue.add(value);
        }
        result = s.getMoreResults();
    }
    s.close();
    Collections.reverse(listValue);
    return listValue;
}
/*
 *  7e) Controllo se l'operatore attuale e anche il migliore
 */
public Boolean checkBestOperatorIsCurrentOperator() throws SQLException{
    CallableStatement s;
    try {
        s = connection.prepareCall("{call CheckCurrentOperatorIsBestOperator(?)}");
        s.registerOutParameter(1, Types.BOOLEAN);
    } catch (SQLException e) {
        Utils.crashWithMessage(e.toString());
        return false;
    }
    s.execute();
    return s.getBoolean(1);
}
/*
 * 8a) Stored Procedure per capire il massimo guadagno per ogni mese da un Dipendente Specifico
 */
public String operatorMaxIncomeForMonth(final int mese) throws SQLException{
    CallableStatement s;
    try {
        s = connection.prepareCall("{call DipendenteEntrateMaggiori(?,?,?)}");
        s.setInt(1, mese);
        s.registerOutParameter(2, Types.INTEGER);
        s.registerOutParameter(3, Types.DOUBLE);
    } catch (Exception e) {
        Utils.crashWithMessage(e.toString());
        return null; // will never run
    }
    s.execute();
    String DipendenteGuadagno = "Dipendente:"+s.getInt(2)+", Guadagno:"+s.getDouble(3)+"€";
    s.close();
    return DipendenteGuadagno;
}
/*
 * 8b) Stored Procedure, per capire il mese con massimo guadagno in un mese durante l'anno
 */
public String monthWithMaxIncomeByYear(final int anno) throws SQLException{
    CallableStatement s;
    try {
        s = connection.prepareCall("{call MeseMassimoGuadagno(?,?)}");
        s.setInt(1, anno);
        s.registerOutParameter(2, Types.INTEGER);
    } catch (Exception e) {
        Utils.crashWithMessage(e.toString());
        return null; // will never run
    }
    s.execute();
    String MeseConMassimoGuadagno = "Mese: "+s.getInt(2);
    s.close();
    return MeseConMassimoGuadagno;
}
/*
 * 8c) Stored Procedure, per capire le specifiche di un determinato giorno
 */
public String dayStatistics(final String giorno) throws SQLException{
    CallableStatement s;
    LocalDate day = LocalDate.parse(giorno);
    try {
        s = connection.prepareCall("{call StatisticheGiornaliere(?,?,?)}");
        s.setDate(1, java.sql.Date.valueOf(day));
        s.registerOutParameter(2, Types.DOUBLE);
        s.registerOutParameter(3, Types.INTEGER);
    } catch (Exception e) {
        Utils.crashWithMessage(e.toString());
        return null; // will never run
    }
    s.execute();
    String StatisticheGiornaliere = "Guadagno: "+s.getDouble(2)+", Numero Scontrini: "+s.getInt(3) ;
    s.close();
    return StatisticheGiornaliere;
}
}