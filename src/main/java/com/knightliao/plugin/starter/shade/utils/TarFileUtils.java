package com.knightliao.plugin.starter.shade.utils;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveOutputStream;
import org.apache.commons.compress.compressors.gzip.GzipCompressorOutputStream;
import org.apache.commons.io.IOUtils;

/**
 * Created by knightliao on 16/6/3.
 */
public class TarFileUtils {

    /**
     * 将文件夹打成包
     *
     * @param SrcDirPath
     * @param destFile
     *
     * @throws FileNotFoundException
     * @throws IOException
     */
    public static void createDirTarGz(String SrcDirPath, String destFile)
            throws FileNotFoundException, IOException {

        FileOutputStream fOut = null;
        BufferedOutputStream bOut = null;
        GzipCompressorOutputStream gzOut = null;
        TarArchiveOutputStream tOut = null;
        try {

            fOut = new FileOutputStream(new File(destFile));
            bOut = new BufferedOutputStream(fOut);
            gzOut = new GzipCompressorOutputStream(bOut);
            tOut = new TarArchiveOutputStream(gzOut);
            addFileToTarGz(tOut, SrcDirPath, "");

        } finally {

            if (tOut != null) {
                tOut.finish();
                tOut.close();
            }

            if (gzOut != null) {
                gzOut.close();
            }

            if (bOut != null) {
                bOut.close();
            }

            if (fOut != null) {
                fOut.close();
            }
        }
    }

    private static void addFileToTarGz(TarArchiveOutputStream tOut, String path, String base)
            throws IOException {

        File f = new File(path);
        String entryName = base + f.getName();
        TarArchiveEntry tarEntry = new TarArchiveEntry(f, entryName);
        tOut.putArchiveEntry(tarEntry);

        if (f.isFile()) {

            IOUtils.copy(new FileInputStream(f), tOut);
            tOut.closeArchiveEntry();

        } else {

            tOut.closeArchiveEntry();
            File[] children = f.listFiles();
            if (children != null) {
                for (File child : children) {
                    System.out.println("add " + child.getName());
                    addFileToTarGz(tOut, child.getAbsolutePath(), entryName + "/");
                }
            }
        }
    }
}
