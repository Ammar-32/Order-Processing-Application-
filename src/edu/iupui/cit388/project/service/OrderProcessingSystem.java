package edu.iupui.cit388.project.service;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import edu.iupui.cit388.project.model.Item;
import edu.iupui.cit388.project.model.OnlineOrder;

public class OrderProcessingSystem {

	private static long storeOrderId = 1000;
	
	private static long onlineOrderId = 1000;
	
	private static Map<String, Item> items = new HashMap<>(); 
		
	private List<OnlineOrder> orders = new ArrayList<>();
	
	public OrderProcessingSystem(Path pathToItemDataFile) {
	
		try (Scanner input = new Scanner(pathToItemDataFile)) {
			
			while(input.hasNext()) {
				String name = input.next();
				double price = input.nextDouble();
				Item item = new Item(name, price);
				items.put(name, item);
			}
		}
		catch(Exception e) {
			e.printStackTrace();
		}
		
		for (String name : items.keySet()) {
			System.out.println(items.get(name));
		}
		System.out.println("Items Load completed! \n\n");
	}
	
	public OnlineOrder createOnlineOrder() {
		OnlineOrder storeOrder = new OnlineOrder(onlineOrderId++, Calendar.getInstance());
		orders.add(storeOrder);
		return storeOrder;
	}

	public static Map<String, Item> getItems() {
		return items;
	}

	public static void setItems(Map<String, Item> items) {
		OrderProcessingSystem.items = items;
	}

	public double getPrice(String itemName) {
		return items.get(itemName).getPrice();
	}

	public List<OnlineOrder> getOrders() {
		return orders;
	}
	public List<String> getOrdersInString(){
		List<String> list = new ArrayList<String>();
		for(int i =0; i<this.getOrders().size();i++) {
			int k = 1000+i;
			list.add(String.valueOf(k) +"\r" + this.getOrders().get(i).getAdditionalInfo());
		
		}
		return list;
	}
	public List<Item> getItemforCombobox(){
		List<Item> list = new ArrayList<Item>();
		for (Item i : this.getItems().values()) {
			  list.add(i);
			}
		return list;
	}

}
