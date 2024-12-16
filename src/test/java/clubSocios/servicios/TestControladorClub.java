package clubSocios.servicios;

import es.ujaen.dae.clubSocios.entidades.Temporada;
import es.ujaen.dae.clubSocios.rest.dto.DTOActividad;
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

import java.time.LocalDate;
import java.util.Objects;

import static org.springframework.test.util.AssertionErrors.assertEquals;

@SpringBootTest(classes = es.ujaen.dae.clubSocios.app.Main.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class TestControladorClub {

    @LocalServerPort
    int localPort;
    private String baseUrl;

    @Autowired
    private ServicioClub servicioClub;

    private Mapeador mapeador;

    private TestRestTemplate testRestTemplate;

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

        ResponseEntity<Void> response = testRestTemplate.withBasicAuth("direccion@clubsocios.es", "serviceSecret").postForEntity("/clubsocios/temporadas", dtoTemporada, Void.class);

        assertEquals("respuesta", HttpStatus.CREATED, response.getStatusCode());

        // Intentar crear una temporada con el mismo año
        response = testRestTemplate.withBasicAuth("direccion@clubsocios.es", "serviceSecret").postForEntity("/clubsocios/temporadas", dtoTemporada, Void.class);

        assertEquals("respuest", HttpStatus.CONFLICT, response.getStatusCode());
    }

    @Test
    @DirtiesContext
    void testObtenerTemporada() {
        int anio = 2025;

        Temporada temporada = new Temporada(anio);

        servicioClub.crearTemporada(servicioClub.login("direccion@clubsocios.es", "serviceSecret").orElseThrow(SocioNoExiste::new), temporada);

        ResponseEntity<DTOTemporada> response = testRestTemplate.getForEntity(baseUrl + "/clubsocios/temporadas/" + anio, DTOTemporada.class);

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

        ResponseEntity<DTOSocio> response = testRestTemplate.withBasicAuth("prueba@gmail.com", "123456").getForEntity(baseUrl + "/clubsocios/socios/prueba@gmail.com?clave=123456", DTOSocio.class);

        assertEquals("status", HttpStatus.OK, response.getStatusCode());
        assertEquals("email", dtoSocio.id(), Objects.requireNonNull(response.getBody()).id());
    }

    @Test
    @DirtiesContext
    void testCrearActividad() {
        DTOTemporada dtoTemporada = new DTOTemporada(null, 2025);
        servicioClub.crearTemporada(servicioClub.login("direccion@clubsocios.es", "serviceSecret").orElseThrow(SocioNoExiste::new), mapeador.entidad(dtoTemporada));

        DTOActividad dtoActividad = new DTOActividad(null, "Clases de flamenco", "Aqui se dara clases de flamenco", 35, 30, 30, LocalDate.parse("2024-10-12"), LocalDate.parse("2024-10-30"), LocalDate.parse("2024-11-16"));

        ResponseEntity<DTOActividad> response = testRestTemplate.withBasicAuth("direccion@clubsocios.es", "serviceSecret").postForEntity("/clubsocios/temporadas/2025/actividades", dtoActividad, DTOActividad.class);

        assertEquals("status", HttpStatus.CREATED, response.getStatusCode());
    }

    @Test
    @DirtiesContext
    void testObtenerActividadesTemporada() {

        DTOTemporada dtoTemporada = new DTOTemporada(1L, 2024);

        servicioClub.crearTemporada(servicioClub.login("direccion@clubsocios.es", "serviceSecret").orElseThrow(SocioNoExiste::new), mapeador.entidad(dtoTemporada));

        DTOActividad dtoActividad1 = new DTOActividad(null, "Clases de flamenco",
                "Aqui se dara clases de flamenco", 35, 30, 30, LocalDate.parse("2024-10-12"),
                LocalDate.parse("2024-10-30"), LocalDate.parse("2024-11-16"));

        DTOActividad dtoActividad2 = new DTOActividad(null, "Clases de guitarra",
                "Aqui se dara clases de guitarra", 20, 20, 20, LocalDate.parse("2024-10-15"),
                LocalDate.parse("2024-10-25"), LocalDate.parse("2024-11-20"));

        servicioClub.crearActividad(servicioClub.login("direccion@clubsocios.es", "serviceSecret").orElseThrow(SocioNoExiste::new), dtoTemporada.id(), mapeador.entidad(dtoActividad1));
        servicioClub.crearActividad(servicioClub.login("direccion@clubsocios.es", "serviceSecret").orElseThrow(SocioNoExiste::new), dtoTemporada.id(), mapeador.entidad(dtoActividad2));

        ResponseEntity<DTOActividad[]> response = testRestTemplate.getForEntity(baseUrl + "/clubsocios/temporadas/2024/actividades", DTOActividad[].class);

        assertEquals("status", HttpStatus.OK, response.getStatusCode());
        assertEquals("numero de actividades", 2, Objects.requireNonNull(response.getBody()).length);
    }

    // 7. Obtener una actividad (todos)
    // 8. Solicitar participación en una actividad (user)
    // 9. Obtener solicitudes de una actividad (admin)
    // 10. Modificar solicitud (user)
}