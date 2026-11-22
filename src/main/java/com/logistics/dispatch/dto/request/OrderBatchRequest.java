
package com.logistics.dispatch.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record OrderBatchRequest(

        @NotEmpty(message = "At least one order is required")
        List<@NotNull @Valid OrderRequest> orders

) {
}
