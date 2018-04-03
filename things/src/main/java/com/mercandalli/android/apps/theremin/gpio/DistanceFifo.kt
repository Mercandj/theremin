package com.mercandalli.android.apps.theremin.gpio

class DistanceFifo(private val size: Int) {

    private val array = ArrayList<Double>(2 * size)
    private val innerArraySizeMinusOne = 2 * size - 1

    val distance: Int
        get() = computeDistance()


    fun add(element: Double) {
        synchronized(array) {
            if (array.size < innerArraySizeMinusOne) {
                array.add(element)
            } else {
                val subList = ArrayList(array.subList(size, innerArraySizeMinusOne))
                array.clear()
                array.addAll(subList)
            }
        }
    }

    private fun computeDistance(): Int {
        synchronized(array) {
            val currentSize = array.size
            if (currentSize <= 0) {
                return -1
            }
            val subList = ArrayList<Double>(array)
            if (currentSize > 4) {
                subList.remove(subList.min())
                subList.remove(subList.max())
            }
            if (currentSize > 12) {
                subList.remove(subList.min())
                subList.remove(subList.max())
            }
            if (currentSize > 16) {
                subList.remove(subList.min())
                subList.remove(subList.max())
            }
            if (currentSize > 20) {
                subList.remove(subList.min())
            }
            return subList.average().toInt()
        }
    }

}
