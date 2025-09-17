package com.trash2cash.waste;
import com.trash2cash.users.model.UserInfoUserDetails;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/recycler/listings")
@RequiredArgsConstructor
@Tag(name = "Recycler Listings", description = "Endpoints for recyclers to manage waste listings")
public class RecyclerListingController {

    private final WasteListingService listingService;

    @Operation(
            summary = "Accept a waste listing",
            description = """
            This endpoint allows a recycler to accept a waste listing that has been 
            posted by a generator. 

            Once accepted:
            - The recycler is assigned to the listing.
            - The listing status changes to **SCHEDULED**.
            - A default pickup schedule is automatically created 
              (next day at 10:00 AM, at the generator’s pickup location).
            - A notification is sent to the generator informing them of the acceptance.

            ⚠️ **Important**: 
            - Only recyclers can call this endpoint.
            - If the listing has already been accepted, this will throw an error.
            """
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Listing accepted successfully, schedule created",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = AcceptListingResponse.class))),
            @ApiResponse(responseCode = "404", description = "Recycler or listing not found",
                    content = @Content(schema = @Schema(example = "{ \"error\": \"Listing not found\" }"))),
            @ApiResponse(responseCode = "400", description = "Listing already accepted",
                    content = @Content(schema = @Schema(example = "{ \"error\": \"Listing already accepted\" }"))),
            @ApiResponse(responseCode = "403", description = "Unauthorized access (not a recycler)",
                    content = @Content(schema = @Schema(example = "{ \"error\": \"Access denied\" }")))
    })
    @PreAuthorize("hasRole('RECYCLER')")
    @PostMapping("/{listingId}/accept")
    public ResponseEntity<AcceptListingResponse> acceptListing(
            @AuthenticationPrincipal UserInfoUserDetails principal,
            @PathVariable Long listingId
    ) {
        String recyclerEmail = principal.getUsername();
        AcceptListingResponse response = listingService.acceptListing(listingId, recyclerEmail);
        return ResponseEntity.ok(response);
    }
}
