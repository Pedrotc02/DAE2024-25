package es.ujaen.dae.clubSocios.util;

import es.ujaen.dae.clubSocios.entidades.Actividad;
import es.ujaen.dae.clubSocios.entidades.Socio;
import es.ujaen.dae.clubSocios.enums.EstadoCuota;

import java.util.Collections;

public class UtilList {
    public static void ordenarListaPorFecha(Actividad a) {
        Collections.sort(a.getSolicitudes(), (s1, s2) -> s1.getFechaSolicitud().compareTo(s2.getFechaSolicitud()));
    }

    public static final Socio EJEMPLO_SOCIO = new Socio("direccion@clubsocios.es", "direccion",
            "-", "99999999Z", "953897654", "serviceSecret", EstadoCuota.PAGADA);
}