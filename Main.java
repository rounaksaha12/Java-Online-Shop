/*
Name: Rounak Saha
Roll no.: 20CS30043
SWE Assignment 1 Java
*/

import java.util.*;

//parent class for all entities
class Entity{
	int id;
	String name;
	
	public Entity(Scanner myObj, int id) {//constructor
		System.out.print("Enter name: ");
		this.name = myObj.nextLine();
		this.id = id;
	}
	
	public void printEntity() {//every entity has at least these 2 attributes, this function is further extended
							   //in each child class to include other atrributes of it e.g zipcode etc
		System.out.print("( "+this.id+" )   "+this.name);
	}

	public void created_message(String str){//Every time user creates a new entity this message is shown,
	                                        //input str would be the type of entity e.g MANUFACTURER, SHOP etc
		System.out.println();
		System.out.println("NEW "+str+" CREATED : ");
		System.out.println("( ID )   (Details)");
		System.out.println("------   ---------");
		this.printEntity();
	}
}

class Product extends Entity{
	Manufacturer manufacturer;
	public Product(Scanner myObj, int id) {
		super(myObj,id);
	}
	public void add_to_manufacturer(Manufacturer manf) {//Adds the product to the product list of a manufacturer,
	                                                    //each pdt is made by exactly one manufacturer, so this function 
		                                                //is called only once for each pdt ---> Requirement of functionality 2
		manf.pdt_list.add(this);
	}
	public void printEntity() {
		super.printEntity();
		System.out.println("  [ Manufacturer : "+manufacturer.name+" ( ID : "+manufacturer.id+" ) ]");
	}
	public void created_message(){
		super.created_message("PRODUCT");
	}
}

class Manufacturer extends Entity{
	HashSet<Product> pdt_list;
	public Manufacturer(Scanner myObj, int id) {
		super(myObj, id);
		this.pdt_list = new HashSet<Product>();
	}
	public void print_pdt_list() {
		for(Product j : this.pdt_list) {
			System.out.println("\t+ "+j.name+"( ID: "+j.id+" )");
		}
	}
	public void printEntity() {
		super.printEntity();
		System.out.println();
	}
	public void created_message(){
		super.created_message("MANUFACTURER");
	}
}

class Record{//this is a class that stores **ONE** customer purchase record characterized by shop, product, quantity
			 //purchase history of a customer is stored as a list of such Record objects
	Product pdt;
	Shop shop;
	int copies;//quantity or number of copies of the product purchased
	public Record(Product pdt_inp, Shop shop_inp, int num) {
		this.pdt = pdt_inp;
		this.shop = shop_inp;
		this.copies = num;
	}
	public void printRecord() {
		System.out.println();
		System.out.print("++ Product : ");
		pdt.printEntity();
		System.out.println("  * Number of units : "+copies);
		System.out.print("  * Seller : ");
		shop.printEntity();
	}
}

class Customer extends Entity{
	int zipcode;
	LinkedList< Record > orderLog; //purchase history of the customer
								   //using linked list : the purchase history is kind of permanent, one purchase made is made
	 						       //and its record can never be deleted (unless the customer itself is deleted), hence we never
								   //need to access/delete any particular element in the list. We only add an element to the list
								   //(O(1) for linked list) or print all the elements of it (O(n))
	public Customer(Scanner myObj, int id){
		super(myObj,id);
		System.out.print("Enter Zip Code: ");
		this.zipcode = myObj.nextInt();
		myObj.nextLine();//consumes the newline
		this.orderLog = new LinkedList< Record >();
	}
	public void printEntity() {
		super.printEntity();
		System.out.println(" ( ZIP : "+zipcode+" )");
	}
	public void created_message(){
		super.created_message("CUSTOMER");
	}
}

class Shop extends Entity{
	int zipcode;
	HashMap<Product, Integer> inventory; //we might need to search/access a particular product in the inventory (e.g when that product
										 //or its manufacturer is deleted we need to delete that from the inventory of each shop). In
										 //such cases we need not do linear search over the entire inventory since search in a hashmap
										 //has an expected complexity of O(1)
	public Shop(Scanner myObj, int id) {
		super(myObj,id);
		System.out.print("Enter Zip Code: ");
		this.zipcode = myObj.nextInt();
		myObj.nextLine();//consumes the newline
		this.inventory = new HashMap <Product, Integer>(); 
	}
	public void printEntity() {
		super.printEntity();
		System.out.println(" ( ZIP : "+zipcode+" )");
	}
	public void printInv() {//print all the elements of the inventory of the shop
		System.out.println("\tStocks available: ");
		for(Map.Entry<Product,Integer> mapElement : this.inventory.entrySet()) {//error maybe
			Product pdt = (Product)mapElement.getKey();
			Integer num = (Integer)mapElement.getValue();
			System.out.println("\t+ "+pdt.name+" ( ID: "+pdt.id+" , Manufacturer: "+pdt.manufacturer.name+" ) --- "+num+" unit(s)");
		}
	}
	public void created_message(){
		super.created_message("SHOP");
	}
}

