package bsep.buildings;

import bsep.api.dto.users.UserDTO;

import io.quarkus.mongodb.panache.PanacheMongoEntity;

import java.util.List;

public class Building extends PanacheMongoEntity {

    public String landlordId;
    public List<UserDTO> tenants;

    public boolean visibleTo(String userId) {
        return this.landlordId.equals(userId) || this.tenants.stream().anyMatch(tenant -> tenant.id.equals(userId));
    }

}
