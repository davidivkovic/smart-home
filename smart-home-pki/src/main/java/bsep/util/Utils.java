package bsep.util;

import org.modelmapper.ModelMapper;
import org.modelmapper.convention.NameTransformers;
import org.modelmapper.convention.NamingConventions;
import org.modelmapper.spi.NamingConvention;

import java.nio.file.Paths;
import java.util.Map;

public class Utils {

    public static final String RootDir = Paths.get("../../../../").toAbsolutePath().normalize().toString();

    public static Map<String, String> Environment = System.getenv();

    public static final ModelMapper mapper = new ModelMapper();

    static {
        mapper.getConfiguration()
                .setAmbiguityIgnored(true)
                .setFieldMatchingEnabled(true);
    }

    public static <T> T coalesce(T nullCheck, T defaultValue) {
        return nullCheck == null ? defaultValue : nullCheck;
    }

}
