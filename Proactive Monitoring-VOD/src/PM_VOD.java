import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;

import com.experitest.client.*;
import com.experitest.client.InternalException;

import junit.framework.AssertionFailedError;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.file.Files;
import java.nio.file.Path;
//import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.imageio.ImageIO;

import org.junit.*;
/**
 *
*/
public class PM_VOD {
    private String host = "localhost";
    private int port = 8889;
    private String projectBaseDirectory = "C:\\Users\\VOD-Testing\\workspace\\FET_VOD_1112";
    protected Client client = null;

    @Before
    public void setUp(){
        client = new Client(host, port, true);
        client.setProjectBaseDirectory(projectBaseDirectory);
        client.setReporter("xml", "C:\\Users\\VOD-Testing\\Desktop\\Report", "VOD");
    }
    String picname="-";
    Path psource;
    Path pdest;
    String logtime;
    String device=System.getProperty("device");
    String stream="-";
//  String stream=System.getProperty("stream");
    String csvpath=System.getProperty("csvpath");
    String scriptpath=System.getProperty("scriptpath");
    String imagepath=System.getProperty("imagepath");
    String apppath=System.getProperty("apppath");
//    private String csvpath="C:\\Users\\SeeTest\\seetest-reports\\";
//    private String scriptpath="C:\\Users\\SeeTest\\Desktop\\scriptlog\\";
//    private String imagepath="C:\\Users\\SeeTest\\Desktop\\img\\";
//   private String apptemppath="C:\\Users\\SeeTest\\Desktop\\";
    String result,errorcode;
    SimpleDateFormat date=new SimpleDateFormat("yyyyMMddHHmmss");
   	@Test
    public void Proactive(){
//    	SimpleDateFormat timeformat = new SimpleDateFormat ("HH:mm:ss");
	SimpleDateFormat timeformat = new SimpleDateFormat ("yyyyMMddHHmmss");
    	try{
	        client.setDevice(device);
	        client.setDefaultClickDownTime(500);
	    	result="";
	    	client.deviceAction("Wake");
	//launch
	    	client.startStepsGroup("Launch");
		        launch();
	//	        if(result=="e0030"){
	//	        	fail(result);
	//	        }
		        if(!client.getCurrentApplicationName().equals("tw.friday.video")){
		        	result="e0020";
		        	Capture_Screen(result);
		        	record("log_time="+timeformat.format(new Date())+",device="+device+",result="+result+",");
		        	removerecord();
		        	fail(result);
		        }
		        if(!client.isElementFound("NATIVE", "xpath=//*[@id='pager_container']", 0)){
		        	result="e0030";
		        	Capture_Screen(result);
		        	record("log_time="+timeformat.format(new Date())+",device="+device+",result="+result+",");
		        	removerecord();
		        	fail(result);
		        }
	        client.stopStepsGroup();
	        client.startStepsGroup("Login");
		        while(client.isElementFound("NATIVE", "xpath=//*[@id='menu_user_btn']", 0)){
		        	client.click("NATIVE", "xpath=//*[@id='menu_user_btn']", 0, 1);
		        	client.sleep(1000);
		        }
		        client.sleep(1000);
		        if(client.isElementFound("NATIVE", "xpath=//*[@text='登出' and @hidden='false']", 0)){
		        	client.click("NATIVE", "xpath=//*[@id='logoutButton']", 0, 1);
		        	client.sleep(2000);
		        }
		        client.click("NATIVE", "xpath=//*[@id='loginButton']", 0, 1);
		        client.sleep(2000);
		        client.elementSendText("WEB", "xpath=//*[@id='j_username']", 0, "fetqram@gmail.com");
		        client.elementSendText("WEB", "xpath=//*[@id='j_password']", 0, "1qaz2wsx");
	//Login
		        login();
	        client.stopStepsGroup();
	//	        if(result=="e0040"){
	//	        	continue;
	//	        }
	        client.startStepsGroup("PlayVedio");
		        client.sleep(1000);
		        client.click("NATIVE", "xpath=//*[@id='menu_back_btn']", 0, 1);
		        client.click("NATIVE", "xpath=//*[@id='menu_search_btn']", 0, 1);
		        client.elementSendText("NATIVE", "xpath=//*[@id='searchView']", 0, "轉檔音頻測試");
		        client.closeKeyboard();
			client.sleep(1000);
		        client.click("NATIVE", "xpath=//*[@id='keywordView' and @text='轉檔音頻測試']", 0, 1);
		        client.click("NATIVE", "xpath=//*[@id='nameView']", 0, 1);
		        client.clearDeviceLog();
		        client.startLoggingDevice(apppath+"log.log");
		        client.sleep(1000);
	//Select Stream Server        
//		        client.click("NATIVE", "xpath=//*[@id='levelView']", 0, 1);
//		        if(client.swipeWhileNotFound("Down", 220, 2000, "NATIVE", "xpath=//*[@text='"+stream+"' and @id='text1']", 0, 1000, 5, true)){
//		        }
	//Play Video
		        client.startTransaction("vod_play");
		        int fail=0;
		        client.click("NATIVE", "xpath=//*[@id='fullVideoButton']", 0, 1);
		        client.sleep(5000);
			errorcode=playvideo();
		        result="stream_server="+stream+",result="+errorcode;
		        client.endTransaction("vod_play");
	//Applog        
		        client.stopLoggingDevice();
	        client.stopStepsGroup();	        
		      	logtime=date.format(new Date());
		        File logf=new File(apppath+"log.log");
		        logf.renameTo(new File(apppath+device.substring(device.indexOf(":")+1)+logtime+".txt"));
	        client.startStepsGroup("Logout");
		        while(client.waitForElement("NATIVE", "xpath=//*[@id='menu_back_btn']", 0, 10000)){
		             client.click("NATIVE", "xpath=//*[@id='menu_back_btn']", 0, 1);
		        }
		        client.click("NATIVE", "xpath=//*[@id='iconView']", 0, 1);
		        logout();
	        client.stopStepsGroup();
	//	        if(result=="e0041"){
	//	        	fail(result);
	//	        }
        }catch(Exception otherexception){
        	if(result.equalsIgnoreCase("")){
        		Capture_Screen("e0990");
        		result="stream_server="+stream+",result=e0990";
        		fail(result);
        	}
        	
        }finally{
        	if(client.applicationClose("tw.friday.video")){
	            // If statement
	        }
        }
        getrecord(device,picname,result,errorcode);
//        client.sleep(Integer.parseInt(device[1])*60000);
    	
        
    }

