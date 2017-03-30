package com.apkscanner;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;

import com.apkscanner.resource.Resource;
import com.apkscanner.util.SystemUtil;

public class Launcher
{
	static private ArrayList<String> defaultCmd;
	
	static public boolean run()
	{
		return SystemUtil.exec(getDefaultCmd());
	}
	
	static public boolean run(String apkFilePath)
	{
		if(apkFilePath == null)
			return false;

		ArrayList<String> cmd = new ArrayList<String>(getDefaultCmd());
		cmd.add(apkFilePath);

		return SystemUtil.exec(cmd);
	}
	
	static public boolean run(String devSerialNum, String devApkFilePath, String frameworkRes)
	{
		if(devApkFilePath == null)
			return false;

		ArrayList<String> cmd = new ArrayList<String>(getDefaultCmd());
		cmd.add("package");
		
		if(devSerialNum != null && !devSerialNum.isEmpty()) {
			cmd.add("-d");
			cmd.add(devSerialNum);
		}
		
		if(frameworkRes != null && !frameworkRes.isEmpty()) {
			cmd.add("-f");
			cmd.add(frameworkRes);
		}

		cmd.add(devApkFilePath);

		return SystemUtil.exec(cmd);
	}
	
	static public boolean install(String apkFilePath)
	{
		if(apkFilePath == null)
			return false;

		ArrayList<String> cmd = new ArrayList<String>(getDefaultCmd());
		cmd.add("install");
		cmd.add(apkFilePath);

		return SystemUtil.exec(cmd);
	}
	
	static public boolean deleteTempPath(String path)
	{
		if(path == null)
			return false;

		ArrayList<String> cmd = new ArrayList<String>(getDefaultCmd());
		cmd.add("delete-temp-path");
		cmd.add(path);

		return SystemUtil.exec(cmd);		
	}
	
	static private ArrayList<String> getDefaultCmd()
	{
		if(defaultCmd != null)
			return defaultCmd;

		defaultCmd = new ArrayList<String>();
		
		StringBuilder classPathBuilder = new StringBuilder(); 
		classPathBuilder.append(Main.class.getProtectionDomain().getCodeSource().getLocation().getPath());
		//classPathBuilder.append(File.pathSeparator);
		//classPathBuilder.append(Resource.LIB_JSON_JAR.getPath());
		//classPathBuilder.append(File.pathSeparator);
		//classPathBuilder.append(Resource.LIB_CLI_JAR.getPath());
		classPathBuilder.append(File.pathSeparator);
		classPathBuilder.append(Resource.LIB_ALL.getPath());

		String classPath = null;
		try {
			classPath = URLDecoder.decode(classPathBuilder.toString(), "UTF-8");
		} catch (UnsupportedEncodingException e1) { }

		defaultCmd.add("java");
		defaultCmd.add("-Dfile.encoding=utf-8");
		defaultCmd.add("-Djava.library.path="+Resource.BIN_PATH.getPath());
		defaultCmd.add("-cp");
		defaultCmd.add(classPath);
		defaultCmd.add(Main.class.getName());

		return defaultCmd;
	}
}