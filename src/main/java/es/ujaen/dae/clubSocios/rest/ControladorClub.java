package es.ujaen.dae.clubSocios.rest;

import es.ujaen.dae.clubSocios.entidades.Actividad;
import es.ujaen.dae.clubSocios.entidades.Solicitud;
import es.ujaen.dae.clubSocios.entidades.Temporada;
import es.ujaen.dae.clubSocios.excepciones.*;
import es.ujaen.dae.clubSocios.rest.dto.*;
import es.ujaen.dae.clubSocios.entidades.Socio;
import es.ujaen.dae.clubSocios.security.Autenticacion;
import es.ujaen.dae.clubSocios.servicios.ServicioClub;

import jakarta.annotation.PostConstruct;
import jakarta.validation.ConstraintViolationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/clubsocios")
public class ControladorClub {
    @Autowired
    Mapeador mapeador;

    @Autowired
    ServicioClub servicioClub;

    @Autowired
    Autenticacion autenticacion;
    Socio direccion;

    @PostConstruct
    void loginDireccion(){
        direccion = servicioClub.("direccion@clubsocios.es", "serviceSecret").orElseThrow(SocioNoExiste::new);
    }

    // Si hay alguna excepción de bean validation, salta el handler
    @ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
    @ExceptionHandler(ConstraintViolationException.class)
    public void mapeadoExcepcionConstraintViolationException() {}

