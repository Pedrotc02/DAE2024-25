package es.ujaen.dae.clubSocios.enums;

/*
 * PENDIENTE: no se ha asignado ninguna plaza de la solicitud, nisiquiera la del socio (ya sea porque su estado de cuota sea pendiente u otro motivo)
 * PARCIAL: se ha asignado la plaza del socio. Estas solicitudes tendrán prioridad sobre las solicitudes PENDIENTES.
 * CERRADA: se han asignado todas las plazas exitosamente de la solicitud.
 * CANCELADA: la solicitud no se ha aceptado (no hay plazas en la actividad, por ejemplo).
 */
public enum EstadoSolicitud {
    PENDIENTE,
    PARCIAL,
    CERRADA,
    CANCELADA;
}
