package es.ujaen.dae.clubSocios.entidades;

import es.ujaen.dae.clubSocios.enums.EstadoSolicitud;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class Solicitud {
    @NotBlank
    String socioId;
    @NotBlank
    Socio socio;
    @NotBlank
    @Min(1)
    int numAcompanantes;
    @NotBlank
    EstadoSolicitud estadoSolicitud;

    public Solicitud(String socioId, Socio socio, int numAcompanantes, EstadoSolicitud estadoSolicitud) {
        this.socioId = socioId;
        this.socio = socio;
        this.numAcompanantes = numAcompanantes;
        this.estadoSolicitud = estadoSolicitud;
    }

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