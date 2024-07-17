package sg.edu.np.mad.beproductive.ExpensesTracker;
public class ExpensesModel {
    private String category;
    private String dateTime;
    private String price;
    private int categoryIcon;

    public ExpensesModel(String category, String dateTime, String price, int categoryIcon) {
        this.category = category;
        this.dateTime = dateTime;
        this.price = price;
        this.categoryIcon = categoryIcon;
    }

    public String getCategory() { return category; }
    public String getDateTime() { return dateTime; }
    public String getPrice() { return price; }
    public int getCategoryIcon() { return categoryIcon; }
}