class DeliveryAgnt extends Entity{
	int zipcode;
	int del_count;
	public DeliveryAgnt(Scanner myObj, int id) {
		super(myObj,id);
		System.out.print("Enter Zip Code: ");
		this.zipcode = myObj.nextInt();
		myObj.nextLine();//consumes the newline
		this.del_count = 0;
	}
	public void printEntity() {
		super.printEntity();
		System.out.println(" ( ZIP: "+zipcode+" ) --- "+del_count+" deliveries");
	}
	public void created_message(){
		super.created_message("DELIVERY AGENT");
	}
}

class Order{//this class stores details of **ONE** order placed by a customer characterized by the customer, product and quantity
			//or the number of copies of the pdt needed. An object of this class is created while placing an order and it is then
			//processed/declined depending on availability of suitable shop/delivry agent in the customer zipcode
	Customer customer;
	Product product;
	int count;//number of copies of product
	public Order(LinkedHashMap<Integer,Customer> customer_list, LinkedHashMap<Integer,Product> Product_list, Scanner myObj) {
		
		//contructor of this class essentially performs the act of placing an order

		Features.printCustomer(customer_list);
		System.out.print("Enter id of customer intending to place an order (Refer to the above list for IDs) : ");
		int id_of_customer = myObj.nextInt();
		myObj.nextLine();
		
		Features.printPdt(Product_list);
		System.out.print("Enter id of product the customer is intending to buy (Refer to the above list for IDs) : ");
		int id_of_pdt = myObj.nextInt();
		myObj.nextLine();
		
		if(!customer_list.containsKey(id_of_customer) || !Product_list.containsKey(id_of_pdt)) {

			//case of invalid id entry

			this.count = -1; //setting number of quantities to -1 is a flag that this order cannot be further processed
			return;
		}
		
		this.customer = customer_list.get(id_of_customer);
		this.product = Product_list.get(id_of_pdt);
		
		System.out.print("Enter the number of copies of the above product: ");
		this.count = myObj.nextInt();
		myObj.nextLine();
	}
}

class Features{
	
	//Create, delete and print fentities ---> Requirement of functionality 1

	//Create, delete and print features on: Manufacturer
	public static void createManufacturer(LinkedHashMap<Integer,Manufacturer> manf_list, Scanner myObj, int id) {
		System.out.println();
		System.out.println("Provide the necessary details : ");
		System.out.println("--------------------------------");
		Manufacturer temp = new Manufacturer(myObj,id);
		manf_list.put(id,temp);
		temp.created_message();
	}
	public static void deleteManufacturer(LinkedHashMap<Integer,Manufacturer> manf_list, LinkedHashMap<Integer,Product> Product_list, LinkedHashMap<Integer,Shop> shop_list, Scanner myObj) {
		if(manf_list.isEmpty()) {
			System.out.println();
			System.out.println("ERROR : NO MANUFACTURER PRESENT IN DATABASE ! UNABLE TO PROCESS YOUR REQUEST :(");
			System.out.println();
			return;
		}
		int id_to_delete;
		printManf(manf_list,false);
		System.out.print("Enter the ID of the manufacturer to be deleted (Refer to the above list for IDs) : ");
		id_to_delete = myObj.nextInt();
		myObj.nextLine();
		if(!manf_list.containsKey(id_to_delete)) {
			System.out.println();
			System.out.println("ERROR : NO SUCH MANUFACTURER FOUND ! UNABLE TO PROCESS YOUR REQUEST :(");
			System.out.println();
			return;
		}

		//first delete each of its products from all places
		for(Product i : manf_list.get(id_to_delete).pdt_list) {
			Product_list.remove(i.id);//remove the product from the central product list
			//remove the product from inventory of each shop/warehouse
			for(Map.Entry<Integer,Shop> mapEntry : shop_list.entrySet()) {
				mapEntry.getValue().inventory.remove(i);
			}
		}
		manf_list.remove(id_to_delete);//remove the manufacturer from the central manufacturer list
	}
	public static void printManf(LinkedHashMap<Integer,Manufacturer> manf_list, boolean showPdts) {
		if(manf_list.isEmpty()) {
			System.out.println();
			System.out.println("ERROR : NO MANUFACTURER PRESENT IN DATABASE ! ");
			System.out.println();
			return;
		}
		System.out.println();
		System.out.println("( ID )   (Details)");
		System.out.println("------   ---------");
		for(Map.Entry<Integer,Manufacturer> mapElement : manf_list.entrySet()) {
			mapElement.getValue().printEntity();
			if(showPdts) {
				mapElement.getValue().print_pdt_list();
			}
		}
		System.out.println();
	}
	
