package es.ujaen.dae.clubSocios.entidades;

import es.ujaen.dae.clubSocios.enums.EstadoCuota;
import es.ujaen.dae.clubSocios.enums.EstadoSolicitud;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.Test;

import org.springframework.test.annotation.DirtiesContext;
import static org.assertj.core.api.Assertions.assertThat;
import java.time.LocalDate;
import java.util.Set;

public class TestSolicitud {

    @Test
    @DirtiesContext
    void testValidacionSolicitud() {
        var socio1 = new Socio("12345678A", "Pepito", "Fernández", "pepfer@gamil.com", "653398283", "pepifer", EstadoCuota.PENDIENTE);
        var solicitud1 = new Solicitud("solicitud1", socio1.getSocioId(), socio1, 4, EstadoSolicitud.PENDIENTE);

        Validator validator = Validation.buildDefaultValidatorFactory().getValidator();
        Set<ConstraintViolation<Solicitud>> violations = validator.validate(solicitud1);

        assertThat(violations).isEmpty();

        var solicitud2 = new Solicitud("solicitud2", socio1.getSocioId(), socio1, 6, EstadoSolicitud.PENDIENTE);

        violations = validator.validate(solicitud2);

        assertThat(violations).isNotEmpty();
    }

    @Test
    @DirtiesContext
    void testNumeroAcompanantesValidos() {
        var socio1 = new Socio("12345678A", "Pepito", "Fernández", "pepfer@gamil.com", "653398283", "pepifer", EstadoCuota.PENDIENTE);
        var actividad = new Actividad("act1", "Clases de Flamenco", "Aqui se dara clases de flamenco",35, 30, LocalDate.parse("2024-10-12"), LocalDate.parse("2024-10-06"), LocalDate.parse("2024-10-11"));
        var solicitud = new Solicitud("solicitud-1", "12345678A", socio1, 4, EstadoSolicitud.PENDIENTE);

        assertThat(solicitud.getNumAcompanantes()).isLessThanOrEqualTo(5);
    }
}
