package com.diamondsoftware.android.sunriver_av_3_0;

import android.app.Activity;
import android.content.Context;
import android.hardware.Camera;
import android.widget.ImageView;

public interface CameraImplementer {
	void setmIndexIntoSelfieImages(int whichItem);
	Activity getActivity();
	void setCameraDisplayOrientation(Activity activity,
									 int cameraId, Camera camera, ImageView overlayView);
}
