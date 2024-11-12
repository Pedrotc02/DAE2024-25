package es.ujaen.dae.clubSocios.repositorios;

import es.ujaen.dae.clubSocios.entidades.Socio;
import es.ujaen.dae.clubSocios.enums.EstadoCuota;
import es.ujaen.dae.clubSocios.excepciones.SocioYaRegistrado;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.dao.DuplicateKeyException;
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
        try {
            em.persist(socio);
            em.flush();
        } catch (DuplicateKeyException duplicateKeyException) {
            throw new SocioYaRegistrado();
        }

    }

    public Socio actualizar(Socio socio) {
        return em.merge(socio);
    }

    public void eliminar(Socio socio) {
        em.remove(em.merge(socio));
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

    @Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
    public List<Socio> listadoSocios() {
        return em.createQuery("select s from Socio s", Socio.class).getResultList();
    }
}
