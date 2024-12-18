package es.ujaen.dae.clubSocios.rest;
import es.ujaen.dae.clubSocios.rest.dto.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

import es.ujaen.dae.clubSocios.enums.EstadoCuota;

import java.time.LocalDate;
import java.util.Objects;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.util.AssertionErrors.assertEquals;

@SpringBootTest(classes = es.ujaen.dae.clubSocios.app.Main.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class TestControladorClub {

    @LocalServerPort
    int localPort;

    @Autowired
    PasswordEncoder passwordEncoder;

    private TestRestTemplate testRestTemplate;

    @BeforeEach
    void setUp() {
//        String pwdEncoded = passwordEncoder.encode("serviceSecret");
//        System.out.println("Password encoded: " + pwdEncoded);
        var restTemplateBuilder = new RestTemplateBuilder()
                .rootUri("http://localhost:" + localPort + "/clubsocios");

        testRestTemplate = new TestRestTemplate(restTemplateBuilder);
    }

    ///Passed
    @Test
    @DirtiesContext
    void testCrearSocio() {
        DTOSocio invalid = new DTOSocio("pruebagmail.com", "Pedro", "", "12348A",
                "69456", "123456", EstadoCuota.PAGADA);

        ResponseEntity<Void> response = testRestTemplate.postForEntity(
                "/socios",
                invalid,
                Void.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY);

        DTOSocio dtoSocio = new DTOSocio("prueba@gmail.com", "Pedro", "Apellido1", "12345678A",
                "690123456", "123456", EstadoCuota.PAGADA);

        response = testRestTemplate.postForEntity(
                "/socios",
                dtoSocio,
                Void.class
        );

        assertEquals("respuesta", HttpStatus.CREATED, response.getStatusCode());

        // Intentar crear un socio con el mismo email
        response = testRestTemplate.postForEntity(
                "/socios",
                dtoSocio,
                Void.class
        );

        assertEquals("respuesta", HttpStatus.CONFLICT, response.getStatusCode());
    }

    @Test
    @DirtiesContext
    void testObtenerSocio() {
        DTOSocio dtoSocio = new DTOSocio("prueba@gmail.com", "Pedro", "Apellido1", "12345678A",
                "690123456", "123456", EstadoCuota.PAGADA);

        testRestTemplate.postForEntity(
                "/socios",
                dtoSocio,
                DTOSocio.class
        );

        ResponseEntity<DTOSocio> response = testRestTemplate
                .getForEntity(
                        "/socios/{email}",
                        DTOSocio.class,
                        dtoSocio.id()
                );

        assertEquals("status", HttpStatus.OK, response.getStatusCode());
        assertEquals("email", dtoSocio.id(), Objects.requireNonNull(response.getBody()).id());
    }

    /// Passed
    @Test
    @DirtiesContext
    void testCrearTemporada() {
        int anio = 2025;
        DTOTemporada dtoTemporada = new DTOTemporada(1L, anio);

        ResponseEntity<Void> response = testRestTemplate.withBasicAuth("direccion@clubsocios.es", "serviceSecret")
                .postForEntity(
                        "/temporadas",
                        dtoTemporada,
                        Void.class
                );

        assertEquals("Respuesta al admin", HttpStatus.CREATED, response.getStatusCode());

        // Intentar crear una temporada con el mismo año
        response = testRestTemplate.withBasicAuth("direccion@clubsocios.es", "serviceSecret")
                .postForEntity(
                        "/temporadas",
                        dtoTemporada,
                        Void.class
                );
        assertEquals("Respuesta al admin misma temporada", HttpStatus.CONFLICT, response.getStatusCode());

        // Intentar que otra persona (no direccion) cree una temporada distinta
        DTOTemporada temp2 = new DTOTemporada(1L, 2024);

        DTOSocio dtoSocio = new DTOSocio("prueba@gmail.com", "Pedro", "Apellido1", "12345678A",
                "690123456", "123456", EstadoCuota.PAGADA);

        testRestTemplate.postForEntity(
                "/socios",
                dtoSocio,
                DTOSocio.class
        );

        response = testRestTemplate.withBasicAuth("prueba@gmail.com", "123456").postForEntity(
                "/temporadas",
                temp2,
                Void.class
        );

        assertEquals("Respuesta a no admin distinta temporada", HttpStatus.FORBIDDEN, response.getStatusCode());
    }

    /// Passed
    @Test
    @DirtiesContext
    void testObtenerTemporada() {
        int anio = 2025;
        DTOTemporada dtoTemporada = new DTOTemporada(1L, anio);

        testRestTemplate.withBasicAuth("direccion@clubsocios.es", "serviceSecret")
                .postForEntity(
                        "/temporadas",
                        dtoTemporada,
                        Void.class
                );

        ResponseEntity<DTOTemporada> response = testRestTemplate.withBasicAuth("direccion@clubsocios.es", "serviceSecret")
                .getForEntity(
                "/temporadas/{anio}",
                DTOTemporada.class,
                dtoTemporada.anio()
        );

        assertEquals("status", HttpStatus.OK, response.getStatusCode());
        assertEquals("año", anio, Objects.requireNonNull(response.getBody()).anio());

        /// Comprobar que a otro socio (no dirección) también le deja ver la temporada con ese año
        DTOSocio dtoSocio = new DTOSocio("prueba@gmail.com", "Pedro", "Apellido1", "12345678A",
                "690123456", "123456", EstadoCuota.PAGADA);

        testRestTemplate.postForEntity(
                "/socios",
                dtoSocio,
                DTOSocio.class
        );

        response = testRestTemplate.withBasicAuth("prueba@gmail.com", "123456")
                .getForEntity(
                "/temporadas/{anio}",
                DTOTemporada.class,
                dtoTemporada.anio()
        );

        assertEquals("status", HttpStatus.OK, response.getStatusCode());
        assertEquals("año", anio, Objects.requireNonNull(response.getBody()).anio());
    }

    ///Passed
    @Test
    @DirtiesContext
    void testCrearActividad() {
        int anio = 2025;
        DTOTemporada dtoTemporada = new DTOTemporada(1L, anio);
        testRestTemplate.withBasicAuth("direccion@clubsocios.es", "serviceSecret")
                .postForEntity(
                        "/temporadas",
                        dtoTemporada,
                        Void.class
                );

        DTOActividad dtoActividad = new DTOActividad(1L, "Clases de flamenco",
                "Aqui se dara clases de flamenco", 35, 30, 30,
                LocalDate.parse("2024-10-12"), LocalDate.parse("2024-10-30"), LocalDate.parse("2024-11-16"));

        ResponseEntity<DTOActividad> response = testRestTemplate
                .withBasicAuth("direccion@clubsocios.es", "serviceSecret")
                .postForEntity(
                        "/temporadas/{anio}/actividades",
                        dtoActividad,
                        DTOActividad.class,
                        dtoTemporada.anio()
                );

        assertEquals("status", HttpStatus.CREATED, response.getStatusCode());

        /// Comprobar que al intentar crear la misma actividad genera conflicto

        response = testRestTemplate
                .withBasicAuth("direccion@clubsocios.es", "serviceSecret")
                .postForEntity(
                        "/temporadas/{anio}/actividades",
                        dtoActividad,
                        DTOActividad.class,
                        dtoTemporada.anio()
                );

        assertEquals("status", HttpStatus.CONFLICT, response.getStatusCode());

        /// Comprobar que otro socio (no direccion) no puede crear una actividad distinta
        DTOActividad dtoActividad2 = new DTOActividad(2L, "Clases de guitarra",
                "Aqui se dara clases de guitarra", 20, 20, 20, LocalDate.parse("2024-10-15"),
                LocalDate.parse("2024-10-25"), LocalDate.parse("2024-11-20"));

        DTOSocio dtoSocio = new DTOSocio("prueba@gmail.com", "Pedro", "Apellido1", "12345678A",
                "690123456", "123456", EstadoCuota.PAGADA);

        testRestTemplate.postForEntity(
                "/socios",
                dtoSocio,
                DTOSocio.class
        );

        response = testRestTemplate
                .withBasicAuth("prueba@gmail.com", "123456")
                .postForEntity(
                        "/temporadas/{anio}/actividades",
                        dtoActividad2,
                        DTOActividad.class,
                        dtoTemporada.anio()
                );

        assertEquals("status", HttpStatus.FORBIDDEN, response.getStatusCode());
    }

    ///Passed
    @Test
    @DirtiesContext
    void testObtenerActividadesTemporada() {

        ///Creo la temporada
        int anio = 2024;
        DTOTemporada dtoTemporada = new DTOTemporada(1L, anio);

        testRestTemplate.withBasicAuth("direccion@clubsocios.es", "serviceSecret")
                .postForEntity(
                        "/temporadas",
                        dtoTemporada,
                        Void.class
                );

        ///Creo las actividades
        DTOActividad dtoActividad1 = new DTOActividad(1L, "Clases de flamenco",
                "Aqui se dara clases de flamenco", 35, 30, 30, LocalDate.parse("2024-10-12"),
                LocalDate.parse("2024-10-30"), LocalDate.parse("2024-11-16"));

        testRestTemplate.withBasicAuth("direccion@clubsocios.es", "serviceSecret")
                .postForEntity(
                        "/temporadas/{anio}/actividades",
                        dtoActividad1,
                        DTOActividad.class,
                        dtoTemporada.anio()
                );

        DTOActividad dtoActividad2 = new DTOActividad(2L, "Clases de guitarra",
                "Aqui se dara clases de guitarra", 20, 20, 20, LocalDate.parse("2024-10-15"),
                LocalDate.parse("2024-10-25"), LocalDate.parse("2024-11-20"));

        testRestTemplate.withBasicAuth("direccion@clubsocios.es", "serviceSecret")
                .postForEntity(
                        "/temporadas/{anio}/actividades",
                        dtoActividad2,
                        DTOActividad.class,
                        dtoTemporada.anio()
                );

        ///Obtengo todas las actividades de una temporada (direccion puede)
        ResponseEntity<DTOActividad[]> response = testRestTemplate.withBasicAuth("direccion@clubsocios.es", "serviceSecret")
                .getForEntity(
                "/temporadas/{anio}/actividades",
                DTOActividad[].class,
                dtoTemporada.anio()
        );

        assertEquals("status", HttpStatus.OK, response.getStatusCode());
        assertEquals("numero de actividades", 2, Objects.requireNonNull(response.getBody()).length);

        ///Comprobar que otro socio (no direccion) también puede obtener todas las actividades de una temporada
        DTOSocio dtoSocio = new DTOSocio("prueba@gmail.com", "Pedro", "Apellido1", "12345678A",
                "690123456", "123456", EstadoCuota.PAGADA);

        testRestTemplate.postForEntity(
                "/socios",
                dtoSocio,
                DTOSocio.class
        );

        response = testRestTemplate.withBasicAuth("prueba@gmail.com", "123456")
                .getForEntity(
                        "/temporadas/{anio}/actividades",
                        DTOActividad[].class,
                        dtoTemporada.anio()
                );

        assertEquals("status", HttpStatus.OK, response.getStatusCode());
        assertEquals("numero de actividades", 2, Objects.requireNonNull(response.getBody()).length);
    }

    ///Passed
    @Test
    @DirtiesContext
    void testObtenerActividad() {

        ///Creo la tempòrada
        int anio = 2024;
        DTOTemporada dtoTemporada = new DTOTemporada(1L, anio);
        testRestTemplate.withBasicAuth("direccion@clubsocios.es", "serviceSecret")
                .postForEntity(
                        "/temporadas",
                        dtoTemporada,
                        Void.class
                );

        ///Creo la actividad
        DTOActividad dtoActividad1 = new DTOActividad(1L, "Clases de flamenco",
                "Aqui se dara clases de flamenco", 35, 30, 30, LocalDate.parse("2024-10-12"),
                LocalDate.parse("2024-10-30"), LocalDate.parse("2024-11-16"));

        testRestTemplate.withBasicAuth("direccion@clubsocios.es", "serviceSecret")
                .postForEntity(
                        "/temporadas/{anio}/actividades",
                        dtoActividad1,
                        DTOActividad.class,
                        dtoTemporada.anio()
                );

        ///Comprobar que todos pueden ver la actividad de la temporada correctamente

        ResponseEntity<DTOActividad[]> responseActividades = testRestTemplate.getForEntity(
                "/temporadas/{anio}/actividades", DTOActividad[].class, dtoTemporada.anio());
        Long actividadId = Objects.requireNonNull(responseActividades.getBody())[0].id();

        ResponseEntity<DTOActividad> response = testRestTemplate.getForEntity(
                 "/temporadas/{anio}/actividades/{idact}",
                DTOActividad.class,
                dtoTemporada.anio(),
                actividadId
        );

        DTOActividad actividad = response.getBody();

        assertEquals("status", HttpStatus.OK, response.getStatusCode());

        ///Comprobar algunos parámetros para asegurar que el objeto devuelto es correcto
        assertEquals("", actividad.titulo(), dtoActividad1.titulo());
        assertEquals("", actividad.id(), dtoActividad1.id());
        assertEquals("", actividad.descripcion(), dtoActividad1.descripcion());

        /// Comprobar que la direccion puede ver la actividad
        responseActividades = testRestTemplate.withBasicAuth("direccion@clubsocios.es", "serviceSecret")
                .getForEntity(
                "/temporadas/{anio}/actividades", DTOActividad[].class, dtoTemporada.anio());
        actividadId = Objects.requireNonNull(responseActividades.getBody())[0].id();

        response = testRestTemplate.getForEntity(
                "/temporadas/{anio}/actividades/{idact}",
                DTOActividad.class,
                dtoTemporada.anio(),
                actividadId
        );

        actividad = response.getBody();

        assertEquals("status", HttpStatus.OK, response.getStatusCode());

        ///Comprobar algunos parámetros para asegurar que el objeto devuelto es correcto
        assertEquals("", actividad.titulo(), dtoActividad1.titulo());
        assertEquals("", actividad.id(), dtoActividad1.id());
        assertEquals("", actividad.descripcion(), dtoActividad1.descripcion());

        /// Comprobar que un socio cualquiera puede ver la actividad
        DTOSocio dtoSocio = new DTOSocio("prueba@gmail.com", "Pedro", "Apellido1", "12345678A",
                "690123456", "123456", EstadoCuota.PAGADA);

        testRestTemplate.postForEntity(
                "/socios",
                dtoSocio,
                DTOSocio.class
        );

        responseActividades = testRestTemplate.withBasicAuth("prueba@gmail.com", "123456")
                .getForEntity(
                        "/temporadas/{anio}/actividades", DTOActividad[].class, dtoTemporada.anio());
        actividadId = Objects.requireNonNull(responseActividades.getBody())[0].id();

        response = testRestTemplate.getForEntity(
                "/temporadas/{anio}/actividades/{idact}",
                DTOActividad.class,
                dtoTemporada.anio(),
                actividadId
        );

        actividad = response.getBody();

        assertEquals("status", HttpStatus.OK, response.getStatusCode());

        ///Comprobar algunos parámetros para asegurar que el objeto devuelto es correcto
        assertEquals("", actividad.titulo(), dtoActividad1.titulo());
        assertEquals("", actividad.id(), dtoActividad1.id());
        assertEquals("", actividad.descripcion(), dtoActividad1.descripcion());
    }

    @Test
    @DirtiesContext
    void testSolicitarParticipacionActividad() {

        ///Creo la temporada
        int anio = 2024;
        DTOTemporada dtoTemporada = new DTOTemporada(1L, anio);
        testRestTemplate.withBasicAuth("direccion@clubsocios.es", "serviceSecret")
                .postForEntity(
                        "/temporadas",
                        dtoTemporada,
                        Void.class
                );

        ///Creo la actividad
        DTOActividad dtoActividad = new DTOActividad(1L, "Clases de flamenco",
                "Aqui se dara clases de flamenco", 35, 30, 30, LocalDate.parse("2024-10-12"),
                LocalDate.parse("2024-10-30"), LocalDate.parse("2024-11-16"));

        testRestTemplate.withBasicAuth("direccion@clubsocios.es", "serviceSecret")
                .postForEntity(
                        "/clubsocios/temporadas/{anio}/actividades",
                        dtoActividad,
                        DTOActividad.class,
                        dtoTemporada.anio()
                );

        ///Creo el socio
        DTOSocio dtoSocio = new DTOSocio("prueba@gmail.com", "Pedro", "Apellido1", "12345678A",
                "690123456", "123456", EstadoCuota.PAGADA);

        testRestTemplate.postForEntity(
                "/socios",
                dtoSocio,
                DTOSocio.class
        );

        int numAcom = 2;

        ///EL socio hace la solicitud
        ResponseEntity<DTOSolicitud> response = testRestTemplate.withBasicAuth("prueba@gmail.com", "123456")
                .postForEntity(
                        "/temporadas/{anio}/actividades/{idact}/solicitudes?emailSocio={emailSocio}&numAcom={numAcom}",
                        null,
                        DTOSolicitud.class,
                        dtoTemporada.anio(),
                        dtoActividad.id(),
                        dtoSocio.id(),
                        numAcom
                );

        assertEquals("status", HttpStatus.CREATED, response.getStatusCode());
    }

     @Test
     @DirtiesContext void testObtenerSolicitudesActividad() {
         ///Creo la temporada
         int anio = 2024;
         DTOTemporada dtoTemporada = new DTOTemporada(1L, anio);
         testRestTemplate.withBasicAuth("direccion@clubsocios.es", "serviceSecret")
                 .postForEntity(
                         "/temporadas",
                         dtoTemporada,
                         Void.class
                 );

         ///Creo la actividad
         DTOActividad dtoActividad = new DTOActividad(1L, "Clases de flamenco",
                 "Aqui se dara clases de flamenco", 35, 30, 30, LocalDate.parse("2024-10-12"),
                 LocalDate.parse("2024-10-30"), LocalDate.parse("2024-11-16"));

         testRestTemplate
                 .withBasicAuth("direccion@clubsocios.es", "serviceSecret")
                 .postForEntity(
                         "/clubsocios/temporadas/{anio}/actividades",
                         dtoActividad,
                         DTOActividad.class,
                         dtoTemporada.anio()
                 );

         ///Creo el socio
         DTOSocio dtoSocio = new DTOSocio("pepfer@gmail.com", "Pepito", "Fernández", "11111111M",
                 "645367898", "pepfer", EstadoCuota.PAGADA);

         testRestTemplate.postForEntity(
                 "/socios",
                 dtoSocio,
                 Void.class
         );


         int numAcom = 2;

         ///EL socio hace la solicitud
         ResponseEntity<DTOSolicitud> postResponse = testRestTemplate.withBasicAuth("prueba@gmail.com", "123456")
                 .postForEntity(
                         "/temporadas/{anio}/actividades/{idact}/solicitudes?emailSocio={emailSocio}&numAcom={numAcom}",
                         null,
                         DTOSolicitud.class,
                         dtoTemporada.anio(),
                         dtoActividad.id(),
                         dtoSocio.id(),
                         numAcom
                         );

         assertEquals("Estado post", HttpStatus.CREATED, postResponse.getStatusCode());

         ///Obtener solicitudes para la actividad (dirección)
         ResponseEntity<DTOSolicitud[]> todasSolicitudes = testRestTemplate.withBasicAuth("direccion@clubsocios.es", "serviceSecret")
         .getForEntity(
                 "/temporadas/{anio}/actividades/{idact}/solicitudes",
                 DTOSolicitud[].class
         );

         ResponseEntity<DTOSolicitud[]> response = testRestTemplate.withBasicAuth("direccion@clubsocios.es", "serviceSecret")
                 .getForEntity(
                         "/temporadas/{anio}/actividades/{idact}/solicitudes",
                         DTOSolicitud[].class
                 );

         assertEquals("Estado get", HttpStatus.OK, response.getStatusCode());

         assertEquals("numero de solicitudes", 1, Objects.requireNonNull(response.getBody()).length);
     }


     @Test
     @DirtiesContext
     void testModificarSolicitud() {
     // Crear temporada
     int anio = 2024;
     DTOTemporada dtoTemporada = new DTOTemporada(1L, anio);
     testRestTemplate.withBasicAuth("direccion@clubsocios.es", "serviceSecret")
             .postForEntity(
                     "/temporadas",
                     dtoTemporada,
                     Void.class
             );

     // Crear actividad
     DTOActividad dtoActividad = new DTOActividad(1L, "Clases de flamenco",
     "Aqui se dara clases de flamenco", 35, 30, 30,
     LocalDate.parse("2024-10-12"), LocalDate.parse("2024-10-30"), LocalDate.parse("2024-11-16"));

     testRestTemplate
             .withBasicAuth("direccion@clubsocios.es", "serviceSecret")
             .postForEntity("/temporada/{anio}/actividades",
                     dtoActividad,
                     DTOActividad.class,
                     dtoTemporada.id()
             );

     // Crear socio
     DTOSocio dtoSocio = new DTOSocio("prueba@gmail.com", "Pedro", "Apellido1", "12345678A",
     "690123456", "123456", EstadoCuota.PAGADA);

     testRestTemplate.postForEntity(
             "/socios",
             dtoSocio,
             DTOSocio.class
     );

     //El socio solicita la participación en una actividad
     int numAcom = 2;
     ResponseEntity<DTOSolicitud[]> respuestaPost = testRestTemplate.withBasicAuth("prueba@gmail.com", "123456")
     .postForEntity(
             "/temporadas/{anio}/actividades/{idact}/solicitudes?emailSocio={emailSocio}&numAcom={numAcom}",
             null,
             DTOSolicitud[].class,
             dtoTemporada.anio(),
             dtoActividad.id(),
             dtoSocio.id(),
             numAcom
             );

     // Obtener las solicitudes de la actividad hechas por el socio, debe ser 1
         ResponseEntity<DTOSolicitud[]> respuestaSolicitudSocio = testRestTemplate.
                 withBasicAuth("prueba@gmail.com", "123456").getForEntity(
             "/temporadas/{anio}/actividades/{idact}/solicitudes?emailSocio={emailSocio}",
             DTOSolicitud[].class,
             dtoTemporada.anio(),
             dtoActividad.id(),
             dtoSocio.id()
         );

     //Modificar la solicitud con un nuevo número de acompañantes y el mismo socio
     int nuevosAcom = 3;
     ResponseEntity<DTOSolicitud> response = testRestTemplate.withBasicAuth("prueba@gmail.com", "123456")
     .exchange(
             "/temporadas/{anio}/actividades/{idact}/solicitudes?emailSocio={emailSocio}&nuevosAcom={nuevosAcom}",
             HttpMethod.PUT,
             HttpEntity.EMPTY,
             DTOSolicitud.class,
             dtoTemporada.anio(),
             dtoActividad.id(),
             dtoSocio.id(),
             nuevosAcom
             );

     assertEquals("status", HttpStatus.OK, response.getStatusCode());
     assertEquals("numAcom", 3, Objects.requireNonNull(response.getBody()).numAcom());
     }

     @Test
     @DirtiesContext
     void testBorrarSolicitud() {
         // Crear temporada
         int anio = 2024;
         DTOTemporada dtoTemporada = new DTOTemporada(1L, anio);
         testRestTemplate.withBasicAuth("direccion@clubsocios.es", "serviceSecret")
                 .postForEntity(
                         "/temporadas",
                         dtoTemporada,
                         Void.class
                 );

         // Crear actividad
         DTOActividad dtoActividad = new DTOActividad(1L, "Clases de flamenco",
                 "Aqui se dara clases de flamenco", 35, 30, 30,
                 LocalDate.parse("2024-10-12"), LocalDate.parse("2024-10-30"), LocalDate.parse("2024-11-16"));

         testRestTemplate
                 .withBasicAuth("direccion@clubsocios.es", "serviceSecret")
                 .postForEntity("/temporada/{anio}/actividades",
                         dtoActividad,
                         DTOActividad.class,
                         dtoTemporada.id()
                 );

         // Crear socio
         DTOSocio dtoSocio = new DTOSocio("prueba@gmail.com", "Pedro", "Apellido1", "12345678A",
                 "690123456", "123456", EstadoCuota.PAGADA);

         testRestTemplate.postForEntity(
                 "/socios",
                 dtoSocio,
                 DTOSocio.class
         );

         //El socio solicita la participación en una actividad
         int numAcom = 2;
         ResponseEntity<DTOSolicitud[]> respuestaPost = testRestTemplate.withBasicAuth("prueba@gmail.com", "123456")
                 .postForEntity(
                         "/temporadas/{anio}/actividades/{idact}/solicitudes?emailSocio={emailSocio}&numAcom={numAcom}",
                         null,
                         DTOSolicitud[].class,
                         dtoTemporada.anio(),
                         dtoActividad.id(),
                         dtoSocio.id(),
                         numAcom
                 );

         // Obtener las solicitudes de la actividad hechas por el socio, debe ser 1
         ResponseEntity<DTOSolicitud[]> respuestaSolicitudSocio = testRestTemplate.
                 withBasicAuth("prueba@gmail.com", "123456").getForEntity(
                         "/temporadas/{anio}/actividades/{idact}/solicitudes?emailSocio={emailSocio}",
                         DTOSolicitud[].class,
                         dtoTemporada.anio(),
                         dtoActividad.id(),
                         dtoSocio.id()
                 );

         ResponseEntity<Void> deleteResponse = testRestTemplate.withBasicAuth("prueba@gmail.com", "123456")
                 .exchange("/temporadas/{anio}/actividades/{idact}/solicitudes?emailSocio={emailSocio}",
                         HttpMethod.DELETE,
                         HttpEntity.EMPTY,
                         Void.class,
                         dtoTemporada.anio(),
                         dtoActividad.id(),
                         dtoSocio.id());

         assertEquals("status", HttpStatus.OK, deleteResponse.getStatusCode());
     }

}