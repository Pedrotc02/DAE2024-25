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
import org.springframework.transaction.annotation.Propagation;
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

    ///Prefiero buscar temporada por año, en vez de por id
    public Optional<Temporada> buscarTemporada(int anio) {
        return repositorioTemporada.buscarPorAnio(anio);
    }

    public Optional<Actividad> buscarActividad(Long id){
        return repositorioActividad.buscarPorId(id);
    }

    public Optional<Socio> buscarSocio(String id){
        return repositorioSocio.buscarPorId(id);
    }

    public List<Actividad> buscarActividadPorNombre(String nombre){
        return repositorioActividad.buscarPorNombre(nombre);
    }

    public List<Actividad> obtenerActividadesTemporada(Long id) {
        return repositorioTemporada.obtenerActividadesDeTemporada(id);
    }

    public Temporada crearTemporada(Socio dir, @Valid Temporada temporada) {
        comprobarDireccion(dir);

        repositorioTemporada.crear(temporada);
        return temporada;
    }

    public void crearSocio(@Valid Socio socio) {
        if (repositorioSocio.buscarPorId(socio.getSocioId()).isPresent()) {
            throw new SocioYaRegistrado();
        }
        repositorioSocio.crear(socio);
        //return socio;
    }

    @Transactional
    public Actividad crearActividad(Socio dir, Long temporadaId, Actividad actividad) {
        comprobarDireccion(dir);

        var temporada = repositorioTemporada.buscarPorId(temporadaId).orElseThrow(() -> new TemporadaNoEncontrada("Temporada " + temporadaId + " no encontrada"));

        if(!repositorioActividad.buscarPorNombre(actividad.getTitulo()).isEmpty()){
            throw new ActividadYaRegistrada();
        }

        temporada.aniadirActividad(actividad);
        actividad.setTemporada(temporada);

        repositorioActividad.guardarActividad(actividad);
        repositorioTemporada.actualizar(temporada);

        return actividad;
    }

//    /**
//     * La dirección registra una nueva solicitud en una actividad.
//     * Esta funcionalidad es suponiendo que alguien vaya presencialmente
//     * y le diga al personal de la dirección que quiere apuntarse a una actividad con un número de acompañantes,
//     * ó que la dirección quiera hacerlo por su cuenta, pero siempre deberá indicar un socio, una actividad y un número de acompañantes.
//     *
//     * @param socio       socio que quiere hacer la solicitud en la actividad.
//     * @param actividadId identificador de la actividad en la que se meterá la solicitud.
//     */
//    @Transactional
//    public void registrarSolicitud(Socio dir, @Valid Socio socio, Long actividadId, int numAcom) {
//        comprobarDireccion(dir);
//
//        var actividad = repositorioActividad.buscarPorId(actividadId).orElseThrow(() -> new ActividadNoEncontrada("Actividad " + actividadId + " no encontrada"));
//
//        repositorioActividad.guardarActividad(actividad);
//        actividad.solicitarInscripcion(socio, numAcom);
//
//    }

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

    /*
    public List<Solicitud> obtenerSolicitudesSocioActividad(@Valid String idSocio, Long actividadId) {
        var actividad = repositorioActividad.buscarPorId(actividadId).orElseThrow(() -> new ActividadNoEncontrada("Actividad " + actividadId + " no encontrada"));

        return actividad.revisarSolicitudes().stream().filter(s -> s.getSocioId().equals(idSocio)).toList();
    }
    */

    @Transactional
    public Solicitud procesarInscripcion(Socio socio, int numAcompanantes, boolean administrador, Actividad actividad){

        Solicitud solicitud = actividad.solicitarInscripcion(socio, numAcompanantes, administrador);
        actividad.agregarSolicitud(solicitud);
        repositorioActividad.guardarSolicitud(solicitud, actividad);
        repositorioActividad.actualizar(actividad);
        return solicitud;
    }

    /**
     * Asignación de plazas al final del período de inscripción de manera manual llevada a cabo por la dirección.
     * Si la solicitud no se encuentra entre las solicitudes de la actividad, lanza la correspondiente excepción.
     * En caso de que el proceso sea exitoso, asignará una plaza en la actividad,
     * contará en la solicitud que se le ha concedido una plaza más, y se revisará el estado de la misma por si necesita cambiarse.
     *
     * @param actividadId id de la actividad en la que se debe encontrar la solicitud.
     * @param solicitud   solicitud a la que la dirección va a asignar la plaza, si se puede.
     */
    @Transactional
    public void asignarPlazasFinal(Socio dir, Long actividadId, @Valid Solicitud solicitud) {
        comprobarDireccion(dir);
        var actividad = repositorioActividad.buscarPorId(actividadId).orElseThrow(() -> new ActividadNoEncontrada("Actividad " + actividadId + " no encontrada"));

        actividad.asignarPlazasFinal(solicitud);
        repositorioActividad.actualizar(actividad);
    }

    @Transactional
    public void asignarPlazasFinInscripcion(@Valid Socio dir, Long actividadId, boolean administrador) {
        comprobarDireccion(dir);
        var actividad = repositorioActividad.buscarPorId(actividadId).orElseThrow(() -> new ActividadNoEncontrada("Actividad " + actividadId + " no encontrada"));

        actividad.asignarPlazasFinInscripcion(administrador);
        repositorioActividad.actualizar(actividad);
    }

    @Transactional
    public void resetearEstadoCuota(Socio dir) {
        comprobarDireccion(dir);

        repositorioSocio.listadoIds().stream()
                .map(id -> repositorioSocio.buscarPorId(id).orElseThrow(SocioNoExiste::new))
                .forEach(socio -> {
                    socio.setEstadoCuota(EstadoCuota.PENDIENTE);
                    repositorioSocio.actualizar(socio);
                });
    }


    public void comprobarDireccion(Socio socio) {
        if (!EJEMPLO_SOCIO.getSocioId().equals(socio.getSocioId()) && !EJEMPLO_SOCIO.getClaveAcceso().equals(socio.getClaveAcceso())) {
            throw new OperacionDeDireccion();
        }
    }

    @Transactional
    public void registrarSolicitud(Socio dir, @Valid Socio socio, Long actividadId, int numAcom) {
        comprobarDireccion(dir);

        boolean solicitudRegistrada = false;
        int maxIntentos = 5;
        int intentos = 0;

        while (!solicitudRegistrada && intentos < maxIntentos) {
            try {

                Actividad actividad = repositorioActividad.buscarPorId(actividadId)
                        .orElseThrow(() -> new ActividadNoEncontrada("La actividad con ID " + actividadId + " no existe."));


                procesarInscripcion(socio, numAcom, true, actividad);
                solicitudRegistrada = true;

            } catch (OptimisticLockingFailureException e) {
                intentos++;
                if (intentos >= maxIntentos) {
                    throw new ConflictoDeConcurrenciaException(
                            "No se pudo registrar la solicitud después de " + maxIntentos +
                                    " intentos debido a conflictos de concurrencia. Intente nuevamente más tarde.");
                }
                // Reintentar automáticamente
            }
        }
    }

    public void modificarSolicitud(Solicitud solicitud, int nuevoNumAcom){
        solicitud.modificarNumAcompanantes(nuevoNumAcom);
    }

    public void borrarSolicitud(Socio socio, String idSolicitud) {
        socio.borrarSolicitud(idSolicitud);
    }

}