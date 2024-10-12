package es.ujaen.dae.clubSocios.entidades;

import es.ujaen.dae.clubSocios.enums.EstadoSolicitud;
import jakarta.validation.constraints.*;

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

    public Solicitud(String solicitudId, String socioId, Socio socio, int numAcompanantes, EstadoSolicitud estadoSolicitud) {
        this.solicitudId = solicitudId;
        this.socioId = socioId;
        this.socio = socio;
        this.numAcompanantes = numAcompanantes;
        this.estadoSolicitud = estadoSolicitud;
    }

    public void modificarNumAcompanantes(int nuevoNumAcompanantes) {
        if (nuevoNumAcompanantes < 0 || nuevoNumAcompanantes > 5)
            throw new IllegalArgumentException("El número de acompañantes debe estar entre 0 y 5");

        this.numAcompanantes = nuevoNumAcompanantes;
    }

    public void eliminarAcompanante() {
        if (numAcompanantes > 0) {
            numAcompanantes--;
        }
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

    public EstadoSolicitud getEstadoSolicitud() {
        return estadoSolicitud;
    }

    //Setters

    public void setNumAcompanantes(int numAcompanantes) {
        this.numAcompanantes = numAcompanantes;
    }

    public void setEstadoSolicitud(EstadoSolicitud estadoSolicitud) {
        this.estadoSolicitud = estadoSolicitud;
    }
}
