package com.zlm.phototable;

public class Event {
    public static class CompleteVideo{
        private boolean isComplete;
        public CompleteVideo(boolean isComplete) {
            this.isComplete = isComplete;
        }
        public boolean isComplete() {
            return isComplete;
        }
    }
}
