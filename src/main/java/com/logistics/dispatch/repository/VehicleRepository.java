
package com.logistics.dispatch.repository;

import com.logistics.dispatch.entity.Vehicle;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface VehicleRepository extends JpaRepository<Vehicle, Long> {

    Optional<Vehicle> findByVehicleId(String vehicleId);

    boolean existsByVehicleId(String vehicleId);

    List<Vehicle> findAllByVehicleIdIn(Collection<String> vehicleIds);

    List<Vehicle> findAllByOrderByVehicleIdAsc();
}
