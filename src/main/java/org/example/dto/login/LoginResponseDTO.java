package org.example.dto.login;

public class LoginResponseDTO{
    private boolean success;
    private String message;
    private int userId;

    public LoginResponseDTO(boolean success, String message, int userId){
        this.success = success;
        this.message = message;
        this.userId = userId;
    }

    public boolean isSuccess() { return success; }
    public void setSuccess(boolean success) { this.success = success; }
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
    public int getUserId() {return userId;}
    public void setUserID(int userId) { this.userId = userId;}

}