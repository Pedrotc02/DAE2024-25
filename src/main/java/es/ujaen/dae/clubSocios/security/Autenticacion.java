package es.ujaen.dae.clubSocios.security;

import es.ujaen.dae.clubSocios.entidades.Socio;
import es.ujaen.dae.clubSocios.excepciones.SocioNoExiste;
import es.ujaen.dae.clubSocios.servicios.ServicioClub;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class Autenticacion implements UserDetailsService {
    @Autowired
    ServicioClub servicioClub;

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Override
    public UserDetails loadUserByUsername(String userName) throws UsernameNotFoundException{
        Socio socio = servicioClub.buscarSocio(userName).orElseThrow(SocioNoExiste::new);

        return User.withUsername(socio.getSocioId())
                .password(socio.getClaveAcceso())
                .roles(socio.getNombre().equals("direccion") ? "ADMIN" : "USER")
                .build();
    }

}
