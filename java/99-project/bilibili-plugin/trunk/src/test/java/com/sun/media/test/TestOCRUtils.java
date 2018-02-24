package com.sun.media.test;

import java.io.File;

import exp.bilibili.plugin.utils.OCRUtils;

public class TestOCRUtils {

	public static void main(String[] args) {
		File dir = new File("./log/节奏风暴验证码");
		File[] imgs = dir.listFiles();
		for(File img : imgs) {
			System.out.println(img.getName() + " : " + OCRUtils.imgToTxt(img.getPath()));
		}
	}
	
}
