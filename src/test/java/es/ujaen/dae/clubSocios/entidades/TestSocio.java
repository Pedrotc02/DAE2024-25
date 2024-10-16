package es.ujaen.dae.clubSocios.entidades;

import es.ujaen.dae.clubSocios.enums.EstadoCuota;
import es.ujaen.dae.clubSocios.enums.EstadoSolicitud;
import jakarta.validation.*;

import java.util.Set;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.util.AssertionErrors.assertEquals;
import static org.springframework.test.util.AssertionErrors.assertFalse;

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
    void testModificarSolicitud(){
        var socio1 = new Socio("pepfer@gmail.com", "Pepito", "Fernández", "12345678A", "645367898", "pepfer", EstadoCuota.PENDIENTE);
        var solicitud1 = new Solicitud( socio1.getSocioId(), socio1, 4, EstadoSolicitud.PENDIENTE);

        socio1.anadirSolicitud(solicitud1);

        socio1.modificarSolicitud(solicitud1.getSolicitudId(), 2);

        assertEquals("El numero de acompañantes debe ser 2", 2, solicitud1.getNumAcompanantes());
    }

    @Test
    @DirtiesContext
    void testEliminarSolicitud(){
        var socio1 = new Socio("pepfer@gmail.com", "Pepito", "Fernández", "12345678A", "645367898", "pepfer", EstadoCuota.PENDIENTE);
        var solicitud1 = new Solicitud( socio1.getSocioId(), socio1, 4, EstadoSolicitud.PENDIENTE);
        var solicitud2 = new Solicitud( socio1.getSocioId(), socio1, 4, EstadoSolicitud.PENDIENTE);

        socio1.anadirSolicitud(solicitud1);
        socio1.anadirSolicitud(solicitud2);

        socio1.borrarSolicitud(solicitud2.getSolicitudId());

        assertEquals("El numero de solicitudes debe ser 1", 1, socio1.solicitudes.size());
        assertFalse("La solicitud 2 no se deberia encontrar", socio1.solicitudes.stream().anyMatch(s -> s.getSolicitudId().equals("solicitud2")));
    }
}
