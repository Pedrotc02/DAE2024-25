package es.ujaen.dae.clubSocios.entidades;

import es.ujaen.dae.clubSocios.enums.EstadoCuota;

import es.ujaen.dae.clubSocios.enums.EstadoSolicitud;
import jakarta.validation.constraints.*;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

import java.util.ArrayList;
import java.util.List;


public class Socio {

    @NotBlank
    @Pattern(regexp = "^\\d{8}[TRWAGMYFPDXBNJZSQVHLCKE]$", message = "DNI no válido")
    private String socioId;
    @NotBlank
    private String nombre;
    @NotBlank
    private String apellidos;
    @Email
    private String email;
    @NotBlank
    @Pattern(regexp = "^(\\+34|0034|34)?[6789]\\d{8}$", message = "No es un número de teléfono válido")
    private String tlf;
    @NotBlank
    @Pattern(regexp = "^[a-zA-Z0-9]{6,}$", message = "La clave debe tener más de 5 caracteres")
    private String claveAcceso;
    @NotNull
    private EstadoCuota estadoCuota;

    List<Solicitud> solicitudes;

    public Socio(String socioId, String nombre, String apellidos, String email, String tlf, String claveAcceso,
                 EstadoCuota estadoCuota) {
        this.socioId = socioId;
        this.nombre = nombre;
        this.apellidos = apellidos;
        this.email = email;
        this.tlf = tlf;
        this.claveAcceso = claveAcceso;
        this.estadoCuota = estadoCuota;
        this.solicitudes = new ArrayList<>();
    }

    public void solicitarInscripcion(Actividad actividad, @PositiveOrZero int numAcompanantes) {

        //Primero se comprueba si hay hueco en la actividad, si no hay la solicitud es cancelada
        if (actividad.hayPlazas()){
            //Si hay hueco, se comprueba si el socio ha pagado la cuota, si no la solicitud estará en pendiente
            if(this.getEstadoCuota().equals(EstadoCuota.PAGADA)){
                //Se ocupa un lugar de las plazas
                actividad.asignarPlazas(1);

                //Si hay mas de un acompañante la solicitud se pasa a parcial para una posterior revision
                if(numAcompanantes > 0){
                    String solicitudId = this.socioId + "-" + System.currentTimeMillis();
                    solicitudes.add(new Solicitud(solicitudId, this.socioId, this, numAcompanantes, EstadoSolicitud.PARCIAL));
                }
                //Si es un unico participante se cierra la solicitud como confirmada
                String solicitudId = this.socioId + "-" + System.currentTimeMillis();
                solicitudes.add(new Solicitud(solicitudId, this.socioId, this, numAcompanantes, EstadoSolicitud.CERRADA));
            }
            String solicitudId = this.socioId + "-" + System.currentTimeMillis();
            solicitudes.add(new Solicitud(solicitudId, this.socioId, this, numAcompanantes, EstadoSolicitud.PENDIENTE));
        } else {
            String solicitudId = this.socioId + "-" + System.currentTimeMillis();
            solicitudes.add(new Solicitud(solicitudId, this.socioId, this, numAcompanantes, EstadoSolicitud.CANCELADA));
        }
    }

    public void modificarSolicitud(String solicitudId, int numAcompanantes) {
        for (int i = 0; i < solicitudes.size(); i++) {
            solicitudes.get(i).setNumAcompanantes(numAcompanantes);
        }
    }

    public void borrarSolicitud(String solicitudId) {
        for (int i = solicitudes.size() -1; i >= 0; i--) {
            if(solicitudes.get(i).getSolicitudId().equals(solicitudId)){
                solicitudes.remove(i);
            }
        }
    }

    public void anadirSolicitud(Solicitud solicitud){
        solicitudes.add(solicitud);
    }

    // Getters
    public String getSocioId() {
        return socioId;
    }

    public String getNombre() {
        return nombre;
    }

    public String getApellidos() {
        return apellidos;
    }

    public String getEmail() {
        return email;
    }

    public String getTlf() {
        return tlf;
    }

    public String getClaveAcceso() {
        return claveAcceso;
    }

    public EstadoCuota getEstadoCuota() {
        return estadoCuota;
    }

    // Setters

    public void setEstadoCuota(EstadoCuota estadoCuota) {
        this.estadoCuota = estadoCuota;
    }
}
