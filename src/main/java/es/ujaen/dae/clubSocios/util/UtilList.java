package es.ujaen.dae.clubSocios.util;

import es.ujaen.dae.clubSocios.entidades.Actividad;
import es.ujaen.dae.clubSocios.entidades.Socio;
import es.ujaen.dae.clubSocios.enums.EstadoCuota;

import java.util.Collections;

public class UtilList {
    public static final Socio EJEMPLO_SOCIO = new Socio("direccion@clubsocios.es", "direccion",
            "-", "99999999Z", "953897654", "serviceSecret", EstadoCuota.PAGADA);
}