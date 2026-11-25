package codecrafters.vista.gui;

import codecrafters.controlador.Controlador;
import codecrafters.modelo.Cliente;

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
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

public class ClienteGUI extends VBox {

    private final Controlador controlador;

    private final TableView<Cliente> tabla = new TableView<>();

    private final TextField buscador = new TextField();

    private final TextField nombre = new TextField();
    private final TextField apellido = new TextField();
    private final TextField dni = new TextField();
    private final TextField email = new TextField();
    private final TextField telefono = new TextField();

    public ClienteGUI(Controlador controlador) {

        this.controlador = controlador;

        setSpacing(20);
        setPadding(new Insets(35));
        getStyleClass().add("contenido-principal");

        // Cabecera de la sección.
        Label titulo = new Label("Gestión de clientes");
        titulo.getStyleClass().add("titulo-pagina");

        Label descripcion = new Label(
                "Registra, consulta y busca clientes del sistema.");
        descripcion.getStyleClass().add("descripcion-pagina");

        // Buscador de clientes.
        buscador.setPromptText("Buscar por nombre o apellido");

        Button buscar = new Button("Buscar");
        buscar.getStyleClass().add("boton-principal");
        buscar.setOnAction(evento -> buscarClientes());

        buscador.setOnAction(evento -> buscarClientes());

        Button mostrarTodos = new Button("Mostrar todos");
        mostrarTodos.getStyleClass().add("boton-secundario");

        mostrarTodos.setOnAction(evento -> {
            buscador.clear();
            listarClientes();
        });

        HBox barraBusqueda = new HBox(
                10, buscador, buscar, mostrarTodos);

        barraBusqueda.setAlignment(Pos.CENTER_LEFT);
        HBox.setHgrow(buscador, Priority.ALWAYS);

        // Tabla de clientes.
        configurarTabla();
        VBox.setVgrow(tabla, Priority.ALWAYS);

        // Consulta de reservas del cliente seleccionado.
        Button consultarReservas = new Button("Consultar reservas");
        consultarReservas.getStyleClass().add("boton-secundario");

        consultarReservas.disableProperty().bind(
                tabla.getSelectionModel()
                        .selectedItemProperty().isNull()
        );

        consultarReservas.setOnAction(evento -> {

                Cliente seleccionado =
                        tabla.getSelectionModel().getSelectedItem();

                if (seleccionado != null) {

                        ConsultaReservasGUI consulta =
                                new ConsultaReservasGUI(controlador);

                        consulta.mostrar(seleccionado);
                }
        });

        // Formulario de registro.
        Label tituloFormulario = new Label("Añadir nuevo cliente");
        tituloFormulario.getStyleClass().add("titulo-tarjeta");

        nombre.setPromptText("Nombre");
        apellido.setPromptText("Apellido");
        dni.setPromptText("DNI");
        email.setPromptText("Correo electrónico");
        telefono.setPromptText("Teléfono");

        GridPane formulario = new GridPane();
        formulario.setHgap(12);
        formulario.setVgap(12);

        formulario.add(new Label("Nombre"), 0, 0);
        formulario.add(nombre, 1, 0);

        formulario.add(new Label("Apellido"), 2, 0);
        formulario.add(apellido, 3, 0);

        formulario.add(new Label("DNI"), 0, 1);
        formulario.add(dni, 1, 1);

        formulario.add(new Label("Email"), 2, 1);
        formulario.add(email, 3, 1);

        formulario.add(new Label("Teléfono"), 0, 2);
        formulario.add(telefono, 1, 2);

        Button registrar = new Button("Registrar cliente");
        registrar.getStyleClass().add("boton-principal");
        registrar.setOnAction(evento -> registrarCliente());

        Button limpiar = new Button("Limpiar");
        limpiar.getStyleClass().add("boton-secundario");
        limpiar.setOnAction(evento -> limpiarFormulario());

        HBox acciones = new HBox(10, registrar, limpiar);

        VBox panelFormulario = new VBox(
                15, tituloFormulario, formulario, acciones);

        panelFormulario.setPadding(new Insets(20));
        panelFormulario.getStyleClass().add("tarjeta");

        getChildren().addAll(
                titulo,
                descripcion,
                barraBusqueda,
                tabla,
                consultarReservas,
                panelFormulario
        );

        listarClientes();
    }

