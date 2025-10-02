package com.ecommerce.project.service;

import com.ecommerce.project.exceptions.ApiException;
import com.ecommerce.project.exceptions.ResourceNotFoundException;
import com.ecommerce.project.model.Cart;
import com.ecommerce.project.model.CartItems;
import com.ecommerce.project.model.Product;
import com.ecommerce.project.payload.CartDTO;
import com.ecommerce.project.payload.CartItemDTO;
import com.ecommerce.project.payload.ProductDTO;
import com.ecommerce.project.repositories.CartItemRepository;
import com.ecommerce.project.repositories.CartRepository;
import com.ecommerce.project.repositories.ProductRepository;
import com.ecommerce.project.util.AuthUtils;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.liquibase.LiquibaseProperties;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class CartServiceImpl implements CartService {

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private AuthUtils authUtils;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CartItemRepository cartItemRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Override
    public CartDTO addProductToCart(Long productId, Integer quantity) {
        // find existing cart or create one
            Cart cart = createCart();

        // retrieve product details
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product","productId",productId));

        // perform validation
        CartItems cartItem = cartItemRepository.findCartItemByProductIdAndCartId(
                cart.getCartId(),
                productId
        );

        if(cartItem != null){
            throw new ApiException("product "+ product.getProductName()+ " Already exist!");
        }

        if(product.getQuantity() == 0){
            throw new ApiException(product.getProductName() + " is not available");
        }

        if(product.getQuantity() < quantity){
            throw new ApiException("Please, make an order of the "+ product.getProductName()+
                    " less than or equal to quantity "+ product.getQuantity());
        }

        // create cart item
        CartItems newCartItem = new CartItems();
        newCartItem.setCart(cart);
        newCartItem.setProduct(product);
        newCartItem.setQuantity(quantity);
        newCartItem.setDiscount(product.getDiscount());
        newCartItem.setProductPrice(product.getSpecialPrice());

        // save cart item
        cartItemRepository.save(newCartItem);

        product.setQuantity(product.getQuantity());

        cart.setTotalPrice(cart.getTotalPrice()+ (product.getSpecialPrice()*quantity));

        cartRepository.save(cart);

        // return updated cart information
        CartDTO cartDTO = modelMapper.map(cart,CartDTO.class);

        List<CartItems> cartItems = cart.getCartItems();

        Stream<ProductDTO> productStream = cartItems.stream().map(
                item ->{
                    ProductDTO map = modelMapper.map(item.getProduct(),ProductDTO.class);
                    map.setQuantity(item.getQuantity());
                    return map;
                }
        );
        cartDTO.setProducts(productStream.toList());
        return cartDTO;
    }

    @Override
    public List<CartDTO> getAllCarts() {
        List<Cart> carts = cartRepository.findAll();

        if (carts.size() == 0) {
            throw new ApiException("No cart exists");
        }

        List<CartDTO> cartDTOs = carts.stream().map(cart -> {
            CartDTO cartDTO = modelMapper.map(cart, CartDTO.class);

            List<ProductDTO> products = cart.getCartItems().stream().map(cartItem -> {
                ProductDTO productDTO = modelMapper.map(cartItem.getProduct(), ProductDTO.class);
                productDTO.setQuantity(cartItem.getQuantity()); // Set the quantity from CartItem
                return productDTO;
            }).collect(Collectors.toList());


            cartDTO.setProducts(products);

            return cartDTO;

        }).collect(Collectors.toList());

        return cartDTOs;
    }

    @Override
    public CartDTO getCart(String emailId, Long cartId) {
        Cart cart = cartRepository.findCartByEmailAndByCartId(emailId,cartId);
        if(cart == null){
            throw  new ResourceNotFoundException("cart","cartId",cartId);
        }

        CartDTO cartDTO = modelMapper.map(cart,CartDTO.class);
        cart.getCartItems().forEach(c->c.getProduct().setQuantity(c.getQuantity()));
        List<ProductDTO> products = cart.getCartItems().stream()
                .map(p -> modelMapper.map(p.getProduct(),ProductDTO.class))
                .toList();
        cartDTO.setProducts(products);
        return cartDTO;
    }

    @Transactional
    @Override
    public CartDTO updateProductQuantityInCart(Long productId, Integer quantity) {
        String emailId = authUtils.loggedInEmail();
        Cart userCart = cartRepository.findCartByEmail(emailId);
        Long cartId = userCart.getCartId();
        Cart cart = cartRepository.findById(cartId)
                .orElseThrow(()->new ResourceNotFoundException("cart","cartId",cartId));
        Product product = productRepository.findById(productId)
                .orElseThrow(()->new ResourceNotFoundException("product","productId",productId));

        if(product.getQuantity() == 0){
            throw new ApiException(product.getProductName() + " is not available");
        }

        if(product.getQuantity() < quantity){
            throw new ApiException("Please, make an order of the "+ product.getProductName()+
                    " less than or equal to quantity "+ product.getQuantity());
        }

        CartItems cartItems = cartItemRepository.findCartItemByProductIdAndCartId(cartId,productId);
        if (cartItems == null){
            throw new ApiException("product "+product.getProductName() + " not available in cart");
        }

        int newQuantity = cartItems.getQuantity() + quantity;
        if(newQuantity < 0){
            throw new ApiException("The resulting quantity can not be negative!");
        }

        if(newQuantity == 0){
            deleteProductFromCart(cartId,productId);
        }else {
            cartItems.setProductPrice(product.getSpecialPrice());
            cartItems.setQuantity(cartItems.getQuantity() + quantity);
            cartItems.setDiscount(product.getDiscount());
            cart.setTotalPrice(cart.getTotalPrice() + (cartItems.getProductPrice() * quantity));
            cartRepository.save(cart);
        }
        CartItems updatedCartItem = cartItemRepository.save(cartItems);
        if(updatedCartItem.getQuantity() == 0){
            cartItemRepository.deleteById(updatedCartItem.getCartItemId());
        }

        CartDTO cartDTO = modelMapper.map(cart,CartDTO.class);
        List<CartItems> cartItemsList = cart.getCartItems();

        Stream<ProductDTO> productStream = cartItemsList.stream().map(item -> {
            ProductDTO pDTO = modelMapper.map(item.getProduct(),ProductDTO.class);
            pDTO.setQuantity(item.getQuantity());
            return pDTO;
        });

        cartDTO.setProducts(productStream.toList());
        return cartDTO;
    }

    @Transactional
    @Override
    public String deleteProductFromCart(Long cartId, Long productId) {
        Cart cart = cartRepository.findById(cartId)
                .orElseThrow(()-> new ResourceNotFoundException("cart","cartId",cartId));

        CartItems cartItems = cartItemRepository.findCartItemByProductIdAndCartId(cartId,productId);
        if(cartItems == null){
            throw new ResourceNotFoundException("product","productId",productId);
        }
        cart.setTotalPrice(cart.getTotalPrice() - (cartItems.getProductPrice()* cartItems.getQuantity()));
        cartItemRepository.deleteCartItemByProductIdAndCartId(cartId,productId);

        return "Product "+ cartItems.getProduct().getProductName() + " is removed from the cart!";
    }

    @Override
    public void updateProductsInCart(Long cartId, Long productId) {
        Cart cart = cartRepository.findById(cartId)
                .orElseThrow(()->new ResourceNotFoundException("cart","cartId",cartId));
        Product product = productRepository.findById(productId)
                .orElseThrow(()->new ResourceNotFoundException("product","productId",productId));

        CartItems cartItems = cartItemRepository.findCartItemByProductIdAndCartId(cartId,productId);

        if(cartItems == null)
            throw new ApiException("product "+ product.getProductName()+ " not available in the cart!");

        double cartPrice = cart.getTotalPrice() -
                (cartItems.getProductPrice() * cartItems.getQuantity());

        cartItems.setProductPrice(product.getSpecialPrice());

        cart.setTotalPrice(cartPrice +
                (cartItems.getProductPrice() * cartItems.getQuantity()));

        cartItems = cartItemRepository.save(cartItems);
    }

    @Transactional
    @Override
    public String createOrUpdateCartWithItems(List<CartItemDTO> cartItems) {
        // Get User's email
        String emailId = authUtils.loggedInEmail();
        // check if an existing cart is available or create a new one
        Cart existingCart = cartRepository.findCartByEmail(emailId);
        if(existingCart == null){
            existingCart = new Cart();
            existingCart.setTotalPrice(0.00);
            existingCart.setUser(authUtils.loggedInUser());
            existingCart = cartRepository.save(existingCart);
        }else{
            // clear all current item in the existing cart
            cartItemRepository.deleteAllByCartId(existingCart.getCartId());
        }

        double totalPrice = 0.00;

        // process each item in the request to add to the cart
        for (CartItemDTO cartItemDTO : cartItems){
            Long productId = cartItemDTO.getProductId();
            Integer quantity = cartItemDTO.getQuantity();

            // find the product by Id
            Product product = productRepository.findById(productId)
                    .orElseThrow(() -> new ResourceNotFoundException("Product","ProductId",productId));

            // Directly update the product stock and total price
            //product.setQuantity(product.getQuantity() - quantity);
            totalPrice += product.getSpecialPrice() * quantity;

            // create and save cart item
            CartItems cartItem = new CartItems();
            cartItem.setProduct(product);
            cartItem.setCart(existingCart);
            cartItem.setQuantity(quantity);
            cartItem.setProductPrice(product.getSpecialPrice());
            cartItem.setDiscount(product.getDiscount());
            cartItemRepository.save(cartItem);
        }

        //update the cart total price and save
        existingCart.setTotalPrice(totalPrice);
        cartRepository.save(existingCart);
        return "Cart created/updated with new items";
    }

    private Cart createCart(){
        Cart userCart = cartRepository.findCartByEmail(authUtils.loggedInEmail());
        if(userCart != null){
            return userCart;
        }

        Cart cart = new Cart();
        cart.setTotalPrice(0.00);
        cart.setUser(authUtils.loggedInUser());
        Cart savedCart = cartRepository.save(cart);
        return savedCart;
    }
}
