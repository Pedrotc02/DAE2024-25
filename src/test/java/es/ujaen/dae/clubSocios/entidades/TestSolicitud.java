package es.ujaen.dae.clubSocios.entidades;

import es.ujaen.dae.clubSocios.enums.EstadoCuota;
import org.junit.jupiter.api.Test;

import org.springframework.test.annotation.DirtiesContext;
import static org.assertj.core.api.Assertions.assertThat;
import java.time.LocalDate;
import java.util.List;

public class TestSolicitud {

    @Test
    @DirtiesContext
    public void testSolicitudValida() {

        //Equivale a Actividad actividad, Usuario usuario...
        var actividad = new Actividad("1", "Clases de Flamenco", "Aqui se dara clases de flamenco",35, 30, LocalDate.parse("2024-10-12"), LocalDate.parse("2024-10-06"), LocalDate.parse("2024-10-11"));
        var socio1 = new Socio("1", "Pepito", "Fernández", "pepfer@gamil.com", "653398283", "holamundo", EstadoCuota.PENDIENTE);

        var solicitud1 = socio1.solicitarInscripcion(actividad, 4);

        assertThat(solicitud1.getNumAcompanantes()).isLessThanOrEqualTo(5);

        var solicitud2 = socio1.solicitarInscripcion(actividad, 6);
        assertThat(solicitud2.getNumAcompanantes()).isLessThanOrEqualTo(5);
    }
}
