package InventoryAPPBE.InventoryAPPBE.controllers;

import InventoryAPPBE.InventoryAPPBE.helpers.BaseResponse;
import InventoryAPPBE.InventoryAPPBE.helpers.RequestHelper;
import InventoryAPPBE.InventoryAPPBE.models.Supplier;
import InventoryAPPBE.InventoryAPPBE.models.User;
import InventoryAPPBE.InventoryAPPBE.modelsDTO.SupplierDTO;
import InventoryAPPBE.InventoryAPPBE.repositories.supplier.SupplierRepository;
import InventoryAPPBE.InventoryAPPBE.repositories.user.UserRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.*;

import javax.persistence.EntityNotFoundException;
import javax.persistence.NoResultException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping("/suppliers")
public class SupplierController extends AbstractController<SupplierDTO>{
    @Autowired
    private SupplierRepository supplierRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RequestHelper requestHelper;

    @PostMapping("")
    public ResponseEntity<BaseResponse<SupplierDTO>> create(@RequestBody SupplierDTO req){
        BaseResponse<SupplierDTO> response = new BaseResponse<>();
        ModelMapper modelMapper = new ModelMapper();
        try{
            User loggedUser = requestHelper.getUserFromContext();
            if(Objects.equals(req.getSupplierEmail(), loggedUser.getEmail())){
                throw new BadCredentialsException("You can't add a supplier with your email");
            }
            User supplierAccount = userRepository.findByEmail(req.getSupplierEmail());
            if(!supplierAccount.getRole().equals("supplier")){
                throw new BadCredentialsException("You can't add a supplier with a non supplier account");
            }

            // check if the supplier already exists or not
            try{
                supplierRepository.verifySupplier(loggedUser.getId(), supplierAccount.getId());
                throw new BadCredentialsException("You can't add a supplier that already exists");
            }catch (EntityNotFoundException ignored){
                //ignored
            }


            Supplier supplier = modelMapper.map(req, Supplier.class);
            supplier.setOwner(loggedUser);
            supplier.setSupplier(supplierAccount);
            supplierRepository.addSupplier(supplier);

            SupplierDTO supplierDTO = modelMapper.map(supplier, SupplierDTO.class);
            response.setData(supplierDTO);
            response.setMessage("Supplier added successfully");
            return new ResponseEntity<>(response, HttpStatus.OK);
        }catch (BadCredentialsException e){
            response.setData(null);
            response.setMessage(e.getMessage());
            response.setStatus("failed");
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }catch (NoResultException e){
            response.setData(null);
            response.setMessage("email not found");
            response.setStatus("failed");
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            response.setData(null);
            response.setMessage(e.getMessage());
            response.setStatus("error");
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("")
    public ResponseEntity<BaseResponse<List<SupplierDTO>>> getAllSupplierByOwner(@RequestParam(required = false) boolean supplier) throws Exception {
        BaseResponse<List<SupplierDTO>> response = new BaseResponse<>();
        ModelMapper modelMapper = new ModelMapper();
        try {
            User owner = requestHelper.getUserFromContext();
            List<Supplier> suppliers = null;
            if(supplier){
                if(!owner.getRole().equals("supplier")){
                    throw new BadCredentialsException("You can't get inventories with a non supplier account");
                }
                suppliers = supplierRepository.getAllBySupplier(owner);
            }else{
                suppliers = supplierRepository.getAllByOwner(owner);
            }
            List<SupplierDTO> suppliersDTO = new ArrayList<>();
            for (Supplier supp : suppliers) {
                suppliersDTO.add(modelMapper.map(supp, SupplierDTO.class));
            }
            response.setData(suppliersDTO);
            response.setStatus("success");
            response.setMessage("Suppliers retrieved successfully");
            return new ResponseEntity<>(response, HttpStatus.OK);
        }catch (BadCredentialsException e){
            response.setData(null);
            response.setMessage(e.getMessage());
            response.setStatus("failed");
            return new ResponseEntity<>(response, HttpStatus.FORBIDDEN);
        } catch (Exception e) {
            response.setData(null);
            response.setMessage(e.getMessage());
            response.setStatus("error");
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<BaseResponse<SupplierDTO>> remove(@PathVariable("id") int id){
        BaseResponse<SupplierDTO> response = new BaseResponse<>();
        ModelMapper modelMapper = new ModelMapper();
        try{
            Supplier supplier = supplierRepository.getById(id);
            if(supplier == null){
                throw new NoResultException("Supplier not found");
            }
            supplierRepository.deleteSupplier(supplier);
            SupplierDTO supplierDTO = modelMapper.map(supplier, SupplierDTO.class);
            response.setData(supplierDTO);
            response.setMessage("Supplier removed successfully");
            return new ResponseEntity<>(response, HttpStatus.OK);
        }catch (NoResultException e){
            response.setData(null);
            response.setMessage(e.getMessage());
            response.setStatus("failed");
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            response.setData(null);
            response.setMessage(e.getMessage());
            response.setStatus("error");
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