	//Create, delete and print features on: Product
	public static void createProduct(LinkedHashMap<Integer,Manufacturer> manf_list, LinkedHashMap<Integer,Product> Product_list, LinkedHashMap<Integer,Shop> shop_list, Scanner myObj, int id) {
		if(manf_list.isEmpty()) {
			System.out.println();
			System.out.println("ERROR : NO MANUFACTURER PRESENT IN DATABSE ! YOU NEED TO REGISTER AT LEAST ONE MANUFACTURER BEFORE ADDING ANY PRODUCT !");
			System.out.println();
			//Since each product can have exactly one manufacturer it is necessary that the user must have first registered the manufacturer
			//to the database before trying to register one of its products, for that matter at least one manufacturer must be present while 
			//trying to add a product
			return;
		}
		System.out.println();
		System.out.println("Provide the necessary details : ");
		System.out.println("--------------------------------");
		Product temp = new Product(myObj,id);
		int id_of_manufacturer;
		printManf(manf_list,false);
		System.out.print("Enter the id of its manufacturer (Refer to the above list for IDs) : ");
		id_of_manufacturer = myObj.nextInt();
		myObj.nextLine();
		if(!manf_list.containsKey(id_of_manufacturer)) {
			System.out.println();
			System.out.println("ERROR : NO SUCH MANUFACTURER FOUND ! UNABLE TO PROCESS YOUR REQUEST :(");
			System.out.println();
			return;
		}
		temp.manufacturer = manf_list.get(id_of_manufacturer); //set the manufacturer attribute of the product
		temp.add_to_manufacturer(manf_list.get(id_of_manufacturer)); //add the pdt to the list of products of its manufacturer
		
		Product_list.put(id,temp);//add the pdt to the central product list

		//add 0 copies of the pdt to the inventory of each shop
		for(Map.Entry<Integer,Shop> mapElement : shop_list.entrySet()) {
			mapElement.getValue().inventory.put(temp,0);
		}
		temp.created_message();
	}
	public static void deleteProduct(LinkedHashMap<Integer,Manufacturer> manf_list, LinkedHashMap<Integer,Product> Product_list, LinkedHashMap<Integer,Shop> shop_list, Scanner myObj) {
		if(Product_list.isEmpty()) {
			System.out.println();
			System.out.println("ERROR : NO PRODUCT PRESENT IN DATABASE ! UNABLE TO PROCESS YOUR REQUEST :(");
			System.out.println();
			return;
		}
		int id_to_delete;
		printPdt(Product_list);
		System.out.print("Enter the id of the product to delete (Refer to the above list for IDs) : ");
		id_to_delete = myObj.nextInt();
		myObj.nextLine();
		if(!Product_list.containsKey(id_to_delete)) {
			System.out.println();
			System.out.println("ERROR : NO SUCH PRODUCT FOUND ! UNABLE TO PROCESS YOUR REQUEST :(");
			System.out.println();
			return;
		}
		Product pdt_to_delete = Product_list.get(id_to_delete);
		//delete the product from the central product list
		Product_list.remove(id_to_delete);
		//delete the product from its manufacturer list
	    manf_list.get(pdt_to_delete.manufacturer.id).pdt_list.remove(pdt_to_delete);
		//delete the product from every shop/warehouse
	    for(Map.Entry<Integer,Shop> mapElement : shop_list.entrySet()) {
			mapElement.getValue().inventory.remove(pdt_to_delete);
		}
	}
	public static void printPdt(LinkedHashMap<Integer,Product> Product_list) {
		if(Product_list.isEmpty()) {
			System.out.println();
			System.out.println("ERROR : NO PRODUCT PRESENT IN DATABASE ! UNABLE TO PROCESS YOUR REQUEST :(");
			System.out.println();
			return;
		}
		System.out.println();
		System.out.println("( ID )   (Details)");
		System.out.println("------   ---------");
		for(Map.Entry<Integer,Product> mapElement : Product_list.entrySet()) {
			mapElement.getValue().printEntity();
		}
		System.out.println();
	}
	//Create, delete and print features on: Customer
	public static void createCustomer(LinkedHashMap<Integer,Customer> customer_list, Scanner myObj, int id) {
		System.out.println();
		System.out.println("Provide the necessary details : ");
		System.out.println("--------------------------------");
		Customer temp = new Customer(myObj,id);
		customer_list.put(id,temp);
		temp.created_message();
	}
	public static void deleteCustomer(LinkedHashMap<Integer,Customer> customer_list, Scanner myObj) {
		if(customer_list.isEmpty()) {
			System.out.println();
			System.out.println("ERROR : NO CUSTOMER PRESENT IN DATABASE ! UNABLE TO PROCESS YOUR REQUEST :(");
			System.out.println();
			return;
		}
		int id_to_delete;
		printCustomer(customer_list);
		System.out.print("Enter the id of the customer to delete (Refer to the above list for IDs) : ");
		id_to_delete = myObj.nextInt();
		myObj.nextLine();
		if(!customer_list.containsKey(id_to_delete)) {
			System.out.println();
			System.out.println("ERROR : NO SUCH CUSTOMER FOUND ! UNABLE TO PROCESS YOUR REQUEST :(");
			System.out.println();
			return;
		}
		//sufficient to delete the customer from the central customer list only
		customer_list.remove(id_to_delete);
	}
	public static void printCustomer(LinkedHashMap<Integer,Customer> customer_list) {
		if(customer_list.isEmpty()) {
			System.out.println();
			System.out.println("ERROR : NO CUSTOMER PRESENT IN DATABASE ! UNABLE TO PROCESS YOUR REQUEST :(");
			System.out.println();
			return;
		}
		System.out.println();
		System.out.println("( ID )   (Details)");
		System.out.println("------   ---------");
		for(Map.Entry<Integer,Customer> mapEntry : customer_list.entrySet()) {
			mapEntry.getValue().printEntity();
		}
		System.out.println();
	}

