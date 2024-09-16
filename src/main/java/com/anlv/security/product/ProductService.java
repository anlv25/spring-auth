package com.anlv.security.product;

import com.anlv.security.image.ProductImage;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.io.IOException;

import com.anlv.security.category.Category;
import com.anlv.security.util.FileUploadUtil;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository repository;

    public List<Product> getAllProducts() {
        return repository.findAll();
    }

    public Optional<Product> getProductById(Long id) {
        return repository.findById(id);
    }

    public Product createProduct(Product product) {
        return repository.save(product);
    }

    public Product updateProduct(Long id, ProductUpdateRequest request) throws IOException {
        Product product = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy sản phẩm"));

        product.setName(request.getName());
        product.setDescription(request.getDescription());
        product.setPrice(request.getPrice());
        product.setStockQuantity(request.getStockQuantity());
        product.setCategory(request.getCategory());

        // Xóa các hình ảnh được chỉ định
        if (request.getImagesToRemove() != null && !request.getImagesToRemove().isEmpty()) {
            product.getImages().removeIf(image -> request.getImagesToRemove().contains(image.getId()));
        }

        // Thêm hình ảnh mới
        if (request.getNewImages() != null && !request.getNewImages().isEmpty()) {
            for (MultipartFile imageFile : request.getNewImages()) {
                String fileName = StringUtils.cleanPath(imageFile.getOriginalFilename());
                String uploadDir = "product-images/" + product.getId();

                FileUploadUtil.saveFile(uploadDir, fileName, imageFile);

                ProductImage image = new ProductImage();
                image.setProduct(product);
                image.setImageUrl(uploadDir + "/" + fileName);
                product.getImages().add(image);
            }
        }

        return repository.save(product);
    }

    public Product updateProduct(Product product) {
        return repository.save(product);
    }

    public void deleteProduct(Long id) {
        repository.deleteById(id);
    }

    public Product createProductWithImages(ProductCreateRequest request) throws IOException {
        Product product = new Product();
        product.setName(request.getName());
        product.setDescription(request.getDescription());
        product.setPrice(request.getPrice());
        product.setStockQuantity(request.getStockQuantity());
        product.setCategory(request.getCategory());

        Product savedProduct = repository.save(product);

        if (request.getImages() != null && !request.getImages().isEmpty()) {
            for (MultipartFile imageFile : request.getImages()) {
                String fileName = StringUtils.cleanPath(imageFile.getOriginalFilename());
                String uploadDir = "product-images/" + savedProduct.getId();

                FileUploadUtil.saveFile(uploadDir, fileName, imageFile);

                ProductImage image = new ProductImage();
                image.setProduct(savedProduct);
                image.setImageUrl(uploadDir + "/" + fileName);
                savedProduct.getImages().add(image);
            }
            savedProduct = repository.save(savedProduct);
        }

        return savedProduct;
    }
}