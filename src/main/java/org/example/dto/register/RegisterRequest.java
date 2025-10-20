package org.example.dto.register;

public class RegisterRequest {
    private String type; 
    private RegisterRequestPayload payload;

    /**
     * This method is the constructor, with this, we 
     * can initialize the type of request to json and 
     * load the payload Data.
     * 
     * @param type  String value
     * @param payload Object with information.
     */
    public RegisterRequest(String type, RegisterRequestPayload payload) {
        this.type = type;
        this.payload = payload;
    }

    /**
     * this method return a String with
     * type of request.
     * 
     * @return String that represent the request type.
     */
    public String getType() {
        return type;
    }

    /**
     * this method return the payload object.
     * 
     * @return object with information.
     */
    public RegisterRequestPayload getPayload() {
        return payload;
    }
}
