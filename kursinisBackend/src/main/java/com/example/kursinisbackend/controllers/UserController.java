package com.example.kursinisbackend.controllers;

import com.example.kursinisbackend.dto.*;
import com.example.kursinisbackend.dto.user.*;
import com.example.kursinisbackend.model.*;
import com.example.kursinisbackend.repos.BasicUserRepo;
import com.example.kursinisbackend.repos.DriverRepo;
import com.example.kursinisbackend.repos.UserRepo;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/users")
public class UserController {
    @Autowired
    private UserRepo userRepo;
    @Autowired
    private BasicUserRepo basicUserRepo;
    @Autowired
    private DriverRepo driverRepo;
    @Autowired
    private PasswordEncoder passwordEncoder;


    @PostMapping("/login")
    public @ResponseBody ResponseEntity<?> validateUser(@Valid @RequestBody LoginRequestDTO dto) {
        User user = userRepo.findByLogin(dto.getLogin()).orElse(null);

        if (user == null || !user.isActive()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ErrorResponseDTO("User not found", HttpStatus.UNAUTHORIZED.value()));
        }
        if (!passwordEncoder.matches(dto.getPassword(), user.getPassword())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ErrorResponseDTO("Invalid password", HttpStatus.UNAUTHORIZED.value()));
        }
        UserType userType = user.getUserType();
        if (userType != UserType.BASIC && userType != UserType.DRIVER) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(new ErrorResponseDTO("User type not allowed", HttpStatus.FORBIDDEN.value()));
        }

        return ResponseEntity.ok(new LoginResponseDTO(
                user.getId(),
                user.getLogin(),
                user.getName(),
                user.getSurname(),
                userType.name()
        ));
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getUserById(@PathVariable int id) {
        User user = userRepo.findById(id).orElse(null);

        if (user == null || !user.isActive()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ErrorResponseDTO("User not found", HttpStatus.NOT_FOUND.value()));
        }

        Object response;

        if (user.getUserType() == UserType.BASIC) {
            response = (BasicUser) user;
        } else if (user.getUserType() == UserType.DRIVER) {
            response = (Driver) user;
        } else {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(new ErrorResponseDTO("User type not allowed", HttpStatus.FORBIDDEN.value()));
        }

        return ResponseEntity.ok(response);
    }


    @Transactional
    @PostMapping("/basic")
    public ResponseEntity<?> createClient(@Valid @RequestBody ClientCreateRequestDTO dto) {
        if (userRepo.existsByLogin(dto.getLogin())) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(new ErrorResponseDTO("Username taken!", HttpStatus.CONFLICT.value()));
        }

        BasicUser user = new BasicUser(
                dto.getLogin(),
                passwordEncoder.encode(dto.getPassword()),
                dto.getName(),
                dto.getSurname(),
                dto.getPhoneNumber(),
                dto.getAddress(),
                UserType.BASIC
        );
        user.setDateCreated(LocalDateTime.now());
        user.setDateUpdated(LocalDateTime.now());

        basicUserRepo.save(user);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new SuccessResponseDTO("User created successfully"));
    }

    @Transactional
    @PostMapping("/driver")
    public ResponseEntity<?> createDriver(@Valid @RequestBody DriverCreateRequestDTO dto) {
        if (userRepo.existsByLogin(dto.getLogin())) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(new ErrorResponseDTO("Username taken!", HttpStatus.CONFLICT.value()));
        }

        Driver driver = new Driver(
                dto.getLogin(),
                passwordEncoder.encode(dto.getPassword()),
                dto.getName(),
                dto.getSurname(),
                dto.getPhoneNumber(),
                dto.getAddress(),
                UserType.DRIVER,
                dto.getLicence(),
                dto.getBDate(),
                dto.getVehicleType()
        );

        driver.setDateCreated(LocalDateTime.now());
        driver.setDateUpdated(LocalDateTime.now());

        driverRepo.save(driver);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new SuccessResponseDTO("Driver created successfully"));
    }

    @Transactional
    @PutMapping("/update/basic/{id}")
    public ResponseEntity<?> updateClientDetails(@PathVariable int id, @Valid @RequestBody ClientUpdateRequestDTO dto) {
        User user = userRepo.findById(id).orElse(null);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ErrorResponseDTO("User not found", HttpStatus.NOT_FOUND.value()));
        }

        if (user.getUserType() != UserType.BASIC) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(new ErrorResponseDTO("Not a basic user", HttpStatus.FORBIDDEN.value()));
        }

        if (!user.getLogin().equals(dto.getLogin()) && userRepo.existsByLogin(dto.getLogin())) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(new ErrorResponseDTO("Username taken!", HttpStatus.CONFLICT.value()));
        }

        BasicUser basicUser = (BasicUser) user;
        basicUser.setLogin(dto.getLogin());
        basicUser.setName(dto.getName());
        basicUser.setSurname(dto.getSurname());
        basicUser.setPhoneNumber(dto.getPhoneNumber());
        basicUser.setAddress(dto.getAddress());
        basicUser.setDateUpdated(LocalDateTime.now());

        userRepo.save(basicUser);
        return ResponseEntity.ok(new SuccessResponseDTO("Client user updated"));
    }

    @Transactional
    @PutMapping("/update/driver/{id}")
    public ResponseEntity<?> updateDriver(@PathVariable int id, @Valid @RequestBody DriverUpdateRequestDTO dto) {
        User user = userRepo.findById(id).orElse(null);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ErrorResponseDTO("User not found", HttpStatus.NOT_FOUND.value()));
        }
        if (user.getUserType() != UserType.DRIVER) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(new ErrorResponseDTO("Not a driver", HttpStatus.FORBIDDEN.value()));
        }

        if (!user.getLogin().equals(dto.getLogin()) && userRepo.existsByLogin(dto.getLogin())) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(new ErrorResponseDTO("Username taken!", HttpStatus.CONFLICT.value()));
        }

        Driver driver = (Driver) user;
        driver.setLogin(dto.getLogin());
        driver.setName(dto.getName());
        driver.setSurname(dto.getSurname());
        driver.setPhoneNumber(dto.getPhoneNumber());
        driver.setAddress(dto.getAddress());
        driver.setLicence(dto.getLicence());
        driver.setBDate(dto.getBDate());
        driver.setVehicleType(dto.getVehicleType());
        driver.setDateUpdated(LocalDateTime.now());

        driverRepo.save(driver);
        return ResponseEntity.ok(new SuccessResponseDTO("Driver user updated"));
    }

    @PutMapping("/password/{id}")
    public ResponseEntity<?> updatePassword(@PathVariable int id, @Valid @RequestBody PasswordUpdateRequestDTO dto) {
        User user = userRepo.findById(id).orElse(null);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ErrorResponseDTO("User not found", HttpStatus.NOT_FOUND.value()));
        }

        if (!passwordEncoder.matches(dto.getOldPassword(), user.getPassword())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ErrorResponseDTO("Invalid old password", HttpStatus.UNAUTHORIZED.value()));
        }

        if (!dto.getNewPassword1().equals(dto.getNewPassword2()))
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ErrorResponseDTO("Passwords do not match", HttpStatus.BAD_REQUEST.value()));

        user.setPassword(passwordEncoder.encode(dto.getNewPassword1()));
        user.setDateUpdated(LocalDateTime.now());

        userRepo.save(user);
        return ResponseEntity.ok(new SuccessResponseDTO("Password updated"));
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable int id) {
        User user = userRepo.findById(id).orElse(null);

        if (user == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ErrorResponseDTO("User not found", HttpStatus.NOT_FOUND.value()));
        }

        user.setActive(false);
        user.setDateUpdated(LocalDateTime.now());
        userRepo.save(user);
        return ResponseEntity.ok(new SuccessResponseDTO("User deactivated"));
    }
}
