package es.ujaen.dae.clubSocios.repositorios;

import es.ujaen.dae.clubSocios.entidades.Solicitud;
import jakarta.persistence.EntityManager;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

import jakarta.persistence.PersistenceContext;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@Repository
public class RepositorioSolicitud {
    @PersistenceContext
    EntityManager em;

    @Caching(evict={
            @CacheEvict(value="solicitudes", key="#solicitud.solicitudId()")
    })

    public Solicitud actualizar(Solicitud solicitud){
        return em.merge(solicitud);
    }
}
