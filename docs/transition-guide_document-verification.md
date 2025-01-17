![Jumio](images/document_verification.png)

# Transition guide for Document Verification SDK

This section only covers the breaking technical changes that should be considered when updating from the previous version.

## 3.3.0
#### New SDK localizations added
SDK Translations for the languages Italian and Portuguese have been added.


## 3.2.1
No backward incompatible changes

## 3.2.0
#### Dependency change
*  ~~androidx.appcompat:appcompat:1.0.0~~ is replaced by androidx.appcompat:appcompat:1.0.2

## 3.1.0
No backward incompatible changes

## 3.0.0
#### Renamed public API methods and parameters
The following methods and the related parameters have been renamed to ensure consistency across all platforms
* `setMerchantReportingCriteria(..)` -> `setReportingCriteria(..)`
* `setMerchantIdScanReference(..)` -> `setCustomerInternalReference(..)`
* `setCustomerId(..)` -> `setUserReference(..)`

## 2.15.0
#### Added Room
Dependencies that have been added to the SDK:
+ androidx.room:room-runtime:2.0.0  

## 2.14.0
#### Migrate to AndroidX
The support library was migrated to [`AndroidX`](https://developer.android.com/jetpack/androidx/). As the developer page outlines, this is a mandatory step since all new Support Library development and maintenance will occur in the AndroidX library. This [`migration guide`](https://developer.android.com/jetpack/androidx/migrate) shows you how to migrate your application to AndroidX.

Check out the changed dependencies in the  [`dependencies section`](https://github.com/Jumio/mobile-sdk-android/blob/master/docs/integration_netverify-fastfill.md#dependencies) or in the [`build.gradle`](https://github.com/Jumio/mobile-sdk-android/blob/master/sample/JumioMobileSample/build.gradle) of the sample application.
The mapping for all support libraries is listed in section "Artifact mappings" [here](https://developer.android.com/jetpack/androidx/migrate)

Dependencies that changed in the SDK:
+ com.android.support:appcompat-v7:27.1.1 -> androidx.appcompat:appcompat:1.0.0
+ com.android.support:design:27.1.1 -> com.google.android.material:material:1.0.0
- com.android.support:support-v4:27.1.1 -> androidx.legacy:legacy-support-v4:1.0.0 (was merged by AndroidX and can be therefore be fully removed)

#### Removed document type USSS
The document type for US social security card (USSS) was removed from the SDK.

#### Default Settings
The default value for [`enableExtraction`](https://jumio.github.io/mobile-sdk-android/com/jumio/dv/DocumentVerificationSDK.html#setEnableExtraction-boolean-) was changed to `true`. Please make sure that it is explicitly set to false in case a scan without extraction should be performed.

## 2.13.0
#### Removed deprecated ABIs mips, mips64 and armeabi
These ABIs were deprecated in recent NDK toolsets as mentioned here - https://developer.android.com/ndk/guides/abis and are not used any more.

#### New SDK localizations added
In addition to English, the translations for the languages Chinese (Simplified), Dutch, Frensh, German and Spanish have been added.

## 2.12.1
No backward incompatible changes.

## 2.12.0
#### New method setEnableExtraction
SDK method `documentVerificationSDK.setEnableExtraction(boolean)` has been added for enabling/disabling extraction on documents.

#### Additional information method removed
SDK method `documentVerificationSDK.setAdditionalInformation` has been removed.

## 2.11.0
#### New error scheme
The schema for `errorCode` changed and it's type is now String instead of Integer.
Read more detailed information on this in chapter [Error codes](/docs/integration_document-verification.md#error-codes)

## 2.10.1
No backward incompatible changes.

## 2.10.0
* SDK updated to Android plugin for gradle 3.0 - https://developer.android.com/studio/build/gradle-plugin-3-0-0-migration.html
* Minimum API level was raised from Android 4.1 (API level 16) to Android 4.4 (API level 19)

## 2.9.0
No backward incompatible changes.

## 2.8.0
No backward incompatible changes.

## 2.7.0
* Removed SDK method `setShowHelpBeforeScan(boolean)` because the collapsed help view is now constantly visible during scanning.
* Additional Proguard rules for the Samsung Camera SDK have to be added:
 ```
 -keep class com.samsung.** { *; }
 -keep class com.samsung.**$* { *; }
 -dontwarn com.samsung.**
 ```

## 2.6.1
No backward incompatible changes.

## 2.6.0
#### Changes in SDK Api
* Add DocumentVerificationSDK method `isRooted(Context)` for device root-check before starting the SDK

## 2.5.0
No backward incompatible changes.

## 2.4.0
No backward incompatible changes.

## 2.3.0
No backward incompatible changes.

## Copyright

&copy; Jumio Corp. 268 Lambert Avenue, Palo Alto, CA 94306
