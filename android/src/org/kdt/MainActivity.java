package org.kdt;

import java.nio.charset.Charset;
import java.util.LinkedList;
import java.util.List;

import org.kdt.kanbandatatracker.R;

import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.os.Bundle;
import android.os.Parcelable;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

public class MainActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	@Override
    protected void onNewIntent(Intent intent) {
		List<String> payloads = tagToStrings(intent);
		
		for (String text : payloads) {
			final TextView helloWorldText = getSnapshotControl();
			//each activity is new and doesn't just keep a running list
		    helloWorldText.append("\n"+text);
		}
	}
	
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("snapshot", getSnapshotControl().getText().toString());
    }
    
    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        getSnapshotControl().setText(savedInstanceState.getString("snapshot"));
    }
    
    public void saveSnapshot(View view) {
        getSnapshotControl().setText("");
    }    
    
    private TextView getSnapshotControl() {
       return (TextView) findViewById(R.id.snapshot_editText);
    }

    private List<String> tagToStrings(Intent intent) {
		List<String> textPayloads = new LinkedList<String>();
		
		if (NfcAdapter.ACTION_NDEF_DISCOVERED.equals(intent.getAction())) {
			Parcelable[] rawMsgs = intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);
			textPayloads.addAll(rawToStrings(rawMsgs));
	    }
		
		return textPayloads;
	}

	private List<String> rawToStrings(Parcelable[] rawMsgs) {
		List<String> textPayloads = new LinkedList<String>();
		
		if (rawMsgs != null) {
		    for (Parcelable rawMessage : rawMsgs) {
		        NdefMessage message = (NdefMessage) rawMessage;

		        textPayloads.addAll(messageToStrings(message));
		    }
		}
		
		return textPayloads;
	}

	private List<String> messageToStrings(NdefMessage message) {
		List<String> textPayloads = new LinkedList<String>();
		
		NdefRecord[] records = message.getRecords();
		for (NdefRecord record : records) {
			String mimeType = decodePayload(record.getType());
			String text = decodePayload(record.getPayload());
			
			if (mimeType.endsWith("task")) {
				text = "\t\t" + text;
			}
			
			textPayloads.add(text);
		}
		
		return textPayloads;
	}

	private String decodePayload(byte[] payload) {
		return new String(payload, Charset.forName("US-ASCII"));
	}

}
