package es.ujaen.dae.clubSocios.repositorios;

import es.ujaen.dae.clubSocios.entidades.Solicitud;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Transactional
@Repository
public class RepositorioSolicitud {
    @PersistenceContext
    EntityManager em;

    /**
     * Busca una solicitud por su ID en la base de datos.
     *
     * @param id el ID de la solicitud a buscar
     * @return Optional con la solicitud encontrada o vacío si no se encuentra
     */
    @Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
    public Optional<Solicitud> buscarPorId(Long id) {
        return Optional.ofNullable(em.find(Solicitud.class, id));
    }

    /**
     * Guarda una nueva solicitud en la base de datos.
     *
     * @param solicitud la solicitud a guardar
     */
    public void guardarSolicitud(Solicitud solicitud) {
        em.persist(solicitud);
        em.flush();
    }

    /**
     * Actualiza una solicitud existente en la base de datos.
     *
     * @param solicitud la solicitud a actualizar
     * @return la solicitud actualizada
     */
    public Solicitud actualizar(Solicitud solicitud) {
        return em.merge(solicitud);
    }

    /**
     * Elimina una solicitud de la base de datos.
     *
     * @param solicitud la solicitud a eliminar
     */
    public void eliminar(Solicitud solicitud) {
        em.remove(em.merge(solicitud));
    }

    /**
     * Obtiene todas las solicitudes de la base de datos.
     *
     * @return lista de todas las solicitudes
     */
    @Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
    public List<Solicitud> listadoSolicitudes() {
        return em.createQuery("select a.solicitudes from Actividad a", Solicitud.class).getResultList();
    }
}
