package es.ujaen.dae.clubSocios.entidades;

import es.ujaen.dae.clubSocios.enums.EstadoCuota;
import jakarta.validation.*;

import java.time.LocalDate;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.util.AssertionErrors.assertEquals;

import org.junit.jupiter.api.Test;
import org.springframework.test.annotation.DirtiesContext;

public class TestSocio {

    @Test
    @DirtiesContext
    void testValidacionSocio() {
        var socio1 = new Socio("pepfer@gmail.com", "Pepito", "Fernández", "12345678A", "645367898", "pepfer", EstadoCuota.PENDIENTE);

        Validator validator = Validation.buildDefaultValidatorFactory().getValidator();
        Set<ConstraintViolation<Socio>> violations = validator.validate(socio1);

        assertThat(violations).isEmpty();

        var socio2 = new Socio("prueba@gmail.com", "", "Fernández", "2345678A", "64536714", "pepfer", EstadoCuota.PENDIENTE);

        violations = validator.validate(socio2);

        assertThat(violations).isNotEmpty();
    }

    @Test
    @DirtiesContext
    void testModificarSolicitud() {
        var socio1 = new Socio("pepfer@gmail.com", "Pepito", "Fernández", "12345678A", "645367898", "pepfer", EstadoCuota.PENDIENTE);
        var actividad = new Actividad("Clases de informática", "Aqui se dara clases de informática", 25, 30, LocalDate.parse("2024-12-25"), LocalDate.parse("2024-10-12"), LocalDate.parse("2024-12-21"));

        var solicitudIns = actividad.solicitarInscripcion(socio1, 1, false);
        actividad.getSolicitudes().get(0).modificarNumAcompanantes(2);

        assertEquals("El numero de acompañantes debe ser 2", 2, actividad.getSolicitudes().get(0).getNumAcompanantes());
    }

}
