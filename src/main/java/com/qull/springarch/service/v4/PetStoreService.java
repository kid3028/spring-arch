package com.qull.springarch.service.v4;

import com.qull.springarch.dao.v4.AccountDao;
import com.qull.springarch.dao.v4.ItemDao;
import com.qull.springarch.stereotype.Autowired;
import com.qull.springarch.stereotype.Component;
import lombok.Data;

import javax.management.loading.PrivateClassLoader;

/**
 * @author kzh
 * @description
 * @DATE 2019/10/16 13:41
 */
@Component(value = "petStore")
@Data
public class PetStoreService {
    @Autowired
    private AccountDao accountDao;

    @Autowired
    private ItemDao itemDao;

    private int version;
}
