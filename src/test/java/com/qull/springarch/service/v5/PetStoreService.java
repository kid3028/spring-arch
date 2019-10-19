package com.qull.springarch.service.v5;

import com.qull.springarch.dao.v5.AccountDao;
import com.qull.springarch.dao.v5.ItemDao;
import com.qull.springarch.stereotype.Autowired;
import com.qull.springarch.stereotype.Component;
import com.qull.springarch.util.MessageTracker;
import lombok.Data;

/**
 * @author kzh
 * @description
 * @DATE 2019/10/17 15:35
 */
@Component(value = "petStore")
@Data
public class PetStoreService {

    @Autowired
    private AccountDao accountDao;

    @Autowired
    private ItemDao itemDao;

    public PetStoreService() {}

    public void placeOrder() {
        System.out.println("place order...");
        MessageTracker.addMsg("place order...");
    }

    public void placeOrderWithException() {
        throw new RuntimeException();
    }
}
