package es.ujaen.dae.clubSocios.rest.dto;

import es.ujaen.dae.clubSocios.enums.EstadoCuota;

///Al ser record, incluye constructores, getters e equals, pero es de sólo lectura.

public record DTOSocio(
    String id,
    String nombre,
    String apellidos,
    String dni,
    String tlf,
    String claveAcceso,
    EstadoCuota cuota) {
}
