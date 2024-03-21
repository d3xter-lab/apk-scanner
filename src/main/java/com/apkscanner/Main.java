package com.apkscanner;

import java.awt.EventQueue;
import java.io.File;
import java.io.PrintStream;
import java.nio.charset.Charset;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

import com.apkscanner.gui.UIController;
import com.apkscanner.cli.CLIController;
import com.apkscanner.gui.installer.ApkInstallWizard;
import com.apkscanner.resource.RProp;
import com.apkscanner.resource.RStr;
import com.apkspectrum.core.scanner.ApkScanner;
import com.apkspectrum.logback.Log;
import com.apkspectrum.tool.adb.AdbServerMonitor;
import com.apkspectrum.util.FileUtil;
import com.apkspectrum.util.SystemUtil;

public class Main {
    private static final Options allOptions = new Options();
    private static final Options normalOptions = new Options();
    private static final Options targetApkOptions = new Options();
    private static final Options targetPackageOptions = new Options();

    private static final PrintStream systemOut = System.out;

    static public void main(final String[] args) {
        RStr.setLanguage(RProp.S.LANGUAGE.get());
        if ("user".equalsIgnoreCase(RStr.APP_BUILD_MODE.get())) {
            Log.enableConsoleLog(false);
        }

        Log.i(RStr.APP_NAME + " " + RStr.APP_VERSION + " " + RStr.APP_BUILD_MODE);
        Log.i("OS : " + SystemUtil.OS);
        Log.i("java.version : " + System.getProperty("java.version"));
        Log.i("java.specification.version : " + System.getProperty("java.specification.version"));
        Log.i("file.encoding : " + System.getProperty("file.encoding"));
        Log.i("java.home : " + System.getProperty("java.home"));
        Log.i("java.library.path : " + System.getProperty("java.library.path"));
        Log.i("Default Charset : " + Charset.defaultCharset());
        Log.i("sun.arch.data.model : " + System.getProperty("sun.arch.data.model"));

        createOpstions();

        CommandLine cmd = null;
        try {
            String cmdType = null;
            if (args.length > 0) {
                if (!"p".equals(args[0]) && !"package".equals(args[0]) && "i".equals(args[0])
                        && "install".equals(args[0]) && "d".equals(args[0])
                        && "delete-temp-path".equals(args[0]) && !args[0].startsWith("-")
                        && !args[0].endsWith(".apk") && !args[0].endsWith(".ppk")
                        && !args[0].endsWith(".apex")) {
                    throw new ParseException("Missing argument for option: " + args[0]);
                }
                if ("p".equals(args[0]) || "package".equals(args[0])) {
                    cmdType = "package";
                } else if ("i".equals(args[0]) || "install".equals(args[0])) {
                    cmdType = "install";
                } else if ("d".equals(args[0]) || "delete-temp-path".equals(args[0])) {
                    cmdType = "delete-temp-path";
                }
            }

            CommandLineParser parser = new DefaultParser();
            cmd = parser.parse(allOptions, args);

            if (cmdType == null && !cmd.hasOption("v") && !cmd.hasOption("version")
                    && !cmd.hasOption("h") && !cmd.hasOption("help")) {
                cmdType = "file";
            }

            if ("package".equals(cmdType)) {
                if (cmd.getArgs().length > 2 || cmd.getArgs().length == 0) {
                    throw new ParseException("Must be just one that the package");
                }
            } else if ("file".equals(cmdType)) {
                if (cmd.getArgs().length > 2 /* || cmd.getArgs().length == 0 */) {
                    throw new ParseException("Must be just one that the Apk file path");
                }

                if (cmd.getArgs().length == 0) {
                    cmdType = null;
                }

                // if(!cmd.getArgs()[0].endsWith(".apk"))
                // throw new ParseException("Unknown type : " + cmd.getArgs()[0]);
            }

            if ("file".equals(cmdType)) {
                solveApkFile(cmd);
            } else if ("package".equals(cmdType)) {
                solvePackage(cmd);
            } else if ("install".equals(cmdType)) {
                install(cmd);
            } else if ("delete-temp-path".equals(cmdType)) {
                deleteTempPath(cmd);
            } else {
                emptyCmd(cmd);
            }
        } catch (ParseException e) {
            String argsList = "";
            for (String s : args) {
                argsList += " " + s;
            }
            // Log.enableConsoleLog(false);
            Log.e(e.toString());
            Log.e("args[" + args.length + "] :" + argsList);
            usage();
            emptyCmd(cmd);
            System.exit(1);
        }
    }

    static private void emptyCmd(CommandLine cmd) {
        Log.v("emptyCmd() ");

        if (!cmd.hasOption("c") && !cmd.hasOption("cli")) {
            EventQueue.invokeLater(UIController.getInstance());
        } else {
            usage();
        }
    }

    static private void solveApkFile(CommandLine cmd) {
        final String apkFilePath = cmd.getArgs()[1];

        Log.v("solveApkFile() " + apkFilePath);

        ApkScanner apkScanner = ApkScanner.getInstance();
        if (!cmd.hasOption("c") && !cmd.hasOption("cli")) {
            EventQueue.invokeLater(UIController.getInstance(apkScanner));
            waitAdbServer();
        } else {
            apkScanner.openApk(apkFilePath);
            CLIController cli = CLIController.getInstance(apkScanner);
        }
    }

