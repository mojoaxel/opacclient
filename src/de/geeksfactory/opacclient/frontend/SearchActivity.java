package de.geeksfactory.opacclient.frontend;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.NavUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.WazaBe.HoloEverywhere.widget.Spinner;
import com.actionbarsherlock.view.MenuItem;

import de.geeksfactory.opacclient.R;
import de.geeksfactory.opacclient.zxing.IntentIntegrator;
import de.geeksfactory.opacclient.zxing.IntentResult;

public class SearchActivity extends OpacActivity {

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent idata) {
		super.onActivityResult(requestCode, resultCode, idata);

		// Barcode
		IntentResult scanResult = IntentIntegrator.parseActivityResult(
				requestCode, resultCode, idata);
		if (resultCode != RESULT_CANCELED && scanResult != null) {
			Log.i("scanned", scanResult.getContents());
			if (scanResult.getContents() == null)
				return;
			if (scanResult.getContents().length() < 3)
				return;
			((EditText) SearchActivity.this.findViewById(R.id.etISBN))
					.setText(scanResult.getContents());
		}
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		SharedPreferences sp = PreferenceManager
				.getDefaultSharedPreferences(this);

		if (getIntent().getBooleanExtra("barcode", false)) {
			IntentIntegrator integrator = new IntentIntegrator(
					SearchActivity.this);
			integrator.initiateScan();
		}

		// Fill combo boxes

		Spinner cbZst = (Spinner) findViewById(R.id.cbZweigstelle);
		String[] zst = sp.getString("opac_zst", ":Alle").split("~");
		if (zst[0].startsWith(": ")) {
			zst[0] = zst[0].substring(2);
		}
		ArrayAdapter<String> zst_adapter = new ArrayAdapter<String>(this,
				R.layout.simple_spinner_item, zst);
		zst_adapter
				.setDropDownViewResource(R.layout.simple_spinner_dropdown_item);
		cbZst.setAdapter(zst_adapter);

		Spinner cbMg = (Spinner) findViewById(R.id.cbMediengruppe);
		String[] mg = sp.getString("opac_mg", ":Alle").split("~");
		if (mg[0].startsWith(": ")) {
			mg[0] = mg[0].substring(2);
		}
		ArrayAdapter<String> mg_adapter = new ArrayAdapter<String>(this,
				R.layout.simple_spinner_item, mg);
		mg_adapter
				.setDropDownViewResource(R.layout.simple_spinner_dropdown_item);
		cbMg.setAdapter(mg_adapter);

		ArrayAdapter<CharSequence> order_adapter = ArrayAdapter
				.createFromResource(this, R.array.orders,
						R.layout.simple_spinner_item);
		order_adapter
				.setDropDownViewResource(R.layout.simple_spinner_dropdown_item);
		((Spinner) SearchActivity.this.findViewById(R.id.cbOrder))
				.setAdapter(order_adapter);

		ImageView ivBarcode = (ImageView) findViewById(R.id.ivBarcode);
		ivBarcode.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				IntentIntegrator integrator = new IntentIntegrator(
						SearchActivity.this);
				integrator.initiateScan();
			}
		});
		// Go

		Button btGo = (Button) findViewById(R.id.btStartsearch);
		btGo.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				String zst = ((String) ((Spinner) SearchActivity.this
						.findViewById(R.id.cbZweigstelle)).getSelectedItem());
				if (zst.contains(":")) {
					zst = zst.split(":", 2)[0];
				} else {
					zst = "";
				}
				String mg = ((String) ((Spinner) SearchActivity.this
						.findViewById(R.id.cbMediengruppe)).getSelectedItem());
				if (mg.contains(":")) {
					mg = mg.split(":", 2)[0];
				} else {
					mg = "";
				}
				Intent myIntent = new Intent(SearchActivity.this,
						SearchResultsActivity.class);
				myIntent.putExtra("titel", ((EditText) SearchActivity.this
						.findViewById(R.id.etTitel)).getEditableText()
						.toString());
				myIntent.putExtra("verfasser", ((EditText) SearchActivity.this
						.findViewById(R.id.etVerfasser)).getEditableText()
						.toString());
				myIntent.putExtra("schlag_a", ((EditText) SearchActivity.this
						.findViewById(R.id.etSchlagA)).getEditableText()
						.toString());
				myIntent.putExtra("schlag_b", ((EditText) SearchActivity.this
						.findViewById(R.id.etSchlagB)).getEditableText()
						.toString());
				myIntent.putExtra("zst", zst);
				myIntent.putExtra("mg", mg);
				myIntent.putExtra("isbn", ((EditText) SearchActivity.this
						.findViewById(R.id.etISBN)).getEditableText()
						.toString());
				myIntent.putExtra("jahr_von", ((EditText) SearchActivity.this
						.findViewById(R.id.etJahrVon)).getEditableText()
						.toString());
				myIntent.putExtra("jahr_bis", ((EditText) SearchActivity.this
						.findViewById(R.id.etJahrBis)).getEditableText()
						.toString());
				myIntent.putExtra("systematik", ((EditText) SearchActivity.this
						.findViewById(R.id.etSystematik)).getEditableText()
						.toString());
				myIntent.putExtra("ikr", ((EditText) SearchActivity.this
						.findViewById(R.id.etInteressenkreis))
						.getEditableText().toString());
				myIntent.putExtra("verlag", ((EditText) SearchActivity.this
						.findViewById(R.id.etVerlag)).getEditableText()
						.toString());
				myIntent.putExtra(
						"order",
						(((Integer) ((Spinner) SearchActivity.this
								.findViewById(R.id.cbOrder))
								.getSelectedItemPosition()) + 1)
								+ "");
				startActivity(myIntent);
			}
		});

		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			NavUtils.navigateUpFromSameTask(this);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

}