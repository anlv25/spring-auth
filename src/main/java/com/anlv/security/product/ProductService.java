package com.anlv.security.product;

import com.anlv.security.category.CategoryRepository;
import com.anlv.security.minio.ObjectStorageService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.List;
import java.util.Optional;
import java.io.IOException;

import com.anlv.security.category.Category;
import com.anlv.security.util.FileUploadUtil;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository repository;
    private final CategoryRepository categoryRepository;
    private final ObjectStorageService objectStorageService;

    public List<ProductDTO> getAllProducts() {
        return repository.findAll().stream()
                .map(ProductDTO::new)
                .toList();
    }

    public Optional<ProductDTO> getProductById(Long id) {
        return repository.findById(id).map(ProductDTO::new);
    }

    public Product updateProduct(Long id, ProductUpdateRequest request) throws IOException {
        Product product = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy sản phẩm"));

        product.setName(request.getName());
        product.setDescription(request.getDescription());
        product.setPrice(request.getPrice());
        product.setStockQuantity(request.getStockQuantity());
        
        // Tìm và set Category dựa trên categoryId
        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy danh mục"));
        product.setCategory(category);

        // Xóa các hình ảnh được chỉ định
        if (request.getImagesToRemove() != null && !request.getImagesToRemove().isEmpty()) {
            product.getImages().removeIf(image -> {
                if(request.getImagesToRemove().contains(image.getId())){
                    objectStorageService.deleteFile(image.getImageName());
                    return true;
                }
                return false;
            });
            
        }

        // Thêm hình ảnh mới
        if (request.getNewImages() != null && !request.getNewImages().isEmpty()) {

            for (MultipartFile imageFile : request.getNewImages()) {

                ProductImage image = objectStorageService.uploadFile(imageFile);
                if(image != null){
                    image.setProduct(product);
                    product.getImages().add(image);
                }
            }
        }

        return repository.save(product);
    }

    public void deleteProduct(Long id) {
        Optional<Product> oProduct = repository.findById(id);
        if (oProduct.isPresent()) {
            Product product = oProduct.get();

            for (ProductImage image : oProduct.get().getImages()) {
                objectStorageService.deleteFile(image.getImageName());
            }
            product.getImages().removeAll(product.getImages());
            repository.delete(oProduct.get());
        }
    }
    @Transactional
    public Product createProductWithImages(ProductCreateRequest request) throws IOException {
        Product product = new Product();
        product.setName(request.getName());
        product.setDescription(request.getDescription());
        product.setPrice(request.getPrice());
        product.setStockQuantity(request.getStockQuantity());
        product.setCategory(categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy danh mục")));

        Product savedProduct = repository.save(product);

        if (request.getImages() != null && !request.getImages().isEmpty()) {
            for (MultipartFile imageFile : request.getImages()) {
                ProductImage image = objectStorageService.uploadFile(imageFile);
                if(image != null){
                    image.setProduct(savedProduct);
                    savedProduct.getImages().add(image);
                }
            }
            savedProduct = repository.save(savedProduct);
        }

        return savedProduct;
    }

    public void deleteProductImages(String imageUrl) {
        File directory = new File(imageUrl);
        if (directory.exists()) {
            directory.delete();
        }
    }


}