	//Create, delete and print features on: Shop
	public static void createShop(LinkedHashMap<Integer,Shop> shop_list, LinkedHashMap<Integer,Product> Product_list,Scanner myObj, int id) {
		System.out.println();
		System.out.println("Provide the necessary details : ");
		System.out.println("--------------------------------");
		Shop temp = new Shop(myObj,id);
		//add the currently existing products to the inventory of the shop with number of copies of each item is 0
		for(Map.Entry<Integer,Product> mapElement : Product_list.entrySet()) {
			temp.inventory.put(mapElement.getValue(), 0);
		}
		shop_list.put(id, temp);
		temp.created_message();
	}
	public static void deleteShop(LinkedHashMap<Integer,Shop> shop_list, Scanner myObj) {
		if(shop_list.isEmpty()) {
			System.out.println();
			System.out.println("ERROR : NO SHOP PRESENT IN DATABASE ! UNABLE TO PROCESS YOUR REQUEST :(");
			System.out.println();
			return;
		}
		int id_to_delete;
		printShop(shop_list,false);
		System.out.print("Enter the id of the shop to delete (Refer to the above list for IDs) : ");
		id_to_delete = myObj.nextInt();
		myObj.nextLine();
		if(!shop_list.containsKey(id_to_delete)) {
			System.out.println();
			System.out.println("ERROR : NO SUCH SHOP FOUND ! UNABLE TO PROCESS YOUR REQUEST :(");
			System.out.println();
			return;
		}
		shop_list.remove(id_to_delete);
	}
	public static void printShop(LinkedHashMap<Integer,Shop> shop_list, boolean showInv) {
		if(shop_list.isEmpty()) {
			System.out.println();
			System.out.println("ERROR : NO SHOP PRESENT IN DATABASE ! UNABLE TO PROCESS YOUR REQUEST :(");
			System.out.println();
			return;
		}
		System.out.println();
		System.out.println("( ID )   (Details)");
		System.out.println("------   ---------");
		for(Map.Entry<Integer,Shop> mapElement : shop_list.entrySet()) {
			mapElement.getValue().printEntity();
			if(showInv) {
				mapElement.getValue().printInv();
			}
		}
		System.out.println();
	}

