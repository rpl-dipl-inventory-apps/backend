package InventoryAPPBE.InventoryAPPBE.controllers;

import InventoryAPPBE.InventoryAPPBE.helpers.BaseResponse;
import InventoryAPPBE.InventoryAPPBE.helpers.RequestHelper;
import InventoryAPPBE.InventoryAPPBE.models.Category;
import InventoryAPPBE.InventoryAPPBE.models.User;
import InventoryAPPBE.InventoryAPPBE.modelsDTO.CategoryDTO;
import InventoryAPPBE.InventoryAPPBE.repositories.category.CategoryRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.persistence.EntityNotFoundException;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/categories")
public class CategoryController extends AbstractController<CategoryDTO> {

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private RequestHelper requestHelper;

    @Override
    @PostMapping("")
    public ResponseEntity<BaseResponse<CategoryDTO>> create(@RequestBody CategoryDTO categoryDTO) {
        ModelMapper modelMapper = new ModelMapper();
        BaseResponse<CategoryDTO> response = new BaseResponse<>(null, null, null);
        try {
            User loggedUser = requestHelper.getUserFromContext();
            Category category = modelMapper.map(categoryDTO, Category.class);

            category.setUser(loggedUser);
            int latestIncrementId = categoryRepository.getLatestIncrementIdByUser(loggedUser);
            category.setIncrementId(latestIncrementId + 1);

            Category savedValue = categoryRepository.create(category);

            CategoryDTO categoryResponse = modelMapper.map(savedValue, CategoryDTO.class);
            response = new BaseResponse<>(categoryResponse, "success", "success");
            return new ResponseEntity<>(response, HttpStatus.CREATED);
        } catch (Exception e) {
            response.setData(null);
            response.setMessage(e.getMessage());
            response.setStatus("error");
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }

    @Override
    @GetMapping("")
    public ResponseEntity<BaseResponse<ArrayList<CategoryDTO>>> findAll() {
        ModelMapper modelMapper = new ModelMapper();
        BaseResponse<ArrayList<CategoryDTO>> response = new BaseResponse<>(null, null, null);
        ArrayList<CategoryDTO> categories = new ArrayList<>();
        try {
            User loggedUser = requestHelper.getUserFromContext();
            ArrayList<Category> category = (ArrayList<Category>) categoryRepository.getAllCategory(loggedUser);
            for (Category c : category) {
                CategoryDTO categoryDTO = modelMapper.map(c, CategoryDTO.class);
                categories.add(categoryDTO);
            }
            response.setData(categories);
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

    @Override
    @GetMapping("/{id}")
    public ResponseEntity<BaseResponse<CategoryDTO>> findById(@PathVariable String id) {
        BaseResponse<CategoryDTO> response = new BaseResponse<>(null, null, null);
        ModelMapper modelMapper = new ModelMapper();
        try {
            User loggedUser = requestHelper.getUserFromContext();
            int idInteger = Integer.parseInt(id);
            Category category = categoryRepository.findById(idInteger, loggedUser);

            CategoryDTO categoryDTO = modelMapper.map(category, CategoryDTO.class);

            response.setData(categoryDTO);
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
    public ResponseEntity<BaseResponse<CategoryDTO>> deleteById(@PathVariable String id) {
        BaseResponse<CategoryDTO> response = new BaseResponse<>(null, null, null);
        try {
            User loggedUser = requestHelper.getUserFromContext();
            int idInteger = Integer.parseInt(id);
            categoryRepository.findById(idInteger, loggedUser);

            categoryRepository.deleteCategory(idInteger);
            response.setData(null);
            response.setMessage("product has been deleted");
            response.setStatus("success");
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (EntityNotFoundException e) {
            response.setData(null);
            response.setMessage(e.getMessage());
            response.setStatus("failed");
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        } catch (DataIntegrityViolationException e) {
            response.setData(null);
            response.setMessage("Category is being used by a product, please delete related product first");
            response.setStatus("failed");
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            response.setData(null);
            response.setMessage(e.getMessage());
            response.setStatus("error");
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    @PutMapping("/{id}")
    public ResponseEntity<BaseResponse<CategoryDTO>> updateById(@PathVariable String id, @RequestBody CategoryDTO t) {
        BaseResponse<CategoryDTO> response = new BaseResponse<>(null, null, null);
        ModelMapper modelMapper = new ModelMapper();
        try {
            User loggedUser = requestHelper.getUserFromContext();
            int idInteger = Integer.parseInt(id);
            Category categoryFromDTO = modelMapper.map(t, Category.class);
            Category category = categoryRepository.findById(idInteger, loggedUser);
            categoryFromDTO.setId(idInteger);
            categoryFromDTO.setCreatedAt(category.getCreatedAt());
            categoryFromDTO.setUser(category.getUser());
            Category categoryUpdated = categoryRepository.update(categoryFromDTO);

            CategoryDTO categoryDTO = modelMapper.map(categoryUpdated, CategoryDTO.class);
            response.setData(categoryDTO);
            response.setMessage("product has been updated");
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
}
