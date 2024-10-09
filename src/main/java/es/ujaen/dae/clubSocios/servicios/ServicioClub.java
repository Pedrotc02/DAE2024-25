package es.ujaen.dae.clubSocios.servicios;

import es.ujaen.dae.clubSocios.entidades.Actividad;
import es.ujaen.dae.clubSocios.entidades.Socio;
import es.ujaen.dae.clubSocios.enums.EstadoCuota;
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

    public ServicioClub() {
        this.socios = new TreeMap<>();
    }

    public void crearSocio(@Valid Socio socio) {
        if(socios.containsKey(socio.getSocioId())){
            throw new SocioYaRegistrado();
        }
        
        socios.put(socio.getSocioId(), socio);
    }

    public Optional<Socio> login(@Email String email, String clave){
        Socio socio = socios.get(email);
        return (socio != null && socio.getClaveAcceso().equals(clave)) ? Optional.of(socio): Optional.empty();
    }

    public void actualizarEstadoCuota(String socioId, EstadoCuota estadoCuota) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public Actividad crearActividad(/* parameters */) {
        throw new UnsupportedOperationException("Not supported yet.");
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
