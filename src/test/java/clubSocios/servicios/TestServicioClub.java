package clubSocios.servicios;

import es.ujaen.dae.clubSocios.entidades.Actividad;
import es.ujaen.dae.clubSocios.entidades.Socio;
import es.ujaen.dae.clubSocios.entidades.Solicitud;
import es.ujaen.dae.clubSocios.enums.EstadoActividad;
import es.ujaen.dae.clubSocios.enums.EstadoCuota;
import es.ujaen.dae.clubSocios.excepciones.ActividadYaRegistrada;
import es.ujaen.dae.clubSocios.excepciones.SocioYaRegistrado;
import es.ujaen.dae.clubSocios.servicios.ServicioClub;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.constraints.Email;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.springframework.test.util.AssertionErrors.assertEquals;

@SpringBootTest(classes = es.ujaen.dae.clubSocios.app.Main.class)
public class TestServicioClub {
    @Autowired
    ServicioClub servicio;

    @Test
    @DirtiesContext
    void testNuevoSocioInvalido(){
        //Email incorrecto,dni incorrecto
        var socio = new Socio("11111111", "Pedro", "Apellido1 Apellido2", "pruebagmail.com", "123456789", "contraseña", EstadoCuota.PAGADA);

        assertThatThrownBy(() -> servicio.crearSocio(socio)).isInstanceOf(ConstraintViolationException.class);

        //Socio repetido
        var socio2 = new Socio("11111111M", "Pedro", "Apellido1 Apellido2", "prueba@gmail.com", "690123456", "123456", EstadoCuota.PAGADA);
        servicio.crearSocio(socio2);
        assertThatThrownBy(() -> servicio.crearSocio(socio2)).isInstanceOf(SocioYaRegistrado.class);

    }

    @Test
    @DirtiesContext
    void testLoginSocio() {
        var socio = new Socio("11111111M", "Pedro", "Apellido1 Apellido2", "prueba@gmail.com", "690123456", "123456", EstadoCuota.PAGADA);
        servicio.crearSocio(socio);

        Optional<Socio> re = servicio.login("prueba@gmail.com", "123456");
        System.out.println(re);

        assertThat(servicio.login("error@gmail.com", "prueba")).isEmpty();
        assertThat(servicio.login("prueba@gmail.com", "claveError")).isEmpty();
        assertThat(servicio.login("prueba@gmail.com", "123456")).hasValueSatisfying(s -> s.getEmail().equals(socio.getEmail()));
    }

    @Test
    @DirtiesContext
    void testActualizaEstado() {
        var socio = new Socio("11111111M", "Pedro", "Apellido1 Apellido2", "prueba@gmail.com", "690123456", "123456", EstadoCuota.PENDIENTE);
        servicio.crearSocio(socio);

        servicio.actualizarEstadoCuota(socio.getEmail(), EstadoCuota.PAGADA);
        assertEquals("EL estado debe ser PAGADA", EstadoCuota.PAGADA, socio.getEstadoCuota());
    }

    @Test
    @DirtiesContext
    void testNuevaActividad(){
        //Precio incorrecto, plazas incorrectas, violaciones fecha
        var actividad = new Actividad("act1", "Visita a museo", "Descricion", -10, -15, LocalDate.of(2023,10,13), LocalDate.of(2023,9,13), LocalDate.of(2023,10,10));

        assertThatThrownBy(() -> servicio.crearActividad(actividad)).isInstanceOf(ConstraintViolationException.class);

        //Actividad repetida
        var actividad2 = new Actividad("act1", "Visita a museo", "Descricion", 15, 30, LocalDate.of(2024,10,13), LocalDate.of(2024,10,13), LocalDate.of(2024,10,15));
        servicio.crearActividad(actividad2);
        assertThatThrownBy(() -> servicio.crearActividad(actividad2)).isInstanceOf(ActividadYaRegistrada.class);

    }

    @Test
    @DirtiesContext
    void testResetearEstadoCuota(){
        var socio1 = new Socio("11111111M", "Pedro", "Apellido1 Apellido2", "prueba@gmail.com", "690123456", "123456", EstadoCuota.PAGADA);
        var socio2 = new Socio("22222222M", "Edu", "Apellido1 Apellido2", "edu@gmail.com", "690123456", "123456", EstadoCuota.PAGADA);

        servicio.crearSocio(socio1);
        servicio.crearSocio(socio2);

        servicio.resetearEstadoCuota();

        assertEquals("El estado debe estar en Pendiente", EstadoCuota.PENDIENTE, socio1.getEstadoCuota());
        assertEquals("El estado debe estar en Pendiente", EstadoCuota.PENDIENTE, socio2.getEstadoCuota());

    }


}

