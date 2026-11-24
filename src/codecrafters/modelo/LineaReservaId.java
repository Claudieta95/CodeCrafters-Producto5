package codecrafters.modelo;

import java.io.Serializable;
import java.util.Objects;

public class LineaReservaId implements Serializable {

    private static final long serialVersionUID = 1L;

    private int reserva;
    private int habitacion;

    public LineaReservaId() {
    }

    public LineaReservaId(int reserva, int habitacion) {
        this.reserva = reserva;
        this.habitacion = habitacion;
    }

    @Override
    public boolean equals(Object obj) {

        if (this == obj) {
            return true;
        }

        if (!(obj instanceof LineaReservaId other)) {
            return false;
        }

        return reserva == other.reserva
                && habitacion == other.habitacion;
    }

    @Override
    public int hashCode() {
        return Objects.hash(reserva, habitacion);
    }
}
