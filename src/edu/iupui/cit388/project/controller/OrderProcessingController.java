package edu.iupui.cit388.project.controller;

import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Scanner;
import java.util.StringTokenizer;

import javax.swing.JPanel;

import edu.iupui.cit388.project.model.CreditCard;
import edu.iupui.cit388.project.model.Item;
import edu.iupui.cit388.project.model.OnlineOrder;
import edu.iupui.cit388.project.model.OrderLine;
import edu.iupui.cit388.project.service.OrderProcessingSystem;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;

// fx:controller="edu.iupui.cit388.project.controller.OrderProcessingController"
public class OrderProcessingController {

	// your @FXML goes here

	private OrderProcessingSystem system;
	private List<OrderLine> orderLineList = new ArrayList<>();
	
	@FXML
	public ListView<String> listview;
	
	@FXML
	public TextArea textarea;
	
	@FXML
	public TextField addressfield;
	
	@FXML
	public TextField quantityfield;
	
	@FXML
	public ComboBox<Item> combobox;
	
	@FXML
	public Label addresslabel;
	
	@FXML
	public Label itemlabel;
	
	@FXML
	public Label quantitylabel;
	
	@FXML
	public Button neworderbtn;
	
	@FXML
	public Button additembtn;
	
	@FXML
	public Button confirmorderbtn;
	
	@FXML
	public TextArea newordertextarea;
	
	@FXML
	void initialize() {
		
		Path itemDataFile = Paths.get(".\\resource\\Item.txt");
		system = new OrderProcessingSystem(itemDataFile);

		Path orderDataFile = Paths
				.get(".\\resource\\OnlineOrder.txt");
		loadOnlineOrder(system, orderDataFile);
		listvieworders();
	   ObservableList<Item> itemList = FXCollections.observableArrayList(system.getItemforCombobox());
	   combobox.setItems(itemList);
	}
	@FXML
	private void listvieworders() {
		
		ObservableList<String> list = FXCollections.observableArrayList(system.getOrdersInString());
		   listview.setItems(list);
	}
	@FXML
	private void initiateOrder(MouseEvent event) {
		addressfield.setText(null);
		combobox.getSelectionModel().clearSelection();
		quantityfield.setText(null);
		newordertextarea.setText(null);
	}
	public static boolean isNumeric(String str) { 
		  try {  
		    Double.parseDouble(str);  
		    return true;
		  } catch(NumberFormatException e){  
		    return false;  
		  }  
		}
	@FXML
	private void addItem(MouseEvent event) {
		
		if(isNumeric(quantityfield.getText())==false || Integer.valueOf(quantityfield.getText())<0) {
			Alert warning = new Alert(Alert.AlertType.WARNING);
			warning.setHeaderText("You should enter a non-negative numeric value in the quantity field");
			warning.show();
			return;
		}
		for(int i=0; i<orderLineList.size();i++) {
			if(combobox.getSelectionModel().getSelectedItem().getName()==orderLineList.get(i).getItemDescription()) {
				if(Integer.valueOf(quantityfield.getText())==0) {
					orderLineList.remove(orderLineList.get(i));
					newordertextarea.setText("Item Removed");
					return;
				}
				orderLineList.get(i).setQuantity(orderLineList.get(i).getQuantity()+Integer.valueOf(quantityfield.getText()));
				newordertextarea.setText("Item exists, quantity increased !");
				return;
				
			}
		}
		
		OrderLine orderline = new OrderLine(combobox.getSelectionModel().getSelectedItem().getName(),Integer.valueOf(quantityfield.getText()),combobox.getSelectionModel().getSelectedItem().getPrice());
		this.orderLineList.add(orderline);
		newordertextarea.setText("Item Added");
	}
	
	@FXML
	private void confirmOrder(MouseEvent event) {
		if(addressfield.getText().equals("")) {
			Alert warning = new Alert(Alert.AlertType.WARNING);
			warning.setHeaderText("Address Cannot Be Empty");
			warning.show();
			return;
		}
		if(orderLineList.isEmpty()) {
			Alert warning = new Alert(Alert.AlertType.WARNING);
			warning.setHeaderText("No items added, Please add items for the order");
			warning.show();
			return;
	}
		OnlineOrder order = system.createOnlineOrder();
		order.setShipAddress(addressfield.getText());
		for(int i=0;i<orderLineList.size();i++) {
			order.addOrderLine(orderLineList.get(i));
		}
		newordertextarea.setText(order.receiptDetails()+"\n \n"+ NumberFormat.getCurrencyInstance(Locale.US).format(order.receiptTotalAmount()));
		listvieworders();
		
		orderLineList.removeAll(orderLineList);
		Alert feedback = new Alert(Alert.AlertType.INFORMATION);
		feedback.setHeaderText("Order Added Successfully !");
		feedback.show();
		System.out.println("Order Added!");
	}
	
	@FXML
	private void displaySelectedOrder(MouseEvent event) {
		StringTokenizer order=new StringTokenizer(listview.getSelectionModel().getSelectedItem());
		String s= order.nextToken();
		long id = Long.parseLong(s);
		for(int i = 0; i < system.getOrders().size();i++) {
			if(id==system.getOrders().get(i).getId()) {
				textarea.setText(system.getOrders().get(i).receiptDetails() +"\n \n" + NumberFormat.getCurrencyInstance(Locale.US).format(system.getOrders().get(i).receiptTotalAmount() ));
			}
		}
		
	}

	private void loadOnlineOrder(OrderProcessingSystem system, Path orderDataFile) {

		try (Scanner input = new Scanner(orderDataFile)) {

			while (input.hasNext()) {

				OnlineOrder order = system.createOnlineOrder();

				String number = input.next();
				int expireMonth = input.nextInt();
				int expireYear = input.nextInt();
				input.nextLine(); // finish reading the current line
				CreditCard creditCard = new CreditCard(number, expireMonth, expireYear);
				order.setCreditCardInfo(creditCard);

				String shipAddress = input.nextLine();
				order.setShipAddress(shipAddress);

				int numberOfOrderLine = input.nextInt();
				input.nextLine(); // finish reading the current line

				for (int i = 0; i < numberOfOrderLine; i++) {
					String itemDescription = input.next();
					int quantity = input.nextInt();
					double unitPrice = system.getPrice(itemDescription);
					OrderLine orderLine = new OrderLine(itemDescription, quantity, unitPrice);
					order.addOrderLine(orderLine);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
