import java.util.Iterator;
import javafx.stage.Stage;
import java.text.DecimalFormat;
import javafx.scene.shape.Circle;
import java.util.UUID;
import javafx.scene.control.Alert;
import javafx.geometry.Pos;
import javafx.scene.control.ToggleGroup;
import javafx.application.Application;
import javafx.scene.shape.QuadCurve;
import java.util.HashMap;
import javafx.scene.layout.Pane;
import javafx.scene.Group;
import javafx.beans.property.DoubleProperty;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Font;
import javafx.scene.control.Toggle;
import javafx.scene.control.TextField;
import javafx.event.ActionEvent;
import java.util.TimerTask;
import javafx.scene.control.RadioButton;
import java.util.Optional;
import java.util.Random;
import javafx.scene.control.Label;
import java.util.Timer;
import javafx.scene.layout.BackgroundFill;
import javafx.geometry.Insets;
import java.util.HashSet;
import java.util.PriorityQueue;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import java.util.ArrayList;
import javafx.scene.layout.Background;
import javafx.beans.binding.DoubleBinding;
import javafx.scene.paint.Color;
import javafx.event.EventHandler;
import javafx.scene.input.MouseEvent;
import javafx.application.Platform;
import javafx.scene.Scene;

class GFDot extends Dot implements Comparable<GFDot> {

	StateController stateController;
	Field field;

	GFDot(double inX, double inY, double inRadius, StateController stateController, Field field) {
		super(inX, inY, inRadius);
		this.centerXProperty().set(inX);
		this.centerYProperty().set(inY);
		this.setup(stateController, field);
	}

	GFDot(double inRadius, StateController stateController, Field field) {
		super(inRadius);
		this.setup(stateController, field);
	}

	private void setup(StateController stateController, Field field) {
		this.setUserData(UUID.randomUUID().toString());
		this.stateController = stateController;
		this.field = field;
		this.setDotListener();
		this.setStrokeWidth(5);
		this.setTranslateZ(1);
		this.enableDrag();
	}

	private void setDotListener() {
		this.setPickOnBounds(false);
		this.setOnMouseClicked(event -> {
			switch (this.stateController.getButtonState()) {
			case ADD_EDGE:
				if (!stateController.isDotSelected(this)) {
					stateController.selectDot(this);
				}
				if (stateController.numberOfSelectedDots() >= 2) {
					this.field.addEdgeToField(stateController.getSelectedDot(0), stateController.getSelectedDot(1));
				}
				break;
			case MOVE_VERTEX:
				if (!stateController.isDotSelected(this)) {
					stateController.selectDot(this);
					stateController.setButtonState(States.Buttons.MOVE_VERTEX_SELECTED, false);
				}
				break;
			case MOVE_VERTEX_SELECTED:
				break;
			case DRAGGING_VERTEX:
				stateController.setButtonState(States.Buttons.MOVE_VERTEX, false);
			case ADD_VERTEX:
				break;
			case REMOVE_VERTEX:
				field.removeVertex(this);
				break;
			case SHORTEST_PATH:
				stateController.clearAll();
				stateController.selectDot(this);
				if (stateController.numberOfSelectedDots() >= 1) {
					field.shortestPath();
				}
				break;
			case CHANGE_WEIGHT:
			default:
				stateController.selectDot(this);
			}
		});
	}

	private class Delta {
		double x, y;
	}

	private void enableDrag() {
		final Delta dragDelta = new Delta();
		final GFDot dot = this;
		setOnMousePressed(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent mouseEvent) {
				if (stateController.getButtonState() == States.Buttons.MOVE_VERTEX
						|| stateController.getButtonState() == States.Buttons.DRAGGING_VERTEX) {
					// record a delta distance for the drag and drop operation.
					dragDelta.x = getCenterX() - mouseEvent.getX();
					dragDelta.y = getCenterY() - mouseEvent.getY();
				}
			}
		});
		setOnMouseDragged(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent mouseEvent) {
				if (stateController.getButtonState() == States.Buttons.MOVE_VERTEX
						|| stateController.getButtonState() == States.Buttons.DRAGGING_VERTEX) {
					double newX = mouseEvent.getX() + dragDelta.x;
					if (newX > 0 && newX < getScene().getWidth()) {
						dot.centerXProperty().set(newX);
					}
					double newY = mouseEvent.getY() + dragDelta.y;
					if (newY > 0 && newY < getScene().getHeight()) {
						dot.centerYProperty().set(newY);
					}
					stateController.setButtonState(States.Buttons.DRAGGING_VERTEX, false);
				}
			}
		});
	}

	@Override
	public String toString() {
		return "(" + this.getCenterX() + ", " + this.getCenterY() + ")";
	}

	@Override
	public int compareTo(GFDot arg0) {
		return 0;
	}

	@Override
	public int hashCode() {
		UUID thisUUID = UUID.fromString(this.getUserData().toString());
		long highbits = thisUUID.getMostSignificantBits();
		long lowbits = thisUUID.getLeastSignificantBits();
		double res = 5;
		res = res * 17 + highbits;
		res = res * 17 + lowbits;
		return (int) res;
	}
}

class RGBColor {
	int red, green, blue;
	Color color;

	RGBColor(int r, int g, int b) {
		red = r;
		green = g;
		blue = b;
	}

	@Override
	public boolean equals(Object arg) {
		if (arg != null && arg instanceof Dot) {
			RGBColor other = (RGBColor) arg;
			return this.red == other.red && this.green == other.green && this.blue == other.blue;
		}
		return false;
	}

	public Color color() {
		return Color.rgb(red, green, blue);
	}

	@Override
	public int hashCode() {
		double res = 5;
		res = res * 17 + this.red;
		res = res * 17 + this.green;
		res = res * 17 + this.blue;
		return (int) res;
	}
}

