package com.qull.springarch.core.io.support;

import com.qull.springarch.core.io.FileSystemResource;
import com.qull.springarch.core.io.Resource;
import com.qull.springarch.util.Assert;
import com.qull.springarch.util.ClassUtils;
import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.net.URL;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * @author kzh
 * @description
 * @DATE 2019/10/16 14:11
 */
@Data
public class PackageResourceLoader {

    private static final Logger log = LoggerFactory.getLogger(PackageResourceLoader.class);

    private final ClassLoader classLoader;

    public PackageResourceLoader() {
        this.classLoader = ClassUtils.class.getClassLoader();
    }

    public PackageResourceLoader(ClassLoader classLoader) {
        Assert.notNull(classLoader, "ResourceLoader must not be null");
        this.classLoader = classLoader;
    }

    public Resource[] getResources(String basePackage) {
        Assert.notNull(basePackage, "basePackage must not be null");
        String location = ClassUtils.convertClassNameToResourcePath(basePackage);
        ClassLoader c1 = getClassLoader();
        URL url = c1.getResource(location);
        File rootDir = new File(url.getFile());

        Set<File> matchingFiles = retrieveMatchingFiles(rootDir);
        Resource[] result = new Resource[matchingFiles.size()];
        int i = 0;
        for (File matchingFile : matchingFiles) {
            result[i++] = new FileSystemResource(matchingFile);
        }
        return result;
    }

    private Set<File> retrieveMatchingFiles(File rootDir) {
        if (!rootDir.exists()) {
            // Silently skip non-existing directories
            if (log.isDebugEnabled()) {
                log.debug("Skipping [ " + rootDir.getAbsolutePath() + " ] because it dose not exists" );
            }
            return Collections.emptySet();
        }
        if (!rootDir.isDirectory()) {
            // Complain louder if it exists but is no directory
            if (log.isWarnEnabled()) {
                log.warn("Skipping [ " + rootDir.getAbsolutePath() + " ] because it dose not denote a directory");
            }
            return Collections.emptySet();
        }
        if (!rootDir.canRead()) {
            if (log.isWarnEnabled()) {
                log.warn("Cannot search for matching files underneath directory [ " + rootDir.getAbsolutePath() + " ] because the application is not allowed to read the directory");
            }
            return Collections.emptySet();
        }

        Set<File> result = new LinkedHashSet<>();
        doRetrieveMatchingFiles(rootDir, result);
        return result;
    }

    private void doRetrieveMatchingFiles(File dir, Set<File> result) {
        File[] dirContents = dir.listFiles();
        if (dirContents == null) {
            if (log.isWarnEnabled()) {
                log.warn("Could not retrieve contents of directory [{}]", dir.getAbsolutePath());
            }
            return;
        }

        for (File content : dirContents) {
                if (content.isDirectory()) {
                    if (!content.canRead()) {
                        if (log.isWarnEnabled()) {
                            log.warn("Skipping subdirectory [{}] because the application is not allowed to read", content.getAbsolutePath());
                        }
                    }else {
                        doRetrieveMatchingFiles(content, result);
                    }
                }else {
                    result.add(content);
                }
        }
    }
}
