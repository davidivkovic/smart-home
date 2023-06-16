package bsep.alarming;

import io.quarkus.mongodb.panache.common.ProjectionFor;
import org.drools.io.ByteArrayResource;

import org.drools.kiesession.rulebase.InternalKnowledgeBase;
import org.drools.kiesession.rulebase.KnowledgeBaseFactory;

import org.kie.api.io.ResourceType;
import org.kie.internal.builder.KnowledgeBuilderFactory;
import org.kie.internal.builder.KnowledgeBuilderResult;
import org.kie.internal.builder.ResultSeverity;

import java.util.List;

public class RuleEngine {

    public static InternalKnowledgeBase knowledgeBase = KnowledgeBaseFactory.newKnowledgeBase();

    public void init() {
        @ProjectionFor(Alarm.class) record RuleProjection(List<Rule> rules) {};
        var rules = Alarm
                .findAll()
                .project(RuleProjection.class)
                .list()
                .stream()
                .flatMap(r -> r.rules.stream())
                .toList();

        addRules(rules);
    }

    public static void addRules(List<Rule> rules) {
        var knowledgeBuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        for (var rule : rules) {
            knowledgeBuilder.add(new ByteArrayResource(rule.drl.getBytes()), ResourceType.DRL);
        }
        knowledgeBase.addPackages(knowledgeBuilder.getKnowledgePackages());
    }

    public static void addRule(Rule rule) {
        addRules(List.of(rule));
    }

    public static List<String> checkRules(List<Rule> rules) {
        var knowledgeBuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        for (var rule : rules) {
            knowledgeBuilder.add(new ByteArrayResource(rule.drl.getBytes()), ResourceType.DRL);
        }
        return knowledgeBuilder.getErrors()
                .stream()
                .filter(e -> e.getSeverity().equals(ResultSeverity.ERROR))
                .map(KnowledgeBuilderResult::getMessage)
                .toList();
    }

    public static List<String> checkRule(Rule rule) {
        return checkRules(List.of(rule));
    }

}
