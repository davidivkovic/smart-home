package bsep.logging;

import io.quarkus.mongodb.panache.PanacheMongoEntity;

import java.time.LocalDateTime;

public class Log extends PanacheMongoEntity {

    public LocalDateTime timestamp;
    public String message;
    public String level;
    public String service;

}
