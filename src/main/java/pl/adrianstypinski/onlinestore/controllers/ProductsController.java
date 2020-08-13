package pl.adrianstypinski.onlinestore.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import pl.adrianstypinski.onlinestore.datamodel.product.ProductItem;
import pl.adrianstypinski.onlinestore.services.DataService;

@RestController
@RequestMapping("api/v1/products")
public class ProductsController {
    private final DataService dataService;

    @Autowired
    public ProductsController(DataService dataService) {
        this.dataService = dataService;
    }

    @GetMapping
    public Iterable<ProductItem.ProductItemDto> getAllProducts(
            @RequestParam(required = false, defaultValue = "50") int size,
            @RequestParam(required = false, defaultValue = "1") int page,
            @RequestParam long productCategoryId) {

        return this.dataService.getAllProductItemsDtoByCategoryId(productCategoryId, page - 1, size);
    }
}
