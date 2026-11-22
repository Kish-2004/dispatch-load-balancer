package com.logistics.dispatch.controller;


import com.logistics.dispatch.dto.response.DispatchPlanResponse;
import com.logistics.dispatch.service.DispatchService;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/dispatch")
public class DispatchController {

    private final DispatchService dispatchService;

    public DispatchController(DispatchService dispatchService) {
        this.dispatchService = dispatchService;
    }

    @GetMapping("/plan")
    public ResponseEntity<DispatchPlanResponse> getDispatchPlan() {

        DispatchPlanResponse response =
                dispatchService.generateDispatchPlan();

        return ResponseEntity.ok(response);
    }
}
