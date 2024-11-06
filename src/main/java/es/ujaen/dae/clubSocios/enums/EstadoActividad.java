package es.ujaen.dae.clubSocios.enums;

/**
 * ABIERTA: la actividad está en período de inscripción ( el plazo ha empezado y no ha terminado ) y se pueden realizar solicitudes.
 * CERRADA: el período de inscripción de la actividad no es válido (ya sea porque la fecha actual es antes del inicio del plazo o después).
 *          No se pueden hacer solicitudes a esta actividad.
 */

public enum EstadoActividad {
    ABIERTA,
    CERRADA
}
