package com.darTabkh.darTabkh.controller;

import com.darTabkh.darTabkh.dto.UserResponse;
import com.darTabkh.darTabkh.service.CookerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.constraints.NotBlank;
import java.util.List;

@RestController
@RequestMapping("/api/cooker")
@RequiredArgsConstructor
@Tag(name = "Cooker", description = "Cooker management APIs")
@SecurityRequirement(name = "Bearer Authentication")
public class CookerController {

    private final CookerService cookerService;

    @GetMapping
    @Operation(summary = "Get all cookers")
    @ApiResponse(responseCode = "200", description = "Cookers retrieved successfully")
    @ApiResponse(responseCode = "401", description = "Unauthorized")
    public ResponseEntity<List<UserResponse>> getAllCookers() {
        List<UserResponse> cookers = cookerService.getAllCookers();
        return ResponseEntity.ok(cookers);
    }

    @GetMapping("/search")
    @Operation(summary = "Get cookers by city")
    @ApiResponse(responseCode = "200", description = "Cookers retrieved successfully")
    @ApiResponse(responseCode = "400", description = "Invalid city parameter")
    @ApiResponse(responseCode = "401", description = "Unauthorized")
    public ResponseEntity<List<UserResponse>> getCookersByCity(
            @RequestParam @NotBlank(message = "City is required") String city) {
        List<UserResponse> cookers = cookerService.getCookersByCity(city);
        return ResponseEntity.ok(cookers);
    }
}
