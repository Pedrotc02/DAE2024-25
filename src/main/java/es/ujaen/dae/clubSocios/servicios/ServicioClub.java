package es.ujaen.dae.clubSocios.servicios;

import es.ujaen.dae.clubSocios.entidades.Actividad;
import es.ujaen.dae.clubSocios.entidades.Socio;
import es.ujaen.dae.clubSocios.entidades.Solicitud;
import es.ujaen.dae.clubSocios.enums.EstadoCuota;
import es.ujaen.dae.clubSocios.excepciones.*;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.util.*;

@Service
@Validated
public class ServicioClub {
    Map<String, Socio> socios;
    Map<String, Actividad> actividades;

    private static final Socio direccion = new Socio("direccion@clubsocios.es", "direccion", "-", "99999999Z","953897654", "serviceSecret", EstadoCuota.PAGADA);

    public ServicioClub() {
        this.socios = new TreeMap<>();
        this.actividades = new TreeMap<>();
    }

    public void crearSocio(Socio dir, @Valid Socio socio) {
        validarDireccion(dir);

        if(socios.containsKey(socio.getSocioId())){
            throw new SocioYaRegistrado();
        }
        
        socios.put(socio.getSocioId(), socio);
    }

    public Optional<Socio> login(@Email String email, String clave){
        if (direccion.getSocioId().equals(email) && direccion.getClaveAcceso().equals(clave))
            return Optional.of(direccion);

        Socio socio = socios.get(email);
        return (socio != null && socio.getClaveAcceso().equals(clave)) ? Optional.of(socio): Optional.empty();
    }


    public void actualizarEstadoCuota(String email, EstadoCuota estadoCuota) {
        if (!socios.containsKey(email))
            throw new NoSuchElementException();

        socios.get(email).setEstadoCuota(estadoCuota);
    }

    public void crearActividad(Socio dir, @Valid Actividad actividad) {
        validarDireccion(dir);

        if (actividades.containsKey(actividad.getId())){
            throw new ActividadYaRegistrada();
        }

        actividades.put(actividad.getId(), actividad);
    }

    /**
     * La dirección registra una nueva solicitud en una actividad.
     * Esta funcionalidad es suponiendo que alguien vaya presencialmente
     * y le diga al personal de la dirección que quiere apuntarse a una actividad con un número de acompañantes,
     * ó que la dirección quiera hacerlo por su cuenta, pero siempre deberá indicar un socio, una actividad y un número de acompañantes.
     * @param dir dirección del club.
     * @param socio socio que quiere hacer la solicitud en la actividad.
     * @param actividadId identificador de la actividad en la que se meterá la solicitud.
     */
    public void registrarSolicitud(Socio dir, @Valid Socio socio, @Valid String actividadId, int numAcom) {
        validarDireccion(dir);

        Actividad actividad = obtenerActividadPorId(actividadId);

        actividad.solicitarInscripcion(socio, numAcom);
    }

    /**
     * Al usar la dirección esta funcionalidad del servicio, éste devuelve la lista de solicitudes de la actividad elegida
     * @param dir dirección del club.
     * @param actividadId id de la actividad en la que se quieren revisar solicitudes
     * @return lista de solicitudes de la actividad cuyo id ha sido dado
     */
    public List<Solicitud> revisarSolicitudes(Socio dir, String actividadId) {
        validarDireccion(dir);

        Actividad actividad = obtenerActividadPorId(actividadId);

        return actividad.revisarSolicitudes();
    }

    /**
     * Asignación de plazas al final del período de inscripción de manera manual llevada a cabo por la dirección.
     * Si la solicitud no se encuentra entre las solicitudes de la actividad, lanza la correspondiente excepción.
     * En caso de que el proceso sea exitoso, asignará una plaza en la actividad,
     * contará en la solicitud que se le ha concedido una plaza más, y se revisará el estado de la misma por si necesita cambiarse.
     * @param dir dirección del club.
     * @param actividadId id de la actividad en la que se debe encontrar la solicitud.
     * @param solicitud solicitud a la que la dirección va a asignar la plaza, si se puede.
     */
    public void asignarPlazasFinal(Socio dir, String actividadId, @Valid Solicitud solicitud) {
        validarDireccion(dir);

        Actividad actividad = obtenerActividadPorId(actividadId);

        actividad.asignarPlazasFinal(solicitud);
    }


    public void asignarPlazasFinInscripcion(Socio dir, String actividadId) {
        validarDireccion(dir);

        Actividad actividad = obtenerActividadPorId(actividadId);

        actividad.asignarPlazasFinInscripcion();
    }


    public void resetearEstadoCuota() {
        socios.values().forEach(socio -> socio.setEstadoCuota(EstadoCuota.PENDIENTE));
    }

    
    private Actividad obtenerActividadPorId(String actividadId) {
        Actividad actividad = actividades.get(actividadId);
        if (actividad == null) {
            throw new ActividadNoValida();
        }
        return actividad;
    }

    private void validarDireccion(Socio socio) {
        if (!socio.getNombre().equals("direccion")) {
            throw new OperacionDeDireccion();
        }
    }
}
