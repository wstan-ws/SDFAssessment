package server;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class Sorter {

    private double budget;
    private String requestID;
    private List<Product> products = new ArrayList<>();
    private List<Product> chosenProducts = new ArrayList<>();

    public void readResult(Socket socket) throws Exception {
        try (InputStream is = socket.getInputStream()) {
            InputStreamReader isr = new InputStreamReader(is);
            BufferedReader br = new BufferedReader(isr);

            String line;

            while ((line = br.readLine()) != null) {
                System.out.println(line);
            }
        }   
    }

    public void read(Socket socket) throws Exception {

        try (InputStream is = socket.getInputStream()) {
            InputStreamReader isr = new InputStreamReader(is);
            BufferedReader br = new BufferedReader(isr);

            String line;
            Product product = null;
            boolean stop = false;

            while (!stop) {
                line = br.readLine();
                System.out.println(line);
                String[] terms = line.trim().toLowerCase().split(":");
                switch (terms[0]) {
                    case "request_id":
                        setRequestID(terms[1].trim());
                        break;
                    case "budget":
                        setBudget(Double.parseDouble(terms[1].trim()));
                        break;
                    case "prod_id":
                        product = new Product();
                        product.setId(terms[1].trim());
                        saveProduct(product);
                        break;
                    case "title":
                        product.setName(terms[1].trim());
                        break;
                    case "price":
                        product.setPrice(Double.parseDouble(terms[1].trim()));
                        break;
                    case "rating":
                        product.setRating(Double.parseDouble(terms[1].trim()));
                        break;
                    case "item_count":
                        continue;
                    case "prod_list":
                        continue;
                    case "prod_start":
                        continue;
                    case "prod_end":
                        continue;
                    default:
                        stop = true;
                }
            }
        }
    }

    public void sort() {
        products.sort(Comparator.comparing(Product::getRating).thenComparing(Product::getPrice));
        products.forEach(System.out::println);
    }

    public void pick() {
        for (Product product : products) {
            if (getBudget() - product.getPrice() > 0) {
                chosenProducts.add(product);
                setBudget(getBudget() - product.getPrice());
            } else {
                continue;
            }
        }
    }

    public void write(Socket socket) throws Exception {
        
        try(OutputStream os = socket.getOutputStream()) {
            OutputStreamWriter osw = new OutputStreamWriter(os);
            BufferedWriter bw = new BufferedWriter(osw);
            String ids = "";
            double totalPrice = 0;

            bw.write("request_id:" + getRequestID() + "\n");
            bw.write("name: Tan Wei Sheng\n");
            bw.write("email: wstan.ws97@gmail.com\n");
            for (Product product : chosenProducts) {
                ids = ids + " " + product.getId();
            }
            bw.write("items: " + ids.trim() + "\n");
            for (Product product : chosenProducts) {
                totalPrice += product.getPrice();
            }
            bw.write("spent: " + totalPrice + "\n");
            bw.write("remaining: " + (getBudget() - totalPrice) + "\n");
            bw.write("client_end\n");

            bw.flush();
        }
    }

    private void setBudget(double budget) {
        this.budget = budget;
    }

    public double getBudget() {
        return budget;
    }

    private void setRequestID(String requestID) {
        this.requestID = requestID;
    }

    public String getRequestID() {
        return requestID;
    }

    private void saveProduct(Product product) {
        products.add(product);
    }
}