package es.ujaen.dae.clubSocios.entidades;

import es.ujaen.dae.clubSocios.enums.EstadoCuota;
import es.ujaen.dae.clubSocios.enums.EstadoSolicitud;
import jakarta.validation.constraints.*;
import org.springframework.cglib.core.Local;

import java.time.LocalDate;

public class Solicitud {
    @NotBlank
    private String solicitudId;
    @NotBlank
    private String socioId;
    @NotNull
    private Socio socio;
    @Min(0) @Max(5)
    private int numAcompanantes;

    @NotNull
    private EstadoSolicitud estadoSolicitud;
    @PastOrPresent
    private LocalDate fechaSolicitud;

    @Min(0) @Max(6)
    private int plazasConcedidas;

    public Solicitud(String socioId, Socio socio, int numAcompanantes) {
        this.socioId = socioId;
        this.socio = socio;
        this.numAcompanantes = numAcompanantes;
        //El id de la solicitud se crea en función del id del socio, el id de actividad y la fecha en la que se realiza
        this.solicitudId = generarSolicitudId();
        //Todas las solicitudes se crean con un estado pendiente
        this.estadoSolicitud = EstadoSolicitud.PENDIENTE;
        //La solicitud se crea a fecha actual del sistema
        this.fechaSolicitud = LocalDate.now();
        //Al crear la solicitud de por sí no se concede ninguna plaza, de eso se encarga la propia dirección
        this.plazasConcedidas = 0;

    }

    private String generarSolicitudId() {
        return this.socioId +"-"+ System.currentTimeMillis();
    }

    public void modificarNumAcompanantes(int nuevoNumAcompanantes) {
        if (nuevoNumAcompanantes < 0 || nuevoNumAcompanantes >= 5)
            throw new IllegalArgumentException("El número de acompañantes debe ser mayor que 0 y menor que 5");

        this.numAcompanantes = nuevoNumAcompanantes;
    }

    protected void concederPlaza() {
        plazasConcedidas++;
    }

    /**
     * Asigna el estado que le corresponde a cada solicitud.
     * Para ello, comprueba si el socio ha pagado y si hay plaza en la actividad, en caso contrario la solicitud será Pendiente.
     * Si estas condiciones son ciertas, asigna el estado de la solicitud teniendo en cuenta que:
     *      si el número total de plazas de la solicitud (acompañantes + socio) es mayor que 1 y
     *      si no se han concedido todas las plazas de la solicitud
     * La solicitud será Parcial en este caso, y Cerrada en caso contrario.
     */
    public void evaluarEstado(Actividad actividad) {
        int totalPlazas = numAcompanantes + 1;
        if (!socio.getEstadoCuota().equals(EstadoCuota.PAGADA) || !actividad.hayPlaza()) {
            this.estadoSolicitud = EstadoSolicitud.PENDIENTE;
        } else {
            this.estadoSolicitud = totalPlazas > 1 &&
                    plazasConcedidas >= 1 &&
                    plazasConcedidas <= totalPlazas  ? EstadoSolicitud.PARCIAL : EstadoSolicitud.CERRADA;
        }
    }

    // Getters
    public String getSocioId() {
        return socioId;
    }

    public String getSolicitudId() {
        return solicitudId;
    }

    public Socio getSocio() {
        return socio;
    }

    public int getNumAcompanantes() {
        return numAcompanantes;
    }

    public LocalDate getFechaSolicitud() {
        return fechaSolicitud;
    }

    public void setFechaSolicitud(LocalDate fechaSolicitud) {
        this.fechaSolicitud = fechaSolicitud;
    }

    public EstadoSolicitud getEstadoSolicitud() {
        return estadoSolicitud;
    }

    public void setEstadoSolicitud(EstadoSolicitud estadoSolicitud) {
        this.estadoSolicitud = estadoSolicitud;
    }

    public int getPlazasConcedidas() {
        return plazasConcedidas;
    }
}
