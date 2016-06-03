package com.knightliao.plugin.starter.shade.utils;

import java.io.IOException;
import java.net.URL;

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

        } catch (IOException e) {

            Assert.assertFalse(true);
        }
    }
}
