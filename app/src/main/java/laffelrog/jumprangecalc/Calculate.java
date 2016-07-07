package laffelrog.jumprangecalc;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class Calculate extends AppCompatActivity  implements View.OnClickListener {

    public static final String PREFERENCE_FILE = "preferenceFile";
    private static final String JUMP_RANGE_KEY  = "jumpRange";
    private static final String DISTANCE_SAG_KEY  = "distanceSag";

    private EditText tbJumpRange;
    private EditText tbDistanceSag;
    private TextView lblOptimalJumpRange;
    private Button btCalculate;

    private JumpRangeCalculator jumpRangeCalc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calculate);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

       FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        setup();
    }

    private void setup() {
        tbJumpRange = (EditText) findViewById(R.id.tbJumpRange);
        tbDistanceSag = (EditText) findViewById(R.id.tbDistanceSag);
        lblOptimalJumpRange = (TextView) findViewById(R.id.lblOptimalJumpRange);
        btCalculate = (Button) findViewById(R.id.btCalculate);

        jumpRangeCalc = new JumpRangeCalculator();

        SharedPreferences settings = getSharedPreferences(PREFERENCE_FILE, 0);

        if (settings != null) {
            double lastJumpRange = (double)settings.getFloat(JUMP_RANGE_KEY, 0);
            double lastDistanceSag = (double)settings.getFloat(DISTANCE_SAG_KEY, 0);

            if (lastJumpRange > 0) {
                tbJumpRange.setText(String.format("%.2f", lastJumpRange));
            }

            if (lastDistanceSag > 0) {
                tbDistanceSag.setText(String.format("%.2f", lastDistanceSag));
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_calculate, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View view) {
        String jumpRangeString = tbJumpRange.getText().toString();
        String distanceSagString = tbDistanceSag.getText().toString();

        if (jumpRangeString.length() > 0 && distanceSagString.length() > 0) {
            double jumpRange = Double.parseDouble(jumpRangeString);
            double distanceSag = Double.parseDouble(distanceSagString);

            if (jumpRange > 0 && distanceSag >= 0) {
                double optimalJumpRange = jumpRangeCalc.calcOptJumpRange(jumpRange, distanceSag);
                lblOptimalJumpRange.setTextColor(Color.GREEN);
                lblOptimalJumpRange.setText(String.format("Optimal jump range: %.2f ly.", optimalJumpRange));
            } else {
                lblOptimalJumpRange.setTextColor(Color.RED);
                lblOptimalJumpRange.setText("Jump range and distance to Sag A* must be greater than 0.");
            }
        } else {
            lblOptimalJumpRange.setTextColor(Color.RED);
            lblOptimalJumpRange.setText("Insert jump range and distance to Sag A*.");
        }
    }

    @Override
    public void onStop() {
        super.onStop();

        SharedPreferences settings = getSharedPreferences(PREFERENCE_FILE, 0);
        SharedPreferences.Editor editor = settings.edit();

        editor.putFloat(JUMP_RANGE_KEY, (float)jumpRangeCalc.getLastJumpRange());
        editor.putFloat(DISTANCE_SAG_KEY, (float)jumpRangeCalc.getLastDistanceSag());
        editor.commit();
    }
}
