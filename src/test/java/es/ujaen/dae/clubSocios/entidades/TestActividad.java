package es.ujaen.dae.clubSocios.entidades;

import es.ujaen.dae.clubSocios.excepciones.PlazasNoDisponibles;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.Test;
import org.springframework.test.annotation.DirtiesContext;

import java.time.LocalDate;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;


public class TestActividad {

    @Test
    @DirtiesContext
    void testValidacionActividad() {
        var actividad1 = new Actividad("1", "Clases de flamenco", "Aqui se dara clases de flamenco",35, 30, LocalDate.parse("2024-10-16"), LocalDate.parse("2024-10-12"), LocalDate.parse("2024-10-15"));

        Validator validator = Validation.buildDefaultValidatorFactory().getValidator();
        Set<ConstraintViolation<Actividad>> violations = validator.validate(actividad1);

        assertThat(violations).isEmpty();

        var actividad2 = new Actividad("1", "Clases de flamenco", "Aqui se dara clases de flamenco",35, 30, LocalDate.parse("2023-10-16"), LocalDate.parse("2023-10-12"), LocalDate.parse("2023-10-15"));

        violations = validator.validate(actividad2);
        assertThat(violations).isNotEmpty();
    }

    @Test
    @DirtiesContext
    void testPlazasValidas() {

        var actividad1 = new Actividad("1", "Clases de flamenco", "Aqui se dara clases de flamenco",35, 4, LocalDate.parse("2024-10-16"), LocalDate.parse("2024-10-12"), LocalDate.parse("2024-10-15"));

        assertThatThrownBy( () -> {
            actividad1.asignarPlazas(5);
        }).isInstanceOf(PlazasNoDisponibles.class);

        var actividad2 = new Actividad("1", "Clases de informática", "Aqui se dara clases de informática",25, 30, LocalDate.parse("2024-10-16"), LocalDate.parse("2024-10-12"), LocalDate.parse("2024-10-15"));
        assertDoesNotThrow( () -> {
            actividad2.asignarPlazas(5);
        });
    }

    @Test
    @DirtiesContext
    void testFechasValidas() {
        var fechaIniIns = LocalDate.parse("2024-10-10");
        var fechaFinIns = LocalDate.parse("2024-10-15");
        var fechaCel = LocalDate.parse("2024-10-16");

        var actividad = new Actividad("1", "Clases de flamenco", "Aqui se dara clases de flamenco",35, 30, fechaCel, fechaIniIns, fechaFinIns);

        assertThat(actividad.estaEnPeriodoInscripcion()).isTrue();
    }
}
