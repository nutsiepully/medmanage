package org.vcs.medmanage;

import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class AptArrayAdapter extends ArrayAdapter<String>{

	private final Context context;
	private final String[] values;
 
	public AptArrayAdapter(Context context, String[] values) {
		super(context, R.layout.fragment_medication, values);
		this.context = context;
		this.values = values;
	}
 
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		LayoutInflater inflater = (LayoutInflater) context
			.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
 
		View rowView = inflater.inflate(R.layout.fragment_medication, parent, false);
		TextView textView = (TextView) rowView.findViewById(R.id.med_name);
		ImageView imageView = (ImageView) rowView.findViewById(R.id.med_image);
		textView.setText(values[position]);
 
		// Change icon based on name
		String s = values[position];
 
		System.out.println(s);
		
		imageView.setImageResource(R.drawable.ic_launcher);
 
		return rowView;
	}

}
