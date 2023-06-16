package bsep.alarming;

import bsep.buildings.Building;

import io.quarkus.mongodb.panache.PanacheMongoEntity;

import java.util.ArrayList;
import java.util.List;

public class Alarm extends PanacheMongoEntity {

    public String message;
    public String level;
    public String service;
    public List<Rule> rules = new ArrayList<>();

    public static Alarm tooManyLoginAttempts()
    {
        var alarm = new Alarm();

        alarm.message = "Too many login attempts from the same user.";
        alarm.level = "WARNING";
        alarm.service = "smart-home-pki";

        var drl = """
            package rules;
                            
            rule "String test rule"
                when
                    $s: String()
                then
                    System.out.println("String: " + $s);
            end
        """;

        alarm.rules.add(new Rule(drl));

        return alarm;
    }

    public static List<Alarm> presetAlarms = List.of(
        tooManyLoginAttempts()
    );

    public static void initPresetAlarms()
    {
        var alarmsExist = Alarm.count() > 0;
        if (!alarmsExist) {
            var errors = RuleEngine.checkRules(presetAlarms.stream().flatMap(a -> a.rules.stream()).toList());
            if (errors.size() == 0) Alarm.persist(presetAlarms);
            else throw new RuntimeException("Preset alarms contain errors: " + errors);
        }
    }

}
