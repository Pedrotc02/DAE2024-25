package es.ujaen.dae.clubSocios.servicios;

import es.ujaen.dae.clubSocios.entidades.Actividad;
import es.ujaen.dae.clubSocios.entidades.Socio;
import es.ujaen.dae.clubSocios.entidades.Solicitud;
import es.ujaen.dae.clubSocios.enums.EstadoCuota;
import es.ujaen.dae.clubSocios.excepciones.ActividadYaRegistrada;
import es.ujaen.dae.clubSocios.excepciones.OperacionDeDireccion;
import es.ujaen.dae.clubSocios.excepciones.SocioYaRegistrado;
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

    private static final Socio direccion = new Socio("99999999Z", "direccion", "-", "direccion@clubsocios.es","953897654", "serviceSecret", EstadoCuota.PAGADA );

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

    public void crearActividad(Socio dir,@Valid Actividad actividad) {
        if (!dir.getNombre().equals("direccion")) {
            throw new OperacionDeDireccion();
        }

        if(actividades.containsKey(actividad.getId())){
            throw new ActividadYaRegistrada();
        }

        actividades.put(actividad.getId(), actividad);
    }

    public List<Solicitud> revisarSolicitudes(Socio dir, String actividadId) {
        if (!dir.getNombre().equals("direccion")) {
            throw new OperacionDeDireccion();
        }

        if (!actividades.containsKey(actividadId)) {
            throw new UnsupportedOperationException("Not expected yet. ");
        }

        LocalDate now = LocalDate.now();
        if (actividades.get(actividadId).getFechaFinInscripcion().isBefore(now)) {
            throw new UnsupportedOperationException("Not expected yet. ");
        }

        return actividades.get(actividadId).getSolicitudes();
    }

    public void asignarPlazasFinales(Actividad actividad) {

    }

    public void resetearEstadoCuota() {
        for(Socio socio :  socios.values()){
            socio.setEstadoCuota(EstadoCuota.PENDIENTE);
        }
    }
}
