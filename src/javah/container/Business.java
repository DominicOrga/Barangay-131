package javah.container;

/**
 * A class that serves as a business data holder.
 */
public class Business {
    /* Holds the unique ID of this business. */
    private String mID;

    /* Holds the name of this business. */
    private String mName;

    private String mType;

    /* Holds the address of this business. */
    private String mAddress;

    /**
     * An array that holds the name of the owners of this business. Max number of
     * owners is five.
     * The array contains the following elements:
     * 0 <= n <= 4
     * array[n][0] = first name of owner n.
     * array[n][1] = middle name of owner n.
     * array[n][3] = last name of owner n.
     * array[n][4] = auxiliary of owner n.
     */
    private String[][] mOwners = new String[5][4];

    /**
     * Get the ID of this business.
     *
     * @return the assigned ID of this business.
     */
    public String getID() {
        return mID;
    }

    /**
     * Set the ID of this business.
     *
     * @param id
     *        The ID assigned to this business.
     */
    public void setID(String id) {
        mID = id;
    }

    /**
     * Get the type of this business.
     *
     * @return the type of this business.
     */
    public String getType() {
        return mType;
    }

    /**
     * Set the type of this business.
     *
     * @param type
     *        The type of this business.
     */
    public void setType(String type) {
        mType = type;
    }

    /**
     * Get the name of this business.
     *
     * @return the name of this business.
     */
    public String getName() {
        return mName;
    }

    /**
     * Set the name of this business.
     *
     * @param name
     *        The string name to be assigned to this business.
     */
    public void setName(String name) {
        mName = name;
    }

    /**
     * Get the address of this business.
     *
     * @return the assigned address of this business.
     */
    public String getAddress() {
        return mAddress;
    }

    /**
     * Set the address of this business.
     *
     * @param address
     *        The string to be assigned as an address to this business.
     */
    public void setAddress(String address) {
        mAddress = address;
    }

    /**
     * Get the names of the owners of this business.
     *
     * @return the array containing the owners of this business.
     *         The array contains the following elements:
     *         0 <= n <= 4
     *         array[n][0] = first name of owner n.
     *         array[n][1] = middle name of owner n.
     *         array[n][3] = last name of owner n.
     *         array[n][4] = auxiliary of owner n.
     */
    public String[][] getOwners() {
        return mOwners;
    }

    /**
     * Set the names of the owners of this business.
     *
     * @param owners
     *        The array containing the names of this business.
     *        The array contains the following elements:
     *        0 <= n <= 4
     *        array[n][0] = first name of owner n.
     *        array[n][1] = middle name of owner n.
     *        array[n][3] = last name of owner n.
     *        array[n][4] = auxiliary of owner n.
     */
    public void setOwners(String[][] owners) {
        mOwners = owners;
    }
}

