package com.google.sample.stockwatcher.client;

import java.util.ArrayList;
import java.util.Date;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.DragOverEvent;
import com.google.gwt.event.dom.client.DragOverHandler;
import com.google.gwt.event.dom.client.DragStartEvent;
import com.google.gwt.event.dom.client.DragStartHandler;
import com.google.gwt.event.dom.client.DropEvent;
import com.google.gwt.event.dom.client.DropHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.event.dom.client.KeyPressHandler;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;

public class PopulationWatcher implements EntryPoint {

	private static final int REFRESH_INTERVAL = 5000;
	
	private HorizontalPanel bigPanel = new HorizontalPanel();
	
	private VerticalPanel mainPanel = new VerticalPanel();
	private FlexTable citiesFlexTable = new FlexTable();
	private HorizontalPanel addPanel = new HorizontalPanel();
	private TextBox newCityTextBox = new TextBox();
	private Button addCityButton = new Button("Add");
	private Label lastUpdatedLabel = new Label();
	private Label errorMsgLabel = new Label();

	private HorizontalPanel addAvailableCityPanel = new HorizontalPanel();
	private TextArea textArea = new TextArea();
	private Button addAvailableCityBtn = new Button("Add City");
	private Label errorOnAddLabel = new Label();
	
	private VerticalPanel dropPanel = new VerticalPanel();
	private FlexTable secondTable = new FlexTable();
	
	private ArrayList<String> tableCities = new ArrayList<String>();
	private CityPopulationServiceAsync cityPopulationSvc = GWT.create(CityPopulationService.class);

