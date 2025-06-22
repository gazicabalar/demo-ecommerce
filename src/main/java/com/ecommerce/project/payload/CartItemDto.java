package com.ecommerce.project.payload;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CartItemDto {
    private Long cartItemId;
    private CartDto cartDto;
    private ProductDTO productDto;
    private Integer quantity;
    private Double discount;
    private Double productPrice;
}
