package at.qe.skeleton.mappers;

import at.qe.skeleton.dtos.ProductCreateDTO;
import at.qe.skeleton.model.Product;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Service
public class ProductCreateMapper {
    
    public Product mapFrom(ProductCreateDTO dto) {
        if (dto == null) {
            return null;
        }

        Product product = new Product();
        product.setName(dto.name());
        product.setDescription(dto.description());
        product.setPrice(dto.price());
        product.setStock(dto.stock());
        product.setDiscount(dto.discount() != null ? dto.discount() : 0.0);
        product.setAvgScore(0.0);
        product.setDeleted(false);
        product.setImages(dto.images() != null ? dto.images() : new ArrayList<>());
        
        return product;
    }
}
