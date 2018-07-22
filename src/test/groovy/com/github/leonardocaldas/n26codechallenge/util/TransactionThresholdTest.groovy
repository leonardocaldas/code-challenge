package com.github.leonardocaldas.n26codechallenge.util

import spock.lang.Specification

class TransactionThresholdTest extends Specification {

    def "should get threshold in millis"() {
        given:
        def currentMillis = 100000
        def timeRange = 20L

        when:
        def threshold = TransactionThreshold.getThresholdInMillis(currentMillis, timeRange)

        then:
        threshold == 80000
    }

    def "should get threshold in seconds"() {
        given:
        def currentMillis = 100000
        def timeRange = 20L

        when:
        def threshold = TransactionThreshold.getThresholdInSeconds(currentMillis, timeRange)

        then:
        threshold == 80
    }
}
