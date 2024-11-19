package com.jcondotta.service.request;

import com.jcondotta.argument_provider.BlankValuesArgumentProvider;
import com.jcondotta.argument_provider.InvalidPassportNumberArgumentProvider;
import com.jcondotta.factory.ValidatorTestFactory;
import com.jcondotta.helper.TestAccountHolderRequest;
import jakarta.validation.Validator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ArgumentsSource;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

class CreateBankAccountRequestTest {

    private static final String ACCOUNT_HOLDER_NAME_JEFFERSON = TestAccountHolderRequest.JEFFERSON.getAccountHolderName();
    private static final String PASSPORT_NUMBER_JEFFERSON = TestAccountHolderRequest.JEFFERSON.getPassportNumber();
    private static final LocalDate DATE_OF_BIRTH_JEFFERSON = TestAccountHolderRequest.JEFFERSON.getDateOfBirth();

    private static final Validator VALIDATOR = ValidatorTestFactory.getValidator();

    @Test
    void shouldNotDetectConstraintViolation_whenRequestIsValid() {
        var accountHolderRequest = new AccountHolderRequest(ACCOUNT_HOLDER_NAME_JEFFERSON, DATE_OF_BIRTH_JEFFERSON, PASSPORT_NUMBER_JEFFERSON);
        var createBankAccountRequest = new CreateBankAccountRequest(accountHolderRequest);

        var constraintViolations = VALIDATOR.validate(createBankAccountRequest);

        assertThat(constraintViolations).isEmpty();
        assertThat(createBankAccountRequest.accountHolder()).isEqualTo(accountHolderRequest);
    }

    @Test
    void shouldDetectConstraintViolation_whenRequestHasNullAccountHolder() {
        var createBankAccountRequest = new CreateBankAccountRequest(null);

        var constraintViolations = VALIDATOR.validate(createBankAccountRequest);
        assertThat(constraintViolations)
                .hasSize(1)
                .first()
                .satisfies(violation -> {
                    assertThat(violation.getMessage()).isEqualTo("bankAccount.accountHolder.notNull");
                    assertThat(violation.getPropertyPath()).hasToString("accountHolder");
                });
    }

    @ParameterizedTest
    @ArgumentsSource(BlankValuesArgumentProvider.class)
    void shouldDetectConstraintViolation_whenAccountHolderNameIsBlank(String blankAccountHolderName) {
        var accountHolderRequest = new AccountHolderRequest(blankAccountHolderName, DATE_OF_BIRTH_JEFFERSON, PASSPORT_NUMBER_JEFFERSON);
        var createBankAccountRequest = new CreateBankAccountRequest(accountHolderRequest);

        var constraintViolations = VALIDATOR.validate(createBankAccountRequest);
        assertThat(constraintViolations)
                .hasSize(1)
                .first()
                .satisfies(violation -> {
                    assertThat(violation.getMessage()).isEqualTo("accountHolder.accountHolderName.notBlank");
                    assertThat(violation.getPropertyPath()).hasToString("accountHolder.accountHolderName");
                });
    }

    @Test
    void shouldDetectConstraintViolation_whenAccountHolderNameIsLongerThan255Characters() {
        final var veryLongAccountHolderName = "J".repeat(256);
        var accountHolderRequest = new AccountHolderRequest(veryLongAccountHolderName, DATE_OF_BIRTH_JEFFERSON, PASSPORT_NUMBER_JEFFERSON);
        var createBankAccountRequest = new CreateBankAccountRequest(accountHolderRequest);

        var constraintViolations = VALIDATOR.validate(createBankAccountRequest);
        assertThat(constraintViolations)
                .hasSize(1)
                .first()
                .satisfies(violation -> {
                    assertThat(violation.getMessage()).isEqualTo("accountHolder.accountHolderName.tooLong");
                    assertThat(violation.getPropertyPath()).hasToString("accountHolder.accountHolderName");
                });
    }

