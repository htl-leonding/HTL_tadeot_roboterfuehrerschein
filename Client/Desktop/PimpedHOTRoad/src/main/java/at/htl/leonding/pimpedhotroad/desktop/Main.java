package at.htl.leonding.pimpedhotroad.desktop;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

/**
 * Created by kepplinger on 29.12.16.
 */
public class Main extends Application {

    private static Stage primaryStage = null;

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {

        //Prevents bad font rendering in Ubuntu
        System.setProperty("prism.lcdtext", "false");
        System.setProperty("prism.text", "t2k");

        Main.primaryStage = primaryStage;

        Parent root = FXMLLoader.load(getClass().getResource("/fxml/view.fxml"));

        Scene scene = new Scene(root);

        Main.primaryStage.setTitle("Robo Führerschein");
        Main.primaryStage.getIcons().add(new Image(getClass().getResourceAsStream("/images/icon.png")));
        Main.primaryStage.setScene(scene);
        Main.primaryStage.centerOnScreen();
        Main.primaryStage.show();
    }

    /**
     * Returns the primaryStage for the Controller to add listeners.
     * @return
     */
    static Stage getPrimaryStage() {
        return primaryStage;
    }
}
