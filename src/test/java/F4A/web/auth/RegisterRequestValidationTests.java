package F4A.web.auth;

import F4A.web.auth.dto.RegisterRequest;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class RegisterRequestValidationTests {
    private static Validator validator;
    private static jakarta.validation.ValidatorFactory validatorFactory;

    @BeforeAll
    static void createValidator() {
        validatorFactory = Validation.buildDefaultValidatorFactory();
        validator = validatorFactory.getValidator();
    }

    @AfterAll
    static void closeValidator() {
        validatorFactory.close();
    }

    @Test
    void acceptsAccentsSpacesAndHyphensInNamesAndValidEmail() {
        RegisterRequest request = new RegisterRequest(
                "José María",
                "Muñoz-Pérez",
                "jose@example.com",
                "ClaveSegura123"
        );

        assertTrue(validator.validate(request).isEmpty());
    }

    @Test
    void rejectsSpecialCharactersInNameAndInvalidEmail() {
        RegisterRequest request = new RegisterRequest(
                "Nombre@",
                "Apellido",
                "correo-invalido",
                "ClaveSegura123"
        );

        var violations = validator.validate(request);
        assertEquals(2, violations.size());
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("nombre")));
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("correo")));
    }
}
