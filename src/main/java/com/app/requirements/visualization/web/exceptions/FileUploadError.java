package com.app.requirements.visualization.web.exceptions;

public class FileUploadError extends RuntimeException {
     private String message;

     public String getMessage() {
       return message;
    }

    public void setMessage(String message) {
      this.message = message;
    }
}