abstract class Algorithm implements I_Algorithm {
	GFAdjacencyList graph;
	StateController stateController;

	Algorithm(GFAdjacencyList graph, StateController stateController) {
		this.graph = graph;
		this.stateController = stateController;
	}
}

/**
 * 
 * @author ehudadler
 * @project GraphFun
 */

class SidebarButton extends Button {

	final int BUTTON_WIDTH = 170;

	public SidebarButton(String text, int col, int row) {
		super(text);
		this.setMinWidth(BUTTON_WIDTH);
		this.setAlignment(Pos.BASELINE_LEFT);
		GridPane.setConstraints(this, col, row);
	}
}

class Prims extends Algorithm {

	Prims(GFAdjacencyList graph, StateController stateController) {
		super(graph, stateController);
	}

	@Override
	public void run() {

		stateController.setButtonState(States.Buttons.MINIMAL_SPANNING_TREE, true);

		HashSet<Dot> visited = new HashSet<>();
		PriorityQueue<GFEdge> pQueue = new PriorityQueue<GFEdge>();
		Iterator<GFDot> itr = graph.getIterator();

		if (!itr.hasNext())
			return;

		GFDot source = itr.next();
		visited.add(source);

		for (GFEdge edge : graph.getEdge(source)) {
			pQueue.add(edge);
		}

		GFEdge edge;
		GFDot terminal;
		while (!pQueue.isEmpty()) {
			edge = pQueue.poll();
			terminal = edge.dot;
			if (!visited.contains(terminal)) {
				stateController.selectEdge(edge);
				visited.add(terminal);
				for (GFEdge neighbor : graph.getEdge(terminal)) {
					pQueue.add(neighbor);
				}
			}
		}
	}
}

class MovableWeightedLine extends Group implements Comparable<MovableWeightedLine> {

	StateController stateController;
	WeightLabel weightLabel;
	MovableLine moveableLine;
	Field field;

	MovableWeightedLine(DoubleProperty startX, DoubleProperty startY, DoubleProperty endX, DoubleProperty endY,
			StateController sc, Field field, long offset) {
		this.field = field;
		this.stateController = sc;
		this.moveableLine = new MovableLine(startX, startY, endX, endY, offset);
		this.setUpLineMouseListener();
		this.setup();
	}

	public double getWeight() {
		return weightLabel.getWeight();
	}

	public MovableLine getLine() {
		return moveableLine;
	}

	private void setup() {
		weightLabel = new WeightLabel(this.stateController.getWeightedTextFieldValue(),
				moveableLine.startXProperty().add(moveableLine.endXProperty()).divide(2),
				moveableLine.startYProperty().add(moveableLine.endYProperty()).divide(2));
		this.getChildren().addAll(moveableLine, weightLabel);
		this.setTranslateZ(1);
	}

	public void updateWeight(double weight) {
		WeightLabel changeLabel = this.weightLabel;
		changeLabel.setWeight(weight);
	}

	private void setUpLineMouseListener() {
		this.setOnMouseClicked(event -> {
			MovableWeightedLine group = (MovableWeightedLine) event.getSource();
			switch (stateController.getButtonState()) {
			case CHANGE_WEIGHT:
				group.updateWeight(this.stateController.getWeightedTextFieldValue());
				break;
			case REMOVE_EDGE:
				field.removeEdge(this);
			default:
				break;
			}
		});
	}

	@Override
	public int compareTo(MovableWeightedLine arg0) {
		if (this.getWeight() < arg0.getWeight())
			return -1;
		if (this.getWeight() > arg0.getWeight())
			return 1;
		return 0;
	}

}

interface I_Algorithm {
	void run();
}

class CutVertices extends Algorithm {

	HashMap<GFDot, Integer> disc = new HashMap<>();
	HashMap<GFDot, Integer> low = new HashMap<>();
	HashMap<GFDot, GFDot> parent = new HashMap<>();
	HashSet<GFDot> visited = new HashSet<>();
	private int time = 0;

	CutVertices(GFAdjacencyList graph, StateController stateController) {
		super(graph, stateController);
	}

	private int min(int d1, int d2) {
		return d1 < d2 ? d1 : d2;
	}

	private void updateDotColor() {
		stateController.changeColorForKey(Constants.ColorGenerator.Keys.cutVertex);
	}

	private void colorDot(GFDot dot) {
		dot.setFill(stateController.getEdgeColor(Constants.ColorGenerator.Keys.cutVertex).color());
	}

	private boolean shouldColor(GFDot dot1, GFDot dot2, int children) {
		return (!parent.containsKey(dot1) && children > 1)
				|| (parent.containsKey(dot1) && low.get(dot2) >= disc.get(dot1));
	}

	private void dfs(GFDot dot) {

		int children = 0;
		visited.add(dot);

		time++;
		disc.put(dot, time);
		low.put(dot, time);

		PriorityQueue<GFEdge> pQueue = graph.getEdge(dot);
		for (GFEdge edge : pQueue) {
			if (!visited.contains(edge.dot)) {
				children++;
				parent.put(edge.dot, dot);
				dfs(edge.dot);
				low.put(dot, min(low.get(dot), low.get(edge.dot)));
				if (shouldColor(dot, edge.dot, children)) {
					stateController.selectDot(dot);
					colorDot(dot);
				}
			} else if (edge.dot != parent.get(dot)) {
				low.put(dot, min(low.get(dot), disc.get(edge.dot)));
			}
		}
	}

	@Override
	public void run() {
		Iterator<GFDot> itr = graph.getIterator();
		while (itr.hasNext()) {
			GFDot dot = itr.next();
			if (!visited.contains(dot)) {
				dfs(dot);
			}
		}
		updateDotColor();
	}
}

class StateController {

