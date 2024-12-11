package es.ujaen.dae.clubSocios.rest.dto;

import java.time.LocalDate;

public record DTOActividad(
        Long id,
        String titulo,
        String descripcion,
        double precio,
        int plazasDisp,
        int totalPlazas,
        LocalDate fechaInicioInscripcion,
        LocalDate fechaFinInscripcion,
        LocalDate fechaCelebracion) {
}
