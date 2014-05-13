package ch.hsr.faith.android.app.activities;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import ch.hsr.faith.android.app.R;
import ch.hsr.faith.android.app.activities.listeners.BaseRequestListener;
import ch.hsr.faith.android.app.dto.FacilityCategoryList;
import ch.hsr.faith.android.app.services.request.AddOrUpdateFacilityRequest;
import ch.hsr.faith.android.app.services.request.FacilityCategoriesGetAllRequest;
import ch.hsr.faith.android.app.services.response.FacilityCategoryListResponse;
import ch.hsr.faith.android.app.services.response.FacilityResponse;
import ch.hsr.faith.android.app.util.LocaleUtil;
import ch.hsr.faith.domain.Facility;
import ch.hsr.faith.domain.FacilityCategory;

import com.octo.android.robospice.persistence.DurationInMillis;

public class AddFacilityActivity extends BaseActivity {
	private TextView failuresTextView;
	private String addFacilityRequestCacheKey;
	private String lastFacilityCategoriesGetAllRequestCacheKey;
	
	private Spinner facilityCategorySpinner;
	private FacilityCategoryAdapter categoryAdapter;

	private EditText nameField;
	private EditText streetField;
	private EditText zipField;
	private EditText cityField;
	private EditText homepageField;
	private EditText phoneField;
	private EditText emailField;
	private Spinner facilityCategoryField;
	private Spinner countryField;
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.fragment_add_or_edit_facility);

		facilityCategorySpinner = (Spinner) findViewById(R.id.select_facility_category);
		categoryAdapter = new FacilityCategoryAdapter(this, android.R.layout.simple_spinner_dropdown_item, new ArrayList<FacilityCategory>());
		facilityCategorySpinner.setAdapter(categoryAdapter);
		loadFacilityCategoryList();
		
		Spinner spinner = (Spinner) findViewById(R.id.select_facility_country);
		ArrayAdapter<CharSequence> countryAdapter = ArrayAdapter.createFromResource(this, R.array.countries_available, android.R.layout.simple_spinner_dropdown_item);
		countryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinner.setAdapter(countryAdapter);
	}
	
	@Override
	protected void onStart() {
		super.onStart();
		failuresTextView = (TextView) findViewById(R.id.add_facility_failures);
		nameField = (EditText) findViewById(R.id.edit_facility_name);
		streetField = (EditText) findViewById(R.id.edit_facility_street);
		zipField = (EditText) findViewById(R.id.edit_facility_zip);
		cityField = (EditText) findViewById(R.id.edit_facility_city);
		homepageField = (EditText) findViewById(R.id.edit_facility_homepage);
		phoneField = (EditText) findViewById(R.id.edit_facility_phone);
		emailField = (EditText) findViewById(R.id.edit_facility_email);
		facilityCategoryField = (Spinner) findViewById(R.id.select_facility_category);
		countryField = (Spinner) findViewById(R.id.select_facility_country);
	}
	
	private void loadFacilityCategoryList() {
		FacilityCategoriesGetAllRequest request = new FacilityCategoriesGetAllRequest();
		lastFacilityCategoriesGetAllRequestCacheKey = request.createCacheKey();
		spiceManager.execute(request, lastFacilityCategoriesGetAllRequestCacheKey, DurationInMillis.ONE_MINUTE, new FacilityCategoriesListRequestListener(this));
	}

	public void addButtonClicked(View view) {
		cleanFailuresView();
		if (isInputValid()) {
			Facility facility = new Facility();
			facility.setName(nameField.getText().toString());
			facility.setStreet(streetField.getText().toString());
			facility.setZip(zipField.getText().toString());
			facility.setCity(cityField.getText().toString());
			facility.setHomepage(homepageField.getText().toString());
			facility.setPhone(phoneField.getText().toString());
			facility.setEmail(emailField.getText().toString());
			facility.setFacilityCategory((FacilityCategory) facilityCategoryField.getSelectedItem());
			facility.setCountry(countryField.getSelectedItem().toString());
			facility.setLevel(0);
			
			AddOrUpdateFacilityRequest request = new AddOrUpdateFacilityRequest(getLoginObject(), facility);
			addFacilityRequestCacheKey = request.createCacheKey();
			spiceManager.execute(request, addFacilityRequestCacheKey, DurationInMillis.ALWAYS_EXPIRED, new FacilityRequestListener(this));
		}
	}

	private boolean isInputValid() {
		if ("".equals(nameField.getText().toString())) {
			nameField.setError(getString(R.string.add_facility_error_name_empty));
			return false;
		}
		if ("".equals(streetField.getText().toString())) {
			nameField.setError(getString(R.string.add_facility_error_street_empty));
			return false;
		}
		if ("".equals(zipField.getText().toString())) {
			nameField.setError(getString(R.string.add_facility_error_zip_empty));
			return false;
		}
		if ("".equals(cityField.getText().toString())) {
			nameField.setError(getString(R.string.add_facility_error_city_empty));
			return false;
		}
		if ("".equals(emailField.getText().toString())) {
			nameField.setError(getString(R.string.add_facility_error_email_empty));
			return false;
		}
		if (countryField.getSelectedItem() == null) {
			failuresTextView.setText(getString(R.string.add_facility_error_no_country_selected));
			failuresTextView.setVisibility(TextView.VISIBLE);
			return false;
		}
		if (facilityCategoryField.getSelectedItem() == null) {
			failuresTextView.setText(getString(R.string.add_facility_error_no_category_selected));
			failuresTextView.setVisibility(TextView.VISIBLE);
			return false;
		}
		return true;
	}

	private void cleanFailuresView() {
		failuresTextView.setText("");
		failuresTextView.setVisibility(TextView.INVISIBLE);
	}
	
	private class FacilityCategoriesListRequestListener extends BaseRequestListener<FacilityCategoryListResponse, FacilityCategoryList> {
		public FacilityCategoriesListRequestListener(BaseActivity baseActivity) {
			super(baseActivity);
		}

		@Override
		protected void handleSuccess(FacilityCategoryList data) {
			logger.info("List of FacilityCategories successfully loaded");
			categoryAdapter.clear();
			for (FacilityCategory facilityCategory : data) {
				categoryAdapter.add(facilityCategory);
			}
			categoryAdapter.notifyDataSetChanged();
		}
	}
	
	private class FacilityCategoryAdapter extends ArrayAdapter<FacilityCategory> {

		public FacilityCategoryAdapter(Context context, int textViewResourceId, List<FacilityCategory> objects) {
			super(context, textViewResourceId, objects);
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			FacilityCategory facilityCategory = getItem(position);
			if (convertView == null) {
				convertView = LayoutInflater.from(getContext()).inflate(android.R.layout.simple_spinner_dropdown_item, null);
			}
			TextView textView = (TextView) convertView.findViewById(android.R.id.text1);
			textView.setTextSize(18);
			textView.setText(facilityCategory.getName().getText(LocaleUtil.getCurrentLocale()));
			return convertView;
		}
	}
	
	private class FacilityRequestListener extends BaseRequestListener<FacilityResponse, Facility> {

		public FacilityRequestListener(BaseActivity baseActivity) {
			super(baseActivity);
		}

		@Override
		protected void handleSuccess(Facility facility) {
			Toast.makeText(getApplicationContext(), getString(R.string.add_facility_successfully_saved), Toast.LENGTH_LONG).show();
			finish();
		}

		@Override
		protected void handleFailures(List<String> failures) {
			String failureText = new String();
			for (String string : failures) {
				failureText = failureText + string + "\n";
			}
			failuresTextView.setText(failureText);
			failuresTextView.setVisibility(TextView.VISIBLE);
		}
	}
}