	private double weightedTFValue = 0;
	private States.Buttons currentButtonState = States.Buttons.NO_STATE;
	private ArrayList<GFDot> highlightedDots = new ArrayList<GFDot>();
	private ArrayList<GFEdge> highlightedEdges = new ArrayList<GFEdge>();
	private ColorGenerator colorGenerator = new ColorGenerator();
	private ToggleGroup toggleGroup;

	public StateController(ToggleGroup toggleGroup) {
		this.toggleGroup = toggleGroup;
	}

	// Mark: Button state
	public void setButtonState(States.Buttons newState, boolean clearAll) {
		if (clearAll) {
			this.clearAll();
		}
		this.currentButtonState = newState;
	}

	public void setButtonStateAndUpdate(States.Buttons newState, boolean clearAll) {
		setButtonState(newState, clearAll);
		Toggle tog = toggleGroup.getToggles().get(newState.getValue());
		if (toggleGroup.getSelectedToggle() != tog) {
			toggleGroup.selectToggle(tog);
		}
	}

	public States.Buttons getButtonState() {
		return currentButtonState;
	}

	// Mark: WeightedTextField
	public void setWeightedTextFieldValue(double newValue) {
		this.weightedTFValue = newValue;
	}

	public double getWeightedTextFieldValue() {
		return weightedTFValue;
	}

	// Mark: Selected Dots
	public GFDot getSelectedDot(int index) {
		return highlightedDots.get(index);
	}

	public boolean isDotSelected(GFDot dot) {
		return highlightedDots.contains(dot);
	}

	public void selectDot(GFDot dot) {
		dot.setFill(this.getDotColor(Constants.ColorGenerator.Keys.dotSelected).color());
		highlightedDots.add(dot);
	}

	public void unselectDot(GFDot dot) {
		dot.setFill(this.getDotColor(Constants.ColorGenerator.Keys.dotUnselected).color());
		highlightedDots.remove(dot);
	}

	public void clearSelectedDots() {
		for (GFDot dot : highlightedDots) {
			dot.setFill(this.getDotColor(Constants.ColorGenerator.Keys.dotUnselected).color());
		}
		highlightedDots.clear();
	}

	public int numberOfSelectedDots() {
		return highlightedDots.size();
	}

	public RGBColor getDotColor(Constants.ColorGenerator.Keys forState) {
		return colorGenerator.requestColorAndSaveIfNecessary(forState);
	}

	// Mark: Selected Dots
	public GFEdge getSelectedEdge(int index) {
		return highlightedEdges.get(index);
	}

	public boolean isEdgeSelected(GFEdge edge) {
		return highlightedEdges.contains(edge);
	}

	public void selectEdge(GFEdge edge) {
		edge.mvl.moveableLine.setStroke(this.getDotColor(Constants.ColorGenerator.Keys.lineHighlighted).color());
		highlightedEdges.add(edge);
	}

	public void unselectEdge(GFEdge edge) {
		edge.mvl.moveableLine.setStroke(this.getDotColor(Constants.ColorGenerator.Keys.lineUnhighlighted).color());
		highlightedEdges.remove(edge);
	}

	public void clearSelectedEdges() {
		for (GFEdge edge : highlightedEdges) {
			edge.mvl.moveableLine.setStroke(this.getDotColor(Constants.ColorGenerator.Keys.lineUnhighlighted).color());
		}
		highlightedEdges.clear();
	}

	public int numberOfSelectedEdge() {
		return highlightedEdges.size();
	}

	public RGBColor getEdgeColor(Constants.ColorGenerator.Keys forState) {
		return colorGenerator.requestColorAndSaveIfNecessary(forState);
	}

	public void clearAll() {
		clearSelectedDots();
		clearSelectedEdges();
	}

	public void changeColorForKey(Constants.ColorGenerator.Keys key) {
		colorGenerator.requestAndSaveColor(key);
	}

}

class RandomNumberGenerator {
	Random rand = new Random();

	RandomNumberGenerator() {
	}

	double generateNumberForKey(Constants.NumberGenerator.Keys key) {
		switch (key) {
		case randomLineWeight:
			int min = Constants.NumberGenerator.Limits.LineWeight.min;
			int max = Constants.NumberGenerator.Limits.LineWeight.max;
			DecimalFormat df = new DecimalFormat("#.##");
			return Double.valueOf(df.format(min + (max - min) * rand.nextDouble()));
		case randomColorComponent:
			return rand.nextInt(255) + 1;
		default:
			return rand.nextInt();
		}
	}

}

class States {

	public enum Buttons {
		ADD_VERTEX(0), REMOVE_VERTEX(1), ADD_EDGE(2), REMOVE_EDGE(3), MOVE_VERTEX(4), SHORTEST_PATH(5),
		CHANGE_WEIGHT(6), DRAGGING_VERTEX(-1), MOVE_VERTEX_SELECTED(-1), MINIMAL_SPANNING_TREE(-1), NO_STATE(-1);

		private int value;

		private Buttons(int value) {
			this.value = value;
		}

		public int getValue() {
			return value;
		}
	}

	public static class Algorithms {
		enum ShortestPathState {
			DIJKSTRA, BELLMAN_FORD, FLOID_WARSHALL, TOPOLOGICAL_SORT;
		}
	}

	public static class Dot {
		enum State {
			selected, unselected;
		}
	}

}

/**
 * 
 * @author ehudadler
 * @project GraphFun
 */

class Dot extends Circle {

	private double x;
	private double y;

	void setX(double in) {
		x = in;
		this.setCenterX(in);
	}

	void setY(double in) {
		y = in;
		this.setCenterY(in);
	}

	double getX() {
		return x;
	}

	double getY() {
		return y;
	}

	Dot(double inX, double inY, double inRadius) {
		super(inX, inY, inRadius);
		x = inX;
		y = inY;
	}

