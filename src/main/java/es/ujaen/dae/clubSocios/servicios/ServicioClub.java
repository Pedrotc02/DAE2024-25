package es.ujaen.dae.clubSocios.servicios;

import es.ujaen.dae.clubSocios.entidades.Actividad;
import es.ujaen.dae.clubSocios.entidades.Socio;
import es.ujaen.dae.clubSocios.entidades.Solicitud;
import es.ujaen.dae.clubSocios.entidades.Temporada;
import es.ujaen.dae.clubSocios.enums.EstadoCuota;
import es.ujaen.dae.clubSocios.excepciones.*;
import es.ujaen.dae.clubSocios.repositorios.RepositorioActividad;
import es.ujaen.dae.clubSocios.repositorios.RepositorioSocio;
import es.ujaen.dae.clubSocios.repositorios.RepositorioTemporada;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.util.*;

import static es.ujaen.dae.clubSocios.util.UtilList.EJEMPLO_SOCIO;

@Service
@Validated
public class ServicioClub {
    @Autowired
    RepositorioSocio repositorioSocio;

    @Autowired
    RepositorioActividad repositorioActividad;

    @Autowired
    RepositorioTemporada repositorioTemporada;

    public ServicioClub() {

    }

    public Optional<Temporada> buscarTemporada(Long id) {
        return repositorioTemporada.buscarPorId(id);
    }

    public Actividad buscarActividad(Long id){
        return repositorioActividad.buscarPorId(id).get() != null ? repositorioActividad.buscarPorId(id).get() : null;
    }

    public Temporada crearTemporada(Socio dir, @Valid Temporada temporada) {
        comprobarDireccion(dir);

        repositorioTemporada.crear(temporada);
        return temporada;
    }

    public Socio crearSocio(@Valid Socio socio) {
        if (repositorioSocio.buscarPorId(socio.getSocioId()).isPresent()) {
            throw new SocioYaRegistrado();
        }
        repositorioSocio.crear(socio);
        return socio;
    }

    @Transactional
    public Actividad crearActividad(Socio dir, Long temporadaId, Actividad actividad) {
        comprobarDireccion(dir);

        var temporada = repositorioTemporada.buscarPorId(temporadaId).orElseThrow(() -> new TemporadaNoEncontrada("Temporada " + temporadaId + " no encontrada"));

        temporada.aniadirActividad(actividad);
        actividad.setTemporada(temporada);

        repositorioActividad.guardarActividad(actividad);

        return actividad;
    }

    /**
     * La dirección registra una nueva solicitud en una actividad.
     * Esta funcionalidad es suponiendo que alguien vaya presencialmente
     * y le diga al personal de la dirección que quiere apuntarse a una actividad con un número de acompañantes,
     * ó que la dirección quiera hacerlo por su cuenta, pero siempre deberá indicar un socio, una actividad y un número de acompañantes.
     *
     * @param socio       socio que quiere hacer la solicitud en la actividad.
     * @param actividadId identificador de la actividad en la que se meterá la solicitud.
     */
    @Transactional
    public void registrarSolicitud(Socio dir, @Valid Socio socio, Long actividadId, int numAcom) {
        comprobarDireccion(dir);

        var actividad = repositorioActividad.buscarPorId(actividadId).orElseThrow(() -> new ActividadNoEncontrada("Actividad " + actividadId + " no encontrada"));

        repositorioActividad.guardarActividad(actividad);
        actividad.solicitarInscripcion(socio, numAcom);

    }

    public Optional<Socio> login(@Email String email, String clave) {
        if (EJEMPLO_SOCIO.getSocioId().equals(email) && EJEMPLO_SOCIO.getClaveAcceso().equals(clave))
            return Optional.of(EJEMPLO_SOCIO);

        return repositorioSocio.buscarPorId(email).filter(socio -> socio.getClaveAcceso().equals(clave));
    }

    public Socio actualizarEstadoCuota(Socio dir, String email, EstadoCuota estadoCuota) {
        comprobarDireccion(dir);

        var socio = repositorioSocio.buscarPorId(email).orElseThrow(SocioNoExiste::new);

        socio.setEstadoCuota(estadoCuota);
        return repositorioSocio.actualizar(socio);

    }

