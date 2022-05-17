package InventoryAPPBE.InventoryAPPBE.controllers;

import InventoryAPPBE.InventoryAPPBE.helpers.BaseResponse;
import InventoryAPPBE.InventoryAPPBE.helpers.RequestHelper;
import InventoryAPPBE.InventoryAPPBE.models.*;
import InventoryAPPBE.InventoryAPPBE.modelsDTO.ProductDTO;
import InventoryAPPBE.InventoryAPPBE.modelsDTO.StockDTO;
import InventoryAPPBE.InventoryAPPBE.repositories.category.CategoryRepository;
import InventoryAPPBE.InventoryAPPBE.repositories.location.LocationRepository;
import InventoryAPPBE.InventoryAPPBE.repositories.product.ProductRepository;
import InventoryAPPBE.InventoryAPPBE.repositories.stockhistory.StockHistoryRepository;
import InventoryAPPBE.InventoryAPPBE.repositories.supplier.SupplierRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.persistence.EntityNotFoundException;
import javax.validation.Valid;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.InputMismatchException;
import java.util.List;

@RestController
@RequestMapping("/products")
public class ProductController extends AbstractController<ProductDTO> {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private LocationRepository locationRepository;

    @Autowired
    private StockHistoryRepository stockHistoryRepository;

    @Autowired
    private SupplierRepository supplierRepository;

    @Autowired
    private RequestHelper requestHelper;

