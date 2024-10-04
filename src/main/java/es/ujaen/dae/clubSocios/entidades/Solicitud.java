package es.ujaen.dae.clubSocios.entidades;

import es.ujaen.dae.clubSocios.enums.EstadoSolicitud;
import jakarta.validation.constraints.*;

public class Solicitud {
    @NotBlank
    String socioId;
    @NotBlank
    Socio socio;
    @NotBlank
    @Min(0) @Max(5)
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
