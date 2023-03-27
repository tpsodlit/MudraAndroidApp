package com.collection.tpwodloffline.utils

class Distance {

    companion object {
        @JvmStatic
        fun Distancekm(lat01: String, lon01: String, lat02: String, lon02: String): String {

            val lat1: Double = lat01.toDouble()
            val lon1: Double = lon01.toDouble()
            val lat2: Double = lat02.toDouble()
            val lon2: Double = lon02.toDouble()

            val theta = lon1 - lon2
            var dist = (Math.sin(deg2rad(lat1))
                    * Math.sin(deg2rad(lat2))
                    + (Math.cos(deg2rad(lat1))
                    * Math.cos(deg2rad(lat2))
                    * Math.cos(deg2rad(theta))))
            dist = Math.acos(dist)
            dist = rad2deg(dist)
            dist = dist * 60 * 1.1515
            dist = dist * 1.609344
            return dist.toString()

        }

        private fun deg2rad(deg: Double): Double {
            return deg * Math.PI / 180.0
        }

        private fun rad2deg(rad: Double): Double {
            return rad * 180.0 / Math.PI
        }


    }
}