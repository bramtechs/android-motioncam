package be.brambasiel.motioncam

object CameraMath {

    private var previous: ByteArray? = null;
    private var samples: ArrayList<Float> = arrayListOf();

    fun calculateSimilarity(data: ByteArray): Float {
        var similar = 0;
        previous?.let {
            for (i in data.indices) {
                if (data[i] == it[i]) {
                    similar++;
                }
            }
        }
        previous = data;
        return similar / data.size.toFloat();
    }

    fun calculateAverage(data: ByteArray): Float {
        val result = calculateSimilarity(data);
        if (samples.size > 30) {
            samples.removeFirst();
        }
        samples.add(result)

        // calculate average
        var total = 0.0f
        for (i in samples) {
            total += i;
        }
        val avg = total / samples.count().toFloat();
        return avg;
    }

    fun isMotion(data: ByteArray, sensible: Float): Boolean {
        return calculateAverage(data) < sensible;
    }
}