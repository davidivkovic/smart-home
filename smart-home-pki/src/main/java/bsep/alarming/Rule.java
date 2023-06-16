package bsep.alarming;

import io.quarkus.mongodb.panache.PanacheMongoEntity;

import java.util.regex.Pattern;

public class Rule {

    public String name;
    public String drl;

    Pattern nameRegex = Pattern.compile("rule[^\"]+\"([^\"]*)\"");

    public Rule() {}

    public Rule(String drl) {
        this.drl = drl;
        var matcher = nameRegex.matcher(drl);
        if (matcher.find()) this.name = matcher.group(1);
        else this.name = "Unknown Rule";
    }

}
