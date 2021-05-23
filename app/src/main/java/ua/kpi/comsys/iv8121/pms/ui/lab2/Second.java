package ua.kpi.comsys.iv8121.pms.ui.lab2;

import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.google.android.material.chip.Chip;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import ua.kpi.comsys.iv8121.pms.R;

public class Second extends Fragment {

    private LineChart line;
    private PieChart pieChart;
    private Chip chip;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.l2_lay, container, false);

        line = root.findViewById(R.id.chart_line);
        pieChart = root.findViewById(R.id.chart_pie);

        List<Entry> chartData_main = new ArrayList<>();
        List<Entry> chartData_X = new ArrayList<>();
        List<Entry> chartData_Y = new ArrayList<>();

        setGraphicsOnChart(line, chartData_main, chartData_X, chartData_Y);

        chip = root.findViewById(R.id.chip4);

        chip.setOnClickListener(v -> {
            if (chip.getText() == "Pie Chart" || line.getVisibility() == View.VISIBLE) {
                line.setVisibility(View.GONE);
                pieChart.setVisibility(View.VISIBLE);
                chip.setText("Graphic");
            }
            else {
                pieChart.setVisibility(View.GONE);
                line.setVisibility(View.VISIBLE);
                chip.setText("Pie Chart");
            }
        });

        updateGraphic(root, line, chartData_main, chartData_X, chartData_Y);
        updateDiagram(root, pieChart);


        return root;
    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (chip.getText() == "Graphic") {
            line.setVisibility(View.GONE);
            pieChart.setVisibility(View.VISIBLE);
            chip.setText("Graphic");
        }
        else {
            pieChart.setVisibility(View.GONE);
            line.setVisibility(View.VISIBLE);
            chip.setText("Pie Chart");
        }
    }

    private void updateDiagram(View v, PieChart pieChart) {
        pieChart.getDescription().setEnabled(false);
        pieChart.setRotationEnabled(false);
        pieChart.setRotationAngle(0);
        pieChart.setHighlightPerTapEnabled(false);
        pieChart.getLegend().setCustom(new ArrayList<>());

        ArrayList<PieEntry> pieEntries = new ArrayList<>();
        ArrayList<Integer> typeAmountMap = new ArrayList<>();
        ArrayList<Integer> colors = new ArrayList<>();

        typeAmountMap.add(15);
        colors.add(Color.YELLOW);

        typeAmountMap.add(25);
        colors.add(Color.parseColor("#7A3B13"));

        typeAmountMap.add(45);
        colors.add(Color.GRAY);

        typeAmountMap.add(10);
        colors.add(Color.RED);

        typeAmountMap.add(5);
        colors.add(Color.parseColor("#730AB8"));

        for(int i=0; i<typeAmountMap.size(); i++){
            pieEntries.add(new PieEntry(typeAmountMap.get(i).floatValue(), ""));
        }

        PieDataSet pieDataSet = new PieDataSet(pieEntries, "");
        pieDataSet.setValueTextSize(0f);
        pieDataSet.setColors(colors);

        PieData pieData = new PieData(pieDataSet);
        pieData.setValueTextColor(Color.WHITE);

        pieChart.setData(pieData);
        pieChart.invalidate();
    }

    private void updateGraphic(View v, LineChart lineChart,
                               List<Entry> chartData_main,
                               List<Entry> chartData_X,
                               List<Entry> chartData_Y){
        SortedMap<Double, Double> graph_main = new TreeMap<>();
        SortedMap<Double, Double> graph_X = new TreeMap<>();
        SortedMap<Double, Double> graph_Y = new TreeMap<>();

        lineChart.getXAxis().setAxisMinimum(-3);
        lineChart.getAxisLeft().setAxisMinimum(-27);
        lineChart.getAxisRight().setAxisMinimum(-27);
        lineChart.getXAxis().setAxisMaximum(3);
        lineChart.getAxisLeft().setAxisMaximum(27);
        lineChart.getAxisRight().setAxisMaximum(27);

        lineChart.getAxisLeft().setCenterAxisLabels(true);

        for (double i = -3; i <= 3; i+=0.1) {
            drawGraph(lineChart, graph_X, chartData_X, 1, i, 0);
        }
        for (double i = -3; i <= 3; i+=0.1) {
            drawGraph(lineChart, graph_main, chartData_main, 0, i, i*i*i);
        }
        for (double i = -0.01; i <= 0.01; i+=0.01) {
            drawGraph(lineChart, graph_Y, chartData_Y, 2, i,
                    10000*i);
        }
    }

    private void setGraphicsOnChart(LineChart line,
                                    List<Entry> chartData_main,
                                    List<Entry> chartData_X,
                                    List<Entry> chartData_Y){
        line.getDescription().setEnabled(false);

        line.getLegend().setCustom(new ArrayList<>());

        setAxisParams(line.getXAxis());
        setAxisParams(line.getAxisLeft());
        setAxisParams(line.getAxisRight());

        line.getXAxis().setAxisMinimum(1f);
        line.getXAxis().setAxisMaximum(5f);

        LineDataSet chartDataSet_main = new LineDataSet(chartData_main, "Function");
        chartDataSet_main.setColor(ContextCompat.getColor(getContext(), R.color.cyan2));

        LineDataSet chartDataSet_X = new LineDataSet(chartData_X, "X");
        chartDataSet_X.setColor(Color.GRAY);

        LineDataSet chartDataSet_Y = new LineDataSet(chartData_Y, "Y");
        chartDataSet_Y.setColor(Color.GRAY);

        setDataSetParams(chartDataSet_main, 1.5f, 5f, false);
        setDataSetParams(chartDataSet_X, 1.5f, 5f, false);
        setDataSetParams(chartDataSet_Y, 1.5f, 5f, false);

        List<ILineDataSet> charDataSets = new ArrayList<>();
        charDataSets.add(chartDataSet_main);
        charDataSets.add(chartDataSet_X);
        charDataSets.add(chartDataSet_Y);

        LineData lineData = new LineData(charDataSets);
        line.setData(lineData);
    }

    private void drawGraph(LineChart l, Map<Double, Double> m, List data,
                           int index, double key, double value){
        m.put(key, value);

        data.clear();
        for (double v: m.keySet()) {
            data.add(new Entry((float)v, m.get(v).floatValue()));
        }
        LineDataSet set = (LineDataSet)l.getData().getDataSetByIndex(index);
        set.setValues(data);
        set.notifyDataSetChanged();
        l.getData().notifyDataChanged();
        l.notifyDataSetChanged();
        l.invalidate();
    }

    private void setAxisParams(AxisBase axis){
        axis.setDrawLabels(false);
        axis.setDrawAxisLine(false);
        axis.setDrawGridLinesBehindData(false);
        axis.setDrawLimitLinesBehindData(false);
        axis.setDrawGridLines(false);
    }

    private void setDataSetParams(LineDataSet dataSet,
                                  float lineWidth, float circleRadius, boolean drawCircle){
        dataSet.setLineWidth(lineWidth);
        dataSet.setCircleRadius(circleRadius);

        dataSet.setDrawCircleHole(true);

        dataSet.setFormLineWidth(1f);
        dataSet.setFormSize(15.f);

        dataSet.setValueTextSize(9f);
        dataSet.setDrawValues(false);
        dataSet.setDrawCircles(drawCircle);
    }
}