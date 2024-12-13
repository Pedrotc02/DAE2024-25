package clubSocios.servicios;

import es.ujaen.dae.clubSocios.entidades.Temporada;
import es.ujaen.dae.clubSocios.rest.dto.DTOTemporada;
import es.ujaen.dae.clubSocios.rest.dto.Mapeador;
import es.ujaen.dae.clubSocios.servicios.ServicioClub;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

import es.ujaen.dae.clubSocios.excepciones.SocioNoExiste;

import java.util.Objects;

import static org.springframework.test.util.AssertionErrors.assertEquals;

@SpringBootTest(classes = es.ujaen.dae.clubSocios.app.Main.class)
@ActiveProfiles("test")
public class TestControladorClub {

    @Autowired
    private ServicioClub servicioClub;

    // private Mapeador mapeador;

    private TestRestTemplate testRestTemplate;

    @BeforeEach
    void setUp() {
        testRestTemplate = new TestRestTemplate();
    }

    // Tests a realizar:

    /**
     * 1. **Crear una temporada (admin)**
     *
     * - Endpoint: `/clubsocios/temporadas`
     * - Método HTTP: POST
     * - Autenticación: Requerida (admin)
     * - Datos de entrada: DTOTemporada
     */
    @Test
    @DirtiesContext
    void testCrearTemporada() {
        // Datos de entrada
        DTOTemporada dtoTemporada = new DTOTemporada(null, 2025);
        String base = "http://localhost:8080";

        // Realizar la petición POST
        ResponseEntity<Void> response = testRestTemplate.withBasicAuth("direccion@clubsocios.es", "serviceSecret")
                .postForEntity(base + "/clubsocios/temporadas", dtoTemporada, Void.class);

        // Verificar la respuesta
        assertEquals("respuesta", HttpStatus.CREATED, response.getStatusCode());

        // Intentar crear una temporada con el mismo año
        response = testRestTemplate.withBasicAuth("direccion@clubsocios.es", "serviceSecret")
                .postForEntity(base + "/clubsocios/temporadas", dtoTemporada, Void.class);

        // Verificar la respuesta
        assertEquals("respuest", HttpStatus.CONFLICT, response.getStatusCode());
    }

    /**
     * 2. **Obtener una temporada (todos)**
     *
     * - Endpoint: `/clubsocios/temporadas/{anio}`
     * - Método HTTP: GET
     * - Autenticación: No requerida
     * - Respuestas esperadas:
     *      - 200 OK con el DTOTemporada si la temporada existe
     *      - 404 Not Found si la temporada no existe
     */
    @Test
    @DirtiesContext
    void testObtenerTemporada() {
        int anio = 2025;

        String base = "http://localhost:8080";
        // Crear una temporada para la prueba
        servicioClub.crearTemporada(servicioClub.login("direccion@clubsocios.es", "serviceSecret").orElseThrow(SocioNoExiste::new), new Temporada(anio));

        // Realizar la petición GET
        ResponseEntity<DTOTemporada> response = testRestTemplate.getForEntity(base + "/clubsocios/temporadas/" + anio, DTOTemporada.class);

        // Verificar la respuesta
        assertEquals("status", HttpStatus.OK, response.getStatusCode());
        assertEquals("año", anio, Objects.requireNonNull(response.getBody()).anio());

        // Intentar obtener una temporada que no existe
        response = testRestTemplate.getForEntity(base + "/clubsocios/temporadas/2026", DTOTemporada.class);

        // Verificar la respuesta
        assertEquals("respuesta", HttpStatus.NOT_FOUND, response.getStatusCode());
    }


    // 3. Crear un socio (todos)
    // 4. Obtener un socio (todos)
    // 5. Crear una actividad (admin)
    // 6. Obtener actividades de una temporada (todos)
    // 7. Obtener una actividad (todos)
    // 8. Solicitar participación en una actividad (user)
    // 9. Obtener solicitudes de una actividad (admin)
    // 10. Modificar solicitud (user)
}
