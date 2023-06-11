package bsep.devices;

import io.quarkus.mongodb.panache.PanacheMongoEntity;

import java.util.List;
import java.util.Map;

public class Device extends PanacheMongoEntity {

    public String name;
    public String brand;
    public String ownerId;
    public String buildingId;
    public DeviceType type;

    public enum DeviceCategory {
        HOUSEHOLD_SECURITY,
        LIGHTING,
        POWER_SWITCH,
        SENSOR,
        AIR_TREATMENT,
        ROUTER_AND_GATEWAY,
        KITCHEN_ELECTRONICS,
        CLEANING_APPLIANCES
    }

    public static Map<DeviceCategory, String> categories = Map.of(
        DeviceCategory.HOUSEHOLD_SECURITY, "Household security",
        DeviceCategory.LIGHTING, "Lighting",
        DeviceCategory.POWER_SWITCH, "Power switch",
        DeviceCategory.SENSOR, "Sensor",
        DeviceCategory.AIR_TREATMENT, "Air treatment",
        DeviceCategory.ROUTER_AND_GATEWAY, "Router and gateway",
        DeviceCategory.KITCHEN_ELECTRONICS, "Kitchen electronics",
        DeviceCategory.CLEANING_APPLIANCES, "Cleaning appliances"
    );

    public static class DeviceType {

        public String name;
        public int id;
        public DeviceCategory category;
        public String categoryName;

        public DeviceType() {}

        public DeviceType(int id, DeviceCategory category, String name) {
            this.id = id;
            this.category = category;
            this.name = name;
            this.categoryName = categories.get(category);
        }

        public String getImage() {
            return "/images/" + this.id + "_" + this.category.name().toLowerCase() + ".png";
        }
    }

    public static List<List<DeviceType>> devices = List.of(
        List.of(
            new DeviceType(1, DeviceCategory.HOUSEHOLD_SECURITY, "Video camera"),
            new DeviceType(2, DeviceCategory.HOUSEHOLD_SECURITY, "Visible Doorbell")
        ),
        List.of(
            new DeviceType(3, DeviceCategory.LIGHTING, "Light strips"),
            new DeviceType(4, DeviceCategory.LIGHTING, "Desk lamp"),
            new DeviceType(5, DeviceCategory.LIGHTING, "Bedside lamp"),
            new DeviceType(6, DeviceCategory.LIGHTING, "Ceiling light"),
            new DeviceType(7, DeviceCategory.LIGHTING, "Pendant lamp"),
            new DeviceType(8, DeviceCategory.LIGHTING, "Light bulb")
        ),
        List.of(
            new DeviceType(9, DeviceCategory.POWER_SWITCH, "Power outlet"),
            new DeviceType(10, DeviceCategory.POWER_SWITCH, "Light switch")
        ),
        List.of(
            new DeviceType(11, DeviceCategory.SENSOR, "Temperature/Humidity sensor"),
            new DeviceType(12, DeviceCategory.SENSOR, "Water leak sensor"),
            new DeviceType(13, DeviceCategory.SENSOR, "Window/Door sensor"),
            new DeviceType(14, DeviceCategory.SENSOR, "Photosensor")
        ),
        List.of(
            new DeviceType(15, DeviceCategory.AIR_TREATMENT, "Air humidifier"),
            new DeviceType(16, DeviceCategory.AIR_TREATMENT, "Electric heater"),
            new DeviceType(17, DeviceCategory.AIR_TREATMENT, "Air conditioner"),
            new DeviceType(18, DeviceCategory.AIR_TREATMENT, "Air Purifier")
        ),
        List.of(
            new DeviceType(19, DeviceCategory.ROUTER_AND_GATEWAY, "Router"),
            new DeviceType(20, DeviceCategory.ROUTER_AND_GATEWAY, "Gateway")
        ),
        List.of(
            new DeviceType(21, DeviceCategory.KITCHEN_ELECTRONICS, "Air fryer "),
            new DeviceType(22, DeviceCategory.KITCHEN_ELECTRONICS, "Dishwasher")
        ),
        List.of(
            new DeviceType(23, DeviceCategory.CLEANING_APPLIANCES, "Robot vacuum")
        )
    );

    public Device() {}

    public Device(String name, String brand, String ownerId, String buildingId, DeviceType type) {
        this.name = name;
        this.brand = brand;
        this.ownerId = ownerId;
        this.buildingId = buildingId;
        this.type = type;
    }

    public static DeviceType getTypeById(int id) {
        for (var category : devices) {
            for (var type : category) {
                if (type.id == id) {
                    return type;
                }
            }
        }
        return null;
    }

}
