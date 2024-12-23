package es.ujaen.dae.clubSocios.rest;

import es.ujaen.dae.clubSocios.entidades.Actividad;
import es.ujaen.dae.clubSocios.entidades.Solicitud;
import es.ujaen.dae.clubSocios.entidades.Temporada;
import es.ujaen.dae.clubSocios.enums.EstadoCuota;
import es.ujaen.dae.clubSocios.excepciones.*;
import es.ujaen.dae.clubSocios.rest.dto.*;
import es.ujaen.dae.clubSocios.entidades.Socio;
import es.ujaen.dae.clubSocios.security.Autenticacion;
import es.ujaen.dae.clubSocios.servicios.ServicioClub;

import jakarta.validation.ConstraintViolationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

import static es.ujaen.dae.clubSocios.util.UtilList.EJEMPLO_SOCIO;

@RestController
@RequestMapping("/clubsocios")
public class ControladorClub {
    @Autowired
    Mapeador mapeador;

    @Autowired
    ServicioClub servicioClub;

    @Autowired
    Autenticacion autenticacion;

    // Si hay alguna excepción de bean validation, salta el handler
    @ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
    @ExceptionHandler(ConstraintViolationException.class)
    public void mapeadoExcepcionConstraintViolationException() {}

    //Crear una temporada (admin)
    @PostMapping("/temporadas")
    public ResponseEntity<Void> nuevaTemporada(@RequestBody DTOTemporada dtoTemporada) {
        try {
            servicioClub.crearTemporada(EJEMPLO_SOCIO, mapeador.entidad(dtoTemporada));
        } catch (TemporadaYaRegistrada e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping("/temporadas/{anio}")
    public ResponseEntity<DTOTemporada> buscarTemporada(@PathVariable int anio) {
        try {
            Temporada temporada = servicioClub.buscarTemporada(anio).orElseThrow(() -> new TemporadaNoEncontrada("Temporada " + anio + " no encontrada"));
            return ResponseEntity.ok(mapeador.dto(temporada));
        } catch (TemporadaNoEncontrada e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    //Crear socio (todos)
    @PostMapping("/socios")
    public ResponseEntity<Void> nuevoSocio(@RequestBody DTOSocio dtoSocio) {
        try {
            servicioClub.crearSocio(mapeador.entidadCoded(dtoSocio));
        } catch (SocioYaRegistrado socioYaRegistrado) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping("/socios/{email}")
    public ResponseEntity<DTOSocio> buscarSocio(@PathVariable String email) {
        try{
            Socio socio = servicioClub.buscarSocio(email).orElseThrow(SocioNoExiste::new);
            return ResponseEntity.ok(mapeador.dto(socio));
        } catch (SocioNoExiste socioNoExiste) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    //Sólo puede admin
    @PutMapping("/socios/{email}")
    public ResponseEntity<DTOSocio> pagarCuotaSocio(@PathVariable String email){
        Socio socio;
        try {
            servicioClub.buscarSocio(email).orElseThrow(SocioNoExiste::new);
            socio = servicioClub.actualizarEstadoCuota(EJEMPLO_SOCIO, email, EstadoCuota.PAGADA);
        } catch (SocioNoExiste e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        return ResponseEntity.ok(mapeador.dto(socio));
    }

    //Crear una actividad (admin)
    @PostMapping("/temporadas/{anio}/actividades")
    public ResponseEntity<DTOActividad> nuevaActividad(@PathVariable int anio, @RequestBody DTOActividad dtoActividad) {
        Temporada temporada;
        try {
            temporada = servicioClub.buscarTemporada(anio).orElseThrow(() -> new TemporadaNoEncontrada("Temporada " + anio + " no encontrada"));
            servicioClub.crearActividad(EJEMPLO_SOCIO, temporada.getTemporadaId(), mapeador.entidad(dtoActividad));
        } catch (TemporadaNoEncontrada e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (ActividadYaRegistrada e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
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
        try {
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
                                                       @RequestParam String emailSocio, @RequestParam int numAcom) {
        Solicitud solicitud;
        try {
            //Antes de nada, voy a ver si lo solicita alguien logeado, porque si no no tiene sentido buscar nada.
            Socio socio = servicioClub.buscarSocio(emailSocio).orElseThrow(SocioNoExiste::new);

            servicioClub.buscarTemporada(anio).orElseThrow(() -> new TemporadaNoEncontrada(""));
            servicioClub.buscarActividad(idact).orElseThrow(() -> new ActividadNoEncontrada(""));

            servicioClub.registrarSolicitud(EJEMPLO_SOCIO, socio, idact, numAcom);

        } catch (TemporadaNoEncontrada | ActividadNoEncontrada | SocioNoExiste | SolicitudNoExiste e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (FueraDePlazo | NoHayPlazas e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        } catch (SolicitudYaRealizada e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    //Modificar solicitud (user)
    @PutMapping("/temporadas/{anio}/actividades/{idact}/solicitudes")
    public ResponseEntity<DTOSolicitud> modificarSolicitud(@PathVariable int anio, @PathVariable Long idact,
                                                           @RequestParam String emailSocio, @RequestParam int nuevosAcom) {
        Solicitud solicitud;
        try {
            Temporada temporada = servicioClub.buscarTemporada(anio).orElseThrow(() -> new TemporadaNoEncontrada(""));
            Actividad actividad = servicioClub.buscarActividad(idact).orElseThrow(() -> new ActividadNoEncontrada(""));

            solicitud = servicioClub.revisarSolicitudes(EJEMPLO_SOCIO, idact)
                                                .stream()
                                                .filter(s -> s.getSocioId().equals(emailSocio))
                                                .findAny()
                                                .orElseThrow(SolicitudNoExiste::new);

            solicitud = servicioClub.modificarSolicitud(solicitud, nuevosAcom);

        } catch (TemporadaNoEncontrada | ActividadNoEncontrada | SolicitudNoExiste e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        return ResponseEntity.ok(mapeador.dto(solicitud));
    }

    //Borrar solicitud (user/admin)
    @DeleteMapping("/temporadas/{anio}/actividades/{idact}/solicitudes")
    public ResponseEntity<Void> borrarSolicitud(@PathVariable int anio, @PathVariable Long idact,
                                                @RequestParam String emailSocio) {
        try {
            Socio socio = servicioClub.buscarSocio(emailSocio).orElseThrow(SocioNoExiste::new);
            Temporada temporada = servicioClub.buscarTemporada(anio).orElseThrow(() -> new TemporadaNoEncontrada(""));
            Actividad actividad = servicioClub.buscarActividad(idact).orElseThrow(() -> new ActividadNoEncontrada(""));

            Solicitud solicitud = servicioClub.revisarSolicitudes(EJEMPLO_SOCIO, idact)
                                    .stream()
                                    .filter(s -> s.getSocioId().equals(emailSocio))
                                    .findAny()
                                    .orElseThrow(SolicitudNoExiste::new);

            servicioClub.borrarSolicitud(socio, solicitud, idact);

        } catch (TemporadaNoEncontrada | ActividadNoEncontrada | SolicitudNoExiste | SocioNoExiste e ) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        return ResponseEntity.status(HttpStatus.OK).build();
    }

//    Obtener solicitudes de una actividad, obtiene todas
//    @GetMapping("/temporadas/{anio}/actividades/{idact}/solicitudes")
//    public ResponseEntity<List<DTOSolicitud>> obtenerSolicitudesActividad(@PathVariable int anio, @PathVariable Long idact) {
//        try {
//            Temporada temporada = servicioClub.buscarTemporada(anio).orElseThrow(() -> new TemporadaNoEncontrada(""));
//            Actividad actividad = servicioClub.buscarActividad(idact).orElseThrow(() -> new ActividadNoEncontrada(""));
//        } catch (TemporadaNoEncontrada | ActividadNoEncontrada e) {
//            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
//        }
//        List<Solicitud> solicitudes;
//        solicitudes = servicioClub.revisarSolicitudes(EJEMPLO_SOCIO, idact);
//        return ResponseEntity.ok(solicitudes.stream().map(s -> mapeador.dto(s)).toList());
//    }

    //Obtener solicitudes de un socio determinado a una actividad (socio y admin), que puede ser o 1 o 0
    //Si es la direccion, devuelve todas las solicitudes de la actividad
    @GetMapping("/temporadas/{anio}/actividades/{idact}/solicitudes")
    public ResponseEntity<List<DTOSolicitud>> obtenerSolicitudesActividad(@PathVariable int anio, @PathVariable Long idact,
                                                                          @RequestParam(required = false) String emailSocio,
                                                                          Principal usuarioAutenticado) {
        List<Solicitud> solicitudes;

        try {
            Temporada temporada = servicioClub.buscarTemporada(anio).orElseThrow(() -> new TemporadaNoEncontrada(""));
            Actividad actividad = servicioClub.buscarActividad(idact).orElseThrow(() -> new ActividadNoEncontrada(""));

            var esAdmin = autenticacion.loadUserByUsername(usuarioAutenticado.getName())
                    .getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

            if (esAdmin) {
                solicitudes = servicioClub.revisarSolicitudes(EJEMPLO_SOCIO, idact)
                                          .stream()
                                          .toList();
            } else {
                //Aunque sólo se devuelva la solicitud del socio, es una operación que debe hacer la dirección,
                //por eso revisa las solicitudes ésta, y entonces devuelve al socio una lista con 1 item o 0
                solicitudes = servicioClub.revisarSolicitudes(EJEMPLO_SOCIO, idact)
                                           .stream()
                                           .filter(s-> s.getSocioId().equals(emailSocio))
                                           .toList();
            }

        } catch (TemporadaNoEncontrada | ActividadNoEncontrada e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        return ResponseEntity.ok(solicitudes.stream()
                                            .map(s -> mapeador.dto(s))
                                            .toList());
    }
}