	//Create, delete and print features on: Delivery agent
	public static void createDelA(LinkedHashMap<Integer,DeliveryAgnt> delA_list, Scanner myObj,int id) {
		System.out.println();
		System.out.println("Provide the necessary details : ");
		System.out.println("--------------------------------");
		DeliveryAgnt temp = new DeliveryAgnt(myObj,id);
		delA_list.put(id, temp);
		temp.created_message();
	}
	public static void deleteDelA(LinkedHashMap<Integer,DeliveryAgnt> delA_list, Scanner myObj) {
		if(delA_list.isEmpty()) {
			System.out.println();
			System.out.println("ERROR : NO DELIVERY AGENT PRESENT IN DATABASE ! UNABLE TO PROCESS YOUR REQUEST :(");
			System.out.println();
			return;
		}
		int id_to_delete;
		printDelA(delA_list);
		System.out.print("Enter the id of the delivery agent to delete (Refer to the above list for IDs) : ");
		id_to_delete = myObj.nextInt();
		myObj.nextLine();
		if(!delA_list.containsKey(id_to_delete)) {
			System.out.println();
			System.out.println("ERROR : NO SUCH DELIVERY AGENT FOUND ! UNABLE TO PROCESS YOUR REQUEST :(");
			System.out.println();
			return;
		}
		//sufficient to delete the delivery agent from the central delivery agent list only
		delA_list.remove(id_to_delete);	
	}
	public static void printDelA(LinkedHashMap<Integer,DeliveryAgnt> delA_list) {
		if(delA_list.isEmpty()) {
			System.out.println();
			System.out.println("ERROR : NO DELIVERY AGENT PRESENT IN DATABASE ! UNABLE TO PROCESS YOUR REQUEST :(");
			System.out.println();
			return;
		}
		System.out.println();
		System.out.println("( ID )   (Details)");
		System.out.println("------   ---------");
		for(Map.Entry<Integer,DeliveryAgnt> mapElement : delA_list.entrySet()) {
			mapElement.getValue().printEntity();
		}
		System.out.println();
	}
	
	//ADD A CERTAIN NUMBER OF A PRODUCT TO A SHOP ---> Requirement of functionality 3
	public static void add_prod_to_shop(LinkedHashMap<Integer,Shop> shop_list, LinkedHashMap<Integer,Product> Product_list, Scanner myObj) {
		if(shop_list.isEmpty() || Product_list.isEmpty()) {
			System.out.println();
			System.out.println("UNABLE TO PROCESS REQUEST DUE TO ABSENCE OF ANY SHOP OR/AND PRODUCT IN THE DATABASE :(");
			System.out.println();
			return;
		}
		System.out.println();
		System.out.println("Provide the necessary details : ");
		System.out.println("--------------------------------");
		int id_of_shop, id_of_pdt, number_of_copies;
		printShop(shop_list,false);
		System.out.print("Enter id of the shop to which product has to be added (Refer to the above list for IDs) : ");
		id_of_shop = myObj.nextInt();
		myObj.nextLine();
		if(!shop_list.containsKey(id_of_shop)) {
			System.out.println();
			System.out.println("ERROR : NO SUCH SHOP FOUND ! UNABLE TO PROCESS YOUR REQUEST :(");
			System.out.println();
			return;
		}
		printPdt(Product_list);
		System.out.print("Enter id of the product to be added (Refer to the above list for IDs) : ");
		id_of_pdt = myObj.nextInt();
		myObj.nextLine();
		System.out.print("Enter number of copies of the product to be added: ");
		number_of_copies = myObj.nextInt();
		myObj.nextLine();
		if(!Product_list.containsKey(id_of_pdt)) {
			System.out.println();
			System.out.println("ERROR : NO PRODUCT FOUND ! UNABLE TO PROCESS YOUR REQUEST :(");
			System.out.println();
			return;
		}
		int prev_count = (shop_list.get(id_of_shop).inventory).get(Product_list.get(id_of_pdt));
		//prev_count is the number of copies of the product already present in the concerned shop
		(shop_list.get(id_of_shop).inventory).put(Product_list.get(id_of_pdt), prev_count+number_of_copies);
	}
	
	//LIST ALL THE PURCHASES MADE BY A CUSTOMER ---> Requirement of functionality 6
	public static void customer_purchase_record(LinkedHashMap<Integer,Customer> customer_list, Scanner myObj) {
		if(customer_list.isEmpty()) {
			System.out.println();
			System.out.println("ERROR : NO CUSTOMER PRESENT IN DATABASE ! UNABLE TO PROCESS YOUR REQUEST :(");
			System.out.println();
			return;
		}
		printCustomer(customer_list);
		System.out.print("Enter the id of the customer whose purchage record to view (Refer to the above list for IDs) : ");
		int id_to_view = myObj.nextInt();
		myObj.nextLine();
		System.out.println();
		if(!customer_list.containsKey(id_to_view)) {
			System.out.println();
			System.out.println("ERROR : NO SUCH CUSTOMER FOUND ! UNABLE TO PROCESS YOUR REQUEST :(");
			System.out.println();
			return;
		}
		if(customer_list.get(id_to_view).orderLog.isEmpty()){
			System.out.println("NO PURCHASE MADE SO FAR");
			return;
		}
		for(Record i : customer_list.get(id_to_view).orderLog) {
			i.printRecord();
			System.out.println();
		}
	}
	
