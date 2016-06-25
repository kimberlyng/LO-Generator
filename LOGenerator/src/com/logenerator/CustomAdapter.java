package com.logenerator;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class CustomAdapter extends BaseAdapter {
	String[] loContentTitle;
	String[] loContentAuthor;
	String[] loContentDate;
	String[] loXML;
	String[] loTypes;
	//ArrayList<XmlPullParser> parsers;
	Context context;
	int[] imageId;
	View rowView;
	String msg;
	Holder holder;
	LinearLayout previewArea;
	
	
	private static LayoutInflater inflater = null;

	public CustomAdapter(MainActivity mainActivity, String[] loTitleList,
			String[] loAuthorList, String[] loDateList, String[] loXMLList, String[] loTypeList) {
		// TODO Auto-generated constructor stub
		loContentTitle = loTitleList;
		loContentAuthor = loAuthorList;
		loContentDate = loDateList; 
		loXML = loXMLList;
		loTypes = loTypeList;
		context = mainActivity;
		// imageId=prgmImages;
		inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return loContentTitle.length;
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	public class Holder {
		TextView tvLOTitle;
		TextView tvLOAuthor;
		TextView tvLODate;
		CheckBox checkbox;
		ImageView img;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		
		Holder holder = new Holder();
	
			rowView = inflater.inflate(R.layout.list_item, null);
			holder.tvLOTitle = (TextView) rowView.findViewById(R.id.textView1);
			holder.tvLOAuthor = (TextView) rowView.findViewById(R.id.textView2);
			holder.tvLODate = (TextView) rowView.findViewById(R.id.textView3);
			holder.checkbox = (CheckBox) rowView.findViewById(R.id.checkBox1);
			holder.img=(ImageView) rowView.findViewById(R.id.imgLOtype);
			holder.tvLOTitle.setText(loContentTitle[position]);
			holder.tvLOAuthor.setText("Author: " + loContentAuthor[position]);
			holder.tvLODate.setText("Date Created: " + loContentDate[position]);
			for (int i = 0; i < MainActivity.finalXmlList.size(); i++) {
				 if (loXML[position].equalsIgnoreCase(MainActivity.finalXmlList.get(i))){
					 holder.checkbox.setChecked(true);
				 }
			 }
			if(loTypes[position].equalsIgnoreCase("text"))
				holder.img.setImageResource(R.drawable.text);
			if(loTypes[position].equalsIgnoreCase("image"))
				holder.img.setImageResource(R.drawable.image);
			if(loTypes[position].equalsIgnoreCase("table"))
				holder.img.setImageResource(R.drawable.table);
			if(loTypes[position].equalsIgnoreCase("audio"))
				holder.img.setImageResource(R.drawable.audio);
			if(loTypes[position].equalsIgnoreCase("video"))
				holder.img.setImageResource(R.drawable.video);
			
			holder.checkbox.setOnCheckedChangeListener(new OnCheckedChangeListener() {
				@Override
				public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
					if(isChecked){
						addNode(position);						
					}else{
						removeNode(position);
					}
				}
			});
			
		 
			return rowView;
		
	}

	private void addNode(int id) {
		previewArea = MainActivity.previewArea;
		LinearLayout wrap = new LinearLayout(context);
		//add LayoutParams
		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
		
		wrap.setOrientation(LinearLayout.HORIZONTAL);
		wrap.setGravity(Gravity.CENTER_VERTICAL);

		//add textView
		TextView tvLOTitle = new TextView(context);
		tvLOTitle.setText(loContentTitle[id]);
		tvLOTitle.setLayoutParams(params);
		tvLOTitle.setTextSize(15);
		
		
		ImageView imageView = new ImageView(rowView.getContext());
		// setting image resource
		if(loTypes[id].equalsIgnoreCase("text"))
			imageView.setImageResource(R.drawable.text);
		if(loTypes[id].equalsIgnoreCase("image"))
			imageView.setImageResource(R.drawable.image);
		if(loTypes[id].equalsIgnoreCase("table"))
			imageView.setImageResource(R.drawable.table);
		if(loTypes[id].equalsIgnoreCase("audio"))
			imageView.setImageResource(R.drawable.audio);
		if(loTypes[id].equalsIgnoreCase("video"))
			imageView.setImageResource(R.drawable.video);
		
		// setting image position
		imageView.setLayoutParams(new LayoutParams(40,
				40));
		
		
		
		wrap.addView(imageView);
		wrap.addView(tvLOTitle);
		wrap.setId(id);
		//adding view to layout
		previewArea.addView(wrap);
		
		//ADD lo content xml to array
		MainActivity.finalXmlList.add(loXML[id]);
	}
	
	private void removeNode(int id) {
		previewArea = MainActivity.previewArea;
		//ImageView imageView = (ImageView) previewArea.findViewById(id);
		LinearLayout wrap = (LinearLayout) previewArea.findViewById(id);
		previewArea.removeView(wrap);
		
		//REMOVE lo content xml
		MainActivity.finalXmlList.remove(loXML[id]);

	}

}