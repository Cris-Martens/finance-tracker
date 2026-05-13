package be.crismartens.financetracker.auth.security;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;


@ConfigurationProperties(prefix = "rsa")
@Getter
@Setter
public class RsaKeyProperties {

    private String publicKey;
    private String privateKey;
}
