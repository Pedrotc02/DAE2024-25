package clubSocios.servicios;

import es.ujaen.dae.clubSocios.entidades.Socio;
import es.ujaen.dae.clubSocios.enums.EstadoCuota;
import es.ujaen.dae.clubSocios.excepciones.SocioYaRegistrado;
import es.ujaen.dae.clubSocios.servicios.ServicioClub;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.constraints.Email;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;

import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

@SpringBootTest(classes = es.ujaen.dae.clubSocios.app.Main.class)
public class TestServicioClub {
    @Autowired
    ServicioClub servicio;

    @Test
    @DirtiesContext
    void testNuevoSocioInvalido(){
        //Email incorrecto,dni incorrecto
        var socio = new Socio("11111111", "Pedro", "Apellido1 Apellido2", "pruebagmail.com", "123456789", "contraseña", EstadoCuota.PAGADA);

        assertThatThrownBy(() -> servicio.crearSocio(socio)).isInstanceOf(ConstraintViolationException.class);

        //Socio repetido
        var socio2 = new Socio("11111111M", "Pedro", "Apellido1 Apellido2", "prueba@gmail.com", "123456789", "contraseña", EstadoCuota.PAGADA);
        servicio.crearSocio(socio2);
        assertThatThrownBy(() -> servicio.crearSocio(socio2)).isInstanceOf(SocioYaRegistrado.class);


    }

}

