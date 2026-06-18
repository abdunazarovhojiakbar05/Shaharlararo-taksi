package com.example.taxi_project.service.impl;

import com.example.taxi_project.dto.admin.DriverResponseDTO;
import com.example.taxi_project.dto.driver.CarUpdate;
import com.example.taxi_project.dto.driver.DriverResponse;
import com.example.taxi_project.dto.driver.DriverUpdate;
import com.example.taxi_project.enums.DriverStatus;
import com.example.taxi_project.exceptions.ResourceNotFoundException;
import com.example.taxi_project.model.Cars;
import com.example.taxi_project.model.Driver;
import com.example.taxi_project.repository.DriverCarsRepository;
import com.example.taxi_project.repository.DriverRepository;
import com.example.taxi_project.service.DriverService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DriverServiceImpl implements DriverService {

    private final DriverRepository driverRepository;
    private final DriverCarsRepository driverCarsRepository;

    @Override
    public DriverResponse getById(UUID id) {
        Driver driver = driverRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Haydovchi topilmadi: " + id));

        return toResponse(driver);
    }

    @Override
    public DriverResponse update(UUID id, DriverUpdate updateDto) {
        Driver driver = driverRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Haydovchi topilmadi: " + id));

        Driver saved = driverRepository.save(driver);
        return toResponse(saved);
    }

    @Override
    public void changeStatus(UUID id, DriverStatus status) {
        Driver driver = driverRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Haydovchi topilmadi: " + id));

        driver.setStatus(status);
        driverRepository.save(driver);

        System.out.println("Haydovchi " + id + " holati o'zgardi: " + status);
    }

    @Override
    public BigDecimal getBalance(UUID id) {
        Driver driver = driverRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Haydovchi topilmadi: " + id));
        return BigDecimal.ZERO;
    }

    @Override
    public void updateCarInfo(UUID id, CarUpdate carUpdateDto) {
        Driver driver = driverRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Haydovchi topilmadi: " + id));

        Cars car = driver.getCar();
        if (carUpdateDto.getModel() != null) car.setModel(carUpdateDto.getModel());
        driverCarsRepository.save(car);

        System.out.println("Haydovchi " + id + " mashinasi yangilandi");
    }

    @Override
    public Double getRating(UUID id) {
        Driver driver = driverRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Haydovchi topilmadi: " + id));

        return driver.getRating();
    }

    @Override
    public void apply(long chatId, Map<String, String> data) {

    }

    @Override
    public List<DriverResponseDTO> getAllPendingApplications() {
            return List.of();
    }

    @Override
    public void approveDriver(Long userId) {

    }

    @Override
    public void rejectDriver(Long userId, String reason) {

    }

    private DriverResponse toResponse(Driver driver) {

        return DriverResponse.builder()
                .id(driver.getId())
                .name(driver.getName())
                .username(driver.getUsername())
                .phone(driver.getPhone())
                .rating(driver.getRating())
                .role(driver.getRole())
                .is_active(driver.isActive())
                .build();
    }
}