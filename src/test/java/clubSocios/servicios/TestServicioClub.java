package clubSocios.servicios;

import es.ujaen.dae.clubSocios.entidades.Actividad;
import es.ujaen.dae.clubSocios.entidades.Socio;
import es.ujaen.dae.clubSocios.entidades.Temporada;
import es.ujaen.dae.clubSocios.enums.EstadoCuota;
import es.ujaen.dae.clubSocios.excepciones.ActividadYaRegistrada;
import es.ujaen.dae.clubSocios.excepciones.OperacionDeDireccion;
import es.ujaen.dae.clubSocios.excepciones.SocioYaRegistrado;
import es.ujaen.dae.clubSocios.excepciones.TemporadaNoEncontrada;
import es.ujaen.dae.clubSocios.servicios.ServicioClub;
import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;

import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.as;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.springframework.test.util.AssertionErrors.assertEquals;

@SpringBootTest(classes = es.ujaen.dae.clubSocios.app.Main.class)
public class TestServicioClub {
    @Autowired
    ServicioClub servicio;

    @Test
    @DirtiesContext
    void testNuevaTemporada() {
        var temporada = new Temporada(2024);
        servicio.crearTemporada(temporada);

        assertThat(servicio.temporadas().size()).isEqualTo(1);
        assertThat(servicio.actividades().size()).isEqualTo(0);
        assertThat(servicio.socios().size()).isEqualTo(0);
    }

    @Test
    @DirtiesContext
    void testBuscarTemporada() {
        var temporada = new Temporada();
        servicio.crearTemporada(temporada);


        Optional<Temporada> temporadaOptional = servicio.buscarTemporada(temporada.getTemporadaId());
        assertThat(temporadaOptional.isPresent()).isTrue();
        assertThat(temporadaOptional.get()).isEqualTo(temporada);
    }

    @Test
    @DirtiesContext
    void testOperacionDireccion() {
        var temporada = new Temporada(2024);
        var direccion = servicio.login("direccion@clubsocios.es", "serviceSecret").get();
        var socio1 = new Socio("prueba@gmail.com", "Pedro", "Apellido1 Apellido2", "11111111M", "690123456", "123456", EstadoCuota.PAGADA);
        var socio2 = new Socio("edu@gmail.com", "Edu", "Apellido1 Apellido2", "22222222M", "690123456", "123456", EstadoCuota.PAGADA);

        assertThatThrownBy(() -> servicio.crearSocio(socio1)).isInstanceOf(OperacionDeDireccion.class);

        var actividad = new Actividad("Visita a museo", "Descricion", 15, 30, LocalDate.parse("2024-12-25"), LocalDate.parse("2024-10-12"), LocalDate.parse("2024-12-21"));
        assertThatThrownBy(() -> servicio.crearActividad(temporada.getTemporadaId(), actividad)).isInstanceOf(OperacionDeDireccion.class);
    }

    @Test
    @DirtiesContext
    void testNuevoSocio() {
        // Verifica que el correo no tiene el formato correcto y lanza una ConstraintViolationException
        var socio = new Socio("pruebagmail.com", "Pedro", "Apellido1 Apellido2", "11111111M", "123456789", "contraseña", EstadoCuota.PAGADA);
        assertThatThrownBy(() -> servicio.crearSocio(socio)).isInstanceOf(ConstraintViolationException.class);

        // Crea un nuevo socio correctamente y verifica que se puede añadir al sistema
        var socio2 = new Socio("prueba@gmail.com", "Pedro", "Apellido1 Apellido2", "11111111M", "690123456", "123456", EstadoCuota.PAGADA);
        servicio.crearSocio(socio2);

        // Verifica que el intento de agregar el mismo socio lanza una excepción SocioYaRegistrado
        assertThatThrownBy(() -> servicio.crearSocio(socio2)).isInstanceOf(SocioYaRegistrado.class);

        var socio3 = new Socio("tomas@gmail.com", "Tomás", "A1 A2", "33333333M", "690123456", "123456", EstadoCuota.PAGADA);
        servicio.crearSocio(socio3);

        // Verifica que el sistema contiene ahora dos socios registrados
        assertThat(servicio.socios()).hasSize(2);
        assertThat(servicio.socios().stream().anyMatch(s -> s.getSocioId().equals(socio3.getSocioId()))).isTrue();
    }

