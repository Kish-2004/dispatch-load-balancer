package com.logistics.dispatch.controller;


import com.logistics.dispatch.dto.response.DispatchPlanResponse;
import com.logistics.dispatch.service.DispatchService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;
        import static org.mockito.Mockito.*;

class DispatchControllerTest {

    private DispatchService dispatchService;
    private DispatchController dispatchController;

    @BeforeEach
    void setUp() {
        dispatchService = mock(DispatchService.class);
        dispatchController = new DispatchController(dispatchService);
    }

    @Test
    void shouldReturnDispatchPlanSuccessfully() {

        DispatchPlanResponse expectedResponse =
                mock(DispatchPlanResponse.class);

        when(dispatchService.generateDispatchPlan())
                .thenReturn(expectedResponse);

        ResponseEntity<DispatchPlanResponse> response =
                dispatchController.getDispatchPlan();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertSame(expectedResponse, response.getBody());

        verify(dispatchService, times(1)).generateDispatchPlan();
    }

    @Test
    void shouldPropagateServiceException() {

        when(dispatchService.generateDispatchPlan())
                .thenThrow(new RuntimeException("Dispatch failed"));

        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> dispatchController.getDispatchPlan()
        );

        assertEquals("Dispatch failed", exception.getMessage());

        verify(dispatchService).generateDispatchPlan();
    }
}
