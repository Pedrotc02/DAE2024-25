package es.ujaen.dae.clubSocios.repositorios;

import es.ujaen.dae.clubSocios.entidades.Actividad;
import es.ujaen.dae.clubSocios.entidades.Temporada;
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
public class RepositorioTemporada {

    @PersistenceContext
    EntityManager em;

    /**
     * Busca una temporada por su ID en la base de datos.
     *
     * @param id el ID de la temporada a buscar
     * @return Optional con la temporada encontrada o vacío si no se encuentra
     */
    @Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
    public Optional<Temporada> buscarPorId(Long id) {
        return Optional.ofNullable(em.find(Temporada.class, id));
    }

    /**
     * Guarda una nueva temporada en la base de datos.
     *
     * @param temporada la temporada a guardar
     */
    public void guardarTemporada(Temporada temporada) {
        em.persist(em.merge(temporada));
        em.flush();
    }

    /**
     * Actualiza una temporada existente en la base de datos.
     *
     * @param temporada la temporada a actualizar
     * @return la temporada actualizada
     */
    public Temporada actualizar(Temporada temporada) {
        return em.merge(temporada);
    }

    /**
     * Elimina una temporada de la base de datos.
     *
     * @param temporada la temporada a eliminar
     */
    public void eliminar(Temporada temporada) {
        em.remove(em.merge(temporada));
    }

    /**
     * Obtiene todas las temporadas de la base de datos.
     *
     * @return lista de todas las temporadas
     */
    @Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
    public List<Temporada> listadoTemporadas() {
        return em.createQuery("select t from Temporada t", Temporada.class).getResultList();
    }

    /**
     * Obtiene todas las actividades de una temporada específica.
     *
     * @param id el ID de la temporada
     * @return lista de actividades de la temporada ordenadas por ID
     */
    @Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
    public List<Actividad> obtenerActividadesDeTemporada(Long id) {
        return em.createQuery(
                        "select a from Temporada t join t.actividades a where t.temporadaId = :id order by a.id ASC",
                        Actividad.class)
                .setParameter("id", id)
                .getResultList();
    }
}
