package com.example.demo.payload.login.response;

public class MessageResponse {
  private String message;

  private int flag;

  public MessageResponse(String message) {
    this.message = message;
  }

  public String getMessage() {
    return message;
  }

  public void setMessage(String message) {
    this.message = message;
  }
}
