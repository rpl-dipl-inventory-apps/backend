package InventoryAPPBE.InventoryAPPBE.helpers;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BaseResponse <T>{
    private T data;
    private String message;
    private String status;
}
