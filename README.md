# Goal

Xposed module that listens to okhttp3 requests and saves all unique URLs to a file.

# Environment

This project developed on Genymotion emulator and xposed framework.


## Preapering emulator
Download framework: http://dl-xda.xposed.info/framework/

Download installer: https://forum.xda-developers.com/t/official-xposed-for-lollipop-marshmallow-nougat-oreo-v90-beta3-2018-01-29.3034811/

Video: https://www.youtube.com/watch?v=cxf0819hph8


# Build

Configurations come with the repo, so just needs to run/debug app. Default package is "org.wikipedia". It's variable(PACKAGE_NAME) on Hooks.java. When the method calls, we get url with "this" object via originalRequest object.


# Hook method

The hook class is: https://github.com/square/okhttp/blob/master/okhttp/src/jvmMain/kotlin/okhttp3/internal/connection/RealCall.kt

There is 2 different ways to call new http requests with okhttp3. They are "execute" and "enqueue" methods. And both of them calls "callStart" method in them. So our hook method is "callStart".


# Result

You can find 'urls.txt' file in emulator, under '/storage/emulated/legacy/'

```adb shell```

```cat /storage/emulated/legacy/urls.txt```
