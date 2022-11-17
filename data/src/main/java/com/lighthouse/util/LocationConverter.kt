package com.lighthouse.util

import kotlin.math.floor

/**
 * 1. 도분초 -> 좌표 (도)
 * 36°10'09.91" - 36도 10분 9.91초를 변환해보자
 * 1) 정수자리는 그냥 도를 그대로 가져온다. 36도
 * 2) 소수자리는 다음과 같이 계산한다. (분/60)+(초/3600)
 * 계산 결과 36+((10/60)+(9.91/3600)) = 36.16941944
 *
 * 2. 좌표 (도) -> 도분초
 * 도 = INT(좌표)
 * 분 = INT((좌표 - 도) * 60)
 * 초 = ((좌표 - 도) * 60 - 분 ) * 60
 * 위 INT(x) 는 소수점 버림을 의미합니다.
 *
 * 36.16941944를 계산한 경우 계산 결과
 * 도 = 36°
 * 분 = INT( 0.16941944 * 60) = INT(10.1651664) = 10'
 * 초 = (0.16941944 * 60 - 10) * 60 = (10.1651664 - 10) * 60 = 0.1651664 * 60 = 9.909984" = 약 9.91"
 */
object LocationConverter {

    /**
     * 도분초(DMS) : 저희가 ROOM에 저장할 좌표라고 생각하시면 편할거 같습니다 :)
     * 십진수도(DD): Location 에서 갖고 왔을때 정보가 DD입니다!
     * 십진수를 도분초로 바꾸는 함수입니다.
     * @param coordinate -> x,y 좌표
     */
    fun toDMS(coordinate: String): String {
        val coordinateToDouble = coordinate.toDouble()
        val degree = getDegree(coordinateToDouble)
        val minutes = getMinutes(coordinateToDouble, degree)
        val seconds = getSeconds(coordinateToDouble, degree, minutes)
        return mergeDMS(degree, minutes, seconds)
    }

    private fun getDegree(coordinateToDouble: Double) = floor(coordinateToDouble).toInt()

    private fun getMinutes(coordinateToDouble: Double, degree: Int) = getDegree((coordinateToDouble - degree) * 60)

    /**
     * @param coordinateToDouble -> x,y 좌표 double 값
     * @param degree -> 도
     * @param minutes -> 분
     * @return 초 계산된거에서 내림 값 (32 -> 30)
     */
    private fun getSeconds(coordinateToDouble: Double, degree: Int, minutes: Int) =
        getDepression(getMinutes((coordinateToDouble - degree) * 60, minutes))

    /**
     * 올림 함수 (12 -> 20)
     * @param value
     */
    private fun getLifting(value: Int) = (value + 9) / 10 * 10

    /**
     * 내림 함수 (12 -> 10)
     * @param value
     */
    private fun getDepression(value: Int) = value - value % 10

    /**
     * @param degree 도
     * @param minutes 분
     * @param seconds 초
     * @return 12(도) 20(분) 30(초) 로 나온 값을 하나의 String으로 합쳐주는 과정입니다.
     */
    private fun mergeDMS(degree: Int, minutes: Int, seconds: Int) =
        StringBuilder().append(degree).append(fillZero(minutes)).append(fillZero(seconds)).toString()

    private fun fillZero(seconds: Int) = String.format("%02d", seconds)
}
