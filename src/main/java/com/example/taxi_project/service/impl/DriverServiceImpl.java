package com.example.taxi_project.service.impl;

import com.example.taxi_project.dto.admin.DriverResponseDTO;
import com.example.taxi_project.dto.driver.CarUpdate;
import com.example.taxi_project.dto.driver.DriverResponse;
import com.example.taxi_project.dto.driver.DriverUpdate;
import com.example.taxi_project.enums.ApplicationStatus;
import com.example.taxi_project.enums.DriverStatus;
import com.example.taxi_project.enums.UserRole;
import com.example.taxi_project.exceptions.ResourceNotFoundException;
import com.example.taxi_project.exceptions.UserAlreadyExistsException;
import com.example.taxi_project.model.Cars;
import com.example.taxi_project.model.Driver;
import com.example.taxi_project.model.User;
import com.example.taxi_project.repository.DriverCarsRepository;
import com.example.taxi_project.repository.DriverRepository;
import com.example.taxi_project.repository.UserRepository;
import com.example.taxi_project.security.CustomUserDetails;
import com.example.taxi_project.service.DriverService;
import jakarta.xml.bind.ValidationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;


@Slf4j
@Service
@RequiredArgsConstructor
public class DriverServiceImpl implements DriverService {

    private final DriverRepository driverRepository;
    private final DriverCarsRepository driverCarsRepository;
    private final UserRepository usersRepository;

    @Override
    public DriverResponse getById(UserDetails userDetails) {

        Driver driver = driverRepository.findDriverByPhone(userDetails.getUsername())
                .orElseThrow(() -> new ResourceNotFoundException("Haydovchi topilmadi: " + userDetails.getUsername()));

        return toResponse(driver);
    }

    @Override
    public DriverResponse update(UserDetails userDetails, DriverUpdate updateDto) {
        Driver driver = driverRepository.findDriverByPhone(userDetails.getUsername())
                .orElseThrow(() -> new ResourceNotFoundException("Haydovchi topilmadi"));

        driver.setName(updateDto.getName());
        driver.setUsername(updateDto.getUsername());
        Driver saved = driverRepository.save(driver);
        return toResponse(saved);
    }

    @Override
    public void changeStatus(CustomUserDetails userDetails, DriverStatus status) {
        Driver driver = driverRepository.findById(userDetails.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Haydovchi topilmadi: " + userDetails.getDriver().getId()));

        driver.setStatus(status);
        driverRepository.save(driver);

        System.out.println("Haydovchi " + userDetails.getDriver().getId() + " holati o'zgardi: " + status);
    }

    @Override
    public BigDecimal getBalance(CustomUserDetails userDetails) {
        Driver driver = driverRepository.findById(userDetails.getDriver().getId())
                .orElseThrow(() -> new ResourceNotFoundException("Haydovchi topilmadi: " + userDetails.getDriver().getId()));


        return BigDecimal.ZERO;
    }

    @Override
    public void updateCarInfo(UUID id, CarUpdate carUpdateDto) {
        Driver driver = driverRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Haydovchi topilmadi: " + id));

        Cars car = driver.getCar();
        if (carUpdateDto.getModel() != null) car.setModel(carUpdateDto.getModel());
        car.setPrice(carUpdateDto.getPrice());
        car.setPlace(carUpdateDto.getPlace());
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

        User user = usersRepository.findByChatId(chatId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Cars car = new Cars();

        data.forEach((key, value) -> {
            if (key.equals("model")) {
                car.setModel(value);
            }
            if (key.equals("passport_image")) {
                user.setPassport_image(value);
            }
            if (key.equals("license_image")) {
                user.setLicence_image(value);
            }
            if (key.equals("car_image")) {
                car.setPicture(value);
            }
        });
        user.setStatus_application(ApplicationStatus.PENDING);
        user.setCars(car);

        usersRepository.save(user);
        driverCarsRepository.save(car);

    }

    @Override
    public List<DriverResponseDTO> getAllPendingApplications() {
        List<User> all = usersRepository.findAllByStatusApplication(ApplicationStatus.PENDING);

        List<DriverResponseDTO> list = new ArrayList<>();
        for (User user : all) {

                 DriverResponseDTO driverResponseDTO = new DriverResponseDTO();
                list.add(driverResponseDTO);
        }

        return list;
    }

    @Override
    @Transactional
    public void approveDriver(UUID userId) throws ValidationException {

        User user = usersRepository.findUserById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("user.not.found"));

        if (driverRepository.existsById(userId)) {
            throw new UserAlreadyExistsException("driver.already.approved");
        }

        if (user.getStatus_application() == ApplicationStatus.BANNED) {
            throw new ValidationException("user.banned");
        }

        if (user.getCount_application_status() >= 5) {
            user.setStatus_application(ApplicationStatus.BANNED);
            usersRepository.save(user);
            return;
        }

        Cars car = user.getCars();
        if (car == null) {
            throw new ResourceNotFoundException("car.not.found");
        }

        Driver driver = new Driver();
        driver.setName(user.getName());
        driver.setPhone(user.getPhone());
        driver.setRole(UserRole.DRIVER);
        driver.setUsername(user.getUsername());
        driver.setRating(0.0);
        driver.setActive(true);
        driver.setChat_id(user.getChat_id());

        driverRepository.save(driver);

        car.setDriver_id(driver);
        driver.setCar(car);
        driverCarsRepository.save(car);

        user.setRole(UserRole.DRIVER);
        user.setActive(false);
        user.setChat_id(null);
        user.setCars(null);
        usersRepository.save(user);

        log.info("Driver successfully approved for userId: {}, username: {}", userId, user.getUsername());
    }

    @Override
    public void rejectDriver(UUID userId, String reason) {
        User user = usersRepository.findUserById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("user.not.found"));

        if (user.getStatus_application() == ApplicationStatus.BANNED) {
            throw new RuntimeException("user.banned");
        }

        int newCount = user.getCount_application_status() + 1;
        user.setCount_application_status(newCount);

        if (newCount >= 5) {
            user.setStatus_application(ApplicationStatus.BANNED);
        } else {
            user.setStatus_application(ApplicationStatus.REJECTED);
        }

        usersRepository.save(user);
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