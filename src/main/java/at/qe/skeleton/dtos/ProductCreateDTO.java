package at.qe.skeleton.dtos;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.List;

public record ProductCreateDTO(
    @NotBlank(message = "Name is required")
    @Size(max = 255, message = "Name must not exceed 255 characters")
    String name,

    List<String> images,

    @NotBlank(message = "Description is required")
    @Size(max = 2000, message = "Description must not exceed 2000 characters")
    String description,

    @NotNull(message = "Price is required")
    @Min(value = 0, message = "Price must be non-negative")
    Double price,

    @NotNull(message = "Stock is required")
    @Min(value = 0, message = "Stock must be non-negative")
    Integer stock,

    @NotNull(message = "Discount is required")
    @Min(value = 0, message = "Discount must be non-negative")
    Double discount
) {}

