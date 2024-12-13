package es.ujaen.dae.clubSocios.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import javax.sql.DataSource;

public class ClubSecurity {
    @Bean
    public AuthenticationManager authManager(AuthenticationConfiguration authConf) throws Exception {
        return authConf.getAuthenticationManager();
    }

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity httpSecurity) throws Exception {
        return httpSecurity.csrf(Customizer.withDefaults())
                    .sessionManagement(sessionManagementCustomizer -> sessionManagementCustomizer.disable())
                    .httpBasic(httpBasic -> httpBasic.disable())
                    .authorizeRequests().requestMatchers("/clubsocios/**")
                                            .hasRole("ADMIN")
                                        .requestMatchers("/clubsocios/socios")
                                            .hasRole("SOCIO")
                    .and().build();
    }
}
