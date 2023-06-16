package bsep.api;

import bsep.alarming.Rule;
import bsep.alarming.RuleEngine;
import bsep.logging.Log;
import bsep.users.User;

import static bsep.util.Utils.coalesce;

import org.drools.io.ReaderResource;
import org.drools.kiesession.rulebase.KnowledgeBaseFactory;

import org.kie.api.io.ResourceType;
import org.kie.internal.builder.KnowledgeBuilderError;
import org.kie.internal.builder.KnowledgeBuilderFactory;
import org.kie.internal.io.ResourceFactory;

import io.quarkus.panache.common.Page;
import io.quarkus.panache.common.Sort;

import jakarta.annotation.security.RolesAllowed;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;

@Path("logs")
@Produces(MediaType.APPLICATION_JSON)
public class Logs extends Resource {

    @GET
    @RolesAllowed({ User.Roles.ADMIN })
    public Response getLogsBeforeDate(
            @QueryParam("before") String before,
            @QueryParam("after") String after,
            @QueryParam("regex") String regex,
            @QueryParam("level") String level
    )
    {
        if (before == null && after == null) return badRequest("Please provide a before or after date.");
        if (regex == null || regex.isEmpty()) regex = ".*";

        var query = after != null ? "timestamp > ?1" : "timestamp < ?1";
        if (level != null && !level.isEmpty()) query += " and level = ?3";

        var value = coalesce(after, before);
        var date = LocalDateTime.now();

        try { date = LocalDateTime.parse(value); }
        catch (Exception e) { return badRequest("Specified date format is not valid."); }

        var logs = Log
                .find(query + " and message like ?2",
                        Sort.by("timestamp", Sort.Direction.Descending),
                        date, regex, level
                )
                .page(Page.ofSize(20))
                .list();

        return ok(logs);
    }

    @GET
    @Path("/test-drools")
    public Response testDrools() {

        var rule = """
            package rules;
                            
            rule "String test rule"
                when
                    $s: String()
                then
                    System.out.println("String: " + $s);
            end
        """;

        var compilationErrors = RuleEngine.checkRule(new Rule(rule));
        if (compilationErrors.size() > 0) return badRequest(compilationErrors);

        try {

            var knowledgeBuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();

            var errors = knowledgeBuilder.getErrors();

            for (KnowledgeBuilderError error : errors) {
                System.out.println(error);
            }

            var knowledgeBase = KnowledgeBaseFactory.newKnowledgeBase();
            knowledgeBase.addPackages(knowledgeBuilder.getKnowledgePackages());

            var kieSession = knowledgeBase.newKieSession();

            kieSession.insert("Hello World");
            var rulesFired = kieSession.fireAllRules();
            io.quarkus.logging.Log.warn("Rules fired: " + rulesFired);

            // ########### MAGIC! ###########
            knowledgeBuilder.add(ResourceFactory.newByteArrayResource(rule.getBytes(StandardCharsets.UTF_8)), ResourceType.DRL);
            knowledgeBase.addPackages(knowledgeBuilder.getKnowledgePackages());
            // ##############################

            rulesFired = kieSession.fireAllRules();
            io.quarkus.logging.Log.warn("Rules fired: " + rulesFired);

            knowledgeBuilder.add(ResourceFactory.newByteArrayResource(rule.getBytes(StandardCharsets.UTF_8)), ResourceType.DRL);
            knowledgeBase.addPackages(knowledgeBuilder.getKnowledgePackages());

            rulesFired = kieSession.fireAllRules();
            io.quarkus.logging.Log.warn("Rules fired: " + rulesFired);

            return ok("OK");

        }
        catch (Exception e) {
            e.printStackTrace();
            return badRequest("Error");
        }

    }
}
