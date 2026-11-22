package com.logistics.dispatch;


import com.logistics.dispatch.dto.response.DispatchPlanResponse;
import com.logistics.dispatch.exception.GlobalExceptionHandler;
import com.logistics.dispatch.service.DispatchService;
import com.logistics.dispatch.service.OrderService;
import com.logistics.dispatch.service.VehicleService;

import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.*;
        import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
        import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest
@AutoConfigureMockMvc(addFilters = false)
@Import({
        com.logistics.dispatch.controller.OrderController.class,
        com.logistics.dispatch.controller.VehicleController.class,
        com.logistics.dispatch.controller.DispatchController.class,
        GlobalExceptionHandler.class
})
class DispatchApiIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private OrderService orderService;

    @MockBean
    private VehicleService vehicleService;

    @MockBean
    private DispatchService dispatchService;

    // -----------------------------------------
    // ORDER API TESTS
    // -----------------------------------------

    @Test
    void shouldAcceptValidOrders() throws Exception {

        String request = """
                {
                    "orders": [
                        {
                            "orderId": "ORD001",
                            "latitude": 28.6139,
                            "longitude": 77.2090,
                            "address": "New Delhi",
                            "packageWeight": 25.500,
                            "priority": "HIGH"
                        }
                    ]
                }
                """;

        mockMvc.perform(post("/api/dispatch/orders")
                        .contentType("application/json")
                        .content(request))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.message")
                        .value("Delivery orders accepted."))
                .andExpect(jsonPath("$.status").value("success"));

        verify(orderService, times(1))
                .createOrders(any());
    }

    @Test
    void shouldRejectEmptyOrderList() throws Exception {

        String request = """
                {
                    "orders": []
                }
                """;

        mockMvc.perform(post("/api/dispatch/orders")
                        .contentType("application/json")
                        .content(request))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value("error"))
                .andExpect(jsonPath("$.message")
                        .value(org.hamcrest.Matchers.containsString(
                                "At least one order is required")));

        verifyNoInteractions(orderService);
    }

    @Test
    void shouldRejectInvalidOrderLatitude() throws Exception {

        String request = """
                {
                    "orders": [
                        {
                            "orderId": "ORD002",
                            "latitude": 95.0,
                            "longitude": 77.2090,
                            "address": "New Delhi",
                            "packageWeight": 25.500,
                            "priority": "HIGH"
                        }
                    ]
                }
                """;

        mockMvc.perform(post("/api/dispatch/orders")
                        .contentType("application/json")
                        .content(request))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value("error"))
                .andExpect(jsonPath("$.message")
                        .value(org.hamcrest.Matchers.containsString(
                                "latitude must not exceed 90")));

        verifyNoInteractions(orderService);
    }

    @Test
    void shouldRejectUnsupportedOrderPriority() throws Exception {

        String request = """
                {
                    "orders": [
                        {
                            "orderId": "ORD003",
                            "latitude": 28.6139,
                            "longitude": 77.2090,
                            "address": "New Delhi",
                            "packageWeight": 25.500,
                            "priority": "URGENT"
                        }
                    ]
                }
                """;

        mockMvc.perform(post("/api/dispatch/orders")
                        .contentType("application/json")
                        .content(request))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value("error"))
                .andExpect(jsonPath("$.message")
                        .value("Invalid request body or unsupported enum value."));

        verifyNoInteractions(orderService);
    }

    // -----------------------------------------
    // VEHICLE API TESTS
    // -----------------------------------------

    @Test
    void shouldAcceptValidVehicles() throws Exception {

        String request = """
                {
                    "vehicles": [
                        {
                            "vehicleId": "VEH001",
                            "capacity": 100.000,
                            "currentLatitude": 28.6139,
                            "currentLongitude": 77.2090,
                            "currentAddress": "New Delhi"
                        }
                    ]
                }
                """;

        mockMvc.perform(post("/api/dispatch/vehicles")
                        .contentType("application/json")
                        .content(request))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.message")
                        .value("Vehicle details accepted."))
                .andExpect(jsonPath("$.status").value("success"));

        verify(vehicleService, times(1))
                .createVehicles(any());
    }

    @Test
    void shouldRejectEmptyVehicleList() throws Exception {

        String request = """
                {
                    "vehicles": []
                }
                """;

        mockMvc.perform(post("/api/dispatch/vehicles")
                        .contentType("application/json")
                        .content(request))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value("error"))
                .andExpect(jsonPath("$.message")
                        .value(org.hamcrest.Matchers.containsString(
                                "At least one vehicle is required")));

        verifyNoInteractions(vehicleService);
    }

    @Test
    void shouldRejectInvalidVehicleCapacity() throws Exception {

        String request = """
                {
                    "vehicles": [
                        {
                            "vehicleId": "VEH002",
                            "capacity": 0,
                            "currentLatitude": 28.6139,
                            "currentLongitude": 77.2090,
                            "currentAddress": "New Delhi"
                        }
                    ]
                }
                """;

        mockMvc.perform(post("/api/dispatch/vehicles")
                        .contentType("application/json")
                        .content(request))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value("error"))
                .andExpect(jsonPath("$.message")
                        .value(org.hamcrest.Matchers.containsString(
                                "capacity must be greater than zero")));

        verifyNoInteractions(vehicleService);
    }

    // -----------------------------------------
    // DISPATCH PLAN API TEST
    // -----------------------------------------

    @Test
    void shouldReturnDispatchPlanSuccessfully() throws Exception {

        DispatchPlanResponse plan =
                new DispatchPlanResponse(List.of(), List.of());

        when(dispatchService.generateDispatchPlan())
                .thenReturn(plan);

        mockMvc.perform(get("/api/dispatch/plan"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.dispatchPlan").isArray())
                .andExpect(jsonPath("$.unassignedOrders").isArray());

        verify(dispatchService, times(1))
                .generateDispatchPlan();
    }
}