    //Crear una temporada (admin)
    @PostMapping("/temporadas")
    public ResponseEntity<Void> nuevaTemporada(@RequestBody DTOTemporada dtoTemporada) {
        try{
            servicioClub.crearTemporada(direccion, mapeador.entidad(dtoTemporada));
        } catch (TemporadaYaRegistrada e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping("/temporadas/{anio}")
    public ResponseEntity<DTOTemporada> buscarTemporada(@PathVariable int anio) {
        try {
            Temporada temporada = servicioClub.buscarTemporada(anio).orElseThrow(()-> new TemporadaNoEncontrada("Temporada " + anio + " no encontrada"));
            return ResponseEntity.ok(mapeador.dto(temporada));
        } catch (TemporadaNoEncontrada e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    //Crear socio (todos)
    @PostMapping("/socios")
    public ResponseEntity<Void> nuevoSocio(@RequestBody DTOSocio dtoSocio) {
        try {
            servicioClub.crearSocio(mapeador.entidad(dtoSocio));
        } catch (SocioYaRegistrado socioYaRegistrado) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping("/socios/{email}")
    public ResponseEntity<DTOSocio> buscarSocio(@PathVariable String email, @RequestParam String clave) {
        try{
            Socio socio = servicioClub.login(email, clave).orElseThrow(SocioNoExiste::new);
            return ResponseEntity.ok(mapeador.dto(socio));
        } catch(SocioNoExiste socioNoExiste) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    //Crear una actividad (admin)
    @PostMapping("/temporadas/{anio}/actividades")
    public ResponseEntity<DTOActividad> nuevaActividad(@PathVariable int anio, @RequestBody DTOActividad dtoActividad) {
        Temporada temporada;
        try{
            temporada = servicioClub.buscarTemporada(anio).orElseThrow(() -> new TemporadaNoEncontrada("Temporada " + anio + " no encontrada"));
        } catch (TemporadaNoEncontrada e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        servicioClub.crearActividad(direccion, temporada.getTemporadaId(), mapeador.entidad(dtoActividad));
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    //Obtener actividades de una temporada
    @GetMapping("/temporadas/{anio}/actividades")
    public ResponseEntity<List<DTOActividad>> obtenerActividades(@PathVariable int anio) {
        List<Actividad> actividades;
        Temporada temporada;
        try {
            temporada = servicioClub.buscarTemporada(anio).orElseThrow(() -> new TemporadaNoEncontrada("Temporada " + anio + " no encontrada"));
            actividades = servicioClub.obtenerActividadesTemporada(temporada.getTemporadaId());
        } catch (TemporadaNoEncontrada e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        return ResponseEntity.ok(actividades.stream().map(a -> mapeador.dto(a)).toList());
    }

    @GetMapping("/temporadas/{anio}/actividades/{idact}")
    public ResponseEntity<DTOActividad> buscarActividad(@PathVariable int anio, @PathVariable Long idact) {
        Actividad actividad;
        try{
            Temporada temporada = servicioClub.buscarTemporada(anio).orElseThrow(() -> new TemporadaNoEncontrada("Temporada " + anio + " no encontrada"));
            actividad = servicioClub.buscarActividad(idact).orElseThrow(() -> new ActividadNoEncontrada(""));
        } catch (ActividadNoEncontrada | TemporadaNoEncontrada e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        return ResponseEntity.ok(mapeador.dto(actividad));
    }

    //Solicitar participación en una actividad (user)
    @PostMapping("/temporadas/{anio}/actividades/{idact}/solicitudes")
    public ResponseEntity<DTOSolicitud> crearSolicitud(@PathVariable int anio, @PathVariable Long idact,
                                                       @RequestBody DTOSocio dtoSocio, @RequestParam int numAcom,
                                                       Principal socioAutenticado) {
        Solicitud solicitud;
        try {
            //Antes de nada, voy a ver si lo solicita alguien logeado, porque si no no tiene sentido buscar nada.
            Socio socio = servicioClub.socio(socioAutenticado.getName()).orElseThrow(

            );

            servicioClub.buscarTemporada(anio).orElseThrow(() -> new TemporadaNoEncontrada(""));
            servicioClub.buscarActividad(idact).orElseThrow(() -> new ActividadNoEncontrada(""));
            servicioClub.login(dtoSocio.id(), dtoSocio.claveAcceso()).orElseThrow(SocioNoExiste::new);
            solicitud = servicioClub.registrarSolicitud(direccion, mapeador.entidad(dtoSocio), idact, numAcom);
        } catch (TemporadaNoEncontrada | ActividadNoEncontrada | SocioNoExiste s) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (FueraDePlazo | NoHayPlazas e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        } catch (SolicitudYaRealizada e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }

        return ResponseEntity.ok(mapeador.dto(solicitud));
    }

    //Modificar solicitud (user)
    @PutMapping("/temporadas/{anio}/actividades/{idact}/solicitudes")
    public ResponseEntity<DTOSolicitud> modificarSolicitud(@PathVariable int anio, @PathVariable Long idact, @RequestBody DTOSolicitud dtoSolicitud, @RequestParam int nuevosAcom) {
        Solicitud solicitud;
        try {
            Temporada temporada = servicioClub.buscarTemporada(anio).orElseThrow(() -> new TemporadaNoEncontrada(""));
            Actividad actividad = servicioClub.buscarActividad(idact).orElseThrow(() -> new ActividadNoEncontrada(""));

            solicitud = servicioClub.revisarSolicitudes(direccion, idact)
                                                .stream()
                                                .filter(s -> s.getSolicitudId().equals(dtoSolicitud.id()))
                                                .findAny()
                                                .orElseThrow(SolicitudNoExiste::new);

            solicitud = servicioClub.modificarSolicitud(solicitud, nuevosAcom);

        } catch (TemporadaNoEncontrada | ActividadNoEncontrada e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        return ResponseEntity.ok(mapeador.dto(solicitud));
    }

    //Borrar solicitud (user)
    @DeleteMapping("/temporadas/{anio}/actividades/{idact}/solicitudes")
    public ResponseEntity<Void> borrarSolicitud(@PathVariable int anio, @PathVariable Long idact, @RequestBody DTOSolicitud dtoSolicitud, @RequestBody DTOSocio dtoSocio) {
        try {
            Temporada temporada = servicioClub.buscarTemporada(anio).orElseThrow(() -> new TemporadaNoEncontrada(""));
            Actividad actividad = servicioClub.buscarActividad(idact).orElseThrow(() -> new ActividadNoEncontrada(""));

            Solicitud solicitud = servicioClub.revisarSolicitudes(direccion, idact)
                                    .stream()
                                    .filter(s -> s.getSolicitudId().equals(dtoSolicitud.id()))
                                    .findAny()
                                    .orElseThrow(SolicitudNoExiste::new);
            Socio socio = mapeador.entidad(dtoSocio);
            servicioClub.borrarSolicitud(socio, solicitud.getSolicitudId());

        } catch (TemporadaNoEncontrada | ActividadNoEncontrada e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    //Obtener solicitudes de una actividad
    @GetMapping("/temporadas/{id}/actividades/{idact}/solicitudes")
    public ResponseEntity<List<DTOSolicitud>> obtenerSolicitudesActividad(@PathVariable Long id, @PathVariable Long idact) {
        try {
            Temporada temporada = servicioClub.buscarTemporada(id).orElseThrow(() -> new TemporadaNoEncontrada(""));
            Actividad actividad = servicioClub.buscarActividad(idact).orElseThrow(() -> new ActividadNoEncontrada(""));
        } catch (TemporadaNoEncontrada | ActividadNoEncontrada e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        List<Solicitud> solicitudes;
        solicitudes = servicioClub.revisarSolicitudes(direccion, idact);
        return ResponseEntity.ok(solicitudes.stream().map(s -> mapeador.dto(s)).toList());
    }

    //Obtener solicitudes de un socio determinado a una actividad
    @GetMapping("/temporadas/{id}/actividades/{idact}/solicitudes")
    public ResponseEntity<List<DTOSolicitud>> obtenerSolicitudesActividad(@PathVariable Long id, @PathVariable Long idact, @RequestParam String socioId) {
        try {
            Temporada temporada = servicioClub.buscarTemporada(id).orElseThrow(() -> new TemporadaNoEncontrada(""));
            Actividad actividad = servicioClub.buscarActividad(idact).orElseThrow(() -> new ActividadNoEncontrada(""));
        } catch (TemporadaNoEncontrada | ActividadNoEncontrada e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        List<Solicitud> solicitudes;
        solicitudes = servicioClub.revisarSolicitudes(direccion, idact);
        return ResponseEntity.ok(solicitudes.stream()
                                            .map(s -> mapeador.dto(s))
                                            .filter(s-> s.idSocio().equals(socioId))
                                            .toList());
    }

}
