package com.logenerator;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import word.api.interfaces.IDocument;
import word.w2004.Document2004;
import word.w2004.Document2004.Encoding;
import word.w2004.elements.BreakLine;
import word.w2004.elements.Heading1;
import word.w2004.elements.Heading2;
import word.w2004.elements.HyperLink;
import word.w2004.elements.Image;
import word.w2004.elements.Paragraph;
import word.w2004.elements.Table;
import word.w2004.elements.tableElements.TableEle;
import word.w2004.style.HeadingStyle.Align;



public class MainActivity extends Activity {
	
	EditText etxTitle;
	TextView tvTitle;
	//Spinner spinLoContentType, spinFileType;
	Button btnGenerate;
	static LinearLayout previewArea;
	//public static ArrayList<XmlPullParser> finalParserList;
	public volatile boolean parsingComplete = true;
	public static ArrayList<String> finalXmlList;
	public ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_main);
        ListView lv;
        Resources res = getResources();
    	String [] loTitleList = res.getStringArray(R.array.loTitleList);
    	String [] loAuthorList= res.getStringArray(R.array.loAuthorList);
    	String [] loDateList= res.getStringArray(R.array.loDateList);
    	//String [] loFileType={"File Type","doc","html","pdf","txt"};
    	//String [] loContentType={"Content Type","Audio","Image","Table","Text","Video"};
    	String [] loXMLList=res.getStringArray(R.array.loXMLList);
    	String [] loTypeList=res.getStringArray(R.array.loTypeList);
    	
    	
    	finalXmlList = new ArrayList<String>();
    	
        lv = (ListView) findViewById(R.id.listView);
       
        etxTitle = (EditText) findViewById(R.id.editText1);
        //spinLoContentType = (Spinner) findViewById(R.id.spinner2);
        //spinFileType = (Spinner) findViewById(R.id.spinner1);
        previewArea = (LinearLayout) findViewById(R.id.previewArea);
        tvTitle = (TextView) findViewById(R.id.textViewTitle);
        btnGenerate = (Button) findViewById(R.id.btnGenerate);
        
        //ArrayAdapter<String> fileTypeAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, loFileType);
        //spinFileType.setAdapter(fileTypeAdapter);
        
        //ArrayAdapter<String> contentTypeAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, loContentType);
        //spinLoContentType.setAdapter(contentTypeAdapter);
        dialog = new ProgressDialog(MainActivity.this);
        
        etxTitle.addTextChangedListener(new TextWatcher() {
        	
            @Override
            public void onTextChanged(CharSequence s, int start, int before,
                    int count) {

            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                    int after) {
                
            }

            @Override
            public void afterTextChanged(Editable s) {

                tvTitle.setText(s.toString());
            }

        });
        
        btnGenerate.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if(etxTitle.getText().toString().isEmpty()){
					Toast.makeText(MainActivity.this, "Document Title is required.",
							Toast.LENGTH_LONG).show();
				}else{
					if(finalXmlList.size()<1){
						Toast.makeText(MainActivity.this, "Select at lease one Learning Object.",
								Toast.LENGTH_LONG).show();
					}else{
						//parse xml files and generate doc
						try {
							asyncGenerateDoc async = new asyncGenerateDoc();
							async.execute();
						} catch (Exception e){
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				}
			}
				
		});
        
        //createParsers();       
        
        lv.setAdapter(new CustomAdapter(this, loTitleList,loAuthorList,loDateList,loXMLList,loTypeList));
        

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


	private void saveFile(String docString) {
		File root = android.os.Environment.getExternalStorageDirectory();
		File dir = new File(root.getAbsolutePath() + "/download");
		dir.mkdirs();

		try {
			String fileName = new SimpleDateFormat("yyyyMMddhhmm").format(new Date());
			File file = new File(dir, "LO"+fileName+".doc");
			FileOutputStream f = new FileOutputStream(file);			
			
			PrintWriter writer = null;
			writer = new PrintWriter(file);
			writer.println(docString);
			writer.close();
			f.close();
			
			Toast.makeText(this, "File generated successfully",
					Toast.LENGTH_LONG).show();

		} catch (FileNotFoundException e) {
			e.printStackTrace();
			Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
		}
		
		
	}
	
	private class asyncGenerateDoc extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
        	if(Util.isConnected(MainActivity.this)){
	        	IDocument myDoc = new Document2004();
	        	
	        	// Heading
	            myDoc.addEle(Heading1.with(etxTitle.getText().toString()).withStyle()
	                    .align(Align.CENTER).create());
	            myDoc.addEle(BreakLine.times(1).create());
	            
	        	int event;
	            String text=null;
	            String contentType=null;
	            for (int i = 0; i < finalXmlList.size(); i++) {
	            	XmlPullParserFactory pullParserFactory;
	            	Table tbl = new Table();
	            	List<String> rowString = new ArrayList<String>();
	            	
	            	try {
						pullParserFactory = XmlPullParserFactory.newInstance();
						XmlPullParser myParser = pullParserFactory.newPullParser();
		                InputStream in_s0 = getApplicationContext().getAssets().open(finalXmlList.get(i));
		                myParser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
		                myParser.setInput(in_s0, null);
				
	                    event = myParser.getEventType();
	                    
	                    while (event != XmlPullParser.END_DOCUMENT) {
	                       String value=myParser.getName();
	                       
	                    
	                       switch (event){
	                          case XmlPullParser.START_TAG:
	                        	  if(value.equalsIgnoreCase("lo_content"))
	                        		  contentType=myParser.getAttributeValue(null, "type");
	                        	  
	                        	  if(value.equalsIgnoreCase("table_header")||
	                        			  value.equalsIgnoreCase("table_row")){
	                        		  rowString.clear();                        	  }
	                          break;
	                       
	                          case XmlPullParser.TEXT:
	                          text = myParser.getText();
	                          break;
	                       
	                          case XmlPullParser.END_TAG:
	                          if(!value.isEmpty()){
	                        	  if(contentType.equalsIgnoreCase("text")){
	    	    	                  if(value.equals("title")){
	    	    	                	  myDoc.addEle(Heading2.with(text).create());
	    	    	                  }
	    	    	               
	    	    	                  else if(value.equals("paragraph")){
	    	    	                	  myDoc.addEle(Paragraph
	    	    	                              .with(text)
	    	    	                              .create());
	    	    	                	  myDoc.addEle(BreakLine.times(1).create());
	    	    	                  }
	
	    	    	               
	    	    	                  else{
	    	    	                  }
	                        	  }
	                        	  else if(contentType.equalsIgnoreCase("image")){
	                        		  if(value.equals("location")){
	
	                        			  Picture pic = Picture.from_WEB_URL(text).create();
	                        			  
	                        			  myDoc.addEle(pic);
	                        			  myDoc.addEle(BreakLine.times(1).create());
	    	    	                  }
	                        	  }
	                        	  else if (contentType
											.equalsIgnoreCase("table")) {
	
										if (value.equals("title")) {
											myDoc.addEle(Heading2.with(text)
													.create());
										}
	
										if (value.equals("table_column")) {
	
											rowString.add(text);
										}
	
										if (value.equals("table_header")) {
											String[] rowArray = new String[rowString
													.size()];
											rowArray = rowString.toArray(rowArray);
											tbl.addTableEle(TableEle.TH, rowArray);
										}
	
										if (value.equals("table_row")) {
											String[] rowArray = new String[rowString
													.size()];
											rowArray = rowString.toArray(rowArray);
											tbl.addTableEle(TableEle.TD, rowArray);
										}
										if (value.equals("lo_content")) {
											myDoc.addEle(tbl);
											myDoc.addEle(BreakLine.times(1)
													.create());
										}
									}
	                        	  else if(contentType.equalsIgnoreCase("video")){
										if (value.equals("title")) {
											myDoc.addEle(Heading2.with(text)
													.create());
										}
										if (value.equals("location")) {
											myDoc.addEle(HyperLink.with(text,"Video").create());
											myDoc.addEle(BreakLine.times(2)
													.create());
										}
	                        	  }
	                        	  
	                        	  else if(contentType.equalsIgnoreCase("audio")){
										if (value.equals("title")) {
											myDoc.addEle(Heading2.with(text)
													.create());
										}
										if (value.equals("location")) {
											myDoc.addEle(HyperLink.with(text,"Audio").create());
											myDoc.addEle(BreakLine.times(2)
													.create());
										}
	                        	  }
	                          }
	                          break;
	                       }
	                       event = myParser.next();
	                    }
	                    parsingComplete = false;
	                 }
	                 
	                 catch (Exception e) {
	                    e.printStackTrace();
	                 }
	    		}
	            
	        	
	            // myDoc.setPageOrientationLandscape();
	            // default is Portrait be can be changed.
	            myDoc.encoding(Encoding.UTF_8); //or ISO8859-1. Default is UTF-8
	
	           // myDoc.addEle(BreakLine.times(1).create()); // this is one breakline
	
	            String myWord = myDoc.getContent();
	            return myWord;
	            //TestUtils.createLocalDoc(myDoc.getContent());
        	}else{
        		return "";
        	}
        }

        @Override
        protected void onPostExecute(String result) {
            if(!result.isEmpty())
            	saveFile(result);
            
        	if (dialog.isShowing()) {
                dialog.dismiss();
            }
        	
        	Toast.makeText(MainActivity.this, "Please check your internet connection", Toast.LENGTH_LONG).show();

        }

        @Override
        protected void onPreExecute() {
        	dialog.setMessage("Please wait. Creating Document...");
            dialog.show();
        }

        @Override
        protected void onProgressUpdate(Void... values) {}
    }
	
	
	
	
	

}
