package es.ujaen.dae.clubSocios.security;

import es.ujaen.dae.clubSocios.entidades.Socio;
import es.ujaen.dae.clubSocios.excepciones.SocioNoExiste;
import es.ujaen.dae.clubSocios.servicios.ServicioClub;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class Autenticacion implements UserDetailsService {
    @Autowired
    ServicioClub servicioClub;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException{
        Socio socio = servicioClub.socio(username).orElseThrow(SocioNoExiste::new);

        return User.withUsername(username)
                   .password(socio.getClaveAcceso())
                   .roles(username.equals("direccion@clubsocios.es") ? "ADMIN" : "SOCIO")
                   .build();
    }

}