    @Test
    @DirtiesContext
    void testNuevaActividad() {
        var direccion = servicio.login("direccion@clubsocios.es", "serviceSecret").get();
        var temporada = servicio.crearTemporada(new Temporada(2024));

        var actividad = new Actividad("Visita a museo", "Descricion", -10, -15, LocalDate.parse("2024-12-25"), LocalDate.parse("2024-10-12"), LocalDate.parse("2024-12-21"));
        assertThatThrownBy(() -> servicio.crearActividad(temporada.getTemporadaId(), actividad)).isInstanceOf(TemporadaNoEncontrada.class);

        var actividad2 = new Actividad("Visita a museo", "Descricion", 15, 30, LocalDate.parse("2024-12-25"), LocalDate.parse("2024-10-12"), LocalDate.parse("2024-12-21"));
        servicio.crearActividad(temporada.getTemporadaId(), actividad2);

        assertThat(servicio.actividades().size()).isEqualTo(1);
    }

    @Test
    @DirtiesContext
    void testRegistrarSolicitud() {
        var temporada = new Temporada(2024);
        var direccion = servicio.login("direccion@clubsocios.es", "serviceSecret").get();
        var socio1 = new Socio("prueba@gmail.com", "Pedro", "Apellido1 Apellido2", "11111111M", "690123456", "123456", EstadoCuota.PAGADA);
        var actividad = new Actividad("Visita a museo", "Descricion", 15, 30, LocalDate.parse("2024-12-25"), LocalDate.parse("2024-10-12"), LocalDate.parse("2024-12-21"));

        servicio.crearActividad(temporada.getTemporadaId(), actividad);
        servicio.crearSocio(socio1);
        servicio.registrarSolicitud(socio1, actividad.getId(), 4);

        assertThat(servicio.actividades().get(actividad.getId().intValue())
                .getSolicitudes().stream()
                .filter(s -> s.getSocioId().equals(socio1.getSocioId())))
                .size()
                .isEqualTo(1);
    }

    @Test
    @DirtiesContext
    void testLoginSocio() {
        var direccion = servicio.login("direccion@clubsocios.es", "serviceSecret").get();

        var socio = new Socio("prueba@gmail.com", "Pedro", "Apellido1 Apellido2", "11111111M", "690123456", "123456", EstadoCuota.PAGADA);
        servicio.crearSocio(socio);

        assertThat(servicio.login("error@gmail.com", "prueba")).isEmpty();
        assertThat(servicio.login("prueba@gmail.com", "claveError")).isEmpty();
        assertThat(servicio.login("prueba@gmail.com", "123456")).hasValueSatisfying(s -> s.getSocioId().equals(socio.getSocioId()));
    }

    @Test
    @DirtiesContext
    void testActualizaEstado() {
        var direccion = servicio.login("direccion@clubsocios.es", "serviceSecret").get();

        var socio = new Socio("prueba@gmail.com", "Pedro", "Apellido1 Apellido2", "11111111M", "690123456", "123456", EstadoCuota.PENDIENTE);
        servicio.crearSocio(socio);

        servicio.actualizarEstadoCuota(socio.getSocioId(), EstadoCuota.PAGADA);
        assertEquals("EL estado debe ser PAGADA", EstadoCuota.PAGADA, socio.getEstadoCuota());
    }

    @Test
    @DirtiesContext
    void testResetearEstadoCuota() {
        var direccion = servicio.login("direccion@clubsocios.es", "serviceSecret").get();

        var socio1 = new Socio("prueba@gmail.com", "Pedro", "Apellido1 Apellido2", "11111111M", "690123456", "123456", EstadoCuota.PAGADA);
        var socio2 = new Socio("edu@gmail.com", "Edu", "Apellido1 Apellido2", "22222222M", "690123456", "123456", EstadoCuota.PAGADA);

        servicio.crearSocio(socio1);
        servicio.crearSocio(socio2);

        // servicio.reiniciarEstadoCuotas();

        servicio.socios().forEach(s -> assertEquals("Cuota debe estar pendiente", EstadoCuota.PENDIENTE, s.getEstadoCuota()));
    }
}