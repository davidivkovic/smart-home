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
        public int id;
        public DeviceCategory category;
        public String categoryName;
        public String name;
        public String image;

        public DeviceType(int id, DeviceCategory category, String name, String image) {
            this.id = id;
            this.category = category;
            this.name = name;
            this.image = image;
            this.categoryName = categories.get(category);
        }
    }

    public static Map<DeviceCategory, List<DeviceType>> devices = Map.of(
        DeviceCategory.HOUSEHOLD_SECURITY, List.of(
            new DeviceType(1, DeviceCategory.HOUSEHOLD_SECURITY, "Video camera", "video_camera.png"),
            new DeviceType(2, DeviceCategory.HOUSEHOLD_SECURITY, "Visible Doorbell", "visible_doorbell.png")
        ),
        DeviceCategory.LIGHTING, List.of(
            new DeviceType(3, DeviceCategory.LIGHTING, "Light strips", "light_strips.png"),
            new DeviceType(4, DeviceCategory.LIGHTING, "Desk lamp", "desk_lamp.png"),
            new DeviceType(5, DeviceCategory.LIGHTING, "Bedside lamp", "bedside_lamp.png"),
            new DeviceType(6, DeviceCategory.LIGHTING, "Ceiling light", "ceiling_light.png"),
            new DeviceType(7, DeviceCategory.LIGHTING, "Pendant lamp", "pendant_lamp.png"),
            new DeviceType(8, DeviceCategory.LIGHTING, "Light bulb", "light_bulb.png")
        ),
        DeviceCategory.POWER_SWITCH, List.of(
            new DeviceType(9, DeviceCategory.POWER_SWITCH, "Power outlet", "power_outlet.png"),
            new DeviceType(10, DeviceCategory.POWER_SWITCH, "Light switch", "light_switch.png")
        ),
        DeviceCategory.SENSOR, List.of(
            new DeviceType(11, DeviceCategory.SENSOR, "Temperature/Humidity sensor", "temp_hum_sensor.png"),
            new DeviceType(12, DeviceCategory.SENSOR, "Water leak sensor", "water_sensor.png"),
            new DeviceType(13, DeviceCategory.SENSOR, "Window/Door sensor", "win_door_sensor.png"),
            new DeviceType(14, DeviceCategory.SENSOR, "Photosensor", "photo_sensor.png")
        ),
        DeviceCategory.AIR_TREATMENT, List.of(
            new DeviceType(15, DeviceCategory.AIR_TREATMENT, "Air humidifier", "humidifier.png"),
            new DeviceType(16, DeviceCategory.AIR_TREATMENT, "Electric heater", "heater.png"),
            new DeviceType(17, DeviceCategory.AIR_TREATMENT, "Air conditioner", "conditioner.png"),
            new DeviceType(18, DeviceCategory.AIR_TREATMENT, "Air Purifier", "purifier.png")
        ),
        DeviceCategory.ROUTER_AND_GATEWAY, List.of(
            new DeviceType(19, DeviceCategory.ROUTER_AND_GATEWAY, "Router", "router.png"),
            new DeviceType(20, DeviceCategory.ROUTER_AND_GATEWAY, "Gateway", "gateway.png")
        ),
        DeviceCategory.KITCHEN_ELECTRONICS, List.of(
            new DeviceType(21, DeviceCategory.KITCHEN_ELECTRONICS, "Air fryer ", "air_fryer.png"),
            new DeviceType(22, DeviceCategory.KITCHEN_ELECTRONICS, "Dishwasher", "dishwasher.png")
        ),
        DeviceCategory.CLEANING_APPLIANCES, List.of(
            new DeviceType(23, DeviceCategory.CLEANING_APPLIANCES, "Robot vacuum", "robot_vacuum.png")
        )
    );

    public Device(String name, String brand, String ownerId, String buildingId, DeviceType type) {
        this.name = name;
        this.brand = brand;
        this.ownerId = ownerId;
        this.buildingId = buildingId;
        this.type = type;
    }

    public static DeviceType getTypeById(int id) {
        for (DeviceCategory category : devices.keySet()) {
            for (DeviceType type : devices.get(category)) {
                if (type.id == id) {
                    return type;
                }
            }
        }
        return null;
    }

}
