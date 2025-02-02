package bistro.core;

/**
 * All operations implemented by the system.
 * For each operation type, there is one class which implements it.
 */
public enum OperationType {

    //
    // Column operations
    //

    // Attribute column
    // Its values will be set by the table population procedure
    ATTRIBUTE(0),

    // Calculate column
    CALCULATE(10),

    // Link column
    // Find an element in the type table and store it as output
    LINK(20),

    // Project column
    // Append an element to the output table (if does not exist) and store it as an output
    PROJECT(30),

    // Accumulate column
    // Update the output value for each element of the group
    ACCUMULATE(40),

    // Rolling column
    // Update the output value for each element of  the window taking into account the distance
    ROLL(50),

    //
    // Table operations
    //

    PRODUCT(100), // Product table

    RANGE(110), // Range table
    ;

    private int value;

    public int getValue() {
        return value;
    }

    public static OperationType fromInt(int value) {
        for (OperationType kind : OperationType.values()) {
            if (kind.getValue() == value) {
                return kind;
            }
        }
        return OperationType.ATTRIBUTE;
    }

    private OperationType(int value) {
        this.value = value;
    }
}
