package es.ujaen.dae.clubSocios.enums;

/**
 * ABIERTA: la actividad está en período de inscripción ( el plazo ha empezado y no ha terminado ) y se pueden realizar solicitudes.
 * CERRADA: la fecha actual es anterior a la fecha de inicio de inscripción.
 *          No se pueden hacer solicitudes a esta actividad.
 * PLAZO_INSCRIPCION_FINALIZADO: la fecha actual es posterior a la fecha fin de inscripción.
 *          No se pueden hacer solicitudes a esta actividad
 */

public enum EstadoActividad {
    ABIERTA,
    CERRADA,
    PLAZO_INSCRIPCION_FINALIZADO
}
