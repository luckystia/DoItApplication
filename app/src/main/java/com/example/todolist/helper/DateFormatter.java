package com.example.todolist.helper;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DateFormatter {
	public static Date stringToDate(String dob) throws ParseException {
		//Instantiating the SimpleDateFormat class
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
		//Parsing the given String to Date object
		Date date = formatter.parse(dob);
		System.out.println("Date object value: "+date);
		return date;
	}

	public static String formatTanggal(String tanggal) throws ParseException {
		Date date = stringToDate(tanggal);
		String formatted_date = new SimpleDateFormat("dd MMM yyyy").format(date);
		return formatted_date;
	}
}
