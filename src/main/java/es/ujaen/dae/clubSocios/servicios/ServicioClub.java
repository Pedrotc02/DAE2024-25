package es.ujaen.dae.clubSocios.servicios;

import es.ujaen.dae.clubSocios.entidades.Actividad;
import es.ujaen.dae.clubSocios.entidades.Socio;
import es.ujaen.dae.clubSocios.enums.EstadoCuota;
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

    public void crearActividad(@Valid Actividad actividad) {
        if(actividades.containsKey(actividad.getId())){
            throw new ActividadYaRegistrada();
        }

        actividades.put(actividad.getId(), actividad);
    }

    public void revisarSolicitudes(String actividadId) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void asignarPlazasFinales(String actividadId) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void resetearEstadoCuota() {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
