package bsep;

import bsep.util.SecurityUtils;
import bsep.certificates.CertificateService;

import io.quarkus.runtime.Startup;

import org.bouncycastle.jce.provider.BouncyCastleProvider;

import javax.enterprise.context.ApplicationScoped;

import java.security.Security;

@Startup
@ApplicationScoped
public class OnStartup {

    OnStartup () {
        Security.addProvider(new BouncyCastleProvider());
        CertificateService.init();
        SecurityUtils.init();
    }

}
