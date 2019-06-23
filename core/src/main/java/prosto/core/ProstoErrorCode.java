package prosto.core;

public enum ProstoErrorCode {
    NONE(0),

    NAME_RESOLUTION_ERROR(10),

    ELEMENT_CREATION_ERROR(20),
    ELEMENT_UPDATE_ERROR(30),
    ELEMENT_DELETE_ERROR(40),

    DEFINITION_ERROR(50),
    EVALUATION_ERROR(70),

    SERVER_ERROR(100),
    CONNECTOR_ERROR(110),
    ACTION_ERROR(120),

    GENERAL(1000),
    ;

    private int value;

    public int getValue() {
        return value;
    }

    private ProstoErrorCode(int value) {
        this.value = value;
    }
}
