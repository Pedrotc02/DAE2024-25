package es.ujaen.dae.clubSocios.excepciones;

public class FueraDePlazo extends RuntimeException {
    public FueraDePlazo(String mensaje) {
        super(mensaje);
    }
}