	//LIST INVENTORY OF A SHOP ---> Requirements of functionality 7
	public static void list_inventory(LinkedHashMap<Integer,Shop> shop_list, Scanner myObj) {
		if(shop_list.isEmpty()) {
			System.out.println();
			System.out.println("ERROR : NO SHOP PRESENT IN DATABASE ! UNABLE TO PROCESS YOUR REQUEST :(");
			System.out.println();
			return;
		}
		printShop(shop_list,false);
		System.out.print("Enter the id of the shop whose inventory to view (Refer to the above list for IDs) : ");
		int id_to_view = myObj.nextInt();
		myObj.nextLine();
		System.out.println();
		if(!shop_list.containsKey(id_to_view)) {
			System.out.println();
			System.out.println("ERROR : NO SUCH SHOP FOUND ! UNABLE TO PROCESS YOUR REQUEST :(");
			System.out.println();
			return;
		}
		shop_list.get(id_to_view).printEntity();
		shop_list.get(id_to_view).printInv();
	}
	
	//LIST PRODUCTS MADE BY A MANUFACTURER ---> Requirement of functionality 8
	public static void list_pdt(LinkedHashMap<Integer,Manufacturer> manf_list, Scanner myObj) {
		if(manf_list.isEmpty()) {
			System.out.println();
			System.out.println("ERROR : NO MANUFACTURER PRESENT IN DATABASE ! UNABLE TO PROCESS YOUR REQUEST :(");
			System.out.println();
			return;
		}
		printManf(manf_list,false);
		System.out.print("Enter the id of the manufacturer whose product range to view (Refer to the above list for IDs) : ");
		int id_to_view = myObj.nextInt();
		myObj.nextLine();
		System.out.println();
		if(!manf_list.containsKey(id_to_view)) {
			System.out.println();
			System.out.println("ERROR : NO SUCH MANUFACTURER FOUND ! UNABLE TO PROCESS YOUR REQUEST :(");
			System.out.println();
			return;
		}
		if(manf_list.get(id_to_view).pdt_list.isEmpty()){
			System.out.println("NO PRODUCT ADDED SO FAR");
			return;
		}
		manf_list.get(id_to_view).printEntity();
		manf_list.get(id_to_view).print_pdt_list();
	}
	
	//PLACE AND PROCESS AN ORDER
	public static void dealOrder(LinkedHashMap<Integer,Customer> customer_list,LinkedHashMap<Integer,Product> Product_list, LinkedHashMap<Integer,Shop> shop_list, LinkedHashMap<Integer,DeliveryAgnt> delA_list,Scanner myObj) {
		System.out.println();
		System.out.println("Provide the necessary details : ");
		System.out.println("--------------------------------");
		if(customer_list.isEmpty() || Product_list.isEmpty()) {
			System.out.println();
			System.out.println("UNABLE TO PROCESS REQUEST DUE TO ABSENCE OF ANY CUSTOMER OR/AND PRODUCT IN THE DATABASE : ");
			System.out.println();
			return;
		}
		Order order = new Order(customer_list, Product_list,myObj); //ADD AN ORDER OF A CERTAIN NUMBER OF PRODUCTS BY A CUSTOMER
																	//---> Requirement of functionality 4
		
		if(order.count == -1) {//order.count is set to -1 only if the user enters wrong id while specifying customer/product for the order
							   //see constructor of Order class
			System.out.println();
			System.out.println("ERROR : INVALID ID ENTERED : ");
			System.out.println();
			return;
		}
		
		//PROCESS THE ORDER OR DECLINE IT BASED ON AVAILABILTY OF SUITABLE SHOP/SELIVERY AGENT ---> Requirement of functionality 5
		for(Map.Entry<Integer,Shop> shopElement : shop_list.entrySet()) {
			//look for a suitable shop which has same zip code and also has >= copies required of the desired pdt
			Shop i = shopElement.getValue();
			if(i.zipcode == order.customer.zipcode && i.inventory.get(order.product)>=order.count) {
				//suitable shop found is i
				//hence find a suitable delivery agent(**SAME ZIP CODE** and minimum number of deliveries so far) and change his del_count
				int min = 999999, minId = -1;

				for(Map.Entry<Integer,DeliveryAgnt> delAElement : delA_list.entrySet()) {
					DeliveryAgnt j = delAElement.getValue();
					if(j.zipcode == order.customer.zipcode) {
						if(j.del_count<min) {
							min = j.del_count;
							minId = j.id;
						}
					}
				}
				if(minId == -1) {
					System.out.println("NO SUITABLE DELIVERY AGENT WAS FOUND IN THE AREA (ZIP CODE MISMATCH)!");
					System.out.println("UNABLE TO PROCESS ORDER :(");
					return;
				}
				DeliveryAgnt suitable_deliveryA = delA_list.get(minId);//this guy does the delivery and so his del_count increases by 1
				suitable_deliveryA.del_count++;
				delA_list.replace(suitable_deliveryA.id, suitable_deliveryA);//save the changes in central list of delivery agents
				
				//reduce the number of available copies of the purchased product from the inventory of the shop
				int updated_number_of_copies = i.inventory.get(order.product)-order.count;
				i.inventory.replace(order.product, updated_number_of_copies);
				
				//record the purchase in the orderLog of the customer
				Record newRecord = new Record(order.product,i,order.count);
				customer_list.get(order.customer.id).orderLog.add(newRecord);
				
				System.out.println();
				System.out.println("Order processed successfully!");
				System.out.println("  Order & delivery details  ");
				System.out.println("++++++++++++++++++++++++++++++");
				System.out.print("* Product        : ");
				order.product.printEntity();
				System.out.print("* Seller         : ");
				i.printEntity();
				System.out.println("* Quantity       : "+order.count+" unit(s)");
				System.out.print("* Customer       : ");
				order.customer.printEntity();
				System.out.print("* Delivery Agent : ");
				suitable_deliveryA.printEntity();
				System.out.println();
				
				return;
			}
		}
		System.out.println("PRODUCT NOT AVAILABLE IN SHOPS/WAREHOUSES IN AREA WITH ZIP CODE: "+order.customer.zipcode);
		System.out.println("UNABLE TO PROCESS ORDER :(");
		return;
	}
}

