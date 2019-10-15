package com.qull.springarch.service.v2;

import com.qull.springarch.dao.v2.AccountDao;
import com.qull.springarch.dao.v2.ItemDao;
import lombok.Data;

/**
 * @author kzh
 * @description
 * @DATE 2019/10/15 17:30
 */
@Data
public class PetStoreService {
    private AccountDao accountDao;
    private ItemDao itemDao;
    private String owner;
    private int version;
}
