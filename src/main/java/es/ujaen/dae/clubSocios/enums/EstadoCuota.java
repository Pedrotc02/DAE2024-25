package es.ujaen.dae.clubSocios.enums;

/**
 * PAGADA: el socio ha pagado la cuota por lo que tendrá prioridad sobre los socios que la cuota la tengan en estado PENDIENTE.
 * PENDIENTE: el socio no ha pagado la cuota, así que no tendrá ninguna prioridad, ni él ni sus acompañantes.
 */

public enum EstadoCuota {
    PAGADA,
    PENDIENTE;
}