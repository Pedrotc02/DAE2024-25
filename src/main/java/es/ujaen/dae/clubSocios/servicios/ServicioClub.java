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

        if(actividades.containsKey(actividad.getId())){
            throw new ActividadYaRegistrada();
        }

        actividades.put(actividad.getId(), actividad);
    }


    public List<Solicitud> revisarSolicitudes(Socio dir, String actividadId) {
        validarDireccion(dir);
        Actividad actividad = obtenerActividadPorId(actividadId);
        return actividad.revisarSolicitudes();
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
