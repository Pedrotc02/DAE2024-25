package es.ujaen.dae.clubSocios.servicios;

import es.ujaen.dae.clubSocios.entidades.Actividad;
import es.ujaen.dae.clubSocios.entidades.Socio;
import es.ujaen.dae.clubSocios.entidades.Solicitud;
import es.ujaen.dae.clubSocios.enums.EstadoCuota;
import es.ujaen.dae.clubSocios.excepciones.*;
import es.ujaen.dae.clubSocios.repositorios.RepositorioActividad;
import es.ujaen.dae.clubSocios.repositorios.RepositorioSocio;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.util.*;

@Service
@Validated
public class ServicioClub {
    @Autowired
    RepositorioSocio repositorioSocio;

    @Autowired
    RepositorioActividad repositorioActividad;

    private static final Socio direccion = new Socio("direccion@clubsocios.es", "direccion", "-", "99999999Z","953897654", "serviceSecret", EstadoCuota.PAGADA);

    public ServicioClub() {

    }

    public void crearSocio(@Valid Socio socio) {
        if(socio.getSocioId().equals(direccion.getSocioId())){
            throw new SocioYaRegistrado();
        }

        repositorioSocio.guardarSocio(socio);
    }

    public Optional<Socio> login(@Email String email, String clave){
        if (direccion.getSocioId().equals(email) && direccion.getClaveAcceso().equals(clave))
            return Optional.of(direccion);

        return repositorioSocio.buscarPorId(email).filter(socio -> socio.getClaveAcceso().equals(clave));
    }


    public void actualizarEstadoCuota(String email, EstadoCuota estadoCuota) {
        if(repositorioSocio.buscarPorId(email).isEmpty()){
            throw new NoSuchElementException();
        }

        repositorioSocio.actualizarEstadoCuota(email, estadoCuota);
    }

    public void crearActividad(@Valid Socio socio, @Valid Actividad actividad) {
        if (direccion.getSocioId().equals(socio.getSocioId()) && direccion.getClaveAcceso().equals(socio.getClaveAcceso())){
            repositorioActividad.guardarActividad(actividad);
        }else{
            throw new OperacionDeDireccion();
        }
    }

    /**
     * La dirección registra una nueva solicitud en una actividad.
     * Esta funcionalidad es suponiendo que alguien vaya presencialmente
     * y le diga al personal de la dirección que quiere apuntarse a una actividad con un número de acompañantes,
     * ó que la dirección quiera hacerlo por su cuenta, pero siempre deberá indicar un socio, una actividad y un número de acompañantes.
     * @param socio socio que quiere hacer la solicitud en la actividad.
     * @param actividadId identificador de la actividad en la que se meterá la solicitud.
     */
    public void registrarSolicitud(@Valid Socio socio, int actividadId, int numAcom) {
        if (direccion.getSocioId().equals(socio.getSocioId()) && direccion.getClaveAcceso().equals(socio.getClaveAcceso())){
            Optional<Actividad> actividadOptional = repositorioActividad.buscarPorId(actividadId);

            if(actividadOptional.isPresent()){
                Actividad actividad = actividadOptional.get();
                actividad.solicitarInscripcion(socio, numAcom);
            }
        }else{
            throw new OperacionDeDireccion();
        }

    }

    /**
     * Al usar la dirección esta funcionalidad del servicio, éste devuelve la lista de solicitudes de la actividad elegida
     * @param actividadId id de la actividad en la que se quieren revisar solicitudes
     * @return lista de solicitudes de la actividad cuyo id ha sido dado
     */
    public List<Solicitud> revisarSolicitudes(@Valid Socio socio, int actividadId) {
        if (direccion.getSocioId().equals(socio.getSocioId()) && direccion.getClaveAcceso().equals(socio.getClaveAcceso())){
            Optional<Actividad> actividadOptional = repositorioActividad.buscarPorId(actividadId);

            if(actividadOptional.isPresent()){
                Actividad actividad = actividadOptional.get();
                return actividad.revisarSolicitudes();
            }
        }else{
            throw new OperacionDeDireccion();
        }
        
        return null;
    }

    /**
     * Asignación de plazas al final del período de inscripción de manera manual llevada a cabo por la dirección.
     * Si la solicitud no se encuentra entre las solicitudes de la actividad, lanza la correspondiente excepción.
     * En caso de que el proceso sea exitoso, asignará una plaza en la actividad,
     * contará en la solicitud que se le ha concedido una plaza más, y se revisará el estado de la misma por si necesita cambiarse.
     * @param actividadId id de la actividad en la que se debe encontrar la solicitud.
     * @param solicitud solicitud a la que la dirección va a asignar la plaza, si se puede.
     */
    public void asignarPlazasFinal(@Valid Socio socio, int actividadId, @Valid Solicitud solicitud) {
        if (direccion.getSocioId().equals(socio.getSocioId()) && direccion.getClaveAcceso().equals(socio.getClaveAcceso())){
            Optional<Actividad> actividadOptional = repositorioActividad.buscarPorId(actividadId);

            if(actividadOptional.isPresent()){
                Actividad actividad = actividadOptional.get();
                actividad.asignarPlazasFinal(solicitud);
            }
        }else{
            throw new OperacionDeDireccion();
        }
    }


    public void asignarPlazasFinInscripcion(@Valid Socio socio, int actividadId) {
        if (direccion.getSocioId().equals(socio.getSocioId()) && direccion.getClaveAcceso().equals(socio.getClaveAcceso())){
            Optional<Actividad> actividadOptional = repositorioActividad.buscarPorId(actividadId);

            if(actividadOptional.isPresent()){
                Actividad actividad = actividadOptional.get();
                actividad.asignarPlazasFinInscripcion();
            }
        }else{
            throw new OperacionDeDireccion();
        }
    }


    public void resetearEstadoCuota() {
        List<String> idSocios = repositorioSocio.listadoIds();

        idSocios.stream().map(id -> repositorioSocio.buscarPorId(id).get())
                .forEach(socio -> socio.setEstadoCuota(EstadoCuota.PENDIENTE));

    }

}
