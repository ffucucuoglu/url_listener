package com.fuatf.url_listener;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.HashSet;
import java.util.Set;
import android.os.Environment;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.callbacks.XC_LoadPackage.LoadPackageParam;
import static de.robv.android.xposed.XposedHelpers.findAndHookMethod;
import static de.robv.android.xposed.XposedHelpers.getObjectField;

public class Hooks implements IXposedHookLoadPackage {
    private static final String PACKAGE_NAME = "org.wikipedia";
    private static final String URLS_FILENAME = "urls.txt";

    public void handleLoadPackage(final LoadPackageParam lpparam) throws Throwable {

        if(lpparam.packageName.equals(PACKAGE_NAME)) {
            File urlsFile = new File(Environment.getExternalStorageDirectory()  + "/" + URLS_FILENAME);
            Set<String> urlsList  = new HashSet<String>();

            //Loads old urls from file
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
                XposedBridge.log("An error occurred when reading urls file!");
                XposedBridge.log(e.getMessage());
            }

            try{
                /*
                The hook class is: https://github.com/square/okhttp/blob/master/okhttp/src/jvmMain/kotlin/okhttp3/internal/connection/RealCall.kt
                There is 2 different ways to call new http requests with okhttp3. They are "execute" and "enqueue" methods. And both of them calls "callStart" method in them. So our hook method is "callStart".
                 */
                findAndHookMethod("okhttp3.internal.connection.RealCall", lpparam.classLoader, "callStart", new XC_MethodHook() {

                    @Override
                    protected void afterHookedMethod(MethodHookParam param) {
                        Object request = getObjectField(param.thisObject, "originalRequest");
                        String url = getObjectField(request, "url").toString();
                        try {
                            //We want just unique urls!
                            if(urlsList.contains(url)==false){
                                FileWriter fileWriter = new FileWriter(urlsFile, true);
                                BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
                                bufferedWriter.write(url+'\n');
                                urlsList.add(url);
                                bufferedWriter.close();
                            }
                        } catch (Exception e) {
                            XposedBridge.log("An error occurred when write urls to file!");
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


