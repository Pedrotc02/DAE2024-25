package es.ujaen.dae.clubSocios.rest.dto;

import es.ujaen.dae.clubSocios.entidades.*;
import es.ujaen.dae.clubSocios.excepciones.SocioNoExiste;
import es.ujaen.dae.clubSocios.repositorios.RepositorioSocio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class Mapeador {

    @Autowired
    RepositorioSocio repositorioSocio;

    ///Primero la temporada
    public DTOTemporada dto(Temporada temporada){
        return new DTOTemporada(temporada.getTemporadaId(), temporada.getAnio());
    }

    public Temporada entidad(DTOTemporada dtoTemporada){
        return new Temporada(dtoTemporada.anio());
    }

    ///Luego los socios

    public DTOSocio dto(Socio socio){
        return new DTOSocio(socio.getSocioId(),
                            socio.getNombre(),
                            socio.getApellidos(),
                            socio.getDni(),
                            socio.getTlf(),
                            "",
                            socio.getEstadoCuota());
    }

    public Socio entidad(DTOSocio dtoSocio){
        return new Socio(dtoSocio.id(),
                         dtoSocio.nombre(),
                         dtoSocio.apellidos(),
                         dtoSocio.dni(),
                         dtoSocio.tlf(),
                         dtoSocio.claveAcceso(),
                         dtoSocio.cuota());
    }

    ///Después, actividades

    public DTOActividad dto(Actividad actividad){
        return new DTOActividad(actividad.getId(),
                                actividad.getTitulo(),
                                actividad.getDescripcion(),
                                actividad.getPrecio(),
                                actividad.getPlazasDisponibles(),
                                actividad.getTotalPlazas(),
                                actividad.getFechaInicioInscripcion(),
                                actividad.getFechaFinInscripcion(),
                                actividad.getFechaCelebracion());
    }

    public Actividad entidad(DTOActividad dtoActividad){
        return new Actividad(dtoActividad.titulo(),
                             dtoActividad.descripcion(),
                             dtoActividad.precio(),
                             dtoActividad.totalPlazas(),
                             dtoActividad.fechaCelebracion(),
                             dtoActividad.fechaInicioInscripcion(),
                             dtoActividad.fechaFinInscripcion());
    }

    ///Por último, solicitudes

    public DTOSolicitud dto(Solicitud solicitud) {
        return new DTOSolicitud(solicitud.getSolicitudId(),
                                solicitud.getNumAcompanantes(),
                                solicitud.getEstadoSolicitud(),
                                solicitud.getFechaSolicitud(),
                                solicitud.getPlazasConcedidas(),
                                solicitud.getSocioId());
    }

    public Solicitud entidad(DTOSolicitud dtoSolicitud){
        Socio socio = repositorioSocio.buscarPorId(dtoSolicitud.idSocio()).orElseThrow(SocioNoExiste::new);
        return new Solicitud(socio, dtoSolicitud.numAcom());
    }
}
