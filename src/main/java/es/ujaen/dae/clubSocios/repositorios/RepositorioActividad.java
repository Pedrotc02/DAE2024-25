package es.ujaen.dae.clubSocios.repositorios;

import es.ujaen.dae.clubSocios.entidades.Actividad;
import es.ujaen.dae.clubSocios.entidades.Solicitud;
import es.ujaen.dae.clubSocios.excepciones.ActividadYaRegistrada;
import es.ujaen.dae.clubSocios.excepciones.FechaNoValida;
import es.ujaen.dae.clubSocios.excepciones.SolicitudNoExiste;
import es.ujaen.dae.clubSocios.excepciones.SolicitudYaRealizada;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

import java.time.LocalDate;
import java.util.*;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.dao.DuplicateKeyException;
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

    @Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
    public Optional<Actividad> buscarPorId(Long id) {
        return Optional.ofNullable(em.find(Actividad.class, id));
    }

    public void guardarActividad(Actividad actividad) {
        try {
            em.persist(actividad);
            em.flush();
        } catch (DuplicateKeyException duplicateKeyException) {
            throw new ActividadYaRegistrada();
        }
    }

    public Actividad actualizar(Actividad actividad) {
        return em.merge(actividad);
    }

    public void eliminar(Actividad actividad) {
        em.remove(em.merge(actividad));
    }

    // listadoIDs
    @Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
    public List<Long> listadoIds() {
        return em.createQuery("select a.id from Actividad a", Long.class).getResultList();
    }

    // listadoActividades
    @Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
    public List<Actividad> listadoActividades() {
        return em.createQuery("select a from Actividad a", Actividad.class).getResultList();
    }

    //listadoSolicitudes
    @Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
    public List<Solicitud> listadoSolicitudes(Long actividadId) {
        Optional<Actividad> a = buscarPorId(actividadId);

        return buscarPorId(actividadId)
                .map(Actividad::getSolicitudes)
                .orElse(Collections.emptyList());
    }

    @Transactional
    public void guardarSolicitud(String socioId, Solicitud solicitud, Long actividadId) {
        em.persist(solicitud);
    }

    //borrar una solicitud (necesita un socio y una actividad, además de la propia solicitud)
    public void eliminarSolicitud(String socioId, Solicitud solicitud, Long actividadId) {
        List<Solicitud> solicitudes = listadoSolicitudes(actividadId);

        boolean existeSolicitud = solicitudes.stream().anyMatch(s -> s.getSocioId().equals(socioId));

        if (!existeSolicitud)
            throw new SolicitudNoExiste();

        em.remove(em.merge(solicitud));
    }

    public Solicitud actualizarSolicitud(Solicitud solicitud) {
        return em.merge(solicitud);
    }

    public void refrescar() {
        em.flush();
    }
}