	Dot(double inRadius) {
		super(inRadius);
	}

	@Override
	public boolean equals(Object arg) {
		if (arg != null && arg instanceof Dot) {
			Dot other = (Dot) arg;
			return this.x == other.x && this.y == other.y;
		}
		return false;
	}

}

/**
 * 
 * @author ehudadler
 * @project GraphFun
 */

class NumericTextField extends TextField {

	StateController stateController;
	public double value;

	NumericTextField(StateController stateController) {
		this.textProperty().addListener((observable, oldValue, newValue) -> {
			try {
				stateController.setButtonStateAndUpdate(States.Buttons.CHANGE_WEIGHT, true);
				this.value = Double.parseDouble(newValue);
				stateController.setWeightedTextFieldValue(value);
			} catch (NumberFormatException e) {
				// Not numeric
				if ((newValue.length() == 1 && newValue.charAt(newValue.length() - 1) != '.')
						|| newValue.length() > 1) {
					this.deleteNextChar();
					this.showAlert();
				}
			}
		});
	}

	private void showAlert() {
		Alert dialog = new Alert(Alert.AlertType.ERROR);
		dialog.setHeaderText("Invalid weight");
		dialog.setContentText("The weight must be numerical (Pos) ");
		dialog.setResizable(true);
		dialog.getDialogPane().setPrefSize(480, 320);
		dialog.showAndWait();
	}
}

class ColorGenerator {

	private HashMap<Constants.ColorGenerator.Keys, RGBColor> colors;

	ColorGenerator() {
		colors = new HashMap<Constants.ColorGenerator.Keys, RGBColor>();
	}

	private RGBColor generateColor() {
		RandomNumberGenerator rand = new RandomNumberGenerator();
		Constants.NumberGenerator.Keys key = Constants.NumberGenerator.Keys.randomColorComponent;
		RGBColor newColor;

		do {
			int r = (int) rand.generateNumberForKey(key);
			int g = (int) rand.generateNumberForKey(key);
			int b = (int) rand.generateNumberForKey(key);
			newColor = new RGBColor(r, g, b);
		} while (colors.containsValue(newColor));

		return newColor;
	}

	public RGBColor getColor(Constants.ColorGenerator.Keys key) throws Exception {
		if (!colors.containsKey(key))
			throw new Exception("That key has no current value");
		return colors.get(key);
	}

	public RGBColor requestAndSaveColor(Constants.ColorGenerator.Keys forKey) {

		// Generate new color
		RGBColor newColor = generateColor();

		// Save
		colors.put(forKey, newColor);
		return newColor;
	}

	public RGBColor requestColorAndSaveIfNecessary(Constants.ColorGenerator.Keys forKey) {

		if (colors.containsKey(forKey)) {
			return colors.get(forKey);
		}

		// Generate new color
		RGBColor newColor = generateColor();

		// Save
		colors.put(forKey, newColor);
		return newColor;
	}

	public RGBColor requestColor() {
		// Generate new color
		RGBColor newColor = generateColor();
		return newColor;
	}

}

class GFAdjacencyList {
	private HashMap<GFDot, PriorityQueue<GFEdge>> adjacencyList;

	GFAdjacencyList() {
		adjacencyList = new HashMap<GFDot, PriorityQueue<GFEdge>>();
	}

	public int size() {
		return adjacencyList.size();
	}

	public boolean isEmpty() {
		return adjacencyList.isEmpty();
	}

	public boolean addVertex(GFDot inDot) {
		boolean exists = adjacencyList.containsKey(inDot);
		adjacencyList.putIfAbsent(inDot, new PriorityQueue<GFEdge>());
		return exists;
	}

	public void removeVertex(GFDot inDot) throws Exception {
		adjacencyList.remove(inDot);
	}

	public Iterator<GFDot> getIterator() {
		return adjacencyList.keySet().iterator();
	}

	public PriorityQueue<GFEdge> getEdge(GFDot start) {
		return adjacencyList.get(start);
	}

	public void removeEdge(MovableWeightedLine mvl) {
		for (HashMap.Entry<GFDot, PriorityQueue<GFEdge>> entry : adjacencyList.entrySet()) {
			GFDot key = entry.getKey();
			PriorityQueue<GFEdge> value = entry.getValue();
			for (GFEdge edge : value) {
				if (edge.mvl.equals(mvl)) {
					adjacencyList.get(key).remove(edge);
					return;
				}
			}

		}
	}

	public void removeEdgeBetween(GFDot dot1, GFDot dot2) {
		if (adjacencyList.containsKey(dot1)) {
			PriorityQueue<GFEdge> edges = adjacencyList.get(dot1);
			for (GFEdge edge : edges) {
				if (edge.dot == dot2) {
					edges.remove(edge);
					return;
				}
			}
		}
	}

	public void removeEdgesBetween(GFDot dot1, GFDot dot2) {
		if (adjacencyList.containsKey(dot1)) {
			PriorityQueue<GFEdge> edges = adjacencyList.get(dot1);
			for (GFEdge edge : edges) {
				if (edge.dot == dot2) {
					edges.remove(edge);
				}
			}
		}
	}

	private void _addEdge(GFDot inDot, GFDot terminalDot, int weight, MovableWeightedLine mvl) {
		adjacencyList.putIfAbsent(inDot, new PriorityQueue<GFEdge>());
		adjacencyList.get(inDot).add(new GFEdge(terminalDot, mvl));
	}

	public void addEdge(GFDot inDot, GFDot terminalDot, int weight, MovableWeightedLine mvl) {
		_addEdge(inDot, terminalDot, weight, mvl);
		_addEdge(terminalDot, inDot, weight, mvl);
	}