    static private void solvePackage(CommandLine cmd) {
        final String apkPath = cmd.getArgs()[1];
        final String fwResPath = cmd.getOptionValue("famework", null);
        final String devSerialNum = cmd.getOptionValue("device", null);

        Log.v("solvePackage() " + apkPath + ", " + fwResPath + ", " + devSerialNum);

        ApkScanner apkScanner = ApkScanner.getInstance();
        if (!cmd.hasOption("c") && !cmd.hasOption("cli")) {
            EventQueue.invokeLater(UIController.getInstance(apkScanner));
        } else {

        }
        apkScanner.openPackage(devSerialNum, apkPath, fwResPath);
    }

    static private void install(CommandLine cmd) {
        final String apkFilePath = cmd.getArgs()[1];

        if (apkFilePath == null || apkFilePath.isEmpty() || !new File(apkFilePath).exists()) {
            Log.e("apk is null");
            return;
        }
        Log.v("install() " + apkFilePath);

        if (!cmd.hasOption("c") && !cmd.hasOption("cli")) {
            EventQueue.invokeLater(new Runnable() {
                @Override
                public void run() {
                    String path = new File(apkFilePath).getAbsolutePath();
                    ApkInstallWizard wizard = new ApkInstallWizard(path);
                    wizard.start();
                }
            });
        } else {

        }
    }

    static private void deleteTempPath(CommandLine cmd) {
        String path = cmd.getArgs()[1];

        while (path != null && !path.isEmpty() && path.startsWith(FileUtil.getTempPath())
                && new File(path).exists()) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
            }

            File parent = new File(path).getParentFile();
            Log.i("delete temp APK folder : " + parent.getPath());
            while (parent != null && parent.exists() && parent.getParentFile() != null
                    && parent.getParentFile().listFiles().length == 1 && parent.getParentFile()
                            .getAbsolutePath().length() > FileUtil.getTempPath().length()) {
                parent = parent.getParentFile();
            }
            FileUtil.deleteDirectory(parent);
        }
    }

    static private void waitAdbServer() {
        if (SystemUtil.isWindows() && RProp.B.ADB_DEVICE_MONITORING.get()) {
            // AdbServerMonitor.startServerAndCreateBridge();
            UIController ui = UIController.getInstance();
            ui.showAdbServerLodingDlg(true);
            while (AdbServerMonitor.getAndroidDebugBridge(1000) == null) {
                Log.v("wait for adb server");
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                }
            }
            ui.showAdbServerLodingDlg(false);
        }
    }

    static private void createOpstions() {
        Option opt = new Option("v", "version", false, "Prints the version then exits");
        allOptions.addOption(opt);
        normalOptions.addOption(opt);

        opt = new Option("h", "help", false, "Prints this help");
        allOptions.addOption(opt);
        normalOptions.addOption(opt);


        opt = new Option( "c", "cli", false, "Prints the result to the command line");
        allOptions.addOption(opt);
        targetApkOptions.addOption(opt);
        targetPackageOptions.addOption(opt);

        // opt = new Option( "g", "gui", false, "Show result by GUI [default]");
        // allOptions.addOption(opt);
        // targetApkOptions.addOption(opt);
        // targetPackageOptions.addOption(opt);

        opt = new Option("i", "install", true, "install APK");
        allOptions.addOption(opt);

        opt = new Option("d", "device", true, "The serial number of device");
        allOptions.addOption(opt);
        targetPackageOptions.addOption(opt);

        opt = new Option("f", "framework", true, "Uses framework files located in <arg>");
        allOptions.addOption(opt);
        targetApkOptions.addOption(opt);
        targetPackageOptions.addOption(opt);
    }

    static private void usage() {
        systemOut.println(RStr.APP_NAME + " " + RStr.APP_VERSION);
        // systemOut.println("with apktool " + ApktoolWrapper.getApkToolVersion() + "
        // (http://ibotpeaches.github.io/Apktool/)");
        // systemOut.println(" - Apache License 2.0 (http://www.apache.org/licenses/LICENSE-2.0)");
        systemOut.println(
                "with Android debug bridge (http://developer.android.com/tools/help/adb.html)");
        systemOut.println(
                "Programmed by " + RStr.APP_MAKER + " <" + RStr.APP_MAKER_EMAIL + ">" + ", 2015");
        systemOut.println("Apache License 2.0 (http://www.apache.org/licenses/LICENSE-2.0)");
        systemOut.println();

        HelpFormatter formatter = new HelpFormatter();
        formatter.printHelp("apkscanner ", normalOptions);
        formatter.printHelp("apkscanner [options] <app_path>", targetApkOptions);
        formatter.printHelp(
                "apkscanner p[ackage] [options] [-d[evice] <serial_number>] [-f[ramework] <framework.apk>] <package>",
                targetPackageOptions);
    }
}
