package com.qull.springarch.service.v6;

import com.qull.springarch.dao.v6.AccountDao;
import com.qull.springarch.dao.v6.ItemDao;
import com.qull.springarch.stereotype.Autowired;
import com.qull.springarch.stereotype.Component;
import com.qull.springarch.util.MessageTracker;
import sun.plugin2.message.Message;

/**
 * @author kzh
 * @description
 * @DATE 2019/10/19 23:56
 */
@Component(value = "petStore")
public class PetStoreServiceImpl implements IPetStoreService {

    @Autowired
    private AccountDao accountDao;

    @Autowired
    private ItemDao itemDao;

    @Override
    public void placeOrder() {
        System.out.println("place order...");
        MessageTracker.addMsg("place order...");
    }

    public AccountDao getAccountDao() {
        return accountDao;
    }

    public void setAccountDao(AccountDao accountDao) {
        this.accountDao = accountDao;
    }

    public ItemDao getItemDao() {
        return itemDao;
    }

    public void setItemDao(ItemDao itemDao) {
        this.itemDao = itemDao;
    }
}