//class for menu based master text based interface including display messages and the main loop ---> Requirement of functionality 9
class text_based_interface{
	/*

	REASON FOR USING LINKED HASH MAP:

	ID of every entity is a unique integer, we can use this property to create hash maps with ID being the key
	to get expected O(1) access to the elements in the list of entities. Otherwise we might need to do linear
	search over the entire list to find an element with a given ID.
	We often need to go through all the elements of the list, using simple hash map this traversal is O(n+m) where m
	is the capacity of the hash map, linked hash map ensures a complexity of O(n)    

	*/
	LinkedHashMap<Integer,Manufacturer> manf_list;
	LinkedHashMap<Integer,Product> Product_list;
	LinkedHashMap<Integer,Customer> customer_list;
	LinkedHashMap<Integer,Shop> shop_list;
	LinkedHashMap<Integer,DeliveryAgnt> delA_list;
	Scanner myObj;
	int current_id;
	
	public text_based_interface(){
		manf_list = new LinkedHashMap<Integer,Manufacturer>();
		Product_list = new LinkedHashMap<Integer,Product>();
		customer_list = new LinkedHashMap<Integer,Customer>();
		shop_list = new LinkedHashMap<Integer,Shop>();
		delA_list = new LinkedHashMap<Integer,DeliveryAgnt>();
		myObj = new Scanner(System.in);
		current_id = 1;
	}
	
	void printBanner() {
		System.out.println();
		System.out.println("++========================================================================++");
		System.out.println();
	}
	
	void print_welcome_message() {
		System.out.println();
		System.out.println("    ++============================================================++");
		System.out.println("    ++ WELCOME TO THE ONLINE PHARMACY MANAGEMENT SYSTEM INTERFACE ++");
		System.out.println("    ++============================================================++");
		System.out.println();
		//printBanner();
	}
	
	char continue_resp() { // asks the user and returns whether the user of the interface wants to continue another round of operations
		System.out.println();
		System.out.print("Do you want to continue ? (Y/N): ");
		char response = myObj.next().charAt(0);
		myObj.nextLine();
		printBanner();
		return response;
	}
	
