package es.ujaen.dae.clubSocios.repositorios;

import es.ujaen.dae.clubSocios.entidades.Socio;
import es.ujaen.dae.clubSocios.enums.EstadoCuota;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@Transactional
@Repository
public class RepositorioSocio {
    @PersistenceContext
    EntityManager em;

    @Cacheable("socios")
    @Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
    public Optional<Socio> buscarPorId(String id) {
        return Optional.ofNullable(em.find(Socio.class, id));
    }

    public void guardarSocio(Socio socio) {
        em.persist(socio);
    }

    @Cacheable("socios")
    public Socio actualizarEstadoCuota(String email, EstadoCuota estadoCuota) {
        Optional<Socio> socioOptional = buscarPorId(email);

        if (socioOptional.isPresent()) {
            Socio socio = socioOptional.get();
            socio.setEstadoCuota(estadoCuota);

            return em.merge(socio);
        } else {
            throw new NoSuchElementException();
        }
    }

    @Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
    public List<String> listadoIds() {
        return em.createQuery("select s.socioId from Socio s").getResultList();
    }

}
