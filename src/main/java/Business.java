import com.google.gson.annotations.SerializedName;

public class Business {
    @SerializedName("business_id")
    private String business_id;

    @SerializedName("name")
    private String name;

    @SerializedName("neighborhood")
    private String neighborhood;

    @SerializedName("address")
    private String address;

    @SerializedName("city")
    private String city;

    @SerializedName("state")
    private String state;

    @SerializedName("postal_code")
    private String postal_code;

    @SerializedName("latitude")
    private double latitude;

    @SerializedName("longitude")
    private double longitude;

    @SerializedName("stars")
    private double stars;

    @SerializedName("review_count")
    private int review_count;

    @SerializedName("is_open")
    private int is_open;

    @SerializedName("categories")
    private String[] categories;

    private double similarity;

    public double getSimilarity(Business b){

        int similarCategories = 0;
        double manhattan;

        //if categories match, then find closest mahatten distance
        for (int i = 0; i<this.categories.length; i++) {
            for (int j=0; j<b.categories.length; j++) {
                if (this.categories[i].equals(b.categories[j])) {
                    similarCategories++;
                }
            }
        }

        if (similarCategories <2) {
            return 99999999;
        } else {
            manhattan = Math.sqrt((Math.pow((this.latitude- b.latitude),2)) +
                    (Math.pow((this.longitude- b.longitude),2)));
        }
        double score = manhattan / (similarCategories <<2);
        b.similarity = score;
        return score;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String[] getCategories() {
        return categories;
    }

    public String getBusiness_id() {
        return business_id;
    }

    public String getName() {
        return name;
    }

    public String getNeighborhood() {
        return neighborhood;
    }

    public String getAddress() {
        return address;
    }

    public String getCity() {
        return city;
    }

    public String getState() {
        return state;
    }

    public String getPostal_code() {
        return postal_code;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public double getStars() {
        return stars;
    }

    public int getReview_count() {
        return review_count;
    }

    public int getIs_open() {
        return is_open;
    }

    /*Business(String tempBusiness_id, String tempName, String tempNeighborhood, String tempAddress, String tempCity, String tempState, String tempPostal_code, double tempLatitude, double tempLongitude, double tempStars, int tempReview_count, int tempIs_open){
        business_id = tempBusiness_id;
        name = tempName;
        neighborhood = tempNeighborhood;
        address = tempAddress;
        city = tempCity;
        state = tempState;
        postal_code = tempPostal_code;
        latitude = tempLatitude;
        longitude = tempLongitude;
        stars = tempStars;
        review_count = tempReview_count;
        is_open = tempIs_open;
    }*/

}