    // Configuración de las columnas de la tabla.
    private void configurarTabla() {

        TableColumn<Cliente, Number> columnaId =
                new TableColumn<>("ID");

        columnaId.setCellValueFactory(dato ->
                new SimpleIntegerProperty(
                        dato.getValue().getIdPersona()));

        TableColumn<Cliente, String> columnaNombre =
                new TableColumn<>("Nombre");

        columnaNombre.setCellValueFactory(dato ->
                new SimpleStringProperty(
                        dato.getValue().getNombre()));

        TableColumn<Cliente, String> columnaApellido =
                new TableColumn<>("Apellido");

        columnaApellido.setCellValueFactory(dato ->
                new SimpleStringProperty(
                        dato.getValue().getApellido()));

        TableColumn<Cliente, String> columnaDni =
                new TableColumn<>("DNI");

        columnaDni.setCellValueFactory(dato ->
                new SimpleStringProperty(
                        dato.getValue().getDni()));

        TableColumn<Cliente, String> columnaEmail =
                new TableColumn<>("Email");

        columnaEmail.setCellValueFactory(dato ->
                new SimpleStringProperty(
                        dato.getValue().getEmail()));

        TableColumn<Cliente, String> columnaTelefono =
                new TableColumn<>("Teléfono");

        columnaTelefono.setCellValueFactory(dato ->
                new SimpleStringProperty(
                        dato.getValue().getTelefono()));

        tabla.getColumns().add(columnaId);
        tabla.getColumns().add(columnaNombre);
        tabla.getColumns().add(columnaApellido);
        tabla.getColumns().add(columnaDni);
        tabla.getColumns().add(columnaEmail);
        tabla.getColumns().add(columnaTelefono);

        tabla.setColumnResizePolicy(
                TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);

        tabla.setPlaceholder(
                new Label("No se han encontrado clientes."));

        tabla.setPrefHeight(300);
        tabla.setMinHeight(120);
    }

    // Consulta de todos los clientes.
    private void listarClientes() {

        try {

            tabla.getItems().setAll(
                    controlador.listarClientes());

        } catch (RuntimeException e) {

            mostrarMensaje(
                    Alert.AlertType.ERROR,
                    "Error",
                    "No se han podido consultar los clientes."
            );
        }
    }

    // Búsqueda de clientes por nombre o apellido.
    private void buscarClientes() {

        String texto = buscador.getText().trim();

        if (texto.isEmpty()) {
            listarClientes();
            return;
        }

        try {

            tabla.getItems().setAll(
                    controlador.buscarClientes(texto));

        } catch (RuntimeException e) {

            mostrarMensaje(
                    Alert.AlertType.ERROR,
                    "Error",
                    "No se ha podido realizar la búsqueda."
            );
        }
    }

    // Registro de un nuevo cliente.
    private void registrarCliente() {

        try {

            Cliente cliente = new Cliente(
                    0,
                    nombre.getText().trim(),
                    apellido.getText().trim(),
                    dni.getText().trim(),
                    email.getText().trim(),
                    telefono.getText().trim()
            );

            controlador.agregarCliente(cliente);

        } catch (IllegalArgumentException |
                 IllegalStateException e) {

            mostrarMensaje(
                    Alert.AlertType.ERROR,
                    "Error de registro",
                    e.getMessage()
            );

            return;

        } catch (RuntimeException e) {

            mostrarMensaje(
                    Alert.AlertType.ERROR,
                    "Error de registro",
                    "No se ha podido registrar el cliente."
            );

            return;
        }

        limpiarFormulario();
        buscador.clear();
        listarClientes();

        mostrarMensaje(
                Alert.AlertType.INFORMATION,
                "Cliente registrado",
                "El cliente se ha registrado correctamente."
        );
    }

    // Limpieza de los campos del formulario.
    private void limpiarFormulario() {

        nombre.clear();
        apellido.clear();
        dni.clear();
        email.clear();
        telefono.clear();
    }

    // Mensajes informativos y de error.
    private void mostrarMensaje(
            Alert.AlertType tipo,
            String titulo,
            String mensaje) {

        Alert alerta = new Alert(tipo);

        alerta.setTitle(titulo);
        alerta.setHeaderText(null);
        alerta.setContentText(mensaje);

        alerta.showAndWait();
    }
}