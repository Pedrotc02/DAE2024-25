package clubSocios.servicios;

import es.ujaen.dae.clubSocios.entidades.Actividad;
import es.ujaen.dae.clubSocios.entidades.Socio;
import es.ujaen.dae.clubSocios.entidades.Solicitud;
import es.ujaen.dae.clubSocios.entidades.Temporada;
import es.ujaen.dae.clubSocios.enums.EstadoCuota;
import es.ujaen.dae.clubSocios.enums.EstadoSolicitud;
import es.ujaen.dae.clubSocios.excepciones.*;
import es.ujaen.dae.clubSocios.repositorios.RepositorioActividad;
import es.ujaen.dae.clubSocios.repositorios.RepositorioSocio;
import es.ujaen.dae.clubSocios.servicios.ServicioClub;
import jakarta.transaction.Transactional;
import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.as;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.springframework.test.util.AssertionErrors.assertEquals;

@SpringBootTest(classes = es.ujaen.dae.clubSocios.app.Main.class)
@ActiveProfiles("test")
public class TestServicioClub {
    @Autowired
    ServicioClub servicio;

    @Test
    @DirtiesContext
    void testNuevaTemporada() {
        var direccion = servicio.login("direccion@clubsocios.es", "serviceSecret").get();
        var temporada24 = new Temporada(2024);
        servicio.crearTemporada(direccion, temporada24);

//        assertThat(servicio.temporadas().size()).isEqualTo(1);
//        assertThat(servicio.actividades().size()).isEqualTo(0);
//        assertThat(servicio.socios().size()).isEqualTo(0);

        var temporada24V2 = new Temporada(2024);
        assertThatThrownBy(() -> servicio.crearTemporada(direccion, temporada24V2)).isInstanceOf(TemporadaYaRegistrada.class);
    }

    @Test
    @DirtiesContext
    void testNuevaActividad() {
        var direccion = servicio.login("direccion@clubsocios.es", "serviceSecret").get();
        var temporada24 = servicio.crearTemporada(direccion, new Temporada(2024));

        var noValida = new Actividad("Visita a museo", "Descricion", -15, -30, LocalDate.parse("2024-12-25"), LocalDate.parse("2024-10-12"), LocalDate.parse("2024-12-21"));
        assertThatThrownBy(() -> servicio.crearActividad(direccion, temporada24.getTemporadaId(), noValida))
                .isInstanceOf(ConstraintViolationException.class);

        var actividad = new Actividad("Visita a museo", "Descricion", 15, 30, LocalDate.parse("2024-12-25"), LocalDate.parse("2024-10-12"), LocalDate.parse("2024-12-21"));
        var creada1 = servicio.crearActividad(direccion, temporada24.getTemporadaId(), actividad);

        assertThat(creada1.getId()).isEqualTo(1);

        var actividad2 = new Actividad("Visita a museo", "Descricion", 15, 30, LocalDate.parse("2024-12-25"), LocalDate.parse("2024-10-12"), LocalDate.parse("2024-12-21"));

        assertThatThrownBy(() -> servicio.crearActividad(direccion, temporada24.getTemporadaId(), actividad2)).isInstanceOf(ActividadYaRegistrada.class);
    }

    @Test
    @DirtiesContext
    void testCrearSocio() {
        // Verifica que el correo no tiene el formato correcto y lanza una ConstraintViolationException
        var socio = new Socio("pruebagmail.com", "Pedro", "Apellido1 Apellido2", "11111111M", "123456789", "contraseña", EstadoCuota.PAGADA);
        assertThatThrownBy(() -> servicio.crearSocio(socio)).isInstanceOf(ConstraintViolationException.class);

        // Crea un nuevo socio correctamente y verifica que se puede añadir al sistema
        var socio2 = new Socio("prueba@gmail.com", "Pedro", "Apellido1 Apellido2", "11111111M", "690123456", "123456", EstadoCuota.PAGADA);
        servicio.crearSocio(socio2);

        // Verifica que el intento de agregar el mismo socio lanza una excepción SocioYaRegistrado
        assertThatThrownBy(() -> servicio.crearSocio(socio2)).isInstanceOf(SocioYaRegistrado.class);
    }

    @Test
    @DirtiesContext
    void testRegistrarSolicitud() {
        var direccion = servicio.login("direccion@clubsocios.es", "serviceSecret").get();

        var temporada = servicio.crearTemporada(direccion, new Temporada(2024));
        var socio1 = new Socio("prueba@gmail.com", "Pedro", "Apellido1 Apellido2", "11111111M", "690123456", "123456", EstadoCuota.PAGADA);
        var actividad = new Actividad("Visita a museo", "Descricion", 15, 30, LocalDate.parse("2024-12-25"), LocalDate.parse("2024-10-12"), LocalDate.parse("2024-12-21"));

        servicio.crearActividad(direccion, temporada.getTemporadaId(), actividad);
        servicio.crearSocio(socio1);
        //servicio.registrarSolicitud(direccion,socio1, actividad.getId(), solicitud, 4);

        List<Solicitud> resultadoEsperado = servicio.revisarSolicitudes(direccion, actividad.getId());

        assertEquals("Hay una solicitud", 1, resultadoEsperado.size());
        assertEquals("Es del socio creado", socio1.getSocioId(), resultadoEsperado.get(1).getSocioId());

//        assertThat(servicio.actividades().get(actividad.getId().intValue() - 1)
//                .getSolicitudes().stream()
//                .filter(s -> s.getSocioId().equals(socio1.getSocioId())))
//                .size()
//                .isEqualTo(1);
//
//        assertThat(servicio.solicitudes(actividad.getId()).size()).isEqualTo(1);
    }

