package com.allfire.qqcolormanager.config;

public class RelationConfig {
    
    public enum RelationMode {
        TARGET_AND_SENDER,
        SENDER_AND_TARGET
    }
    
    private RelationMode mode;
    
    public RelationConfig() {
        this.mode = RelationMode.TARGET_AND_SENDER;
    }
    
    public void setMode(String mode) {
        try {
            this.mode = RelationMode.valueOf(mode.toUpperCase());
        } catch (IllegalArgumentException e) {
            this.mode = RelationMode.TARGET_AND_SENDER;
        }
    }
    
    public RelationMode getMode() {
        return mode;
    }
    
    public boolean isTargetAndSender() {
        return mode == RelationMode.TARGET_AND_SENDER;
    }
    
    public boolean isSenderAndTarget() {
        return mode == RelationMode.SENDER_AND_TARGET;
    }
}
