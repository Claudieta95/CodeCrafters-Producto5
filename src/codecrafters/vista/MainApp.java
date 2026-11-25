package codecrafters.vista;

import codecrafters.vista.gui.MenuPrincipalGUI;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class MainApp extends Application {

    @Override
    public void start(Stage stage) {

        // Creación de la interfaz gráfica principal.
        MenuPrincipalGUI raiz = new MenuPrincipalGUI();

        Scene escena = new Scene(raiz, 1100, 700);

        // Aplicación de la hoja de estilos común.
        escena.getStylesheets().add(
                getClass().getResource("/css/estilos.css")
                        .toExternalForm()
        );

        stage.setTitle("CodeCrafters - Gestión hotelera");
        stage.setScene(escena);
        stage.setMinWidth(900);
        stage.setMinHeight(600);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}