package com.fuatf.url_listener;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.callbacks.XC_LoadPackage.LoadPackageParam;
import static de.robv.android.xposed.XposedHelpers.findAndHookMethod;
import static de.robv.android.xposed.XposedHelpers.getObjectField;

import android.os.Environment;

public class Hooks implements IXposedHookLoadPackage {
    private static final String PACKAGE_NAME = "org.wikipedia";
    private static final String URLS_FILENAME = "urls.txt";

    public void handleLoadPackage(final LoadPackageParam lpparam) throws Throwable {

        if(lpparam.packageName.equals(PACKAGE_NAME)) {
            File urlsFile = new File(Environment.getExternalStorageDirectory()  + "/" + URLS_FILENAME);
            Set<String> urlsList  = new HashSet<String>();

            try {
                if (urlsFile.exists()==false) {
                    urlsFile.createNewFile();
                }
                FileReader fileReader = new FileReader(urlsFile);
                BufferedReader bufferedReader = new BufferedReader(fileReader);
                String line;
                while ((line = bufferedReader.readLine()) != null) {
                    urlsList.add(line);
                }
                fileReader.close();
                bufferedReader.close();
            }catch (Exception e) {
                XposedBridge.log(e.getMessage());
            }

            try{
                findAndHookMethod("okhttp3.internal.connection.RealCall", lpparam.classLoader, "callStart", new XC_MethodHook() {

                    @Override
                    protected void afterHookedMethod(MethodHookParam param) {
                        Object request = getObjectField(param.thisObject, "originalRequest");
                        String url = getObjectField(request, "url").toString();
                        try {
                            if(urlsList.contains(url)==false){
                                FileWriter fileWriter = new FileWriter(urlsFile, true);
                                BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
                                bufferedWriter.write(url+'\n');
                                urlsList.add(url);
                                bufferedWriter.close();
                            }
                        } catch (FileNotFoundException e) {
                            XposedBridge.log(e.getMessage());
                        } catch (IOException e) {
                            XposedBridge.log(e.getMessage());
                        }
                    }
                });
            } catch (Exception e) {
                XposedBridge.log(e.getMessage());
            }
        }
    }
}


