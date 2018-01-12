package com.wonhigh.bs.sso.admin.common.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.RandomAccessFile;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

/**
 * TODO: 增加描述
 * 
 * @author user
 * @date 2017年11月12日 下午4:05:27
 * @version 0.1.0 
 * @copyright wonhigh.cn
 */
public class ExcelToLdifUtil {
	
	private static final Pattern BLANKPATTERN = Pattern.compile("\\s*|\t|\r|\n");
	
	public static void main(String[] args) throws Exception  {
		
		/*String excel = "C:\\Users\\user\\Desktop\\hr\\HR账户信息-1.xlsx";
		String ldif = "C:\\Users\\user\\Desktop\\hr\\test\\HR-1.csv";
		String userpwd = "C:\\Users\\user\\Desktop\\hr\\test\\HR-password-1.txt";
		ExcelToLdifUtil.exportCsv(excel, ldif, userpwd);*/
		
		/*String excel = "C:\\Users\\user\\Desktop\\dev-data\\sso_user.txt";
		String ldif = "C:\\Users\\user\\Desktop\\dev-data\\sso_user.ldif";
		String userpwd = "C:\\Users\\user\\Desktop\\online-data\\HR-password-1.txt";
		ExcelToLdifUtil.exportTxtHROES(excel, ldif, userpwd);*/
		
		//String excel = "C:\\Users\\user\\Desktop\\online-data\\OES初始化账户信息20171127.xlsx";
		/*String ldif = "C:\\Users\\user\\Desktop\\online-data\\HR-OES-online.ldif";
		String userpwd = "C:\\Users\\user\\Desktop\\online-data\\HR-OES-online-password.txt";
		ExcelToLdifUtil.exportLdifOES(excel, ldif, userpwd);*/
		
		/*String sql = "C:\\Users\\user\\Desktop\\online-data\\HR-OES-online.sql";
		String sqlpwd = "C:\\Users\\user\\Desktop\\online-data\\HR-OES-online-password2.txt";
		ExcelToLdifUtil.exportTxtOES(excel, sql, sqlpwd);*/
		
		/*File mobileFile = new File("C:\\Users\\user\\Desktop\\dev-data\\需要修改手机号为null.json");
    	String txt2String = ExcelToLdifUtil.txt2String(mobileFile);
    	System.out.println(txt2String);*/
		
		String excel = "C:\\Users\\user\\Desktop\\新业务\\新业务初始化账户信息20171205.xlsx";
		String ldif = "C:\\Users\\user\\Desktop\\新业务\\新业务初始化账户信息20171205.ldif";
		String userpwd = "C:\\Users\\user\\Desktop\\新业务\\password.txt";
		ExcelToLdifUtil.exportLdif(excel, ldif, userpwd);
		
    }
	
	private static String txt2String(File file){
        StringBuilder result = new StringBuilder();
        try{
            BufferedReader br = new BufferedReader(new FileReader(file));
            String s = null;
            while((s = br.readLine())!=null){//使用readLine方法，一次读一行
                result.append(System.lineSeparator()+s);
            }
            br.close();    
        }catch(Exception e){
            e.printStackTrace();
        }
        return result.toString();
    }
	
