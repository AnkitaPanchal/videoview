package com.Video.Player.Demo;

import android.annotation.TargetApi;
import android.content.Context;
import android.net.Uri;
import android.os.Build;
import android.util.AttributeSet;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.VideoView;

import org.videolan.libvlc.LibVLC;
import org.videolan.libvlc.Media;

public class VodView extends VideoView
{
	private static LibVLC sLibVLC;

	public VodView(Context context) {
		super(context);
	}

	public VodView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public VodView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}
	public void initLibVLC(Uri uri) {
		sLibVLC = new LibVLC();
		final Media media = new Media(sLibVLC, uri);
	}

	@Override
	@TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
	public void onInitializeAccessibilityEvent(AccessibilityEvent event) {
		super.onInitializeAccessibilityEvent(event);
	}

	@Override
	@TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
	public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo info) {
		super.onInitializeAccessibilityNodeInfo(info);
	}
	/**
	 * Resize video view by using SurfaceHolder.setFixedSize(...). See {@link android.view.SurfaceHolder#setFixedSize}
	 * @param width
	 * @param height
	 */
	public void setFixedVideoSize(int width, int height)
    {
        getHolder().setFixedSize(width, height);

    }
}
