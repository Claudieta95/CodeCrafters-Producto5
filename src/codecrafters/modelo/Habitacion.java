
package codecrafters.modelo;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Id;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Column;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.FetchType;

@Entity
@Table(name = "habitacion")
public class Habitacion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_habitacion")
    private int idHabitacion;

    @Column(name = "numero", nullable = false)
    private int numero;

    @Column(name = "categoria", nullable = false)
    private String categoria;

    @Column(name = "capacidad", nullable = false)
    private int capacidad;

    @Column(name = "precio_noche", nullable = false)
    private double precioNoche;

    // Relación entre habitación y hotel
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_hotel", nullable = false)
    private Hotel hotel;

    // Constructor requerido por JPA
    protected Habitacion() {
    }

    // Constructor original
    public Habitacion(int idHabitacion, int numero,
                     String categoria, int capacidad,
                     double precioNoche) {

        this.idHabitacion = idHabitacion;
        this.numero = numero;
        this.categoria = categoria;
        this.capacidad = capacidad;
        this.precioNoche = precioNoche;
    }

    public int getIdHabitacion() {
        return idHabitacion;
    }

    public void setIdHabitacion(int idHabitacion) {
        this.idHabitacion = idHabitacion;
    }

    public int getNumero() {
        return numero;
    }

    public void setNumero(int numero) {
        this.numero = numero;
    }

    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    public int getCapacidad() {
        return capacidad;
    }

    public void setCapacidad(int capacidad) {
        this.capacidad = capacidad;
    }

    public double getPrecioNoche() {
        return precioNoche;
    }

    public void setPrecioNoche(double precioNoche) {
        this.precioNoche = precioNoche;
    }

    public Hotel getHotel() {
        return hotel;
    }

    public void setHotel(Hotel hotel) {
        this.hotel = hotel;
    }

    @Override
    public String toString() {
        return "Habitacion{" +
                "idHabitacion=" + idHabitacion +
                ", numero=" + numero +
                ", categoria='" + categoria + '\'' +
                ", capacidad=" + capacidad +
                ", precioNoche=" + precioNoche +
                '}';
    }
}
