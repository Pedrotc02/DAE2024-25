package es.ujaen.dae.clubSocios.entidades;

import es.ujaen.dae.clubSocios.enums.EstadoCuota;
import jakarta.validation.*;

import java.util.Set;
import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.test.annotation.DirtiesContext;

public class TestSocio {

    @Test
    @DirtiesContext
    void testValidacionSocio() {
        var socio1 = new Socio("12345678A", "Pepito", "Fernández", "pepfer@gamil.com", "645367898", "pepfer", EstadoCuota.PENDIENTE);

        Validator validator = Validation.buildDefaultValidatorFactory().getValidator();
        Set<ConstraintViolation<Socio>> violations = validator.validate(socio1);

        assertThat(violations).isEmpty();

        var socio2 = new Socio("2345678A", "", "Fernández", "pepfergamil.com", "64536714", "pepfer", EstadoCuota.PENDIENTE);

        violations = validator.validate(socio2);

        assertThat(violations).isNotEmpty();
    }
}