    @Test
    void shouldDetectConstraintViolation_whenDateOfBirthIsNull() {
        var accountHolderRequest = new AccountHolderRequest(ACCOUNT_HOLDER_NAME_JEFFERSON, null, PASSPORT_NUMBER_JEFFERSON);
        var createBankAccountRequest = new CreateBankAccountRequest(accountHolderRequest);

        var constraintViolations = VALIDATOR.validate(createBankAccountRequest);
        assertThat(constraintViolations)
                .hasSize(1)
                .first()
                .satisfies(violation -> {
                    assertThat(violation.getMessage()).isEqualTo("accountHolder.dateOfBirth.notNull");
                    assertThat(violation.getPropertyPath()).hasToString("accountHolder.dateOfBirth");
                });
    }

    @Test
    void shouldDetectConstraintViolation_whenDateOfBirthIsInFuture() {
        LocalDate futureDate = LocalDate.now().plusDays(1);
        var accountHolderRequest = new AccountHolderRequest(ACCOUNT_HOLDER_NAME_JEFFERSON, futureDate, PASSPORT_NUMBER_JEFFERSON);
        var createBankAccountRequest = new CreateBankAccountRequest(accountHolderRequest);

        var constraintViolations = VALIDATOR.validate(createBankAccountRequest);
        assertThat(constraintViolations)
                .hasSize(1)
                .first()
                .satisfies(violation -> {
                    assertThat(violation.getMessage()).isEqualTo("accountHolder.dateOfBirth.past");
                    assertThat(violation.getPropertyPath()).hasToString("accountHolder.dateOfBirth");
                });
    }

    @Test
    void shouldDetectConstraintViolation_whenDateOfBirthIsToday() {
        LocalDate today = LocalDate.now();
        var accountHolderRequest = new AccountHolderRequest(ACCOUNT_HOLDER_NAME_JEFFERSON, today, PASSPORT_NUMBER_JEFFERSON);
        var createBankAccountRequest = new CreateBankAccountRequest(accountHolderRequest);

        var constraintViolations = VALIDATOR.validate(createBankAccountRequest);
        assertThat(constraintViolations)
                .hasSize(1)
                .first()
                .satisfies(violation -> {
                    assertThat(violation.getMessage()).isEqualTo("accountHolder.dateOfBirth.past");
                    assertThat(violation.getPropertyPath()).hasToString("accountHolder.dateOfBirth");
                });
    }

    @Test
    void shouldDetectConstraintViolation_whenPassportNumberIsNull() {
        var accountHolderRequest = new AccountHolderRequest(ACCOUNT_HOLDER_NAME_JEFFERSON, DATE_OF_BIRTH_JEFFERSON, null);
        var createBankAccountRequest = new CreateBankAccountRequest(accountHolderRequest);

        var constraintViolations = VALIDATOR.validate(createBankAccountRequest);
        assertThat(constraintViolations)
                .hasSize(1)
                .first()
                .satisfies(violation -> {
                    assertThat(violation.getMessage()).isEqualTo("accountHolder.passportNumber.notNull");
                    assertThat(violation.getPropertyPath()).hasToString("accountHolder.passportNumber");
                });
    }

    @ParameterizedTest
    @ArgumentsSource(InvalidPassportNumberArgumentProvider.class)
    void shouldDetectConstraintViolation_whenPassportNumberIsNot8CharactersLong(String invalidLengthPassportNumber) {
        var accountHolderRequest = new AccountHolderRequest(ACCOUNT_HOLDER_NAME_JEFFERSON, DATE_OF_BIRTH_JEFFERSON, invalidLengthPassportNumber);
        var createBankAccountRequest = new CreateBankAccountRequest(accountHolderRequest);

        var constraintViolations = VALIDATOR.validate(createBankAccountRequest);
        assertThat(constraintViolations)
                .hasSize(1)
                .first()
                .satisfies(violation -> {
                    assertThat(violation.getMessage()).isEqualTo("accountHolder.passportNumber.invalidLength");
                    assertThat(violation.getPropertyPath()).hasToString("accountHolder.passportNumber");
                });
    }
}