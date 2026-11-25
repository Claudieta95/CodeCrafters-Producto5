package codecrafters.vista.gui;

import codecrafters.controlador.Controlador;
import codecrafters.modelo.Cliente;
import codecrafters.modelo.Reserva;

import java.time.format.DateTimeFormatter;
import java.util.stream.Collectors;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class ConsultaReservasGUI {

    private final Controlador controlador;

    private final TableView<Reserva> tabla = new TableView<>();

    private final DateTimeFormatter formatoFecha =
            DateTimeFormatter.ofPattern("dd/MM/yyyy");

    public ConsultaReservasGUI(Controlador controlador) {

        this.controlador = controlador;
    }

    // Ventana de consulta de reservas de un cliente.
    public void mostrar(Cliente cliente) {

        Stage ventana = new Stage();

        ventana.setTitle("Reservas del cliente");
        ventana.initModality(Modality.APPLICATION_MODAL);

        Label titulo = new Label("Reservas del cliente");
        titulo.getStyleClass().add("titulo-pagina");

        Label descripcion = new Label(
                cliente.getNombre() + " "
                + cliente.getApellido()
                + " (ID: " + cliente.getIdPersona() + ")"
        );

        descripcion.getStyleClass().add("descripcion-pagina");

        configurarTabla();

        // Recuperación de las reservas mediante el controlador.
        try {

            tabla.getItems().setAll(
                    controlador.buscarReservasPorCliente(cliente));

        } catch (RuntimeException e) {

            Alert alerta = new Alert(Alert.AlertType.ERROR);

            alerta.setTitle("Error");
            alerta.setHeaderText(null);
            alerta.setContentText(
                    "No se han podido consultar las reservas del cliente."
            );

            alerta.showAndWait();

            return;
        }

        // Número de reservas encontradas.
        Label resultado = new Label(
                "Reservas encontradas: " + tabla.getItems().size());

        resultado.getStyleClass().add("descripcion-pagina");

        // Botón para cerrar la consulta.
        Button cerrar = new Button("Cerrar");

        cerrar.getStyleClass().add("boton-secundario");
        cerrar.setOnAction(evento -> ventana.close());

        HBox acciones = new HBox(cerrar);
        acciones.setAlignment(Pos.CENTER_RIGHT);

        VBox contenido = new VBox(
                20, titulo, descripcion, tabla, resultado, acciones);

        contenido.setPadding(new Insets(30));
        contenido.getStyleClass().add("contenido-principal");

        VBox.setVgrow(tabla, Priority.ALWAYS);

        Scene escena = new Scene(contenido, 1000, 500);

        escena.getStylesheets().add(
                getClass().getResource("/css/estilos.css")
                        .toExternalForm()
        );

        ventana.setScene(escena);
        ventana.setMinWidth(850);
        ventana.setMinHeight(400);

        ventana.showAndWait();
    }

    // Configuración de las columnas de reservas.
    private void configurarTabla() {

        TableColumn<Reserva, Number> columnaId =
                new TableColumn<>("ID");

        columnaId.setCellValueFactory(dato ->
                new SimpleIntegerProperty(
                        dato.getValue().getIdReserva()));

        TableColumn<Reserva, String> columnaEntrada =
                new TableColumn<>("Entrada");

        columnaEntrada.setCellValueFactory(dato ->
                new SimpleStringProperty(
                        dato.getValue().getFechaEntrada()
                                .format(formatoFecha)));

        TableColumn<Reserva, String> columnaSalida =
                new TableColumn<>("Salida");

        columnaSalida.setCellValueFactory(dato ->
                new SimpleStringProperty(
                        dato.getValue().getFechaSalida()
                                .format(formatoFecha)));

        TableColumn<Reserva, String> columnaHabitaciones =
                new TableColumn<>("Habitaciones");

        columnaHabitaciones.setCellValueFactory(dato ->
                new SimpleStringProperty(
                        dato.getValue().getLineasReserva()
                                .stream()
                                .filter(linea ->
                                        linea.getHabitacion() != null)
                                .map(linea ->
                                        String.valueOf(
                                                linea.getHabitacion()
                                                        .getNumero()))
                                .collect(Collectors.joining(", "))
                ));

        TableColumn<Reserva, Number> columnaPersonas =
                new TableColumn<>("Personas");

        columnaPersonas.setCellValueFactory(dato ->
                new SimpleIntegerProperty(
                        dato.getValue().getNumeroPersonas()));

        TableColumn<Reserva, String> columnaEstado =
                new TableColumn<>("Estado");

        columnaEstado.setCellValueFactory(dato ->
                new SimpleStringProperty(
                        dato.getValue().getEstado()));

        TableColumn<Reserva, String> columnaImporte =
                new TableColumn<>("Importe");

        columnaImporte.setCellValueFactory(dato ->
                new SimpleStringProperty(
                        String.format("%.2f €",
                                dato.getValue().getImporte())));

        tabla.getColumns().clear();

        tabla.getColumns().add(columnaId);
        tabla.getColumns().add(columnaEntrada);
        tabla.getColumns().add(columnaSalida);
        tabla.getColumns().add(columnaHabitaciones);
        tabla.getColumns().add(columnaPersonas);
        tabla.getColumns().add(columnaEstado);
        tabla.getColumns().add(columnaImporte);

        tabla.setColumnResizePolicy(
                TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);

        tabla.setPlaceholder(
                new Label("Este cliente no tiene reservas registradas."));
    }
}
