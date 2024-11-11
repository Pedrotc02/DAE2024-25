package es.ujaen.dae.clubSocios.entidades;

import es.ujaen.dae.clubSocios.excepciones.InvalidoAnio;
import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;

import java.time.LocalDate;
import java.util.SortedMap;
import java.util.TreeMap;

@Entity
public class Temporada {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int temporadaId;
    @Min(2000)
    @Max(9999)
    private int anio;
    @OneToMany
    private SortedMap<Integer, Actividad> actividades;

    public Temporada(){

    }

    public Temporada(int temporadaId, int anio) {
        this.temporadaId = temporadaId;
        this.anio = anio;
        this.actividades = new TreeMap<>();
    }

    public void aniadirActividad(Actividad actividad) {
        actividades.put(actividad.getId(), actividad);
    }

    public SortedMap<Integer, Actividad> obtenerActividades() {
        return new TreeMap<>(actividades);
    }

    // Getters
    public int getTemporadaId() {
        return temporadaId;
    }

    public int getAnio() {
        return anio;
    }

    public void setAnio(int anio){
        if (anio > LocalDate.EPOCH.getYear())
            throw new InvalidoAnio();
        this.anio = anio;
    }
}
