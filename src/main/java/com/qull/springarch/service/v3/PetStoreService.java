package com.qull.springarch.service.v3;

import com.qull.springarch.dao.v3.AccountDao;
import com.qull.springarch.dao.v3.ItemDao;
import lombok.Data;

/**
 * @author kzh
 * @description
 * @DATE 2019/10/16 9:03
 */
@Data
public class PetStoreService {

    private AccountDao accountDao;

    private ItemDao itemDao;

    private int version;

    public PetStoreService(AccountDao accountDao, ItemDao itemDao) {
        this.accountDao = accountDao;
        this.itemDao = itemDao;
        this.version = -1;
    }

    public PetStoreService(AccountDao accountDao, ItemDao itemDao, int version) {
        this.accountDao = accountDao;
        this.itemDao = itemDao;
        this.version = version;
    }
}
