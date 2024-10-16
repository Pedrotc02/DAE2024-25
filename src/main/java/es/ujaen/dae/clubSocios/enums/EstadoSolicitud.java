package es.ujaen.dae.clubSocios.enums;

/*
 * PENDIENTE: no se ha asignado ninguna plaza de la solicitud, nisiquiera la del socio (ya sea porque su estado de cuota sea pendiente u otro motivo)
 * PARCIAL: se ha asignado la plaza del socio. Estas solicitudes tendrán prioridad sobre las solicitudes PENDIENTES.
 * CERRADA: se han asignado todas las plazas exitosamente de la solicitud.
 * EN_ESPERA: la solicitud no se ha aceptado, se deja para cuando se acabe el período de inscripción y lo haga la dirección(no hay plazas en la actividad, por ejemplo).
 */

public enum EstadoSolicitud {
    PENDIENTE,
    PARCIAL,
    CERRADA,
    EN_ESPERA;
}
