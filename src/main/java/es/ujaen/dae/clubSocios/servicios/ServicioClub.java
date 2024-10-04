package es.ujaen.dae.clubSocios.servicios;

import es.ujaen.dae.clubSocios.entidades.Actividad;
import es.ujaen.dae.clubSocios.entidades.Socio;
import es.ujaen.dae.clubSocios.enums.EstadoCuota;
import org.springframework.stereotype.Service;

@Service
public class ServicioClub {
    public Socio crearSocio(/* parameters */) {
        throw new UnsupportedOperationException("Not supported yet.");
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
