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

    public void write(Socket socket) throws Exception {
        
        try(OutputStream os = socket.getOutputStream()) {
            OutputStreamWriter osw = new OutputStreamWriter(os);
            BufferedWriter bw = new BufferedWriter(osw);

            bw.write("\n");
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