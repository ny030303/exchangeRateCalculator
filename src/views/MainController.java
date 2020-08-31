package views;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import domain.CashVO;
import domain.GraphVO;
import domain.NationVO;
import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.StackedAreaChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import parsePack.Parser;

public class MainController {

	@FXML
	private ComboBox<NationVO> box1;

	@FXML
	private ComboBox<NationVO> box2;

	@FXML
	private TextField input1;

	@FXML
	private TextField input2;

	private ObservableList<NationVO> nationList;
	private Parser parser;

	@FXML
	StackedAreaChart<Number, Number> lineChart;

	@FXML
	private void initialize() {

		final int MIN = 0;
		final int MAX = 1;

		parser = new Parser();
		nationList = FXCollections.observableArrayList();

		try {
			File dataFile = new File(getClass().getResource("/resources/data.txt").getFile());
			FileInputStream fis = new FileInputStream(dataFile);
			InputStreamReader isr = new InputStreamReader(fis);
			BufferedReader br = new BufferedReader(isr);

			List<CashVO> list = parser.getCashData();

			while (true) {
				String line = br.readLine();
				if (line == null)
					break;
				String[] lines = line.split("#");
				NationVO temp = new NationVO(lines[0], lines[1]);
				nationList.add(temp);
			}

			box1.setItems(nationList);
			box2.setItems(nationList);

			box1.setValue(nationList.get(0));
			box2.setValue(nationList.get(1));

		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("데이터 파일 불러오는 중 오류 발생");
			Alert alert = new Alert(AlertType.ERROR);
			alert.setTitle("Error Dialog");
			alert.setHeaderText("Look, an Error Dialog");
			alert.setContentText("데이터 파일 불러오는 중 오류 발생");

			alert.showAndWait();
		}

		ChangeListener<NationVO> funcBox1Changed = (ob, o, v) -> {
//			System.out.println("이전 값 " + o + ", 새로운 값 : " + v);
			List<CashVO> list = parser.getCashData();

			String nationName = v.getName();
			List<GraphVO> graphList = new ArrayList<GraphVO>();
			for (CashVO data : list) {
				if(nationName.length() > data.getName().length()) {
					continue;
				}
				String dataName = data.getName().substring(0, nationName.length());
//				System.out.println(dataName + ", " + nationName);
				if (dataName.equals(nationName)) {
					String url = data.getUrlLink();
					int idx = url.lastIndexOf("?");
					url = url.substring(idx + 1);

					// 1개에 10일이니깐 2달분 60일 데이터 가져오기
					for (int i = 0; i < 6; i++) {
						String graphUrl = "https://finance.naver.com/marketindex/exchangeDailyQuote.nhn?" + url;
						graphUrl += String.format("&page=%d", i + 1);
//						System.out.println(graphUrl);
						parser.getGraphData(graphUrl).forEach(graphItem -> graphList.add(graphItem));
					}
					break;
				}
			}
			lineChart.getData().clear();
			XYChart.Series<Number, Number> series = new XYChart.Series<Number, Number>();

			series.setName(nationName);
			graphList.sort((a, b) -> a.getDate().compareTo(b.getDate()));

			double dblMinMax[] = { Double.MAX_VALUE, Double.MIN_VALUE };

			graphList.forEach(graphVo -> {
//				System.out.println(graphVo);
				int nIdx = graphList.indexOf(graphVo);
				double dblValue = Double.parseDouble(graphVo.getBias().replaceAll(",", ""));
				if (dblValue < dblMinMax[MIN])
					dblMinMax[MIN] = dblValue;
				if (dblValue > dblMinMax[MAX])
					dblMinMax[MAX] = dblValue;
				series.getData().add(new XYChart.Data<Number, Number>(nIdx, dblValue));
			});
			lineChart.getData().add(series);

			double yTickSpace = Math.floor(dblMinMax[MIN]) / 100;

			// Y축 그래프 값을 설정
			NumberAxis yAxis = (NumberAxis) lineChart.getYAxis();
			yAxis.setForceZeroInRange(false);
			yAxis.setAutoRanging(false);
			yAxis.setTickUnit(yTickSpace);
			yAxis.setTickLength(yTickSpace);
			yAxis.setLowerBound(dblMinMax[MIN] - yTickSpace);
			yAxis.setUpperBound(dblMinMax[MAX] + yTickSpace);
//			System.out.println("MIN: " + dblMinMax[MIN] + ", MAX: " + dblMinMax[MAX]);

			// X축 그래프 값을 설정
			NumberAxis xAxis = (NumberAxis) lineChart.getXAxis();
			xAxis.setTickUnit(1.0);
			xAxis.setTickLength(1.0);
			xAxis.setUpperBound(graphList.size() - 1);
			// x축 그래프의 Tick값을 글자로 변경
			xAxis.setTickLabelFormatter(new NumberAxis.DefaultFormatter(xAxis) {
				@Override
				public String toString(Number object) {
					String tickDate = (graphList.size() > 0) ? graphList.get(object.intValue()).getDate() : "";
					if (tickDate.length() == 10)
						return tickDate.substring(5).replace('.', '/');
					return tickDate;
				}
			});
		};

		// 숫자만 쓸 수 있게
		input1.textProperty().addListener((observable, oldValue, newValue) -> {
//			System.out.println("oldValue: " + oldValue);
//			System.out.println("newValue: " + newValue);
			if (!newValue.matches("\\d*")) {
				if (newValue.charAt(newValue.length() - 1) != '.') {
					input1.setText(newValue.replaceAll("[^\\d]", ""));
				}
			}
		});

		// 콤보박스 1번의 value값이 변경되면 그래프 그리는 이벤트리스너 추가
		box1.valueProperty().addListener(funcBox1Changed);
		// 콤보박스가 선택되도록 이벤트 발생시킴 (초기값: 미국 그래프로 지정)
		funcBox1Changed.changed(null, nationList.get(0), nationList.get(1));
	}

	public void getData() {
		List<CashVO> list = parser.getCashData();

		NationVO value = box1.getValue();
		NationVO value2 = box2.getValue();

		if (value == null || value2 == null || input1.getCharacters().length() == 0) {
			Alert alert = new Alert(AlertType.WARNING);
			alert.setTitle("Warning");
			alert.setHeaderText("비어있는 입력 칸이 있습니다!");
			alert.setContentText("다시 한 번 확인해 주세요.");

			alert.showAndWait();
		} else {
			List<String> nationNames = Arrays.asList(value.getName(), value2.getName());
			double dblBias[] = { 1.0, 1.0 };

			for (CashVO data : list) {
				// System.out.println(data);
				nationNames.forEach((nationName) -> {
					String dataName = data.getName().substring(0, nationName.length());
					if (dataName.equals(nationName)) {
//						System.out.println(data);
						int idx = nationNames.indexOf(nationName);
						dblBias[idx] = Double.parseDouble(data.getBias().replaceAll(",", ""));
						if (nationName.equals("일본"))
							dblBias[idx] /= 100;
					}
				});
			}

			int inputValue1 = Integer.parseInt(input1.getText());

			double krMoney = inputValue1 * dblBias[0]; // 원화

			input2.setText(String.format("%.2f", (krMoney / dblBias[1])));

//			System.out.println("첫번째 input : " + box1.getValue());
//			System.out.println("두번째 input : " + box2.getValue());
		}
	}
}
