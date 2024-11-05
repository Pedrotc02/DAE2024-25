package es.ujaen.dae.clubSocios.entidades;

import es.ujaen.dae.clubSocios.enums.EstadoActividad;
import es.ujaen.dae.clubSocios.enums.EstadoCuota;
import es.ujaen.dae.clubSocios.enums.EstadoSolicitud;
import es.ujaen.dae.clubSocios.excepciones.ActividadYaRegistrada;
import es.ujaen.dae.clubSocios.excepciones.FechaFinInscripcionNoValida;
import es.ujaen.dae.clubSocios.excepciones.FueraDePlazo;
import es.ujaen.dae.clubSocios.excepciones.PlazasNoDisponibles;
import es.ujaen.dae.clubSocios.util.UtilList;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import org.springframework.cglib.core.Local;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Entity
public class Actividad {
    @Positive
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
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
    @OneToMany(mappedBy = "actividad")
    List<Solicitud> solicitudes;

    public Actividad() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public Actividad(String id, String titulo, String descripcion, double precio,
                     int totalPlazas, LocalDate fechaCelebracion, LocalDate fechaInicioInscripcion, LocalDate fechaFinInscripcion) {

        if (!fechaFinInscripcion.isAfter(fechaInicioInscripcion)) {
            throw new IllegalArgumentException("La fecha de fin de inscripción debe ser posterior a la fecha de inicio de inscripción.");
        }

        if (!fechaCelebracion.isAfter(fechaFinInscripcion)) {
            throw new IllegalArgumentException("La fecha de celebración debe ser posterior a la fecha de fin de inscripción.");
        }

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
        if (!estaEnPeriodoInscripcion()) {
            throw new FueraDePlazo("No se pueden agregar solicitudes cuando la actividad no está abierta.");
        }
        solicitudes.add(solicitud);
    }

    public void quitarSolicitud(Solicitud solicitud) {
        if (!estaEnPeriodoInscripcion()) {
            throw new FueraDePlazo("No se pueden quitar solicitudes cuando la actividad no está abierta.");
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

    public void solicitarInscripcion(Socio socio, @PositiveOrZero int numAcompanantes) {
        if (!estaEnPeriodoInscripcion()) {
            throw new FueraDePlazo("Inscripción fuera de plazo");
        }

        //Si el socio ya ha hecho una solicitud en la actividad dada
        for (var solicitud: getSolicitudes()) {
            if (solicitud.getSocioId().equals(socio.getSocioId()))
                throw new ActividadYaRegistrada();
        }

        if (plazasDisponibles == 0)
            throw new PlazasNoDisponibles("No hay plazas disponibles para la actividad");

        //crear y asignar el estado de la solicitud
        Solicitud nuevaSolicitud = new Solicitud(socio.getSocioId(), socio, numAcompanantes);

        // no creo que haya que evaluar el estado de la solicitud al solicitar la inscripción, creo que sería mejor
        // cuando la dirección se encargue de asignar plazas manualmente, porque es cuando realmente se puede poner la solicitud a parcial o cerrada
        nuevaSolicitud.evaluarEstado(1 + numAcompanantes);

        //añadir la solicitud a la actividad y al socio
        agregarSolicitud(nuevaSolicitud);
        solicitudes.add(nuevaSolicitud);
    }

    public void asignarPlazasFinInscripcion() {

        if (LocalDate.now().isBefore(fechaFinInscripcion))
            throw new FueraDePlazo("La fecha de inscripción no ha finalizado");

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

    /**
     * Usando la fecha y hora actual del sistema, devuelve el estado que le corresponde a la actividad
     * @return el estado que corresponda a la actividad
     */
    public EstadoActividad estado() {
        if (estaEnPeriodoInscripcion())
            return EstadoActividad.ABIERTA;
        return EstadoActividad.CERRADA;
    }

    // Getters
    public String getId() {
        return id;
    }

    public int getPlazasDisponibles() {
        return plazasDisponibles;
    }

    public List<Solicitud> getSolicitudes() {
        return solicitudes;
    }

    boolean hayPlazas(int numPlazasSolicitadas) {
        return plazasDisponibles >= numPlazasSolicitadas;
    }

    public void setPlazasDisponibles(@PositiveOrZero int plazasDisponibles) {
        this.plazasDisponibles = plazasDisponibles;
    }
}

