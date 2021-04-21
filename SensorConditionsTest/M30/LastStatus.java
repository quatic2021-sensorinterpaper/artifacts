package com.ichi2;


import org.javatuples.Pair;

import java.io.Serializable;
import java.util.ArrayList;

public class LastStatus implements Serializable {
    private ArrayList<Pair<String,Boolean>> allStatus;

    public LastStatus() {
        allStatus = new ArrayList<>();
    }

    public int amountStatusItens() {
        return allStatus.size();
    }

    public void addStatus(String sensorName, boolean status) {
        allStatus.add(new Pair<>(sensorName, status));
    }

    public boolean getStatus(String sensorName) {
        String sName;
        boolean status = false;

        for (Pair<String,Boolean> pair:allStatus) {
            sName = pair.getValue0();
            if (sName.equals(sensorName)) {
                status = pair.getValue1();
                break;
            }
        }

        return status;
    }

    public void setCurrentStatus(String sensorName, boolean status) {
        int index = 0;
        String sName;

        for (Pair<String,Boolean> pair:allStatus) {
            sName = pair.getValue0();
            if (sName.equals(sensorName)) {
                break;
            }
            index++;
        }
        allStatus.set(index, new Pair<>(sensorName,status));
    }

}
