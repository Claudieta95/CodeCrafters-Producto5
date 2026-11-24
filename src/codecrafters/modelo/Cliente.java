package codecrafters.modelo;

import jakarta.persistence.Entity;
import jakarta.persistence.PrimaryKeyJoinColumn;
import jakarta.persistence.Table;

@Entity
@Table(name = "cliente")
@PrimaryKeyJoinColumn(name = "id_persona")
public class Cliente extends Persona {

    protected Cliente() {
        super();
    }

    public Cliente(int idPersona, String nombre, String apellido, String dni, String email, String telefono) {
        super(nombre, apellido, dni, email, telefono);
        setIdPersona(idPersona);
    }

    @Override
    public String toString() {
        return "Cliente{" +
                "idPersona=" + getIdPersona() +
                ", nombre='" + getNombre() + '\'' +
                ", apellido='" + getApellido() + '\'' +
                ", dni='" + getDni() + '\'' +
                ", email='" + getEmail() + '\'' +
                ", telefono='" + getTelefono() + '\'' +
                '}';
    }
}
