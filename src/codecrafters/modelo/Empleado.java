package codecrafters.modelo;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.PrimaryKeyJoinColumn;
import jakarta.persistence.Table;

@Entity
@Table(name = "empleado")
@PrimaryKeyJoinColumn(name = "id_persona")
public class Empleado extends Persona {

    @Column(name = "puesto", nullable = false, length = 100)
    private String puesto;

    protected Empleado() {
        super();
    }

    public Empleado(String nombre, String apellido, String dni, String email, String telefono, String puesto) {
        super(nombre, apellido, dni, email, telefono);
        this.puesto = puesto;
    }

    public Empleado(int idPersona, String nombre, String apellido, String dni, String email, String telefono, String puesto) {
        this(nombre, apellido, dni, email, telefono, puesto);
        setIdPersona(idPersona);
    }

    public String getPuesto() {
        return puesto;
    }

    public void setPuesto(String puesto) {
        this.puesto = puesto;
    }

    @Override
    public String toString() {
        return "Empleado{" +
                "idPersona=" + getIdPersona() +
                ", nombre='" + getNombre() + '\'' +
                ", apellido='" + getApellido() + '\'' +
                ", dni='" + getDni() + '\'' +
                ", email='" + getEmail() + '\'' +
                ", telefono='" + getTelefono() + '\'' +
                ", puesto='" + getPuesto() + '\'' +
                '}';
    }
}
