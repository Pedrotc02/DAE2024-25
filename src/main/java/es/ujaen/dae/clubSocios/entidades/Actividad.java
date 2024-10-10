package es.ujaen.dae.clubSocios.entidades;

import es.ujaen.dae.clubSocios.enums.EstadoActividad;
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

    @PositiveOrZero
    private int totalPlazas;
    @Future
    private LocalDate fechaCelebracion;
    @FutureOrPresent
    private LocalDate fechaInicioInscripcion;
    @Future
    private LocalDate fechaFinInscripcion;
    private List<Solicitud> solicitudes;
    private EstadoActividad estado;

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
        this.estado = EstadoActividad.ABIERTA;
    }

    public void agregarSolicitud(Solicitud solicitud) {
        solicitudes.add(solicitud);
    }

    public void quitarSolicitud(Solicitud solicitud) {
        solicitudes.remove(solicitud);
    }

    public void asignarPlazas() {
        if (plazasDisponibles > 0)
            throw new PlazasNoDisponibles("No hay plazas disponibles para esta actividad");
        // decrementar las plazas en 1 al asignarlas (de momento aquí, seguramente acabe estando en el servicio)
        plazasDisponibles--;
    }

    public boolean estaEnPeriodoInscripcion() {
        LocalDate now = LocalDate.now();
        return !now.isBefore(fechaInicioInscripcion) && !now.isAfter(fechaFinInscripcion);
    }

    public void cambiarEstado(EstadoActividad nuevoEstado) {
        this.estado = nuevoEstado;
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
        return estado;
    }

    public void setPlazasDisponibles(@PositiveOrZero int plazasDisponibles) {
        this.plazasDisponibles = plazasDisponibles;
    }


}

//    EstadoActividad estadoActual() {
//        var fechaActual = LocalDateTime.now();
//
//        if (fechaActual.isBefore(fechaInicioInscripcion))
//            return PENDIENTE;
//        else if (fechaActual.isBefore(fechaFinInscripcion))
//            return PERIODO_INSCRIPCION;
//    ...
//    }
