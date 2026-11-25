package codecrafters.vista.gui;

import codecrafters.controlador.Controlador;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

public class MenuPrincipalGUI extends BorderPane {

    private final Controlador controlador = new Controlador();    

    public MenuPrincipalGUI() {

        // Cabecera de la aplicación.
        Label titulo = new Label("CodeCrafters");
        titulo.getStyleClass().add("titulo-cabecera");

        Label subtitulo = new Label("Sistema de gestión hotelera");
        subtitulo.getStyleClass().add("subtitulo-cabecera");

        VBox cabecera = new VBox(5, titulo, subtitulo);
        cabecera.getStyleClass().add("cabecera");
        cabecera.setPadding(new Insets(20));

        setTop(cabecera);

        // Menú lateral de navegación.
        Button inicio = crearBotonMenu("Inicio");
        Button clientes = crearBotonMenu("Clientes");
        Button hoteles = crearBotonMenu("Hoteles");
        Button habitaciones = crearBotonMenu("Habitaciones");
        Button reservas = crearBotonMenu("Reservas");
        Button salir = crearBotonMenu("Salir");

        // Espacio para situar el botón Salir al final del menú.
        Region espacio = new Region();
        VBox.setVgrow(espacio, Priority.ALWAYS);

        VBox menu = new VBox(
                12, inicio, clientes, hoteles,
                habitaciones, reservas, espacio, salir);

        menu.setPadding(new Insets(20));
        menu.setPrefWidth(220);
        menu.getStyleClass().add("menu-lateral");

        setLeft(menu);

        // Acciones de navegación.
        inicio.setOnAction(evento -> mostrarInicio());
        clientes.setOnAction(evento -> mostrarClientes());
        hoteles.setOnAction(evento -> mostrarHoteles());
        habitaciones.setOnAction(evento -> mostrarHabitaciones());
        reservas.setOnAction(evento -> mostrarReservas());
        salir.setOnAction(evento -> Platform.exit());

        mostrarInicio();
    }

    // Creación de los botones del menú lateral.
    private Button crearBotonMenu(String texto) {

        Button boton = new Button(texto);
        boton.setMaxWidth(Double.MAX_VALUE);
        boton.getStyleClass().add("boton-menu");

        return boton;
    }

    // Pantalla principal de la aplicación.
    private void mostrarInicio() {

        Label titulo = new Label("Panel de gestión");
        titulo.getStyleClass().add("titulo-pagina");

        Label descripcion = new Label(
                "Selecciona el apartado que deseas gestionar.");

        descripcion.getStyleClass().add("descripcion-pagina");

        GridPane tarjetas = new GridPane();
        tarjetas.setHgap(20);
        tarjetas.setVgap(20);

        tarjetas.add(crearTarjeta(
                "Clientes",
                "Registro y consulta de clientes",
                this::mostrarClientes), 0, 0);

        tarjetas.add(crearTarjeta(
                "Hoteles",
                "Consulta y búsqueda de hoteles",
                this::mostrarHoteles), 1, 0);

        tarjetas.add(crearTarjeta(
                "Habitaciones",
                "Consulta de habitaciones y capacidad",
                this::mostrarHabitaciones), 0, 1);

        tarjetas.add(crearTarjeta(
                "Reservas",
                "Gestión de reservas hoteleras",
                this::mostrarReservas), 1, 1);

        VBox contenido = new VBox(
                20, titulo, descripcion, tarjetas);

        contenido.setPadding(new Insets(35));
        contenido.getStyleClass().add("contenido-principal");

        setCenter(contenido);
    }

    // Creación de las tarjetas de acceso a las secciones.
    private VBox crearTarjeta(String titulo, String descripcion,
                              Runnable accion) {

        Label nombre = new Label(titulo);
        nombre.getStyleClass().add("titulo-tarjeta");

        Label detalle = new Label(descripcion);
        detalle.setWrapText(true);
        detalle.getStyleClass().add("descripcion-tarjeta");

        VBox tarjeta = new VBox(12, nombre, detalle);

        tarjeta.setAlignment(Pos.CENTER_LEFT);
        tarjeta.setPadding(new Insets(25));
        tarjeta.setPrefSize(300, 150);
        tarjeta.getStyleClass().add("tarjeta");

        tarjeta.setStyle("-fx-cursor: hand;");
        tarjeta.setOnMouseClicked(evento -> accion.run());

        return tarjeta;
    }

    // Acceso a la pantalla de clientes.
    private void mostrarClientes() {

        setCenter(new ClienteGUI(controlador));
    }

    // Acceso a la pantalla de hoteles.
    private void mostrarHoteles() {

        setCenter(new HotelGUI(controlador));
    }

    // Acceso a la pantalla de habitaciones.
    private void mostrarHabitaciones() {

        mostrarSeccion("Gestión de habitaciones",
                "Consulta y filtrado de habitaciones.");
    }

    // Acceso a la pantalla de reservas.
    private void mostrarReservas() {

        mostrarSeccion("Gestión de reservas",
                "Creación, consulta y cancelación de reservas.");
    }

    // Estructura provisional de las secciones.
    private void mostrarSeccion(String nombre, String detalle) {

        Label titulo = new Label(nombre);
        titulo.getStyleClass().add("titulo-pagina");

        Label descripcion = new Label(detalle);
        descripcion.getStyleClass().add("descripcion-pagina");

        Button volver = new Button("Volver al inicio");
        volver.getStyleClass().add("boton-secundario");
        volver.setOnAction(evento -> mostrarInicio());

        VBox contenido = new VBox(
                20, titulo, descripcion, volver);

        contenido.setPadding(new Insets(35));
        contenido.getStyleClass().add("contenido-principal");

        setCenter(contenido);
    }
}