package es.ujaen.dae.clubSocios.entidades;

import es.ujaen.dae.clubSocios.enums.EstadoActividad;
import es.ujaen.dae.clubSocios.enums.EstadoSolicitud;
import es.ujaen.dae.clubSocios.excepciones.PlazasNoDisponibles;
import jakarta.validation.constraints.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Actividad {
    @NotBlank
    private String id;
    @NotBlank
    private String titulo;
    @NotBlank
    private String descripcion;
    @Positive
    private double precio;
    @PositiveOrZero
    private int plazasDisponibles;
    @Positive
    private int totalPlazas;
    @Future
    private LocalDate fechaCelebracion;
    @FutureOrPresent
    private LocalDate fechaInicioInscripcion;
    @Future
    private LocalDate fechaFinInscripcion;
    private List<Solicitud> solicitudes;

    public Actividad() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public Actividad(String id, String titulo, String descripcion, double precio,
                     int totalPlazas, LocalDate fechaCelebracion, LocalDate fechaInicioInscripcion, LocalDate fechaFinInscripcion) {
        this.id = id;
        this.titulo = titulo;
        this.descripcion = descripcion;
        this.precio = precio;
        //las plazas disponibles inicialmente son iguales al total de plazas
        this.plazasDisponibles = totalPlazas;
        this.totalPlazas = totalPlazas;
        this.fechaCelebracion = fechaCelebracion;
        this.fechaInicioInscripcion = fechaInicioInscripcion;
        this.fechaFinInscripcion = fechaFinInscripcion;
        this.solicitudes = new ArrayList<>();
    }

    public void agregarSolicitud(Solicitud solicitud) {
        solicitudes.add(solicitud);
    }

    // TODO: hacer esto por solicitudId
    public void quitarSolicitud(Solicitud solicitud) {
        solicitudes.remove(solicitud);
    }

    public void asignarPlazas(int numPlazasPedidas) {
        // TODO: ver si este metodo sirve para algo o quitarlo
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public boolean estaEnPeriodoInscripcion() {
        return getEstado() == EstadoActividad.ABIERTA;
    }

    // Getters
    public String getId() {
        return id;
    }

    public @NotBlank String getTitulo() {
        return titulo;
    }

    public @NotBlank String getDescripcion() {
        return descripcion;
    }

    @Positive
    public double getPrecio() {
        return precio;
    }

    @PositiveOrZero
    public int getPlazasDisponibles() {
        return plazasDisponibles;
    }

    public @Future LocalDate getFechaCelebracion() {
        return fechaCelebracion;
    }

    public @FutureOrPresent LocalDate getFechaInicioInscripcion() {
        return fechaInicioInscripcion;
    }

    public @Future LocalDate getFechaFinInscripcion() {
        return fechaFinInscripcion;
    }

    public List<Solicitud> getSolicitudes() {
        return solicitudes;
    }

    public EstadoActividad getEstado() {
        LocalDate fechaActual = LocalDate.now();
        if (fechaActual.isBefore(fechaInicioInscripcion)) {
            return EstadoActividad.PENDIENTE;
        } else if (fechaActual.isBefore(fechaFinInscripcion)) {
            return EstadoActividad.ABIERTA;
        } else if (fechaActual.isBefore(fechaCelebracion)) {
            return EstadoActividad.CERRADA;
        } else if (fechaActual.isEqual(fechaCelebracion)) {
            return EstadoActividad.EN_CURSO;
        } else {
            return EstadoActividad.FINALIZADA;
        }
    }

    public void setPlazasDisponibles(@PositiveOrZero int plazasDisponibles) {
        this.plazasDisponibles = plazasDisponibles;
    }
}

