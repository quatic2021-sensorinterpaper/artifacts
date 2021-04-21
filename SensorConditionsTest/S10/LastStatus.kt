package com

import org.javatuples.Pair
import java.io.Serializable
import java.util.ArrayList

class LastStatus : Serializable {
    private val allStatus: ArrayList<Pair<String, Boolean>>
    fun amountStatusItens(): Int {
        return allStatus.size
    }

    fun addStatus(sensorName: String, status: Boolean) {
        allStatus.add(Pair(sensorName, status))
    }

    fun getStatus(sensorName: String): Boolean {
        var sName: String
        var status = false
        for (pair in allStatus) {
            sName = pair.value0
            if (sName == sensorName) {
                status = pair.value1
                break
            }
        }
        return status
    }

    fun setCurrentStatus(sensorName: String, status: Boolean) {
        var index = 0
        var sName: String
        for (pair in allStatus) {
            sName = pair.value0
            if (sName == sensorName) {
                break
            }
            index++
        }
        allStatus[index] = Pair(sensorName, status)
    }

    init {
        allStatus = ArrayList()
    }
}
