package com.volasoftware.tinder.validator.password;

import java.io.InputStream;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import lombok.SneakyThrows;
import org.passay.CharacterRule;
import org.passay.EnglishCharacterData;
import org.passay.LengthRule;
import org.passay.MessageResolver;
import org.passay.PasswordData;
import org.passay.PasswordValidator;
import org.passay.PropertiesMessageResolver;
import org.passay.RuleResult;
import org.passay.WhitespaceRule;

public class PasswordConstraintValidator implements ConstraintValidator<ValidPassword, String> {

    @Override
    public void initialize(final ValidPassword arg0) {}

    @SneakyThrows
    @Override
    public boolean isValid(String password, ConstraintValidatorContext context) {

        Properties props = new Properties();
        InputStream inputStream = getClass().getClassLoader().getResourceAsStream("passay.properties");
        props.load(inputStream);
        MessageResolver resolver = new PropertiesMessageResolver(props);
        PasswordValidator validator =
                new PasswordValidator(
                        resolver,
                        Arrays.asList(
                                new LengthRule(8, 30),
                                new CharacterRule(EnglishCharacterData.Alphabetical, 1),
                                new CharacterRule(EnglishCharacterData.Digit, 1),
                                new WhitespaceRule()));
        RuleResult result = validator.validate(new PasswordData(password));
        if (result.isValid()) {
            return true;
        }
        List<String> messages = validator.getMessages(result);
        String messageTemplate = String.join(",", messages);
        context
                .buildConstraintViolationWithTemplate(messageTemplate)
                .addConstraintViolation()
                .disableDefaultConstraintViolation();
        return false;
    }
}
