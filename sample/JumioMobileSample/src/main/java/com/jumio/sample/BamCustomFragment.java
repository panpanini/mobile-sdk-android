package com.jumio.sample;

import android.app.*;
import android.content.*;
import android.content.res.Configuration;
import android.graphics.Point;
import android.os.Bundle;
import android.support.v4.app.*;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.*;
import android.widget.*;

import com.jumio.MobileSDK;
import com.jumio.bam.*;
import com.jumio.bam.custom.*;
import com.jumio.commons.utils.ScreenUtil;
import com.jumio.core.enums.JumioDataCenter;
import com.jumio.core.exceptions.PlatformNotSupportedException;

import java.util.ArrayList;

/**
 * Copyright © 2017 Jumio Corporation All rights reserved.
 */
public class BamCustomFragment extends Fragment implements BamCustomScanInterface {
	private final static String TAG = "JumioSDK_BamCustom";
	private static final int PERMISSION_REQUEST_CODE_BAM_CUSTOM = 302;

	private String apiToken = null;
	private String apiSecret = null;

	private Button startBamCustom;
	private Button stopBamCustom;
	private BamSDK bamSDK;

	private BamCustomScanPresenter customScanPresenter;
	private ImageView switchCameraImageView;
	private ImageView toggleFlashImageView;
	private RelativeLayout bamCustomContainer;
	private BamCustomScanView customScanView;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
	                         Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_bam_custom, container, false);

		Bundle args = getArguments();
		customScanView = (BamCustomScanView) rootView.findViewById(R.id.bamCustomScanView);

		apiToken = args.getString(MainActivity.KEY_API_TOKEN);
		apiSecret = args.getString(MainActivity.KEY_API_SECRET);

		startBamCustom = (Button) rootView.findViewById(R.id.btnStart);
		startBamCustom.setText(args.getString(MainActivity.KEY_BUTTON_TEXT));
		startBamCustom.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {

				//Since the BamSDK is a singleton internally, a new instance is not
				//created here.
				if (!MobileSDK.hasAllRequiredPermissions(getActivity())) {
					ActivityCompat.requestPermissions(getActivity(), MobileSDK.getMissingPermissions(getActivity()), PERMISSION_REQUEST_CODE_BAM_CUSTOM);
				} else
					startBamCustomScan();
			}
		});

		stopBamCustom = (Button) rootView.findViewById(R.id.stopBamCustomButton);
		stopBamCustom.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {

				//Do not just re-instantiate the SDK here because fast subsequent taps on the button
				//can cause two SDK instances to be created, which will result in undefined (and
				//most likely incorrect) behaviour. A suitable place for the re-instantiation of the SDK
				//would be onCreate().
				stopBamCustomScan();
			}
		});

		bamCustomContainer = (RelativeLayout) rootView.findViewById(R.id.bamCustomContainer);

		switchCameraImageView = (ImageView) rootView.findViewById(R.id.switchCameraImageView);
		switchCameraImageView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				v.setVisibility(View.INVISIBLE);
				if (customScanPresenter != null && customScanPresenter.hasMultipleCameras())
					customScanPresenter.switchCamera();
			}
		});

		toggleFlashImageView = (ImageView) rootView.findViewById(R.id.toggleFlashImageView);
		toggleFlashImageView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				v.setEnabled(false);
				if (customScanPresenter != null && customScanPresenter.hasFlash()) {
					customScanPresenter.toggleFlash();
					v.setEnabled(true);
				}
				toggleFlashImageView.setImageResource(customScanPresenter.isFlashOn() ? R.drawable.ic_flash_off : R.drawable.ic_flash_on);
			}
		});

		return rootView;
	}

	private void stopBamCustomScan() {
		if (customScanPresenter != null) {
			customScanPresenter.stopScan();
			customScanPresenter.clearSDK();
			customScanPresenter = null;
		}
		if (bamCustomContainer != null) {
			bamCustomContainer.setVisibility(View.GONE);
		}
		if (startBamCustom != null) {
			startBamCustom.setVisibility(View.VISIBLE);
		}
	}

	@Override
	public void onPause() {
		if (customScanPresenter != null)
			customScanPresenter.onActivityPause();
		stopBamCustomScan();
		super.onPause();
	}


	@Override
	public void onDestroy() {
		super.onDestroy();
		if (customScanPresenter != null)
			customScanPresenter.clearSDK();

		if(bamSDK != null) {
			bamSDK.destroy();
			bamSDK = null;
		}
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);

		boolean isPortrait = newConfig.orientation == Configuration.ORIENTATION_PORTRAIT;
		FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(isPortrait ? FrameLayout.LayoutParams.MATCH_PARENT : FrameLayout.LayoutParams.WRAP_CONTENT, isPortrait ? FrameLayout.LayoutParams.WRAP_CONTENT : ScreenUtil.dpToPx(getActivity(), 300));
		customScanView.setLayoutParams(params);
	}

	private void startBamCustomScan() {
		try {
			startBamCustom.setVisibility(View.GONE);
			bamCustomContainer.setVisibility(View.VISIBLE);
			initializeBamSDK();
			if (bamSDK == null)
				return;
			customScanPresenter = bamSDK.start(this, customScanView);
		} catch (IllegalArgumentException e) {
			Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_LONG).show();
		}
	}


	private void initializeBamSDK() {
		try {
			// You can get the current SDK version using the method below.
			// BamSDK.getSDKVersion();

			// Call the method isSupportedPlatform to check if the device is supported
			// BamSDK.isSupportedPlatform(getActivity());

			// Applications implementing the SDK shall not run on rooted devices. Use either the below
			// method or a self-devised check to prevent usage of SDK scanning functionality on rooted
			// devices.
			if (BamSDK.isRooted(getActivity()))
				Log.w(TAG, "Device is rooted");

			// To create an instance of the SDK, perform the following call as soon as your activity is initialized.
			// Make sure that your merchant API token and API secret are correct and specify an instance
			// of your activity. If your merchant account is created in the EU data center, use
			// JumioDataCenter.EU instead.
			bamSDK = BamSDK.create(getActivity(), apiToken, apiSecret, JumioDataCenter.US);

			// Use the following method to enable offline credit card scanning.
			// try {
			//     bamSDK = BamSDK.create(SampleActivity.this, "YOUROFFLINETOKEN");
			// } catch (SDKExpiredException e) {
			//    e.printStackTrace();
			//    Toast.makeText(getApplicationContext(), "The offline SDK is expired", Toast.LENGTH_LONG).show();
			// }

			// Overwrite your specified reporting criteria to identify each scan attempt in your reports (max. 100 characters).
			// bamSDK.setMerchantReportingCriteria("YOURREPORTINGCRITERIA");

			// To restrict supported card types, pass an ArrayList of CreditCardTypes to the setSupportedCreditCardTypes method.
			// ArrayList<CreditCardType> creditCardTypes = new ArrayList<CreditCardType>();
			// creditCardTypes.add(CreditCardType.VISA);
			// creditCardTypes.add(CreditCardType.MASTER_CARD);
			// creditCardTypes.add(CreditCardType.AMERICAN_EXPRESS);
			// creditCardTypes.add(CreditCardType.DINERS_CLUB);
			// creditCardTypes.add(CreditCardType.DISCOVER);
			// creditCardTypes.add(CreditCardType.CHINA_UNIONPAY);
			// creditCardTypes.add(CreditCardType.JCB);
			// bamSDK.setSupportedCreditCardTypes(creditCardTypes);

			// Expiry recognition, card holder name and CVV entry are enabled by default and can be disabled.
			// You can enable the recognition of sort code and account number.
			// bamSDK.setExpiryRequired(false);
			// bamSDK.setCardHolderNameRequired(false);
			// bamSDK.setCvvRequired(false);
			// bamSDK.setSortCodeAndAccountNumberRequired(true);

			// You can show the unmasked credit card number to the user during the workflow if setCardNumberMaskingEnabled is disabled.
			// bamSDK.setCardNumberMaskingEnabled(false);

			// The user can edit the recognized expiry date if setExpiryEditable is enabled.
			// bamSDK.setExpiryEditable(true);

			// The user can edit the recognized card holder name if setCardHolderNameEditable is enabled.
			// bamSDK.setCardHolderNameEditable(true);

			// You can set a short vibration and sound effect to notify the user that the card has been detected.
			// bamSDK.setVibrationEffectEnabled(true);
			// bamSDK.setSoundEffect(R.raw.shutter_sound);

			// Use the following method to set the default camera position.
			// bamSDK.setCameraPosition(JumioCameraPosition.FRONT);

			// Automatically enable flash when scan is started.
			// bamSDK.setEnableFlashOnScanStart(true);

			// You can add custom fields to the confirmation page (keyboard entry or predefined values).
			// bamSDK.addCustomField("zipCodeId", getString(R.string.zip_code), InputType.TYPE_CLASS_NUMBER, "[0-9]{5,}");
			// ArrayList<String> states = new ArrayList<String>(Arrays.asList(getResources().getStringArray(R.array.state_selection_values)));
			// bamSDK.addCustomField("stateId", getString(R.string.state), states, false, getString(R.string.state_reset_value));

			// Use the following method to override the SDK theme that is defined in the Manifest with a custom Theme at runtime
			//bamSDK.setCustomTheme(R.style.YOURCUSTOMTHEMEID);

		} catch (PlatformNotSupportedException e) {
			e.printStackTrace();
			Toast.makeText(getActivity().getApplicationContext(), "This platform is not supported", Toast.LENGTH_LONG).show();
		}
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == BamSDK.REQUEST_CODE) {
			if (data == null)
				return;
			ArrayList<String> scanAttempts = data.getStringArrayListExtra(BamSDK.EXTRA_SCAN_ATTEMPTS);

			if (resultCode == Activity.RESULT_OK) {
				BamCardInformation cardInformation = data.getParcelableExtra(BamSDK.EXTRA_CARD_INFORMATION);

				cardInformation.clear();
			} else if (resultCode == Activity.RESULT_CANCELED) {
				String errorMessage = data.getStringExtra(BamSDK.EXTRA_ERROR_MESSAGE);
				int errorCode = data.getIntExtra(BamSDK.EXTRA_ERROR_CODE, 0);
			}

			//At this point, the SDK is not needed anymore. It is highly advisable to call destroy(), so that
			//internal resources can be freed.
			if (bamSDK != null) {
				bamSDK.destroy();
				bamSDK = null;
			}
		}
	}

	@Override
	public void setUserVisibleHint(boolean isVisibleToUser) {
		super.setUserVisibleHint(isVisibleToUser);
		if (!isVisibleToUser) {
			stopBamCustomScan();
		}
	}

	//Called as soon as the camera is available for the custom scan. It is safe to check for flash and additional cameras here.
	@Override
	public void onBamCameraAvailable() {
		Log.d("BamCustomScan", "camera available");
		switchCameraImageView.setVisibility(customScanPresenter.hasMultipleCameras() ? View.VISIBLE : View.INVISIBLE);
		switchCameraImageView.setImageResource(customScanPresenter.isCameraFrontFacing() ? R.drawable.ic_camera_rear : R.drawable.ic_camera_front);
		toggleFlashImageView.setVisibility(customScanPresenter.hasFlash() ? View.VISIBLE : View.INVISIBLE);
		toggleFlashImageView.setImageResource(customScanPresenter.isFlashOn() ? R.drawable.ic_flash_off : R.drawable.ic_flash_on);
	}

	@Override
	public void onBamError(int errorCode, int detailedErrorCode, String errorMessage, boolean retryPossible, ArrayList<String> scanAttempts) {
		Log.d("BamCustomScan", "error occured");
		//Do not show error dialog when it is an error because of background execution not supported exception.
		if (errorCode == 310)
			return;
		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
		alertDialogBuilder.setTitle("Scan error");
		alertDialogBuilder.setMessage(errorMessage);
		if (retryPossible) {
			alertDialogBuilder.setPositiveButton("retry", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					try {
						customScanPresenter.retryScan();
					} catch (UnsupportedOperationException e) {
						e.printStackTrace();
						Toast.makeText(getActivity().getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
					}
				}
			});
		}
		alertDialogBuilder.setNegativeButton("cancel", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				stopBamCustom.performClick();
			}
		});
		alertDialogBuilder.show();
	}

	//When extraction is started, the preview screen will be paused. A loading indicator can be displayed within this callback.
	@Override
	public void onBamExtractionStarted() {
		Log.d("BamCustomScan", "extraction started");
	}

	@Override
	public void onBamExtractionFinished(BamCardInformation bamCardInformation, ArrayList<String> scanAttempts) {
		Log.d("BamCustomScan", "extraction finished");
		bamCardInformation.clear();
	}
}
