package es.ujaen.dae.clubSocios.rest.dto;
import es.ujaen.dae.clubSocios.enums.EstadoSolicitud;
import java.time.LocalDateTime;

public record DTOSolicitud(
        String id,
        int numAcom,
        EstadoSolicitud estado,
        LocalDateTime fechaSoli,
        int plazasConcedidas) {
}
