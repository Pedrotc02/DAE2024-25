package clubSocios.servicios;

import es.ujaen.dae.clubSocios.entidades.Actividad;
import es.ujaen.dae.clubSocios.entidades.Socio;
import es.ujaen.dae.clubSocios.enums.EstadoCuota;
import es.ujaen.dae.clubSocios.excepciones.ActividadYaRegistrada;
import es.ujaen.dae.clubSocios.excepciones.OperacionDeDireccion;
import es.ujaen.dae.clubSocios.excepciones.SocioYaRegistrado;
import es.ujaen.dae.clubSocios.servicios.ServicioClub;
import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;

import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.springframework.test.util.AssertionErrors.assertEquals;

@SpringBootTest(classes = es.ujaen.dae.clubSocios.app.Main.class)
public class TestServicioClub {
    @Autowired
    ServicioClub servicio;

    /**
     * Test passed
     */
    @Test
    @DirtiesContext
    void testOperacionDireccion() {
        var direccion = servicio.login("direccion@clubsocios.es", "serviceSecret").get();

        var socio1 = new Socio("prueba@gmail.com", "Pedro", "Apellido1 Apellido2", "11111111M", "690123456", "123456", EstadoCuota.PAGADA);
        var socio2 = new Socio("edu@gmail.com", "Edu", "Apellido1 Apellido2", "22222222M", "690123456", "123456", EstadoCuota.PAGADA);

        assertThatThrownBy(() -> servicio.crearSocio(socio1, socio2)).isInstanceOf(OperacionDeDireccion.class);

        var actividad = new Actividad("act1", "Visita a museo", "Descricion", 15, 30, LocalDate.parse("2024-12-25"), LocalDate.parse("2024-10-12"), LocalDate.parse("2024-12-21"));

        assertThatThrownBy(() -> servicio.crearActividad(socio1, actividad)).isInstanceOf(OperacionDeDireccion.class);

    }

    /**
     * Test passed
     */
    @Test
    @DirtiesContext
    void testNuevoSocio(){
        var direccion = servicio.login("direccion@clubsocios.es", "serviceSecret").get();

        //Email incorrecto,dni incorrecto
        var socio = new Socio("pruebagmail.com", "Pedro", "Apellido1 Apellido2", "11111111M", "123456789", "contraseña", EstadoCuota.PAGADA);

        assertThatThrownBy(() -> servicio.crearSocio(direccion, socio)).isInstanceOf(ConstraintViolationException.class);

        //Socio repetido
        var socio2 = new Socio("prueba@gmail.com", "Pedro", "Apellido1 Apellido2", "11111111M", "690123456", "123456", EstadoCuota.PAGADA);
        servicio.crearSocio(direccion, socio2);

        //Intenta crear el socio otra vez
        assertThatThrownBy(() -> servicio.crearSocio(direccion, socio2)).isInstanceOf(SocioYaRegistrado.class);

        var socio3 = new Socio("tomas@gmail.com", "Tomás", "A1 A2", "11111111M", "690123456", "123456", EstadoCuota.PAGADA);
        servicio.crearSocio(direccion, socio3);

        assertThat(servicio.socios().size()).isEqualTo(2);
        assertThat(servicio.socios().containsKey(socio3.getSocioId())).isTrue();
    }

    /**
     * Crea una nueva actividad válida y la añade a las actividades del servicio
     * Test passed
     */
    @Test
    @DirtiesContext
    void testNuevaActividad(){
        var direccion = servicio.login("direccion@clubsocios.es", "serviceSecret").get();

        //Precio incorrecto, plazas incorrectas, violaciones fecha
        var actividad = new Actividad("act1", "Visita a museo", "Descricion", -10, -15, LocalDate.parse("2024-12-25"), LocalDate.parse("2024-10-12"), LocalDate.parse("2024-12-21"));

        assertThatThrownBy(() -> servicio.crearActividad(direccion, actividad)).isInstanceOf(ConstraintViolationException.class);

        //Actividad repetida
        var actividad2 = new Actividad("act1", "Visita a museo", "Descricion", 15, 30, LocalDate.parse("2024-12-25"), LocalDate.parse("2024-10-12"), LocalDate.parse("2024-12-21"));
        servicio.crearActividad(direccion, actividad2);

        assertThat(servicio.actividades()).size().isEqualTo(1);
    }

