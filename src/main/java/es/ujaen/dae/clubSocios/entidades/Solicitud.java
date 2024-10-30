package es.ujaen.dae.clubSocios.entidades;

import es.ujaen.dae.clubSocios.enums.EstadoCuota;
import es.ujaen.dae.clubSocios.enums.EstadoSolicitud;
import jakarta.validation.constraints.*;
import org.springframework.cglib.core.Local;

import java.time.LocalDate;

public class Solicitud {
    @NotBlank
    private String solicitudId;
    @NotBlank
    private String socioId;
    @NotNull
    private Socio socio;
    @Min(0) @Max(5)
    private int numAcompanantes;

    //Una solicitud está asociada a una actividad y en ningún momento la metíamos
    private Actividad actividad;
    @NotNull
    private EstadoSolicitud estadoSolicitud;
    @PastOrPresent
    private LocalDate fechaSolicitud;

    @Min(0) @Max(6)
    private int plazasConcedidas;

    public Solicitud(String socioId, Socio socio, int numAcompanantes) {
        this.socioId = socioId;
        this.socio = socio;
        this.numAcompanantes = numAcompanantes;
        //El id de la solicitud se crea en función del id del socio, el id de actividad y la fecha en la que se realiza
        this.solicitudId = generarSolicitudId();
        //Todas las solicitudes se crean con un estado pendiente
        this.estadoSolicitud = EstadoSolicitud.PENDIENTE;
        //La solicitud se crea a fecha actual del sistema
        this.fechaSolicitud = LocalDate.now();
        //Al crear la solicitud de por sí no se concede ninguna plaza, de eso se encarga la propia dirección
        this.plazasConcedidas = 0;
    }

    private String generarSolicitudId() {
        return this.socioId + "-" +"Act"+actividad.getId()+"-"+ System.currentTimeMillis();
    }

    public void modificarNumAcompanantes(int nuevoNumAcompanantes) {
        if (nuevoNumAcompanantes < 0 || nuevoNumAcompanantes >= 5)
            throw new IllegalArgumentException("El número de acompañantes debe ser mayor que 0 y menor que 5");

        this.numAcompanantes = nuevoNumAcompanantes;
    }

    public void evaluarEstado(int totalPlazas) {
        if (!socio.getEstadoCuota().equals(EstadoCuota.PAGADA) || actividad.getPlazasDisponibles() == 0) {
            this.estadoSolicitud = EstadoSolicitud.PENDIENTE;
        }

        this.estadoSolicitud = totalPlazas > 1 ? EstadoSolicitud.PARCIAL : EstadoSolicitud.CERRADA;
    }

    // Getters
    public String getSocioId() {
        return socioId;
    }

    public String getSolicitudId() {
        return solicitudId;
    }

    public Socio getSocio() {
        return socio;
    }

    public int getNumAcompanantes() {
        return numAcompanantes;
    }

    public LocalDate getFechaSolicitud() {
        return fechaSolicitud;
    }

    public void setFechaSolicitud(LocalDate fechaSolicitud) {
        this.fechaSolicitud = fechaSolicitud;
    }

    public EstadoSolicitud getEstadoSolicitud() {
        return estadoSolicitud;
    }

    public void setEstadoSolicitud(EstadoSolicitud estadoSolicitud) {
        this.estadoSolicitud = estadoSolicitud;
    }
}
