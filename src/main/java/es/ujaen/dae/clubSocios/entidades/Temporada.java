package es.ujaen.dae.clubSocios.entidades;

import es.ujaen.dae.clubSocios.excepciones.InvalidoAnio;
import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

@Entity
public class Temporada {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long temporadaId;

    @Min(2000)
    @Max(9999)
    private int anio;

    @OneToMany(fetch = FetchType.LAZY)
    @OrderBy("id ASC") // Orden ascendente por ID
    private List<Actividad> actividades;

    public Temporada(){
        this.actividades = new ArrayList<>();
    }

    public Temporada(Long temporadaId, int anio) {
        this.temporadaId = temporadaId;
        this.anio = anio;
        this.actividades = new ArrayList<>();
    }

    public void aniadirActividad(Actividad actividad) {
        actividades.add(actividad);
    }

    public List<Actividad> obtenerActividades() {
        return new ArrayList<>(actividades);
    }

    // Getters
    public Long getTemporadaId() {
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