	public static void exportLdifFromSql(String excelFilePath, String ldifFilePath, String userpwdPath) throws FileNotFoundException, IOException, NoSuchAlgorithmException, InvalidFormatException{
		File excelFile = new File(excelFilePath);
        XSSFWorkbook wb = new XSSFWorkbook(new FileInputStream(excelFile));
        XSSFSheet sheet = wb.getSheetAt(0);
        
        // 打开一个随机访问文件流，按读写方式
        RandomAccessFile randomFile = new RandomAccessFile(ldifFilePath, "rw");
        String hrBizCode = "HR";
        JSONArray sendData = new JSONArray();
        int l = -1;
        for (Row row : sheet) {
        	String d = DateUtil.getDateTimeFormat(new Date());
        	l++;
        	if(l==0) {
        		continue;
        	}
        	JSONObject obj = new JSONObject();
        	int i= 1;
        	String loginName = "";
        	MessageDigest md = MessageDigest.getInstance("MD5");
        	for (int j=0;j<row.getLastCellNum();j++) {
        		Cell cell = row.getCell(j);
            	String content = "";
            	if(cell!=null){
	            	cell.setCellType(Cell.CELL_TYPE_STRING);
	            	content = cell.getRichStringCellValue().getString();
	            	content = ExcelToLdifUtil.replaceBlank(content);
            	}
            	if(i==1){ 
            		loginName = content;
            		content = "dn: uid="+content+",ou=employee,dc=wonhigh,dc=com";
            		content += "\r\n";
            		content += "objectclass: SsoUser";
            		content += "\r\n";
            		content += "objectclass: top";
            		content += "\r\n";
            		content += "uid: "+loginName;
            		obj.put("loginName", loginName);
            		
            		long fileLength = randomFile.length();
                    randomFile.seek(fileLength);
                    randomFile.write(content.getBytes());
                    randomFile.writeBytes("\r\n");
            	}
            	if(i==2){
            		content = Base64Utils.encrytor(content);
            		content = "sn:: "+content;
            		long fileLength = randomFile.length();
                    randomFile.seek(fileLength);
                    randomFile.write(content.getBytes());
                    randomFile.writeBytes("\r\n");
            	}
            	if(i==3){
            		obj.put("phone", content);
            		if(StringUtils.isEmpty(content)){
            			content = "mobile: 0";
            		}else{
            			content = "mobile: "+content;
            		}
            		
            		long fileLength = randomFile.length();
                    randomFile.seek(fileLength);
                    randomFile.write(content.getBytes());
                    randomFile.writeBytes("\r\n");
            	}
            	if(i==4){
            		content = "employeeNumber: "+content;
            		long fileLength = randomFile.length();
                    randomFile.seek(fileLength);
                    randomFile.write(content.getBytes());
                    randomFile.writeBytes("\r\n");
            	}
            	if(i==5){
            		if(StringUtils.isEmpty(content)){
            			content = "mail: NULL";
            		}else{
            			content = "mail: "+content;
            		}
            		long fileLength = randomFile.length();
                    randomFile.seek(fileLength);
                    randomFile.write(content.getBytes());
                    randomFile.writeBytes("\r\n");
            	}
            	if(i==6){
            		obj.put("password", content);
            		content = "userPassword: "+content;
            		long fileLength = randomFile.length();
                    randomFile.seek(fileLength);
                    randomFile.write(content.getBytes());
                    randomFile.writeBytes("\r\n");
            	}
            	if(i==7){
            		content = "biz-user: "+content;
            		long fileLength = randomFile.length();
                    randomFile.seek(fileLength);
                    randomFile.write(content.getBytes());
                    randomFile.writeBytes("\r\n");
            	}
            	if(i==8){
            		String org = Base64Utils.encrytor(content);
            		content = "organizationalUnitName:: "+ org;
            		long fileLength = randomFile.length();
                    randomFile.seek(fileLength);
                    randomFile.write(content.getBytes());
                    randomFile.writeBytes("\r\n");
            	}
            	if(i==9){
            		content = "organization-code: "+content;
            		long fileLength = randomFile.length();
                    randomFile.seek(fileLength);
                    randomFile.write(content.getBytes());
                    randomFile.writeBytes("\r\n");
            	}
            	if(i==13){
            		content = "update-time-str: "+d;
            		long fileLength = randomFile.length();
                    randomFile.seek(fileLength);
                    randomFile.write(content.getBytes());
                    randomFile.writeBytes("\r\n");
            	}
            	if(i==15){
            		content = "sex: "+content;
            		long fileLength = randomFile.length();
                    randomFile.seek(fileLength);
                    randomFile.write(content.getBytes());
                    randomFile.writeBytes("\r\n");
            	}
            	if(i==16){
            		content = "state: "+content;
            		long fileLength = randomFile.length();
                    randomFile.seek(fileLength);
                    randomFile.write(content.getBytes());
                    randomFile.writeBytes("\r\n");
            	}
            	if(i==17){
            		content = "del-flag: "+content;
            		long fileLength = randomFile.length();
                    randomFile.seek(fileLength);
                    randomFile.write(content.getBytes());
                    randomFile.writeBytes("\r\n");
            	}
            	if(i==20){
            		content = "create-time-str: "+d;
            		content += "\r\n";
            		content += "creater: HR";
            		content += "\r\n";
            		content += "id-card: NULL";
            		content += "\r\n";
            		content += "telephoneNumber: 0";
            		content += "\r\n";
            		content += "description: NULL";
            		content += "\r\n";
            		content += "employeeType: 0";
            		long fileLength = randomFile.length();
                    randomFile.seek(fileLength);
                    randomFile.write(content.getBytes());
                    randomFile.writeBytes("\r\n");
            	}
                System.out.println("写入第 "+l+" 条数据..");
                i++;
            }
            randomFile.writeBytes("\r\n");
            sendData.add(obj);
        }
        randomFile.close();
        System.out.println("写入完成，总数="+l);
        
        //保存用户密码
        try  
        {      
          File fileText = new File(userpwdPath);  
          FileWriter fileWriter = new FileWriter(fileText);  
          fileWriter.write(sendData.toJSONString());  
          fileWriter.close();  
        }  
        catch (IOException e)  
        {  
          e.printStackTrace();  
        }  
	}
	
