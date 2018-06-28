package yuanwei.hqx.com.hellochartdemo;

import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import lecho.lib.hellocharts.animation.ChartAnimationListener;
import lecho.lib.hellocharts.gesture.ContainerScrollType;
import lecho.lib.hellocharts.gesture.ZoomType;
import lecho.lib.hellocharts.listener.LineChartOnValueSelectListener;
import lecho.lib.hellocharts.model.Axis;
import lecho.lib.hellocharts.model.AxisValue;
import lecho.lib.hellocharts.model.Line;
import lecho.lib.hellocharts.model.LineChartData;
import lecho.lib.hellocharts.model.PointValue;
import lecho.lib.hellocharts.model.ValueShape;
import lecho.lib.hellocharts.model.Viewport;
import lecho.lib.hellocharts.util.ChartUtils;
import lecho.lib.hellocharts.view.Chart;
import lecho.lib.hellocharts.view.LineChartView;

public class LineChartActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_line_chart);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().add(R.id.container, new PlaceholderFragment()).commit();
        }
    }

    /**
     * A fragment containing a line chart.
     */
    public static class PlaceholderFragment extends Fragment {

        private LineChartView chart;
        private LineChartData data;
        private int numberOfLines = 4;
        private int maxNumberOfLines = 4;
        private int numberOfPoints = 50;

        float[][] randomNumbersTab = new float[maxNumberOfLines][numberOfPoints];

        private List<List<TemperatureBean>> listdata=new ArrayList<>();
        private boolean hasAxes = true;
        private boolean hasAxesNames = true;
        private boolean hasLines = true;
        private boolean hasPoints = true;
        private ValueShape shape = ValueShape.CIRCLE;
        private boolean isFilled = false;
        private boolean hasLabels = false;
        private boolean isCubic = false;
        private boolean hasLabelForSelected = false;
        private boolean pointsHaveDifferentColor;
        private boolean hasGradientToTransparent = false;
        private List<AxisValue> mAxisXValues = new ArrayList<AxisValue>();   //x轴方向的坐标数据
        private List<AxisValue> mAxisYValues = new ArrayList<AxisValue>();            //y轴方向的坐标数据
        private List<Float> distanceList = new ArrayList<Float>();
        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            setHasOptionsMenu(true);
            View rootView = inflater.inflate(R.layout.fragment_line_chart, container, false);

            chart = (LineChartView) rootView.findViewById(R.id.chart);
            chart.setOnValueTouchListener(new ValueTouchListener());

            chart.setInteractive(true);
            chart.setZoomType(ZoomType.HORIZONTAL);
            chart.setMaxZoom((float) 4);//最大方法比例
            chart.setContainerScrollEnabled(true, ContainerScrollType.HORIZONTAL);
            chart.setVisibility(View.VISIBLE);

            // Generate some random values.
            generateValues();

            generateData();

            // Disable viewport recalculations, see toggleCubic() method for more info.
            chart.setViewportCalculationEnabled(false);

            resetViewport();

            return rootView;
        }

        // MENU
        @Override
        public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
            inflater.inflate(R.menu.line_chart, menu);
        }

        @Override
        public boolean onOptionsItemSelected(MenuItem item) {
            int id = item.getItemId();
            if (id == R.id.action_reset) {
                reset();
                generateData();
                return true;
            }
            return super.onOptionsItemSelected(item);
        }

        private void generateValues() {
            listdata.clear();

            for(int i=0;i<4;i++) {
                Random random = new Random();
                int size = random.nextInt(100);
                 List<TemperatureBean> listBlood = new ArrayList<>();
                listBlood.add(new TemperatureBean("2017-5-1", size+i, size));
                listBlood.add(new TemperatureBean("2017-5-2", size, size));
                listBlood.add(new TemperatureBean("2017-5-3", size, size));
                listBlood.add(new TemperatureBean("2017-5-4", size, size));
                listBlood.add(new TemperatureBean("2017-5-5", size, size));
                listBlood.add(new TemperatureBean("2017-5-6", size, size));
                listBlood.add(new TemperatureBean("2017-5-7", size, 21));
                listBlood.add(new TemperatureBean("2017-5-8", size, 21));
                listBlood.add(new TemperatureBean("2017-5-9", size, 21));
                listBlood.add(new TemperatureBean("2017-5-10", size, 21));
                listBlood.add(new TemperatureBean("2017-5-11", size, 21));
                listBlood.add(new TemperatureBean("2017-5-12", size, 21));
                listBlood.add(new TemperatureBean("2017-5-13", size, 21));
                listBlood.add(new TemperatureBean("2017-5-14", size, 21));
                listdata.add(listBlood);
            }


        }

        private void reset() {
            numberOfLines = 4;

            hasAxes = true;
            hasAxesNames = true;
            hasLines = true;
            hasPoints = true;
            shape = ValueShape.CIRCLE;
            isFilled = false;
            hasLabels = false;
            isCubic = false;
            hasLabelForSelected = false;
            pointsHaveDifferentColor = false;

            chart.setValueSelectionEnabled(hasLabelForSelected);
            resetViewport();
        }

        private void resetViewport() {
            // Reset viewport height range to (0,100)

            final Viewport v = new Viewport(chart.getMaximumViewport());
            v.bottom = 0;
            v.top = 100;
            v.left = 0;
            v.right =15;
            chart.setMaximumViewport(v);

            v.left = 0;
            v.right =4;
            chart.setCurrentViewport(v);
        }

        private void generateData() {
            mAxisXValues.clear();
            List<Line> lines = new ArrayList<Line>();
            for (int i = 0; i < listdata.size(); ++i) {

                List<PointValue> values = new ArrayList<PointValue>();

                for (int j = 0; j < listdata.get(i).size(); ++j) {
                   values.add(new PointValue(j,listdata.get(i).get(j).getMaxTemp()));
                    String data = listdata.get(i).get(j).getData();
                    mAxisXValues.add(new AxisValue(j).setLabel(data));
                }

                Line line = new Line(values);
                line.setColor(ChartUtils.COLORS[i]);
                line.setShape(shape);
                line.setCubic(isCubic);
                line.setFilled(isFilled);
                line.setHasLabels(hasLabels);
                line.setHasLabelsOnlyForSelected(hasLabelForSelected);
                line.setHasLines(hasLines);
                line.setHasPoints(hasPoints);
                //line.setHasGradientToTransparent(hasGradientToTransparent);
                if (pointsHaveDifferentColor){
                    line.setPointColor(ChartUtils.COLORS[(i + 1) % ChartUtils.COLORS.length]);
                }
                lines.add(line);
            }

            data = new LineChartData(lines);

            if (hasAxes) {
                Axis axisX = new Axis();
                Axis axisY = new Axis().setHasLines(true);
                if (hasAxesNames) {
                    axisX.setName("时间");
                    axisY.setName("温度");
                }

                axisX.setTextSize(8);//设置字体的大小
                axisX.setHasTiltedLabels(false);//x坐标轴字体是斜的显示还是直的，true表示斜的
                axisX.setTextColor(Color.BLACK);//设置字体颜色
                axisX.setHasLines(true);//x轴的分割线
                axisX.setValues(mAxisXValues); //设置x轴各个坐标点名称

                data.setAxisXBottom(axisX);
                data.setAxisYLeft(axisY);
            } else {
                data.setAxisXBottom(null);
                data.setAxisYLeft(null);
            }

            data.setBaseValue(Float.NEGATIVE_INFINITY);
            chart.setLineChartData(data);

        }


        private class ValueTouchListener implements LineChartOnValueSelectListener {

            @Override
            public void onValueSelected(int lineIndex, int pointIndex, PointValue value) {
                Toast.makeText(getActivity(), "Selected: " + value.getY(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onValueDeselected() {
                // TODO Auto-generated method stub

            }

        }
    }
}
