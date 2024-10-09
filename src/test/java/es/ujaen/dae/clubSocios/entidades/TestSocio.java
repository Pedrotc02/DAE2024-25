package es.ujaen.dae.clubSocios.entidades;

import es.ujaen.dae.clubSocios.enums.EstadoCuota;
import org.junit.jupiter.api.Test;
import org.springframework.test.annotation.DirtiesContext;

import static net.bytebuddy.matcher.ElementMatchers.is;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.not;

public class TestSocio {

    @Test
    @DirtiesContext
    public void socioValido() {
        var socio1 = new Socio("1", "Pepito", "Fernández", "pepfer@gamil.com", "653398283", "holamundo", EstadoCuota.PENDIENTE);

        var socio2 = new Socio("2", "Juan", "Jiménez", "jujim@gmail.com", "678324567", "jujin", EstadoCuota.PENDIENTE);

        assertThat(socio1).isNotEqualTo(socio2);
    }
}
