package es.ujaen.dae.clubSocios.excepciones;

public class TemporadaYaRegistrada extends RuntimeException {
    public TemporadaYaRegistrada(String message) {
        super(message);
    }
}