	/**
	 * Entry point method.
	 */
	public void onModuleLoad() {

		// Create table for city data.
		citiesFlexTable.setText(0, 0, "Name");
		citiesFlexTable.setText(0, 1, "Population");
		citiesFlexTable.setText(0, 2, "Area");
		citiesFlexTable.setText(0, 3, "Remove");

		// Add styles to elements in the city list table.
		citiesFlexTable.setCellPadding(6);
		citiesFlexTable.getRowFormatter().addStyleName(0, "watchListHeader");
		citiesFlexTable.addStyleName("watchList");
		citiesFlexTable.getCellFormatter().addStyleName(0, 1, "watchListNumericColumn");
		citiesFlexTable.getCellFormatter().addStyleName(0, 2, "watchListNumericColumn");
		citiesFlexTable.getCellFormatter().addStyleName(0, 3, "watchListRemoveColumn");

		// Assemble Add city panel.
		addPanel.add(newCityTextBox);
		addPanel.add(addCityButton);
		addPanel.addStyleName("addPanel");

		// Format textArea panel
		textArea.setVisibleLines(4);
		textArea.setCharacterWidth(25);
		errorOnAddLabel.setStyleName("errorMessage");
		addAvailableCityPanel.setStyleName("addAvailableCityPanel");
		addAvailableCityPanel.add(textArea);
		addAvailableCityPanel.add(addAvailableCityBtn);

		// ErrorMsg
		errorMsgLabel.setStyleName("errorMessage");
		errorMsgLabel.setVisible(false);

		// Assemble Main panel.
		mainPanel.add(errorMsgLabel);
		mainPanel.add(citiesFlexTable);
		mainPanel.add(addPanel);
		mainPanel.add(lastUpdatedLabel);
		mainPanel.add(errorOnAddLabel);
		mainPanel.add(addAvailableCityPanel);
		
		// Assemble dropPanel
		secondTable.setText(0, 0, "Name");
		secondTable.setText(0, 1, "Population");
		secondTable.setText(0, 2, "Area");
		
		secondTable.setCellPadding(6);
		secondTable.getRowFormatter().addStyleName(0, "watchListHeader");
		secondTable.addStyleName("watchList");
		secondTable.getCellFormatter().addStyleName(0, 1, "watchListNumericColumn");
		secondTable.getCellFormatter().addStyleName(0, 2, "watchListNumericColumn");
		
		secondTable.addDragOverHandler(new DragOverHandler() {
			
			@Override
			public void onDragOver(DragOverEvent event) {
			}
		});
		secondTable.addDropHandler(new DropHandler() {
			
			@Override
			public void onDrop(DropEvent event) {
				event.preventDefault();
				
				String name = event.getData("name");
				String area = event.getData("area");
				String pop = event.getData("pop");
				
				int row = secondTable.getRowCount() + 1;
				
				secondTable.getCellFormatter().setStyleName(row, 1, "watchListNumericColumn");
				secondTable.getCellFormatter().setStyleName(row, 2, "watchListNumericColumn");
				
		        secondTable.setText(row, 0, name);
		        secondTable.setText(row, 1, pop);
		        secondTable.setText(row, 2, area);
			}
		});
		
		dropPanel.add(secondTable);
		
		// Assemble bigPanel
		bigPanel.add(mainPanel);
		bigPanel.add(dropPanel);

		// Associate the Main panel with the HTML host page.
		RootPanel.get("cityList").add(bigPanel);

		// Move cursor focus to the input box.
		newCityTextBox.setFocus(true);

		// Listen for mouse events on the Add button.
		addCityButton.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				addCity();
			}
		});

		// Listen for keyboard events in the input box.
		newCityTextBox.addKeyPressHandler(new KeyPressHandler() {
			public void onKeyPress(KeyPressEvent event) {
				if (event.getCharCode() == KeyCodes.KEY_ENTER) {   	
					addCity();
				}
			}
		});

		addAvailableCityBtn.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				addNewAvailableCity();
			}
		});

		// Setup timer to refresh list automatically.
		Timer refreshTimer = new Timer() {
			@Override
			public void run() {
				refreshWatchList();
			}
		};
		refreshTimer.scheduleRepeating(REFRESH_INTERVAL);

	}

	private void addNewAvailableCity(){
		

		// Initialize the service proxy.
		if (cityPopulationSvc == null) {
			cityPopulationSvc = GWT.create(CityPopulationService.class);
		}

		// Set up the callback object.
		AsyncCallback<Void> callback = new AsyncCallback<Void>() {
			public void onFailure(Throwable caught) {
				String details = caught.getMessage();
				if (caught instanceof CityAlreadyExistsException){
					details = "City '" + ((CityAlreadyExistsException)caught).getCityName() +
							"' already exists in PopulationWatcher. Try another city.";
				}

				errorOnAddLabel.setText("Error: " + details);
				errorOnAddLabel.setVisible(true);
				Timer timer = new Timer(){
					@Override
					public void run() {
						errorOnAddLabel.setVisible(false);
					}
				};
				timer.schedule(5000);
			}

			@Override
			public void onSuccess(Void result) {
				textArea.setText("");				
			}
		};

		String addedCities = textArea.getText();
		addedCities.trim();
		String[] cities = addedCities.split(";");
		
		CityPopulation city;
		for(int i = 0; i<cities.length; i++){
			String [] cityString = cities[i].split(",");
			city = new CityPopulation(cityString[0].trim(),
					Integer.parseInt(cityString[1].trim()), Integer.parseInt(cityString[2].trim()));
			cityPopulationSvc.addNewAvailableCity(city, callback);
		}		
		

	}

	/**
	 * Add city to FlexTable. Executed when the user clicks the addCityButton or
	 * presses enter in the newCityTextBox.
	 */
	private void addCity() {
		final String cityName = newCityTextBox.getText().trim();

		newCityTextBox.setFocus(true);
		newCityTextBox.setText("");

		// Add the city to the table.
		int row = citiesFlexTable.getRowCount();
		tableCities.add(cityName.toLowerCase());
		
		Label nameLabel = new Label(cityName.toUpperCase());
		nameLabel.getElement().setDraggable(Element.DRAGGABLE_TRUE);
		nameLabel.addDragStartHandler(new DragStartHandler() {
			
			@Override
			public void onDragStart(DragStartEvent event) {
				
				String pop = ""+((Label)citiesFlexTable.getWidget(tableCities.indexOf(cityName.toLowerCase()) + 1, 1)).getText();
				String area = ""+citiesFlexTable.getText(tableCities.indexOf(cityName.toLowerCase()) + 1, 2);
				
				event.setData("name", cityName.toUpperCase());
				event.setData("area", area);
				event.setData("pop", pop);
			}
		});
		
		citiesFlexTable.setWidget(row, 0, nameLabel);
		citiesFlexTable.setWidget(row, 1, new Label());
		citiesFlexTable.getCellFormatter().addStyleName(row, 1, "watchListNumericColumn");
		citiesFlexTable.getCellFormatter().addStyleName(row, 2, "watchListNumericColumn");
		citiesFlexTable.getCellFormatter().addStyleName(row, 3, "watchListRemoveColumn");

		// Add a button to remove this city from the table.
		Button removeCityButton = new Button("x");
		removeCityButton.addStyleDependentName("remove");
		removeCityButton.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				int removedIndex = tableCities.indexOf(cityName.toLowerCase());
				tableCities.remove(removedIndex);
				citiesFlexTable.removeRow(removedIndex + 1);
			}
		});
		citiesFlexTable.setWidget(row, 3, removeCityButton);

		// Get the city population.
		refreshWatchList();
	}

	/**
	 * Generate random city populations.
	 */
	private void refreshWatchList() {

		if (tableCities.isEmpty()){
			return;
		}

		// Initialize the service proxy.
		if (cityPopulationSvc == null) {
			cityPopulationSvc = GWT.create(CityPopulationService.class);
		}

		// Set up the callback object.
		AsyncCallback<ArrayList<CityPopulation>> callback = new AsyncCallback<ArrayList<CityPopulation>>() {
			public void onFailure(Throwable caught) {
				String details = caught.getMessage();
				if (caught instanceof DelistedException){
					details = "City '" + ((DelistedException)caught).getCityName() + "' is not included in PopulationWatcher";
				}

				errorMsgLabel.setText("Error: " + details);
				errorMsgLabel.setVisible(true);
			}

			public void onSuccess(ArrayList<CityPopulation> result) {
				updateTable(result);
			}
		};

		// Make the call to the city population service.
		cityPopulationSvc.getPopulations(tableCities, callback);
	}

	/**
	 * Update the population and area fields of all the rows in the city table.
	 *
	 * @param cities City data for all rows.
	 * 
	 *
	 */
	private void updateTable(ArrayList<CityPopulation> cities) {
		for (int i = 0; i < cities.size(); i++) {
			updateTable(cities.get(i));
		}

		// Display timestamp showing last refresh.
		lastUpdatedLabel.setText("Last update : "
				+ DateTimeFormat.getMediumDateTimeFormat().format(new Date()));

		// Clear errors
		errorMsgLabel.setVisible(false);

	}

	/**
	 * Update a single row in the city table.
	 *
	 * @param city City data for a single row.
	 */
	private void updateTable(CityPopulation city) {
		// Make sure the city is still in the city table.
		if (!tableCities.contains(city.getName().toLowerCase())) {
			return;
		}

		int row = tableCities.indexOf(city.getName().toLowerCase()) + 1;

		// Populate the population and area fields with new data.
		String oldPop = ((Label)citiesFlexTable.getWidget(row, 1)).getText();

		citiesFlexTable.setText(row, 2, ""+city.getArea());
		Label popWidget = (Label)citiesFlexTable.getWidget(row, 1);
		popWidget.setText(city.getPopulation()+"");

		// Change the color of text in the pop field based on its value.
		String changeStyleName = "noChange";
		if (Integer.parseInt(oldPop) > city.getPopulation()) {
			changeStyleName = "negativeChange";
		}
		else if (Integer.parseInt(oldPop) < city.getPopulation()) {
			changeStyleName = "positiveChange";
		}

		popWidget.setStyleName(changeStyleName);

	}

}