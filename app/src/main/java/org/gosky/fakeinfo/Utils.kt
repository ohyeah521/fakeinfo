package org.gosky.fakeinfo

/**
 * Created by zohar on 2017/7/16.
 * desc:
 */
fun Any.getRandomTelNumber(): String {
    val telFirst = "134,135,136,137,138,139,150,151,152,157,158,159,130,131,132,155,156,133,153"
            .split(",".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()

    val index = getNum(0, telFirst.size - 1)
    val first = telFirst[index]
    val second = (getNum(1, 888) + 10000).toString().substring(1)
    val thrid = (getNum(1, 9100) + 10000).toString().substring(1)
    return first + second + thrid
}

fun getNum(start: Int, end: Int): Int {
    return (Math.random() * (end - start + 1) + start).toInt()
}