	void main_loop() { //user is shown menu and asked what he/she wants
		System.out.println("++ Choose from the following options ++");
		System.out.println();
		System.out.println("   * To create an entity                  : Press 1");
		System.out.println("   * To delete an entity                  : Press 2");
		System.out.println("   * To view the contents of the database : Press 3");
		System.out.println("   * To add products to a shop            : Press 4");
		System.out.println("   * To place a customer order            : Press 5");
		System.out.println();
		System.out.print(">>Enter your response here: ");
		int response1 = myObj.nextInt();
		myObj.nextLine();
		System.out.println();
		
		switch(response1) {
		case 1: {
			System.out.println();
			System.out.println("   ++ Which type of entity do you want to create ? ++");
			System.out.println("      * Manufacturer   : Press 1");
			System.out.println("      * Product        : Press 2");
			System.out.println("      * Shop           : Press 3");
			System.out.println("      * Customer       : Press 4");
			System.out.println("      * Delivery agent : Press 5");
			System.out.println();
			System.out.print("   Enter your response here: ");
			int response2 = myObj.nextInt();
			myObj.nextLine();
			switch(response2) {
			case 1:{
				Features.createManufacturer(manf_list, myObj, current_id);
				current_id++;
				break;
			}
			case 2:{
				Features.createProduct(manf_list, Product_list, shop_list, myObj, current_id);
				current_id++;
				break;
			}
			case 3:{
				Features.createShop(shop_list, Product_list, myObj, current_id);
				current_id++;
				break;
			}
			case 4:{
				Features.createCustomer(customer_list, myObj,current_id);
				current_id++;
				break;
			}
			case 5:{
				Features.createDelA(delA_list, myObj,current_id);
				current_id++;
				break;
			}
			}
			break;
		}
		case 2: {
			System.out.println("   ++ Which type of entity do you want to delete ? ++");
			System.out.println("      * Manufacturer   : Press 1");
			System.out.println("      * Product        : Press 2");
			System.out.println("      * Shop           : Press 3");
			System.out.println("      * Customer       : Press 4");
			System.out.println("      * Delivery agent : Press 5");
			System.out.println();
			System.out.print(">>Enter your response here: ");
			int response2 = myObj.nextInt();
			myObj.nextLine();
			System.out.println();
			switch(response2) {
			case 1:{
				Features.deleteManufacturer(manf_list, Product_list, shop_list, myObj);
				break;
			}
			case 2:{
				Features.deleteProduct(manf_list, Product_list, shop_list, myObj);
				break;
			}
			case 3:{
				Features.deleteShop(shop_list, myObj);
				break;
			}
			case 4:{
				Features.deleteCustomer(customer_list, myObj);
				break;
			}
			case 5:{
				Features.deleteDelA(delA_list, myObj);
				break;
			}
			}
			break;
		}
		case 3:{
			System.out.println("   ++ Which of the following operations ? ++");
			System.out.println("      * View the product range of a manufacturer   : Press 1");
			System.out.println("      * View the product inventory of a shop       : Press 2");
			System.out.println("      * View the purchase record of a customer     : Press 3");
			System.out.println("      Other options : ");
			System.out.println("          * List down all the manufacturers   : Press 4");
			System.out.println("          * List down all the products        : Press 5");
			System.out.println("          * List down all the shops           : Press 6");
			System.out.println("          * List down all the customers       : Press 7");
			System.out.println("          * List down all the delivery agents : Press 8");
			System.out.println();
			System.out.print(">>Enter your response here: ");
			int response2 = myObj.nextInt();
			myObj.nextLine();
			switch(response2) {
			case 1:{
				Features.list_pdt(manf_list, myObj);
				break;
			}
			case 2:{
				Features.list_inventory(shop_list, myObj);
				break;
			}
			case 3:{
				Features.customer_purchase_record(customer_list, myObj);
				break;
			}
			case 4:{
				System.out.print("Show product range alongside ? (Y/N) : ");
				char ans = myObj.next().charAt(0);
				myObj.nextLine();
				if(ans == 'Y') {
					Features.printManf(manf_list, true);
				}
				else {
					Features.printManf(manf_list, false);
				}
				break;
			}
			case 5:{
				Features.printPdt(Product_list);
				break;
			}
			case 6:{
				System.out.print("Show product inventory alongside ? (Y/N) : ");
				char ans = myObj.next().charAt(0);
				myObj.nextLine();
				if(ans == 'Y') {
					Features.printShop(shop_list, true);
				}
				else {
					Features.printShop(shop_list, false);
				}
				break;
			}
			case 7:{
				Features.printCustomer(customer_list);
				break;
			}
			case 8:{
				Features.printDelA(delA_list);
				break;
			}
			}
			break;
		}
		case 4:{
			Features.add_prod_to_shop(shop_list, Product_list, myObj);
			break;
		}
		case 5:{
			Features.dealOrder(customer_list, Product_list, shop_list, delA_list, myObj);
			break;
		}
		}
	}
	public void operate() {
		char shouldContinue = 'Y';
		print_welcome_message();
		while(shouldContinue == 'Y') {
			main_loop();
			shouldContinue = continue_resp();
		}
		System.out.println("Program terminated. Thank you!");
	}
}

public class Main {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		text_based_interface run = new text_based_interface();
		
		run.operate();
		
	}

}



