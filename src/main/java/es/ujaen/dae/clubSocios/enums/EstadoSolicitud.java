package es.ujaen.dae.clubSocios.enums;

public enum EstadoSolicitud {
    CONFIRMADA, // Hay espacio para el socio y todos los acompañantes
    PENDIENTE, // Hay espacio para el socio pero no para todos los acompañantes
    RECHAZADA, // No hay plazas disponibles
    INVALIDA // Fuera del periodo de inscripcion
}
