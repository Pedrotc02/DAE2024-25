package es.ujaen.dae.clubSocios.excepciones;

public class NoHayPlazas extends RuntimeException{
    public NoHayPlazas(String message) {
        super(message);
    }
}
