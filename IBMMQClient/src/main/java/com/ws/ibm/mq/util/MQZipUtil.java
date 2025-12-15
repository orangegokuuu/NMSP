package com.ws.ibm.mq.util;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import javax.xml.bind.DatatypeConverter;

public class MQZipUtil {
	
	public static final String ZIP_TIME_STAMP_FORMAT = "yyMMddHHmmsss";
	
	public static byte[] compress(String primaryKey, byte[] inputData) throws IOException {
		// create zip file
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		ZipOutputStream zos = new ZipOutputStream(baos);
		ZipEntry zipEntry = new ZipEntry(primaryKey);
		zipEntry.setSize(inputData.length);
		zos.putNextEntry(zipEntry);
		zos.write(inputData);
		zos.closeEntry();
		zos.close();
		return baos.toByteArray();
	}

	public static byte[] decompress(byte[] data) throws IOException {
		 ByteArrayOutputStream byteArrayOutputStream = null;
		    ZipInputStream zipIs = new ZipInputStream(new BufferedInputStream(new ByteArrayInputStream(data)));
		    byteArrayOutputStream = new ByteArrayOutputStream();
		    ZipEntry entry = zipIs.getNextEntry();
		    while (entry != null) {
		        byte[] tmp = new byte[2048];
		        BufferedOutputStream bos = null;
		        bos = new BufferedOutputStream(byteArrayOutputStream);
		        int size = 0;
		        while ((size = zipIs.read(tmp)) != -1) {
		            bos.write(tmp, 0, size);
		        }
		        bos.flush();
		        bos.close();
		        entry = zipIs.getNextEntry();
		    }
		    zipIs.close();
		    return byteArrayOutputStream.toByteArray();
	}
	
	public static byte[] decompress2(byte[] data) throws IOException {
		 ByteArrayOutputStream byteArrayOutputStream = null;
		    ZipInputStream zipIs = new ZipInputStream(new BufferedInputStream(new ByteArrayInputStream(data)));
		    byteArrayOutputStream = new ByteArrayOutputStream();
		    ZipEntry entry = zipIs.getNextEntry();
		    while (entry != null) {
		        byte[] tmp = new byte[1024];
		        BufferedOutputStream bos = null;
		        bos = new BufferedOutputStream(byteArrayOutputStream);
		        int size = 0;
		        while ((size = zipIs.read(tmp)) != -1) {
		            bos.write(tmp, 0, size);
		        }
		        bos.flush();
		        bos.close();
		        entry = zipIs.getNextEntry();
		    }
		    zipIs.close();
		    return byteArrayOutputStream.toByteArray();
	}
	
	/*
	public static void main(String[] args) {
		
		String hexString = "504B0304140008080800087E104F0000000000000000000000000D000000313930383136313534383031364D8DCD0AC2301084EF798A65EF5ABD2924E9C19F9B22A20F1092A544DAAC6653D1B73707A1FD6EC3CCF0E9F633F4F0A62C9193C1F5728540C97388A93378BF1D171B04292E05D77322835F126CADD2A7CB951EE48B5550D1879C39EF3890DD567433E559BF27F1393E4B55D933C34022AE2388095E238DF47FCD574A3793E807504B0708F0F1AFBB80000000AC000000504B01021400140008080800087E104FF0F1AFBB80000000AC0000000D000000000000000000000000000000000031393038313631353438303136504B050600000000010001003B000000BB0000000000";
		
		System.out.println(hexString.replaceAll(" ", ""));
		
		try {
			System.out.println(new String(decompress(parseHexString(hexString.trim()))));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static byte[] parseHexString(String hexString) {
		return DatatypeConverter.parseHexBinary(hexString);
	}
	*/
}
