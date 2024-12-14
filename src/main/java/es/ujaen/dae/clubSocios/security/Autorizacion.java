package es.ujaen.dae.clubSocios.security;

import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.expression.WebExpressionAuthorizationManager;

public class Autorizacion {
    @Bean
    public AuthenticationManager authManager(AuthenticationConfiguration authConf) throws Exception {
        return authConf.getAuthenticationManager();
    }

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain autorizaciones(HttpSecurity httpSecurity) throws Exception {
        return httpSecurity.authorizeRequests()
                // Todos pueden crear su usuario y ver las actividades de una temporada en particular (la actual)
                        .requestMatchers(HttpMethod.POST,"/clubsocios/socios")
                            .permitAll()
                        .requestMatchers(HttpMethod.GET, "/clubsocios/temporadas/{anio}/actividades")
                            .permitAll()

                //Los socios son los únicos que pueden solicitar participar en una actividad
                        .requestMatchers(HttpMethod.POST,"/clubsocios/temporadas/{anio}/actividades/{idact}/solicitudes")
                            .hasRole("USER")

                // Sólo el admin puede: listar temporadas, listar todas las solicitudes de una actividad
                        .requestMatchers(HttpMethod.POST, "/clubsocios/temporadas")
                            .hasRole("ADMIN")
                        .requestMatchers(HttpMethod.GET, "/clubsocios/temporadas/{anio}/actividades/{idact}/solicitudes")
                            .hasRole("ADMIN")

                // Cualquiera entre admin y user.
                // Modificar solicitud de un socio, borrar solicitud de un socio,
                // obtener solicitud de un socio en una actividad
                        .requestMatchers(HttpMethod.DELETE, "/clubsocios/temporadas/{anio}/actividades/{idact}/solicitudes")
                            .access(String.valueOf(new WebExpressionAuthorizationManager("hasRole('ADMIN') or (hasRole('SOCIO') and #emailSocio == principal.username)")))
                        .requestMatchers(HttpMethod.PUT, "/clubsocios/temporadas/{anio}/actividades/{idact}/solicitudes")
                            .access(String.valueOf(new WebExpressionAuthorizationManager("hasRole('ADMIN') or (hasRole('SOCIO') and #emailSocio == principal.username)")))
                        .requestMatchers(HttpMethod.GET, "/clubsocios/temporadas/{anio}/actividades/{idact}/solicitudes")
                            .access(String.valueOf(new WebExpressionAuthorizationManager("hasRole('ADMIN') or (hasRole('SOCIO') and #emailSocio == principal.username)")))
                .and().csrf(csrf -> csrf.disable())
                      .sessionManagement(sM -> sM.disable())
                      .httpBasic(hB -> hB.disable())
                .build();
    }
}
