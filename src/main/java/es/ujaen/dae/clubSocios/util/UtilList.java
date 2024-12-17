package es.ujaen.dae.clubSocios.util;

import es.ujaen.dae.clubSocios.entidades.Actividad;
import es.ujaen.dae.clubSocios.entidades.Socio;
import es.ujaen.dae.clubSocios.enums.EstadoCuota;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Collections;

public class UtilList {
    public static final Socio EJEMPLO_SOCIO = new Socio("direccion@clubsocios.es", "direccion",
            "-", "99999999Z", "953897654", "$2a$10$231.hQz4p2Db4dgtjFA.kOequXNQDAdCfxXBB8009BT15/PfQjJfa",
            EstadoCuota.PAGADA);
}