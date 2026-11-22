package com.logistics.dispatch.service;


import com.logistics.dispatch.dto.request.VehicleBatchRequest;
import com.logistics.dispatch.dto.request.VehicleRequest;
import com.logistics.dispatch.dto.response.VehicleResponse;
import com.logistics.dispatch.entity.Vehicle;
import com.logistics.dispatch.repository.VehicleRepository;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class VehicleService {

    private final VehicleRepository vehicleRepository;

    public VehicleService(VehicleRepository vehicleRepository) {
        this.vehicleRepository = vehicleRepository;
    }

    @Transactional
    public void createVehicles(VehicleBatchRequest request) {

        List<VehicleRequest> vehicles = request.vehicles();

        // 1. Check duplicate IDs within the incoming request.
        Set<String> incomingIds = new HashSet<>();

        for (VehicleRequest vehicle : vehicles) {

            String vehicleId = vehicle.vehicleId().trim();

            if (!incomingIds.add(vehicleId)) {
                throw new ResponseStatusException(
                        HttpStatus.CONFLICT,
                        "Duplicate vehicle ID in request: " + vehicleId
                );
            }
        }

        // 2. Check IDs already present in the database.
        List<Vehicle> existingVehicles =
                vehicleRepository.findAllByVehicleIdIn(
                        incomingIds.stream().toList()
                );

        if (!existingVehicles.isEmpty()) {

            String duplicateId =
                    existingVehicles.get(0).getVehicleId();

            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Vehicle already exists: " + duplicateId
            );
        }

        // 3. Convert request DTOs into entities.
        List<Vehicle> entities = new ArrayList<>();

        for (VehicleRequest vehicle : vehicles) {

            Vehicle entity = new Vehicle();

            entity.setVehicleId(vehicle.vehicleId().trim());
            entity.setCapacity(vehicle.capacity());
            entity.setCurrentLatitude(vehicle.currentLatitude());
            entity.setCurrentLongitude(vehicle.currentLongitude());
            entity.setCurrentAddress(vehicle.currentAddress().trim());

            entities.add(entity);
        }

        // 4. Save all vehicles in one transaction.
        vehicleRepository.saveAll(entities);
    }

}
