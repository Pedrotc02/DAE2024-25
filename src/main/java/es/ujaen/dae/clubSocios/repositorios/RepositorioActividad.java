package es.ujaen.dae.clubSocios.repositorios;

import es.ujaen.dae.clubSocios.entidades.Actividad;
import es.ujaen.dae.clubSocios.entidades.Solicitud;
import es.ujaen.dae.clubSocios.excepciones.FechaNoValida;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;


import java.util.Optional;
import java.util.stream.Collectors;

@Transactional
@Repository
public class RepositorioActividad {
    @PersistenceContext
    EntityManager em;

    @Cacheable("actividades")
    @Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
    public Optional<Actividad> buscarPorId(int id) {
        return Optional.ofNullable(em.find(Actividad.class, id));
    }

    public void guardarActividad(Actividad actividad) {
        em.persist(actividad);
    }



}
