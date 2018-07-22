package com.github.leonardocaldas.n26codechallenge.service

import com.github.leonardocaldas.n26codechallenge.model.TransactionAggregate
import com.github.leonardocaldas.n26codechallenge.repository.TransactionAggregateRepository
import com.github.leonardocaldas.n26codechallenge.representation.TransactionStatistics
import com.github.leonardocaldas.n26codechallenge.service.impl.StatisticServiceImpl
import spock.lang.Specification

import static java.time.Instant.now
import static java.util.Arrays.asList
import static java.util.Collections.emptyList

class StatisticsServiceTest extends Specification {

    def repositoy = Mock(TransactionAggregateRepository)
    def TIME_RANGE = 60L
    def statisticsService = new StatisticServiceImpl(repositoy, TIME_RANGE)

    def "should get empty statistics"() {
        given:
        def expected = new TransactionStatistics()

        when:
        def statistics = statisticsService.getStatistics()

        then:
        1 * repositoy.findAll() >> emptyList()
        statistics == expected
    }

    def "should get statistics of one aggregate"() {
        given:
        def expected = TransactionStatistics.builder()
                .count(2L)
                .sum(15.0)
                .min(5.0)
                .max(10.0)
                .avg(7.5)
                .build()

        def transaction = TransactionAggregate.builder()
                .count(2L)
                .sum(15.0)
                .min(5.0)
                .max(10.0)
                .timestampInSeconds(getCurrentTimestampInSeconds())
                .build()

        when:
        def statistics = statisticsService.getStatistics()

        then:
        1 * repositoy.findAll() >> asList(transaction)
        statistics == expected
    }

    def "should get statistics of many aggregates"() {
        given:
        def expected = TransactionStatistics.builder()
                .count(12L)
                .sum(165.2)
                .min(2.5)
                .max(40.0)
                .avg(13.76)
                .build()

        def transaction1 = TransactionAggregate.builder()
                .count(3L)
                .sum(23.0)
                .min(2.5)
                .max(4.5)
                .timestampInSeconds(getCurrentTimestampInSeconds())
                .build()

        def transaction2 = TransactionAggregate.builder()
                .count(4L)
                .sum(42.3)
                .min(5.0)
                .max(20.0)
                .timestampInSeconds(getCurrentTimestampInSeconds())
                .build()

        def transaction3 = TransactionAggregate.builder()
                .count(5L)
                .sum(99.9)
                .min(20.0)
                .max(40.0)
                .timestampInSeconds(getCurrentTimestampInSeconds())
                .build()

        when:
        def statistics = statisticsService.getStatistics()

        then:
        1 * repositoy.findAll() >> asList(transaction1, transaction2, transaction3)
        statistics == expected
    }

    def "should get statistics and ignore old transactions"() {
        given:
        def expected = TransactionStatistics.builder()
                .count(7L)
                .sum(65.3)
                .min(2.5)
                .max(20.0)
                .avg(9.32)
                .build()

        def transaction1 = TransactionAggregate.builder()
                .count(3L)
                .sum(23.0)
                .min(2.5)
                .max(4.5)
                .timestampInSeconds(getCurrentTimestampInSeconds())
                .build()

        def transaction2 = TransactionAggregate.builder()
                .count(4L)
                .sum(42.3)
                .min(5.0)
                .max(20.0)
                .timestampInSeconds(getCurrentTimestampInSeconds())
                .build()

        def oldTransaction = TransactionAggregate.builder()
                .count(5L)
                .sum(99.9)
                .min(20.0)
                .max(40.0)
                .timestampInSeconds(getCurrentTimestampInSeconds() - TIME_RANGE - 1)
                .build()

        when:
        def statistics = statisticsService.getStatistics()

        then:
        1 * repositoy.findAll() >> asList(transaction1, transaction2, oldTransaction)
        statistics == expected
    }

    private static Long getCurrentTimestampInSeconds() {
        return now().toEpochMilli() / 1000;
    }
}