    /**
     * Registra un socio a una actividad
     * Test passed
     */
    @Test
    @DirtiesContext
    void testRegistrarSolicitud() {
        var direccion = servicio.login("direccion@clubsocios.es", "serviceSecret").get();
        var socio1 = new Socio("prueba@gmail.com", "Pedro", "Apellido1 Apellido2", "11111111M", "690123456", "123456", EstadoCuota.PAGADA);
        var actividad = new Actividad("act1", "Visita a museo", "Descricion", 15, 30, LocalDate.parse("2024-12-25"), LocalDate.parse("2024-10-12"), LocalDate.parse("2024-12-21"));

        servicio.crearActividad(direccion, actividad);
        servicio.crearSocio(direccion, socio1);
        servicio.registrarSolicitud(direccion, socio1, actividad.getId(), 4);

        assertThat(servicio.actividades().get(actividad.getId())
                .getSolicitudes().stream()
                .filter(s -> s.getSocioId().equals(socio1.getSocioId())))
                .size()
                .isEqualTo(1);
    }

    /**
     * Test passed
     */
    @Test
    @DirtiesContext
    void testLoginSocio() {
        var direccion = servicio.login("direccion@clubsocios.es", "serviceSecret").get();

        var socio = new Socio("prueba@gmail.com", "Pedro", "Apellido1 Apellido2", "11111111M", "690123456", "123456", EstadoCuota.PAGADA);

        servicio.crearSocio(direccion, socio);

        Optional<Socio> re = servicio.login("prueba@gmail.com", "123456");
        System.out.println(re);

        assertThat(servicio.login("error@gmail.com", "prueba")).isEmpty();
        assertThat(servicio.login("prueba@gmail.com", "claveError")).isEmpty();
        assertThat(servicio.login("prueba@gmail.com", "123456")).hasValueSatisfying(s -> s.getSocioId().equals(socio.getSocioId()));
    }

    /**
     * Test passed
     */
    @Test
    @DirtiesContext
    void testActualizaEstado() {
        var direccion = servicio.login("direccion@clubsocios.es", "serviceSecret").get();

        var socio = new Socio("prueba@gmail.com", "Pedro", "Apellido1 Apellido2", "11111111M", "690123456", "123456", EstadoCuota.PENDIENTE);
        servicio.crearSocio(direccion, socio);

        servicio.actualizarEstadoCuota(socio.getSocioId(), EstadoCuota.PAGADA);
        assertEquals("EL estado debe ser PAGADA", EstadoCuota.PAGADA, socio.getEstadoCuota());
    }

    /**
     * Test passed
     */
    @Test
    @DirtiesContext
    void testResetearEstadoCuota(){
        var direccion = servicio.login("direccion@clubsocios.es", "serviceSecret").get();

        var socio1 = new Socio("prueba@gmail.com", "Pedro", "Apellido1 Apellido2", "11111111M", "690123456", "123456", EstadoCuota.PAGADA);
        var socio2 = new Socio("edu@gmail.com", "Edu", "Apellido1 Apellido2", "22222222M", "690123456", "123456", EstadoCuota.PAGADA);

        servicio.crearSocio(direccion, socio1);
        servicio.crearSocio(direccion, socio2);

        servicio.resetearEstadoCuota();

        assertEquals("El estado debe estar en Pendiente", EstadoCuota.PENDIENTE, socio1.getEstadoCuota());
        assertEquals("El estado debe estar en Pendiente", EstadoCuota.PENDIENTE, socio2.getEstadoCuota());

    }
}

