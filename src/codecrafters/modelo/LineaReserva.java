
package codecrafters.modelo;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "linea_reserva")
@IdClass(LineaReservaId.class)
public class LineaReserva {

    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_reserva", nullable = false)
    private Reserva reserva;

    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_habitacion", nullable = false)
    private Habitacion habitacion;

    @Column(name = "precio_noche", nullable = false)
    private double precioNoche;

    // Constructor obligatorio para JPA
    protected LineaReserva() {
    }

    // Constructor original
    public LineaReserva(
            Habitacion habitacion,
            double precioNoche,
            Reserva reserva) {

        this.habitacion = habitacion;
        this.precioNoche = precioNoche;
        this.reserva = reserva;

        if (reserva != null
                && reserva.getLineasReserva() != null
                && !reserva.getLineasReserva().contains(this)) {

            reserva.getLineasReserva().add(this);
        }
    }

    public double getPrecioNoche() {
        return precioNoche;
    }

    public void setPrecioNoche(double precioNoche) {
        this.precioNoche = precioNoche;
    }

    public Habitacion getHabitacion() {
        return habitacion;
    }

    public void setHabitacion(Habitacion habitacion) {
        this.habitacion = habitacion;
    }

    public Reserva getReserva() {
        return reserva;
    }

    public void setReserva(Reserva reserva) {
        this.reserva = reserva;
    }

    @Override
    public String toString() {
        return "LineaReserva{" +
                "precioNoche=" + precioNoche +
                ", reserva=" +
                (reserva != null
                    ? reserva.getIdReserva() : "N/A") +
                ", habitacion=" +
                (habitacion != null
                    ? habitacion.toString() : "N/A") +
                '}';
    }
}
