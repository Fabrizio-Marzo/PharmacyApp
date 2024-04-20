import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
       try {
            Parent root = FXMLLoader.load(ClassLoader.getSystemResource("views/PaneLoginPharmacy.fxml")); // Faccio il Parsing di quello che c'è nel file fxml in root
            Scene scene = new Scene(root); // Creo la scena con il suo contenuto del file fxml
            Image icon = new Image("asset/Logo_Farmacia2.jpg"); // Carico l'immagine della Farmacia 
            primaryStage.getIcons().add(icon); // Metto l'immagine nel palcoscenico
            primaryStage.setTitle("Farmacia");
            
            primaryStage.initStyle(StageStyle.TRANSPARENT); // Non vedo i bordi

            primaryStage.setScene(scene); // Setto la scena nel palcoscenico
            primaryStage.show(); // Mostro la scena
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
