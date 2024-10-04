package es.ujaen.dae.clubSocios.entidades;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

import java.util.SortedMap;
import java.util.TreeMap;

public class Temporada {
    @NotBlank
    private String temporadaId;
    @Min(2000)
    @Max(9999)
    private int anio;
    private SortedMap<String, Actividad> actividades;

    public Temporada(String temporadaId, int anio) {
        this.temporadaId = temporadaId;
        this.anio = anio;
        this.actividades = new TreeMap<>();
    }

    public void aniadirActividad(Actividad actividad) {
        actividades.put(actividad.getId(), actividad);
    }

    public SortedMap<String, Actividad> obtenerActividades() {
        return new TreeMap<>(actividades);
    }

    // Getters
    public String getTemporadaId() {
        return temporadaId;
    }

    public int getAnio() {
        return anio;
    }
}
