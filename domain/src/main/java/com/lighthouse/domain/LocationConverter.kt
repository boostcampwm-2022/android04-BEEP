package com.lighthouse.domain

import kotlin.math.acos
import kotlin.math.cos
import kotlin.math.floor
import kotlin.math.sin

/**
 * @property degree 도
 * @property minutes 분
 * @property seconds 초
 */
data class Dms(
    val degree: Int,
    val minutes: Int,
    val seconds: Int
) {
    fun dmsToString() = "${degree}${fillZero(minutes)}${fillZero(seconds)}"

    private fun fillZero(seconds: Int) = seconds.toString().padStart(2, '0')
}

/**
 * @property longitude : 경도
 * @property latitude  : 위도
 */
data class VertexLocation(
    val longitude: Double,
    val latitude: Double
)

data class NextLocation(
    val x: Int,
    val y: Int
)

data class DmsLocation(
    val x: Dms,
    val y: Dms
)

object LocationConverter {

    private const val gap = 10
    private const val searchGap = gap * 2

    private val directions = listOf(
        NextLocation(0, 0),
        NextLocation(-gap, 0),
        NextLocation(-gap, gap),
        NextLocation(0, gap),
        NextLocation(gap, gap),
        NextLocation(gap, 0),
        NextLocation(gap, -gap),
        NextLocation(0, -gap),
        NextLocation(-gap, -gap)
    )

    private val searchDirections = directions + listOf(
        NextLocation(-searchGap, -searchGap),
        NextLocation(-searchGap, -gap),
        NextLocation(-searchGap, 0),
        NextLocation(-searchGap, gap),
        NextLocation(-searchGap, searchGap),
        NextLocation(-gap, searchGap),
        NextLocation(0, searchGap),
        NextLocation(gap, searchGap),
        NextLocation(searchGap, searchGap),
        NextLocation(searchGap, gap),
        NextLocation(searchGap, 0),
        NextLocation(searchGap, -gap),
        NextLocation(searchGap, -searchGap),
        NextLocation(gap, -searchGap),
        NextLocation(0, -searchGap),
        NextLocation(-gap, -searchGap)
    )

    fun getCardinalDirections(x: Double, y: Double): List<DmsLocation> {
        val xDms = toMinDms(x)
        val yDms = toMinDms(y)

        return directions.map { nextLocation ->
            val nextDmsX = calculateTime(nextLocation.x, xDms)
            val nextDmsY = calculateTime(nextLocation.y, yDms)
            DmsLocation(nextDmsX, nextDmsY)
        }
    }

    fun getSearchCardinalDirections(x: Dms, y: Dms): List<DmsLocation> {
        return searchDirections.map { nextLocation ->
            val nextDmsX = calculateTime(nextLocation.x, x)
            val nextDmsY = calculateTime(nextLocation.y, y)
            DmsLocation(nextDmsX, nextDmsY)
        }
    }

    private fun calculateTime(
        nextSecond: Int,
        dms: Dms
    ): Dms {
        val degreeToSeconds = dms.degree * 3600
        val minutesToSeconds = dms.minutes * 60
        val seconds = dms.seconds

        val sum = degreeToSeconds + minutesToSeconds + seconds + nextSecond

        val resultDegree: Int = sum / 3600
        val resultMinutes: Int = sum / 60 - (resultDegree * 60)
        val resultSeconds: Int = sum % 60

        return Dms(resultDegree, resultMinutes, resultSeconds)
    }

    /**
     * 도분초(DMS) : 저희가 ROOM에 저장할 좌표라고 생각하시면 편할거 같습니다 :)
     * 십진수도(DD): Location 에서 갖고 왔을때 정보가 DD입니다!
     * 십진수를 도분초로 바꾸는 함수입니다.
     * @param coordinate -> x,y 좌표
     */
    fun toMinDms(coordinate: Double): Dms {
        val dms = setDms(coordinate)

        val depressionSeconds = getDepression(dms.seconds)

        return dms.copy(
            degree = dms.degree,
            minutes = dms.minutes,
            seconds = depressionSeconds
        )
    }

    private fun setDms(coordinateToDouble: Double): Dms {
        val degree = getDegree(coordinateToDouble)
        val minutes = getMinutes(coordinateToDouble, degree)
        val seconds = getSeconds(coordinateToDouble, degree, minutes)
        return Dms(degree, minutes, seconds)
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
        getMinutes((coordinateToDouble - degree) * 60, minutes)

    private fun getDepression(value: Int) = value - value % 10

    /**
     * x, y 좌표를 기준으로 각 꼭짓점을 찾아주는 함수입니다.
     * @param x section 의 최소 x 값
     * @param y section 의 최소 x 값
     * @return (좌측 X 좌표, 좌측 Y 좌표, 우측 X 좌표, 우측 Y 좌표 형식)
     */
    fun getVertex(x: Dms, y: Dms): String {
        val (maxX, maxY) = calculateVertex(x, y, searchGap + gap)
        val (minX, minY) = calculateVertex(x, y, -searchGap)
        return "$minX,$minY,$maxX,$maxY"
    }

    /**
     * section 에서 좌측 하단의 좌표를 이용해서 우측 상단의 좌표를 구하는 코드입니다.
     * @param x section x
     * @param y section y
     * @return DMS(도분초) 에서 DD(십진수)로 변환해서 반환을 합니다.
     */
    private fun calculateVertex(x: Dms, y: Dms, value: Int): VertexLocation {
        val calculateX = calculateTime(value, x)
        val calculateY = calculateTime(value, y)

        return VertexLocation(convertToDD(calculateX), convertToDD(calculateY))
    }

    /**
     * DMS(도분초, 60진수) to DD(10진수) 변환
     * @param dms : 도.분.초
     * @return 도분초를 각각 계산해서 하나의 좌표로 만들어줍니다.
     */
    fun convertToDD(dms: Dms) =
        dms.degree.toDouble() + (dms.minutes.toDouble() / 60.0) + (dms.seconds.toDouble() / 3600.0)

    /** 참고 : https://www.geodatasource.com/developers/java
     * 두 좌표 사이의 거리를 구하는 함수
     * @param lat1 기준 x
     * @param lon1 기준 y
     * @param lat2 비교 x
     * @param lon2 비교 y
     * @return 미터 단위로 반환
     */
    fun locationDistance(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
        val theta = lon1 - lon2
        var dist =
            sin(decimalToRadian(lat1)) * sin(decimalToRadian(lat2)) + cos(decimalToRadian(lat1)) * cos(
                decimalToRadian(
                    lat2
                )
            ) * cos(decimalToRadian(theta))
        dist = acos(dist)
        dist = radianToDecimal(dist)
        dist *= 60 * 1.1515 * 1609.344
        return dist // 단위 meter
    }

    /**
     * 10진수를 radian(라디안)으로 변환
     * @param deg 10진수
     * @return radian 반환
     */
    private fun decimalToRadian(deg: Double): Double {
        return deg * Math.PI / 180.0
    }

    /**
     * radian(라디안)을 10진수로 변환
     * @param radian
     * @return
     */
    private fun radianToDecimal(radian: Double): Double {
        return radian * 180 / Math.PI
    }
}
