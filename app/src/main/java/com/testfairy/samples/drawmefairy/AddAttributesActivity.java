package com.testfairy.samples.drawmefairy;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.testfairy.TestFairy;

import java.util.HashMap;
import java.util.Map;

import utils.ActivityTime;

public class AddAttributesActivity extends Activity {

	private static final String SEPARATOR = " -> ";

	private class AttributeTableItem {
		private final LinearLayout container;

		public final TextView keyText;
		public final TextView valueText;

		public AttributeTableItem(final LinearLayout parent, final String key, final String value) {
			this.container = new LinearLayout(AddAttributesActivity.this);
			this.keyText = new TextView(AddAttributesActivity.this);
			this.valueText = new TextView(AddAttributesActivity.this);

			int padding = (int) dp2px(10);
			container.setOrientation(LinearLayout.HORIZONTAL);
			container.setWeightSum(1f);
			container.setPadding(padding, padding / 5, padding, padding / 5);
			container.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));

			LinearLayout.LayoutParams keyParam = new LinearLayout.LayoutParams(
					LinearLayout.LayoutParams.MATCH_PARENT,
					LinearLayout.LayoutParams.WRAP_CONTENT,
					0.6f
			);
			keyText.setLayoutParams(keyParam);

			LinearLayout.LayoutParams valueParam = new LinearLayout.LayoutParams(
					LinearLayout.LayoutParams.MATCH_PARENT,
					LinearLayout.LayoutParams.WRAP_CONTENT,
					0.4f
			);
			valueText.setLayoutParams(valueParam);

			keyText.setTextSize(dp2px(14));
			valueText.setTextSize(dp2px(14));

			keyText.setTextColor(Color.WHITE);
			valueText.setTextColor(Color.WHITE);

			keyText.setText(key);
			valueText.setText(SEPARATOR + value);

			keyText.setTypeface(Typeface.MONOSPACE);
			valueText.setTypeface(Typeface.MONOSPACE);

			keyText.setSingleLine(false);
			valueText.setSingleLine(false);

			container.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					container.setBackgroundColor(0xFF3355CC);

					DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							container.setBackgroundColor(Color.TRANSPARENT);
							switch (which){
								case DialogInterface.BUTTON_POSITIVE:
									remove();
									break;

								case DialogInterface.BUTTON_NEGATIVE:
									//No button clicked
									break;
							}
						}
					};

					AlertDialog.Builder builder = new AlertDialog.Builder(AddAttributesActivity.this);
					builder.setMessage("Do you want to delete \"" + key + "\" attribute?").setPositiveButton("Yes", dialogClickListener)
							.setNegativeButton("No", dialogClickListener).show();
				}
			});

			container.addView(keyText);
			container.addView(valueText);
			parent.addView(container);
		}

		public void remove() {
			String key = keyText.getText().toString();

			TestFairy.setAttribute(key, "");

			container.removeAllViews();
			ViewGroup parent = (ViewGroup) container.getParent();
			parent.removeView(container);

			attributeViews.remove(key);
			attributes.remove(key);
		}
	}

	private final String TAG = getClass().getSimpleName();

	ActivityTime activityTime;

	private static final Map<String, String> attributes = new HashMap<>();
	private final Map<String, AttributeTableItem> attributeViews = new HashMap<>();

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		Log.d(TAG, "onCreate " + TAG);

		// hide title bar
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.add_attributes);

		final Button addAttributeButton = findViewById(R.id.add_attibute_button);
		final EditText attributeKeyEditText = findViewById(R.id.attributes_key_edittext);
		final EditText attributeValueEditText = findViewById(R.id.attributes_value_edittext);
		final LinearLayout attributesLinearLayout = findViewById(R.id.current_attributes);

		addAttributeButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				try {
					final String key = attributeKeyEditText.getText().toString();
					final String value = attributeValueEditText.getText().toString().replace(SEPARATOR, "");

					if (key.length() == 0 || value.length() == 0) {
						throw new NullPointerException();
					}

					TestFairy.setAttribute(key, value);
					attributes.put(key, value);

					if (attributeViews.containsKey(key)) {
						AttributeTableItem item = attributeViews.get(key);
						item.keyText.setText(key);
						item.valueText.setText(SEPARATOR + value);
					} else {
						AttributeTableItem item = new AttributeTableItem(attributesLinearLayout, key, value);
						attributeViews.put(key, item);
					}

					attributeKeyEditText.setText("");
					attributeValueEditText.setText("");
				} catch (NullPointerException e) {
					Toast.makeText(AddAttributesActivity.this, "You must provide a valid text for the attribute.", Toast.LENGTH_SHORT).show();
				}
			}
		});

		// Load backup
		for (String key : attributes.keySet()) {
			AttributeTableItem item = new AttributeTableItem(attributesLinearLayout, key, attributes.get(key));
			attributeViews.put(key, item);
		}
	}

	@Override
	protected void onStart() {
		activityTime = new ActivityTime(TAG);
		super.onStart();
	}

	@Override
	protected void onPause() {
		activityTime.cancel();
		super.onPause();
	}

	private float dp2px(float dp){
		return dp * ((float) getResources().getDisplayMetrics().densityDpi / DisplayMetrics.DENSITY_DEFAULT);
	}
}