package InventoryAPPBE.InventoryAPPBE.controllers;

import InventoryAPPBE.InventoryAPPBE.helpers.BaseResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;

public class AbstractController<T> {
    public ResponseEntity<BaseResponse<T>> create(@Valid @RequestBody T t) {
        return null;
    }

    public ResponseEntity<BaseResponse<T>> updateById(@PathVariable String id, @RequestBody T t) {
        return null;
    }

    ;

    public ResponseEntity<BaseResponse<T>> deleteById(@PathVariable String id) {
        return null;
    }

    ;

    public ResponseEntity<BaseResponse<ArrayList<T>>> findAll() {
        return null;
    }

    public ResponseEntity<BaseResponse<T>> findById(@PathVariable String id) {
        return null;
    }
}