    @Test
    @DirtiesContext
    void testBuscarTemporada() {
        var direccion = servicio.login("direccion@clubsocios.es", "serviceSecret").get();

        var temporada = new Temporada(2024);
        servicio.crearTemporada(direccion, temporada);

        Optional<Temporada> temporadaOptional = servicio.buscarTemporada(temporada.getTemporadaId());
        assertThat(temporadaOptional.isPresent()).isTrue();
        assertThat(temporadaOptional.get().getTemporadaId()).isEqualTo(temporada.getTemporadaId());
    }

    @Test
    @DirtiesContext
    void testBuscarActividad() {
        var direccion = servicio.login("direccion@clubsocios.es", "serviceSecret").get();

        var temporada = new Temporada(2024);
        servicio.crearTemporada(direccion, temporada);

        var actividad = new Actividad("Visita a museo", "Descricion", 15, 30, LocalDate.parse("2024-12-25"), LocalDate.parse("2024-10-12"), LocalDate.parse("2024-12-21"));
        servicio.crearActividad(direccion, temporada.getTemporadaId(), actividad);

        var acti2 = servicio.buscarActividad(actividad.getId());

        assertThat(acti2.get().getId()).isEqualTo(actividad.getId());

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
    void testOperacionDireccion() {
        var temporada = new Temporada(2024);
        var direccion = servicio.login("direccion@clubsocios.es", "serviceSecret").get();
        var socio1 = new Socio("prueba@gmail.com", "Pedro", "Apellido1 Apellido2", "11111111M", "690123456", "123456", EstadoCuota.PAGADA);

        assertThatThrownBy(() -> servicio.crearTemporada(socio1, temporada)).isInstanceOf(OperacionDeDireccion.class);
        servicio.crearTemporada(direccion, temporada);

        var actividad = new Actividad("Visita a museo", "Descricion", 15, 30, LocalDate.parse("2024-12-25"), LocalDate.parse("2024-10-12"), LocalDate.parse("2024-12-21"));
        assertThatThrownBy(() -> servicio.crearActividad(socio1, temporada.getTemporadaId(), actividad)).isInstanceOf(OperacionDeDireccion.class);
        servicio.crearActividad(direccion, temporada.getTemporadaId(), actividad);

        assertThat(servicio.buscarTemporada(temporada.getTemporadaId()).isPresent());
        assertThat(servicio.buscarActividad(actividad.getId()).get().getId().equals(actividad.getId()));
    }

    @Test
    @DirtiesContext
    void testActualizaEstado() {
        var direccion = servicio.login("direccion@clubsocios.es", "serviceSecret").get();

        var socio = new Socio("prueba@gmail.com", "Pedro", "Apellido1 Apellido2", "11111111M", "690123456", "123456", EstadoCuota.PENDIENTE);
        servicio.crearSocio(socio);

        socio = servicio.actualizarEstadoCuota(direccion, socio.getSocioId(), EstadoCuota.PAGADA);
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

        servicio.resetearEstadoCuota(direccion);

        var socio1Actualizado = servicio.buscarSocio(socio1.getSocioId()).get();
        var socio2Actualizado = servicio.buscarSocio(socio2.getSocioId()).get();

        assertEquals("El estado de cuota de los socios debe estar en Pendiente", EstadoCuota.PENDIENTE, socio1Actualizado.getEstadoCuota());
        assertEquals("El estado de cuota de los socios debe estar en Pendiente", EstadoCuota.PENDIENTE, socio2Actualizado.getEstadoCuota());
    }

    @Test
    @DirtiesContext
    void testAsignarPlazasFinal(){
        var direccion = servicio.login("direccion@clubsocios.es", "serviceSecret").get();

        var temporada = new Temporada(2024);
        servicio.crearTemporada(direccion, temporada);

        var socio1 = new Socio("prueba@gmail.com", "Pedro", "Apellido1 Apellido2", "11111111M", "690123456", "123456", EstadoCuota.PAGADA);
        servicio.crearSocio(socio1);

        var actividad = new Actividad("Visita a museo", "Descricion", 15, 30, LocalDate.parse("2024-12-25"), LocalDate.parse("2024-10-12"), LocalDate.parse("2024-12-09"));
        servicio.crearActividad(direccion, temporada.getTemporadaId(), actividad);

        var solicitud = servicio.procesarInscripcion(socio1, 3, true, actividad);

        servicio.asignarPlazasFinal(direccion, actividad.getId(), solicitud);

        var solicitudesActualizadas = actividad.revisarSolicitudes();

        assertEquals("El numero de solicitudes actualizadas debe ser 1", 1, solicitudesActualizadas.size());
        assertEquals("La solicitud debería estar en estado Parcial ya que no se han asignado todos los acompañantes.", EstadoSolicitud.PARCIAL, solicitudesActualizadas.get(0).getEstadoSolicitud());
        assertEquals("El numero de plazas libres de la actividad sera 29, se ha asignado 1 ", 29, actividad.getPlazasDisponibles());
    }

    @Test
    @DirtiesContext
    void testAsignarPlazasFinInscripcion(){
        var direccion = servicio.login("direccion@clubsocios.es", "serviceSecret").get();

        var temporada = new Temporada(2024);
        servicio.crearTemporada(direccion, temporada);

        var socio1 = new Socio("prueba@gmail.com", "Pedro", "Apellido1 Apellido2", "11111111M", "690123456", "123456", EstadoCuota.PAGADA);
        servicio.crearSocio(socio1);
        var socio2 = new Socio("prueba2@gmail.com", "Prueba", "Apellido1 Apellido2", "11211111M", "690123456", "123456", EstadoCuota.PAGADA);
        servicio.crearSocio(socio2);

        var actividad = new Actividad("Visita a museo", "Descricion", 15, 6, LocalDate.parse("2024-12-25"), LocalDate.parse("2024-10-12"), LocalDate.parse("2024-12-09"));
        servicio.crearActividad(direccion, temporada.getTemporadaId(), actividad);

        servicio.procesarInscripcion(socio1, 3, true, actividad);
        servicio.procesarInscripcion(socio2, 5, true, actividad);

        servicio.asignarPlazasFinInscripcion(direccion, actividad.getId());

        var solicitudesActualizadas = actividad.revisarSolicitudes();

        assertEquals("El numero de solicitudes actualizadas debe ser 2", 2, solicitudesActualizadas.size());
        assertEquals("La solicitud 1 debería estar en estado Cerrada ya que se han asignado todos los acompañantes.", EstadoSolicitud.CERRADA, solicitudesActualizadas.get(0).getEstadoSolicitud());
        assertEquals("La solicitud 2 debería estar en estado Parcial ya que no se han asignado todos los acompañantes.", EstadoSolicitud.PARCIAL, solicitudesActualizadas.get(1).getEstadoSolicitud());
        assertEquals("El numero de plazas libres de la actividad sera 0, se ha asignado 6 ", 0, actividad.getPlazasDisponibles());

    }

//    @Test
//    public void testAsignarUltimaPlazaConcurrencia() throws InterruptedException {
//        int anioActual = LocalDate.now().getYear();
//        LocalDate fechaInicioInscripcion = LocalDate.of(anioActual, 11, 15); // Inicio antes de hoy
//        LocalDate fechaFinInscripcion = LocalDate.of(anioActual, 12, 15); // Fin después de hoy
//        LocalDate fechaCelebracion = LocalDate.of(anioActual, 12, 20); // Celebración después de la fecha de fin
//
//        Actividad actividad = new Actividad(
//                "Excursión de Montaña",
//                "Actividad de senderismo en la sierra",
//                50.0,
//                1, // Solo una plaza disponible
//                fechaCelebracion,
//                fechaInicioInscripcion,
//                fechaFinInscripcion
//        );
//        servicio.guardarActividad(actividad);
//
//        Socio socio1 = new Socio("socio1@mail.com", "Juan", "Pérez", "12345678A", "953112233", "clave123", EstadoCuota.PAGADA);
//        Socio socio2 = new Socio("socio2@mail.com", "Ana", "López", "23456789B", "953223311", "clave123", EstadoCuota.PAGADA);
//        servicio.crearSocio(socio1);
//        servicio.crearSocio(socio2);
//
//        // Crear dos hilos para simular la concurrencia
//        Thread hilo1 = new Thread(() -> {
//            try {
//                servicio.asignarUltimaPlaza(socio1, actividad.getId());
//            } catch (NoHayPlazas | SolicitudYaRealizada e) {
//                System.err.println(e.getMessage());
//            }
//        });
//
//        Thread hilo2 = new Thread(() -> {
//            try {
//                servicio.asignarUltimaPlaza(socio2, actividad.getId());
//            } catch (NoHayPlazas | SolicitudYaRealizada e) {
//                System.err.println(e.getMessage());
//            }
//        });
//
//        hilo1.start();
//        hilo2.start();
//
//        hilo1.join();
//        hilo2.join();
//
//        // Verificar que solo uno de los dos socios haya conseguido la plaza
//        Actividad actividadFinal = servicio.buscarActividad(actividad.getId());
//        if (actividadFinal == null)
//            throw new NullPointerException("La actividad no se ha encontrado.");
//
//        long solicitudesConPlaza = actividadFinal.getSolicitudes().stream()
//                .filter(solicitud -> solicitud.getPlazasConcedidas() == 1)
//                .count();
//
//        Assertions.assertEquals(1, solicitudesConPlaza, "Solo un socio debería haber obtenido la plaza.");
//    }
}