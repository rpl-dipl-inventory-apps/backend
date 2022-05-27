package InventoryAPPBE.InventoryAPPBE.controllers;

import InventoryAPPBE.InventoryAPPBE.helpers.BaseResponse;
import InventoryAPPBE.InventoryAPPBE.helpers.RequestHelper;
import InventoryAPPBE.InventoryAPPBE.models.StockHistory;
import InventoryAPPBE.InventoryAPPBE.models.User;
import InventoryAPPBE.InventoryAPPBE.repositories.stockhistory.StockHistoryRepository;
import InventoryAPPBE.InventoryAPPBE.repositories.supplier.SupplierRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/stockhistory")
public class StockHistoryController extends AbstractController<StockHistory> {
    @Autowired
    private StockHistoryRepository stockHistoryRepository;

    @Autowired
    private SupplierRepository supplierRepository;

    @Autowired
    private RequestHelper requestHelper;

    @Override
    @GetMapping("")
    public ResponseEntity<BaseResponse<ArrayList<StockHistory>>> findAll(){
        BaseResponse<ArrayList<StockHistory>> response = new BaseResponse<>(null, null, null);
        try {
            User loggedUser = requestHelper.getUserFromContext();
            List<Integer> listIds= supplierRepository.getListOfSuppliers(loggedUser.getId());
            listIds.add(loggedUser.getId());

            ArrayList<StockHistory> stockHistories = (ArrayList<StockHistory>) stockHistoryRepository.getAll(listIds);
            response.setData(stockHistories);
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

    @GetMapping("/in")
    public ResponseEntity<BaseResponse<Integer>> getTotalIn(){
        BaseResponse<Integer> response = new BaseResponse<>(null, null, null);
        try {
            User loggedUser = requestHelper.getUserFromContext();
            Integer totalIn = stockHistoryRepository.getTotalIn(loggedUser);
            response.setData(totalIn);
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

    @GetMapping("/out")
    public ResponseEntity<BaseResponse<Integer>> getTotalOut(){
        BaseResponse<Integer> response = new BaseResponse<>(null, null, null);
        try {
            User loggedUser = requestHelper.getUserFromContext();
            Integer totalOut = stockHistoryRepository.getTotalOut(loggedUser);
            response.setData(totalOut);
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
}
