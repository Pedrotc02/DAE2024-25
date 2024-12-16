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
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

import es.ujaen.dae.clubSocios.excepciones.SocioNoExiste;
import es.ujaen.dae.clubSocios.rest.dto.DTOSocio;
import es.ujaen.dae.clubSocios.enums.EstadoCuota;

import java.util.Objects;

import static org.springframework.test.util.AssertionErrors.assertEquals;

@SpringBootTest(classes = es.ujaen.dae.clubSocios.app.Main.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class TestControladorClub {

    @LocalServerPort
    int localPort;

    @Autowired
    private ServicioClub servicioClub;

    private Mapeador mapeador;

    private TestRestTemplate testRestTemplate;
    private String baseUrl;

    @BeforeEach
    void setUp() {
        mapeador = new Mapeador();
        baseUrl = "http://localhost:" + localPort;
        var restTemplateBuilder = new RestTemplateBuilder().rootUri(baseUrl);
        testRestTemplate = new TestRestTemplate(restTemplateBuilder);
    }

    @Test
    @DirtiesContext
    void testCrearTemporada() {
        DTOTemporada dtoTemporada = new DTOTemporada(null, 2025);

        ResponseEntity<Void> response = testRestTemplate.withBasicAuth("direccion@clubsocios.es", "serviceSecret")
                .postForEntity("/clubsocios/temporadas", dtoTemporada, Void.class);

        assertEquals("respuesta", HttpStatus.CREATED, response.getStatusCode());

        // Intentar crear una temporada con el mismo año
        response = testRestTemplate.withBasicAuth("direccion@clubsocios.es", "serviceSecret")
                .postForEntity("/clubsocios/temporadas", dtoTemporada, Void.class);

        assertEquals("respuest", HttpStatus.CONFLICT, response.getStatusCode());
    }

    @Test
    @DirtiesContext
    void testObtenerTemporada() {
        int anio = 2025;

        Temporada temporada = new Temporada(anio);

        servicioClub.crearTemporada(
                servicioClub.login("direccion@clubsocios.es", "serviceSecret")
                        .orElseThrow(SocioNoExiste::new),
                temporada
        );

        ResponseEntity<DTOTemporada> response = testRestTemplate.getForEntity(baseUrl + "/clubsocios/temporadas/" + anio,
                DTOTemporada.class
        );

        assertEquals("status", HttpStatus.OK, response.getStatusCode());
        assertEquals("año", anio, Objects.requireNonNull(response.getBody()).anio());
    }

    @Test
    @DirtiesContext
    void testCrearSocio() {
        DTOSocio dtoSocio = new DTOSocio("prueba@gmail.com", "Pedro", "Apellido1", "12345678A", "690123456", "123456", EstadoCuota.PAGADA);

        ResponseEntity<Void> response = testRestTemplate.postForEntity(baseUrl + "/clubsocios/socios", dtoSocio, Void.class);

        assertEquals("respuesta", HttpStatus.CREATED, response.getStatusCode());

        // Intentar crear un socio con el mismo email
        response = testRestTemplate.postForEntity(baseUrl + "/clubsocios/socios", dtoSocio, Void.class);

        assertEquals("respuesta", HttpStatus.CONFLICT, response.getStatusCode());
    }

    @Test
    @DirtiesContext
    void testObtenerSocio() {
        DTOSocio dtoSocio = new DTOSocio("prueba@gmail.com", "Pedro", "Apellido1", "12345678A", "690123456", "123456", EstadoCuota.PAGADA);
        servicioClub.crearSocio(mapeador.entidad(dtoSocio));

        ResponseEntity<DTOSocio> response = testRestTemplate.withBasicAuth("prueba@gmail.com", "123456")
                .getForEntity(baseUrl + "/clubsocios/socios/prueba@gmail.com?clave=123456", DTOSocio.class);

        assertEquals("status", HttpStatus.OK, response.getStatusCode());
        assertEquals("email", dtoSocio.id(), Objects.requireNonNull(response.getBody()).id());
    }

    // 5. Crear una actividad (admin)
    // 6. Obtener actividades de una temporada (todos)
    // 7. Obtener una actividad (todos)
    // 8. Solicitar participación en una actividad (user)
    // 9. Obtener solicitudes de una actividad (admin)
    // 10. Modificar solicitud (user)
}