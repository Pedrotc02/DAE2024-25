package es.ujaen.dae.clubSocios.excepciones;

public class ConflictoDeConcurrenciaException extends RuntimeException{
    public ConflictoDeConcurrenciaException(String message){
        super(message);
    }
}
