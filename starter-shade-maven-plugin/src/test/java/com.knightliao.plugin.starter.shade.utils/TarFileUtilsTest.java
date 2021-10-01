package com.knightliao.plugin.starter.shade.utils;

import java.io.File;
import java.io.IOException;
import java.net.URL;

import org.apache.commons.io.FileUtils;
import org.junit.Assert;
import org.junit.Test;

/**
 * Created by knightliao on 16/6/3.
 */
public class TarFileUtilsTest {

    @Test
    public void test() {

        try {

            URL url = TarFileUtils.class.getClassLoader().getResource("testdir");

            TarFileUtils.createDirTarGz(url.getPath(), "testdir.tar.gz");

            FileUtils.forceDelete(new File("testdir.tar.gz"));

        } catch (IOException e) {

            Assert.assertFalse(true);
        }
    }
}
