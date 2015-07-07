package com.ApkInfo.Core;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import com.ApkInfo.Core.CoreApkTool.FSStyle;
import com.ApkInfo.Resource.Resource;

public class ApkManager
{
	private ApkInfo mApkInfo = null;

	//private String mApkPath = null;
	//private String mWorkTempPath = null;
	//private String mWorkspacePath = null;
 	
	//private Status mState = Status.UNINITIALIZE;
	
	private ProcessThead mProcess = null;
	
	private StatusListener mStatusListener = null;

	public class ApkInfo
	{
		public String Labelname = null;
		public String PackageName = null;
		public String VersionName = null;
		public String VersionCode = null;
		public String MinSDKversion = null;
		public String TargerSDKversion = null;
		public String Signing = null;
		public String Hidden = null;
		public String IconPath = null;
		public String Permissions = null;
		public String Startup = null;
		public String ProtectionLevel = null;
		public String SharedUserId = null;
		public String ApkSize = null;
		
		public ArrayList<Object[]> WidgetList = new ArrayList<Object[]>();
		public ArrayList<String> ImageList = new ArrayList<String>();
		public ArrayList<String> LibList = new ArrayList<String>();

		public ArrayList<Object[]> ActivityList = new ArrayList<Object[]>();
		public ArrayList<String> CertList = new ArrayList<String>();
		
		public ArrayList<String> PermissionList = new ArrayList<String>();

		public String ApkPath = null;
		public String WorkTempPath = null;
		
		public void verify() {
			if(Labelname == null) Labelname = "";
			if(PackageName == null) PackageName = "";
			if(VersionName == null) VersionName = "";
			if(VersionCode == null) VersionCode = "";
			if(MinSDKversion == null) MinSDKversion = "";
			if(TargerSDKversion == null) TargerSDKversion = "";
			if(Signing == null) Signing = "";
			if(Hidden == null) Hidden = "";
			if(IconPath == null) IconPath = "";
			if(Permissions == null) Permissions = "";
			if(Startup == null) Startup = "";
			if(ProtectionLevel == null) ProtectionLevel = "";
			if(SharedUserId == null) SharedUserId = "";
			if(ApkSize == null) ApkSize = "";
			
			for(int i = 0; i < WidgetList.size(); i++){
				Object[] info = (Object[])WidgetList.get(i);
				info[0] = info[0] != null && !((String)info[0]).isEmpty() ? info[0] : IconPath;
				info[1] = info[1] != null && !((String)info[1]).isEmpty() ? info[1] : Labelname;
				info[2] = info[2] != null && !((String)info[2]).isEmpty() ? info[2] : "1 X 1";
				info[3] = info[3] != null && !((String)info[3]).isEmpty() ? info[3] : PackageName;
				info[4] = info[4] != null && !((String)info[4]).isEmpty() ? info[4] : "Unknown";
				
				if(((String)info[3]).matches("^\\..*")) {
					info[3] = PackageName + (String)info[3];
	        	}
			}

			for(int i = 0; i < ActivityList.size(); i++){
				Object[] info = (Object[])ActivityList.get(i);
				
				info[0] = info[0] != null && !((String)info[0]).isEmpty() ? info[0] : PackageName;
				info[1] = info[1] != null && !((String)info[1]).isEmpty() ? info[1] : "Unknown";
				info[2] = info[2] != null && !((String)info[2]).isEmpty() ? info[2] : "X";
				info[3] = info[3] != null && !((String)info[3]).isEmpty() ? info[3] : "";

				if(((String)info[0]).matches("^\\..*")) {
					info[0] = PackageName + (String)info[0];
	        	}
			}
		}
	}
	
	public interface StatusListener
	{
		public void OnStart(ProcessCmd cmd);
		public void OnComplete(ProcessCmd cmd);
		public void OnProgress(int step, String msg);
		public void OnStateChange();
	}

	public enum Status {
		UNINITIALIZE,
		INITIALIZING,
		INITIALIZEED,
		SOLVE_RESOURCE,
		SOLVE_CODE,
		SOLVE_BOTH,
		STANDBY,
		DELETEING
	}
	
	public enum ProcessCmd {
		SOLVE_RESOURCE,
		SOLVE_CODE,
		SOLVE_BOTH,
		DELETE_TEMP_PATH,
	}
	
	public enum SolveType {
		RESOURCE,
		CODE,
		BOTH
	}
	
	public ApkManager()
	{
		this(null, null);
	}
	
