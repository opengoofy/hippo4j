/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package cn.hippo4j.common.toolkit;

import org.apache.commons.io.input.BrokenInputStream;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import java.io.BufferedReader;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.Writer;
import java.io.File;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Closeable;
import java.io.FileReader;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ByteArrayOutputStream;
import java.io.ByteArrayInputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

/**
 * test for {@link IoUtil}
 */
public class IoUtilTest {

    private Path tempDir;
    private File sourceFile;

    @Before
    public void setUp() throws IOException {
        tempDir = Files.createTempDirectory("test");
        sourceFile = new File(tempDir.toFile(), "source.txt");
        try (FileOutputStream fos = new FileOutputStream(sourceFile)) {
            fos.write("Hello, World!".getBytes());
        }
    }

    @After
    public void tearDown() throws IOException {
        Files.walk(tempDir)
                .sorted((a, b) -> -a.compareTo(b))
                .forEach(path -> {
                    try {
                        Files.deleteIfExists(path);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });
    }

    @Test
    public void testTryDecompressInputStream() throws IOException {
        byte[] inputBytes = "This is a test string.".getBytes("UTF-8");
        ByteArrayOutputStream compressedOutput = new ByteArrayOutputStream();
        try (GZIPOutputStream gzipOutputStream = new GZIPOutputStream(compressedOutput)) {
            gzipOutputStream.write(inputBytes);
        }
        byte[] compressedBytes = compressedOutput.toByteArray();
        ByteArrayInputStream compressedInput = new ByteArrayInputStream(compressedBytes);
        byte[] decompressedBytes = IoUtil.tryDecompress(compressedInput);
        Assert.assertNotNull(decompressedBytes);
        Assert.assertTrue(decompressedBytes.length > 0);
        Assert.assertArrayEquals(inputBytes, decompressedBytes);
    }

    @Test
    public void testTryDecompressByteArray() throws Exception {
        byte[] inputBytes = "This is a test string.".getBytes("UTF-8");
        ByteArrayOutputStream compressedOutput = new ByteArrayOutputStream();
        try (GZIPOutputStream gzipOutputStream = new GZIPOutputStream(compressedOutput)) {
            gzipOutputStream.write(inputBytes);
        }
        byte[] compressedBytes = compressedOutput.toByteArray();
        byte[] decompressedBytes = IoUtil.tryDecompress(compressedBytes);
        Assert.assertNotNull(decompressedBytes);
        Assert.assertTrue(decompressedBytes.length > 0);
        Assert.assertArrayEquals(inputBytes, decompressedBytes);
    }

    @Test
    public void testTryCompress() throws IOException {
        String inputString = "This is a test string.";
        String encoding = "UTF-8";
        byte[] compressedBytes = IoUtil.tryCompress(inputString, encoding);
        Assert.assertNotNull(compressedBytes);
        Assert.assertTrue(compressedBytes.length > 0);
        try (
                GZIPInputStream gzipInputStream = new GZIPInputStream(new ByteArrayInputStream(compressedBytes))) {
            byte[] decompressedBytes = new byte[1024];
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            int readBytes;
            while ((readBytes = gzipInputStream.read(decompressedBytes)) > 0) {
                outputStream.write(decompressedBytes, 0, readBytes);
            }
            String decompressedString = outputStream.toString(encoding);
            Assert.assertEquals(inputString, decompressedString);
        }
    }

    @Test
    public void testWriteStringToFile() throws IOException {
        File tempFile = new File(tempDir.toFile(), "testWriteStringToFile.txt");
        String testString = "test string";
        IoUtil.writeStringToFile(tempFile, testString, "UTF-8");
        BufferedReader reader = new BufferedReader(new FileReader(tempFile));
        String fileContent = reader.readLine();
        reader.close();
        Assert.assertEquals(testString, fileContent);
    }

    @Test
    public void testReadLines() throws IOException {
        File tempFile = new File(tempDir.toFile(), "testReadLines.txt");
        try (
                PrintWriter writer = new PrintWriter(tempFile)) {
            writer.println("test string 1");
            writer.println("test string 2");
            writer.println("test string 3");
        }
        FileReader fileReader = new FileReader(tempFile);
        List<String> lines = IoUtil.readLines(fileReader);
        fileReader.close();
        Assert.assertEquals(3, lines.size());
        Assert.assertEquals("test string 1", lines.get(0));
        Assert.assertEquals("test string 2", lines.get(1));
        Assert.assertEquals("test string 3", lines.get(2));
    }

    @Test
    public void testToStringInputStream() {
        String testString = "test string";
        InputStream inputStream = new ByteArrayInputStream(testString.getBytes());
        String result = IoUtil.toString(inputStream, "UTF-8");
        Assert.assertEquals(testString, result);
    }

    @Test
    public void testToStringReader() throws IOException {
        String testString = "test string";
        Reader reader = new StringReader(testString);
        String result = IoUtil.toString(reader);
        Assert.assertEquals(testString, result);
    }

    @Test
    public void testCopyReaderWriter() throws IOException {
        String inputString = "testCopyReaderWriter";
        Reader reader = new StringReader(inputString);
        Writer writer = new StringWriter();
        IoUtil.copy(reader, writer);
        Assert.assertEquals(inputString, writer.toString());
    }

    @Test
    public void testCopyInputStreamOutputStream() throws IOException {
        String inputString = "testCopyInputStreamOutputStream";
        InputStream inputStream = new ByteArrayInputStream(inputString.getBytes());
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        IoUtil.copy(inputStream, outputStream);
        Assert.assertEquals(inputString, outputStream.toString());
    }

    @Test
    public void testDelete() throws IOException {
        File deleteFile = new File(tempDir.toFile(), "delete.txt");
        deleteFile.createNewFile();
        Assert.assertTrue(deleteFile.exists());
        IoUtil.delete(deleteFile);
        Assert.assertFalse(deleteFile.exists());
        File deleteDir = new File(tempDir.toFile(), "delete");
        deleteDir.mkdirs();
        Assert.assertTrue(deleteDir.exists());
        File deleteDirFile = new File(deleteDir, "delete.txt");
        deleteDirFile.createNewFile();
        Assert.assertTrue(deleteDirFile.exists());
        IoUtil.delete(deleteDir);
        Assert.assertTrue(deleteDir.exists());
        Assert.assertFalse(deleteDirFile.exists());
    }

    @Test
    public void testCleanDirectory() throws IOException {
        File cleanDir = new File(tempDir.toFile(), "clean");
        cleanDir.mkdirs();
        File cleanFile1 = new File(cleanDir, "clean1.txt");
        File cleanFile2 = new File(cleanDir, "clean2.txt");
        Assert.assertTrue(cleanDir.exists());
        Assert.assertTrue(cleanFile1.createNewFile());
        Assert.assertTrue(cleanFile2.createNewFile());
        IoUtil.cleanDirectory(cleanDir);
        Assert.assertFalse(cleanFile1.exists());
        Assert.assertFalse(cleanFile2.exists());
    }

    @Test
    public void testCopyFile() throws Exception {
        String sourcePath = sourceFile.getAbsolutePath();
        String targetPath = sourceFile.getParent() + File.separator + "copy" + File.separator + "target.txt";
        IoUtil.copyFile(sourcePath, targetPath);
        File targetFile = new File(targetPath);
        Assert.assertTrue(targetFile.exists());
        Assert.assertEquals(sourceFile.length(), targetFile.length());
        byte[] sourceBytes = Files.readAllBytes(sourceFile.toPath());
        byte[] targetBytes = Files.readAllBytes(targetFile.toPath());
        Assert.assertArrayEquals(sourceBytes, targetBytes);
    }

    @Test
    public void testIsGzipStream() {
        byte[] gzipBytes = new byte[2];
        gzipBytes[0] = (byte) GZIPInputStream.GZIP_MAGIC;
        gzipBytes[1] = (byte) (GZIPInputStream.GZIP_MAGIC >> 8);
        byte[] invalidGzipBytes = new byte[2];
        invalidGzipBytes[0] = (byte) (GZIPInputStream.GZIP_MAGIC + 1);
        invalidGzipBytes[1] = (byte) ((GZIPInputStream.GZIP_MAGIC >> 8) + 1);
        byte[] invalidGzipBytes2 = new byte[1];
        byte[] normalBytes = new byte[2];
        Assert.assertTrue(IoUtil.isGzipStream(gzipBytes));
        Assert.assertFalse(IoUtil.isGzipStream(invalidGzipBytes));
        Assert.assertFalse(IoUtil.isGzipStream(invalidGzipBytes2));
        Assert.assertFalse(IoUtil.isGzipStream(null));
        Assert.assertFalse(IoUtil.isGzipStream(normalBytes));
    }

    @Test
    public void testCloseQuietly() throws IOException {
        Closeable closeable = new BrokenInputStream();
        URL url = new URL("https://www.baidu.com");
        HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
        Assertions.assertDoesNotThrow(() -> IoUtil.closeQuietly(closeable));
        Assertions.assertDoesNotThrow(() -> IoUtil.closeQuietly(closeable, closeable, closeable));
        Assertions.assertDoesNotThrow(() -> IoUtil.closeQuietly(httpURLConnection));
    }
}
