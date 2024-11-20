package es.ujaen.dae.clubSocios.entidades;

import es.ujaen.dae.clubSocios.enums.EstadoActividad;
import es.ujaen.dae.clubSocios.enums.EstadoCuota;
import es.ujaen.dae.clubSocios.enums.EstadoSolicitud;
import es.ujaen.dae.clubSocios.excepciones.*;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.Test;
import org.springframework.test.annotation.DirtiesContext;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.springframework.test.util.AssertionErrors.*;


public class TestActividad {

    /**
     * Test passed
     */
    @Test
    @DirtiesContext
    void testValidacionActividad() {
        var actividad1 = new Actividad("Clases de flamenco", "Aqui se dara clases de flamenco", 35, 30, LocalDate.parse("2024-11-16"), LocalDate.parse("2024-10-12"), LocalDate.parse("2024-10-30"));

        Validator validator = Validation.buildDefaultValidatorFactory().getValidator();
        Set<ConstraintViolation<Actividad>> violations = validator.validate(actividad1);

        assertThat(violations).isEmpty();

        var actividad2 = new Actividad("Clases de flamenco", "Aqui se dara clases de flamenco", 35, -2, LocalDate.parse("2024-11-16"), LocalDate.parse("2024-10-12"), LocalDate.parse("2024-10-15"));

        violations = validator.validate(actividad2);
        assertThat(violations).isNotEmpty();
    }

    /**
     * Comprueba que las fechas de las actividades son válidas y de la temporada actual.
     * Test passed
     */
    @Test
    @DirtiesContext
    void testFechasActividadValidas() {

        assertThatThrownBy(() -> new Actividad("Clases de flamenco", "Aqui se dara clases de flamenco",
                35, 30, LocalDate.parse("2025-11-16"), LocalDate.parse("2025-10-12"),
                LocalDate.parse("2025-10-30")))
                .isInstanceOf(InvalidoAnio.class);

        assertThatThrownBy(() -> new Actividad("Clases de flamenco", "Aqui se dara clases de flamenco",
                35, 30, LocalDate.parse("2024-10-10"), LocalDate.parse("2024-10-12"),
                LocalDate.parse("2024-10-11")))
                .isExactlyInstanceOf(FechaNoValida.class);

        var actividad = new Actividad("Clases de flamenco", "Aqui se dara clases de flamenco",
                35, 30, LocalDate.parse("2024-12-25"), LocalDate.parse("2024-10-12"),
                LocalDate.parse("2024-12-21"));

        assertThat(actividad.estado()).isEqualTo(EstadoActividad.ABIERTA);
    }

    @Test
    @DirtiesContext
    void testSolicitarInscripcion() {
        var actividad = new Actividad("Clases de flamenco", "Aqui se dara clases de flamenco", 35, 4, LocalDate.parse("2025-12-25"), LocalDate.parse("2024-10-12"), LocalDate.parse("2024-12-21"));
        var socio1 = new Socio("prueba@gmail.com", "Pedro", "Apellido1 Apellido2", "11111111M", "690123456", "123456", EstadoCuota.PAGADA);
        var socio2 = new Socio("tomas@gmail.com", "Tomás", "A1 A2", "11111111M", "690123456", "123456", EstadoCuota.PAGADA);

        actividad.solicitarInscripcion(socio1, 3);
        actividad.solicitarInscripcion(socio2, 3);

        assertEquals("Debe haber 2 solicitudes en la actividad", 2, actividad.getSolicitudes().size());
        assertThatThrownBy(() -> actividad.solicitarInscripcion(socio1, 4)).isInstanceOf(SolicitudYaRealizada.class);
    }

    /**
     * Comprueba que un socio pueda solicitar correctamente la inscripción a la actividad que él quiera.
     * Si no puede, es informado del error correspondiente.
     * Además comprueba que la solicitud se ha añadido tanto a la lista de solicitudes
     * del socio solicitante como a la lista de solicitudes de la actividad solicitada.
     * Test passed.
     */
    @Test
    @DirtiesContext
    void testSolicitudInscripcionValida() {

        var socio1 = new Socio("pepfer@gmail.com", "Pepito", "Fernández", "12345678A", "645367898", "pepfer", EstadoCuota.PENDIENTE);

        var actividad1 = new Actividad("Clases de flamenco", "Aqui se dara clases de flamenco", 35, 4, LocalDate.parse("2024-10-30"), LocalDate.parse("2024-10-12"), LocalDate.parse("2024-10-16"));

        assertThatThrownBy(
                () -> actividad1.solicitarInscripcion(socio1, 4)
        ).isExactlyInstanceOf(FueraDePlazo.class);

        var actividad2 = new Actividad("Clases de flamenco", "Aqui se dara clases de flamenco", 35, 0, LocalDate.parse("2024-12-25"), LocalDate.parse("2024-10-12"), LocalDate.parse("2024-12-21"));

        assertThatThrownBy(() ->
                actividad2.solicitarInscripcion(socio1, 4)
        ).isInstanceOf(NoHayPlazas.class);

        var actividad3 = new Actividad("Clases de informática", "Aqui se dara clases de informática", 25, 30, LocalDate.parse("2024-12-25"), LocalDate.parse("2024-10-12"), LocalDate.parse("2024-12-21"));

        assertDoesNotThrow(() ->
                actividad3.solicitarInscripcion(socio1, 4)
        );

        assertThatThrownBy(() ->
                actividad3.solicitarInscripcion(socio1, 2)
        ).isInstanceOf(SolicitudYaRealizada.class);


        //Todavía no ha acabado el período de inscripción, por lo que no puede revisar nadie las solicitudes de la actividad
        assertThatThrownBy(actividad3::revisarSolicitudes).isInstanceOf(FechaNoValida.class);
        assertThat(actividad3.getSolicitudes()).size().isEqualTo(1);
    }

    @Test
    @DirtiesContext
    void testRevisarSolicitudes() {

        var actividad = new Actividad("Clases de flamenco", "Aqui se dara clases de flamenco", 35, 4, LocalDate.parse("2025-10-30"), LocalDate.parse("2024-10-12"), LocalDate.parse("2024-10-16"));
        var socio1 = new Socio("prueba@gmail.com", "Pedro", "Apellido1 Apellido2", "11111111M", "690123456", "123456", EstadoCuota.PAGADA);
        var socio2 = new Socio("tomas@gmail.com", "Tomás", "A1 A2", "11111111M", "690123456", "123456", EstadoCuota.PAGADA);

        var solicitud1 = new Solicitud(socio1, 3);
        var solicitud2 = new Solicitud(socio2, 3);

        actividad.agregarSolicitud(solicitud1);
        actividad.agregarSolicitud(solicitud2);

        assertEquals("Debe haber 2 solicitudes en la actividad", 2, actividad.revisarSolicitudes().size());
    }
}
