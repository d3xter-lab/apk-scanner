package com.apkscanner.cli;

import java.io.FileWriter;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.json.simple.JSONObject;

import com.apkscanner.resource.RImg;
import com.apkscanner.resource.RProp;
import com.apkspectrum.core.scanner.ApkScanner;
import com.apkspectrum.data.apkinfo.ApkInfo;
import com.apkspectrum.data.apkinfo.ApkInfoHelper;
import com.apkspectrum.data.apkinfo.PermissionInfo;
import com.apkspectrum.data.apkinfo.ResourceInfo;
import com.apkspectrum.data.apkinfo.UsesPermissionInfo;
import com.apkspectrum.data.apkinfo.UsesPermissionSdk23Info;
import com.apkspectrum.logback.Log;

public class CLIController {
    private static CLIController instance;
    private ApkScanner apkScanner;
    private ApkInfo apkInfo;

    private CLIController(ApkScanner apkScanner) {
        if (apkScanner == null) {
            apkScanner = ApkScanner.getInstance();
        }
        this.apkScanner = apkScanner;
        this.apkInfo = this.apkScanner.getApkInfo();

        apkScanner.setStatusListener(new ApkScannerListener(), true);
    }

    public static CLIController getInstance(ApkScanner apkScanner) {
        if (instance == null) {
            instance = new CLIController(apkScanner);
        }
        return instance;
    }

    private String getAppLabel(ResourceInfo[] labels, String packageName) {
        String appName = null;
        StringBuilder labelBuilder = new StringBuilder();
        if (labels != null && labels.length > 0) {
            appName = ApkInfoHelper.getResourceValue(labels, RProp.S.PREFERRED_LANGUAGE.get());
            if (appName != null && appName.isEmpty()) appName = null;

            for (ResourceInfo r : labels) {
                if (r.configuration == null || r.configuration.isEmpty()
                        || "default".equals(r.configuration)) {
                    labelBuilder.append(r.name != null ? r.name : packageName).append('/');
                    if (appName == null && r.name != null) appName = r.name;
                } else {
                    labelBuilder.append("[").append(r.configuration).append("] ").append(r.name).append('/');
                }
            }
        }

        if (labelBuilder.length() > 0) {
            String mutiLabels = labelBuilder.toString(); // not used
        }

        if (appName == null) appName = packageName;

        return appName;
    }

    private String getAppIcon(ResourceInfo[] icons) {
        String iconPath = null;
        if (icons != null && icons.length > 0) {
            ResourceInfo[] iconList = icons;
            for (int i = iconList.length - 1; i >= 0; i--) {
                if (iconList[i] == null || iconList[i].name == null || iconList[i].name.endsWith(".xml"))
                    continue;
                iconPath = iconList[i].name;
                if (iconPath != null) break;
            }
        }
        if (iconPath == null) {
            iconPath = RImg.DEF_APP_ICON.getPath();
        } else if (iconPath.toLowerCase().endsWith(".qmg")) {
            iconPath = RImg.QMG_IMAGE_ICON.getPath();
        }

        String pattern = "apk!/(.+)$";
        Pattern p = Pattern.compile(pattern);
        Matcher m = p.matcher(iconPath);
        
        if (m.find()) {
            String extracted = m.group(1);
            iconPath = extracted;
        }
        return iconPath;
    }

    private String makeCertSummary(String[] certificates) {
        if (certificates == null || certificates.length == 0) return null;
        StringBuilder summary = new StringBuilder();
        for (String sign : certificates) {
            summary.append(sign);
        }
        return summary.toString();
    }

    @SuppressWarnings("unchecked")
    public void saveData() {
        String packageName = apkInfo.manifest.packageName;
        String appName = this.getAppLabel(apkInfo.manifest.application.labels, packageName);
        String versionName = apkInfo.manifest.versionName;
        String versionCode = apkInfo.manifest.versionCode.toString();
        String minSdkVersion = apkInfo.manifest.usesSdk.minSdkVersion.toString();
        String targetSdkVersion = apkInfo.manifest.usesSdk.targetSdkVersion.toString();
        String signatureScheme = apkInfo.signatureScheme;
        String certificate = this.makeCertSummary(apkInfo.certificates);
        String iconPath = this.getAppIcon(apkInfo.manifest.application.icons);
        StringBuilder permissionLists = new StringBuilder();
        if (apkInfo.manifest.usesPermission != null && apkInfo.manifest.usesPermission.length > 0) {
            for (UsesPermissionInfo nn: apkInfo.manifest.usesPermission) {
                permissionLists.append(nn.name).append(',');
            }
        }
        if (apkInfo.manifest.usesPermissionSdk23 != null && apkInfo.manifest.usesPermissionSdk23.length > 0) {
            for (UsesPermissionSdk23Info nn: apkInfo.manifest.usesPermissionSdk23) {
                permissionLists.append(nn.name).append(',');
            }
        }
        if (apkInfo.manifest.permission != null && apkInfo.manifest.permission.length > 0) {
            for (PermissionInfo nn: apkInfo.manifest.permission) {
                permissionLists.append(nn.name).append(',');
            }
        }
        String permissions = "";
        if (permissionLists.length() > 0) {
            permissions = permissionLists.toString();
        }

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("packageName", packageName);
        jsonObject.put("appName", appName);
        jsonObject.put("versionName", versionName);
        jsonObject.put("versionCode", versionCode);
        jsonObject.put("minSdkVersion", minSdkVersion);
        jsonObject.put("targetSdkVersion", targetSdkVersion);
        jsonObject.put("signatureScheme", signatureScheme);
        jsonObject.put("certificate", certificate);
        jsonObject.put("iconPath", iconPath);
        jsonObject.put("permissions", permissions);

        String jsonString = jsonObject.toString();

        try (FileWriter fileWriter = new FileWriter("temp.json")) {
            fileWriter.write(jsonString);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private class ApkScannerListener implements ApkScanner.StatusListener {
        @Override
        public void onStart(final long estimatedTime) {
        }

        @Override
        public void onSuccess() {
        }

        @Override
        public void onError(int error) {
        }

        @Override
        public void onCompleted() {
        }

        @Override
        public void onProgress(final int step, final String message) {
        }

        @Override
        public void onStateChanged(final int status) {
            if (status == ApkScanner.STATUS_ALL_COMPLETED) {
                saveData();
                return;
            }
        }
    }
}
