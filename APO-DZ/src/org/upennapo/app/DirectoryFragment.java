package org.upennapo.app;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Arrays;
import java.util.Comparator;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

public class DirectoryFragment extends Fragment{
	
	public static final String URL_KEY   = "URL";
	public static final String SHEET_KEY = "SHEET_KEY";
	private ArrayList<Brother> directoryList;
	private View view;
	
	public DirectoryFragment() {
    }

	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
		// Retrieve the arguments passed by the MainActivity
        final String urlString = getArguments().getString(URL_KEY);
        final String sheetKey  = getArguments().getString(SHEET_KEY);
		
        Activity context = getActivity();
        context.getActionBar();
        // Activate the progress view in the action bar.
        // Progress bar code is due to http://guides.thecodepath.com/android/Handling-ProgressBars
        if (ReadJSON.isNetworkAvailable(context)) {
        	// Make an asynchronous request for the JSON using the URL
        	AsyncBrotherLoader loader = new AsyncBrotherLoader();
        	showProgressBar();
        	loader.execute(urlString, sheetKey);
        } else {
        	// Display a Toast if we don't have an Internet connection.
        	Toast noInternetAlert = Toast.makeText(context,
        											"Oops! There's no internet connection. Try again later.",
        											Toast.LENGTH_LONG);
        	noInternetAlert.show();
        }
		
		
		// Inflate the View
		view = inflater.inflate(R.layout.fragment_directory, container, false);
		return view;
	}
	
	
	private void showProgressBar() {
        getActivity().setProgressBarVisibility(true);
    }
	
	private void updateProgressValue(int value) {
        // Manage the progress (i.e within an AsyncTask)
        // Valid ranges are from 0 to 10000 (both inclusive). 
        // If 10000 is given, the progress bar will be completely filled and will fade out.
		getActivity().setProgress(value);
    }
    
    // Should be called when an async task has finished
    public void hideProgressBar() {
    	getActivity().setProgressBarVisibility(false);
    }
	
	private class AsyncBrotherLoader extends AsyncTask<String, Void, Brother[]> {

	    @Override
	    protected void onPreExecute() {        
	        super.onPreExecute();
	        updateProgressValue(1000);
	    }

	    @Override
	    protected Brother[] doInBackground(String... params) {
	    	Brother[] results = ReadJSON.getDirectoryData(params[0], params[1]);
	    	return results;
	    }
	    
	    @Override
	    protected void onPostExecute(Brother[] result) {
	    	if (result == null) {
	    		// If there is an error getting the result, break the loop and display an alert.
	    		updateProgressValue(10000);
	    		
	    		Toast failureAlert = Toast.makeText(getActivity(), "Unable to load at this time.", Toast.LENGTH_LONG);
	    		failureAlert.show();
	    		return;
	    	}
	        directoryList = new ArrayList<Brother>(Arrays.asList(result));
	        updateProgressValue(4000);
	        Collections.sort(directoryList, new BrotherComparator());        
	        updateProgressValue(7000);
	        
	        ArrayList<String> alphabetizedNames = new ArrayList<String>();
	       	for (Brother brother : directoryList) {
				String firstName =
					brother.Preferred_Name.equals("") ? brother.First_Name : brother.Preferred_Name;
				alphabetizedNames.add(firstName + " " + brother.Last_Name);
			}
	       	updateProgressValue(8500);

			AlphabeticalAdapter adapter =
				new AlphabeticalAdapter(getActivity(), R.layout.centered_textview, R.id.centered_text, alphabetizedNames);
			ListView list = (ListView) view.findViewById(R.id.name_list);
			
			list.setAdapter(adapter);
			updateProgressValue(9000);
			list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
				public void onItemClick(AdapterView <?> parent, View view, int position, long id) {
					// Prepare data to send to the details view.
					Brother b = directoryList.get(position);
					HashMap<String,String> broMap = new HashMap<String,String>();
					broMap.put(Brother.LAST_NAME_KEY, b.Last_Name);
					broMap.put(Brother.FIRST_NAME_KEY, b.First_Name);
					broMap.put(Brother.PREFERRED_NAME_KEY, b.Preferred_Name);
					broMap.put(Brother.EMAIL_ADDRESS_KEY, b.Email_Address);
					broMap.put(Brother.PHONE_NUMBER_KEY, b.Phone_Number);
					broMap.put(Brother.PLEDGE_CLASS_KEY, b.Pledge_Class);
					broMap.put(Brother.GRADUATION_YEAR_KEY, String.valueOf(b.Expected_Graduation_Year));
					broMap.put(Brother.SCHOOL_KEY, b.School);
					broMap.put(Brother.MAJOR_KEY, b.Major);
					broMap.put(Brother.BIRTHDAY_KEY, b.Birthday);
					
					// Open the details view.
					Intent detailPage = new Intent(getActivity(), DirectoryDetails.class);
					detailPage.putExtra(getString(R.string.dir_brother_data), broMap);
					getActivity().startActivity(detailPage);
				}
				
			});
			updateProgressValue(10000);
	    }
	}
	
	public class BrotherComparator implements Comparator<Brother> {
		@Override
		public int compare(Brother b1, Brother b2) {
			// Retrieve brother 1's first name.
			final String preferredName1 = b1.Preferred_Name;
			final String firstName1 = preferredName1.length() == 0 ? b1.First_Name : preferredName1;
			
			// Retrieve brother 2's first name.
			final String preferredName2 = b2.Preferred_Name;
			final String firstName2 = preferredName2.length() == 0 ? b2.First_Name : preferredName2;
			
			// Compare and return lexographic ordering.
			return firstName1.compareToIgnoreCase(firstName2);
		}
	}
	
}

