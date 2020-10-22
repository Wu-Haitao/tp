package seedu.duke.command;

import org.apache.poi.xddf.usermodel.chart.AxisPosition;
import org.apache.poi.xddf.usermodel.chart.ChartTypes;
import org.apache.poi.xddf.usermodel.chart.XDDFChartAxis;
import org.apache.poi.xddf.usermodel.chart.XDDFChartData;
import org.apache.poi.xddf.usermodel.chart.XDDFChartLegend;
import org.apache.poi.xddf.usermodel.chart.XDDFDataSource;
import org.apache.poi.xddf.usermodel.chart.XDDFDataSourcesFactory;
import org.apache.poi.xddf.usermodel.chart.XDDFNumericalDataSource;
import org.apache.poi.xddf.usermodel.chart.XDDFValueAxis;
import org.apache.poi.xssf.usermodel.XSSFChart;
import org.apache.poi.xssf.usermodel.XSSFClientAnchor;
import org.apache.poi.xssf.usermodel.XSSFDrawing;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTBoolean;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTLineChart;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTLineSer;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTPlotArea;
import seedu.duke.SpendingList;
import seedu.duke.Ui;
import seedu.duke.category.Item;
import seedu.duke.exceptions.InvalidCommandException;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.TreeMap;

public class DrawCommand extends Command {
    private final DateFormatter dateFormatter = new DateFormatter();
    private String timePeriod;

    public DrawCommand() throws InvalidCommandException {
        timePeriod = "";
    }

    public DrawCommand(String year) throws InvalidCommandException {
        timePeriod = year;
    }

    public DrawCommand(String year, String month) throws InvalidCommandException {
        String convertedMonth = dateFormatter.changeMonthFormat(month);
        if (convertedMonth == null) {
            throw new InvalidCommandException();
        } else {
            timePeriod = year + "-" + convertedMonth;
        }
    }

    @Override
    public void execute(SpendingList spendingList, Ui ui) throws IOException {
        XSSFWorkbook workbook = new XSSFWorkbook();
        XSSFSheet sheet0 = workbook.createSheet("Sheet 0");
        XSSFSheet sheet1 = workbook.createSheet("Sheet 1");
        sheet0.setDefaultColumnWidth(5);
        sheet1.setDefaultColumnWidth(5);
        ArrayList<Item> items = new ArrayList<Item>();
        SpendingList targetSpendingList = new SpendingList(items);
        for (int i = 0; i < spendingList.getListSize(); i++) {
            if (spendingList.getItem(i).getDate().startsWith(timePeriod)) {
                targetSpendingList.getSpendingList().add(spendingList.getItem(i));
            }
        }
        if (targetSpendingList.getListSize() != 0) {
            TreeMap<Integer, Double> dateMap;
            if (timePeriod.equals("")) {
                dateMap = getYearMap(targetSpendingList);
            } else if (timePeriod.length() == 4) {
                dateMap = getMonthMap(targetSpendingList);
            } else {
                dateMap = getDayMap(targetSpendingList);
            }
            Integer[] dates = dateMap.keySet().toArray(new Integer[0]);
            Double[] amountsForDates = dateMap.values().toArray(new Double[0]);
            drawChart(sheet0, dates, amountsForDates, 0, 0, 10, 15);

            TreeMap<String, Double> categoryMap = getCategoryMap(targetSpendingList);
            String[] categories = categoryMap.keySet().toArray(new String[0]);
            Double[] amountsForCategories = categoryMap.values().toArray(new Double[0]);
            drawChart(sheet1, categories, amountsForCategories, 0, 0, 10, 12);

            FileOutputStream fileOut = new FileOutputStream("Charts.xlsx");
            workbook.write(fileOut);
            fileOut.flush();
            fileOut.close();
        }
    }

    private TreeMap<String, Double> getCategoryMap(SpendingList spendingList) {
        TreeMap<String, Double> map = new TreeMap<>();
        for (int i = 0; i < spendingList.getListSize(); i++) {
            Item item = spendingList.getItem(i);
            if (map.containsKey(item.getCategory())) {
                map.replace(item.getCategory(), map.get(item.getCategory()) + item.getAmount());
            } else {
                map.put(item.getCategory(), item.getAmount());
            }
        }
        return map;
    }

