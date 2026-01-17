package at.qe.skeleton.mappers;

import at.qe.skeleton.dtos.ProductDTO;
import at.qe.skeleton.dtos.ProductUpdateDTO;
import at.qe.skeleton.model.Product;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Collection;

@Service
public class ProductMapper {


    public ProductDTO mapTo(Product product, Collection<? extends GrantedAuthority> authorities) {
        if (product == null) {
            return null;
        }

        boolean isAdminOrManager = authorities != null && authorities.stream()
                .anyMatch(auth -> auth.getAuthority().equals("ADMIN") || auth.getAuthority().equals("MANAGER"));

        Long createdByName = null;
        LocalDateTime createdAt = null;
        Long updatedByName = null;
        LocalDateTime updatedAt = null;

        if (isAdminOrManager) {
            if (product.getCreateUser() != null) {
                createdByName = product.getCreateUser().getId();
            }
            createdAt = product.getCreateDate();
            if (product.getUpdateUser() != null) {
                updatedByName = product.getUpdateUser().getId();
            }
            updatedAt = product.getUpdateDate();
        }

        return new ProductDTO(
                product.getId(),
                product.getName(),
                product.getDescription(),
                product.getPrice(),
                product.getStock(),
                product.getDiscount(),
                product.getAvgScore(),
                product.getImages(),
                product.getDeleted(),
                createdByName,
                createdAt,
                updatedByName,
                updatedAt
        );
    }


    public ProductDTO mapTo(Product product) {
        return mapTo(product, null);
    }

    public void apply(Product product, ProductUpdateDTO dto) {
        if (dto.name() != null) {
            product.setName(dto.name());
        }
        if (dto.description() != null) {
            product.setDescription(dto.description());
        }
        if (dto.price() != null) {
            product.setPrice(dto.price());
        }
        if (dto.stock() != null) {
            product.setStock(dto.stock());
        }
        if (dto.discount() != null) {
            product.setDiscount(dto.discount());
        }
        if (dto.images() != null) {
            product.setImages(dto.images());
        }
    }
}

