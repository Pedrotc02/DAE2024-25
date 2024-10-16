package es.ujaen.dae.clubSocios.entidades;

import es.ujaen.dae.clubSocios.enums.EstadoActividad;
import es.ujaen.dae.clubSocios.enums.EstadoCuota;
import es.ujaen.dae.clubSocios.enums.EstadoSolicitud;
import es.ujaen.dae.clubSocios.excepciones.InscripcionFueraDePlazoException;
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

    @Test
    @DirtiesContext
    void testAgregarSolicitudCuandoLaActividadEstaAbierta() {
        var actividad = new Actividad("1", "Clases de flamenco", "Aqui se dara clases de flamenco",35, 4, LocalDate.parse("2024-10-16"), LocalDate.parse("2024-10-12"), LocalDate.parse("2024-10-15"));
        var socio1 = new Socio("11111111M", "Pedro", "Apellido1 Apellido2", "prueba@gmail.com", "690123456", "123456", EstadoCuota.PAGADA);
        Solicitud solicitud = new Solicitud(socio1.getSocioId(), socio1, 3, EstadoSolicitud.PENDIENTE);
        assertDoesNotThrow(() -> actividad.agregarSolicitud(solicitud));
        assertEquals("La solicitud se ha tenido que agregar a la actividad", 1, actividad.getSolicitudes().size());
    }

    @Test
    @DirtiesContext
    void testAgregarSolicitudCuandoLaActividadNoEstaAbierta() {
        var actividad = new Actividad("1", "Clases de flamenco", "Aqui se dara clases de flamenco",35, 4, LocalDate.parse("2024-10-16"), LocalDate.parse("2024-10-12"), LocalDate.parse("2024-10-15"));
        var socio1 = new Socio("11111111M", "Pedro", "Apellido1 Apellido2", "prueba@gmail.com", "690123456", "123456", EstadoCuota.PAGADA);
        Solicitud solicitud = new Solicitud(socio1.getSocioId(), socio1, 3, EstadoSolicitud.PENDIENTE);
        actividad.setEstado(EstadoActividad.CERRADA);

        // Verificar que se lanza la excepción adecuada
        var exception = assertThrows(
                InscripcionFueraDePlazoException.class,
                () -> actividad.agregarSolicitud(solicitud)
        );

    }

    @Test
    @DirtiesContext
    void testRevisarSolicitudes() {
        var actividad = new Actividad("1", "Clases de flamenco", "Aqui se dara clases de flamenco",35, 4, LocalDate.parse("2024-10-16"), LocalDate.parse("2024-10-12"), LocalDate.parse("2024-10-15"));
        var socio1 = new Socio("11111111M", "Pedro", "Apellido1 Apellido2", "prueba@gmail.com", "690123456", "123456", EstadoCuota.PAGADA);
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

//    @Test
//    @DirtiesContext
//    void testAsignarPlazasFinInscripcion(){
//        var actividad = new Actividad("1", "Clases de flamenco", "Aqui se dara clases de flamenco",35, 4, LocalDate.parse("2024-10-16"), LocalDate.parse("2024-10-12"), LocalDate.parse("2024-10-15"));
//        var socio1 = new Socio("11111111M", "Pedro", "Apellido1 Apellido2", "prueba@gmail.com", "690123456", "123456", EstadoCuota.PAGADA);
//        var socio2 = new Socio("11111111M", "Pedro", "Apellido1 Apellido2", "prueba@gmail.com", "690123456", "123456", EstadoCuota.PAGADA);
//
//        // Escenario 1: Hay plazas disponibles y solicitudes parciales
//        actividad.setPlazasDisponibles(5); // 5 plazas disponibles
//
//        Solicitud solicitud1 = new Solicitud(EstadoSolicitud.PARCIAL, 1); // 1 acompañante
//        Solicitud solicitud2 = new Solicitud(EstadoSolicitud.PARCIAL, 2); // 2 acompañantes
//        Solicitud solicitud3 = new Solicitud(EstadoSolicitud.PARCIAL, 3); // 3 acompañantes
//
//        actividad.agregarSolicitud(solicitud1);
//        actividad.agregarSolicitud(solicitud2);
//        actividad.agregarSolicitud(solicitud3);
//
//        // Ejecutar la asignación de plazas
//        actividad.asignarPlazasFinInscripcion();
//
//        // Verificar el estado de las solicitudes y plazas restantes
//        assertEquals(EstadoSolicitud.CERRADA, solicitud1.getEstadoSolicitud()); // Asignada
//        assertEquals(EstadoSolicitud.CERRADA, solicitud2.getEstadoSolicitud()); // Asignada
//        assertEquals(EstadoSolicitud.PARCIAL, solicitud3.getEstadoSolicitud()); // No asignada
//        assertEquals(2, actividad.getPlazasDisponibles()); // Quedan 2 plazas
//
//        // Escenario 2: Ahora agregamos solicitudes en estado PENDIENTE
//        Solicitud solicitud4 = new Solicitud(EstadoSolicitud.PENDIENTE, 1); // 1 acompañante
//        Solicitud solicitud5 = new Solicitud(EstadoSolicitud.PENDIENTE, 2); // 2 acompañantes
//
//        actividad.agregarSolicitud(solicitud4);
//        actividad.agregarSolicitud(solicitud5);
//
//        // Ejecutar la asignación de plazas de nuevo
//        actividad.asignarPlazasFinInscripcion();
//
//        // Verificar el estado de las nuevas solicitudes y plazas restantes
//        assertEquals(EstadoSolicitud.CERRADA, solicitud4.getEstadoSolicitud()); // Asignada
//        assertEquals(EstadoSolicitud.PENDIENTE, solicitud5.getEstadoSolicitud()); // No asignada
//        assertEquals(0, actividad.getPlazasDisponibles()); // Quedan 0 plazas
//
//        // Escenario 3: No hay plazas disponibles
//        actividad.setPlazasDisponibles(0); // Cambiar a 0 plazas disponibles
//
//        Solicitud solicitud6 = new Solicitud(EstadoSolicitud.PARCIAL, 1); // 1 acompañante
//        Solicitud solicitud7 = new Solicitud(EstadoSolicitud.PENDIENTE, 2); // 2 acompañantes
//
//        actividad.agregarSolicitud(solicitud6);
//        actividad.agregarSolicitud(solicitud7);
//
//        // Ejecutar la asignación de plazas de nuevo
//        actividad.asignarPlazasFinInscripcion();
//
//        // Verificar que no se han cerrado las solicitudes
//        assertEquals(EstadoSolicitud.PARCIAL, solicitud6.getEstadoSolicitud()); // No asignada
//        assertEquals(EstadoSolicitud.PENDIENTE, solicitud7.getEstadoSolicitud()); // No asignada
//    }

    @Test
    @DirtiesContext
    void testAsignarPlazas() {
        var actividad = new Actividad("1", "Clases de flamenco", "Aqui se dara clases de flamenco",35, 4, LocalDate.parse("2024-10-16"), LocalDate.parse("2024-10-12"), LocalDate.parse("2024-10-15"));

        actividad.asignarPlazas(3);

        assertEquals("El numero de plazas disponibles debe ser 1", 1, actividad.getPlazasDisponibles());
    }

    @Test
    @DirtiesContext
    void testAsignarPlazasError() {
        var actividad = new Actividad("1", "Clases de flamenco", "Aqui se dara clases de flamenco",35, 4, LocalDate.parse("2024-10-16"), LocalDate.parse("2024-10-12"), LocalDate.parse("2024-10-15"));

        PlazasNoDisponibles exception = assertThrows(PlazasNoDisponibles.class, () -> {
            actividad.asignarPlazas(5);
        });
    }

    @Test
    @DirtiesContext
    void testEstaEnPeriodoInscripcion() {
        var actividad1 = new Actividad("1", "Clases de flamenco", "Aqui se dara clases de flamenco",35, 4, LocalDate.parse("2024-10-16"), LocalDate.parse("2024-10-12"), LocalDate.parse("2024-10-30"));
        var actividad2 = new Actividad("1", "Clases de flamenco", "Aqui se dara clases de flamenco",35, 4, LocalDate.parse("2024-10-16"), LocalDate.parse("2024-10-12"), LocalDate.parse("2024-10-14"));

        boolean resultado1 = actividad1.estaEnPeriodoInscripcion();
        boolean resultado2 = actividad2.estaEnPeriodoInscripcion();

        assertTrue("Debe devolver verdad", resultado1);
        assertFalse("Debe devolver falso", resultado2);

    }

}
