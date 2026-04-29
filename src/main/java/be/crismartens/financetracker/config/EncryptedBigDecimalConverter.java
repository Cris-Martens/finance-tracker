package be.crismartens.financetracker.config;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.encrypt.Encryptors;
import org.springframework.security.crypto.encrypt.TextEncryptor;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
@Converter
public class EncryptedBigDecimalConverter implements AttributeConverter<BigDecimal, String> {

    private final TextEncryptor encryptor;

    public EncryptedBigDecimalConverter(
            @Value("${app.crypto.password}") String password,
            @Value("${app.crypto.salt}") String salt) {
        this.encryptor = Encryptors.delux(password, salt);
    }

    @Override
    public String convertToDatabaseColumn(BigDecimal value) {
        return value == null ? null : encryptor.encrypt(value.toPlainString());
    }

    @Override
    public BigDecimal convertToEntityAttribute(String dbValue) {
        return dbValue == null ? null : new BigDecimal(encryptor.decrypt(dbValue));
    }
}
