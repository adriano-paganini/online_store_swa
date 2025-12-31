package at.qe.skeleton.dtos;

import java.util.List;

public record CartDTO(
    List<CartItemDTO> items
) {}