    private TreeMap<Integer, Double> getYearMap(SpendingList spendingList) {
        TreeMap<Integer, Double> map = new TreeMap<>();
        int minYear = Integer.parseInt(spendingList.getItem(0).getDate());
        int maxYear = minYear;
        for (int i = 0; i < spendingList.getListSize(); i++) {
            Item item = spendingList.getItem(i);
            int year = Integer.parseInt(item.getDate().substring(0, 4));
            if (year > maxYear) {
                maxYear = year;
            }
            if (year < minYear) {
                minYear = year;
            }
            if (map.containsKey(year)) {
                map.replace(year, map.get(year) + item.getAmount());
            } else {
                map.put(year, item.getAmount());
            }
        }
        for (int i = minYear; i <= maxYear; i++) {
            map.putIfAbsent(i, 0.0);
        }
        return map;
    }

    private TreeMap<Integer, Double> getMonthMap(SpendingList spendingList) {
        TreeMap<Integer, Double> map = new TreeMap<>();
        final int minMonth = 1;
        final int maxMonth = 12;
        for (int i = 0; i < spendingList.getListSize(); i++) {
            Item item = spendingList.getItem(i);
            int month = Integer.parseInt(item.getDate().substring(5, 7));
            if ((month < minMonth) || (month > maxMonth)) {
                continue;
            }
            if (map.containsKey(month)) {
                map.replace(month, map.get(month) + item.getAmount());
            } else {
                map.put(month, item.getAmount());
            }
        }
        for (int i = minMonth; i <= maxMonth; i++) {
            map.putIfAbsent(i, 0.0);
        }
        return map;
    }

    private TreeMap<Integer, Double> getDayMap(SpendingList spendingList) {
        TreeMap<Integer, Double> map = new TreeMap<>();
        final int minDay = 1;
        final int maxDay = 12;
        for (int i = 0; i < spendingList.getListSize(); i++) {
            Item item = spendingList.getItem(i);
            int day = Integer.parseInt(item.getDate().substring(8, 10));
            if ((day < minDay) || (day > maxDay)) {
                continue;
            }
            if (map.containsKey(day)) {
                map.replace(day, map.get(day) + item.getAmount());
            } else {
                map.put(day, item.getAmount());
            }
        }
        for (int i = minDay; i <= maxDay; i++) {
            map.putIfAbsent(i, 0.0);
        }
        return map;
    }

    private void drawChart(XSSFSheet sheet, String[] category, Double[] values, int x1, int y1, int x2, int y2) {
        XSSFDrawing drawing = sheet.createDrawingPatriarch();
        XSSFClientAnchor anchor = drawing.createAnchor(0, 0, 0, 0, x1, y1, x2, y2);
        XSSFChart chart = drawing.createChart(anchor);
        XDDFChartLegend legend = chart.getOrAddLegend();
        legend.setPosition(org.apache.poi.xddf.usermodel.chart.LegendPosition.TOP_RIGHT);
        XDDFDataSource<String> cat = XDDFDataSourcesFactory.fromArray(category);
        XDDFNumericalDataSource<Double> val = XDDFDataSourcesFactory.fromArray(values);
        XDDFChartData chartData = chart.createData(ChartTypes.PIE, null, null);
        chartData.addSeries(cat, val);
        chart.plot(chartData);
    }

    private void drawChart(XSSFSheet sheet, Integer[] category, Double[] values, int x1, int y1, int x2, int y2) {
        XSSFDrawing drawing = sheet.createDrawingPatriarch();
        XSSFClientAnchor anchor = drawing.createAnchor(0, 0, 0, 0, x1, y1, x2, y2);
        XSSFChart chart = drawing.createChart(anchor);
        XDDFDataSource<Integer> cat = XDDFDataSourcesFactory.fromArray(category);
        XDDFNumericalDataSource<Double> val = XDDFDataSourcesFactory.fromArray(values);
        XDDFChartAxis chartAxis = chart.createCategoryAxis(AxisPosition.LEFT);
        XDDFValueAxis valueAxis = chart.createValueAxis(AxisPosition.BOTTOM);
        XDDFChartData chartData = chart.createData(ChartTypes.LINE, chartAxis, valueAxis);
        chartData.addSeries(cat, val);
        chart.plot(chartData);
        CTPlotArea plotArea = chart.getCTChart().getPlotArea();
        for (CTLineChart ch : plotArea.getLineChartList()) {
            for (CTLineSer ser : ch.getSerList()) {
                CTBoolean ctBool = CTBoolean.Factory.newInstance();
                ctBool.setVal(false);
                ser.setSmooth(ctBool);
            }
        }
    }
}