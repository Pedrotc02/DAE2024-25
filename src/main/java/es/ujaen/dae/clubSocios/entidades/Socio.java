package es.ujaen.dae.clubSocios.entidades;

import es.ujaen.dae.clubSocios.enums.EstadoCuota;
import es.ujaen.dae.clubSocios.enums.EstadoSolicitud;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

public class Socio {
    @NotBlank
    @Pattern(regexp = "^\\d{8}[TRWAGMYFPDXBNJZSQVHLCKE]$", message = "DNI no válido")
    private String socioId;
    @NotBlank
    private String nombre;
    @NotBlank
    private String apellidos;
    @Email
    private String email;
    @NotBlank
    @Pattern(regexp = "^(\\+34|0034|34)?[6789]\\d{8}$", message = "No es un número de teléfono válido")
    private String tlf;
    @NotBlank
    @Pattern(regexp = "^[a-zA-Z0-9]{6,}$", message = "La clave debe tener más de 5 caracteres")
    private String claveAcceso;
    @NotNull
    private EstadoCuota estadoCuota;

    public Socio() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public Socio(String socioId, String nombre, String apellidos, String email, String tlf, String claveAcceso, EstadoCuota estadoCuota) {
        this.socioId = socioId;
        this.nombre = nombre;
        this.apellidos = apellidos;
        this.email = email;
        this.tlf = tlf;
        this.claveAcceso = claveAcceso;
        this.estadoCuota = estadoCuota;
    }

    public Solicitud solicitarInscripcion(Actividad actividad, int numAcompanantes) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void modificarSolicitud(Solicitud solicitud) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void borrarSolicitud(Solicitud solicitud) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    // Getters
    public String getSocioId() {
        return socioId;
    }

    public String getNombre() {
        return nombre;
    }

    public String getApellidos() {
        return apellidos;
    }

    public String getEmail() {
        return email;
    }

    public String getTlf() {
        return tlf;
    }

    public String getClaveAcceso() {
        return claveAcceso;
    }

    public EstadoCuota getEstadoCuota() {
        return estadoCuota;
    }
}
