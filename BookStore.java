// Name: Tyler Gauntlett
// Date 8/28/2016
// Course: CNT4714-16FALL

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import java.awt.Color;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.Scanner;
import java.util.TimeZone;

@SuppressWarnings("serial")
public class BookStore extends JPanel implements ActionListener {

	protected JButton processItem, confirmItem, viewOrder, finishOrder, newOrder, exit;
	protected int itemNumber = 1;
	protected int currentItemNumber = 0;
	protected String currentGUIItemInfo = "";
	protected int itemsInOrder;
	protected double subtotal = 0;
	protected static double taxRate = .06;
	protected static LinkedList<Integer> bookIdList = new LinkedList<Integer>();
	protected static LinkedList<String> bookInfoList = new LinkedList<String>();
	protected static LinkedList<Double> bookPriceList = new LinkedList<Double>();
	protected static LinkedList<String> checkoutInfoGUIList = new LinkedList<String>();
	protected static LinkedList<String> checkoutInfoWriteList = new LinkedList<String>();

	public BookStore() {

		// Create all label and input boxes.
		JLabel numOfItemsLabel = new JLabel("Enter number of items in this order:");
		JTextField numOfItemsField = new JTextField(50);

		JLabel bookIdLabel = new JLabel("Enter book ID for Item #" + itemNumber + ":");
		JTextField bookIdField = new JTextField(50);

		JLabel quanityLabel = new JLabel("Enter quanity for Item #" + itemNumber + ":");
		JTextField quanityField = new JTextField(50);

		// TODO: Update this item number based on the number of confirmed items.
		JLabel itemLabel = new JLabel("Item #" + itemNumber + " info:");
		JTextField itemField = new JTextField(50);
		itemField.setEditable(false);

		JLabel subtotalLabel = new JLabel("Order subtotal for " + currentItemNumber + " item(s):");
		JTextField subtotalField = new JTextField(50);
		subtotalField.setEditable(false);

		// Create all buttons with action listener and action listener status.
		processItem = new JButton("Process Item #" + itemNumber);
		processItem.setEnabled(true);
		processItem.setBackground(Color.YELLOW);
		processItem.setOpaque(true);
		processItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {

				// Check if the user entered in data in all 3 fields.
				if (numOfItemsField.getText().isEmpty() || bookIdField.getText().isEmpty()
						|| quanityField.getText().isEmpty()) {
					JOptionPane.showMessageDialog(null, "Please fill required fields before processing order.");
				}

				// Check if data entered in all 3 fields are numbers.
				else if (!isNumeric(numOfItemsField.getText()) || !isNumeric(bookIdField.getText())
						|| !isNumeric(quanityField.getText())) {
					JOptionPane.showMessageDialog(null,
							"Please fill required fields with numbers before processing order.");
				} else if (Double.parseDouble(quanityField.getText()) <= 0) {
					JOptionPane.showMessageDialog(null, "Quanity must be a positive number.");
				}

				// Check if the book id exists.
				else if (bookIdList.contains(Integer.parseInt(bookIdField.getText()))) {
					currentGUIItemInfo = getGUIText(Integer.parseInt(numOfItemsField.getText()),
							Integer.parseInt(bookIdField.getText()), Integer.parseInt(quanityField.getText()));

					updateSubtotal(Integer.parseInt(numOfItemsField.getText()), Integer.parseInt(bookIdField.getText()),
							Integer.parseInt(quanityField.getText()));

					itemField.setText(currentGUIItemInfo);

					processItem.setEnabled(false);
					confirmItem.setEnabled(true);

					// Color confirm yellow and processing back to default.
					confirmItem.setBackground(Color.YELLOW);
					processItem.setBackground(null);
				}
				// If the id cannot be found, prompt the user to enter a valid
				// id.
				else {
					JOptionPane.showMessageDialog(null, "Book ID " + bookIdField.getText() + " not in file");
				}
			}
		});

		// Confirm button to add the item to the cart.
		confirmItem = new JButton("Confirm Item #" + itemNumber);
		confirmItem.setEnabled(false);
		confirmItem.setOpaque(true);
		confirmItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JOptionPane.showMessageDialog(null, "Item #" + itemNumber + " accepted");

				// Update order based on added items.
				itemNumber++;
				currentItemNumber++;
				processItem.setText("Process Item #" + itemNumber);
				confirmItem.setText("Confirm Item #" + itemNumber);
				checkoutInfoWriteList.add(getWriteText(Integer.parseInt(numOfItemsField.getText()),
						Integer.parseInt(bookIdField.getText()), Integer.parseInt(quanityField.getText())));

				// Update fields to allow for processing the next item.
				itemsInOrder = Integer.parseInt(numOfItemsField.getText());
				numOfItemsField.setEnabled(false);
				bookIdField.setText("");
				quanityField.setText("");
				subtotalField.setText("$" + subtotal);
				subtotalLabel.setText("Order subtotal for " + currentItemNumber + " item(s):");

				// Add the item to the checkout list containing all orders.
				checkoutInfoGUIList.add(currentGUIItemInfo);

				// Check if the customer has reached the originally request
				// amount of items.
				if (Integer.parseInt(numOfItemsField.getText()) == currentItemNumber) {
					processItem.setEnabled(false);
					confirmItem.setEnabled(false);
					viewOrder.setEnabled(true);
					finishOrder.setEnabled(true);

					processItem.setText("Process Item");
					confirmItem.setText("Confirm Item");

					confirmItem.setBackground(null);
					processItem.setBackground(null);

					bookIdLabel.setText("");
					quanityLabel.setText("");

					bookIdField.setEnabled(false);
					quanityField.setEnabled(false);
				} else {

					// Color the process button yellow and confirm back to
					// default.
					confirmItem.setBackground(null);
					processItem.setBackground(Color.YELLOW);

					// Set button availability.
					processItem.setEnabled(true);
					confirmItem.setEnabled(false);
					viewOrder.setEnabled(true);
					finishOrder.setEnabled(true);
				}
			}
		});

		// Print confirmed items in current order.
		viewOrder = new JButton("View Order");
		viewOrder.setEnabled(false);
		viewOrder.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {

				StringBuilder sb = new StringBuilder();

				for (int i = 0; i < checkoutInfoGUIList.size(); i++) {
					sb.append((i + 1) + ".  " + checkoutInfoGUIList.get(i) + "\n");
				}

				JOptionPane.showMessageDialog(null, sb.toString());
			}
		});

		finishOrder = new JButton("Finish order");
		finishOrder.setEnabled(false);
		finishOrder.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// Store this in file System.currentTimeMillis() later.

				DateFormat dateFormat = new SimpleDateFormat("dd/mm/yy hh:mm:ss a");
				Date date = new Date();

				StringBuilder sb = new StringBuilder();

				// Print items in order.
				for (int i = 0; i < checkoutInfoGUIList.size(); i++) {
					sb.append((i + 1) + ".  " + checkoutInfoGUIList.get(i) + "\n");
				}

				// Round the double to 2 decimal places.
				double orderTotal = 100 * (subtotal * (1 + taxRate));
				orderTotal = (int) orderTotal;
				orderTotal = (orderTotal / 100);

				// Round the double to 2 decimal places.
				double taxAmount = 100 * (orderTotal - subtotal);
				taxAmount = (int) taxAmount;
				taxAmount = (taxAmount / 100);

				String outputMessage = "Date: " + dateFormat.format(date) + " EST \nNumber of items: "
						+ currentItemNumber + "\nItem#/ID/Title/Price/Qty/Disc %/Subtotal: \n\n" + sb.toString()
						+ "\n\n" + "Order subtotal: " + subtotal + "\n\nTax rate: " + taxRate * 100
						+ "%\n\nTax amount: $" + taxAmount + "\n\nOrder total: $" + orderTotal
						+ "\n\nThank you for buying a book! Use the New Order button to start over.";

				JOptionPane.showMessageDialog(null, outputMessage);

				// Disable all buttons besides New Order and Exit.
				processItem.setEnabled(false);
				confirmItem.setEnabled(false);
				viewOrder.setEnabled(false);
				finishOrder.setEnabled(false);

				// Turn off background colors.
				processItem.setBackground(null);
				confirmItem.setBackground(null);

				// Disable all text fields.
				bookIdField.setEnabled(false);
				quanityField.setEnabled(false);

				// Overwrite the top box with confirmation message. Hall all
				// unneeded boxes.
				numOfItemsField.setVisible(false);
				quanityLabel.setVisible(false);
				quanityField.setVisible(false);
				bookIdLabel.setVisible(false);
				bookIdField.setVisible(false);

				numOfItemsLabel.setText("The order is complete. The record is stored in the transactions.txt file.");
				subtotalLabel.setText("Order total cost: ");
				subtotalField.setText(Double.toString(orderTotal));
				itemLabel.setText("Unique items in this order.");
				itemField.setText(Integer.toString(currentItemNumber));

				// Store to file on loop.
				for (int i = 0; i < checkoutInfoWriteList.size(); i++) {

					// Put the information in file output format.
					String outputFileString = System.currentTimeMillis() + ", " + checkoutInfoWriteList.get(i) + ", "
							+ dateFormat.format(date) + " "
							+ TimeZone.getDefault().getDisplayName(false, TimeZone.SHORT) + "\n";

					// Write to transaction.txt.
					try {
						File file = new File("src/transaction.txt");

						// if file doesnt exists, then create it
						if (!file.exists()) {
							file.createNewFile();
						}

						// Write to the output file. True param in fileWritter
						// appends instead of replacing.
						FileWriter fileWritter = new FileWriter(file.getPath(), true);
						BufferedWriter bufferWritter = new BufferedWriter(fileWritter);
						bufferWritter.write(outputFileString);
						bufferWritter.close();
					} catch (IOException e1) {
						e1.printStackTrace();
					}
				}

				checkoutInfoGUIList.clear();
				checkoutInfoWriteList.clear();
			}
		});

		// Button to set all values back to default.
		newOrder = new JButton("New Order");
		newOrder.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				// Set initialize values.
				itemNumber = 1;
				currentItemNumber = 0;
				subtotal = 0;
				currentGUIItemInfo = "";
				processItem.setEnabled(true);
				confirmItem.setEnabled(false);
				viewOrder.setEnabled(false);
				finishOrder.setEnabled(false);
				processItem.setText("Process Item #" + itemNumber);
				confirmItem.setText("Confirm Item #" + itemNumber);
				processItem.setBackground(Color.YELLOW);
				confirmItem.setBackground(null);

				// Turn fields back on for fields.
				bookIdField.setEnabled(true);
				quanityField.setEnabled(true);
				numOfItemsField.setEnabled(true);

				// Set visibility of all fields and labels.
				numOfItemsLabel.setVisible(true);
				numOfItemsField.setVisible(true);
				bookIdLabel.setVisible(true);
				bookIdField.setVisible(true);
				quanityLabel.setVisible(true);
				quanityField.setVisible(true);

				// Set text for all fields and labels.
				numOfItemsLabel.setText("Enter number of items in this order:");
				numOfItemsField.setText("");
				bookIdLabel.setText("Enter book ID for Item #" + itemNumber + ":");
				bookIdField.setText("");
				quanityLabel.setText("Enter quanity for Item #" + itemNumber + ":");
				quanityField.setText("");
				itemField.setText("");
				itemLabel.setText("Item #" + itemNumber + " info:");
				subtotalField.setText("");
				subtotalLabel.setText("Order subtotal for " + currentItemNumber + " item(s):");

				checkoutInfoGUIList.clear();
				checkoutInfoWriteList.clear();
			}
		});

		// Close the application.
		exit = new JButton("Exit");
		exit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				System.exit(0);
			}
		});

		// Add items to JPanel
		add(numOfItemsLabel);
		add(numOfItemsField);
		add(bookIdLabel);
		add(bookIdField);
		add(quanityLabel);
		add(quanityField);
		add(itemLabel);
		add(itemField);
		add(subtotalLabel);
		add(subtotalField);
		add(processItem);
		add(confirmItem);
		add(viewOrder);
		add(finishOrder);
		add(newOrder);
		add(exit);

	}

	// Opens the input file and loads it into three separate linked lists. The
	// linked lists have corresponding information identical indexes.
	public static void load() throws URISyntaxException, IOException {

		// Load data from file.
		URL openKeyFile = BookStore.class.getResource("inventory.txt");
		Scanner sc = new Scanner(new File(openKeyFile.toURI()));

		// Init variables.
		StringBuffer sb = new StringBuffer();

		while (sc.hasNextLine()) {
			sb.append(sc.nextLine());
			// String string = sb.toString();
			String[] explode = sb.toString().split(",");
			bookIdList.add(Integer.parseInt(explode[0]));
			bookInfoList.add(explode[1]);
			bookPriceList.add(Double.parseDouble(explode[2]));
			sb.setLength(0);
		}

		sc.close();
	}

	// Regex to determine if a number is numerical.
	public static boolean isNumeric(String str) {
		return str.matches("-?\\d+(\\.\\d+)?");
	}

	// Returns the string representation of the current item order in front end form.
	public String getGUIText(int numOfItemsField, int bookIdField, int quanityField) {

		if (bookIdList.contains(bookIdField)) {

			int location = bookIdList.indexOf(bookIdField);

			double discount = getDiscount(quanityField);

			double orderCost = getTotalCost(quanityField, bookPriceList.get(location), discount);

			return (bookIdField + " " + bookInfoList.get(location) + " $" + bookPriceList.get(location) + " "
					+ quanityField + ", " + 100 * discount + "% $" + orderCost);

			// Save net to ensure a string will always return from this
			// function.
		} else {
			return "";
		}
	}

	// Updates the running sub-total based on a order's cost.
	public void updateSubtotal(int numOfItemsField, int bookIdField, int quanityField) {
		if (bookIdList.contains(bookIdField)) {

			int location = bookIdList.indexOf(bookIdField);

			double discount = getDiscount(quanityField);

			double orderCost = getTotalCost(quanityField, bookPriceList.get(location), discount);

			// Save a running total of the purchase orders. Must update subtotal
			// here.
			subtotal += orderCost;
		}
	}

	// Returns the string representation of the current item order in back-end form.
	public String getWriteText(int numOfItemsField, int bookIdField, int quanityField) {

		if (bookIdList.contains(bookIdField)) {

			int location = bookIdList.indexOf(bookIdField);

			double discount = getDiscount(quanityField);

			double orderCost = getTotalCost(quanityField, bookPriceList.get(location), discount);

			return (bookIdField + ", " + bookInfoList.get(location) + ", " + bookPriceList.get(location) + ", "
					+ quanityField + ", " + discount + ", " + orderCost);

			// Save net to ensure a string will always return from this
			// function.
		} else {
			return "";
		}
	}

	// Get discount based on qunaity supplied.
	public double getDiscount(int quanity) {

		if (quanity < 1) {
			return 0;
		} else if (quanity >= 1 && quanity <= 4) {
			return 0;
		} else if (quanity >= 5 && quanity <= 9) {
			return .1;
		} else if (quanity >= 10 && quanity <= 14) {
			return .15;
		} else {
			return .2;
		}
	}

	// Get the total cost and round to 2 decimal places.
	public double getTotalCost(int quanity, double price, double discount) {
		double discountDifference = 1 - discount;

		// Round the double to 2 decimal places.
		double round = 100 * (quanity * price * discountDifference);
		round = (int) round;

		return round / 100;
	}

	/**
	 * Create the GUI and show it. For thread safety, this method should be
	 * invoked from the event-dispatching thread.
	 */
	private static void createAndShowGUI() {

		try {
			load();
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// Create and set up the window.
		JFrame frame = new JFrame("Book Store");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		// Create and set up the content pane.
		BookStore newContentPane = new BookStore();
		newContentPane.setOpaque(true); // content panes must be opaque
		newContentPane.setBackground(Color.LIGHT_GRAY);
		newContentPane.setLayout(new GridLayout(0, 2));
		frame.setContentPane(newContentPane);

		// Display the window.
		frame.setSize(1000, 500);
		frame.setVisible(true);
	}

	public static void main(String[] args) {
		// Schedule a job for the event-dispatching thread:
		// creating and showing this application's GUI.
		javax.swing.SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				createAndShowGUI();
			}
		});
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub

	}
}