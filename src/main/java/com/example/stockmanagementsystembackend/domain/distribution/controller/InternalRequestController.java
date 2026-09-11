/**
 * REST Controller providing API endpoints for department stock request operations.
 */
package com.example.stockmanagementsystembackend.domain.distribution.controller;

import com.example.stockmanagementsystembackend.domain.distribution.entity.InternalRequest;
import com.example.stockmanagementsystembackend.domain.distribution.service.InternalRequestService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@CrossOrigin
@RestController
@RequestMapping(value = "/api/internal-requests")
public class InternalRequestController {

    private final InternalRequestService internalRequestService;

    public InternalRequestController(InternalRequestService internalRequestService){
        this.internalRequestService=internalRequestService;
    }

    @GetMapping
    public ResponseEntity<?> getALLRequest(){
        return ResponseEntity.ok(internalRequestService.getAllRequest());

    }

    @PostMapping
    public ResponseEntity<?> createRequest(@RequestBody InternalRequest request){
        return ResponseEntity.ok(internalRequestService.createRequest(request));

    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateRequest(
            @PathVariable Integer id,
            @RequestBody InternalRequest request) {
        return ResponseEntity.ok(internalRequestService.updateRequest(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteRequest(@PathVariable Integer id) {
        internalRequestService.deleteRequest(id);
        return ResponseEntity.ok().build();
    }

}
