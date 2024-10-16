package es.ujaen.dae.clubSocios.entidades;

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
    @NotNull
    private EstadoSolicitud estadoSolicitud;
    @PastOrPresent
    private LocalDate fechaSolicitud;

    private String generarSolicitudId() {
        return this.socioId + "-" + System.currentTimeMillis();
    }

    public Solicitud(String socioId, Socio socio, int numAcompanantes, EstadoSolicitud estadoSolicitud) {
        this.solicitudId = generarSolicitudId();
        this.socioId = socioId;
        this.socio = socio;
        this.numAcompanantes = numAcompanantes;
        this.estadoSolicitud = estadoSolicitud;
        this.fechaSolicitud = LocalDate.now();
    }

    public void modificarNumAcompanantes(int nuevoNumAcompanantes) {
        if (nuevoNumAcompanantes < 0 || nuevoNumAcompanantes >= 5)
            throw new IllegalArgumentException("El número de acompañantes debe ser mayor que 0 y menor que 5");

        this.numAcompanantes = nuevoNumAcompanantes;
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
