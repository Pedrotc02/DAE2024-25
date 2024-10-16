package clubSocios.servicios;

import es.ujaen.dae.clubSocios.entidades.Actividad;
import es.ujaen.dae.clubSocios.entidades.Socio;
import es.ujaen.dae.clubSocios.entidades.Solicitud;
import es.ujaen.dae.clubSocios.enums.EstadoActividad;
import es.ujaen.dae.clubSocios.enums.EstadoCuota;
import es.ujaen.dae.clubSocios.excepciones.ActividadNoExistente;
import es.ujaen.dae.clubSocios.excepciones.ActividadYaRegistrada;
import es.ujaen.dae.clubSocios.excepciones.OperacionDeDireccion;
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
import static org.springframework.test.util.AssertionErrors.assertNotNull;

@SpringBootTest(classes = es.ujaen.dae.clubSocios.app.Main.class)
public class TestServicioClub {
    @Autowired
    ServicioClub servicio;

    @Test
    @DirtiesContext
    void testOperacionDireccion() {
        var direccion = servicio.login("direccion@clubsocios.es", "serviceSecret").get();

        var socio1 = new Socio("prueba@gmail.com", "Pedro", "Apellido1 Apellido2", "11111111M", "690123456", "123456", EstadoCuota.PAGADA);
        var socio2 = new Socio("edu@gmail.com", "Edu", "Apellido1 Apellido2", "22222222M", "690123456", "123456", EstadoCuota.PAGADA);

        assertThatThrownBy(() -> servicio.crearSocio(socio1, socio2)).isInstanceOf(OperacionDeDireccion.class);

        var actividad = new Actividad("act1", "Visita a museo", "Descricion", 15, 30, LocalDate.of(2024,10,18), LocalDate.of(2024,10,16), LocalDate.of(2024,10,17));

        assertThatThrownBy(() -> servicio.crearActividad(socio1, actividad)).isInstanceOf(OperacionDeDireccion.class);


    }
    @Test
    @DirtiesContext
    void testNuevoSocioInvalido(){
        var direccion = servicio.login("direccion@clubsocios.es", "serviceSecret").get();

        //Email incorrecto,dni incorrecto
        var socio = new Socio("pruebagmail.com", "Pedro", "Apellido1 Apellido2", "11111111M", "123456789", "contraseña", EstadoCuota.PAGADA);

        assertThatThrownBy(() -> servicio.crearSocio(direccion, socio)).isInstanceOf(ConstraintViolationException.class);

        //Socio repetido
        var socio2 = new Socio("prueba@gmail.com", "Pedro", "Apellido1 Apellido2", "11111111M", "690123456", "123456", EstadoCuota.PAGADA);
        servicio.crearSocio(direccion, socio2);
        assertThatThrownBy(() -> servicio.crearSocio(direccion, socio2)).isInstanceOf(SocioYaRegistrado.class);

    }

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

    @Test
    @DirtiesContext
    void testActualizaEstado() {
        var direccion = servicio.login("direccion@clubsocios.es", "serviceSecret").get();

        var socio = new Socio("prueba@gmail.com", "Pedro", "Apellido1 Apellido2", "11111111M", "690123456", "123456", EstadoCuota.PENDIENTE);
        servicio.crearSocio(direccion, socio);

        servicio.actualizarEstadoCuota(socio.getSocioId(), EstadoCuota.PAGADA);
        assertEquals("EL estado debe ser PAGADA", EstadoCuota.PAGADA, socio.getEstadoCuota());
    }

    @Test
    @DirtiesContext
    void testNuevaActividad(){
        var direccion = servicio.login("direccion@clubsocios.es", "serviceSecret").get();

        //Precio incorrecto, plazas incorrectas, violaciones fecha
        var actividad = new Actividad("act1", "Visita a museo", "Descricion", -10, -15, LocalDate.of(2023,10,13), LocalDate.of(2023,9,13), LocalDate.of(2023,10,10));

        assertThatThrownBy(() -> servicio.crearActividad(direccion, actividad)).isInstanceOf(ConstraintViolationException.class);

        //Actividad repetida
        var actividad2 = new Actividad("act1", "Visita a museo", "Descricion", 15, 30, LocalDate.of(2024,11,2), LocalDate.of(2024,10,20), LocalDate.of(2024,10,30));
        servicio.crearActividad(direccion, actividad2);
        assertThatThrownBy(() -> servicio.crearActividad(direccion, actividad2)).isInstanceOf(ActividadYaRegistrada.class);

    }

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

//    @Test
//    @DirtiesContext
//    void testRevisarSolicitudes(){
//        var direccion = servicio.login("direccion@clubsocios.es", "serviceSecret").get();
//
//        var actividad = new Actividad("act1", "Visita a museo", "Descricion", 15, 30, LocalDate.of(2024,11,1), LocalDate.of(2024,10,20), LocalDate.of(2024,10,30));
//
//        servicio.crearActividad(direccion, actividad);
//        List<Solicitud> solicitudesRevisadas = servicio.revisarSolicitudes(direccion, actividad.getId());
//
//        assertNotNull("Las solicitudesRevisadas no deberian ser nulas", solicitudesRevisadas);
//
//    }

//    @Test
//    @DirtiesContext
//    void testAsignarPlazasFinInscripcion(){
//        var direccion = servicio.login("direccion@clubsocios.es", "serviceSecret").get();
//
//        var actividad = new Actividad("act1", "Visita a museo", "Descricion", 15, 30, LocalDate.of(2024,11,1), LocalDate.of(2024,10,20), LocalDate.of(2024,10,30));
//
//        servicio.crearActividad(direccion, actividad);
//    }


}

