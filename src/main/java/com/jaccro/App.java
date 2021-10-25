package com.jaccro;

import com.jaccro.service.ExcelService;

public class App {
	public static void main(String[] args) {

		ExcelService xlsService = new ExcelService();
		xlsService.write();
	}
}
