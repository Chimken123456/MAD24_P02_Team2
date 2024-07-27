package sg.edu.np.mad.beproductive.ExpensesTracker;

public class ExpensesModel {
    private String category;
    private String dateTime;
    private String price;
    private int categoryIcon;

    // Default constructor required for calls to DataSnapshot.getValue(ExpensesModel.class)
    public ExpensesModel() {
    }

    public ExpensesModel(String category, String dateTime, String price, int categoryIcon) {
        this.category = category;
        this.dateTime = dateTime;
        this.price = price;
        this.categoryIcon = categoryIcon;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getDateTime() {
        return dateTime;
    }

    public void setDateTime(String dateTime) {
        this.dateTime = dateTime;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public int getCategoryIcon() {
        return categoryIcon;
    }

    public void setCategoryIcon(int categoryIcon) {
        this.categoryIcon = categoryIcon;
    }
}

