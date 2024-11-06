package es.ujaen.dae.clubSocios.enums;

/**
 * PENDIENTE: no se ha asignado ninguna plaza de la solicitud, ni siquiera la del socio (ya sea porque su estado de cuota sea pendiente u otro motivo)
 * PARCIAL: se ha asignado la plaza del socio, ó uno o varios acompañantes. Estas solicitudes tendrán prioridad sobre las solicitudes PENDIENTES.
 * CERRADA: se han asignado todas las plazas exitosamente de la solicitud, por lo que al valorar la solicitud, no se le tendrá que asignar nada más.
 */

public enum EstadoSolicitud {
    PENDIENTE,
    PARCIAL,
    CERRADA;
}
