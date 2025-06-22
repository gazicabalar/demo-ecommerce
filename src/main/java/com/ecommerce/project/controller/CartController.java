package com.ecommerce.project.controller;

import com.ecommerce.project.model.Cart;
import com.ecommerce.project.payload.CartDto;
import com.ecommerce.project.repositories.CartRepository;
import com.ecommerce.project.service.CartService;
import com.ecommerce.project.util.AuthUtil;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class CartController {

    private final CartService cartService;
    private final CartRepository cartRepository;
    private final AuthUtil authUtil;

    public CartController(CartService cartService, CartRepository cartRepository, AuthUtil authUtil){
        this.cartService = cartService;
        this.cartRepository = cartRepository;
        this.authUtil = authUtil;
    }

    @PostMapping("/carts/products/{productId}/quantity/{quantity}")
    public ResponseEntity<CartDto> addProductToCart(@PathVariable Long productId,@PathVariable Integer quantity) {
        CartDto cartDto = cartService.addProductToCart(productId, quantity);
        return new ResponseEntity<>(cartDto, HttpStatus.CREATED);
    }

    @GetMapping("/carts")
    public ResponseEntity<List<CartDto>> getCarts(){
        List<CartDto> cartDtos = cartService.getAllCarts();
        return new ResponseEntity<>(cartDtos, HttpStatus.FOUND);
    }

    @GetMapping("/carts/users/cart")
    public ResponseEntity<CartDto> getCartById(){
        String emailId = authUtil.loggedInEmail();
        Cart cart = cartRepository.findByCartEmail(emailId);
        Long cartId = cart.getCartId();
        CartDto cartDto = cartService.getCart(emailId, cartId);
        return new ResponseEntity<>(cartDto, HttpStatus.OK);
    }

    @PutMapping("/carts/products/{productId}/quantity/{operation}")
    public ResponseEntity<CartDto> updateCart(@PathVariable Long productId,@PathVariable String operation){
        CartDto cartDto = cartService.updateProductQuantityInCart(productId, operation.equalsIgnoreCase("delete") ? -1 : 1);

        return new ResponseEntity<>(cartDto, HttpStatus.OK);
    }

    @DeleteMapping("/carts/{cartId}/productId/{productId}")
    public ResponseEntity<String> deleteProductFromCart(@PathVariable Long cartId,@PathVariable Long productId){
        String status = cartService.deleteProductFromCart(cartId, productId);
        return new ResponseEntity<>(status, HttpStatus.OK);
    }

}