	public static void exportLdif(String excelFilePath, String ldifFilePath, String userpwdPath) throws FileNotFoundException, IOException, NoSuchAlgorithmException, InvalidFormatException{
		File excelFile = new File(excelFilePath);
        XSSFWorkbook wb = new XSSFWorkbook(new FileInputStream(excelFile));
        XSSFSheet sheet = wb.getSheetAt(0);
        
        // 打开一个随机访问文件流，按读写方式
        RandomAccessFile randomFile = new RandomAccessFile(ldifFilePath, "rw");
        String hrBizCode = "HR";
        JSONArray sendData = new JSONArray();
        int l = -1;
        for (Row row : sheet) {
        	l++;
        	if(l==0) {
        		continue;
        	}
        	JSONObject obj = new JSONObject();
        	int i= 1;
        	String loginName = "";
        	MessageDigest md = MessageDigest.getInstance("MD5");
        	for (int j=0;j<row.getLastCellNum();j++) {
        		Cell cell = row.getCell(j);
            	String content = "";
            	if(cell!=null){
	            	cell.setCellType(Cell.CELL_TYPE_STRING);
	            	content = cell.getRichStringCellValue().getString();
	            	content = ExcelToLdifUtil.replaceBlank(content);
            	}
            	content = content.replace(" ", "");
            	if(i==1){ 
            		loginName = content;
            		content = "dn: uid="+content+",ou=employee,dc=wonhigh,dc=com";
            		content += "\r\n";
            		content += "objectclass: SsoUser";
            		content += "\r\n";
            		content += "objectclass: top";
            		content += "\r\n";
            		content += "uid: "+loginName;
            		obj.put("loginName", loginName);
            	}
            	if(i==2){
            		if(StringUtils.isEmpty(content)){
            			content = "mail: NULL";
            		}else{
            			content = "mail: "+StringUtils.trimToEmpty(content);
            		}
            	}
            	if(i==3){
            		obj.put("phone", content);
            		if(StringUtils.isEmpty(content)){
            			content = "mobile: 0";
            		}else{
            			content = "mobile: "+StringUtils.trimToEmpty(content);
            		}
            	}
            	if(i==4){
            		content = "employeeNumber: "+content;
            	}
            	if(i==5){
            		content = Base64Utils.encrytor(content);
            		content = "sn:: "+content;
            	}
            	if(i==6){
            		if("男".equalsIgnoreCase(content)){
            			content = "sex: 1";
            		}else{
            			content = "sex: 0";
            		}
            	}
            	if(i==7){
            		String idCard = StringUtils.trimToEmpty(content);
            		content =  "id-card: "+idCard;
            		content += "\r\n";
            		String password = idCard.substring(idCard.length()-6);
            		obj.put("password", password);
            		String pwd = PasswordUtil.createPassword(password);
            		content += "userPassword: "+pwd;
            		//content = "userPassword: {MD5}4QrcOUm6Wau+VuBX8g+IPg==";
            	}
            	if(i==8){
            		content = "organization-code: "+content;
            	}
            	if(i==9){
            		String org = Base64Utils.encrytor(content);
            		content = "organizationalUnitName:: "+ org;
            		content += "\r\n";
            		content += "biz-user: {\""+hrBizCode+"\":\""+loginName+"\"}";
            		content += "\r\n";
            		content += "creater: HR";
            		content += "\r\n";
            		content += "del-flag: 0";
            		content += "\r\n";
            		content += "state: 0";
            		String d = DateUtil.getDateTimeFormat(new Date());
            		content += "\r\n";
            		content += "create-time-str: "+d;
            		content += "\r\n";
            		content += "update-time-str: "+d;
            		content += "\r\n";
            		content += "telephoneNumber: 0";
            		content += "\r\n";
            		content += "description: NULL";
            		content += "\r\n";
            		content += "employeeType: 0";
            	}
            	if(i>=10){
            		continue;
            	}
                try {
                	System.out.println("写入第 "+l+" 条数据..");
                    long fileLength = randomFile.length();
                    randomFile.seek(fileLength);
                    randomFile.write(content.getBytes());
                    randomFile.writeBytes("\r\n");
                } catch (IOException e) {
                    e.printStackTrace();
                }
                i++;
            }
            randomFile.writeBytes("\r\n");
            sendData.add(obj);
        }
        randomFile.close();
        System.out.println("写入完成");
        
        //保存用户密码
        try  
        {      
          File fileText = new File(userpwdPath);  
          FileWriter fileWriter = new FileWriter(fileText);  
          fileWriter.write(sendData.toJSONString());  
          fileWriter.close();  
        }  
        catch (IOException e)  
        {  
          e.printStackTrace();  
        }  
	}
	
