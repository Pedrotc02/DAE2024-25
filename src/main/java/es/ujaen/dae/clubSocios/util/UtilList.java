package es.ujaen.dae.clubSocios.util;

import es.ujaen.dae.clubSocios.entidades.Actividad;

import java.util.Collections;
import java.util.List;

public class UtilList {
    public static void ordenarListaPorFecha(Actividad a) {
        Collections.sort(a.getSolicitudes(), (s1, s2) -> s1.getFechaSolicitud().compareTo(s2.getFechaSolicitud()));
    }
}