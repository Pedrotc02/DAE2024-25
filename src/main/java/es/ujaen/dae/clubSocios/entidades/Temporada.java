package es.ujaen.dae.clubSocios.entidades;

import java.util.SortedMap;
import java.util.TreeMap;

public class Temporada {
    private String temporadaId;
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
