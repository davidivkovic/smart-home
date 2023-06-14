package bsep.api;

import bsep.logging.Log;
import bsep.users.User;

import static bsep.util.Utils.coalesce;

import io.quarkus.panache.common.Page;
import io.quarkus.security.Authenticated;

import jakarta.annotation.security.RolesAllowed;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

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

        var query = after != null ? "timestamp > ?1" : "timestamp <= ?1";
        if (level != null && !level.isEmpty()) query += " and level = ?3";

        var value = coalesce(after, before);
        var date = LocalDateTime.now();

        try { date = LocalDateTime.parse(value); }
        catch (Exception e) { return badRequest("Specified date format is not valid."); }

        var logs = Log
                .find(query + " and message like ?2 order by timestamp desc", date, "/" + regex + "/i", level)
                .page(Page.ofSize(20))
                .list();

        return ok(logs);
    }
}
