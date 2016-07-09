package laffelrog.jumprangecalc;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.TextView;

import java.text.DecimalFormat;

public class Calculate extends AppCompatActivity implements View.OnClickListener {

    private static final String PREFERENCE_FILE = "preferenceFile";
    private static final String JUMP_RANGE_KEY  = "jumpRange";
    private static final String DISTANCE_SAG_KEY  = "distanceSag";

    private EditText tbJumpRange;
    private EditText tbDistanceSag;
    private TextView lblOptimalJumpRange;
    private DecimalFormat df;

    private JumpRangeCalculator jumpRangeCalc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calculate);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        setup();
        setStoredValues();
    }

    @Override
    public void onClick(View view) {
        String jumpRangeString = tbJumpRange.getText().toString();
        String distanceSagString = tbDistanceSag.getText().toString();

        if (jumpRangeString.isEmpty() || distanceSagString.isEmpty()) {
            setOutput(Color.RED, "Insert jump range and distance to Sag A*.");
            return;
        }

        double jumpRange = parseDouble(jumpRangeString);
        double distanceSag = parseDouble(distanceSagString);

        if (Double.isNaN(jumpRange) || jumpRange <= 1 || jumpRange > 500) {
            setOutput(Color.RED, "Jump range must be greater than 1 and less than 500 ly.");
            return;
        }

        if (Double.isNaN(distanceSag) || distanceSag < 0 || distanceSag > 50) {
            setOutput(Color.RED, "Distance to Sag A* must be a positive number less than 50'000 ly.");
            return;
        }

        double optimalJumpRange = jumpRangeCalc.calcOptJumpRange(jumpRange, distanceSag);
        setOutput(Color.GREEN, String.format("Optimal jump range: %s ly.",df.format(optimalJumpRange)));
    }

    @Override
    public void onStop() {
        super.onStop();
        storeLastValues();
    }

    private void setup() {
        tbJumpRange = (EditText) findViewById(R.id.tbJumpRange);
        tbDistanceSag = (EditText) findViewById(R.id.tbDistanceSag);
        lblOptimalJumpRange = (TextView) findViewById(R.id.lblOptimalJumpRange);
        df = new DecimalFormat("#.##");

        jumpRangeCalc = new JumpRangeCalculator();
    }

    private void setOutput(int red, String text) {
        lblOptimalJumpRange.setTextColor(red);
        lblOptimalJumpRange.setText(text);
    }

    private void storeLastValues() {
        SharedPreferences settings = getSharedPreferences(PREFERENCE_FILE, 0);
        SharedPreferences.Editor editor = settings.edit();

        if (jumpRangeCalc.getLastJumpRange() > 0) {
            editor.putFloat(JUMP_RANGE_KEY, (float)jumpRangeCalc.getLastJumpRange());
        }

        if (jumpRangeCalc.getLastDistanceSag() > 0) {
            editor.putFloat(DISTANCE_SAG_KEY, (float)jumpRangeCalc.getLastDistanceSag());
        }

        editor.apply();
    }

    private void setStoredValues() {
        SharedPreferences settings = getSharedPreferences(PREFERENCE_FILE, 0);

        if (settings != null) {
            double lastJumpRange = (double)settings.getFloat(JUMP_RANGE_KEY, 0);
            double lastDistanceSag = (double)settings.getFloat(DISTANCE_SAG_KEY, 0);

            if (lastJumpRange > 0) {
                tbJumpRange.setText(df.format(lastJumpRange));
            }

            if (lastDistanceSag > 0) {
                tbDistanceSag.setText(df.format(lastDistanceSag));
            }
        }
    }

    private double parseDouble(String number) {
        double d;

        try {
            d = Double.parseDouble(number);
        } catch (NumberFormatException ex) {
            d = Double.NaN;
        }

        return d;
    }
}
