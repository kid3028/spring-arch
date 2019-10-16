package com.qull.springarch.core.io;

import com.qull.springarch.util.Assert;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * @author kzh
 * @description
 * @DATE 2019/10/14 21:42
 */
public class FileSystemResource implements Resource {
    private String path;
    private File file;

    public FileSystemResource(File file) {
        this.path = file.getPath();
        this.file = file;
    }

    public FileSystemResource(String path) {
        Assert.notNull(path, "path must be not null");
        this.path = path;
        file = new File(this.path);
    }

    @Override
    public InputStream getInputStream() throws IOException {
        return new FileInputStream(this.file);
    }

    @Override
    public String getDescription() {
        return "file : " + this.file.getAbsolutePath();
    }
}
