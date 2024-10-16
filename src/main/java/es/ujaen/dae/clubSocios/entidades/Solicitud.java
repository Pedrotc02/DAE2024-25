package es.ujaen.dae.clubSocios.entidades;

import es.ujaen.dae.clubSocios.enums.EstadoSolicitud;
import jakarta.validation.constraints.*;

public class Solicitud {
    @NotBlank
    @Email
    private String socioId;
    @NotNull
    private Socio socio;
    @Min(0) @Max(5)
    private int numAcompanantes;
    @NotNull
    private EstadoSolicitud estadoSolicitud;

    public Solicitud(String socioId, Socio socio, int numAcompanantes, EstadoSolicitud estadoSolicitud) {
        this.socioId = socioId;
        this.socio = socio;
        this.numAcompanantes = numAcompanantes;
        this.estadoSolicitud = estadoSolicitud;
    }

    public void modificarNumAcompanantes(int nuevoNumAcompanantes) {
        if (nuevoNumAcompanantes < 0 || nuevoNumAcompanantes > 5)
            throw new IllegalArgumentException("El número de acompañantes debe ser mayor que 0 y menor que 5");

        this.numAcompanantes = nuevoNumAcompanantes;
    }

    public void eliminarAcompanante() {
        if (numAcompanantes > 0) {
            numAcompanantes--;
        }
    }

    public void cancelar() {
        this.estadoSolicitud = EstadoSolicitud.CANCELADA;
    }

    public boolean estaCompleta() {
        return this.estadoSolicitud == EstadoSolicitud.CONFIRMADA;
    }

    // Getters
    public String getSocioId() {
        return socioId;
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
}
