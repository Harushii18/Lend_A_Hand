package com.example.lendahand;

public class CourierInfo {

        private String strCourierName;
        private String strNumDeliveries;

    public CourierInfo(String strCourierName, String strNumDeliveries) {
        this.strCourierName = strCourierName;
        this.strNumDeliveries = strNumDeliveries;
    }
        public String getStrCourierName() {
            return strCourierName;
        }
        public void setStrCourierName(String strCourierName) {
            this.strCourierName = strCourierName;
        }
        public String getStrNumDeliveries() {
            return strNumDeliveries;
        }
        public void setStrNumDeliveries(String strNumDeliveries) {
            this.strNumDeliveries = strNumDeliveries;
        }

}