    @Override
    @PostMapping("")
    public ResponseEntity<BaseResponse<ProductDTO>> create(@Valid @RequestBody ProductDTO req) {
        BaseResponse<ProductDTO> response = new BaseResponse<>(null, null, null);
        ModelMapper modelMapper = new ModelMapper();

        try {
            Product productFromDTO = modelMapper.map(req, Product.class);

            User loggedUser = requestHelper.getUserFromContext();
            productFromDTO.setUser(loggedUser);
            categoryRepository.findById(productFromDTO.getCategory().getId(), loggedUser);

            // count total quantity
            int totalQuantity = 0;
            for(Stock stock : productFromDTO.getStockList()) {
                totalQuantity += stock.getQuantity();
            }
            productFromDTO.setStock(totalQuantity);

            int latestIncrementId = productRepository.getLatestIncrementIdByUser(loggedUser);
            productFromDTO.setIncrementId(latestIncrementId + 1);

            Product product = productRepository.create(productFromDTO);
            Product savedProduct = productRepository.findById(product.getId(), loggedUser);

            if(savedProduct.getStock() > 0) {
                List<Stock> stockList = savedProduct.getStockList();
                for (Stock s : stockList) {
                    StockHistory stockHistory = StockHistory.builder()
                            .productId(savedProduct.getId())
                            .productName(savedProduct.getProductName())
                            .userId(loggedUser.getId())
                            .type("in")
                            .locationId(s.getLocation().getId())
                            .actionBy(loggedUser.getUsername())
                            .stockId(s.getId())
                            .quantity(s.getQuantity())
                            .build();
                    stockHistoryRepository.create(stockHistory);
                }
            }
            ProductDTO productResponse = modelMapper.map(savedProduct, ProductDTO.class);

            response.setData(productResponse);
            response.setMessage("Success");
            response.setStatus("success");
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (EntityNotFoundException e) {
            response.setData(null);
            response.setMessage(e.getMessage());
            response.setStatus("failed");
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            e.printStackTrace();
            response.setData(null);
            response.setMessage(e.getMessage());
            response.setStatus("error");
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("")
    public ResponseEntity<BaseResponse<ArrayList<ProductDTO>>> findAll(@RequestParam(required = false) String inventoryid) {
        BaseResponse<ArrayList<ProductDTO>> response = new BaseResponse<>(null, null, null);
        ModelMapper modelMapper = new ModelMapper();
        ArrayList<ProductDTO> products = new ArrayList<>();
        try {
            User loggedUser = requestHelper.getUserFromContext();
            User owner;
            if (inventoryid != null && !inventoryid.trim().isEmpty()) {
                owner = supplierRepository.verifySupplier(Integer.parseInt(inventoryid), loggedUser.getId());
            }else{
                owner = loggedUser;
            }

            ArrayList<Product> product = (ArrayList<Product>) productRepository.getAllProduct(owner);
            for (Product p : product) {
                ProductDTO productDTO = modelMapper.map(p, ProductDTO.class);
                products.add(productDTO);
            }
            response.setData(products);
            response.setMessage("success");
            response.setStatus("success");
            return new ResponseEntity<>(response, HttpStatus.OK);

        } catch (EntityNotFoundException e){
            response.setData(null);
            response.setMessage(e.getMessage());
            response.setStatus("error");
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }catch (Exception e) {
            response.setData(null);
            response.setMessage(e.getMessage());
            response.setStatus("error");
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/recent")
    public ResponseEntity<BaseResponse<ArrayList<ProductDTO>>> getRecent() {
        BaseResponse<ArrayList<ProductDTO>> response = new BaseResponse<>(null, null, null);
        ModelMapper modelMapper = new ModelMapper();
        ArrayList<ProductDTO> products = new ArrayList<>();
        try {
            User loggedUser = requestHelper.getUserFromContext();
            ArrayList<Product> product = (ArrayList<Product>) productRepository.getRecentProducts(loggedUser);
            for (Product p : product) {
                ProductDTO productDTO = modelMapper.map(p, ProductDTO.class);
                products.add(productDTO);
            }
            response.setData(products);
            response.setMessage("success");
            response.setStatus("success");
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            response.setData(null);
            response.setMessage(e.getMessage());
            response.setStatus("error");
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<BaseResponse<ProductDTO>> findById(@PathVariable String id, @RequestParam(required = false) String inventoryid) {
        BaseResponse<ProductDTO> response = new BaseResponse<>(null, null, null);
        ModelMapper modelMapper = new ModelMapper();
        try {
            User loggedUser = requestHelper.getUserFromContext();
            User owner;
            if (inventoryid != null && !inventoryid.trim().isEmpty()) {
                owner = supplierRepository.verifySupplier(Integer.parseInt(inventoryid), loggedUser.getId());
            }else{
                owner = loggedUser;
            }

            int idInteger = Integer.parseInt(id);

            Product product = productRepository.findById(idInteger, owner);

            ProductDTO productResponse = modelMapper.map(product, ProductDTO.class);

            response.setData(productResponse);
            response.setMessage("Success");
            response.setStatus("success");
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (EntityNotFoundException e) {
            response.setData(null);
            response.setMessage(e.getMessage());
            response.setStatus("failed");
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        } catch (NumberFormatException e) {
            response.setData(null);
            response.setMessage(e.getMessage());
            response.setStatus("failed");
            return new ResponseEntity<>(response, HttpStatus.UNPROCESSABLE_ENTITY);
        } catch (Exception e) {
            response.setData(null);
            response.setMessage(e.getMessage());
            response.setStatus("error");
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    @DeleteMapping("/{id}")
    public ResponseEntity<BaseResponse<ProductDTO>> deleteById(@PathVariable String id) {
        BaseResponse<ProductDTO> response = new BaseResponse<>(null, null, null);
        try {
            User loggedUser = requestHelper.getUserFromContext();
            int idInteger = Integer.parseInt(id);
            Product deletedProduct =  productRepository.findById(idInteger, loggedUser);
            if(deletedProduct.getStock() > 0){
                StockHistory stockHistory = new StockHistory();
                stockHistory.setProductId(deletedProduct.getId());
                stockHistory.setProductName(deletedProduct.getProductName());
                stockHistory.setQuantity(deletedProduct.getStock());
                stockHistory.setUserId(loggedUser.getId());
                stockHistory.setType("out");
                stockHistoryRepository.create(stockHistory);
            }
            productRepository.deleteProduct(idInteger);
            response.setData(null);
            response.setMessage("product has been deleted");
            response.setStatus("success");
            return new ResponseEntity<>(response, HttpStatus.OK);

        } catch (EntityNotFoundException e) {
            response.setData(null);
            response.setMessage(e.getMessage());
            response.setStatus("failed");
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            response.setData(null);
            response.setMessage(e.getMessage());
            response.setStatus("failed");
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<BaseResponse<ProductDTO>> updateById(@PathVariable String id, @RequestBody ProductDTO req, @RequestParam(required = false) String inventoryid) {
        BaseResponse<ProductDTO> response = new BaseResponse<>(null, null, null);
        ModelMapper modelMapper = new ModelMapper();
        try {
            User loggedUser = requestHelper.getUserFromContext();
            int idInteger = Integer.parseInt(id);

            User owner;
            if (inventoryid != null && !inventoryid.trim().isEmpty()) {
                owner = supplierRepository.verifySupplier(Integer.parseInt(inventoryid), loggedUser.getId());
            }else{
                owner = loggedUser;
            }
            Product productFromDTO = modelMapper.map(req, Product.class);

            Product productFromDB = productRepository.findById(idInteger, owner);
            List<Stock> oldStockList = new ArrayList<>();
            for (Stock stock : productFromDB.getStockList()) {
                Stock tempStock = Stock.builder().location(stock.getLocation()).quantity(stock.getQuantity()).build();
                tempStock.setId(stock.getId());
                oldStockList.add(tempStock);
            }
            categoryRepository.findById(productFromDTO.getCategory().getId(), owner);

            productFromDTO.setUser(owner);
            productFromDTO.setId(idInteger);
            productFromDTO.setCreatedAt(productFromDB.getCreatedAt());

            int totalStockDTO = 0;
            for (Stock stock: productFromDTO.getStockList()) {
                totalStockDTO += stock.getQuantity();
            }

            productFromDTO.getStockList().removeIf(stock -> stock.getQuantity() == 0);
            productFromDTO.setStock(totalStockDTO);
            int qtyChangesRequest = 0;

            if(totalStockDTO > productFromDB.getStock()) {
                qtyChangesRequest = totalStockDTO - productFromDB.getStock();
            }

            if(totalStockDTO < productFromDB.getStock()) {
                qtyChangesRequest = productFromDB.getStock() - totalStockDTO;
            }

            Product updatedProduct = productRepository.update(productFromDTO);

            HashMap<Integer, Stock> oldStockMap = new HashMap<>();
            for (Stock stock: oldStockList) {
                oldStockMap.put(stock.getId(), stock);  // old stock map    key: stock id, value: stock
            }

            HashMap<Integer, Stock> newStockMap = new HashMap<>();
            for (Stock stock: updatedProduct.getStockList()) {
                newStockMap.put(stock.getId(), stock);  // new stock map    key: stock id, value: stock
            }

            if(qtyChangesRequest != 0) {
                for (Stock stock: updatedProduct.getStockList()) {
                    String tempType = "";
                    int tempQtyChangesRequest = 0;
                    System.out.println("stock id " + stock.getId());
                    if(oldStockMap.containsKey(stock.getId())) {
                        Stock oldStock = oldStockMap.get(stock.getId());

                        if (stock.getQuantity() > oldStock.getQuantity()) {
                            tempType = "in";
                            tempQtyChangesRequest = stock.getQuantity() - oldStock.getQuantity();
                        }
                        if (stock.getQuantity() < oldStock.getQuantity()) {
                            tempType = "out";
                            tempQtyChangesRequest = oldStock.getQuantity() - stock.getQuantity();
                        }
                        if (stock.getQuantity() == oldStock.getQuantity()) {
                            continue;
                        }
                        StockHistory stockHistory = StockHistory.builder()
                                .productId(updatedProduct.getId())
                                .productName(updatedProduct.getProductName())
                                .userId(loggedUser.getId())
                                .userName(loggedUser.getUsername())
                                .type(tempType)
                                .locationId(stock.getLocation().getId())
                                .locationName(stock.getLocation().getLocationName())
                                .actionBy(loggedUser.getUsername())
                                .stockId(stock.getId())
                                .quantity(tempQtyChangesRequest)
                                .build();
                        stockHistoryRepository.create(stockHistory);
                    }
                }

                if(updatedProduct.getStockList().size() > oldStockList.size()) {
                    for (Stock stock: updatedProduct.getStockList()) {
                        String tempType = "";
                        int tempQtyChangesRequest = 0;
                        if(!oldStockMap.containsKey(stock.getId())) {
                            tempType = "in";
                            tempQtyChangesRequest = stock.getQuantity();

                            StockHistory stockHistory = StockHistory.builder()
                                    .productId(updatedProduct.getId())
                                    .productName(updatedProduct.getProductName())
                                    .userId(loggedUser.getId())
                                    .userName(loggedUser.getUsername())
                                    .type(tempType)
                                    .locationId(stock.getLocation().getId())
                                    .locationName(stock.getLocation().getLocationName())
                                    .actionBy(loggedUser.getUsername())
                                    .stockId(stock.getId())
                                    .quantity(tempQtyChangesRequest)
                                    .build();
                            stockHistoryRepository.create(stockHistory);
                        }

                    }
                }
                if(updatedProduct.getStockList().size() < oldStockList.size()) {
                    for (Stock stock: oldStockList) {
                        String tempType = "";
                        int tempQtyChangesRequest = 0;
                        if(!newStockMap.containsKey(stock.getId())) {
                            tempType = "out";
                            tempQtyChangesRequest = stock.getQuantity();

                            StockHistory stockHistory = StockHistory.builder()
                                    .productId(updatedProduct.getId())
                                    .productName(updatedProduct.getProductName())
                                    .userId(loggedUser.getId())
                                    .userName(loggedUser.getUsername())
                                    .type(tempType)
                                    .locationId(stock.getLocation().getId())
                                    .locationName(stock.getLocation().getLocationName())
                                    .actionBy(loggedUser.getUsername())
                                    .stockId(stock.getId())
                                    .quantity(tempQtyChangesRequest)
                                    .build();
                            stockHistoryRepository.create(stockHistory);
                        }
                    }
                }

            }

            ProductDTO productResponse = modelMapper.map(updatedProduct, ProductDTO.class);
            response.setData(productResponse);
            response.setMessage("product has been updated");
            response.setStatus("success");
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (EntityNotFoundException e) {
            response.setData(null);
            response.setMessage(e.getMessage());
            response.setStatus("failed");
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }  catch (Exception e) {
            response.setData(null);
            response.setMessage(e.getMessage());
            response.setStatus("error");
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // this controller only for reduce total stock with first in stock get out first (FIFO), don't accept any change except total stock
    @PutMapping("/{id}/stock/reduce")
    public ResponseEntity<BaseResponse<ProductDTO>> reduceStock(@PathVariable String id, @RequestBody ProductDTO req, @RequestParam(required = false) String inventoryid){
        BaseResponse<ProductDTO> response = new BaseResponse<>();
        ModelMapper modelMapper = new ModelMapper();
        try {
            User loggedUser = requestHelper.getUserFromContext();
            int idInteger = Integer.parseInt(id);

            User owner;
            if (inventoryid != null && !inventoryid.trim().isEmpty()) {
                owner = supplierRepository.verifySupplier(Integer.parseInt(inventoryid), loggedUser.getId());
            } else {
                owner = loggedUser;
            }

            int totalStockFromDTO = req.getStock();
            Product productFromDB = productRepository.findById(idInteger, owner);

            String type = "";
            int qtyChangesRequest = 0;
            if (totalStockFromDTO > productFromDB.getStock()) {
                throw new InputMismatchException("Stock cannot be greater than current stock");
            }

            if (totalStockFromDTO < productFromDB.getStock()) {
                type = "out";
                qtyChangesRequest = productFromDB.getStock() - totalStockFromDTO;
            }
            productFromDB.setStock(totalStockFromDTO);
            Product updatedProduct = productRepository.update(productFromDB);

            List<StockDTO> stockListResponse = new ArrayList<>();
            if (qtyChangesRequest != 0) {
                List<Stock> stocks = productRepository.getRequestedStock(productFromDB.getId(), qtyChangesRequest);
                for (Stock stock : stocks) {
                    StockDTO tempStock = StockDTO.builder().locationId(stock.getLocation().getId()).locationName(stock.getLocation().getLocationName()).quantity(stock.getQuantity()).build();
                    tempStock.setId(stock.getId());
                    stockListResponse.add(tempStock);
                }
                int index = 0;
                if (stocks.size() > 0) {
                    for (Stock stock : stocks) {

                        int qtyForStockHistory = 0;
                        int tempQtyChanges = qtyChangesRequest;
                        qtyChangesRequest = qtyChangesRequest - stock.getQuantity();
                        if (qtyChangesRequest > 0) {
                            qtyForStockHistory = stock.getQuantity();
                            stock.setQuantity(0);
                        } else {
                            qtyForStockHistory = tempQtyChanges;
                            stock.setQuantity(Math.abs(qtyChangesRequest));
                        }

                        StockHistory stockHistory = StockHistory.builder()
                                .productId(productFromDB.getId())
                                .productName(productFromDB.getProductName())
                                .userId(loggedUser.getId())
                                .userName(loggedUser.getUsername())
                                .type(type)
                                .locationId(stock.getLocation().getId())
                                .locationName(stock.getLocation().getLocationName())
                                .actionBy(loggedUser.getUsername())
                                .stockId(stock.getId())
                                .quantity(qtyForStockHistory)
                                .build();
                        stockHistoryRepository.create(stockHistory);

                        // set quantity for stock response
                        stockListResponse.get(index).setQuantity(qtyForStockHistory);

                        if(stock.getQuantity() == 0){
                            productRepository.deleteStock(stock);
                        }
                        index++;
                    }
                }
                updatedProduct.setStockList(stocks);
                updatedProduct.getStockList().removeIf(stock -> stock.getQuantity() == 0);
            }

            ProductDTO productResponse = modelMapper.map(updatedProduct, ProductDTO.class);
            productResponse.setStockList(stockListResponse);
            response.setData(productResponse);
            response.setMessage("product has been updated");
            response.setStatus("success");
            return new ResponseEntity<>(response, HttpStatus.OK);
        }catch (InputMismatchException e) {
            response.setData(null);
            response.setMessage(e.getMessage());
            response.setStatus("failed");
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        } catch (EntityNotFoundException e) {
            response.setData(null);
            response.setMessage(e.getMessage());
            response.setStatus("failed");
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }  catch (Exception e) {
            response.setData(null);
            response.setMessage(e.getMessage());
            response.setStatus("error");
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/total")
    public ResponseEntity<BaseResponse<Integer>> getTotal() {
        BaseResponse<Integer> response = new BaseResponse<>(null, null, null);
        try {
            User loggedUser = requestHelper.getUserFromContext();
            Integer total = productRepository.getTotalProduct(loggedUser);
            response.setData(total);
            response.setMessage("total product");
            response.setStatus("success");
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            response.setData(null);
            response.setMessage(e.getMessage());
            response.setStatus("error");
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}
