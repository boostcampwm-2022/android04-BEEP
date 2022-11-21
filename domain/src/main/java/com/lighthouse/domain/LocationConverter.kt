package com.lighthouse.domain

import kotlin.math.floor

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
    fun dmsToString() =
        StringBuilder().append(degree).append(fillZero(minutes)).append(fillZero(seconds)).toString()

    private fun fillZero(seconds: Int) = seconds.toString().padStart(2, '0')
}

data class VertexLocation(
    val max: Double,
    val min: Double
)

data class NextLocation(
    val x: Int,
    val y: Int
)

data class Location(
    val x: Dms,
    val y: Dms
)

object LocationConverter {

    private val directions = listOf(
        NextLocation(-10, 0),
        NextLocation(-10, 10),
        NextLocation(0, 10),
        NextLocation(10, 10),
        NextLocation(10, 0),
        NextLocation(10, -10),
        NextLocation(0, -10),
        NextLocation(-10, -10)
    )

    fun getCardinalDirections(x: Double, y: Double): List<Location> {
        val xDms = toDMS(x)
        val yDms = toDMS(y)

        return directions.map { nextLocation ->
            val (nextX, nextY) = nextLocation

            // 다음 초 계산 결과가 0~60 사이면 시/분 쪽은 계산할 필요가 없다.
            val nextDmsX = calculateTime(nextX, xDms)
            val nextDmsY = calculateTime(nextY, yDms)
            Location(nextDmsX, nextDmsY)
        }.plusElement(Location(xDms, yDms))
    }

    private fun calculateTime(
        nextSecond: Int,
        dms: Dms
    ): Dms {
        val dmsNextSecond = dms.seconds + nextSecond
        return when {
            dmsNextSecond in 0..60 -> {
                dms.copy(
                    degree = dms.degree,
                    minutes = dms.minutes,
                    seconds = dmsNextSecond
                )
            }
            dmsNextSecond < 0 -> overMinSeconds(dms)
            else -> overMaxSeconds(dms)
        }
    }

    // 초 계산 결과가 60을 초과하는 경우이다. 즉 70이라는 의미
    private fun overMinSeconds(dms: Dms): Dms {
        val prevMinutesX = dms.minutes - 1
        // 단 분도 -1한 결과가 -인 경우 도에서 -1 하고 분을 59로 한다.
        return if (prevMinutesX < 0) {
            dms.copy(
                degree = dms.degree - 1,
                minutes = 59,
                seconds = 50
            )
        } else {
            dms.copy(
                degree = dms.degree,
                minutes = dms.minutes - 1,
                seconds = 50
            )
        }
    }

    // 초 계산 결과가 0미만 즉 -인 경우 분을 -1하고 초를 50으로 고정하면 된다.
    private fun overMaxSeconds(dms: Dms): Dms {
        val nextMinutesX = dms.minutes + 1
        // 단 분도 +1한 결과가 60분을 넘어가면 도를 +1 하고 분을 1로 고정한다.
        return if (nextMinutesX > 60) {
            dms.copy(
                degree = dms.degree + 1,
                minutes = 1,
                seconds = 10
            )
        } else {
            dms.copy(
                degree = dms.degree,
                minutes = dms.minutes + 1,
                seconds = 10
            )
        }
    }

    /**
     * 도분초(DMS) : 저희가 ROOM에 저장할 좌표라고 생각하시면 편할거 같습니다 :)
     * 십진수도(DD): Location 에서 갖고 왔을때 정보가 DD입니다!
     * 십진수를 도분초로 바꾸는 함수입니다.
     * @param coordinate -> x,y 좌표
     */
    private fun toDMS(coordinate: Double): Dms {
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
        val (maxX, maxY) = calculateMaxVertex(x, y)
        val (minX, minY) = VertexLocation(convertToDD(x), convertToDD(y))
        return "$minX,$minY,$maxX,$maxY"
    }

    /**
     * section 에서 좌측 하단의 좌표를 이용해서 우측 상단의 좌표를 구하는 코드입니다.
     * @param minX section 좌측 하단 x
     * @param minY section 좌측 하단 y
     * @return DMS(도분초) 에서 DD(십진수)로 변환해서 반환을 합니다.
     */
    private fun calculateMaxVertex(minX: Dms, minY: Dms): VertexLocation {
        val maxX = calculateTime(10, minX)
        val maxY = calculateTime(10, minY)

        return VertexLocation(convertToDD(maxX), convertToDD(maxY))
    }

    /**
     * DMS(도분초, 60진수) to DD(10진수) 변환
     * @param dms : 도.분.초
     * @return 도분초를 각각 계산해서 하나의 좌표로 만들어줍니다.
     */
    private fun convertToDD(dms: Dms) =
        dms.degree.toDouble() + (dms.minutes.toDouble() / 60.0) + (dms.seconds.toDouble() / 3600.0)
}
