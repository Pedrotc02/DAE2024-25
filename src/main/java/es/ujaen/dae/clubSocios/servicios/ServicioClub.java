package es.ujaen.dae.clubSocios.servicios;

import es.ujaen.dae.clubSocios.entidades.Actividad;
import es.ujaen.dae.clubSocios.entidades.Socio;
import es.ujaen.dae.clubSocios.entidades.Solicitud;
import es.ujaen.dae.clubSocios.enums.EstadoCuota;
import es.ujaen.dae.clubSocios.enums.EstadoSolicitud;
import es.ujaen.dae.clubSocios.excepciones.*;
import es.ujaen.dae.clubSocios.util.UtilList;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.time.LocalDate;
import java.util.*;

@Service
@Validated
public class ServicioClub {
    Map<String, Socio> socios;
    Map<String, Actividad> actividades;

    private static final Socio direccion = new Socio("99999999Z", "direccion", "-", "direccion@clubsocios.es","953897654", "serviceSecret", EstadoCuota.PAGADA);

    public ServicioClub() {
        this.socios = new TreeMap<>();
        this.actividades = new TreeMap<>();
    }

    public void crearSocio(@Valid Socio socio) {
        if(socios.containsKey(socio.getEmail())){
            throw new SocioYaRegistrado();
        }
        
        socios.put(socio.getEmail(), socio);
    }

    public Optional<Socio> login(@Email String email, String clave){
        Socio socio = socios.get(email);
        return (socio != null && socio.getClaveAcceso().equals(clave)) ? Optional.of(socio): Optional.empty();
    }

    public void actualizarEstadoCuota(String email, EstadoCuota estadoCuota) {
        socios.get(email).setEstadoCuota(estadoCuota);
    }

    public void crearActividad(Socio dir, @Valid Actividad actividad) {
        if (!dir.getNombre().equals("direccion")) {
            throw new OperacionDeDireccion();
        }

        if(actividades.containsKey(actividad.getId())){
            throw new ActividadYaRegistrada();
        }

        actividades.put(actividad.getId(), actividad);
    }

    /**
     * @param dir el objeto direccion
     * @param actividadId id de la actividad de la que se quieren revisar las solicitudes
     * @throws OperacionDeDireccion si quien intenta revisar solicitudes de una actividad no es la direccion del club
     * @throws ActividadNoExistente si la actividad es inválida en el club
     * @throws FechaFinInscripcionNoValida si a fecha actual, no ha terminado la inscripción en la actividad
     * @return deuvelve todas las solicitudes de una actividad ordenadas por fecha de menor a mayor
     */
    public List<Solicitud> revisarSolicitudes(Socio dir, String actividadId) {
        if (!dir.getNombre().equals("direccion")) {
            throw new OperacionDeDireccion();
        }

        if (!actividades.containsKey(actividadId)) {
            throw new ActividadNoExistente();
        }

        LocalDate now = LocalDate.now();
        if (actividades.get(actividadId).getFechaFinInscripcion().isBefore(now)) {
            throw new FechaFinInscripcionNoValida();
        }

        UtilList.ordenarListaPorFecha(actividades.get(actividadId));
        return actividades.get(actividadId).getSolicitudes();
    }


    public void asignarPlazasFinInscripcion(Socio dir , String actividadId) {

        if (!dir.getNombre().equals("direccion")) {
            throw new OperacionDeDireccion();
        }

        Actividad actividad = actividades.get(actividadId);
        List<Solicitud> solicitudes =  revisarSolicitudes(dir, actividadId);
        for (Solicitud solicitud : solicitudes) {
            //Se le da prioridad a las solicitudes de los socios que han pagado para intentar meter a los acompañantes
            if(solicitud.getEstadoSolicitud().equals(EstadoSolicitud.PARCIAL)){

                if(solicitud.getNumAcompanantes() <= actividad.getPlazasDisponibles()) {
                    actividad.asignarPlazas(solicitud.getNumAcompanantes());
                    solicitud.setEstadoSolicitud(EstadoSolicitud.CERRADA);
                } else {
                    for (int i = 0; i < solicitud.getNumAcompanantes(); i++) {
                        if (!actividad.hayPlazas()) {
                            throw new PlazasNoDisponibles("No quedan más plazas por asignar en esta actividad");
                        }
                        actividad.asignarPlazas(1);
                    }
                }
            }
        }

        //Ahora se estudian las solicitudes de las personas que no han pagado la cuota
        for (Solicitud solicitud : solicitudes){
            if(solicitud.getEstadoSolicitud().equals(EstadoSolicitud.PENDIENTE)){

                if(solicitud.getNumAcompanantes() + 1 <= actividad.getPlazasDisponibles()) {
                    actividad.asignarPlazas(solicitud.getNumAcompanantes() + 1);
                    solicitud.setEstadoSolicitud(EstadoSolicitud.CERRADA);
                } else {
                    for (int i = 0; i < solicitud.getNumAcompanantes() + 1; i++) {
                        if (!actividad.hayPlazas()) {
                            throw new PlazasNoDisponibles("No quedan más plazas por asignar en esta actividad");
                        }
                        actividad.asignarPlazas(1);
                    }
                }
            }
        }
    }

    public void resetearEstadoCuota() {
        for(Socio socio :  socios.values()){
            socio.setEstadoCuota(EstadoCuota.PENDIENTE);
        }
    }
}
