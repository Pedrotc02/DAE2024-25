package es.ujaen.dae.clubSocios.entidades;

import es.ujaen.dae.clubSocios.enums.EstadoActividad;
import es.ujaen.dae.clubSocios.enums.EstadoCuota;

import es.ujaen.dae.clubSocios.enums.EstadoSolicitud;
import jakarta.validation.constraints.*;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

import java.util.ArrayList;
import java.util.List;


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

    List<Solicitud> solicitudes;

    public Socio(String socioId, String nombre, String apellidos, String email, String tlf, String claveAcceso,
                 EstadoCuota estadoCuota) {
        this.socioId = socioId;
        this.nombre = nombre;
        this.apellidos = apellidos;
        this.email = email;
        this.tlf = tlf;
        this.claveAcceso = claveAcceso;
        this.estadoCuota = estadoCuota;
        this.solicitudes = new ArrayList<>();
    }


    public Solicitud solicitarInscripcion(Actividad actividad, @PositiveOrZero int numAcompanantes) {

        EstadoSolicitud estadoSolicitud = generarEstadoSolicitud(actividad, numAcompanantes);
        String solicitudId = generarSolicitudId();

        return new Solicitud(solicitudId, this.socioId, this, numAcompanantes, estadoSolicitud);
    }

    private String generarSolicitudId() {
        // TODO: mejorar esto
        return this.socioId + "-" + System.currentTimeMillis();
    }

    private EstadoSolicitud generarEstadoSolicitud(Actividad actividad, int numAcompanantes) {
        if (actividad.getEstado() == EstadoActividad.ABIERTA) {
            // La actividad esta en periodo de inscripcion

            if (actividad.getPlazasDisponibles() >= numAcompanantes + 1) {
                // Hay suficiente sitio para el socio y sus acompañantes
                return EstadoSolicitud.CONFIRMADA;
            } else if ((actividad.getPlazasDisponibles() >= 1)) {
                // Hay sitio para el socio pero no para todos los acompañantes
                return EstadoSolicitud.PENDIENTE;
            } else {
                // No quedan plazas
                return EstadoSolicitud.RECHAZADA;
            }
        } else {
            // Fuera del periodo de inscripcion
            return EstadoSolicitud.INVALIDA;
        }
    }

    public void modificarSolicitud(String solicitudId, int numAcompanantes) {
        for (int i = 0; i < solicitudes.size(); i++) {
            solicitudes.get(i).setNumAcompanantes(numAcompanantes);
        }
    }

    public void borrarSolicitud(String solicitudId) {
        for (int i = solicitudes.size() - 1; i >= 0; i--) {
            if (solicitudes.get(i).getSolicitudId().equals(solicitudId)) {
                solicitudes.remove(i);
            }
        }
    }

    public void anadirSolicitud(Solicitud solicitud) {
        solicitudes.add(solicitud);
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

    // Setters

    public void setEstadoCuota(EstadoCuota estadoCuota) {
        this.estadoCuota = estadoCuota;
    }
}