	public ApkManager(StatusListener listener)
	{
		this(null, listener);
	}
	
	public ApkManager(String apkPath)
	{
		this(apkPath, null);
	}

	public ApkManager(String apkPath, StatusListener listener)
	{
		mApkInfo = new ApkInfo();
		setApkFile(apkPath);
		setListener(listener);
	}
	
	public void setApkFile(String apkPath) {
		File apkFile = new File(apkPath);

		if(!apkFile.exists()) {
			System.out.println("No Such APK file");
			return;
		}
		apkPath = apkFile.getAbsolutePath();
		
		if(apkPath != null && (new File(apkPath)).exists()) {
			mApkInfo.ApkPath = apkPath;
		}
	}
	
	public void setListener(StatusListener listener) {
		mStatusListener = listener;
	}
	
	public void solve(SolveType type)
	{
		if(mApkInfo.ApkPath == null) return;
		System.out.println("solve()....start ");
		synchronized(this) {
			mProcess = new ProcessThead(this, ProcessCmd.SOLVE_RESOURCE);
			mProcess.start();

			try {
				this.wait();
				this.notify();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		System.out.println("solve()....end ");
	}

	public void clear(boolean wait)
	{
		System.out.println("clear()....start ");
		mProcess = new ProcessThead(this, ProcessCmd.DELETE_TEMP_PATH);
		synchronized(this) {
			mProcess.start();
			
			try {
				this.wait();
				this.notify();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		try {
			if(wait) mProcess.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		System.out.println("clear()....end ");
	}
	
	public final ApkInfo getApkInfo()
	{
		return mApkInfo;
	}

	class ProcessThead extends Thread {
		Object mOwner;
		ProcessCmd mCmd;
		
		ProcessThead(Object owner, ProcessCmd cmd) {
			mOwner = owner;
			mCmd = cmd;
		}
		
		private void solve()
		{
			mApkInfo.WorkTempPath = CoreApkTool.makeTempPath(mApkInfo.ApkPath);
			System.out.println("Temp path : " + mApkInfo.WorkTempPath);

			mApkInfo.ApkSize = CoreApkTool.getFileSize((new File(mApkInfo.ApkPath)), FSStyle.FULL);
			
			solveAPK(mApkInfo.ApkPath, mApkInfo.WorkTempPath);
			XmlToMyApkinfo();
			
			mApkInfo.ImageList = CoreApkTool.findFiles(new File(mApkInfo.WorkTempPath + File.separator + "res"), ".png", ".*drawable.*");
			mApkInfo.LibList = CoreApkTool.findFiles(new File(mApkInfo.WorkTempPath + File.separator + "lib"), ".so", null);

			mApkInfo.CertList = CoreCertTool.solveCert(mApkInfo.WorkTempPath + File.separator + "original" + File.separator + "META-INF" + File.separator);
		}
		
		private void solveAPK(String APKFilePath, String solvePath)
		{
			String apkToolPath = Resource.BIN_APKTOOL_JAR.getPath();
			System.out.println("apkToolPath : " + apkToolPath);

			if(!(new File(apkToolPath)).exists()) {
				System.out.println("No such file : apktool.jar");
				return;
			}

			String[] cmd = {"java", "-jar", apkToolPath, "d", "-s", "-f", "-o", solvePath, "-p", solvePath, APKFilePath};

			MyConsolCmd.exc(cmd, true, new MyConsolCmd.OutputObserver() {
				@Override
				public boolean ConsolOutput(String output) {
			    	if(output.matches("^I:.*"))
			    		progress(5,output + "\n");
			    	else
			    		progress(0,output + "\n");
			    	return true;
				}
			});
		}

		private void progress(int step, String msg)
		{
			if(mStatusListener != null) mStatusListener.OnProgress(step, msg);
		}
		
		private void XmlToMyApkinfo()
		{
			progress(5,"Check Yml....\n");
			YmlToMyApkinfo();

			progress(5,"parsing AndroidManifest....\n");
			MyXPath xmlAndroidManifest = new MyXPath(mApkInfo.WorkTempPath + File.separator + "AndroidManifest.xml");

			// package
			xmlAndroidManifest.getNode("/manifest");
			mApkInfo.PackageName = xmlAndroidManifest.getAttributes("package");
			mApkInfo.SharedUserId = xmlAndroidManifest.getAttributes("android:sharedUserId");
			
			if(mApkInfo.VersionCode.isEmpty()) {
				mApkInfo.VersionCode = xmlAndroidManifest.getAttributes("android:versionCode");
			}
			
			if(mApkInfo.VersionName.isEmpty()) {
				mApkInfo.VersionName = xmlAndroidManifest.getAttributes("android:versionName");
			}

			// label & icon
			xmlAndroidManifest.getNode("/manifest/application");
			mApkInfo.Labelname = getResourceInfo(xmlAndroidManifest.getAttributes("android:label"));
			mApkInfo.IconPath = getResourceInfo(xmlAndroidManifest.getAttributes("android:icon"));

	        
	        // hidden
	        if(xmlAndroidManifest.isNode("//category[@name='android.intent.category.LAUNCHER']")) {
	        	mApkInfo.Hidden = "LAUNCHER";
	        } else {
	        	mApkInfo.Hidden = "HIDDEN";
	        }
	        
	        // startup
	        if(xmlAndroidManifest.isNode("//uses-permission[@name='android.permission.RECEIVE_BOOT_COMPLETED']")) {
	        	mApkInfo.Startup = "START_UP";
	        } else {
	        	mApkInfo.Startup = "";
	        }

	        // permission
	        mApkInfo.ProtectionLevel = "";
	        progress(5,"parsing permission...\n");
	        xmlAndroidManifest.getNodeList("//uses-permission");
	        for( int idx=0; idx < xmlAndroidManifest.getLength(); idx++ ){
	        	if(idx==0) mApkInfo.Permissions = "<uses-permission> [" + xmlAndroidManifest.getLength() + "]";
	        	mApkInfo.Permissions += "\n" + xmlAndroidManifest.getAttributes(idx, "android:name");
	        	String sig = xmlAndroidManifest.getAttributes(idx, "android:protectionLevel");
	        	if(sig != null && sig.equals("signature")) {
	        		mApkInfo.Permissions += " - <SIGNATURE>";
	        		mApkInfo.ProtectionLevel = "SIGNATURE";
	        	}
	        }
	        xmlAndroidManifest.getNodeList("//permission");
	        for( int idx=0; idx < xmlAndroidManifest.getLength(); idx++ ){
	        	if(idx==0) mApkInfo.Permissions += "\n\n<permission> [" + xmlAndroidManifest.getLength() + "]";
	        	mApkInfo.Permissions += "\n" + xmlAndroidManifest.getAttributes(idx, "android:name");
	        	String sig = xmlAndroidManifest.getAttributes(idx, "android:protectionLevel");
	        	if(sig != null && sig.equals("signature")) {
	        		mApkInfo.Permissions += " - <SIGNATURE>";
	        		mApkInfo.ProtectionLevel = "SIGNATURE";
	        	}
	        }

	        // widget
	        progress(5,"parsing widget...\n");
	        xmlAndroidManifest.getNodeList("//meta-data[@name='android.appwidget.provider']");
	        //System.out.println("Normal widgetList cnt = " + xmlAndroidManifest.getLength());
	        for( int idx=0; idx < xmlAndroidManifest.getLength(); idx++ ){
	        	Object[] widgetExtraInfo = {mApkInfo.IconPath, ""};
	        	
	        	MyXPath parent = xmlAndroidManifest.getParentNode(idx);
	        	String widgetTitle = getResourceInfo(parent.getAttributes("android:label"));
	        	String widgetActivity = getResourceInfo(parent.getAttributes("android:name"));
	   			Object[] extraInfo = getWidgetInfo(xmlAndroidManifest.getAttributes(idx, "android:resource"));
	        	if(extraInfo != null) {
	        		widgetExtraInfo = extraInfo;
	        	}
	        	
	        	mApkInfo.WidgetList.add(new Object[] {widgetExtraInfo[0], widgetTitle, widgetExtraInfo[1], widgetActivity, "Normal"});
	        }
	        
	        xmlAndroidManifest.getNodeList("//action[@name='android.intent.action.CREATE_SHORTCUT']");
	        //System.out.println("Shortcut widgetList cnt = " + xmlAndroidManifest.getLength());
	        for( int idx=0; idx < xmlAndroidManifest.getLength(); idx++ ){
	        	MyXPath parent = xmlAndroidManifest.getParentNode(idx).getParentNode();
	        	String widgetTitle = getResourceInfo(parent.getAttributes("android:label"));
	        	String widgetActivity = getResourceInfo(parent.getAttributes("android:name"));

	        	mApkInfo.WidgetList.add(new Object[] {mApkInfo.IconPath, widgetTitle, "1 X 1", widgetActivity, "Shortcut"});
	        }
	        
	        // Activity/Service/Receiver/provider intent-filter
	        getActivityInfo(xmlAndroidManifest, "activity");
	        getActivityInfo(xmlAndroidManifest, "service");
	        getActivityInfo(xmlAndroidManifest, "receiver");
	        getActivityInfo(xmlAndroidManifest, "provider");
	        
	        mApkInfo.verify();
		}
		
		private String getResourceInfo(String id) {

			if(id == null) return null;

			String result = null;
			String resXmlPath = new String(mApkInfo.WorkTempPath + File.separator + "res" + File.separator);
			String query = "//";
			String filter = "";
			String fileName = "";
			String type = "string";
			long maxImgSize = 0;
			
			if(!id.matches("^@.*")) {
				//System.out.println("id is start without @");
				result = new String(id);
				return result;
			} else if(id.matches("^@drawable/.*")) {
				//System.out.println("@drawable");

				filter = "^drawable.*";
				fileName = new String(id.substring(10)) + ".png";
				type = "image";
			} else if(id.matches("^@string/.*")) {
				//System.out.println("@stirng");
				
				filter = "^values.*";
				query = "//resources/string[@name='"+id.substring(id.indexOf("/")+1)+"']";
				fileName = "strings.xml";
			} else if(id.matches("^@dimen/.*")) {
				//System.out.println("string@dimen");

				filter = "^values.*";
				query = "//resources/dimen[@name='"+id.substring(id.indexOf("/")+1)+"']";
				fileName = "dimens.xml";
			} else {
				System.out.println("getResourceInfo() Unknown id " + id);
				return new String(id);
			}
			
			for (String s : (new File(resXmlPath)).list()) {
				if(!s.matches(filter)) continue;

				File resFile = new File(resXmlPath + s + File.separator + fileName);
				if(!resFile.exists()) continue;

		        //System.out.println(" - " + resFile.getAbsolutePath() + ", " + type);
				if(type.equals("image")) {
					if(resFile.length() > maxImgSize) {
						//System.out.println(resFile.getPath() + ", " + maxImgSize);
						result = new String(resFile.getPath());
						maxImgSize = resFile.length();
					}
				} else {
			        result = new MyXPath(resFile.getAbsolutePath()).getNode(query).getTextContent();
			        if(result != null) break;;
				}
			}
	        //System.out.println(">> " + result);
			return result;
		}
		
		private void getActivityInfo(MyXPath xmlAndroidManifest, String tag) {
	        xmlAndroidManifest.getNodeList("//"+tag);
	        for( int idx=0; idx < xmlAndroidManifest.getLength(); idx++ ){
	        	String name = xmlAndroidManifest.getAttributes(idx, "android:name");
	        	String startup = "X";
	        	String intents = "";

	        	MyXPath intentsNode = new MyXPath(xmlAndroidManifest);
	        	intentsNode.getNodeList("//"+tag+"[@name='" + name + "']/intent-filter/action");
	        	for( int i=0; i < intentsNode.getLength(); i++ ){
	        		String act = intentsNode.getAttributes(i, "android:name");
	        		if(i==0) intents += "<intent-filter> [" + intentsNode.getLength() + "]";
	        		intents += "\n" + act;
	        		if(act.equals("android.intent.action.BOOT_COMPLETED"))
	        			startup = "O";
	        	}
	        	
	        	if(intentsNode.isNode("//"+tag+"[@name='" + name + "']/intent-filter/category[@name='android.intent.category.LAUNCHER']")) {
	        		name += " - LAUNCHER";
	        	}
	        	
	        	mApkInfo.ActivityList.add(new Object[] { name, tag, startup, intents });
	        }
		}
		
		private Object[] getWidgetInfo(String resource) {

			//System.out.println("getWidgetInfo() " + resource);
			String resXmlPath = new String(mApkInfo.WorkTempPath + File.separator + "res" + File.separator);

			String Size = "";
			String IconPath = "";
			String ReSizeMode = "";
			
			if(resource == null || !resource.matches("^@xml/.*")) {
				return new Object[] { IconPath, Size };
			}

			String widgetXml = new String(resource.substring(5));
			//System.out.println("widgetXml : " + widgetXml);

			for (String s : (new File(resXmlPath)).list()) {
				if(!s.matches("^xml.*")) continue;

				File xmlFile = new File(resXmlPath + s + File.separator + widgetXml + ".xml");
				if(!xmlFile.exists()) continue;
				
				//System.out.println("xmlFile " + xmlFile.getAbsolutePath());

				MyXPath xpath = new MyXPath(xmlFile.getAbsolutePath());
				
				xpath.getNode("//appwidget-provider");
		        
				if(Size.isEmpty() && xpath.getAttributes("android:minWidth") != null
						&& xpath.getAttributes("android:minHeight") != null) {
					String width = xpath.getAttributes("android:minWidth");
					String Height = xpath.getAttributes("android:minHeight");
					width = getResourceInfo(width).replaceAll("^([0-9]*).*", "$1");
					Height = getResourceInfo(Height).replaceAll("^([0-9]*).*", "$1");
					//Size = ((Integer.parseInt(width) - 40) / 70 + 1) + " X " + ((Integer.parseInt(Height) - 40) / 70 + 1);
					Size = (int)Math.ceil((Float.parseFloat(width) - 40) / 76 + 1) + " X " + (int)Math.ceil((Float.parseFloat(Height) - 40) / 76 + 1);
					Size += "\n(" + width + " X " + Height + ")";
			    	//System.out.println("Size " + Size + ", width " + width + ", height " + Height);
				}
				
				if(ReSizeMode.isEmpty() && xpath.getAttributes("android:resizeMode") != null) {
					ReSizeMode = xpath.getAttributes("android:resizeMode");
				}

				if(IconPath.isEmpty() && xpath.getAttributes("android:previewImage") != null) {
					String icon = xpath.getAttributes("android:previewImage");
					IconPath = getResourceInfo(icon);
					//System.out.println("icon " + IconPath);
				}
			}
			
			if(!ReSizeMode.isEmpty()) {
				Size += "\n\n[ReSizeMode]\n" + ReSizeMode.replaceAll("\\|", "\n");
			}
	    	
			return new Object[] { IconPath, Size };
		}
		
		private void YmlToMyApkinfo() {
			String ymlPath = new String(mApkInfo.WorkTempPath + File.separator + "apktool.yml");
			File ymlFile = new File(ymlPath);
		    BufferedReader inFile = null;

			if(!ymlFile.exists()) {
				return;
			}
			
			try {
			    String sLine = null;
				inFile = new BufferedReader(new FileReader(ymlFile));
				while( (sLine = inFile.readLine()) != null ) {
					if(sLine.matches("^\\s*versionCode:.*")) {
						mApkInfo.VersionCode = sLine.replaceFirst("\\s*versionCode:\\s*['\"]?([^'\"]+)['\"]?\\s*$", "$1");
						mApkInfo.VersionCode = getResourceInfo(mApkInfo.VersionCode);
					} else if(sLine.matches("^\\s*versionName:.*")) {
						mApkInfo.VersionName = sLine.replaceFirst("\\s*versionName:\\s*['\"]?([^'\"]+)['\"]?\\s*$", "$1");
						mApkInfo.VersionName = getResourceInfo(mApkInfo.VersionName);
					} else if(sLine.matches("^\\s*minSdkVersion:.*")) {
						mApkInfo.MinSDKversion = sLine.replaceFirst("\\s*minSdkVersion:\\s*['\"]?([^'\"]+)['\"]?\\s*$", "$1");
					} else if(sLine.matches("^\\s*targetSdkVersion:.*")) {
						mApkInfo.TargerSDKversion = sLine.replaceFirst("\\s*targetSdkVersion:\\s*['\"]?([^'\"]+)['\"]?\\s*$", "$1");
					}
				}
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				try {
					if(inFile != null) inFile.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

		private void deleteTempPath()
		{
			System.out.println("delete Folder : "  + mApkInfo.WorkTempPath);
			if(mApkInfo.WorkTempPath != null && !mApkInfo.WorkTempPath.isEmpty())
				CoreApkTool.deleteDirectory(new File(mApkInfo.WorkTempPath));

			mApkInfo = null;
		}

		public void run()
		{
			System.out.println("ProcessThead run()~~~");
			synchronized(mOwner) {
				mOwner.notify();
				try {
					mOwner.wait();
				} catch (InterruptedException e1) {
					e1.printStackTrace();
				}
				
				if(mStatusListener != null) mStatusListener.OnStart(mCmd);
				switch(mCmd) {
				case SOLVE_RESOURCE:
				case SOLVE_CODE:
				case SOLVE_BOTH:
					solve();
					break;
				case DELETE_TEMP_PATH:
					deleteTempPath();
					break;
				}
				if(mStatusListener != null) mStatusListener.OnComplete(mCmd);
			}
		}
	}
}