	public static void exportCsv(String excelFilePath, String csvFilePath, String userpwdPath) throws FileNotFoundException, IOException, NoSuchAlgorithmException, InvalidFormatException{
		File excelFile = new File(excelFilePath);
        XSSFWorkbook wb = new XSSFWorkbook(new FileInputStream(excelFile));
        XSSFSheet sheet = wb.getSheetAt(0);
        
        // 打开一个随机访问文件流，按读写方式
        RandomAccessFile randomFile = new RandomAccessFile(csvFilePath, "rw");
        String head = "loginName,email,mobile,employeeNumber,sureName,sex,password,organizationCode,organizationalUnitName,bizUser,createUser,delFlag,state,createTime,updateTime\r\n";
        try {
            // 文件长度，字节数
            long fileLength = randomFile.length();
            //将写文件指针移到文件尾。
            randomFile.seek(fileLength);
            randomFile.write(head.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
        String hrBizCode = "HR";
        JSONArray sendData = new JSONArray();
        int l = -1;
        for (Row row : sheet) {
        	l++;
        	if(l==0) {
        		continue;
        	}
        	JSONObject obj = new JSONObject();
        	int i= 1;
        	String loginName = "";
        	MessageDigest md = MessageDigest.getInstance("MD5");
        	for (int j=0;j<row.getLastCellNum();j++) {
        		Cell cell = row.getCell(j);
            	String content = "";
            	if(cell!=null){
	            	cell.setCellType(Cell.CELL_TYPE_STRING);
	            	content = cell.getRichStringCellValue().getString();
	            	content = ExcelToLdifUtil.replaceBlank(content);
            	}
            	if(i==1){ 
            		loginName = content;
            		content = loginName+",";
            		obj.put("loginName", loginName);
            	}
            	if(i==2){
            		content += ",";
            	}
            	if(i==3){
            		obj.put("phone", content);
            		content += ",";
            	}
            	if(i==4){
            		content += ",";
            	}
            	if(i==5){
            		content += ",";
            	}
            	if(i==6){
            		if("男".equalsIgnoreCase(content)){
            			content = "1,";
            		}else{
            			content = "0,";
            		}
            	}
            	if(i==7){
            		obj.put("password", content);
            		String pwd = PasswordUtil.createPassword(content);
            		content = pwd+",";
            		//content = "{MD5}4QrcOUm6Wau+VuBX8g+IPg==,";
            	}
            	if(i==8){
            		content += ",";
            	}
            	if(i==9){
            		content += ",";
            		content += "{\""+hrBizCode+"\":\""+loginName+"\"},";
            		content += "HR,"; //createUser
            		content += "0,"; //del-flag
            		content += "0,"; //state
            		String d = DateUtil.getDateTimeFormat(new Date());
            		content += d+","; //create-time
            		content += d;//update-time
            	}
            	if(i>=10){
            		continue;
            	}
            	//oes
            	/*if(i==10){
            		i++;
            		continue;
            	}
            	if(i==11){
            		String oes = Base64Utils.encrytor(content);
            		content = "biz-user: {\""+hrBizCode+"\":\""+loginName+"\",\"OES\":\""+content+"\"}";
            		content += "\r\n";
            		content += "creater: HR";
            		content += "\r\n";
            		content += "del-flag: 0";
            		content += "\r\n";
            		content += "state: 0";
            		String d = DateUtil.getDateTimeFormat(new Date());
            		content += "\r\n";
            		content += "create-time-str: "+d;
            		content += "\r\n";
            		content += "update-time-str: "+d;
            		content += "\r\n";
            		content += "id-card: NULL";
            		content += "\r\n";
            		content += "telephoneNumber: 0";
            		content += "\r\n";
            		content += "description: NULL";
            		content += "\r\n";
            		content += "employeeType: -1";
            	}
            	if(i>11){
            		continue;
            	}*/
                try {
                	System.out.println("写入第 "+l+" 条数据..");
                    // 文件长度，字节数
                    long fileLength = randomFile.length();
                    //将写文件指针移到文件尾。
                    randomFile.seek(fileLength);
                    randomFile.write(content.getBytes());
                } catch (IOException e) {
                    e.printStackTrace();
                }
                i++;
            }
            randomFile.writeBytes("\r\n");
            sendData.add(obj);
        }
        randomFile.close();
        System.out.println("写入完成");
        
        //保存用户密码
        try  
        {      
          File fileText = new File(userpwdPath);  
          FileWriter fileWriter = new FileWriter(fileText);  
          fileWriter.write(sendData.toJSONString());  
          fileWriter.close();  
        }  
        catch (IOException e)  
        {  
          e.printStackTrace();  
        }  
	}
	
	public static void exportLdifOES(String excelFilePath, String ldifFilePath, String userpwdPath) throws FileNotFoundException, IOException, NoSuchAlgorithmException, InvalidFormatException{
		File excelFile = new File(excelFilePath);
        XSSFWorkbook wb = new XSSFWorkbook(new FileInputStream(excelFile));
        XSSFSheet sheet = wb.getSheetAt(0);
        
        // 打开一个随机访问文件流，按读写方式
        RandomAccessFile randomFile = new RandomAccessFile(ldifFilePath, "rw");
        String hrBizCode = "HR";
        JSONArray sendData = new JSONArray();
        int l = -1;
        for (Row row : sheet) {
        	l++;
        	if(l==0) {
        		continue;
        	}
        	JSONObject obj = new JSONObject();
        	int i= 1;
        	String loginName = "";
        	MessageDigest md = MessageDigest.getInstance("MD5");
        	for (int j=0;j<row.getLastCellNum();j++) {
        		Cell cell = row.getCell(j);
            	String content = "";
            	if(cell!=null){
	            	cell.setCellType(Cell.CELL_TYPE_STRING);
	            	content = cell.getRichStringCellValue().getString();
	            	content = ExcelToLdifUtil.replaceBlank(content);
            	}
            	if(i==1){ 
            		loginName = content;
            		content = "dn: uid="+content+",ou=employee,dc=wonhigh,dc=com";
            		content += "\r\n";
            		content += "objectclass: SsoUser";
            		content += "\r\n";
            		content += "objectclass: top";
            		content += "\r\n";
            		content += "uid: "+loginName;
            		obj.put("loginName", loginName);
            	}
            	if(i==2){
            		content = "mail: "+content;
            	}
            	if(i==3){
            		obj.put("phone", content);
            		content = "mobile: "+content;
            	}
            	if(i==4){
            		content = "employeeNumber: "+content;
            	}
            	if(i==5){
            		content = Base64Utils.encrytor(content);
            		content = "sn:: "+content;
            	}
            	if(i==6){
            		if("男".equalsIgnoreCase(content)){
            			content = "sex: 1";
            		}else{
            			content = "sex: 0";
            		}
            	}
            	if(i==7){
            		obj.put("password", content);
            		String pwd = PasswordUtil.createPassword(content);
            		content = "userPassword: "+pwd;
            		//content = "userPassword: {MD5}4QrcOUm6Wau+VuBX8g+IPg==";
            	}
            	if(i==8){
            		content = "organization-code: "+content;
            	}
            	if(i==9){
            		String org = Base64Utils.encrytor(content);
            		content = "organizationalUnitName:: "+ org;
            	}
            	//oes
            	if(i==10){
            		i++;
            		continue;
            	}
            	if(i==11){
            		content = "biz-user: {\""+hrBizCode+"\":\""+loginName+"\",\"OES\":\""+content+"\"}";
            		content += "\r\n";
            		content += "creater: HR";
            		content += "\r\n";
            		content += "del-flag: 0";
            		content += "\r\n";
            		content += "state: 0";
            		String d = DateUtil.getDateTimeFormat(new Date());
            		content += "\r\n";
            		content += "create-time-str: "+d;
            		content += "\r\n";
            		content += "update-time-str: "+d;
            		content += "\r\n";
            		content += "id-card: NULL";
            		content += "\r\n";
            		content += "telephoneNumber: 0";
            		content += "\r\n";
            		content += "description: NULL";
            		content += "\r\n";
            		content += "employeeType: -1";
            	}
            	if(i>11){
            		continue;
            	}
                try {
                	System.out.println("写入第 "+l+" 条数据..");
                    // 文件长度，字节数
                    long fileLength = randomFile.length();
                    //将写文件指针移到文件尾。
                    randomFile.seek(fileLength);
                    randomFile.write(content.getBytes());
                    randomFile.writeBytes("\r\n");
                } catch (IOException e) {
                    e.printStackTrace();
                }
                i++;
            }
            randomFile.writeBytes("\r\n");
            sendData.add(obj);
        }
        randomFile.close();
        System.out.println("写入完成");
        
        //保存用户密码
        try  
        {      
          File fileText = new File(userpwdPath);  
          FileWriter fileWriter = new FileWriter(fileText);  
          fileWriter.write(sendData.toJSONString());  
          fileWriter.close();  
        }  
        catch (IOException e)  
        {  
          e.printStackTrace();  
        }  
	}
	
	public static void exportTxtOES(String excelFilePath, String csvFilePath, String userpwdPath) throws FileNotFoundException, IOException, NoSuchAlgorithmException, InvalidFormatException{
		File excelFile = new File(excelFilePath);
        XSSFWorkbook wb = new XSSFWorkbook(new FileInputStream(excelFile));
        XSSFSheet sheet = wb.getSheetAt(0);
        // 打开一个随机访问文件流，按读写方式
        RandomAccessFile randomFile = new RandomAccessFile(csvFilePath, "rw");
        String head = "INSERT INTO `sso_user_tmp` (`login_name`, `email`, `mobile`, `employee_number`, `sure_name`, `sex`, `password`, `organization_code`, `organizational_unit_name`, `biz_user`, `telephone_number`, `create_user`, `state`, `del_flag`, `create_time`) ";
        String hrBizCode = "HR";
        JSONArray sendData = new JSONArray();
        int l = -1;
        for (Row row : sheet) {
        	l++;
        	if(l==0) {
        		continue;
        	}
        	JSONObject obj = new JSONObject();
        	int i= 1;
        	String loginName = "";
        	MessageDigest md = MessageDigest.getInstance("MD5");
        	// 文件长度，字节数
            long fileLength = randomFile.length();
            //将写文件指针移到文件尾。
            randomFile.seek(fileLength);
        	randomFile.write(head.getBytes());
        	// 文件长度，字节数
            fileLength = randomFile.length();
            //将写文件指针移到文件尾。
            randomFile.seek(fileLength);
        	randomFile.write(" VALUES ( ".getBytes());
        	for (int j=0;j<row.getLastCellNum();j++) {
        		Cell cell = row.getCell(j);
            	String content = "";
            	if(cell!=null){
	            	cell.setCellType(Cell.CELL_TYPE_STRING);
	            	content = cell.getRichStringCellValue().getString();
	            	content = ExcelToLdifUtil.replaceBlank(content);
            	}
            	//loginName
            	if(i==1){ 
            		// 文件长度，字节数
                    fileLength = randomFile.length();
                    //将写文件指针移到文件尾。
                    randomFile.seek(fileLength);
            		randomFile.write(("'"+content+"', ").getBytes());
            		obj.put("loginName", content);
            		loginName = content;
            	}
            	//Email
            	if(i==2){
            		// 文件长度，字节数
                    fileLength = randomFile.length();
                    //将写文件指针移到文件尾。
                    randomFile.seek(fileLength);
            		randomFile.write(("'"+content+"', ").getBytes());
            	}
            	//mobile
            	if(i==3){
            		// 文件长度，字节数
                    fileLength = randomFile.length();
                    //将写文件指针移到文件尾。
                    randomFile.seek(fileLength);
            		randomFile.write(("'"+content+"', ").getBytes());
            	}
            	//employeeNumber
            	if(i==4){
            		// 文件长度，字节数
                    fileLength = randomFile.length();
                    //将写文件指针移到文件尾。
                    randomFile.seek(fileLength);
            		randomFile.write(("'"+content+"', ").getBytes());
            	}
            	//sureName
            	if(i==5){
            		// 文件长度，字节数
                    fileLength = randomFile.length();
                    //将写文件指针移到文件尾。
                    randomFile.seek(fileLength);
            		randomFile.write(("'"+content+"', ").getBytes());
            	}
            	//sex
            	if(i==6){
            		// 文件长度，字节数
                    fileLength = randomFile.length();
                    //将写文件指针移到文件尾。
                    randomFile.seek(fileLength);
            		if("男".equalsIgnoreCase(content)){
            			randomFile.write(("'1', ").getBytes());
            		}else{
            			randomFile.write(("'0', ").getBytes());
            		}
            	}
            	//pwd
            	if(i==7){
            		// 文件长度，字节数
                    fileLength = randomFile.length();
                    //将写文件指针移到文件尾。
                    randomFile.seek(fileLength);
            		obj.put("password", content);
            		String pwd = PasswordUtil.createPassword(content);
            		randomFile.write(("'"+pwd+"', ").getBytes());
            	}
            	//orgCode
            	if(i==8){
            		// 文件长度，字节数
                    fileLength = randomFile.length();
                    //将写文件指针移到文件尾。
                    randomFile.seek(fileLength);
            		randomFile.write(("'"+content+"', ").getBytes());
            	}
            	//orgName
            	if(i==9){
            		// 文件长度，字节数
                    fileLength = randomFile.length();
                    //将写文件指针移到文件尾。
                    randomFile.seek(fileLength);
            		randomFile.write(("'"+content+"', ").getBytes());
            	}
            	//oes
            	if(i==10){
            		i++;
            		continue;
            	}
            	if(i==11){
            		// 文件长度，字节数
                    fileLength = randomFile.length();
                    //将写文件指针移到文件尾。
                    randomFile.seek(fileLength);
            		//bizUser
            		content = "{\\\""+hrBizCode+"\\\":\\\""+loginName+"\\\",\\\"OES\\\":\\\""+content+"\\\"}";
            		randomFile.write(("'"+content+"', ").getBytes());
            		//`telephone_number`, 
            		// 文件长度，字节数
                    fileLength = randomFile.length();
                    //将写文件指针移到文件尾。
                    randomFile.seek(fileLength);
            		randomFile.write(("'', ").getBytes());
            		//`create_user`, 
            		// 文件长度，字节数
                    fileLength = randomFile.length();
                    //将写文件指针移到文件尾。
                    randomFile.seek(fileLength);
            		randomFile.write(("'HR', ").getBytes());
            		//`state`, 
            		// 文件长度，字节数
                    fileLength = randomFile.length();
                    //将写文件指针移到文件尾。
                    randomFile.seek(fileLength);
            		randomFile.write(("'0', ").getBytes());
            		//`del_flag`, 
            		// 文件长度，字节数
                    fileLength = randomFile.length();
                    //将写文件指针移到文件尾。
                    randomFile.seek(fileLength);
            		randomFile.write(("'0', ").getBytes());
            		//`create_time`
            		// 文件长度，字节数
                    fileLength = randomFile.length();
                    //将写文件指针移到文件尾。
                    randomFile.seek(fileLength);
            		String d = DateUtil.getDateTimeFormat(new Date());
            		randomFile.write(("'"+d+"'").getBytes());
            		
            		//sql结束
            		// 文件长度，字节数
                    fileLength = randomFile.length();
                    //将写文件指针移到文件尾。
                    randomFile.seek(fileLength);
            		randomFile.write((" ); ").getBytes());
            		//randomFile.writeBytes("\r\n");
            		
            		System.out.println("写入第 "+l+" 条数据..");
            	}
            	if(i>11){
            		continue;
            	}
                i++;
            }
            randomFile.writeBytes("\r\n");
            sendData.add(obj);
        }
        randomFile.close();
        System.out.println("写入完成");
        
        //保存用户密码
        try  
        {      
          File fileText = new File(userpwdPath);  
          FileWriter fileWriter = new FileWriter(fileText);  
          fileWriter.write(sendData.toJSONString());  
          fileWriter.close();  
        }  
        catch (IOException e)  
        {  
          e.printStackTrace();  
        }  
	}
	
	public static void exportTxtHROES(String excelFilePath, String csvFilePath,
			String userpwdPath) throws FileNotFoundException, IOException,
			NoSuchAlgorithmException, InvalidFormatException {

		RandomAccessFile randomFile = new RandomAccessFile(csvFilePath, "rw");
		FileInputStream fis = null;
		InputStreamReader isr = null;
		BufferedReader br = null; // 用于包装InputStreamReader,提高处理性能。因为BufferedReader有缓冲的，而InputStreamReader没有。
		try {
			String str = "";
			fis = new FileInputStream(excelFilePath);// FileInputStream
			// 从文件系统中的某个文件中获取字节
			isr = new InputStreamReader(fis);// InputStreamReader 是字节流通向字符流的桥梁,
			br = new BufferedReader(isr);// 从字符输入流中读取文件中的内容,封装了一个new
											// InputStreamReader的对象
			int count = -1;
			while ((str = br.readLine()) != null) {
				count++;
				if(count==0){
				    continue;
				}
				String content = "";
				String[] split = str.split("#");
				for (int i = 0; i < split.length; i++) {
					if(i==0){
						content = "dn: uid="+split[i]+",ou=employee,dc=wonhigh,dc=com";
						content += "\r\n";
		        		content += "objectclass: SsoUser";
		        		content += "\r\n";
		        		content += "objectclass: top";
		        		content += "\r\n";
		        		content += "uid: "+split[i];
		        		content += "\r\n";
					}
					if(i==1){
	            		content += "sn:: "+Base64Utils.encrytor(split[i]);
	            		content += "\r\n";
					}
					if(i==2){
						if(StringUtils.isEmpty(split[i])){
							content += "mobile: 0";
						}else{
							content += "mobile: "+split[i];
						}
		        		content += "\r\n";
					}if(i==3){
						if(StringUtils.isEmpty(split[i])){
							content += "employeeNumber: NULL";
						}else{
							content += "employeeNumber: "+split[i];
						}
		        		content += "\r\n";
					}
					if(i==4){
						if(StringUtils.isEmpty(split[i])){
							content += "mail: NULL";
						}else{
							content += "mail: "+split[i];
						}
		        		content += "\r\n";
					}if(i==5){
						content += "userPassword: "+split[i];
		        		content += "\r\n";
					}
					if(i==6){
						content += "biz-user: "+split[i];
		        		content += "\r\n";
					}if(i==7){
						String org = Base64Utils.encrytor(split[i]);
	            		content += "organizationalUnitName:: "+ org;
		        		content += "\r\n";
					}
					if(i==8){
	            		content += "organization-code: "+ split[i];
		        		content += "\r\n";
					}
					if(i==9){
						content += "unit-id: "+split[i];
		        		content += "\r\n";
					}
					if(i==10){
						content += "creater: "+split[i];
		        		content += "\r\n";
					}
					if(i==11){
						//create_user_id
					}if(i==12){
						content += "update-time-str: "+split[i];
		        		content += "\r\n";
					}
					if(i==13){
						//update_user
						content += "telephoneNumber: 0";
		        		content += "\r\n";
		        		content += "employeeType: 0";
		        		content += "\r\n";
					}if(i==14){
						content += "sex: "+split[i];
		        		content += "\r\n";
					}
					if(i==15){
						content += "state: "+split[i];
		        		content += "\r\n";
					}if(i==16){
						content += "del-flag: "+split[i];
		        		content += "\r\n";
					}
					if(i==17){
						if(StringUtils.isEmpty(split[i])){
							content += "description: NULL";
						}else{
							content += "description: "+split[i];
						}
		        		content += "\r\n";
					}
					if(i==18){
						if(StringUtils.isEmpty(split[i])){
							content += "id-card: NULL";
						}else{
							content += "id-card: "+split[i];
						}
		        		content += "\r\n";
					}
					if(i==19){
						content += "create-time-str: "+split[i];
		        		content += "\r\n";
					}
				}
				long fileLength = randomFile.length();
                randomFile.seek(fileLength);
                randomFile.write(content.getBytes());
                randomFile.writeBytes("\r\n");
                System.out.println("写入第"+count);
			}
			randomFile.close();
		} catch (FileNotFoundException e) {
			System.out.println("找不到指定文件");
		} catch (IOException e) {
			System.out.println("读取文件失败");
		} finally {
			try {
				br.close();
				isr.close();
				fis.close();
				// 关闭的时候最好按照先后顺序关闭最后开的先关闭所以先关s,再关n,最后关m
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	 public static String replaceBlank(String str) {  
        String dest = "";  
        if (str!=null) {  
            Matcher m = BLANKPATTERN.matcher(str);  
            dest = m.replaceAll("");  
        }  
        return dest;  
    }  


}
