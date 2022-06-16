package others;

public class Sales {
    int sales_id;
    int customer_id;
    int product_id;
    int qty;
    String date;
    String amount;

    public Sales() {
    }

    public Sales(int sales_id, int customer_id, int product_id, int qty, String date, String amount) {
        this.sales_id = sales_id;
        this.customer_id = customer_id;
        this.product_id = product_id;
        this.qty = qty;
        this.date = date;
        this.amount = amount;
    }

    public int getSales_id() {
        return sales_id;
    }

    public void setSales_id(int sales_id) {
        this.sales_id = sales_id;
    }

    public int getCustomer_id() {
        return customer_id;
    }

    public void setCustomer_id(int customer_id) {
        this.customer_id = customer_id;
    }

    public int getProduct_id() {
        return product_id;
    }

    public void setProduct_id(int product_id) {
        this.product_id = product_id;
    }

    public int getQty() {
        return qty;
    }

    public void setQty(int qty) {
        this.qty = qty;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }
}
