package es.ujaen.dae.clubSocios.entidades;

import es.ujaen.dae.clubSocios.enums.EstadoCuota;

import es.ujaen.dae.clubSocios.enums.EstadoSolicitud;
import es.ujaen.dae.clubSocios.excepciones.ActividadYaRegistrada;
import es.ujaen.dae.clubSocios.excepciones.FueraDePlazo;
import jakarta.validation.constraints.*;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


public class Socio {

    @NotBlank
    @Email
    private String socioId;
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
    List<Solicitud> solicitudes = new ArrayList<>();

    public Socio(String socioId, String nombre, String apellidos, String dni, String tlf, String claveAcceso,
                 EstadoCuota estadoCuota) {
        this.socioId = socioId;
        this.nombre = nombre;
        this.apellidos = apellidos;
        this.dni = dni;
        this.tlf = tlf;
        this.claveAcceso = claveAcceso;
        this.estadoCuota = estadoCuota;
    }

    public void solicitarInscripcion(Actividad actividad, @PositiveOrZero int numAcompanantes) {
        if (!actividad.estaEnPeriodoInscripcion()) {
            throw new FueraDePlazo("Inscripción fuera de plazo ");
        }
        //Si el socio ya ha hecho una solicitud en la actividad dada
        for (var solicitud: actividad.getSolicitudes()) {
            if (solicitud.getSocioId().equals(this.socioId))
                throw new ActividadYaRegistrada();
        }

        int totalPlazas = 1 + numAcompanantes;
        EstadoSolicitud estado = evaluarEstadoSolicitud(actividad, totalPlazas);
        Solicitud nuevaSolicitud = new Solicitud(this.socioId, this, numAcompanantes, estado);
        solicitudes.add(nuevaSolicitud);
        actividad.agregarSolicitud(nuevaSolicitud);
    }

    private EstadoSolicitud evaluarEstadoSolicitud(Actividad actividad, int totalPlazas) {

        if (estadoCuota.equals(EstadoCuota.PAGADA)) {
            return totalPlazas > 1 ? EstadoSolicitud.PARCIAL : EstadoSolicitud.CERRADA;
        }
        return EstadoSolicitud.PENDIENTE;
    }

    public void modificarSolicitud(String solicitudId, int numAcompanantes) {
        boolean flag = false;
        for (Solicitud solicitud : solicitudes) {
            if (solicitud.getSolicitudId().equals(solicitudId)) {
                solicitud.modificarNumAcompanantes(numAcompanantes);
                flag = true;
                break;
            }
        }

        if (!flag) {
            throw new IllegalArgumentException("No se encontró una solicitud con el ID proporcionado.");
        }
    }

    public void borrarSolicitud(String solicitudId) {
        boolean flag = false;
        Iterator<Solicitud> iterator = solicitudes.iterator();
        while (iterator.hasNext()) {
            Solicitud solicitud = iterator.next();
            if (solicitud.getSolicitudId().equals(solicitudId)) {
                iterator.remove();
                flag = true;
                break;
            }
        }

        if (!flag) {
            throw new IllegalArgumentException("No se encontró una solicitud con el ID proporcionado.");
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
