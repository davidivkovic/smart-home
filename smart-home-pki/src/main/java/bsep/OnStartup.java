package bsep;

import bsep.alarming.Alarm;
import bsep.util.SecurityUtils;
import bsep.certificates.CertificateService;

import io.quarkus.runtime.Startup;

import org.bouncycastle.jce.provider.BouncyCastleProvider;

import jakarta.enterprise.context.ApplicationScoped;

import java.io.FileNotFoundException;
import java.security.Security;

@Startup
@ApplicationScoped
public class OnStartup {

    OnStartup () throws FileNotFoundException {
        Security.addProvider(new BouncyCastleProvider());
        CertificateService.init();
        SecurityUtils.init();
        Alarm.initPresetAlarms();
    }

}