	@Override
	public String toString() {
		String str = new String();
		str = "";
		for (HashMap.Entry<GFDot, PriorityQueue<GFEdge>> entry : adjacencyList.entrySet()) {
			GFDot key = entry.getKey();
			PriorityQueue<GFEdge> value = entry.getValue();
			str += key + " | [ " + value + " ]\n";
		}
		return str;
	}

	public long numberOfSharedEdgesUtil(GFDot dot, GFDot dot2) {
		PriorityQueue<GFEdge> edges = adjacencyList.get(dot);
		PriorityQueue<GFEdge> pq = new PriorityQueue<GFEdge>();
		for (GFEdge edge : edges) {
			if (edge.dot == dot2) {
				pq.add(edge);
			}
		}
		return pq.size();
	}

	public long getLineCurveBindingFor(GFDot dot, GFDot dot2) {
		long offset = 0;
		long numOfSharedEdges = this.numberOfSharedEdgesUtil(dot, dot2);
		offset += numOfSharedEdges;
		offset *= 30;
		return numOfSharedEdges % 2 == 0 ? offset : (offset * -1);
	}

}

class Dijkstra extends Algorithm {

	GFDot source, terminal;

	final class Node implements Comparable<Node> {
		GFDot dot;
		double distance;

		Node(GFDot dot, double distance) {
			this.dot = dot;
			this.distance = distance;
		}

		@Override
		public int compareTo(Node arg0) {
			if (this.distance < arg0.distance)
				return -1;
			if (this.distance > arg0.distance)
				return 1;
			return 0;
		}
	}

	Dijkstra(GFAdjacencyList graph, GFDot source, GFDot terminal, StateController stateController) {
		super(graph, stateController);
		this.source = source;
		this.terminal = terminal;
	}

	private void colorEdge(GFEdge edge) {
		edge.mvl.moveableLine
				.setStroke(stateController.getEdgeColor(Constants.ColorGenerator.Keys.shortestPath).color());
	}

	public void update(GFDot newSource, GFDot newTerminal) {
		this.source = newSource;
		this.terminal = newTerminal;
	}

	@Override
	public void run() {
		PriorityQueue<Node> pQueue = new PriorityQueue<>();
		HashSet<GFDot> visited = new HashSet<>();
		HashMap<GFDot, Double> distances = new HashMap<>();
		HashMap<GFDot, GFEdge> pathVia = new HashMap<>();

		visited.add(source);
		distances.put(source, 0.0);
		pQueue.add(new Node(source, 0.0));

		Iterator<GFDot> itr = graph.getIterator();
		while (itr.hasNext()) {
			GFDot dot = itr.next();
			if (dot != source) {
				pQueue.add(new Node(dot, Double.MAX_VALUE));
				distances.put(dot, Double.MAX_VALUE);
			}
		}

		// TODO: Fix this
		while (!pQueue.isEmpty()) {
			Node currentNode = pQueue.poll();
			for (GFEdge edge : graph.getEdge(currentNode.dot)) {
				GFDot targetDot = edge.getDot();
				double newDistance = distances.get(currentNode.dot) + edge.getWeight();
				if (newDistance < distances.get(targetDot)) {
					stateController.selectEdge(edge);
					colorEdge(edge);

					// if(pathVia.containsKey(targetDot)) {
					// stateController.selectEdge(pathVia.get(targetDot));
					// colorEdge(pathVia.get(targetDot));
					// }

					pathVia.put(targetDot, edge);
					distances.put(targetDot, newDistance);
				}
			}
		}
	}
}

/**
 * 
 * @author ehudadler
 * @project GraphFun
 */

class Field extends Pane {

	StateController stateController;
	GFAdjacencyList graph;
	GG6562 graphGUI;

	Field(int width, int height, StateController stateController, GFAdjacencyList graph, GG6562 graphGUI) {
		this.graph = graph;
		this.graphGUI = graphGUI;
		this.setBackground(new Background(new BackgroundFill(Color.GHOSTWHITE, null, null)));
		this.setMinHeight(width);
		this.setMinWidth(height);
		this.stateController = stateController;
		handleMouseClicked();
	}

	private void handleMouseClicked() {
		this.setOnMouseClicked(event -> {
			if (event.getZ() != 0)
				return;
			switch (stateController.getButtonState()) {
			case ADD_VERTEX:
				this.addVertexToField(event.getX(), event.getY());
				break;
			case MOVE_VERTEX_SELECTED:
				if (stateController.numberOfSelectedDots() >= 1) {
					this.moveVertexOnField(stateController.getSelectedDot(0), event.getX(), event.getY(),
							States.Buttons.MOVE_VERTEX, true);
				}
				break;
			default:
			}
		});
	}

	public void shortestPath() {
		graphGUI.shortestPath();
	}

	public void addAllEdges() {
		Iterator<GFDot> itr = graph.getIterator();
		while (itr.hasNext()) {
			GFDot dot = itr.next();
			Iterator<GFDot> itr2 = graph.getIterator();
			while (itr2.hasNext()) {
				GFDot dot2 = itr2.next();
				// We use != because we are checking reference not value
				if (dot != dot2 && graph.numberOfSharedEdgesUtil(dot, dot2) == 0) {
					// These vertices do not already share an edge
					addEdgeToField(dot, dot2);
				}
			}
		}
	}

	public void addEdgeToField(GFDot dot, GFDot dot2) {
		_addEdgeToField(dot, dot2);
	}

	private void _addEdgeToField(GFDot dot, GFDot dot2) {

		MovableWeightedLine movableWeightedLine = new MovableWeightedLine(dot.centerXProperty(), dot.centerYProperty(),
				dot2.centerXProperty(), dot2.centerYProperty(), stateController, this,
				graph.getLineCurveBindingFor(dot, dot2));
		movableWeightedLine.moveableLine
				.setStroke(stateController.getDotColor(Constants.ColorGenerator.Keys.lineUnhighlighted).color());
		if (!this.getChildren().contains(movableWeightedLine)) {
			this.getChildren().add(0, movableWeightedLine);
		}
		graph.addEdge(dot, dot2, 0, movableWeightedLine);
		stateController.clearSelectedDots();
	}

