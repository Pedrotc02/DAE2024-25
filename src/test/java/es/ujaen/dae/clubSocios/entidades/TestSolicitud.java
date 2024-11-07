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
        var actividad3 = new Actividad("1", "Clases de informática", "Aqui se dara clases de informática",25, 30, LocalDate.parse("2024-12-25"), LocalDate.parse("2024-10-12"), LocalDate.parse("2024-12-21"));
        var solicitud1 = new Solicitud(socio1.getSocioId(), socio1, 4);

        Validator validator = Validation.buildDefaultValidatorFactory().getValidator();
        Set<ConstraintViolation<Solicitud>> violations = validator.validate(solicitud1);

        assertThat(violations).isEmpty();

        var solicitud2 = new Solicitud(socio1.getSocioId(), socio1, 6);

        violations = validator.validate(solicitud2);

        assertThat(violations).isNotEmpty();
    }

    @Test
    @DirtiesContext
    void testNumeroAcompanantesValidos() {
        var socio1 = new Socio("12345678A", "Pepito", "Fernández", "pepfer@gamil.com", "653398283", "pepifer", EstadoCuota.PENDIENTE);
        var actividad = new Actividad("1", "Clases de informática", "Aqui se dara clases de informática",25, 30, LocalDate.parse("2024-12-25"), LocalDate.parse("2024-10-12"), LocalDate.parse("2024-12-21"));
        var solicitud = new Solicitud("12345678A", socio1, 4);

        assertThat(solicitud.getNumAcompanantes()).isLessThanOrEqualTo(5);
    }

    @Test
    @DirtiesContext
    void testConcesionPlazas() {
        var socio1 = new Socio("12345678A", "Pepito", "Fernández", "pepfer@gamil.com", "653398283", "pepifer", EstadoCuota.PENDIENTE);
        var actividad = new Actividad("1", "Clases de informática", "Aqui se dara clases de informática",25, 30, LocalDate.parse("2024-12-25"), LocalDate.parse("2024-10-12"), LocalDate.parse("2024-12-21"));

        //Como no ha pagado, no se le da de manera instantánea, si no que al final del período de inscripción la dirección decidirá
        actividad.solicitarInscripcion(socio1, 4);
        assertThat(actividad.getSolicitudes().get(0).getPlazasConcedidas()).isEqualTo(0);

        //Como ha pagado, se le da de manera instantánea la plaza
        var socio2 = new Socio("11111111A", "Pepito", "Fernández", "pepfer@gamil.com", "653398283", "pepifer", EstadoCuota.PAGADA);
        actividad.solicitarInscripcion(socio2, 4);

        assertThat(actividad.getSolicitudes().get(1).getPlazasConcedidas()).isEqualTo(1);
    }
}