    @After
    public void tearDown(){
        client.generateReport(false); 
        client.releaseClient();
    }
    public void launch(){
    	SimpleDateFormat timeformat = new SimpleDateFormat ("HH:mm:ss");
    	int count=1;
    	client.startTransaction("vod_launch");
    	while(count<10 && !client.getCurrentApplicationName().equals("tw.friday.video")){
	        client.launch("tw.friday.video/.EventWebViewActivity", true, true);
	        count+=1;
    	}
        if(client.waitForElement("NATIVE", "xpath=//*[@id='logoView']", 0, 15000)){
            client.endTransaction("vod_launch");
        }else{
        	result="e0030";
        	System.out.println("Launch Failed");
        	Capture_Screen(result);
        	record("log_time="+timeformat.format(new Date())+",result=e0030,");
        	removerecord();
        	fail(result);
        }
    }
    public void login(){
    	SimpleDateFormat timeformat = new SimpleDateFormat ("HH:mm:ss");
    	client.startTransaction("vod_login");
    	client.click("WEB", "xpath=//*[@id='login_btn']", 0, 1);
        
        if(client.waitForElement("NATIVE", "xpath=//*[@text='預約紀錄' and @top='true']", 0, 10000)){
            client.endTransaction("vod_login");
        }else{
        	result="e0040";
        	Capture_Screen("e0040");
        	record("log_time="+timeformat.format(new Date())+",result="+result+",");
        	removerecord();
        	fail(result);
        }
    }
    @SuppressWarnings("deprecation")
	public String playvideo(){
    	String currenttime,videotime,totaltime,result;
    	SimpleDateFormat timeformat = new SimpleDateFormat ("mm:ss");
    	videotime="00:00";
    	totaltime="00:00";
    	currenttime="0";
    	client.sleep(2000);
//    	if(!client.isElementFound("NATIVE", "xpath=//*[@id='progressBar']")){
    	if(!client.waitForElement("NATIVE", "xpath=//*[@id='progressBar']", 0, 5000)){
    		if(client.isElementFound("NATIVE", "xpath=//*[@id='menuTitleView']")){
    			System.out.println("閃退");
    			Capture_Screen("e0050");
    			return "e0050";
    		}else{
    			System.out.println("未知錯誤");
    			Capture_Screen("e0990");
    			return "e0990";
    		}
    	}
    	result=playerror(0);
    	
    	if(result.equals("e0000")){
    		try{
				totaltime= client.elementGetProperty("NATIVE", "xpath=//*[@id='durationView']", 0, "text");
			}catch(Exception e){
				if(client.isElementFound("NATIVE", "xpath=//*[@id='menuTitleView']")){
					System.out.println("中途閃退");
					Capture_Screen("e0051");
	    			return "e0051";
				}
			}
    		while(client.waitForElement("default", "watermark", 0, 15000) && client.waitForElement("NATIVE", "xpath=//*[@id='progressBar' and @hidden='true']", 0, 15000)){
    			try{
    				currenttime = client.elementGetProperty("NATIVE", "xpath=//*[@id='currentView']", 0, "text");
    			}catch(Exception e){
    				break;
    			}

	        	if(currenttime.equals(videotime)){
	        		System.out.println("定格");
	        		Capture_Screen("e0090");
	        		back();
	        		return "e0090";
	        	}else{
	        		videotime=currenttime;
	        	}
    		}
    		if(client.isElementFound("NATIVE", "xpath=//*[@id='menuTitleView']")){
				try {
					Date vt = timeformat.parse(videotime);
					Date tt=timeformat.parse(totaltime);
					if(tt.getMinutes()*60+tt.getSeconds()-vt.getMinutes()*60-vt.getSeconds()>30){
						System.out.println(videotime);
						System.out.println("中途閃退");
						Capture_Screen("e0051");
						return "e0051";
					}else{
						System.out.println("Pass");
						return "e0000";
					}
				} catch (ParseException e) {
					e.printStackTrace();
					Capture_Screen("e0990");
					return "e0990";
				}
    			
    		}else{
    			result=playerror(1);
    		}
    		
    	}else{
    		back();
    		System.out.println(result);
    	}
		return result;
    }
    public String playerror(int first){
    	if(!client.waitForElement("NATIVE", "xpath=//*[@id='progressBar' and @hidden='false']", 0, 60000)){
//No watermark,Loading    		
    		if(!client.waitForElement("default", "watermark", 0, 10000)){
    			System.out.println("無畫面，載入中");
    			Capture_Screen("e008"+first);
	        	return "e008"+first;
//watermark,Loading
    		}else{
    			System.out.println("載入中......");
        		Capture_Screen("e007"+first);
        		return "e007"+first;
    		}
    	}else{
//No watermark,No Load    		
    		if(!client.waitForElement("default", "watermark", 0, 10000)){
    			System.out.println("無畫面，未載入");
            	Capture_Screen("e006"+first);
            	return "e006"+first;
    		}else{
    			return "e0000";
    		}
    	}

//        if(!client.waitForElement("default", "watermark", 0, 15000) && !client.waitForElement("NATIVE", "xpath=//*[@id='progressBar' and @hidden='false']", 0, 15000)){
//        	System.out.println("無畫面，未載入");
//        	Capture_Screen("e006"+first);
//        	return "e006"+first;
//        }else{
//        	
//	        if(!client.waitForElement("default", "watermark", 0, 15000) && client.waitForElement("NATIVE", "xpath=//*[@id='progressBar' and @hidden='false']", 0, 15000)){
//	        	System.out.println("無畫面，載入中");
//	        	Capture_Screen("e008"+first);
//	        	return "e008"+first;
//	        }else{
//
//	        	if(client.waitForElement("default", "watermark", 0, 15000) && client.waitForElement("NATIVE", "xpath=//*[@id='progressBar' and @hidden='false']", 0, 15000)){
//	        		System.out.println("載入中......");
//	        		Capture_Screen("e007"+first);
//	        		return "e007"+first;
//	        	}else{
//	        		return "e0000";
//	        	}
//	        }
//        }
    }    

