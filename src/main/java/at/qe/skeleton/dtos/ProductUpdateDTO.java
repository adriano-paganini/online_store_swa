package at.qe.skeleton.dtos;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import java.util.List;

public record ProductUpdateDTO(
    @Size(max = 255, message = "Name must not exceed 255 characters")
    String name,

    @Size(max = 2000, message = "Description must not exceed 2000 characters")
    String description,

    @Min(value = 0, message = "Price must be non-negative")
    Double price,

    @Min(value = 0, message = "Stock must be non-negative")
    Integer stock,

    @Min(value = 0, message = "Discount must be non-negative")
    @Max(value = 1, message = "Price must be non-negative")
    Double discount,

    List<String> images
) {}

