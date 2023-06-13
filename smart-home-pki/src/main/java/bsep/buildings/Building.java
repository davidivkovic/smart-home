package bsep.buildings;

import bsep.api.dto.users.UserDTO;

import io.quarkus.mongodb.panache.PanacheMongoEntity;

import java.util.List;

public class Building extends PanacheMongoEntity {

    public enum BuildingCategory {
        APARTMENT,
        HOUSE,
        BUSINESS,
        GARAGE
    }

    public static class BuildingType {

        public BuildingCategory category;
        public String categoryName;

        public BuildingType() {}

        public BuildingType(BuildingCategory category, String categoryName) {
            this.category = category;
            this.categoryName = categoryName;
        }

        public String getImage() {
            return "/images/building" + "_" + category.name().toLowerCase() + ".png";
        }

    }

    public String name;
    public String address;
    public BuildingType type;
    public String landlordId;
    public List<UserDTO> tenants;

    public Building() {}

    public Building(String name, String address, BuildingCategory category, String landlordId) {
        this.name = name;
        this.address = address;
        this.type = types.get(category.ordinal());
        this.landlordId = landlordId;
        this.tenants = List.of();
    }

    public static List<BuildingType> types = List.of(
        new BuildingType(BuildingCategory.APARTMENT, "Apartment"),
        new BuildingType(BuildingCategory.HOUSE, "House"),
        new BuildingType(BuildingCategory.BUSINESS, "Business"),
        new BuildingType(BuildingCategory.GARAGE, "Garage")
    );

    public boolean visibleTo(String userId) {
        return this.landlordId.equals(userId) || this.tenants.stream().anyMatch(tenant -> tenant.id.equals(userId));
    }

}
