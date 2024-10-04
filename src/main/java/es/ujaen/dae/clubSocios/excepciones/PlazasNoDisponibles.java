package es.ujaen.dae.clubSocios.excepciones;

public class PlazasNoDisponibles extends RuntimeException{
    public PlazasNoDisponibles(String mensaje) {
        super(mensaje);
    }
}
