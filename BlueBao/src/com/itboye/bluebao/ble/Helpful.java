package com.itboye.bluebao.ble;

import java.io.ByteArrayOutputStream;

public class Helpful {
	// ***********************************************************************/
	// * the function Convert byte value to a "2-char String" Format
	// * Example : ConvertHexByteToString ((byte)0X0F) -> returns "0F"
	// ***********************************************************************/
	public static String MYConvertHexByteToString(byte byteToConvert) {
		String ConvertedByte = "";
		if (byteToConvert < 0) {
			ConvertedByte += Integer.toString(byteToConvert + 256, 16);
		} else if (byteToConvert <= 15) {
			ConvertedByte += "0" + Integer.toString(byteToConvert, 16);
		} else {
			ConvertedByte += Integer.toString(byteToConvert, 16);
		}

		return ConvertedByte;
	}

	public static String MYBytearrayToString(byte[] data) {
		String str = "";
		for (int i = 0; i < data.length; i++) {
			str += MYConvertHexByteToString(data[i]) + " ";
		}

		return str;
	}

	/*
	 * string转换成byte
	 */
	public static byte MYConvertCharToByte(char hvalue, char lvalue) {
		byte value = 0;

		if ((hvalue <= '9') && (hvalue >= '0')) {
			value = (byte) (((hvalue - '0') * 16) & 0xFF);
		} else {
			value = (byte) (((hvalue - 'A' + 10) * 16) & 0xFF);
		}
		
		if ((lvalue <= '9') && (lvalue >= '0')) {
			value |= (byte) ((lvalue - '0') & 0xFF);
		} else {
			value |= (byte) ((lvalue - 'A' + 10) & 0xFF);
		}

		return value;
	}
	
	/*
	 * string转换层byte[]
	 */
	public static byte[] MYConvertStringToByteArray(String str) {
		
		String str_tmp = str.toUpperCase();
		char hvalue;
		char lvalue;
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		
		for(int i=0; i<str_tmp.length(); ){
			hvalue = str.charAt(i);   // 高位
			
			if(((hvalue <= '9') && (hvalue >= '0')) || ((hvalue <= 'Z') && (hvalue >= 'A'))){
				if((i+1)>=str_tmp.length()){
					lvalue = '0';
				}else{
					lvalue = str.charAt(i+1);
					if(((lvalue <= '9') && (lvalue >= '0')) || ((lvalue <= 'Z') && (lvalue >= 'A'))){
						
					}else{
						lvalue = '0';
					}
				}
				i += 2;
				out.write(MYConvertCharToByte(hvalue, lvalue));
			}else{
				i++;
			}
		}
		
		return out.toByteArray();
	}
}
