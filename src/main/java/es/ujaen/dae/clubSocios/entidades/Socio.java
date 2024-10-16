package es.ujaen.dae.clubSocios.entidades;

import es.ujaen.dae.clubSocios.enums.EstadoCuota;

import es.ujaen.dae.clubSocios.enums.EstadoSolicitud;
import jakarta.validation.constraints.*;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;


public class Socio {

    @NotBlank
    @Email
    private String id; // email
    @NotBlank
    private String nombre;
    @NotBlank
    private String apellidos;
    @NotBlank
    @Pattern(regexp = "^\\d{8}[TRWAGMYFPDXBNJZSQVHLCKE]$", message = "DNI no válido")
    private String dni;
    @NotBlank
    @Pattern(regexp = "^(\\+34|0034|34)?[6789]\\d{8}$", message = "No es un número de teléfono válido")
    private String tlf;
    @NotBlank
    @Pattern(regexp = "^[a-zA-Z0-9]{6,}$", message = "La clave debe tener más de 5 caracteres")
    private String claveAcceso;
    @NotNull
    private EstadoCuota estadoCuota;

    public Socio(String email, String nombre, String apellidos, String dni, String tlf, String claveAcceso,
                 EstadoCuota estadoCuota) {
        this.id = email;
        this.nombre = nombre;
        this.apellidos = apellidos;
        this.dni = dni;
        this.tlf = tlf;
        this.claveAcceso = claveAcceso;
        this.estadoCuota = estadoCuota;
    }

    public Solicitud solicitarInscripcion(Actividad actividad, @PositiveOrZero int numAcompanantes) {
        actividad.asignarPlazas(numAcompanantes + 1);
        return new Solicitud(this.id, this, numAcompanantes, EstadoSolicitud.PENDIENTE);
    }

    public void modificarSolicitud(Solicitud solicitud) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void borrarSolicitud(Solicitud solicitud) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    // Getters
    public String getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public String getApellidos() {
        return apellidos;
    }

    public String getEmail() {
        return id;
    } // redundante

    public String getDni() {
        return dni;
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

    // Setters

    public void setEstadoCuota(EstadoCuota estadoCuota) {
        this.estadoCuota = estadoCuota;
    }
}
