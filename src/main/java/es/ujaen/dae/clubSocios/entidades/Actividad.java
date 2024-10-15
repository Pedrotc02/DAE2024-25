package es.ujaen.dae.clubSocios.entidades;

import es.ujaen.dae.clubSocios.enums.EstadoActividad;
import es.ujaen.dae.clubSocios.enums.EstadoSolicitud;
import es.ujaen.dae.clubSocios.excepciones.FechaFinInscripcionNoValida;
import es.ujaen.dae.clubSocios.excepciones.PlazasNoDisponibles;
import jakarta.validation.constraints.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

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

    @NotNull

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
        if (estado != EstadoActividad.ABIERTA) {
            throw new IllegalStateException("No se pueden agregar solicitudes cuando la actividad no está abierta.");
        }
        solicitudes.add(solicitud);
    }

    public void quitarSolicitud(Solicitud solicitud) {
        if (estado != EstadoActividad.ABIERTA) {
            throw new IllegalStateException("No se pueden quitar solicitudes cuando la actividad no está abierta.");
        }
        solicitudes.remove(solicitud);
    }


    public List<Solicitud> revisarSolicitudes() {
        LocalDate now = LocalDate.now();
        if (now.isBefore(fechaFinInscripcion)) {
            throw new FechaFinInscripcionNoValida();
        }
        return solicitudes.stream()
                .sorted(Comparator.comparing(Solicitud::getFechaSolicitud))
                .collect(Collectors.toList());
    }


    public void asignarPlazasFinInscripcion() {
        // Primera vuelta para parciales
        for (Solicitud solicitud : solicitudes) {
            if (solicitud.getEstadoSolicitud() == EstadoSolicitud.PARCIAL && hayPlazas(solicitud.getNumAcompanantes() + 1)) {
                asignarPlazas(solicitud.getNumAcompanantes() + 1);
                solicitud.setEstadoSolicitud(EstadoSolicitud.CERRADA);
            }
        }

        // Segunda vuelta para el resto
        for (Solicitud solicitud : solicitudes) {
            if (solicitud.getEstadoSolicitud() == EstadoSolicitud.PENDIENTE && hayPlazas(solicitud.getNumAcompanantes() + 1)) {
                asignarPlazas(solicitud.getNumAcompanantes() + 1);
                solicitud.setEstadoSolicitud(EstadoSolicitud.CERRADA);
            }
        }
    }


    public void asignarPlazas(int numPlazas) {
        if (numPlazas > plazasDisponibles)
            throw new PlazasNoDisponibles("No hay plazas disponibles para la actividad: " + this.titulo);

        plazasDisponibles -= numPlazas;
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

    public boolean hayPlazas() {
        return plazasDisponibles > 0;
    }

    boolean hayPlazas(int numPlazasSolicitadas) {
        return plazasDisponibles >= numPlazasSolicitadas;
    }

    public void setPlazasDisponibles(@PositiveOrZero int plazasDisponibles) {
        this.plazasDisponibles = plazasDisponibles;
    }
}

