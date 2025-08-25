package com.trash2cash.users.controller;

import com.trash2cash.users.model.WasteListing;
import com.trash2cash.users.service.WasteListingService;
import com.trash2cash.users.dto.WasteListingRequest;
import com.trash2cash.users.utils.ListingResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Tag(name = "Waste Listing")
@RequestMapping("/listings")
public class WasteListingController {
    private final WasteListingService wasteListingService;

    @Operation(
            summary = "Create a waste listing",
            description = "Creates a new waste listing with an image file and other details.",
            requestBody = @RequestBody(
                    required = true,
                    description = "Waste listing details",
                    content = @Content(
                            mediaType = "multipart/form-data",
                            schema = @Schema(implementation = WasteListingRequest.class)
                    )
            ),
            responses = {
                    @ApiResponse(responseCode = "201", description = "Listing created successfully",
                            content = @Content(schema = @Schema(implementation = WasteListing.class))),
                    @ApiResponse(responseCode = "400", description = "Invalid input", content = @Content)
            }
    )
    @PostMapping
    public ResponseEntity<ListingResponse> createListing(@RequestBody WasteListingRequest request) {
        ListingResponse listing = wasteListingService.createListing(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(listing);
    }
    @GetMapping
    @Operation(
            summary = "Get all waste listings",
            description = "Retrieves all available waste listings to be displayed on the dashboard"
    )
    public ResponseEntity<List<WasteListing>> getAllListings() {
        return ResponseEntity.ok(wasteListingService.getAllListings());
    }
}
