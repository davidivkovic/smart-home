package bsep.alarming;

import bsep.buildings.Building;

import io.quarkus.mongodb.panache.PanacheMongoEntity;

public class AlarmTriggered extends PanacheMongoEntity {

    public String alarmName;
    public String message;
    public Building building;
    public boolean handled = false;

}
