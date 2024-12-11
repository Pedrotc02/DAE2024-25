package es.ujaen.dae.clubSocios.repositorios;

import es.ujaen.dae.clubSocios.entidades.Actividad;
import es.ujaen.dae.clubSocios.entidades.Temporada;
import es.ujaen.dae.clubSocios.excepciones.TemporadaYaRegistrada;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import jakarta.validation.ConstraintViolationException;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.DuplicateKeyException;
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

    public Optional<Temporada> buscarPorAnio(int anio) {
        return Optional.ofNullable(em.createQuery("select t from Temporada t where t.anio =: anio", Temporada.class)
                .setParameter("anio", anio)
                .getSingleResult());
    }

    /**
     * Guarda una nueva temporada en la base de datos.
     *
     * @param temporada la temporada a guardar
     */
    public void crear(Temporada temporada) {
        //Si se encuentra una temporada con el mismo año, no la va a registrar otra vez, no tiene sentido.
        if (em.createQuery("SELECT COUNT(t) FROM Temporada t WHERE t.anio = :anio", Long.class)
                .setParameter("anio", temporada.getAnio())
                .getSingleResult() > 0)
                    throw new TemporadaYaRegistrada("Temporada con anio " + temporada.getAnio() + " ya registrada");
        em.persist(temporada);
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
