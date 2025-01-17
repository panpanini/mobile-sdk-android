![FAQ](images/jumio_feature_graphic.jpg)

# FAQ

## Table of Content
- [Improving user experience and reducing drop-off rate](#improving-user-experience-and-reducing-drop-off-rate)
- [Reducing the size of your app](#reducing-the-size-of-your-app)
  - [Strip unused modules](#strip-unused-modules)
  - [App bundles](#app-bundles)
  - [Architectures - ABI filters & Splitting](#arch)
- [Jumio Support](#jumio-support)

### Improve user experience and reducing drop-off rate
When evaluating user flows, one of the most commonly used metrics is the rate of drop-offs. At Jumio, we see considerable variance in drop-off rates across industries and customer implementations. For some implementations and industries, we see a higher rate of drop-offs on the first screens when compared with the average.
Scanning an ID with sensitive personal data printed on it naturally creates a high barrier for participation on the part of the end user. Therefore, conversion rates can be significantly influenced when the application establishes a sense of trust and ensures that users feel secure sharing their information.

One pattern that is recognizable throughout all of our customers’ SDK implementations: the more seamless the SDK integration, and the better job is done of setting user expectations prior to the SDK journey, the lower the drop-off rate becomes.

Our SDK provides a variety of customization options to help customers achieve a seamless integration. For customers using the standard SDK workflow, our [Surface tool](https://jumio.github.io/surface-android/) provides an easy-to-use WYSIWYG interface to see simple customization options that can be incorporated with minimal effort and generate the code necessary to implement them. For customers who want to have more granular control over look and feel, our SDK offers the [CustomUI](https://github.com/Jumio/mobile-sdk-android/blob/master/docs/integration_netverify-fastfill.md#custom-ui) option, which allows you to customize the entire user interface.

#### Example case:
![Onboarding bad case](images/onboardingBadCase.jpg)
- Default SDK UI is used and is presented on one of the first screens during onboarding. The user is unprepared for the next steps and might not understand the intention behind the request to show their ID.

#### Suggested improvements with additional customization:
![Onboarding good case](images/onboardingGoodCase.jpg)
 - Host application has an explanatory help screen that explains what will happen next and why this information is needed.
 - SDK is either customized to have a more embedded appearance or [CustomUI](https://github.com/Jumio/mobile-sdk-android/blob/master/docs/integration_netverify-fastfill.md#custom-ui) is used to create a completely seamless integration in the UX of our customers.
 - Also after the Jumio workflow that shows the displayed results and/or a message that the ID is currently verified, which might take some minutes.

### Reducing the size of your app
The Netverify SDK contains a wide range of different scanning methods. The SDK is able to capture identity documents and extract information on the device using enhanced machine learning and computer vision technologies.
The current download size of the sample application containing all products is 29 MB as mentioned in the [Netverify guide](integration_netverify-fastfill.md).
If you want to reduce the size of the SDK within your application, there are several ways that are explained in the following chapters.

#### Strip unused modules
Depending on the functionality you require, you may want to strip out unused functionality. This can be done adapting your jumio dependencies in your build.gradle. The following table shows a range of different product configurations with the size and modules that are linked for it. The measurements are based on our sample application.

|Product Configuration      | Size   | Modules   |
|:--------------------------|:------:|:----------|
|Full Netverify + Authentication                       | 23.7 MB    | core, nv, nv-mrz, nv-ocr, nv-nfc, nv-barcode, auth, face |
|Netverify MRZ only with Face                          | 20.08 MB  | core, nv, nv-mrz, face |
|Netverify MRZ only without Face                       | 9.24 MB   | core, nv, nv-mrz |
|Netverify Barcode scanning without Face               | 7.9 MB   | core, nv, nv-barcode-vision |
|BAM Checkout                                          | 11.36 MB   | core, bam |
|Document verification                                 | 7.9 MB   | core, dv  |


#### App bundles
Android offers a way to reduce the size of a customer's built application using [App bundles](https://developer.android.com/guide/app-bundle/)
Google Play’s new app serving model uses the *App bundle* to generate and serve optimized APKs for each user’s device configuration, so they download only the code and resources they need to run your app.

We implemented App bundles for our [Jumio Showcase application](https://play.google.com/store/apps/details?id=com.jumio.demo.netverify). It contains all products: Netverify, Authentication, BAM Checkout and Document Verification.
The app size was 28.9 MB when being distributed as a single apk file with all configurations. After implementing App bundles, the download size of the app was reduced significantly:

These are the results of different test devices:

|Device type    | Version       | Download size (Play Store) |
|:--------------|:-------------:|:--------------------------:|
|LG Leon        | 5.0.1   | 16.56 MB |
|Galaxy S7      | 7.0     | 18.10 MB |
|Huawei P10 Lite| 7.0     | 18.74 MB |
|Nexus 6        | 5.1.1   | 16.66 MB |
|Galaxy S5      | 6.0.1   | 16.67 MB |
|Galaxy Tab S   | 4.4.2   | 16.55 MB |
|Galaxy S8      | 7.0     | 18.15 MB |
|Pixel 3 XL     | 9.0     | 19.77 MB |

#### <a name="arch"></a>Architectures - ABI filters & Splitting
The SDK supports *armeabi-v7a* and *arm64-v8a* architecture. The following table shows their sizes:

|Architecture    | Size      |
|:---------------|:---------:|
|Arm64-v8a          | 9.9 MB  |
|Armeabi-v7a        | 8.7 MB |

You can filter which architecture to use by specifying the abiFilters.

That way, you could manually filter for *armeabi-v7a* as *arm64-v8a* is backwards compatible.
__Be aware:__ 64-bit support will be required from August 2019 as mentioned in the [Android developers blog](https://android-developers.googleblog.com/2017/12/improving-app-security-and-performance.html).

__Note:__ The abiFilters command in the ndk closure affects the Google Play Store filtering.

```
defaultConfig {
	ndk {
		abiFilters armeabi-v7a","arm64-v8a","x86","x86_64"
	}
}
```

It's also possible to manually provide a splitted apk on Google Play.
The apk can be split based on the architecture if multiple apks should be uploaded to the Google Play Store. Google Play Store manages to deliver the appropriate apk for the device.
```
splits {
	abi {
		enable true
		reset()
		include armeabi-v7a","arm64-v8a","x86","x86_64"
		universalApk false
	}
}
```

### Jumio Support
The Jumio development team is constantly striving to optimize the size of our frameworks while increasing functionality, to improve your KYC and to fight fraud. If you have any further questions, please reach out to our [support team](mailto:support@jumio.com).
