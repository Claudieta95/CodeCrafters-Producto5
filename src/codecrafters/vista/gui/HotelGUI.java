package codecrafters.vista.gui;

import codecrafters.controlador.Controlador;
import codecrafters.modelo.Hotel;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

public class HotelGUI extends VBox {

    private final Controlador controlador;

    private final TableView<Hotel> tabla = new TableView<>();

    private final TextField buscador = new TextField();

    public HotelGUI(Controlador controlador) {

        this.controlador = controlador;

        setSpacing(20);
        setPadding(new Insets(35));
        getStyleClass().add("contenido-principal");

        // Cabecera de la sección.
        Label titulo = new Label("Gestión de hoteles");
        titulo.getStyleClass().add("titulo-pagina");

        Label descripcion = new Label(
                "Consulta los hoteles del sistema y busca por ciudad.");

        descripcion.getStyleClass().add("descripcion-pagina");

        // Buscador de hoteles por ciudad.
        buscador.setPromptText("Buscar por ciudad");

        Button buscar = new Button("Buscar");
        buscar.getStyleClass().add("boton-principal");
        buscar.setOnAction(evento -> buscarHoteles());

        buscador.setOnAction(evento -> buscarHoteles());

        Button mostrarTodos = new Button("Mostrar todos");
        mostrarTodos.getStyleClass().add("boton-secundario");

        mostrarTodos.setOnAction(evento -> {
            buscador.clear();
            listarHoteles();
        });

        HBox barraBusqueda = new HBox(
                10, buscador, buscar, mostrarTodos);

        barraBusqueda.setAlignment(Pos.CENTER_LEFT);
        HBox.setHgrow(buscador, Priority.ALWAYS);

        // Tabla de hoteles.
        configurarTabla();

        VBox.setVgrow(tabla, Priority.ALWAYS);

        getChildren().addAll(
                titulo,
                descripcion,
                barraBusqueda,
                tabla
        );

        listarHoteles();
    }

    // Configuración de las columnas de la tabla.
    private void configurarTabla() {

        TableColumn<Hotel, Number> columnaId =
                new TableColumn<>("ID");

        columnaId.setCellValueFactory(dato ->
                new SimpleIntegerProperty(
                        dato.getValue().getIdHotel()));

        TableColumn<Hotel, String> columnaNombre =
                new TableColumn<>("Nombre");

        columnaNombre.setCellValueFactory(dato ->
                new SimpleStringProperty(
                        dato.getValue().getNombre()));

        TableColumn<Hotel, String> columnaCiudad =
                new TableColumn<>("Ciudad");

        columnaCiudad.setCellValueFactory(dato ->
                new SimpleStringProperty(
                        dato.getValue().getCiudad()));

        TableColumn<Hotel, Number> columnaEstrellas =
                new TableColumn<>("Estrellas");

        columnaEstrellas.setCellValueFactory(dato ->
                new SimpleIntegerProperty(
                        dato.getValue().getEstrellas()));

        tabla.getColumns().add(columnaId);
        tabla.getColumns().add(columnaNombre);
        tabla.getColumns().add(columnaCiudad);
        tabla.getColumns().add(columnaEstrellas);

        tabla.setColumnResizePolicy(
                TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);

        tabla.setPlaceholder(
                new Label("No se han encontrado hoteles."));

        tabla.setPrefHeight(400);
        tabla.setMinHeight(120);
    }

    // Consulta de todos los hoteles.
    private void listarHoteles() {

        try {

            tabla.getItems().setAll(
                    controlador.listarHoteles());

        } catch (RuntimeException e) {

            mostrarMensaje(
                    "Error",
                    "No se han podido consultar los hoteles."
            );
        }
    }

    // Búsqueda de hoteles por ciudad.
    private void buscarHoteles() {

        String ciudad = buscador.getText().trim();

        if (ciudad.isEmpty()) {
            listarHoteles();
            return;
        }

        try {

            tabla.getItems().setAll(
                    controlador.buscarHotelesPorCiudad(ciudad));

        } catch (RuntimeException e) {

            mostrarMensaje(
                    "Error",
                    "No se ha podido realizar la búsqueda de hoteles."
            );
        }
    }

    // Mensajes de error.
    private void mostrarMensaje(
            String titulo,
            String mensaje) {

        Alert alerta = new Alert(Alert.AlertType.ERROR);

        alerta.setTitle(titulo);
        alerta.setHeaderText(null);
        alerta.setContentText(mensaje);

        alerta.showAndWait();
    }
}