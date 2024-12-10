package es.ujaen.dae.clubSocios.rest.dto;

import java.time.LocalDateTime;
import java.util.List;

public record DTOActividad(
        Long id,
        String titulo,
        String descripcion,
        double precio,
        int plazasDisp,
        int totalPlazas,
        LocalDateTime fechaInicioInscripcion,
        LocalDateTime fechaFinInscripcion,
        LocalDateTime fechaCelebracion) {
}
