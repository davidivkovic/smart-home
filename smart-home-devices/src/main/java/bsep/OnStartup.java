package bsep;

import io.quarkus.runtime.Startup;

import org.bouncycastle.jce.provider.BouncyCastleProvider;

import jakarta.enterprise.context.ApplicationScoped;

import java.security.Security;

@Startup
@ApplicationScoped
public class OnStartup {

    OnStartup () {
        Security.addProvider(new BouncyCastleProvider());
    }

}