    /**
     * Al usar la dirección esta funcionalidad del servicio, éste devuelve la lista de solicitudes de la actividad elegida
     *
     * @param actividadId id de la actividad en la que se quieren revisar solicitudes
     * @return lista de solicitudes de la actividad cuyo id ha sido dado
     */
    public List<Solicitud> revisarSolicitudes(@Valid Socio dir, Long actividadId) {
        comprobarDireccion(dir);
        var actividad = repositorioActividad.buscarPorId(actividadId).orElseThrow(() -> new ActividadNoEncontrada("Actividad " + actividadId + " no encontrada"));

        return actividad.revisarSolicitudes();
    }

//    /**
//     * Asignación de plazas al final del período de inscripción de manera manual llevada a cabo por la dirección.
//     * Si la solicitud no se encuentra entre las solicitudes de la actividad, lanza la correspondiente excepción.
//     * En caso de que el proceso sea exitoso, asignará una plaza en la actividad,
//     * contará en la solicitud que se le ha concedido una plaza más, y se revisará el estado de la misma por si necesita cambiarse.
//     *
//     * @param actividadId id de la actividad en la que se debe encontrar la solicitud.
//     * @param solicitud   solicitud a la que la dirección va a asignar la plaza, si se puede.
//     */
//    public void asignarPlazasFinal(@Valid Socio dir, Long actividadId, @Valid Solicitud solicitud) {
//        comprobarDireccion(dir);
//
//        Optional<Actividad> actividadOptional = repositorioActividad.buscarPorId(actividadId);
//
//        if (actividadOptional.isPresent()) {
//            Actividad actividad = actividadOptional.get();
//            actividad.asignarPlazasFinal(solicitud);
//        }
//
//    }
//
//    public void asignarPlazasFinInscripcion(@Valid Socio dir, Long actividadId) {
//        comprobarDireccion(dir);
//
//        Optional<Actividad> actividadOptional = repositorioActividad.buscarPorId(actividadId);
//
//        if (actividadOptional.isPresent()) {
//            Actividad actividad = actividadOptional.get();
//            actividad.asignarPlazasFinInscripcion();
//        }
//    }

    public void resetearEstadoCuota(Socio dir) {
        comprobarDireccion(dir);

        List<String> idSocios = repositorioSocio.listadoIds();

        idSocios.stream().map(id -> repositorioSocio.buscarPorId(id).get())
                .forEach( socio -> {
                    socio.setEstadoCuota(EstadoCuota.PENDIENTE);
                    socio = repositorioSocio.actualizar(socio);
                });

    }

    public void comprobarDireccion(Socio socio) {
        if (!EJEMPLO_SOCIO.getSocioId().equals(socio.getSocioId()) && !EJEMPLO_SOCIO.getClaveAcceso().equals(socio.getClaveAcceso())) {
            throw new OperacionDeDireccion();
        }
    }

    /* Operacion concurrente con bloqueo optimista */
    @Transactional
    public void asignarUltimaPlaza(@Valid Socio socio, Long actividadId) {
        boolean plazaAsignada = false;

        while (!plazaAsignada) {
            try {
                Actividad actividad = repositorioActividad.buscarPorId(actividadId)
                        .orElseThrow(() -> new ActividadNoEncontrada("La actividad con ID " + actividadId + " no existe."));

                if (!actividad.hayPlaza()) {
                    throw new NoHayPlazas("No hay plazas disponibles para asignar");
                }
                Solicitud nuevaSolicitud = actividad.solicitarInscripcion(socio, 0); // Sin acompañantes
                // Check si la solitud no ha sido ya realizada
                repositorioActividad.guardarSolicitud(socio.getSocioId(), nuevaSolicitud, actividadId);
                repositorioActividad.actualizar(actividad);

                plazaAsignada = true; // Salir del bucle si no hay conflicto
            } catch (OptimisticLockingFailureException e) {
                // Si hay un conflicto, reintentar cargando el estado actualizado
            }
        }
    }

    public void guardarActividad(Actividad actividad) {
        repositorioActividad.guardarActividad(actividad);
    }
}