	public void moveVertexOnField(GFDot dot, double x, double y, States.Buttons state, boolean clear) {
		dot.centerXProperty().set(x);
		dot.centerYProperty().set(y);
		stateController.setButtonState(state, clear);
	}

	private void addVertexToField(double x, double y) {
		GFDot newDot = new GFDot(x, y, Constants.Dot.radius, stateController, this);
		newDot.setFill(stateController.getDotColor(Constants.ColorGenerator.Keys.dotUnselected).color());
		if (!this.getChildren().contains(newDot)) {
			this.getChildren().add(newDot);
		}
		graph.addVertex(newDot);
	}

	public void removeEdge(MovableWeightedLine mvl) {
		this.getChildren().remove(mvl);
		graph.removeEdge(mvl);
	}

	public void removeVertex(GFDot dot) {
		PriorityQueue<GFEdge> edges = graph.getEdge(dot);
		try {
			graph.removeVertex(dot);
			this.getChildren().remove(dot);

			for (GFEdge edge : edges) {
				this.getChildren().remove(edge.mvl);
				graph.removeEdgeBetween(edge.dot, dot);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}

/**
 * 
 * @author ehudadler
 * @project GraphFun
 */

class MovableLine extends QuadCurve {

	MovableLine(DoubleProperty startX, DoubleProperty startY, DoubleProperty endX, DoubleProperty endY, long offset) {
		this.setStrokeWidth(4);
		this.setFill(Color.TRANSPARENT);

		this.controlXProperty().bind(startX.add(endX).divide(2));
		this.controlYProperty().bind(startY.add(endY).divide(2).add(offset));

		this.startXProperty().bind(startX);
		this.startYProperty().bind(startY);
		this.endXProperty().bind(endX);
		this.endYProperty().bind(endY);
	}
}

class GFEdge implements Comparable<GFEdge> {
	GFDot dot;
	MovableWeightedLine mvl;

	public GFEdge(GFDot arg1, MovableWeightedLine arg2) {
		this.dot = arg1;
		this.mvl = arg2;
	}

	public double getWeight() {
		return mvl.getWeight();
	}

	public GFDot getDot() {
		return dot;
	}

	public MovableWeightedLine getLine() {
		return mvl;
	}

	public int compareTo(GFEdge arg) {
		if (this == arg)
			return 0;
		if (arg == null)
			return 0;
		if (arg instanceof GFEdge) {
			GFEdge other = (GFEdge) arg;
			return this.getWeight() >= other.getWeight() ? 1 : -1;
		}
		return 0;
	}

	@Override
	public String toString() {
		return "W: " + this.getWeight() + ", " + this.getDot();
	}

}

/**
 * 
 * @author ehudadler
 * @project GraphFun
 */

class WeightLabel extends Label {

	private double weight;

	void setWeight(double in) {
		this.weight = in;
		this.textProperty().setValue("" + in);
	}

	double getWeight() {
		return this.weight;
	}

	WeightLabel(Double inWeight) {
		this.setFont(new Font("Arial", 12));
		this.setWeight(inWeight);
	}

	WeightLabel(Double inWeight, DoubleBinding inX, DoubleBinding inY) {
		super();
		this.setFont(new Font("Arial", 12));
		this.setWeight(inWeight);
		this.layoutXProperty().bind(inX);
		this.layoutYProperty().bind(inY);
	}

}

class Sidebar extends GridPane {

	Sidebar() {
		this.setMinWidth(Constants.Sidebar.Size.width);
		this.setPadding(new Insets(Constants.Sidebar.Size.inset));
		this.setVgap(Constants.Sidebar.Size.vgap);
		this.setHgap(Constants.Sidebar.Size.hgap);
	}
}

class ConnectedComponents extends Algorithm {

	ConnectedComponents(GFAdjacencyList graph, StateController stateController) {
		super(graph, stateController);
	}

	private void updateEdgeColor() {
		stateController.changeColorForKey(Constants.ColorGenerator.Keys.connectedComponent);
	}

	private void colorEdge(GFEdge edge) {
		edge.mvl.moveableLine
				.setStroke(stateController.getEdgeColor(Constants.ColorGenerator.Keys.connectedComponent).color());
	}

	private void dfs(HashSet<GFDot> visited, GFDot dot) {

		if (visited.contains(dot))
			return;

		visited.add(dot);
		PriorityQueue<GFEdge> pQueue = graph.getEdge(dot);
		for (GFEdge edge : pQueue) {
			stateController.selectEdge(edge);
			colorEdge(edge);
			dfs(visited, edge.dot);
		}
	}

	@Override
	public void run() {
		HashSet<GFDot> visited = new HashSet<>();
		Iterator<GFDot> itr = graph.getIterator();

		while (itr.hasNext()) {
			GFDot dot = itr.next();
			if (!visited.contains(dot)) {
				dfs(visited, dot);
				updateEdgeColor();
			}
		}
	}

}

/**
 *
 * @author ehudadler
 * @project GraphFun
 */

// TODO:
// Fix up shortest path
// Pop up for SSSP choice

public class GG6562 extends Application {

	// Private variables
	private StateController stateController;
	private GridPane layout;
	private Field field; // View
	private NumericTextField weightTextfield;
	private Stage stage;
	private ToggleGroup toggleGroup;
	private GFAdjacencyList graph; // Model
	private Scene scene;
	private Sidebar sidebar;

	public static void main(String[] args) {
		GG6562 gui = new GG6562();
		gui.show();
	}

	@Override
	public void start(Stage primaryStage) throws Exception {

		stage = primaryStage;

		primaryStage.setTitle(Constants.Graph.title);
		primaryStage.setMinHeight(Constants.Graph.Size.height);
		primaryStage.setMinWidth(Constants.Graph.Size.width);

		// Layout
		layout = new GridPane();
		createSidebar();

		// Scene
		scene = new Scene(layout, Constants.Graph.Size.width, Constants.Graph.Size.height);

		field.prefWidthProperty().bind(scene.widthProperty());
		field.prefHeightProperty().bind(scene.heightProperty());

		primaryStage.setScene(scene);
		primaryStage.show();
	}

	// Mark: Private
	private void startToggleListener(ToggleGroup toggle) {
		toggle.selectedToggleProperty().addListener((observable, oldVal, newVal) -> {
			States.Buttons newState = (States.Buttons) toggleGroup.getSelectedToggle().getUserData();
			stateController.setButtonState(newState, true);
			notify("" + newState + " Selected");
		});
	}

	private void notify(String message) {
		stage.setTitle(message);
		Timer timer = new java.util.Timer();
		timer.schedule(new TimerTask() {
			public void run() {
				Platform.runLater(new Runnable() {
					public void run() {
						stage.setTitle(Constants.Graph.title);
					}
				});
			}
		}, 1000);
	}

	private RadioButton createRadioButton(String name, int column, States.Buttons state) {
		RadioButton newRadioButton = new RadioButton(name);
		newRadioButton.setToggleGroup(toggleGroup);
		newRadioButton.setUserData(state);

		GridPane.setConstraints(newRadioButton, 0, column);
		return newRadioButton;
	}

	private void createSidebar() {
		sidebar = new Sidebar();

		/** RADIO BUTTONS **/

		RadioButton addVertex = createRadioButton(Constants.Strings.addVertex, 0, States.Buttons.ADD_VERTEX);
		RadioButton addEdge = createRadioButton(Constants.Strings.addEdge, 1, States.Buttons.ADD_EDGE);
		RadioButton removeVertex = createRadioButton(Constants.Strings.removeVertex, 2, States.Buttons.REMOVE_VERTEX);
		RadioButton removeEdge = createRadioButton(Constants.Strings.removeEdge, 3, States.Buttons.REMOVE_EDGE);
		RadioButton moveVertex = createRadioButton(Constants.Strings.moveVertex, 4, States.Buttons.MOVE_VERTEX);
		RadioButton shortestPath = createRadioButton(Constants.Strings.shortestPath, 5, States.Buttons.SHORTEST_PATH);
		RadioButton changeWeight = createRadioButton(Constants.Strings.changeWeight, 0, States.Buttons.CHANGE_WEIGHT);

		// Change weight stack
		GridPane changeWeightlayout = new GridPane();
		GridPane.setConstraints(changeWeightlayout, 0, 6);
		changeWeightlayout.setHgap(Constants.Sidebar.Size.hgap);

		weightTextfield = new NumericTextField(stateController);
		weightTextfield.setPrefWidth(Constants.Sidebar.Size.weightTextViewWidth);
		GridPane.setConstraints(weightTextfield, 1, 0);
		changeWeightlayout.getChildren().addAll(changeWeight, weightTextfield);

		/** PUSH BUTTONS **/
		Button addAllEdges = new SidebarButton(Constants.Strings.addAllEdges, 0, 7);
		Button randomWeights = new SidebarButton(Constants.Strings.randomWeights, 0, 8);
		Button minimalSpanningTree = new SidebarButton(Constants.Strings.minimalSpanningTree, 0, 9);
		Button findCutVertecies = new SidebarButton(Constants.Strings.showCutVerticies, 0, 10);
		Button findAllComponents = new SidebarButton(Constants.Strings.connectedComponents, 0, 11);
		Button help = new SidebarButton(Constants.Strings.help, 0, 12);

		assignAddAllEdges(addAllEdges);
		assignPrims(minimalSpanningTree);
		assignConnectedComponents(findAllComponents);
		assignCutVertices(findCutVertecies);
		assignRandomWeights(randomWeights);
		assignHelpButton(help);

		// Add all controls to vertical panel
		sidebar.getChildren().addAll(addVertex, addEdge, removeVertex, removeEdge, moveVertex, shortestPath,
				changeWeightlayout, addAllEdges, randomWeights, minimalSpanningTree, findCutVertecies,
				findAllComponents, help);

		field = new Field(Constants.Graph.Size.height, Constants.Graph.Size.width - Constants.Sidebar.Size.width,
				stateController, graph, this);

		GridPane.setConstraints(field, 2, 0);
		GridPane.setConstraints(sidebar, 1, 0);

		layout.getChildren().add(sidebar);
		layout.getChildren().add(field);
		startToggleListener(toggleGroup); // Start radio toggle listener
	}

	// Mark: Actions
	private void assignPrims(Button bttn) {
		bttn.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				stateController.clearAll();
				Prims prim = new Prims(graph, stateController);
				prim.run();
			}
		});
	}

