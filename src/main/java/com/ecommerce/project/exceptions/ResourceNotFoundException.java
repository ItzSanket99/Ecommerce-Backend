package com.ecommerce.project.exceptions;

public class ResourceNotFoundException extends RuntimeException{
    String resourceName;
    String filed;
    String filedName;
    Long filedId;

    public ResourceNotFoundException(String filed, String resourceName, Long filedId) {
        super(String.format("%s not found with %s: %d",resourceName,filed,filedId));
        this.filed = filed;
        this.resourceName = resourceName;
        this.filedId = filedId;
    }

    public ResourceNotFoundException(String filedName, String resourceName, String filed) {
        super(String.format("%s not found with %s: %s",resourceName,filed,filedName));
        this.filedName = filedName;
        this.resourceName = resourceName;
        this.filed = filed;

    }
    public ResourceNotFoundException(){

    }


}