    public void back(){
    	 while(client.isElementFound("NATIVE", "xpath=//*[@id='back_btn']", 0)){
    		 client.click("NATIVE", "xpath=//*[@id='back_btn']", 0, 1);
    	 }
    }
    public void logout(){
    	SimpleDateFormat timeformat = new SimpleDateFormat ("HH:mm:ss");
    	client.startTransaction("vod_logout");
        client.click("NATIVE", "xpath=//*[@id='logoutButton']", 0, 1);
        
        if(client.waitForElement("NATIVE", "xpath=//*[@text='登出' and @hidden='true']", 0,10000)){
        	client.endTransaction("vod_logout");
        }else{
        	result="e0041";
        	Capture_Screen(result);
        	record("log_time="+timeformat.format(new Date())+",result="+result+",");
        	removerecord();
        	System.out.println("Logout Failed");
        	fail(result);
        }
        
    }
    public void getrecord(String device,String picpath,String result,String errorcode){
      	String log="";
      	String line;
      	BufferedReader br=null;
      	File logfile=new File(csvpath,"transactions_summary.csv");
  		try {
			br = new BufferedReader(new FileReader(logfile));
			br.readLine();
			while ((line = br.readLine()) != null) {
					String[] dataary=line.split("\\,");
					if(log !=""){
						log=log+",";
					}else{
						log="log_time="+dataary[5].subSequence(1, 20)+",device="+device+",";
					}
					log=log+dataary[0]+"="+dataary[2].trim();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}finally{
			if(br != null){
				try {
					br.close();
					logfile.delete();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			
		}
		log=log+",image="+picpath+","+result+",";	
	  	record(log,errorcode);
	  }
	public void record(String log,String errorcode){
      	String filename;
      	SimpleDateFormat date=new SimpleDateFormat("YYYYMMdd");
      	String logtime=date.format(new Date());
      	filename=logtime+".txt";
      	File f=new File(scriptpath,filename);
      	try {
    	    	BufferedWriter bw=new BufferedWriter(new OutputStreamWriter(new FileOutputStream(f,true)));
    			bw.write(log);	
    			bw.newLine();
    			bw.close();
		} catch (IOException e) {
				e.printStackTrace();
		}finally{
			if(!errorcode.equals("e0000")){
				fail(errorcode);
			}
		}
      	
	}
	public void removerecord(){
      	File logfile=new File(csvpath,"transactions_summary.csv");
		logfile.delete();
	}
	public void Capture_Screen(String error){
		client.report(client.capture("Capture"), error, false);
//		logtime=date.format(new Date());
//		psource =new File(client.capture("Capture")).toPath();
//		picname=logtime+error+".jpg";
//		pdest =new File(imagepath+picname).toPath();
//		try {
//			Files.copy(psource,pdest,REPLACE_EXISTING);
//		} catch (IOException e1) {
//			e1.printStackTrace();
//		}
	}
	public static void fail(String message) {
        throw new AssertionFailedError(message);
    }
	
}