	private void assignConnectedComponents(Button bttn) {
		bttn.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				stateController.clearAll();
				ConnectedComponents cc = new ConnectedComponents(graph, stateController);
				cc.run();
			}
		});
	}

	private void assignCutVertices(Button bttn) {
		bttn.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				stateController.clearAll();
				CutVertices cv = new CutVertices(graph, stateController);
				cv.run();
			}
		});
	}

	private void assignAddAllEdges(Button bttn) {
		bttn.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				stateController.clearAll();
				field.addAllEdges();
			}
		});
	}

	private void assignRandomWeights(Button bttn) {
		bttn.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				stateController.clearAll();
				RandomNumberGenerator rand = new RandomNumberGenerator();
				Iterator<GFDot> itr = graph.getIterator();
				while (itr.hasNext()) {
					GFDot dot = itr.next();
					for (GFEdge edge : graph.getEdge(dot)) {
						edge.mvl.updateWeight(
								rand.generateNumberForKey(Constants.NumberGenerator.Keys.randomLineWeight));
					}
				}
			}
		});
	}

	private void _shortestPath() {
		Dijkstra dijkstra = new Dijkstra(graph, stateController.getSelectedDot(0), null, stateController);
		dijkstra.run();
	}

	private void assignHelpButton(Button bttn) {
		bttn.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				help();
			}
		});
	}

	private void help() {

		Alert alert = new Alert(AlertType.CONFIRMATION);
		alert.setTitle("Help");
		alert.setHeaderText("Frequency asked questions");
		alert.setContentText("Choose your question");

		ButtonType addRemoveAVertex_Help = new ButtonType("Add/Remove Vertex");
		ButtonType addRemoveAnEdge_Help = new ButtonType("Add/Remove Edge");
		ButtonType addAllEdges_Help = new ButtonType("Add All Edges");
		ButtonType moveVertex_Help = new ButtonType("Move Vertex");
		ButtonType cutVertex_Help = new ButtonType("Cut Vertex");
		ButtonType connectedComp_Help = new ButtonType("Connected Components");
		ButtonType cancel = new ButtonType("Cancel");

		alert.getButtonTypes().setAll(addRemoveAVertex_Help, addRemoveAnEdge_Help, moveVertex_Help, addAllEdges_Help,
				cutVertex_Help, connectedComp_Help, cancel);

		Optional<ButtonType> result = alert.showAndWait();

		String content = "";

		switch (result.get().getText()) {
		case "Add/Remove Vertex":
			content = "Add Vertex:\n" + "1. Tap the 'Add Vertex' button on the left sidebar.\n"
					+ "2. Tap anywhere on the canvas to add a vertex.\n"
					+ "Note: You cannot place a vertex on another vertex\n\n" + "Remove Vertex:\n"
					+ "1. Tap the 'Remove Vertex' button on the left sidebar\n"
					+ "2. Tap the vertex you wish to remove";
			break;
		case "Add/Remove Edge":
			content = "Add Edge:\n" + "1. Tap the 'Add Edge' button on the left sidebar.\n"
					+ "2. Tap the initial vertex.\n" + "2. Tap the terminal vertex.\n"
					+ "Note: You *can* add multiple edges between the same vertices (beta) \n\n" + "Remove Edge:\n"
					+ "1. Tap the 'Remove Edge' button on the left sidebar\n" + "2. Tap the edge you wish to remove";
			break;
		case "Add All Edges":
			content = "This button will add an edge between every single vertex\n\n" + "Add All Edges:\n"
					+ "1. Tap the 'Add All Edge' button on the left sidebar.";
			break;
		case "Move Vertex":
			content = "Move Vertex:\n" + "1. Tap the 'Move Vertex' button on the left sidebar.\n"
					+ "2a. Tap and drag the vertex you wish to move\n"
					+ "2b. Tap the vertex you want to move and tap a new position on the canvas.";
			break;
		case "Cut Vertex":
			content = "This will show each of the cut vertices in a different color\n\n" + "Cut Vertex:\n"
					+ "1. Tap the 'Cut Vertex' button on the left sidebar.";
			break;
		case "Connected Components":
			content = "This will show each of the connected components in a different color\n\n"
					+ "Connected Component:\n" + "1. Tap the 'Connected Component button on the left sidebar.\n"
					+ "Note: Each component will be in a unqiue color and these color will change with each press of the button";
			break;
		case "Cancel":

			return; // Get out
		default:
			break;
		}

		this.showInfoAlert(result.get().getText(), content);
	}

	private void showInfoAlert(String title, String content) {
		Alert alert = new Alert(AlertType.INFORMATION);
		alert.setTitle(title);
		alert.setContentText(content);

		ButtonType cancel = new ButtonType("Cancel");
		alert.getButtonTypes().setAll(cancel);
		alert.showAndWait();
	}

	// Mark: Public
	public void shortestPath() {
		_shortestPath();
	}

	public GG6562() {
		this.graph = new GFAdjacencyList();
		this.toggleGroup = new ToggleGroup();
		this.stateController = new StateController(toggleGroup);
	}

	public void show() {
		launch();
	}
}

