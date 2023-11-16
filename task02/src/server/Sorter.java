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

    public void sort(Socket socket) throws Exception {

        try (InputStream is = socket.getInputStream()) {
            InputStreamReader isr = new InputStreamReader(is);
            BufferedReader br = new BufferedReader(isr);

            OutputStream os = socket.getOutputStream();
            OutputStreamWriter osw = new OutputStreamWriter(os);
            BufferedWriter bw = new BufferedWriter(osw);

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

            products.sort(Comparator.comparing(Product::getRating).thenComparing(Product::getPrice));
            products.forEach(System.out::println);

            for (Product p : products) {
                if (getBudget() - p.getPrice() > 0) {
                    chosenProducts.add(p);
                    setBudget(getBudget() - p.getPrice());
                } else {
                    continue;
                }
            }
            
            String ids = "";
            double totalPrice = 0;

            bw.write("request_id:" + getRequestID() + "\n");
            bw.write("name: Tan Wei Sheng\n");
            bw.write("email: wstan.ws97@gmail.com\n");
            for (Product p : chosenProducts) {
                ids = ids + " " + p.getId();
            }
            bw.write("items: " + ids.trim() + "\n");
            for (Product p : chosenProducts) {
                totalPrice += p.getPrice();
            }
            bw.write("spent: " + totalPrice + "\n");
            bw.write("remaining: " + (getBudget() - totalPrice) + "\n");
            bw.write("client_end\n");

            bw.flush();

            while ((line = br.readLine()) != null) {
                System.out.println(line);
            }
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