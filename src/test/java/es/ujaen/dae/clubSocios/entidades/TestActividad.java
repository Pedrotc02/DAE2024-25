package es.ujaen.dae.clubSocios.entidades;

import es.ujaen.dae.clubSocios.enums.EstadoActividad;
import es.ujaen.dae.clubSocios.enums.EstadoCuota;
import es.ujaen.dae.clubSocios.enums.EstadoSolicitud;
import es.ujaen.dae.clubSocios.excepciones.FueraDePlazo;
import es.ujaen.dae.clubSocios.excepciones.PlazasNoDisponibles;
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

    @Test
    @DirtiesContext
    void testValidacionActividad() {
        var actividad1 = new Actividad("1", "Clases de flamenco", "Aqui se dara clases de flamenco",35, 30, LocalDate.parse("2025-11-16"), LocalDate.parse("2025-10-12"), LocalDate.parse("2025-10-30"));

        Validator validator = Validation.buildDefaultValidatorFactory().getValidator();
        Set<ConstraintViolation<Actividad>> violations = validator.validate(actividad1);

        assertThat(violations).isEmpty();

        var actividad2 = new Actividad("2", "Clases de flamenco", "Aqui se dara clases de flamenco",35, 30, LocalDate.parse("2023-10-16"), LocalDate.parse("2023-10-12"), LocalDate.parse("2023-10-15"));

        violations = validator.validate(actividad2);
        assertThat(violations).isNotEmpty();
    }

    @Test
    @DirtiesContext
    void testPlazasValidas() {

        var actividad1 = new Actividad("1", "Clases de flamenco", "Aqui se dara clases de flamenco",35, 4, LocalDate.parse("2025-11-16"), LocalDate.parse("2025-10-12"), LocalDate.parse("2025-10-30"));

        assertThatThrownBy( () -> {
            actividad1.asignarPlazas(5);
        }).isInstanceOf(PlazasNoDisponibles.class);

        var actividad2 = new Actividad("1", "Clases de informática", "Aqui se dara clases de informática",25, 30, LocalDate.parse("2025-11-16"), LocalDate.parse("2025-10-12"), LocalDate.parse("2025-10-30"));
        assertDoesNotThrow( () -> {
            actividad2.asignarPlazas(5);
        });
    }

    @Test
    @DirtiesContext
    void testFechasValidas() {
        var actividad = new Actividad("1", "Clases de flamenco", "Aqui se dara clases de flamenco",35, 30, LocalDate.parse("2025-11-16"), LocalDate.parse("2024-10-12"), LocalDate.parse("2025-10-30"));

        assertThat(actividad.estaEnPeriodoInscripcion()).isTrue();
    }

    @Test
    @DirtiesContext
    void testAgregarSolicitudCuandoLaActividadEstaAbierta() {
        var actividad = new Actividad("1", "Clases de flamenco", "Aqui se dara clases de flamenco",35, 4, LocalDate.parse("2025-11-16"), LocalDate.parse("2025-10-12"), LocalDate.parse("2025-10-30"));
        var socio1 = new Socio("prueba@gmail.com", "Pedro", "Apellido1 Apellido2", "11111111M", "690123456", "123456", EstadoCuota.PAGADA);
        Solicitud solicitud = new Solicitud(socio1.getSocioId(), socio1, 3, EstadoSolicitud.PENDIENTE);
        assertDoesNotThrow(() -> actividad.agregarSolicitud(solicitud));
        assertEquals("La solicitud se ha tenido que agregar a la actividad", 1, actividad.getSolicitudes().size());
    }

    @Test
    @DirtiesContext
    void testAgregarSolicitudCuandoLaActividadNoEstaAbierta() {
        var actividad = new Actividad("1", "Clases de flamenco", "Aqui se dara clases de flamenco",35, 4, LocalDate.parse("2025-11-16"), LocalDate.parse("2025-10-12"), LocalDate.parse("2025-10-30"));
        var socio1 = new Socio("prueba@gmail.com", "Pedro", "Apellido1 Apellido2", "11111111M", "690123456", "123456", EstadoCuota.PAGADA);
        Solicitud solicitud = new Solicitud(socio1.getSocioId(), socio1, 3, EstadoSolicitud.PENDIENTE);
        actividad.setEstado(EstadoActividad.CERRADA);

        // Verificar que se lanza la excepción adecuada
        var exception = assertThrows(
                FueraDePlazo.class,
                () -> actividad.agregarSolicitud(solicitud)
        );

    }

    @Test
    @DirtiesContext
    void testRevisarSolicitudes() {
        var actividad = new Actividad("1", "Clases de flamenco", "Aqui se dara clases de flamenco",35, 4, LocalDate.parse("2025-10-30"), LocalDate.parse("2024-10-12"), LocalDate.parse("2024-10-15"));
        var socio1 = new Socio("prueba@gmail.com", "Pedro", "Apellido1 Apellido2", "11111111M", "690123456", "123456", EstadoCuota.PAGADA);
        Solicitud solicitud1 = new Solicitud(socio1.getSocioId(), socio1, 3, EstadoSolicitud.PENDIENTE);
        Solicitud solicitud2 = new Solicitud(socio1.getSocioId(), socio1, 3, EstadoSolicitud.PENDIENTE);

        actividad.agregarSolicitud(solicitud1);
        actividad.agregarSolicitud(solicitud2);

        List<Solicitud> solicitudesRevisadas = actividad.revisarSolicitudes();

        // Verificar que las solicitudes están ordenadas por fecha de solicitud
        assertEquals("El tamaño de las solicitudes revisadas debe ser dos", 2, solicitudesRevisadas.size());
        assertEquals("solicitud1 debería ser la primera", solicitudesRevisadas.get(0), solicitud1);
        assertEquals("solicitud2 debería ser la segunda", solicitudesRevisadas.get(1), solicitud2);
    }

    @Test
    @DirtiesContext
    void testAsignarPlazasFinInscripcion(){
        var actividad = new Actividad("1", "Clases de flamenco", "Aqui se dara clases de flamenco",35, 6, LocalDate.parse("2024-10-16"), LocalDate.parse("2024-10-12"), LocalDate.parse("2024-10-15"));
        var socio1 = new Socio("prueba@gmail.com", "Pedro", "Apellido1 Apellido2", "11111111M", "690123456", "123456", EstadoCuota.PAGADA);
        var socio2 = new Socio("prueba2@gmail.com", "Pedro", "Apellido1 Apellido2", "22222222M", "690123456", "123456", EstadoCuota.PAGADA);

        // Escenario 1: Hay plazas disponibles y solicitudes parciales

        Solicitud solicitud1 = new Solicitud(socio1.getSocioId(), socio1, 4, EstadoSolicitud.PARCIAL);
        Solicitud solicitud2 = new Solicitud(socio2.getSocioId(), socio2, 3, EstadoSolicitud.PARCIAL);

        actividad.agregarSolicitud(solicitud1);
        actividad.agregarSolicitud(solicitud2);

        actividad.asignarPlazasFinInscripcion();

        // Verificar el estado de las solicitudes y plazas restantes
        assertEquals("La solicitud debe estar cerrada", EstadoSolicitud.CERRADA, solicitud1.getEstadoSolicitud()); // Asignada
        assertEquals("La solicitud no se ha podido cerrar, se queda en parcial", EstadoSolicitud.PARCIAL, solicitud2.getEstadoSolicitud()); // No Asignada
        assertEquals("Debe quedar 1 plaza",1, actividad.getPlazasDisponibles()); // Quedan 1 plaza

        // Escenario 2: Ahora agregamos solicitudes en estado PENDIENTE
        Solicitud solicitud3 = new Solicitud(socio1.getSocioId(), socio1, 0, EstadoSolicitud.PENDIENTE);

        actividad.agregarSolicitud(solicitud3);

        actividad.asignarPlazasFinInscripcion();

        assertEquals("Se debe cerrar la solicitud", EstadoSolicitud.CERRADA, solicitud3.getEstadoSolicitud()); // Asignada
        assertEquals("No quedan mas plazas", 0, actividad.getPlazasDisponibles()); // Quedan 0 plazas
    }

    @Test
    @DirtiesContext
    void testAsignarPlazas() {
        var actividad = new Actividad("1", "Clases de flamenco", "Aqui se dara clases de flamenco",35, 4, LocalDate.parse("2025-11-16"), LocalDate.parse("2025-10-12"), LocalDate.parse("2025-10-30"));

        actividad.asignarPlazas(3);

        assertEquals("El numero de plazas disponibles debe ser 1", 1, actividad.getPlazasDisponibles());
    }

    @Test
    @DirtiesContext
    void testAsignarPlazasError() {
        var actividad = new Actividad("1", "Clases de flamenco", "Aqui se dara clases de flamenco",35, 4,LocalDate.parse("2025-11-16"), LocalDate.parse("2025-10-12"), LocalDate.parse("2025-10-30"));

        PlazasNoDisponibles exception = assertThrows(PlazasNoDisponibles.class, () -> {
            actividad.asignarPlazas(5);
        });
    }

    @Test
    @DirtiesContext
    void testEstaEnPeriodoInscripcion() {
        var actividad1 = new Actividad("1", "Clases de flamenco", "Aqui se dara clases de flamenco",35, 4, LocalDate.parse("2024-10-31"), LocalDate.parse("2024-10-12"), LocalDate.parse("2024-10-30"));
        var actividad2 = new Actividad("1", "Clases de flamenco", "Aqui se dara clases de flamenco",35, 4, LocalDate.parse("2024-10-16"), LocalDate.parse("2024-10-12"), LocalDate.parse("2024-10-14"));

        boolean resultado1 = actividad1.estaEnPeriodoInscripcion();
        boolean resultado2 = actividad2.estaEnPeriodoInscripcion();

        assertTrue("Debe devolver verdad", resultado1);
        assertFalse("Debe devolver falso", resultado2);

    }

}
