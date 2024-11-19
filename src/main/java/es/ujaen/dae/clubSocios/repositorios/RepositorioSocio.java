package es.ujaen.dae.clubSocios.repositorios;

import es.ujaen.dae.clubSocios.entidades.Socio;
import es.ujaen.dae.clubSocios.entidades.Solicitud;
import es.ujaen.dae.clubSocios.enums.EstadoCuota;
import es.ujaen.dae.clubSocios.excepciones.SocioNoExiste;
import es.ujaen.dae.clubSocios.excepciones.SocioYaRegistrado;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
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

    @Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
    public Optional<Socio> buscarPorId(String id) {
        return Optional.ofNullable(em.find(Socio.class, id));
    }

    public void crear(Socio socio) {
        try {
            em.persist(socio);
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

    public Socio actualizarEstadoCuota(String email, EstadoCuota estadoCuota) {
        Optional<Socio> socioOptional = buscarPorId(email);

        if (socioOptional.isPresent()) {
            Socio socio = socioOptional.get();
            socio.setEstadoCuota(estadoCuota);

            save();
            return em.merge(socio);
        } else {
            throw new SocioNoExiste();
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

    public void crearSolicitud(Solicitud solicitud) {
        em.persist(solicitud);
    }

    public void borrarSolicitud(Solicitud solicitud) {
        em.remove(em.merge(solicitud));
    }

    public void save() {
        em.flush();
    }
}
