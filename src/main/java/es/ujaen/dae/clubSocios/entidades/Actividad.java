package es.ujaen.dae.clubSocios.entidades;

import es.ujaen.dae.clubSocios.enums.EstadoActividad;
import es.ujaen.dae.clubSocios.enums.EstadoCuota;
import es.ujaen.dae.clubSocios.enums.EstadoSolicitud;
import es.ujaen.dae.clubSocios.excepciones.ActividadYaRegistrada;
import es.ujaen.dae.clubSocios.excepciones.FueraDePlazo;
import es.ujaen.dae.clubSocios.util.UtilList;
import jakarta.persistence.*;
import es.ujaen.dae.clubSocios.excepciones.*;
import jakarta.validation.constraints.*;

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
    private Long id;
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
    private LocalDate fechaCelebracion;
    private LocalDate fechaInicioInscripcion;
    private LocalDate fechaFinInscripcion;
    @OneToMany(fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.REMOVE})
    private List<Solicitud> solicitudes;
    // Relación con Temporada
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "temporada_id")
    private Temporada temporada;

    @Version
    int version;

    public Actividad() {
        this.solicitudes = new ArrayList<>();
    }

    public Actividad(String titulo, String descripcion, double precio,
                     int totalPlazas, LocalDate fechaCelebracion, LocalDate fechaInicioInscripcion, LocalDate fechaFinInscripcion) {
        
        this.titulo = titulo;
        this.descripcion = descripcion;
        this.precio = precio;
        //las plazas disponibles inicialmente son iguales al total de plazas
        this.plazasDisponibles = totalPlazas;
        this.totalPlazas = totalPlazas;

        comprobarFechasActividad(fechaInicioInscripcion, fechaFinInscripcion, fechaCelebracion);
        this.fechaCelebracion = fechaCelebracion;
        this.fechaInicioInscripcion = fechaInicioInscripcion;
        this.fechaFinInscripcion = fechaFinInscripcion;
        this.solicitudes = new ArrayList<>();
    }

    public void agregarSolicitud(Solicitud solicitud) {
        solicitudes.add(solicitud);

    }

    public void quitarSolicitud(Solicitud solicitud) {
        solicitudes.remove(solicitud);
    }

    public List<Solicitud> revisarSolicitudes() {
//        if (estado() != EstadoActividad.PLAZO_INSCRIPCION_FINALIZADO) {
//            throw new FechaNoValida();
//        }

        return solicitudes.stream()
                .sorted(Comparator.comparing(Solicitud::getFechaSolicitud))
                .collect(Collectors.toList());
    }

    /**
     * Solicitud de un socio y sus acompañantes en la actividad.
     * Comprueba que la solicitud esté en período de inscripción, que el socio no haya hecho ya una solicitud en la actividad,
     * y que la solicitud tenga plazas disponible, con sus correspondientes excepciones.
     * Entonces, crea una solicitud nueva y asigna el estado a ésta según corresponda (socio pagado o no, actividad abierta o cerrada...).
     * Por último, añade la solicitud a la lista de solicitudes de la actividad y a la lista de solicitudes del socio.
     * @param socio socio que realiza la solicitud en la actividad
     * @param numAcompanantes numero de acompañantes que llevará el socio
     */
    public Solicitud solicitarInscripcion(Socio socio, @PositiveOrZero int numAcompanantes, boolean administrador) {
        if (!estaEnPeriodoInscripcion() && !administrador) {
            throw new FueraDePlazo();
        }

        if (!hayPlaza()) {
            throw new NoHayPlazas("No hay plazas disponibles en la actividad");
        }

        if (solicitudes.stream().anyMatch(s -> s.getSocioId().equals(socio.getSocioId()))) {
            throw new SolicitudYaRealizada();
        }

        Solicitud nuevaSolicitud = new Solicitud(socio, numAcompanantes);

        if (socio.getEstadoCuota().equals(EstadoCuota.PAGADA)) {
            asignarPlaza(nuevaSolicitud);
        }

        // Sin la línea de abajo, fallan los test de Socio (preguntar a profesor) y el de Actividad (testSolicitudInscripcionValida)
        // socio.anadirSolicitud(nuevaSolicitud);

        // Devolver la nueva solicitud para que sea persistida en el servicio
        return nuevaSolicitud;
    }

    /**
     * Asigna plazas de una en una en una actividad, siendo ésta una asignación equitativa.
     * Además, concede la plaza asignada a la solicitud que está siendo manejada.
     */
    public void asignarPlaza(Solicitud s) {
        if (!hayPlaza())
            throw new NoHayPlazas("No hay plazas disponibles en la actividad");

        plazasDisponibles--;
        s.concederPlaza();
        s.evaluarEstado(this);
    }

    /**
     * Esta es la asignación de plazas manual hecha por la dirección.
     * Lo primero es ver si hay plazas disponibles en la actividad y
     * si la solicitud que ha pasado la dirección está en la lista de solicitudes de ésta, y si no lanza las correspondientes excepciones.
     * Ahora vemos si la solicitud no está cerrada, porque si no no tiene sentido ver esta solicitud y la dirección puede pasar a otra.
     * Si no está cerrada, la dirección le da la plaza al socio, evalúa el nuevo estado de la solicitud, y concede una plaza a la solicitud.
     * Como las plazas concedidas pueden ser 6 (incluye al socio) no tenemos por qué preocuparnos de si esa plaza es el socio o no.
     * @param solicitud solicitud en la que se van a asignar las plazas.
     */
    public void asignarPlazasFinal(Solicitud solicitud) {

        if (estado() != EstadoActividad.PLAZO_INSCRIPCION_FINALIZADO) {
            throw new FueraDePlazo();
        }

        if (!hayPlaza())
            throw new NoHayPlazas("No hay plazas disponibles en la actividad");

        if (!solicitudExiste(solicitud.getSolicitudId()))
            throw new SolicitudNoExiste();

        if (solicitud.getEstadoSolicitud().equals(EstadoSolicitud.CERRADA)) {
            throw new ActividadYaRegistrada();
        }

        asignarPlaza(solicitud);
    }

    /**
     * Esta es la asignación automática, esto debe hacerlo al final, es la parte voluntaria.
     * Hace una primera vuelta para las solicitudes parciales y una segunda vuelta para las solicitudes pendientes.
     * En la segunda vuelta, no deberían quedar solicitudes parciales,
     * ya que si sigue habiendo plazas, deberían haber sido asignadas todas las de este tipo de solicitudes.
     * Por tanto, sólo habrá pendientes y cerradas, entonces si se intenta acceder a una solicitud cerrada se pasará a la siguiente hasta que haya una pendiente
     */
    public void asignarPlazasFinInscripcion(boolean administrador) {

        if (estado() != EstadoActividad.ABIERTA && !administrador)
            throw new FueraDePlazo();

        if (!hayPlaza())
            throw new NoHayPlazas("No hay plazas disponibles en la actividad");

        solicitudes.stream()
                   .filter(s -> s.getEstadoSolicitud().equals(EstadoSolicitud.PARCIAL) && hayPlaza())
                   .forEach(this::asignarPlaza);

        solicitudes.stream()
                   .filter(s -> !s.getEstadoSolicitud().equals(EstadoSolicitud.CERRADA) && hayPlaza())
                   .forEach(this::asignarPlaza);
    }

    /**
     * Comprueba si la actividad está en periodo de inscripción.
     * @return true si está en período de inscripción y false si no.
     */
    private boolean estaEnPeriodoInscripcion() {
        LocalDate now = LocalDate.now();
        return !now.isBefore(fechaInicioInscripcion) && !now.isAfter(fechaFinInscripcion);
    }

    /**
     * Usando la fecha y hora actual del sistema, devuelve el estado que le corresponde a la actividad
     * @return el estado de la actividad
     */
    public EstadoActividad estado() {
        if (LocalDate.now().isBefore(fechaInicioInscripcion))
            return EstadoActividad.CERRADA;
        if (LocalDate.now().isAfter(fechaFinInscripcion))
            return EstadoActividad.PLAZO_INSCRIPCION_FINALIZADO;
        return EstadoActividad.ABIERTA;
    }

    /**
     * Comprueba si las fechas a asignar son válidas
     * @param ini fecha inicio de actividad
     * @param fin fecha fin de actividad
     * @param cel fecha de celebración de la actividad
     */
    private void comprobarFechasActividad(@NotNull LocalDate ini,@NotNull LocalDate fin,@NotNull LocalDate cel) {
        int actual = LocalDate.now().getYear();
        if (ini.getYear() != actual && fin.getYear() != actual && cel.getYear() != actual)
            throw new InvalidoAnio();

        if (!fin.isAfter(ini))
            throw new FechaNoValida();

        if (!cel.isAfter(fin))
            throw new FechaNoValida();
    }

    // Getters
    public Long getId() {
        return id;
    }

    public int getPlazasDisponibles() {
        return plazasDisponibles;
    }

    public String getTitulo() {
        return titulo;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public double getPrecio() {
        return precio;
    }

    public int getTotalPlazas() {
        return totalPlazas;
    }

    public LocalDate getFechaCelebracion() {
        return fechaCelebracion;
    }

    public LocalDate getFechaInicioInscripcion() {
        return fechaInicioInscripcion;
    }

    public LocalDate getFechaFinInscripcion() {
        return fechaFinInscripcion;
    }

    public List<Solicitud> getSolicitudes() {
        return solicitudes;
    }

    public boolean hayPlaza() {
        return plazasDisponibles >= 1;
    }

    public void setPlazasDisponibles(@PositiveOrZero int plazasDisponibles) {
        this.plazasDisponibles = plazasDisponibles;
    }

    public Temporada getTemporada() {
        return temporada;
    }

    public void setTemporada(Temporada temporada) {
        this.temporada = temporada;
    }

    public boolean solicitudExiste(String solicitudId){
        for (int i = 0; i < solicitudes.size(); i++) {
            if(solicitudes.get(i).getSolicitudId().equals(solicitudId)){
                return true;
            }
        }
        return false;
    }
}

