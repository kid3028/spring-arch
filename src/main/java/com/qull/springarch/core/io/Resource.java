package com.qull.springarch.core.io;

import java.io.IOException;
import java.io.InputStream;

/**
 * @author kzh
 * @description
 * @DATE 2019/10/14 21:40
 */
public interface Resource {
    InputStream getInputStream() throws IOException;
    String getDescription();
}
