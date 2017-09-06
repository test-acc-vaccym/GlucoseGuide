package io.github.coffeegerm.materiallogbook.statistics;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Calendar;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.github.coffeegerm.materiallogbook.R;
import io.github.coffeegerm.materiallogbook.model.EntryItem;
import io.github.coffeegerm.materiallogbook.ui.activity.MainActivity;
import io.realm.Realm;
import io.realm.RealmResults;

import static io.github.coffeegerm.materiallogbook.utils.Utilities.getAverageGlucose;
import static io.github.coffeegerm.materiallogbook.utils.Utilities.getHighestGlucose;
import static io.github.coffeegerm.materiallogbook.utils.Utilities.getLowestGlucose;

/**
 * Created by David Yarzebinski on 7/28/2017.
 * <p>
 * Fragment used with Statistics ViewPager to show
 * the last three days of statistics
 */

public class ThreeDayStatisticsFragment extends Fragment {
    private static final String TAG = "ThreeDaysStatistics";

    @BindView(R.id.three_days_average)
    TextView average;
    @BindView(R.id.three_days_highest)
    TextView highest;
    @BindView(R.id.three_days_lowest)
    TextView lowest;
    @BindView(R.id.imgAvg)
    ImageView ivAvg;
    @BindView(R.id.imgUpArrow)
    ImageView ivUpArrow;
    @BindView(R.id.imgDownArrow)
    ImageView ivDownArrow;

    Realm realm;
    String pageTitle;
    int pageNumber;

    public static ThreeDayStatisticsFragment newInstance(int pageNumber, String pageTitle) {
        ThreeDayStatisticsFragment threeDayStatisticsFragment = new ThreeDayStatisticsFragment();
        Bundle args = new Bundle();
        args.putInt("pageNumber", pageNumber);
        args.putString("pageTitle", pageTitle);
        threeDayStatisticsFragment.setArguments(args);
        return threeDayStatisticsFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        pageTitle = getArguments().getString("pageTitle");
        pageNumber = getArguments().getInt("pageNumber");
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View threeDaysStatisticsView = inflater.inflate(R.layout.fragment_three_days_stats, container, false);
        ButterKnife.bind(this, threeDaysStatisticsView);
        realm = Realm.getDefaultInstance();
        setValues();
        setImages();
        return threeDaysStatisticsView;
    }

    private void setValues() {
        Date threeDaysAgo = getDateThreeDaysAgo();
        RealmResults<EntryItem> entriesFromLastThreeDays = realm.where(EntryItem.class).greaterThan("date", threeDaysAgo).greaterThan("bloodGlucose", 0).findAll();
        if (entriesFromLastThreeDays.size() == 0) {
            average.setText(R.string.dash);
            highest.setText(R.string.dash);
            lowest.setText(R.string.dash);
        } else {
            average.setText(String.valueOf(getAverageGlucose(threeDaysAgo)));
            highest.setText(String.valueOf(getHighestGlucose(threeDaysAgo)));
            lowest.setText(String.valueOf(getLowestGlucose(threeDaysAgo)));
        }
    }

    public Date getDateThreeDaysAgo() {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DATE, -3);
        return calendar.getTime();
    }

    private void setImages() {
        if (MainActivity.sharedPreferences.getBoolean("pref_dark_mode", false)) {
            ivAvg.setImageResource(R.drawable.ic_average_dark);
            ivUpArrow.setImageResource(R.drawable.ic_up_arrow_dark);
            ivDownArrow.setImageResource(R.drawable.ic_down_arrow_dark);
        }
    }

    @Override
    public void onDestroy() {
        realm.close();
        super.onDestroy();
    }
}