class Constants {
	static class Strings {
		static String addAllEdges = "Add All Edges";
		static String addEdge = "Add Edge";
		static String addVertex = "Add Vertex";
		static String changeWeight = "Change Weight";
		static String connectedComponents = "Connected Componenets";
		static String help = "Help";
		static String minimalSpanningTree = "Minimal Spanning Tree";
		static String moveVertex = "Move Vertex";
		static String removeEdge = "Remove Edge";
		static String removeVertex = "Remove Vertex";
		static String randomWeights = "Random Weights";
		static String shortestPath = "Shortest Path";
		static String showCutVerticies = "Show Cut Verticies";
	}

	static class Graph {
		static class Size {
			static int width = 800;
			static int height = 500;
		}

		static String title = "Graph Fun";
	}

	static class Radio {
		static int spacing = 20;
		static int padding = 20;
	}

	static class Sidebar {
		static class Size {
			static int hgap = 10;
			static int vgap = 15;
			static int inset = 10;
			static int width = 200;
			static int weightTextViewWidth = 50;
		}
	}

	static class Dot {
		static int radius = 5;
	}

	static class ColorGenerator {
		enum Keys {
			dotUnselected, dotSelected, lineUnhighlighted, lineHighlighted, connectedComponent, cutVertex, shortestPath;
		}
	}

	static class NumberGenerator {
		enum Keys {
			randomLineWeight, randomColorComponent;
		}

		static class Limits {
			static class LineWeight {
				static int min = 1;
				static int max = 100;
			}
		}
	}
}
