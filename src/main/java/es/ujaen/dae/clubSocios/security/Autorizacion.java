package es.ujaen.dae.clubSocios.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.expression.WebExpressionAuthorizationManager;

@Configuration
public class Autorizacion {
    @Bean
    public AuthenticationManager authManager(AuthenticationConfiguration authConf) throws Exception {
        return authConf.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain autorizaciones(HttpSecurity httpSecurity) throws Exception {
        return httpSecurity
                .csrf(csrf -> csrf.disable())
                .sessionManagement(sM -> sM.disable())
                .httpBasic(httpBasic -> httpBasic.realmName("clubsocios"))
                .authorizeRequests(auth -> auth
                // Todos pueden crear su usuario y ver las actividades de una temporada en particular,
                // además de buscar una temporada
                        .requestMatchers(HttpMethod.POST,"/clubsocios/socios")
                            .permitAll()
                        .requestMatchers(HttpMethod.GET, "/clubsocios/temporadas/{anio}/actividades")
                            .permitAll()
                        .requestMatchers(HttpMethod.GET, "/clubsocios/temporadas/{anio}")
                            .permitAll()
                //Los socios son los únicos que pueden solicitar participar en una actividad
                        .requestMatchers(HttpMethod.POST,"/clubsocios/temporadas/{anio}/actividades/{idact}/solicitudes")
                            .hasRole("USER")

                // Sólo el admin puede: crear temporadas
                        .requestMatchers(HttpMethod.POST, "/clubsocios/temporadas")
                            .hasRole("ADMIN")

                // Cualquiera entre admin y user.
                // borrar solicitud de un socio, modificar solicitud de un socio,
                // obtener solicitudes de una actividad --> si es admin, todas, si es socio, sólo la suya
                        .requestMatchers(HttpMethod.DELETE, "/clubsocios/temporadas/{anio}/actividades/{idact}/solicitudes")
                            .access(String.valueOf(new WebExpressionAuthorizationManager("hasRole('ADMIN') or (hasRole('USER') and #emailSocio == principal.username)")))
                        .requestMatchers(HttpMethod.PUT, "/clubsocios/temporadas/{anio}/actividades/{idact}/solicitudes")
                            .access(String.valueOf(new WebExpressionAuthorizationManager("hasRole('ADMIN') or (hasRole('USER') and #emailSocio == principal.username)")))
                        .requestMatchers(HttpMethod.GET, "/clubsocios/temporadas/{anio}/actividades/{idact}/solicitudes")
                            .access(String.valueOf(new WebExpressionAuthorizationManager("hasRole('ADMIN') or (hasRole('USER') and #emailSocio == principal.username)")))
                )
                .build();
    }
}
