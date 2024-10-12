package es.ujaen.dae.clubSocios.servicios;

import es.ujaen.dae.clubSocios.entidades.Actividad;
import es.ujaen.dae.clubSocios.entidades.Socio;
import es.ujaen.dae.clubSocios.entidades.Solicitud;
import es.ujaen.dae.clubSocios.enums.EstadoCuota;
import es.ujaen.dae.clubSocios.enums.EstadoSolicitud;
import es.ujaen.dae.clubSocios.excepciones.ActividadYaRegistrada;
import es.ujaen.dae.clubSocios.excepciones.SocioYaRegistrado;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;

@Service
@Validated
public class ServicioClub {
    Map<String, Socio> socios;
    Map<String, Actividad> actividades;

    public ServicioClub() {
        this.socios = new TreeMap<>();
        this.actividades = new TreeMap<>();
    }

    public void crearSocio(@Valid Socio socio) {
        if (socios.containsKey(socio.getEmail())) {
            throw new SocioYaRegistrado();
        }

        socios.put(socio.getEmail(), socio);
    }

    public Optional<Socio> login(@Email String email, String clave) {
        Socio socio = socios.get(email);
        return (socio != null && socio.getClaveAcceso().equals(clave)) ? Optional.of(socio) : Optional.empty();
    }

    public void actualizarEstadoCuota(String email, EstadoCuota estadoCuota) {
        socios.get(email).setEstadoCuota(estadoCuota);
    }

    public void crearActividad(@Valid Actividad actividad) {
        if (actividades.containsKey(actividad.getId())) {
            throw new ActividadYaRegistrada();
        }

        actividades.put(actividad.getId(), actividad);
    }

    public void revisarSolicitudes(String actividadId) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    /**
     * En caso de que haya plazas disponibles,
     * se confirmará inmediatamente la inscripción del socio, pero los acompañantes no tendrán plaza de
     * forma inmediata, teniendo que esperar hasta que finalice el periodo de inscripción. Si un socio no
     * ha pagado la cuota de la presente temporada podrá solicitar la inscripción pero se le tratará como a
     * un acompañante (no tendrá plaza de forma inmediata).
     */

    // caso 1: el socio ha pagado la cuota (estadoCuota == PAGADA)
    // --- caso 1a: no hay sitio para nadie (plazasDisponibles == 0)
    // --- caso 1b: hay sitio para el socio pero no para todos los acompañantes (plazasDisponibles > 1 && < numAcompanantes + 1)
    // --- caso 1c: hay sitio para algunos acompañaantes (plazasDisponibles > 0 && < numAcompanantes + 1)
    // --- caso 1d: hay sitio para todos (plazasDisponibles >= numAcompanantes + 1)
    // caso 2: el socio no ha pagado la cuota (estadoCuota == PENDIENTE)
    // --- caso 2a: no hay sitio para nadie (plazasDisponibles == 0)
    // --- caso 2b: hay sitio para algunos acompañantes (plazasDisponibles > 0 && < numAcompanantes + 1)
    // --- caso 2c: hay sitio para todos (plazasDisponibles >= numAcompanantes + 1)
    public void asignarPlazas(Actividad actividad) {
        // TODO: recorrer el array de solicitudes y añadir a todos los acompañantes y socios con cuota pendiente
        //  (que cuentan como acompañantes) los socios que esten pagados tiene prioridad sobre todos los anteriores,
        //  es decir se hacen dos vueltas, primero se suman los pagos y luego el resto, seguramtente por orden de llegada

        int plazasDisponibles = actividad.getPlazasDisponibles();

        if (actividad.estaEnPeriodoInscripcion()) {
            // Primera vuelta: socios con cuota pagada
            for (Solicitud solicitud : actividad.getSolicitudes()) {
                if (plazasDisponibles == 0)
                    break;
                if (solicitud.getSocio().getEstadoCuota() == EstadoCuota.PAGADA) {
                    plazasDisponibles--;
                    solicitud.setEstadoSolicitud(EstadoSolicitud.CONFIRMADA);
                }
            }
        } else {
            for (Solicitud solicitud : actividad.getSolicitudes()) {
                if (plazasDisponibles == 0)
                    break;
                if (solicitud.getSocio().getEstadoCuota() != EstadoCuota.PAGADA) {
                    solicitud.setEstadoSolicitud(EstadoSolicitud.CONFIRMADA);
                }
            }
        }


    }

    public void asignarPlazasFinales(String actividadId) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void resetearEstadoCuota() {
        for (Socio socio : socios.values()) {
            socio.setEstadoCuota(EstadoCuota.PENDIENTE);
        }

    }
}
